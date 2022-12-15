package org.example.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.example.config.OSSConfig;
import org.example.service.FileService;
import org.example.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private OSSConfig ossConfig;

    @Override
    public String uploadUserImg(MultipartFile file) {

        // get config
        String bucketname = ossConfig.getBucketName();
        String endpoint = ossConfig.getEndPoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();

        // create oss object
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // get original name
        String originalFileName = file.getOriginalFilename();

        // jdk8 date format
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String folder = dtf.format(ldt);

        // build path, store in oss 2022/12/1/UUID.jpg
        String fileName = CommonUtil.generateUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // create under user folder
        String newFileName = "user/" + folder + "/" + fileName + extension;

        try {
            PutObjectResult putObjectResult = ossClient.putObject(bucketname, newFileName, file.getInputStream());
            // return path
            if (putObjectResult != null) {
                String imgUrl = "https://" + bucketname + "." + endpoint + "/" + newFileName;
                return imgUrl;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // need to close or memory leak may happen
            ossClient.shutdown();
        }
        return null;
    }
}
