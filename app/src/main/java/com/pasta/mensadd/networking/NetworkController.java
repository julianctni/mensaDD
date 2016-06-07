package com.pasta.mensadd.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pasta.mensadd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by julian on 17.05.16.
 */
public class NetworkController {

    private static NetworkController mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public static int SUCCESS = 1;
    public static int ERROR = 0;

    private NetworkController(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        mRequestQueue.start();
    }

    public static synchronized NetworkController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkController(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // Instantiate the cache
            Cache cache = new DiskBasedCache(mCtx.getApplicationContext().getCacheDir(), 1024 * 1024);

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);
        }
        return mRequestQueue;
    }


    public void doStringRequest(String url, String body, final AbstractCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResponseMessage(1, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onResponseMessage(ERROR, error.getMessage());
            }
        });
        mRequestQueue.add(stringRequest);
    }

    public void doJSONArrayRequest(String url, String body, final AbstractCallback callback) {
        Log.i("JSON-REQUEST", "Doing request");
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, new JSONArray(), new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onResponseMessage(SUCCESS, response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onResponseMessage(ERROR, error.getMessage().toString());
                    }
                });
        mRequestQueue.add(jsObjRequest);
    }

    public void doImageRequest(String url, final LoadImageCallback callback) {
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.i("LOADING-IMAGE", "SUCCESS");
                callback.onResponseMessage(SUCCESS, "", response);
            }
        }, 0, 0, null, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("LOADING-IMAGE", "ERROR");
            }
        });
        mRequestQueue.add(request);
    }


    public void getCanteenList(String url, AbstractCallback callback) {
        doJSONArrayRequest(url, "", callback);
    }

    public void getMealsForCanteen(String url, AbstractCallback callback) {
        doJSONArrayRequest(url, "", callback);
    }
}
