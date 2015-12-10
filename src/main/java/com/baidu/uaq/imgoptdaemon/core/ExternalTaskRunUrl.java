package com.baidu.uaq.imgoptdaemon.core;

import com.alibaba.fastjson.JSON;
import com.baidu.uaq.imgoptdaemon.bcs.CLoudStorage;
import com.baidu.uaq.imgoptdaemon.bean.ExternalReqImgOptUrl;
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

import java.text.DecimalFormat;

/**
 * Created by miaohong01 on 15/12/10.
 */
public class ExternalTaskRunUrl implements Runnable {
    private Config config = Config.getInstance();
    private ExternalReqImgOptUrl externalReqImgOptUrl;

    public ExternalTaskRunUrl(ExternalReqImgOptUrl externalTaskRunUrl) {
        this.externalReqImgOptUrl = externalTaskRunUrl;
    }

    public void run() {
        Redis redis = null;
        redis = new Redis(config.getRedisAddr(), config.getRedisPort());
        DecimalFormat df = new DecimalFormat("#.00");
        String orgImgStorePath = null;
        double totalOrgSize = 0.0;
        double totalOptSize = 0.0;
        int optimizedNum = 1;

        StoreBean storeBean = new StoreBean();
        System.out.println(this.externalReqImgOptUrl.getUrl());
        String baseName = Util.getPicBaseName(this.externalReqImgOptUrl.getUrl());
        String storeName = MD5.CalcMD5(this.externalReqImgOptUrl.getUrl()) + baseName;
        DownloadImg downloadImg = new DownloadImg(this.externalReqImgOptUrl.getUrl());
        boolean flag = downloadImg.httpDownloadFile(Const.DOWNLOAD_IMG_BASE_PATH + storeName);
        if (flag) {
            orgImgStorePath = Const.DOWNLOAD_IMG_BASE_PATH + storeName;

        } else {
            return;
        }

        String cmd = Util.getShellCmdByPicType(this.externalReqImgOptUrl.getUrl(), orgImgStorePath);
        if (cmd == null) {
            return;
        }
        try {
            Shell.runCmd(cmd);
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        StoreBean.OptimizedImage optimizedImage = storeBean.new OptimizedImage();

        optimizedImage.setOrgImg(this.externalReqImgOptUrl.getUrl());

        PicAttr tmpPicAttr = FileUtil.getPicAttr(Const.DOWNLOAD_IMG_BASE_PATH +
                MD5.CalcMD5(this.externalReqImgOptUrl.getUrl()) + baseName);

        if (tmpPicAttr == null) {
            return;
        }

        long orgPicSize = tmpPicAttr.getSize();
        // totalOrgSize += orgPicSize / 1024.00;

        optimizedImage.setWidth(tmpPicAttr.getWidth());
        optimizedImage.setHeight(tmpPicAttr.getHeight());
        optimizedImage.setOrgSize(tmpPicAttr.getSize());

        tmpPicAttr = FileUtil.getPicAttr(Const.OPT_IMG_BASE_PATH +
                MD5.CalcMD5(this.externalReqImgOptUrl.getUrl()) + baseName);

        if (tmpPicAttr == null) {
            return;
        }

        long optPicSize = tmpPicAttr.getSize();

        if (optPicSize >= orgPicSize) {
            return;
        }

        totalOrgSize += orgPicSize / 1024.00;
        totalOptSize += optPicSize / 1024.00;

        optimizedImage.setMiniSize(tmpPicAttr.getSize());
        optimizedImage.setSavedSize(orgPicSize - optPicSize);
        optimizedImage.setSaveRatio((orgPicSize - optPicSize) * 100.00 / (orgPicSize));

        String optCloudStoreUrl = CLoudStorage.storage(baseName, Const.OPT_IMG_BASE_PATH +
                MD5.CalcMD5(this.externalReqImgOptUrl.getUrl()) + baseName);
        String orgCloudStoreUrl = CLoudStorage.storage(baseName, Const.DOWNLOAD_IMG_BASE_PATH +
                MD5.CalcMD5(this.externalReqImgOptUrl.getUrl()) + baseName);


        optimizedImage.setBeforeOptImg(orgCloudStoreUrl);
        optimizedImage.setAfterOptImg(optCloudStoreUrl);

        storeBean.getOptimizedImages().add(optimizedImage);

        storeBean.setBeforeOptSize(df.format(totalOrgSize));
        storeBean.setAfterOptSize(df.format(totalOptSize));
        storeBean.setSavedSize(df.format(totalOrgSize - totalOptSize));
        storeBean.setOptimizedNum(optimizedNum);

        redis.addKV(this.externalReqImgOptUrl.getRequestId() + Const.EXT_OPT_RESULT_KEY_SUFFIX,
                JSON.toJSONString(storeBean));
    }
}
