package org.wpk.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
public class HttpClientUtil {
    private final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    // OkHttp的连接池
    private final OkHttpClient client;

    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static MediaType XML = MediaType.parse("application/xml; charset=utf-8");
    public static MediaType DATA = MediaType.parse("multipart/form-data");

    @Autowired
    public HttpClientUtil(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
    }

    public Request buildRequest(String url, Map<String, String> headerMap) {
        Request.Builder builder = new Request.Builder();

        if (Objects.nonNull(headerMap) && !headerMap.isEmpty()) {
            headerMap.forEach(builder::addHeader);
        }

        return builder.url(url)
                .build();
    }

    public Request buildRequest(String url, String jsonBody, Map<String, String> headerMap) {
        Request.Builder builder = new Request.Builder();

        if (Objects.nonNull(headerMap) && !headerMap.isEmpty()) {
            headerMap.forEach(builder::addHeader);
        }
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        return builder.url(url)
                .post(requestBody)
                .build();
    }

    public Request buildRequest(String url) {
        return buildRequest(url, null);
    }

    /**
     * okhttp在Request构建完成后, 不支持修改header, 需要重新build
     *
     * @param request
     * @param key
     * @param value
     */
    public Request addHeader(Request request, String key, String value) {
        return request.newBuilder().addHeader(key, value).build();
    }

    public Request addHeader(Request request, Map<String, String> headerMap) {
        if (Objects.isNull(headerMap) || headerMap.isEmpty()) {
            return request;
        }
        Request.Builder builder = request.newBuilder();
        headerMap.forEach(builder::addHeader);
        return builder.build();
    }

    private Response executeRequest(Request request) {
        if (Objects.isNull(request)) {
            return null;
        }
        log.info("请求发送-request:'{}'", request);
        // try-with-resources会导致response自动close(), 则后续无法解析response, 替换为普通try-catch, 在response解析方法里释放资源
        try {
            Response response = client.newCall(request).execute();
            log.info("请求结果-response:'{}'", response);
            return response;
        } catch (IOException e) {
            log.error("请求异常-request:'{}'", request);
            throw new RuntimeException(e);
        }
    }

    public String executeString(Request request) {
        Response response = executeRequest(request);
        return getResponseString(response);
    }

    private Response get(String url, Map<String, String> headerMap) {
        Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headerMap) && !headerMap.isEmpty()) {
            headerMap.forEach(builder::addHeader);
        }
        Request request = builder
                .url(url)
                .build();
        log.info("GET请求发送-url:'{}'", url);
        Response response = executeRequest(request);
        log.info("GET请求结果-response:'{}'", response);
        return response;
    }

    private Response getResponse(String url, Map<String, String> headerMap) {
        return get(url, headerMap);
    }

    public String getString(String url, Map<String, String> headerMap) {
        Response response = getResponse(url, headerMap);
        return getResponseString(response);
    }

    private Response post(String url, String jsonBody, Map<String, String> headerMap) {
        if (Objects.isNull(jsonBody)) {
            jsonBody = "";
        }
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headerMap) && !headerMap.isEmpty()) {
            headerMap.forEach(builder::addHeader);
        }
        Request request = builder
                .url(url)
                .post(requestBody)
                .build();
        log.info("POST请求发送-url:'{}',body:'{}'", url, jsonBody);
        Response response = executeRequest(request);
        log.info("POST请求结果-response:'{}'", response);
        return response;
    }

    /**
     * 返回了response对象, 没有释放资源, 方法private不对外开放
     * @param url
     * @param jsonBody
     * @param headerMap
     * @return
     */
    private Response postResponse(String url, String jsonBody, Map<String, String> headerMap) {
        return post(url, jsonBody, headerMap);
    }

    public String postString(String url, String jsonBody, Map<String, String> headerMap) {
        Response response = postResponse(url, jsonBody, headerMap);
        return getResponseString(response);
    }

    /**
     * 从响应结果获取数据内容
     * @param response
     * @return
     */
    private String getResponseString(Response response) {
        if (Objects.nonNull(response) && response.isSuccessful()) {
            try {
                ResponseBody body = response.body();
                if (Objects.nonNull(body)) {
                    return body.string();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        response.close();
        return null;
    }
}
