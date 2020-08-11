package com.pasta.mensadd.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pasta.mensadd.R;
import com.pasta.mensadd.networking.callbacks.AbstractCallback;
import com.pasta.mensadd.networking.callbacks.LoadCanteensCallback;
import com.pasta.mensadd.networking.callbacks.LoadImageCallback;
import com.pasta.mensadd.networking.callbacks.LoadNewsCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class NetworkController {

    private static NetworkController mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;

    public static int SUCCESS = 1;
    public static int ERROR = 0;
    public static int NO_INTERNET = -1;

    private static String URL_BASE;
    private static String URL_GET_NEWS;
    private static String URL_GET_CANTEENS;
    private static String URL_GET_MEALS;
    private static String API_KEY;

    private NetworkController(Context context) {
        mCtx = context;
        URL_BASE = context.getString(R.string.url_base);
        URL_GET_NEWS = context.getString(R.string.url_suffix_news);
        URL_GET_CANTEENS = context.getString(R.string.url_suffix_canteens);
        URL_GET_MEALS = context.getString(R.string.url_suffix_meals);
        API_KEY = context.getString(R.string.mensadd_api_key);
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
    /*
    public void doJSONArrayRequest(String url, final AbstractCallback callback) {
        JSONObject params = new JSONObject();
        try {
            params.put("apiKey", API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onResponseMessage(SUCCESS, response.toString());
                        Log.i("Parsing canteens", "onResponse");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onResponseMessage(ERROR, error.getMessage());
                    }
                });
        mRequestQueue.add(jsObjRequest);
    }*/

    public void doJSONRequest(String url, final AbstractCallback callback) {
        JSONObject params = new JSONObject();
        try {
            params.put("apiKey", API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onResponseMessage(SUCCESS, response.toString());
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onResponseMessage(ERROR, error.getMessage());

                    }
                });
        mRequestQueue.add(request);
    }

    public void doImageRequest(String url, final LoadImageCallback callback) {
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                callback.onResponseMessage(SUCCESS, "", response);
            }
        }, 0, 0, null, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onResponseMessage(ERROR,"",null);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(2000, 0, 1.0f));
        mRequestQueue.add(request);
    }

    public static boolean isConnectedToInternet(Context context) {
        boolean connected;
        try {
            ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            connected = (   conMgr.getActiveNetworkInfo() != null &&
                    conMgr.getActiveNetworkInfo().isAvailable() &&
                    conMgr.getActiveNetworkInfo().isConnected()   );
        } catch (Exception e) {
            return false;
        }

        return connected;
    }


    public void fetchMealImage(String url, LoadImageCallback callback) {
        if (isConnectedToInternet(mCtx))
            doImageRequest(url, callback);
        else
            callback.onResponseMessage(NO_INTERNET,"",null);
    }

    public void fetchCanteens(LoadCanteensCallback callback) {
        if (isConnectedToInternet(mCtx))
            doJSONRequest(URL_BASE+URL_GET_CANTEENS, callback);
        else {
            Toast.makeText(mCtx, mCtx.getString(R.string.toast_loading_canteens_fail), Toast.LENGTH_LONG).show();
            callback.onResponseMessage(NO_INTERNET,"");
        }
    }

    public void fetchNews(LoadNewsCallback callback) {
        if (isConnectedToInternet(mCtx))
            doJSONRequest(URL_BASE+URL_GET_NEWS, callback);
        else {
            Toast.makeText(mCtx, mCtx.getString(R.string.toast_loading_news_fail), Toast.LENGTH_LONG).show();
            callback.onResponseMessage(NO_INTERNET,"");
        }
    }

    public void fetchMeals(String canteenCode, AbstractCallback callback) {
        if (isConnectedToInternet(mCtx))
            doJSONRequest(URL_BASE+URL_GET_MEALS+canteenCode, callback);
        else {
            Toast.makeText(mCtx, mCtx.getString(R.string.toast_loading_meals_fail), Toast.LENGTH_LONG).show();
            callback.onResponseMessage(NO_INTERNET, "");
        }
    }
}
