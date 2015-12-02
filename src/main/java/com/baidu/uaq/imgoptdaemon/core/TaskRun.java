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

import javax.jws.soap.SOAPBinding;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by miaohong01 on 15/11/20.
 */
public class TaskRun {
    private static Config config = Config.getInstance();

    public static void main(String[] args) {
        Redis redis = null;
        redis = new Redis(config.getRedisAddr(), config.getRedisPort());
        System.out.println(config.getRedisAddr());
        System.out.println(config.getRedisPort());
        System.out.println(redis);

        while(true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Map<String, String> orgStoreImgMap = new ConcurrentHashMap<String, String>();
            Map<String, String> optStoreImgMap = new ConcurrentHashMap<String, String>();

            Map<String, String> orgCloudStoreImgMap = new ConcurrentHashMap<String, String>();
            Map<String, String> optCloudStoreImgMap = new ConcurrentHashMap<String, String>();
            double totalOrgSize = 0.0;
            double totalOptSize = 0.0;
            int optimizedNum = 0;
            OptImg optImg = null;
            ReqTask reqTask = null;

            try {
                // System.out.println("pop task");
                //List<String> tasks = redis.popTask();
                String task = redis.popTask();

                // System.out.println("pop task end");
                //String task = tasks.get(1);

                if (task != null) {
                    Gson gson = new Gson();
                    reqTask = gson.fromJson(task, ReqTask.class);
                    optImg = gson.fromJson(reqTask.getImagelist(), OptImg.class);
                    // continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if (optImg == null) {
                System.out.println("optImg is null");
                continue;
            }
            System.out.println(optImg.getImgs());

            StoreBean storeBean = new StoreBean();

            for (String img : optImg.getImgs()) {
                System.out.println("download img : " + img);
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
                System.out.println("run cmd img : " + img);
                String baseName = Util.getPicBaseName(img);
                String orgImgStorePath = entry.getValue();
                String cmd = Util.getShellCmdByPicType(img, orgImgStorePath);
                if (cmd == null) {
                    continue;
                }
                try {
                    System.out.println(cmd);
                    Shell.runCmd(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                optStoreImgMap.put(img, Const.OPT_IMG_BASE_PATH + MD5.CalcMD5(img) + baseName);

                StoreBean.OptimizedImage optimizedImage = storeBean.new OptimizedImage();
                optimizedImage.setOrgImg(img);

                PicAttr tmpPicAttr = FileUtil.getPicAttr(Const.DOWNLOAD_IMG_BASE_PATH +
                        MD5.CalcMD5(img) + baseName);
                long orgPicSize = tmpPicAttr.getSize();
                totalOrgSize += orgPicSize / 1024.00;

                optimizedImage.setWidth(tmpPicAttr.getWidth());
                optimizedImage.setHeight(tmpPicAttr.getHeight());
                optimizedImage.setOrgSize(tmpPicAttr.getSize());

                tmpPicAttr = FileUtil.getPicAttr(Const.OPT_IMG_BASE_PATH +
                        MD5.CalcMD5(img) + baseName);

                long optPicSize = tmpPicAttr.getSize();
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
            storeBean.setBeforeOptSize(totalOrgSize);
            storeBean.setAfterOptSize(totalOptSize);
            storeBean.setSavedSize(totalOrgSize - totalOptSize);
            storeBean.setOptimizedNum(optimizedNum);

            System.out.println(reqTask.getRequestid());
            System.out.println(JSON.toJSONString(storeBean));

            redis.addKV(reqTask.getRequestid() + Const.OPT_RESULT_KEY_SUFFIX, JSON.toJSONString(storeBean));

            System.out.println(reqTask.getRequestid());
            System.out.println("end");
        }
    }
}
