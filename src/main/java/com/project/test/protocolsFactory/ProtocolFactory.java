package com.project.test.protocolsFactory;

import com.project.test.protocols.FTPProtocol;
import com.project.test.protocols.HttpProtocol;
import com.project.test.protocols.Protocol;

public class ProtocolFactory {
    public static Protocol createProtocol(String protocol) {
        if("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) {
            return new HttpProtocol();
        } else if ("ftp".equalsIgnoreCase(protocol)) {
            return new FTPProtocol();
        } else {
            return null;
        }
    }
}
