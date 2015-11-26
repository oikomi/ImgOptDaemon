package com.baidu.uaq.imgoptdaemon.bcs;


import com.baidu.uaq.imgoptdaemon.config.Config;
import com.baidu.uaq.imgoptdaemon.util.FileUtil;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.baidubce.services.bos.model.PutObjectResponse;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by miaohong01 on 15/11/13.
 */
public class CLoudStorage {
    private static Config config = Config.getInstance();
    public static String storage(String basename, String filePath) {
        String url = null;
        BosClientConfiguration bosConfig = new BosClientConfiguration();
        bosConfig.setCredentials(new DefaultBceCredentials(config.getAccessKey(), config.getSecretKey()));
        bosConfig.setEndpoint(config.getEndPoint());
        BosClient client = new BosClient(bosConfig);
        try {
            url = putObjectByFile(client, config.getBucket(), basename, new File(filePath));

        } catch (Exception e) {

        } finally {
            FileUtil.deleteFile(filePath);
        }

        return url;
    }

    private static String putObjectByFile(BosClient client, String bucketName, String basename, File file) {
        Random random = new Random();
        int randomNum = random.nextInt(99999) % (99999 - 10000 + 1) + 10000;
        Date dNow = new Date();
        DateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
        String formatData = dataFormat.format(dNow);
        String objectKey = formatData + "/" + randomNum + basename;

        PutObjectResponse putObjectFromFileResponse = client.putObject(bucketName, objectKey, file);

        // listObjects(client, bucketName);

        // System.out.println(putObjectFromFileResponse.getETag());
        String url = generatePresignedUrl(client, bucketName, objectKey, -1);

        return url;
    }

    private static String generatePresignedUrl(BosClient client, String bucketName, String objectKey,
                                               int expirationInSeconds) {
        URL url = client.generatePresignedUrl(bucketName, objectKey, expirationInSeconds);

        return url.toString();
    }


    public static void listObjects(BosClient client, String bucketName) {
        ListObjectsResponse listing = client.listObjects(bucketName);
        for (BosObjectSummary objectSummary : listing.getContents()) {
            // System.out.println("ObjectKey: " + objectSummary.getKey());
        }
    }

}
