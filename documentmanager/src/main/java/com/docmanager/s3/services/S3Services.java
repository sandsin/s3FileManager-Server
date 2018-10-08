package com.docmanager.s3.services;

import com.docmanager.s3.services.impl.FileManagerResponseObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Services {
	public InputStreamResource downloadFile(String keyName);
	public FileManagerResponseObject uploadFile(String keyName, MultipartFile uploadFilePath);
	public List<String> getBucketKeys();
	public List<String> getBucketKeys(String userDirectory);
}
