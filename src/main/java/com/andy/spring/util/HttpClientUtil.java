package com.andy.spring.util;

import com.andy.spring.constant.Constant;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

/**
 * http访问工具
 *
 * @author 庞先海 2019-11-14
 */
public class HttpClientUtil {

    public static String sendGet(String url) {
        return sendGet(url, null, null, RequestConfig.DEFAULT, Constant.DEFAULT_CHARSET);
    }

    public static String sendGet(String url, Map<String, Object> paramMap) {
        return sendGet(url, paramMap, RequestConfig.DEFAULT);
    }

    public static String sendGet(String url, Map<String, Object> paramMap, RequestConfig config) {
        return sendGet(url, paramMap, null, config);
    }

    public static String sendGet(String url, Map<String, Object> paramMap, Map<String, Object> headerMap) {
        return sendGet(url, paramMap, headerMap, RequestConfig.DEFAULT);
    }

    public static String sendGet(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config) {
        if (isHttps(url)) {
            return HttpsClientUtil.sendGet(url, paramMap, headerMap, null, config);
        } else {
            return sendGet(url, paramMap, headerMap, config, Constant.DEFAULT_CHARSET);
        }
    }

    public static String sendGet(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config, Charset charset) {
        try {
            String body = null;
            CloseableHttpResponse response = sendGet0(url, paramMap, headerMap, config, charset);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            response.close();
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] sendGetByte(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config, Charset charset) {
        try {
            byte[] body = null;
            CloseableHttpResponse response = sendGet0(url, paramMap, headerMap, config, charset);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toByteArray(entity);
            }
            response.close();
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CloseableHttpResponse sendGet0(String url, Map<String, Object> paramMap,
        Map<String, Object> headerMap, RequestConfig config, Charset charset) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
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

        return client.execute(httpGet);
    }


    public static String sendPost(String url, Map<String, Object> paramMap) {
        return sendPost(url, paramMap, new HashMap<>(0), RequestConfig.DEFAULT);
    }

    public static String sendPost(String url, String requestBoyd) {
        return sendPost(url, requestBoyd, RequestConfig.DEFAULT);
    }

    public static String sendPost(String url, String requestBoyd, RequestConfig config) {
        return sendPost(url, null, requestBoyd, config);
    }

    public static String sendPost(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config) {
        if (isHttps(url)) {
            return HttpsClientUtil.sendPost(url, paramMap, headerMap, null, null, config);
        } else {
            return sendPost(url, paramMap, headerMap, null, config, Constant.DEFAULT_CHARSET);
        }
    }

    public static String sendPost(String url, Map<String, Object> headerMap, String requestBoyd, RequestConfig config) {
        if (isHttps(url)) {
            return HttpsClientUtil.sendPost(url, null, headerMap, requestBoyd, null, config);
        } else {
            return sendPost(url, null, headerMap, requestBoyd, config, Constant.DEFAULT_CHARSET);
        }
    }

    public static String sendPost(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        String requestBody, RequestConfig config, Charset charset) {
        try {
            String body = "";
            CloseableHttpClient client = HttpClients.createDefault();
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String sendPut(String url, Map<String, Object> paramMap) {
        return sendPut(url, paramMap, new HashMap<>(0), RequestConfig.DEFAULT);
    }

    public static String sendPut(String url, String requestBody) {
        return sendPut(url, null, requestBody, RequestConfig.DEFAULT);
    }

    public static String sendPut(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config) {
        if (isHttps(url)) {
            return HttpsClientUtil.sendPut(url, paramMap, headerMap, null, null, config);
        } else {
            return sendPut(url, paramMap, headerMap, null, config, Constant.DEFAULT_CHARSET);
        }
    }


    public static String sendPut(String url, Map<String, Object> headerMap, String requestBody, RequestConfig config) {
        if (isHttps(url)) {
            return HttpsClientUtil.sendPut(url, null, headerMap, requestBody, null, config);
        } else {
            return sendPut(url, null, headerMap, requestBody, config, Constant.DEFAULT_CHARSET);
        }
    }

    public static String sendPut(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        String requestBody, RequestConfig config, Charset charset) {
        try {
            String body = "";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(url);
            httpPut.setConfig(config);
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(paramMap.get(key))));
                }
            }
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairList, charset));
            if (! CollectionUtils.isEmpty(headerMap)) {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendDelete(String url) {
        return sendDelete(url, null, null, RequestConfig.DEFAULT);
    }

    public static String sendDelete(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config) {
        return sendDelete(url, paramMap, headerMap, config, Constant.DEFAULT_CHARSET);
    }

    public static String sendDelete(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config, Charset charset) {
        try {
            String body = "";
            CloseableHttpClient client = HttpClients.createDefault();
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
            HttpDelete httpDelete = new HttpDelete(url);
            httpDelete.setConfig(config);
            if (! CollectionUtils.isEmpty(headerMap)) {
                for (String key : headerMap.keySet()) {
                    httpDelete.setHeader(key, String.valueOf(headerMap.get(key)));
                }
            }

            CloseableHttpResponse response = client.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendHead(String url) {
        return sendHead(url, null, null, RequestConfig.DEFAULT);
    }

    public static String sendHead(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config) {
        return sendHead(url, paramMap, headerMap, config, Constant.DEFAULT_CHARSET);
    }

    public static String sendHead(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config, Charset charset) {
        try {
            String body = "";
            CloseableHttpClient client = HttpClients.createDefault();
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
            HttpHead httpHead = new HttpHead(url);
            httpHead.setConfig(config);
            if (! CollectionUtils.isEmpty(headerMap)) {
                for (String key : headerMap.keySet()) {
                    httpHead.setHeader(key, String.valueOf(headerMap.get(key)));
                }
            }

            CloseableHttpResponse response = client.execute(httpHead);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendOptions(String url) {
        return sendOptions(url, null, null, RequestConfig.DEFAULT);
    }

    public static String sendOptions(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config) {
        return sendOptions(url, paramMap, headerMap, config, Constant.DEFAULT_CHARSET);
    }

    public static String sendOptions(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config, Charset charset) {
        try {
            String body = "";
            CloseableHttpClient client = HttpClients.createDefault();
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
            HttpOptions httpOptions = new HttpOptions(url);
            httpOptions.setConfig(config);
            if (! CollectionUtils.isEmpty(headerMap)) {
                for (String key : headerMap.keySet()) {
                    httpOptions.setHeader(key, String.valueOf(headerMap.get(key)));
                }
            }

            CloseableHttpResponse response = client.execute(httpOptions);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendPatch(String url) {
        return sendPatch(url, null, null, RequestConfig.DEFAULT);
    }

    public static String sendPatch(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config) {
        return sendPatch(url, paramMap, headerMap, config, Constant.DEFAULT_CHARSET);
    }

    public static String sendPatch(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config, Charset charset) {
        try {
            String body = "";
            CloseableHttpClient client = HttpClients.createDefault();
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
            HttpPatch httpPatch = new HttpPatch(url);
            httpPatch.setConfig(config);
            if (! CollectionUtils.isEmpty(headerMap)) {
                for (String key : headerMap.keySet()) {
                    httpPatch.setHeader(key, String.valueOf(headerMap.get(key)));
                }
            }

            CloseableHttpResponse response = client.execute(httpPatch);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendTrace(String url) {
        return sendTrace(url, null, null, RequestConfig.DEFAULT);
    }

    public static String sendTrace(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config) {
        return sendTrace(url, paramMap, headerMap, config, Constant.DEFAULT_CHARSET);
    }

    public static String sendTrace(String url, Map<String, Object> paramMap, Map<String, Object> headerMap,
        RequestConfig config, Charset charset) {
        try {
            String body = "";
            CloseableHttpClient client = HttpClients.createDefault();
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
            HttpTrace httpTrace = new HttpTrace(url);
            httpTrace.setConfig(config);
            if (! CollectionUtils.isEmpty(headerMap)) {
                for (String key : headerMap.keySet()) {
                    httpTrace.setHeader(key, String.valueOf(headerMap.get(key)));
                }
            }

            CloseableHttpResponse response = client.execute(httpTrace);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isHttps(String url) {
        return url.toLowerCase().startsWith("https");
    }
}
