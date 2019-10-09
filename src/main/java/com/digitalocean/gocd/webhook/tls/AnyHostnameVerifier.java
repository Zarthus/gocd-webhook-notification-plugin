package com.digitalocean.gocd.webhook.tls;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class AnyHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
