package com.project.test.commons;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class CommonUtils {

    public static URLConnection getUrlConnection(URL url, int connectionTimeout, int readTimeout) throws  IOException{
        URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(connectionTimeout);
        urlConnection.setReadTimeout(readTimeout);
        return urlConnection;
    }

    public static void moveFileFromTmp(String outputDir, String fileName) throws IOException {
        if(!Files.exists(Paths.get(outputDir + "/" + fileName)))
            Files.move(Paths.get("/tmp/" + fileName), Paths.get(outputDir + "/" + fileName));
        else
            Files.move(Paths.get("/tmp/" + fileName), Paths.get(outputDir+ "/" +
                                                            System.currentTimeMillis()+"_"+fileName));
    }

    public static void handleDownload(Long expectedFileSize, Long readSize, String fileName, String outputDir)
                                                                                                throws IOException {
        if(readSize < expectedFileSize) {
            log.info("file could not be downloaded {} {}", readSize, expectedFileSize);
            if (Files.exists(Paths.get("/tmp/" + fileName)))
                Files.delete(Paths.get("/tmp/" + fileName));
        } else {
            log.info("File downloaded successfully");
            moveFileFromTmp(outputDir, fileName);
        }
    }

    public static long writeToFile(BufferedInputStream bufferedInputStream, FileOutputStream fileOutputStream)
                                                                                                throws IOException {
        int n = 0;
        long totalRead = 0;
        byte[] buffer = new byte[4096];
        while((n = bufferedInputStream.read(buffer, 0 , 4096)) != -1) {
            fileOutputStream.write(buffer, 0 , n);
            totalRead += n;
        }

        return totalRead;
    }

    public static long readFile(URLConnection urlConnection, String fileName) {
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        long totalRead = 0;
        try {
            bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
            fileOutputStream = new FileOutputStream("/tmp/" + fileName);
            totalRead = writeToFile(bufferedInputStream, fileOutputStream);
        } catch (Exception e) {
            totalRead = -1;
            String message = e.getMessage();
            log.info("Error occurred while downloading the file: {}, reason: {}",  urlConnection.getURL().toString(), message);
            try {
                if (Files.exists(Paths.get("/tmp/" + fileName)))
                    Files.delete(Paths.get("/tmp/" + fileName));
            } catch (IOException ex) {
                log.info("Count not delete the file: {} from tmp", fileName);
            }
        } finally {
            try {
                if (bufferedInputStream != null) bufferedInputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch ( IOException e) {
                log.info("Could not close file: {} properly", fileName);
            }
        }
        return totalRead;
    }

    /**
     * This method is solely for the purpose of unit test so that we can test the behaviour of
     * Code when download fails.
     * @param bufferedInputStream
     * @param fileOutputStream
     * @param fileName
     * @return total read bytes from file
     */
    public static long readFile(BufferedInputStream bufferedInputStream, FileOutputStream fileOutputStream, String fileName) {
        long totalRead = 0;
        try {
            int n = 0;
            byte[] buffer = new byte[4096];
            while((n = bufferedInputStream.read(buffer, 0 , 4096)) != -1) {
                fileOutputStream.write(buffer, 0 , n);
                totalRead += n;
            }
        } catch (Exception e) {
            totalRead = -1;
            String message = e.getMessage();
            try {
                if (Files.exists(Paths.get("/tmp/" + fileName)))
                    Files.delete(Paths.get("/tmp/" + fileName));
            } catch (IOException ex) {
                log.info("Count not delete the file: {} from tmp", fileName);
            }
        } finally {
            try {
                if (bufferedInputStream != null) bufferedInputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch ( IOException e) {
                log.info("Could not close file: {} properly", fileName);
            }
        }
        return totalRead;
    }
}
