package com.project.test;

import com.project.test.protocols.Protocol;
import com.project.test.protocolsFactory.ProtocolFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private void downLoadFile(String url) {
        if(url == null || url.isEmpty())
            return;
        try {
            Protocol protocol = ProtocolFactory.createProtocol(url.substring(0, url.indexOf(":")));
            protocol.downloadFile(new URL(url), url.substring(url.lastIndexOf("/") + 1), "files");
        } catch (Exception e) {
            log.info("Something wrong with the given url : {}, reason: {}", url, e.getMessage());
        }
    }

    @Override
    public void run(String... args) {
        try (Stream<String> stream = Files.lines(Paths.get(args[0]))) {
            stream.forEach(url -> downLoadFile(url) );
        } catch (IOException e) {
            log.info("Could not read the file: {}", args[0]);
        }
    }
}
