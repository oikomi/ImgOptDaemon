package com.baidu.uaq.imgoptdaemon.server.controller;

import com.baidu.uaq.imgoptdaemon.bean.RespCmd;
import com.baidu.uaq.imgoptdaemon.config.Config;
import com.baidu.uaq.imgoptdaemon.config.Const;
import com.baidu.uaq.imgoptdaemon.core.TaskRunV2;
import com.baidu.uaq.imgoptdaemon.db.Redis;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created by baidu on 15/12/9.
 */

@Controller
@RequestMapping("/uaq/v1")
public class ImgOptDaemonAction {
    private Config config = Config.getInstance();

//    @PostConstruct
//    public void init() {
//        File f = new File(Const.DOWNLOAD_IMG_BASE_PATH);
//        f.mkdirs();
//        f = new File(Const.OPT_IMG_BASE_PATH);
//        f.mkdirs();
//    }

    @RequestMapping(value = "/doimgopt", method = RequestMethod.GET)
    public @ResponseBody RespCmd doimgopt() {
        RespCmd respCmd = new RespCmd();
        respCmd.setCode(0);
        respCmd.setInfo("success");
        new Thread(new TaskRunV2()).start();

        return respCmd;
    }

//    @RequestMapping(value = "/imgopt", method = RequestMethod.POST)
//    public @ResponseBody
//    RespCmd imgopt(@RequestBody String reqBody) {
//        RespCmd respCmd = new RespCmd();
//        if (reqBody == null) {
//            respCmd.setCode(-1);
//            respCmd.setInfo("error");
//        }
//
//        Redis redis = new Redis(config.getRedisAddr(), config.getRedisPort());
//        System.out.println("store in redis");
//        redis.pushTask(reqBody);
//        System.out.println("store in redis  end");
//        respCmd.setCode(0);
//        respCmd.setInfo("success");
//
//        return respCmd;
//    }
}
