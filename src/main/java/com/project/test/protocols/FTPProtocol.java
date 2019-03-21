package com.project.test.protocols;


import com.project.test.commons.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
@Slf4j
public class FTPProtocol extends Protocol {

    @Override
    public URLConnection getConnection(URL url, int connectionTimeout, int readTimeout) throws IOException {
        return CommonUtils.getUrlConnection(url, connectionTimeout, readTimeout);
    }

    @Override
    public long getContentLength(URLConnection urlConnection) {
        return urlConnection.getContentLengthLong();
    }

    @Override
    public long readFile(URLConnection urlConnection, String fileName) {
        return CommonUtils.readFile(urlConnection, fileName);
    }

    @Override
    public void handleDownload(long expectedContentLength, long actualReadLength, String file, String outputDirectory) throws IOException {
        CommonUtils.handleDownload(actualReadLength, expectedContentLength, file, outputDirectory);
    }
}
