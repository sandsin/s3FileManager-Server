package com.docmanager.s3.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtility {

    public  static File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        String fileName = multipart.getName();
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multipart.transferTo(convFile);
        return convFile;
    }
}
