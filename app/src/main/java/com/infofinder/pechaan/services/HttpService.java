package com.infofinder.pechaan.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class HttpService {

    private Context context;
    private RequestQueue requestQueue;

    public HttpService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    private void getInternetPermission(){

    }

    public void makeGet(String url, Response.Listener<JSONObject> responseListener,
                        Response.ErrorListener errorListener){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                responseListener,
                errorListener
        );
        requestQueue.add(jsonObjectRequest);

    }

    public JsonObjectRequest makePost(String url, JSONObject jsonObject, Response.Listener<JSONObject> responseListener,
                         Response.ErrorListener errorListener){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                responseListener,
                errorListener
        );

        requestQueue.add(jsonObjectRequest);
        return jsonObjectRequest;
    }



}
