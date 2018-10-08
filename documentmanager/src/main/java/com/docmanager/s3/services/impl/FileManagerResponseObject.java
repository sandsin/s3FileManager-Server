package com.docmanager.s3.services.impl;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class FileManagerResponseObject {

    private String responseCode;
    private String responseContent;

    public FileManagerResponseObject(String responseCode,String responseContent){
        this.responseCode = responseCode;
        this.responseContent = responseContent;
    }
}
