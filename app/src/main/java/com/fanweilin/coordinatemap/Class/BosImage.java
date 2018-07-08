package com.fanweilin.coordinatemap.Class;

import com.baidubce.BceClientException;
import com.baidubce.BceServiceException;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.callback.BosProgressCallback;
import com.baidubce.services.bos.model.PutObjectRequest;
import com.baidubce.services.bos.model.PutObjectResponse;
import com.fanweilin.coordinatemap.DataModel.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/5/26.
 */

public class BosImage {
    private BosClient client;
    private String BJ="bj.bcebos.com";
    private String GZ="gz.bcebos.com";
    private String SU="su.bcebos.com";
    private String BucketName="jwddw";
    public void putFile(final String Objectkey, final String filepath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BosClientConfiguration config = new BosClientConfiguration();
                    config.setCredentials(new DefaultBceCredentials( Constants.BOSAK,Constants.BOSSK));
                    config.setEndpoint(GZ);    //传入Bucket所在区域域名
                    BosClient client = new BosClient(config);
                    // 获取数据流
                    InputStream inputStream = new FileInputStream(filepath);
                    // 以数据流形式上传Object
                    PutObjectResponse putObjectResponseFromInputStream = client.putObject(BucketName, Objectkey, inputStream);
                    PutObjectRequest request = new PutObjectRequest(BucketName, Objectkey, inputStream);
                    request.setProgressCallback(new BosProgressCallback<PutObjectRequest>() {
                        @Override
                        public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                            super.onProgress(request, currentSize, totalSize);
                        }
                    });
                } catch (BceServiceException e) {
                    System.out.println("Error ErrorCode: " + e.getErrorCode());
                    System.out.println("Error RequestId: " + e.getRequestId());
                    System.out.println("Error StatusCode: " + e.getStatusCode());
                    System.out.println("Error Message: " + e.getMessage());
                    System.out.println("Error ErrorType: " + e.getErrorType());
                } catch (BceClientException e) {
                    System.out.println("Error Message: " + e.getMessage());
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
