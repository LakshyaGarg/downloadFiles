package com.project.test.protocols;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public abstract class Protocol {

    final int connectionTimeout = 10*1000;
    final int readTimeout = 30*1000;

    public abstract URLConnection getConnection(URL url, int connectionTimeout, int readTimeout) throws IOException;
    public  abstract long getContentLength(URLConnection urlConnection);
    public abstract long readFile(URLConnection urlConnection, String fileName);
    public abstract void handleDownload(long expectedContentLength, long actualReadLength,
                                        String file, String outputDirectory) throws IOException;
    public void downloadFile(URL url, String fileName, String outputDir) {
        log.info("File download request for url: {}", url.toString());
        try {
            URLConnection urlConnection = getConnection(url, connectionTimeout, readTimeout);
            long expectedContentLength = getContentLength(urlConnection);
            long actualReadLength = readFile(urlConnection, fileName);
            handleDownload(expectedContentLength, actualReadLength, fileName, outputDir);
        } catch (IOException e) {
            log.info("Error occurred while downloading file: {}, url: {}, reason:{}", fileName,
                    url.toString(), e.getMessage());
        }
    }
}
