package com.docmanager.s3.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.*;
import com.docmanager.s3.util.FileUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.docmanager.s3.services.S3Services;
import com.docmanager.s3.util.Utility;
import org.springframework.web.multipart.MultipartFile;
import sun.rmi.runtime.Log;

@Service
public class S3ServicesImpl implements S3Services {
	
	private Logger logger = LoggerFactory.getLogger(S3ServicesImpl.class);

	
	@Autowired
	private AmazonS3 s3client;

	@Value("${jsa.s3.bucket}")
	private String bucketName;

	@Override
	public InputStreamResource downloadFile(String keyName) {
		
		try {

            System.out.println("Downloading an object");
            S3Object s3object = s3client.getObject(new GetObjectRequest(
            		bucketName, keyName));
            System.out.println("Content-Type: "  + 
            		s3object.getObjectMetadata().getContentType());
			logger.info("===================== Import File - Done! =====================");
			return new InputStreamResource(s3object.getObjectContent());

            
        } catch (AmazonServiceException ase) {
        	logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
        }
        return null;
	}

	@Override
	public FileManagerResponseObject uploadFile(String keyName,MultipartFile multiPartFile) {
		
		try {
			/*if(multiPartFile.getSize() > 10485760){
				return new FileManagerResponseObject("2","File Size exceeds the limit 10485760bytes "+ keyName);
			}*/
			File file = FileUtility.multipartToFile(multiPartFile);
	        s3client.putObject(new PutObjectRequest(bucketName, keyName, file));
	        logger.info("===================== Upload File - Done! =====================");
			return new FileManagerResponseObject("0","Succesfully Uploaded "+ keyName);
	        
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			return new FileManagerResponseObject("1","Some issue with AWS S3 "+ keyName);
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
			return new FileManagerResponseObject("1","Some issue with AWS S3" + keyName);
        } catch (IOException e) {
			e.printStackTrace();
			return new FileManagerResponseObject("1","Some issue with File "+ keyName);
		}
	}

	@Override
	public List<String> getBucketKeys(){
		return getKeys();
	}

	@Override
	public List<String> getBucketKeys(String userDirectory){

		List<String> keys = getKeys();
		return keys.stream().filter(e -> e.contains(userDirectory)).map(s -> getReplacedValue(s,userDirectory)).collect(Collectors.toList());

	}

	private String getReplacedValue(String fileName,String userDirectory){
		return fileName.replace(userDirectory,"");
	}

	private List<String> getKeys(){
		try{
			List<String> keyList = new ArrayList<>();
			ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
			ListObjectsV2Result result = s3client.listObjectsV2(req);

			for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
				keyList.add(objectSummary.getKey());
			}
			return keyList;
		}
		catch(AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it, so it returned an error response.
			e.printStackTrace();
		}
		catch(SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			e.printStackTrace();
		}
		return null;
	}

}
