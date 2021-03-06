package com.baidu.uaq.imgoptdaemon.config;

/**
 * Created by miaohong01 on 15/11/16.
 */
public class Const {
    public static final String DOWNLOAD_IMG_BASE_PATH = "/tmp/download_img/";
    public static final String CONVERT_CMD = "convert";

    public static final String PNGQUANT_CMD = "pngquant";

    public static final String GIFSICLE_CMD = "gifsicle";

    public static final String OPT_IMG_BASE_PATH = "/tmp/opt_img/";

    public static final String OPT_RESULT_KEY_SUFFIX = "_imgopt";

    public static final String EXT_OPT_RESULT_KEY_SUFFIX = "_extimgopt";


    public static final int SUCCESS_CODE = 0;
    public static final int FAILED_CODE = -1;

    public static final String SUCCESS_INFO = "success";
    public static final String FAILED__INFO = "error";
}
