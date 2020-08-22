package me.droreo002.oreocore.utils.io;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class HttpUtils {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Get and read the URL as a list
     *
     * @param url The url
     * @return List of string
     */
    @NotNull
    public static List<String> getAndReadAsList(String url) throws Exception {
        List<String> data = new ArrayList<>();
        HttpResponse response = getRequest(url, null, null).get();
        String content = CharStreams.toString(new InputStreamReader(response.getContent()));
        data.addAll(Arrays.asList(content.split("\n")));
        return data;
    }

    /**
     * Get and read the URL as a JsonObject
     *
     * @param url The url
     * @return List of string
     */
    @Nullable
    public static JsonObject getAndReadAsJson(String url) {
        try {
            return new Gson().fromJson(CharStreams.toString(new InputStreamReader(getRequest(url, null, null).get().getContent())), JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get and read from the url
     *
     * @param url The url
     * @return String data
     * @throws Exception If something goes wrong
     */
    @NotNull
    public static String getAndRead(String url) throws Exception {
        HttpResponse response = getRequest(url, null, null).get();
        return CharStreams.toString(new InputStreamReader(response.getContent()));
    }

    /**
     * Perform a get request into the url
     *
     * @param url The url
     * @param params The url params to add
     * @return Future of HttpResponse
     */
    public static CompletableFuture<HttpResponse> getRequest(final String url, @Nullable Map<String, String> params, @Nullable HttpHeaders headers) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequestFactory factory = HTTP_TRANSPORT.createRequestFactory();
            String resUrl = url;
            if (params != null) {
                StringBuilder builder = new StringBuilder().append("?");
                for (Map.Entry<String, String> ent : params.entrySet()) {
                    builder.append(ent.getKey()).append("=").append(ent.getValue()).append("&");
                }
                resUrl = resUrl + builder.toString().substring(0, builder.toString().length() - 1);
            }
            try {
                return factory.buildGetRequest(new GenericUrl(resUrl))
                        .setHeaders((headers != null) ? headers : generateDefaultHeader())
                        .setSuppressUserAgentSuffix(true)
                        .setConnectTimeout(10000)
                        .execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Perform a post request into the url
     *
     * @param url The url
     * @param data The post request data
     * @return Future of HttpResponse
     */
    public static CompletableFuture<HttpResponse> postRequest(String url, @Nullable Map<String, String> data, @Nullable HttpHeaders headers) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequestFactory factory = HTTP_TRANSPORT.createRequestFactory();
            byte[] bContent = new byte[0];
            if (data != null) {
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, String> ent : data.entrySet()) {
                    builder.append(ent.getKey()).append("=").append(ent.getValue()).append("&");
                }
                bContent = builder.toString().substring(0, builder.toString().length() - 1).getBytes();
            }
            try {
                return factory.buildPostRequest(new GenericUrl(url), new ByteArrayContent("application/x-www-form-urlencoded", bContent))
                        .setHeaders((headers != null) ? headers : generateDefaultHeader())
                        .setSuppressUserAgentSuffix(true)
                        .setConnectTimeout(10000)
                        .execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Perform a post request with file attached
     *
     * @param url The target url
     * @param fileKey The file form key
     * @param file The file to attach
     * @param data Extra data
     * @param headers Extra headers
     * @return Future of HttpResponse
     */
    public static CompletableFuture<HttpResponse> postRequestWithFile(String url, @NotNull String fileKey, @NotNull File file, @Nullable Map<String, String> data, @Nullable HttpHeaders headers) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequestFactory factory = HTTP_TRANSPORT.createRequestFactory();
            MultipartContent content = new MultipartContent().setMediaType(new HttpMediaType("multipart/form-data").setParameter("boundary", "__END_OF_PART__"));
            if (data != null) {
                for (String name : data.keySet()) {
                    content.addPart(new MultipartContent.Part(new ByteArrayContent(null, data.get(name).getBytes())).setHeaders(new HttpHeaders().set(
                            "Content-Disposition",
                            String.format("form-data; name=\"%s\";", name))));
                }
            }
            content.addPart(new MultipartContent.Part(new FileContent("application/octet-stream", file)).setHeaders(new HttpHeaders().set(
                    "Content-Disposition",
                    String.format("form-data; name=\"%s\"; filename=\"%s\"", fileKey, file.getName()))));
            try {
                return factory.buildPostRequest(new GenericUrl(url), content)
                        .setHeaders((headers != null) ? headers : generateDefaultHeader())
                        .setConnectTimeout(10000)
                        .execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Generate a basic header
     *
     * @return HttpHeaders
     */
    public static HttpHeaders generateDefaultHeader() {
        return new HttpHeaders().setAccept("application/json");
    }
}