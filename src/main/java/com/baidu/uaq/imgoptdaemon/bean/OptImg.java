package com.baidu.uaq.imgoptdaemon.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miaohong01 on 15/11/13.
 */
public class OptImg {
    private String ua;
    private String refer;
    private List<String> imgs = new ArrayList<String>();

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }
}
