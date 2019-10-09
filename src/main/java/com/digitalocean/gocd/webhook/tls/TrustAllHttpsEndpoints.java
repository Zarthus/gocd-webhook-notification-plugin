package com.digitalocean.gocd.webhook.tls;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.HttpURLConnection;

/**
 * This class is useful in environments where the endpoints can be completely trusted.
 * It is a terrible idea to use this to connect to public HTTPS endpoints.
 * Use this at your own risk.
 */
public class TrustAllHttpsEndpoints {

    private static SSLContext context;

    private static SSLContext getContext() throws Exception {
        if (context == null) {
            SSLContext ctx = SSLContext.getInstance("TLS");
            TrustManager[] managers = {new CompleteTrustManager()};
            ctx.init(null, managers, null);
            context = ctx;
        }
        return context;
    }

    public static void configure(HttpURLConnection conn) throws Exception {
        HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
        httpsConn.setHostnameVerifier(new AnyHostnameVerifier());
        httpsConn.setSSLSocketFactory(getContext().getSocketFactory());
    }
}
