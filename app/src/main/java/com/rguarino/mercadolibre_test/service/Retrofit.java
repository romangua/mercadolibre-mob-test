package com.rguarino.mercadolibre_test.service;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public abstract class Retrofit implements Callback<ResponseBody> {

    private static final String BASE_URL = "https://api.mercadolibre.com/";
    private static OkHttpClient client;
    private static ApiInterface apiInterface;
    private static final Boolean showLog = false;

    static {
        if (client == null || apiInterface == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            apiInterface = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(Retrofit.ApiInterface.class);
        }
    }

    public Retrofit() {
    }

    public static Call<ResponseBody> searchItems(Integer increase, String search, Integer offset) {
        return apiInterface.searchItems("sites/MLA/search", increase, offset, search);
    }

    public static Call<ResponseBody> getItem(String itemId) {
        return apiInterface.getItem("items/" + itemId);
    }

    public static Call<ResponseBody> getItemDescription(String itemId) {
        return apiInterface.getItem("items/" + itemId + "/description");
    }

    public interface ApiInterface {
        @GET
        Call<ResponseBody> searchItems(
                @Url String endPoint,
                @Query("limit") Integer limit,
                @Query("offset") Integer offset,
                @Query("q") String search);

        @GET
        Call<ResponseBody> getItem(
                @Url String endPoint);

        @GET
        Call<ResponseBody> getItemDescription(
                @Url String endPoint);
    }

    @Override
    public void onResponse(Call call, Response response) {
        String body = null;
        try {
            if (response.body() != null) {
                body = ((ResponseBody) response.body()).string();
                log("Response", call.request().url().toString() + "\n" + body.toString());
                JSONObject object = new JSONObject(body);
                onResponse(response.code(), object);
            } else {
                body = response.errorBody().string();
                log("Response", call.request().url().toString() + "\n" + body.toString());
                JSONObject object = new JSONObject(body);
                onFailed(response.code(), object.optString("message"));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            if (body != null)
                log("onResponse", body.toString());
            onFailed(response.code(), "¡Algo salió mal! Reintente");
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        log("Response", call.request().url().toString() + "\n" + t.toString());

        if (t instanceof ConnectException || t instanceof SocketTimeoutException || t instanceof UnknownHostException) {
            onFailed(0, "¡No se pudo conectar al servidor!");
        } else if (t instanceof IOException) {
            onFailed(0, "¡Algo salió mal! Reintente");
        } else {
            onFailed(0, t.getMessage());
        }
    }

    public abstract void onResponse(int statusCode, JSONObject jResponse);

    public abstract void onFailed(int statusCode, String message);

    private static void log(String a, String b) {
        if(showLog) {
            Log.e(a, b);
        }
    }
}