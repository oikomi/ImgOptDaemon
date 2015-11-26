package com.baidu.uaq.imgoptdaemon.db;

import java.util.ArrayList;

/**
 * Created by miaohong01 on 15/11/18.
 */
public class StoreBean {
    private double afterOptSize;
    private double beforeOptSize;
    private ArrayList<OptimizedImage> optimizedImages;
    private int optimizedNum;
    private double savedSize;

    public StoreBean () {
        this.optimizedImages = new ArrayList<OptimizedImage>();
    }

    public double getAfterOptSize() {
        return afterOptSize;
    }

    public void setAfterOptSize(double afterOptSize) {
        this.afterOptSize = afterOptSize;
    }

    public double getBeforeOptSize() {
        return beforeOptSize;
    }

    public void setBeforeOptSize(double beforeOptSize) {
        this.beforeOptSize = beforeOptSize;
    }

    public ArrayList<OptimizedImage> getOptimizedImages() {
        return optimizedImages;
    }

    public void setOptimizedImages(ArrayList<OptimizedImage> optimizedImages) {
        this.optimizedImages = optimizedImages;
    }

    public int getOptimizedNum() {
        return optimizedNum;
    }

    public void setOptimizedNum(int optimizedNum) {
        this.optimizedNum = optimizedNum;
    }

    public double getSavedSize() {
        return savedSize;
    }

    public void setSavedSize(double savedSize) {
        this.savedSize = savedSize;
    }

    public class OptimizedImage {
        private String afterOptImg;
        private String beforeOptImg;
        private int height;
        private double miniSize;
        private String orgImg;
        private double orgSize;
        private double saveRatio;
        private double savedSize;
        private int width;

        public String getAfterOptImg() {
            return afterOptImg;
        }

        public void setAfterOptImg(String afterOptImg) {
            this.afterOptImg = afterOptImg;
        }

        public String getBeforeOptImg() {
            return beforeOptImg;
        }

        public void setBeforeOptImg(String beforeOptImg) {
            this.beforeOptImg = beforeOptImg;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public double getMiniSize() {
            return miniSize;
        }

        public void setMiniSize(double miniSize) {
            this.miniSize = miniSize;
        }

        public String getOrgImg() {
            return orgImg;
        }

        public void setOrgImg(String orgImg) {
            this.orgImg = orgImg;
        }

        public double getOrgSize() {
            return orgSize;
        }

        public void setOrgSize(double orgSize) {
            this.orgSize = orgSize;
        }

        public double getSaveRatio() {
            return saveRatio;
        }

        public void setSaveRatio(double saveRatio) {
            this.saveRatio = saveRatio;
        }

        public double getSavedSize() {
            return savedSize;
        }

        public void setSavedSize(double savedSize) {
            this.savedSize = savedSize;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }

}
