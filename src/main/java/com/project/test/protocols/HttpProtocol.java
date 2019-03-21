package com.project.test.protocols;

import com.project.test.commons.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class HttpProtocol extends Protocol {

    @Override
    public URLConnection getConnection(URL url, int connectionTimeout, int readTimeout) throws  IOException{
        return CommonUtils.getUrlConnection(url, connectionTimeout, readTimeout);
    }

    @Override
    public long getContentLength(URLConnection urlConnection) {
        return urlConnection.getContentLength();
    }

    @Override
    public long readFile(URLConnection urlConnection, String fileName) {
        return CommonUtils.readFile(urlConnection, fileName);
    }

    @Override
    public void handleDownload(long expectedContentLength, long actualReadLength, String file,
                                                                    String outputDirectory) throws IOException {
        CommonUtils.handleDownload(actualReadLength, expectedContentLength, file, outputDirectory);
    }

}
