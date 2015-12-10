package com.baidu.uaq.imgoptdaemon.core;

import com.alibaba.fastjson.JSON;
import com.baidu.uaq.imgoptdaemon.bcs.CLoudStorage;
import com.baidu.uaq.imgoptdaemon.bean.OptImg;
import com.baidu.uaq.imgoptdaemon.bean.PicAttr;
import com.baidu.uaq.imgoptdaemon.bean.ReqTask;
import com.baidu.uaq.imgoptdaemon.config.Config;
import com.baidu.uaq.imgoptdaemon.config.Const;
import com.baidu.uaq.imgoptdaemon.db.Redis;
import com.baidu.uaq.imgoptdaemon.db.StoreBean;
import com.baidu.uaq.imgoptdaemon.http.DownloadImg;
import com.baidu.uaq.imgoptdaemon.util.FileUtil;
import com.baidu.uaq.imgoptdaemon.util.MD5;
import com.baidu.uaq.imgoptdaemon.util.Shell;
import com.baidu.uaq.imgoptdaemon.util.Util;
import com.google.gson.Gson;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by baidu on 15/12/9.
 */
public class TaskRunV2 implements Runnable {
    private static Config config = Config.getInstance();

    public void doImgOpt() {
        Redis redis = null;
        redis = new Redis(config.getRedisAddr(), config.getRedisPort());

        DecimalFormat df = new DecimalFormat("#.00");

        Map<String, String> orgStoreImgMap = new ConcurrentHashMap<String, String>();
        Map<String, String> optStoreImgMap = new ConcurrentHashMap<String, String>();

        Map<String, String> orgCloudStoreImgMap = new ConcurrentHashMap<String, String>();
        Map<String, String> optCloudStoreImgMap = new ConcurrentHashMap<String, String>();
        double totalOrgSize = 0.0;
        double totalOptSize = 0.0;
        int optimizedNum = 0;
        OptImg optImg = null;
        ReqTask reqTask = null;

        String task = null;

        try {
            // System.out.println("pop task");
            //List<String> tasks = redis.popTask();
            task = redis.popTask();
            // System.out.println("pop task end");

            if (task != null) {
                Gson gson = new Gson();
                reqTask = gson.fromJson(task, ReqTask.class);
                optImg = gson.fromJson(reqTask.getImagelist(), OptImg.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (optImg == null) {
            // System.out.println("optImg is null");
            return;
        }
        // System.out.println(optImg.getImgs());

        StoreBean storeBean = new StoreBean();

        for (String img : optImg.getImgs()) {
            // System.out.println("download img : " + img);
            String baseName = Util.getPicBaseName(img);
            String storeName = MD5.CalcMD5(img) + baseName;
            DownloadImg downloadImg = new DownloadImg(img);
            boolean flag = downloadImg.httpDownloadFile(Const.DOWNLOAD_IMG_BASE_PATH + storeName);
            if (flag) {
                orgStoreImgMap.put(img, Const.DOWNLOAD_IMG_BASE_PATH + storeName);
            } else {
                continue;
            }
        }

        for (Map.Entry<String, String> entry : orgStoreImgMap.entrySet()) {
            String img = entry.getKey();
            // System.out.println("run cmd img : " + img);
            String baseName = Util.getPicBaseName(img);
            String orgImgStorePath = entry.getValue();
            String cmd = Util.getShellCmdByPicType(img, orgImgStorePath);
            if (cmd == null) {
                continue;
            }
            try {
                Shell.runCmd(cmd);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            optStoreImgMap.put(img, Const.OPT_IMG_BASE_PATH + MD5.CalcMD5(img) + baseName);

            StoreBean.OptimizedImage optimizedImage = storeBean.new OptimizedImage();
            optimizedImage.setOrgImg(img);

            PicAttr tmpPicAttr = FileUtil.getPicAttr(Const.DOWNLOAD_IMG_BASE_PATH +
                    MD5.CalcMD5(img) + baseName);

            if (tmpPicAttr == null) {
                continue;
            }

            long orgPicSize = tmpPicAttr.getSize();
            // totalOrgSize += orgPicSize / 1024.00;

            optimizedImage.setWidth(tmpPicAttr.getWidth());
            optimizedImage.setHeight(tmpPicAttr.getHeight());
            optimizedImage.setOrgSize(tmpPicAttr.getSize());

            tmpPicAttr = FileUtil.getPicAttr(Const.OPT_IMG_BASE_PATH +
                    MD5.CalcMD5(img) + baseName);

            if (tmpPicAttr == null) {
                continue;
            }

            long optPicSize = tmpPicAttr.getSize();

            if (optPicSize >= orgPicSize) {
                continue;
            }

            totalOrgSize += orgPicSize / 1024.00;
            totalOptSize += optPicSize / 1024.00;

            optimizedImage.setMiniSize(tmpPicAttr.getSize());
            optimizedImage.setSavedSize(orgPicSize - optPicSize);
            optimizedImage.setSaveRatio((orgPicSize - optPicSize) * 100.00 / (orgPicSize));

            String optCloudStoreUrl = CLoudStorage.storage(baseName, Const.OPT_IMG_BASE_PATH +
                    MD5.CalcMD5(img) + baseName);
            String orgCloudStoreUrl = CLoudStorage.storage(baseName, Const.DOWNLOAD_IMG_BASE_PATH +
                    MD5.CalcMD5(img) + baseName);

            if (optCloudStoreUrl != null) {
                optCloudStoreImgMap.put(img, optCloudStoreUrl);
            }
            if (orgCloudStoreUrl != null) {
                orgCloudStoreImgMap.put(img, orgCloudStoreUrl);
            }

            optimizedImage.setBeforeOptImg(orgCloudStoreUrl);
            optimizedImage.setAfterOptImg(optCloudStoreUrl);

            optimizedNum++;

            storeBean.getOptimizedImages().add(optimizedImage);
        }
        storeBean.setBeforeOptSize(df.format(totalOrgSize));
        storeBean.setAfterOptSize(df.format(totalOptSize));
        storeBean.setSavedSize(df.format(totalOrgSize - totalOptSize));
        storeBean.setOptimizedNum(optimizedNum);

        // System.out.println(reqTask.getRequestid());
        // System.out.println(JSON.toJSONString(storeBean));

        redis.addKV(reqTask.getRequestid() + Const.OPT_RESULT_KEY_SUFFIX, JSON.toJSONString(storeBean));

        // FileUtil.deleteDir(new File(Const.DOWNLOAD_IMG_BASE_PATH));
    }

    public void run() {
        doImgOpt();
    }
}
