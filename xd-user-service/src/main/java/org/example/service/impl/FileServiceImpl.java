package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Override
    public String uploadUserImg(MultipartFile file) {
        return null;
    }
}
