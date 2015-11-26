package com.baidu.uaq.imgoptdaemon.http;

import com.baidu.uaq.imgoptdaemon.config.Const;
import com.baidu.uaq.imgoptdaemon.util.FileUtil;
import com.baidu.uaq.imgoptdaemon.util.MD5;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by miaohong01 on 15/11/13.
 */
public class DownloadImg {
    private String imgUrl;
    private String storeName;

    public DownloadImg(String imgUrl) {
        this.imgUrl = imgUrl;
        // UUID uuid = UUID.randomUUID();
        String[] urlList = imgUrl.split("/");
        String baseName = urlList[(urlList.length) - 1];
        this.storeName = MD5.CalcMD5(this.imgUrl) + baseName;
    }

    public String download() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(this.imgUrl);
            // System.out.println("executing request " + httpget.getURI());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (entity != null) {
                    FileUtil.write(Const.DOWNLOAD_IMG_BASE_PATH + this.storeName, EntityUtils.toString(entity));

                    return Const.DOWNLOAD_IMG_BASE_PATH + this.storeName;
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public boolean httpDownloadFile(String filePath) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(this.imgUrl);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            try {
                // System.out.println(response1.getStatusLine());
                HttpEntity httpEntity = response.getEntity();
                long contentLength = httpEntity.getContentLength();
                InputStream is = httpEntity.getContent();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int r = 0;
                long totalRead = 0;
                while ((r = is.read(buffer)) > 0) {
                    output.write(buffer, 0, r);
                    totalRead += r;
                }
                FileOutputStream fos = new FileOutputStream(filePath);
                output.writeTo(fos);
                output.flush();
                output.close();
                fos.close();
                EntityUtils.consume(httpEntity);

                return true;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
