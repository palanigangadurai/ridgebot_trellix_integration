package com.ridgebot.ext.restclient;

import com.mcafee.epo.core.services.EPOProxyService;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;


/**
 * Created by Admin
 */
public class RidgeBotServerRestClientUtil {

    private static final Logger LOGG = Logger.getLogger(RidgeBotServerRestClientUtil.class);
    private static final String SSL_PROTOCOL_TLSV1_2 = "TLSv1.2";

    public static RestTemplate getRegisteredServerRestTemplate(EPOProxyService epoProxyService, Connection connection) throws Exception {
        SSLContext sslcontext = null;
        LOGG.warn("RidgeBot Registered Server :: All certificates will be accepted.");
        try {
            sslcontext = SSLContext.getInstance(SSL_PROTOCOL_TLSV1_2);
        } catch (NoSuchAlgorithmException e) {
            LOGG.error("RidgeBot Registered Server :: Error Occurred while disabling ssl certification validation. " + e.getMessage(), e);
            throw new Exception("RidgeBot Registered Server :: Error Occurred while disabling ssl certificate validation.", e);
        }
        try {
            LOGG.warn("Registered Server :: before SSL context ::" + sslcontext);
            sslcontext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                                throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new java.security.SecureRandom());
            LOGG.warn("RidgeBot Registered Server :: After SSL context ::" + sslcontext);

            LOGG.warn("RidgeBot Registered Server :: After SSL ePO Proxy service  ::" + epoProxyService);
        } catch (KeyManagementException e) {
            LOGG.error("RidgeBot Registered Server :: Error Occurred while disabling ssl certification validation. " + e.getMessage(), e);
            throw new Exception("RidgeBot Registered Server : Error Occurred while disabling ssl certificate validation.", e);
        }
        SSLConnectionSocketFactory sslCSF = new SSLConnectionSocketFactory(sslcontext, new String[]{
                SSL_PROTOCOL_TLSV1_2},
                null, new HostnameVerifier() {
            @Override
            public boolean verify(String hostName, SSLSession sslSession) {
                boolean result = hostName.equalsIgnoreCase(sslSession.getPeerHost());
                LOGG.warn("RidgeBot Registered Server :: after host verify ");
                return result;
            }
        });
        HttpComponentsClientHttpRequestFactory requestFactory = RidgeBotServerProxyUtil.getRequestFactory(epoProxyService, connection, sslCSF);
        if (null == requestFactory) {
            requestFactory = new HttpComponentsClientHttpRequestFactory();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslCSF)
                    .build();
            LOGG.warn("RidgeBot Registered Server :: after  http Client request factory ::" + requestFactory);
            requestFactory.setHttpClient(httpClient);
        }
        return new RestTemplate(requestFactory);
    }

}
