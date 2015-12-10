package com.baidu.uaq.imgoptdaemon.bean;

/**
 * Created by baidu on 15/12/10.
 */
public class ExternalReqImgOptUrl {
    private String requestId;
    private String url;
    private String quality;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }


    @Override
    public String toString() {
        return "ExternalReqImgOptUrl{" +
                "requestId='" + requestId + '\'' +
                ", url='" + url + '\'' +
                ", quality='" + quality + '\'' +
                '}';
    }

}
