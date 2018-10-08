package com.docmanager.s3.controller;


import com.docmanager.s3.services.S3Services;
import com.docmanager.s3.services.impl.FileManagerResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class S3RestController {

    @Autowired
    S3Services s3Client;

    @PostMapping("/document/upload")
    public List<FileManagerResponseObject> uploadDocument(@RequestParam("file") MultipartFile[] files){
        ArrayList<FileManagerResponseObject> responses = new ArrayList<>();
        for(MultipartFile file : files){
            responses.add(s3Client.uploadFile(file.getOriginalFilename(),file));
        }
        return responses;
    }

    @PostMapping("/document/upload/{userdirectory}")
    public List<FileManagerResponseObject> uploadUserDocument(@RequestParam("file") MultipartFile[] files,@PathVariable("userdirectory") String userDirectory){
        ArrayList<FileManagerResponseObject> responses = new ArrayList<>();
        for(MultipartFile file : files){
            responses.add(s3Client.uploadFile(userDirectory+"/"+file.getOriginalFilename(),file));
        }
        return responses;
    }

    @GetMapping("/document/{filename:.+}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("filename") String fileName){
        InputStreamResource streamResource = s3Client.downloadFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(streamResource);
    }

    @GetMapping("/document/{userdirectory}/{filename:.+}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("filename") String fileName,@PathVariable("userdirectory") String userDirectory){
        String concatFileName = userDirectory+"/"+fileName;
        InputStreamResource streamResource = s3Client.downloadFile(concatFileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(streamResource);
    }

    @GetMapping("/document/files")
    public List<String> getBucketFiles(){
        return s3Client.getBucketKeys();
    }

    @GetMapping("/document/files/{userDirectory}")
    public List<String> getBucketFilesByDirectory(@PathVariable("userDirectory") String userDirectory){
        return s3Client.getBucketKeys(userDirectory+"/");
    }




}
