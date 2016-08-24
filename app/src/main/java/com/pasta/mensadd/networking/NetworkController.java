package com.pasta.mensadd.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.widget.Toast;

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

import org.json.JSONArray;


public class NetworkController {

    private static NetworkController mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public static int SUCCESS = 1;
    public static int ERROR = 0;
    public static int NO_INTERNET = -1;

    public static final String URL_PREFIX = "http://ctni.sabic.uberspace.de/mensadd";
    public static final String URL_GET_CANTEENS = "/canteens.json";
    public static final String URL_GET_MEALS = "/meals/";

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

    public void doJSONArrayRequest(String url, final AbstractCallback callback) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, new JSONArray(), new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onResponseMessage(SUCCESS, response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onResponseMessage(ERROR, error.getMessage());
                    }
                });
        mRequestQueue.add(jsObjRequest);
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


    public void loadMealImage(String url, LoadImageCallback callback) {
        if (isConnectedToInternet(mCtx))
            doImageRequest(url, callback);
        else
            callback.onResponseMessage(NO_INTERNET,"",null);
    }

    public void getCanteenList(AbstractCallback callback) {
        if (isConnectedToInternet(mCtx))
            doJSONArrayRequest(URL_PREFIX+URL_GET_CANTEENS, callback);
        else {
            Toast.makeText(mCtx, "Mensen konnten nicht aktualisiert werden. Keine Internetverbindung vorhanden.", Toast.LENGTH_LONG).show();
            callback.onResponseMessage(NO_INTERNET,"");
        }
    }

    public void getMealsForCanteen(String canteenCode, AbstractCallback callback) {
        if (isConnectedToInternet(mCtx))
            doJSONArrayRequest(URL_PREFIX+URL_GET_MEALS+canteenCode+".json", callback);
        else {
            Toast.makeText(mCtx, "Speiseplan konnte nicht aktualisiert werden. Keine Internetverbindung vorhanden.", Toast.LENGTH_LONG).show();
            callback.onResponseMessage(NO_INTERNET, "");
        }
    }
}
