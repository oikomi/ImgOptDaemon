package com.baidu.uaq.imgoptdaemon.bean;

/**
 * Created by miaohong01 on 15/11/13.
 */
public class ReqTask {
    private String username;
    private String password;
    //private OptImg imagelist = new OptImg();
    private String imagelist;
    private String requestid;
    private String callbackurl;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImagelist() {
        return imagelist;
    }

    public void setImagelist(String imagelist) {
        this.imagelist = imagelist;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public String getCallbackurl() {
        return callbackurl;
    }

    public void setCallbackurl(String callbackurl) {
        this.callbackurl = callbackurl;
    }
}
