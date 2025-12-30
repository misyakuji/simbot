//package com.miko.util;
//
//
//
//
//
//import java.io.IOException;
//
//
//public class ApiUtil {
//    private static OkHttpClient client = new OkHttpClient();
//
//    public static String callGet(String url) throws IOException {
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
//    }
//
//    String callPost(String url, String json) throws IOException {
//        final MediaType JSON = MediaType.get("application/json");
//        RequestBody body = RequestBody.create(json, JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
//    }
//    public static void main(String[] args) throws IOException {
//
//        System.out.println(callGet("https://api.mcloc.cn/love/"));
//        System.out.println(callGet("https://square.github.io/okhttp/recipes/"));
//    }
//
//}
