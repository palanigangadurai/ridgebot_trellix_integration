package com.ridgebot.ext.restclient;

import com.mcafee.epo.core.model.ProxyInfoVO;
import com.mcafee.epo.core.services.EPOProxyService;
import com.mcafee.orion.core.auth.UserLoader;
import com.mcafee.orion.core.db.base.Database;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.log4j.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;

public class RidgeBotServerProxyUtil {

    private static final Logger LOGG = Logger.getLogger(RidgeBotServerProxyUtil.class);
    private EPOProxyService epoProxyService = null;
    private Database database;
    private UserLoader userLoader;

    public static HttpComponentsClientHttpRequestFactory getRequestFactory(EPOProxyService epoProxyService, Connection connection, SSLConnectionSocketFactory sslCSF) {
        ProxyInfoVO proxyInfoVO = null;
        try {
            LOGG.debug("RidgeBot :: before ePO Proxy service" + epoProxyService);
            proxyInfoVO = epoProxyService.get(connection);
            LOGG.debug("RidgeBot :: after ePO Proxy service :: proxy info vao ==" + proxyInfoVO);
        } catch (Exception e) {
            LOGG.error("Extension Creation Wizard: exception occurred while getting proxy object");
            LOGG.error("Extension Creation Wizard: " + e.getMessage());
            LOGG.error(e.getStackTrace());
        }
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        HttpComponentsClientHttpRequestFactory factory = null;
        if (proxyInfoVO != null) {
            if (proxyInfoVO.getProxyType() == 2) {
                factory = new HttpComponentsClientHttpRequestFactory();
                LOGG.debug("RidgeBot : After Http client factory == " + factory);
                if (!"".equals(proxyInfoVO.getManualProxy_HttpServer()) && !"".equals(proxyInfoVO.getManualProxy_FtpServer())) {
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(new AuthScope(proxyInfoVO.getManualProxy_HttpServer(), proxyInfoVO.getManualProxy_HttpServerPort()),
                            new UsernamePasswordCredentials(proxyInfoVO.getHttpProxyAuthUser(), proxyInfoVO.getHttpProxyAuthPassword()));
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyInfoVO.getManualProxy_HttpServer(), proxyInfoVO.getManualProxy_HttpServerPort()));
                    requestFactory.setProxy(proxy);
                    final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
                    clientBuilder.useSystemProperties();
                    clientBuilder.setProxy(new HttpHost(proxyInfoVO.getManualProxy_HttpServer(), proxyInfoVO.getManualProxy_HttpServerPort()));
                    clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
                    clientBuilder.setSSLSocketFactory(sslCSF);
                    final CloseableHttpClient client = clientBuilder.setDefaultCredentialsProvider(credentialsProvider).build();
                    factory.setHttpClient(client);
                }
            }
        }
        return factory;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public UserLoader getUserLoader() {
        return userLoader;
    }

    public void setUserLoader(UserLoader userLoader) {
        this.userLoader = userLoader;
    }

    public EPOProxyService getEpoProxyService() {
        return epoProxyService;
    }

    public void setEpoProxyService(EPOProxyService epoProxyService) {
        this.epoProxyService = epoProxyService;
    }
}
