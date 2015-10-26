package com.example.kato.helloworld;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karin on 2015/10/19.
 */

public class Post {
    RequestQueue mQueue;


    String url = "ここにURLだよ";

    StringRequest request = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("レスポンス", response);//ここのString型のresponseにサーバからのレスポンスが入る
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("エラーレスポンス", "error");
                }
            }) {
        protected Map<String, String> getParams() {
            //HashMapをこの関数内で書くことでサーバにPOSTする（送る）ことができる

            Map<String, String> params = new HashMap<String, String>();

            params.put("name", "MakinoTakashi");
            params.put("age", "21");

            return params;

            //ここではnameというキー値に対してMakinoTakashiを
            //　　　　ageというキー値に対して21を送っている．
        }
        Context sContext;
        void send(){
            mQueue = Volley.newRequestQueue(sContext);

            mQueue.add(request);
        }
    };
}
