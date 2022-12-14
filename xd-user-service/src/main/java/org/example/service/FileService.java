package org.example.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    public String uploadUserImg(MultipartFile file);
}
