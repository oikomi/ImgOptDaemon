package com.baidu.uaq.imgoptdaemon.server.controller;

import com.baidu.uaq.imgoptdaemon.bean.ExternalReqImgOptUrl;
import com.baidu.uaq.imgoptdaemon.bean.ReqTask;
import com.baidu.uaq.imgoptdaemon.bean.RespCmd;
import com.baidu.uaq.imgoptdaemon.config.Const;
import com.baidu.uaq.imgoptdaemon.core.ExternalTaskRunUrl;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by baidu on 15/12/10.
 */

@Controller
@RequestMapping("/v2/uaq/imgopt")
public class ImgOptExternalAction {
    private Gson gson = new Gson();

    @RequestMapping(value = "/url", method = RequestMethod.POST)
    public @ResponseBody RespCmd doImgoptByUrl(@RequestBody String reqBody) {
        ExternalReqImgOptUrl externalReqImgOptUrl = gson.fromJson(reqBody, ExternalReqImgOptUrl.class);

        RespCmd respCmd = new RespCmd();
        respCmd.setCode(Const.SUCCESS_CODE);
        respCmd.setInfo(Const.SUCCESS_INFO);


        new Thread(new ExternalTaskRunUrl(externalReqImgOptUrl)).start();

        return respCmd;

    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void doImgoptByUpload() {


    }


}
