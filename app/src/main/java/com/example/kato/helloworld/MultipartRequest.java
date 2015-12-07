package com.example.kato.helloworld;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

public abstract class MultipartRequest<T> extends Request<T> {

    private final MultipartEntityBuilder mMultipartEntityBuilder = MultipartEntityBuilder.create();


    //コンテンツ設定に必要なデーターを格納
    public HttpEntity mHttpEntity;

    public MultipartRequest(@NonNull String url, @Nullable LinkedHashMap<String, String> requestStringBody, @Nullable LinkedHashMap<String, File> requestFileBody, @NonNull Response.Listener<T> listener, @NonNull Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        //mistener = listener;

        this.mMultipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        this.mMultipartEntityBuilder.setCharset(Charset.defaultCharset());

        if (requestStringBody != null) {
            ContentType contentType = ContentType.create("text/plain", Consts.UTF_8);
            for (LinkedHashMap.Entry<String, String> entry : requestStringBody.entrySet()) {
                this.mMultipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), contentType);
            }
        }

        if (requestFileBody != null) {
            for (LinkedHashMap.Entry<String, File> entry : requestFileBody.entrySet()) {
                this.mMultipartEntityBuilder.addBinaryBody(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public String getBodyContentType() {
        return this.mHttpEntity.getContentType().getValue();
    }

    @Override
    protected void deliverResponse(T response) {
        //this.mVolleyResponseListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        this.mHttpEntity = this.mMultipartEntityBuilder.build();

        try {
            this.mHttpEntity.writeTo(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }

}


