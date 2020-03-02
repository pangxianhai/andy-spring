package com.andy.spring.util;

import com.andy.spring.constant.Constant;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

/**
 * HTTPS工具
 *
 * @author 庞先海 2019-11-14
 */

public class HttpsClientUtil {

    public static String sendGet(String url, SSLContext sslContext) {
        return sendGet(url, null, null, Constant.DEFAULT_CHARSET, sslContext, RequestConfig.DEFAULT);
    }

    public static String sendGet(String url, Map<String, Object> paramMap, SSLContext sslContext,
        RequestConfig config) {
        return sendGet(url, paramMap, null, Constant.DEFAULT_CHARSET, sslContext, config);
    }

    public static String sendGet(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        SSLContext sslContext, RequestConfig config) {
        return sendGet(url, paramMap, headerMap, Constant.DEFAULT_CHARSET, sslContext, config);
    }

    public static String sendGet(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        Charset charset, SSLContext sslContext, RequestConfig config) {
        try {
            String body = "";
            if (sslContext == null) {
                sslContext = createIgnoreVerifySsl();
            }
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                "http", PlainConnectionSocketFactory.INSTANCE).register("https",
                new SSLConnectionSocketFactory(sslContext)).build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(paramMap.get(key))));
                }
            }
            String queryUri = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairList, charset));
            if (url.contains("?")) {
                url += "&" + queryUri;
            } else {
                url += "?" + queryUri;
            }
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(config);
            if (! CollectionUtils.isEmpty(headerMap)) {
                for (String key : headerMap.keySet()) {
                    httpGet.setHeader(key, String.valueOf(headerMap.get(key)));
                }
            }

            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return body;
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendPost(String url, Map<String, Object> paramMap, SSLContext sslContext) {
        return sendPost(url, paramMap, null, null, sslContext, RequestConfig.DEFAULT);
    }

    public static String sendPost(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        String requestBody, SSLContext sslContext, RequestConfig config) {
        return sendPost(url, paramMap, headerMap, requestBody, Constant.DEFAULT_CHARSET, sslContext, config);
    }

    public static String sendPost(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        String requestBody, Charset charset, SSLContext sslContext, RequestConfig config) {
        try {
            String body = "";
            if (sslContext == null) {
                sslContext = createIgnoreVerifySsl();
            }
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                "http", PlainConnectionSocketFactory.INSTANCE).register("https",
                new SSLConnectionSocketFactory(sslContext)).build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(config);
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(paramMap.get(key))));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, charset));
            if (! CollectionUtils.isEmpty(headerMap)) {
                for (String key : headerMap.keySet()) {
                    httpPost.setHeader(key, String.valueOf(headerMap.get(key)));
                }
            }
            if (StringUtils.isNotEmpty(requestBody)) {
                httpPost.setEntity(new StringEntity(requestBody, charset));
            }
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return body;
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendPut(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        String requestBody, SSLContext sslContext, RequestConfig config) {
        return sendPut(url, paramMap, headerMap, requestBody, Constant.DEFAULT_CHARSET, sslContext, config);
    }

    public static String sendPut(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        String requestBody, Charset charset, SSLContext sslContext, RequestConfig config) {
        try {
            String body = "";
            if (sslContext == null) {
                sslContext = createIgnoreVerifySsl();
            }
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                "http", PlainConnectionSocketFactory.INSTANCE).register("https",
                new SSLConnectionSocketFactory(sslContext)).build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
            HttpPut httpPut = new HttpPut(url);
            httpPut.setConfig(config);
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(paramMap.get(key))));
                }
            }
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairList, charset));
            if (CollectionUtils.isEmpty(headerMap)) {
                for (String key : headerMap.keySet()) {
                    httpPut.setHeader(key, String.valueOf(headerMap.get(key)));
                }
            }
            if (StringUtils.isNotEmpty(requestBody)) {
                httpPut.setEntity(new StringEntity(requestBody, charset));
            }
            CloseableHttpResponse response = client.execute(httpPut);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return body;
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SSLContext createIgnoreVerifySsl() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] {trustManager}, null);
        return sc;
    }
}
