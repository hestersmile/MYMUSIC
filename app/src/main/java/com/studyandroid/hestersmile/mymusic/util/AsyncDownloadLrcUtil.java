package com.studyandroid.hestersmile.mymusic.util;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.studyandroid.hestersmile.mymusic.MusicApplication;
import com.studyandroid.hestersmile.mymusic.javabean.lrconline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hestersmile on 2016/6/4.
 */
public class AsyncDownloadLrcUtil extends AsyncTask<Integer, Integer, List<lrconline>> {
    private String sname;
    private String sauthor;
    private String queryURL = "http://apis.baidu.com/geekery/music/query";
    private String lrcURL = "http://apis.baidu.com/geekery/music/krc";
    private String APIkey = "bff74d0a67456e8264411418f451c225";
    private lrconline mylrc;
    private Handler handler;
    public AsyncDownloadLrcUtil(lrconline mylrc, Handler mhandler) {
        this.mylrc = mylrc;
        this.handler = mhandler;
        Log.v("hahalrc", "222222");
    }

    @Override
    protected List<lrconline> doInBackground(Integer... params) {
        Log.v("hahalrc", "333333");
        String key = mylrc.getFilename();
        String utf8 = getUTF8XMLString(key);
        String hash = mylrc.getHash();
        int duration = mylrc.getDuration();
        Log.v("hahalrc",key+"---"+hash+"---"+duration);
        Log.v("hahalrc",utf8+"---"+hash+"---"+duration);
        String httpArg = "name=" + utf8 + "&hash=" + hash + "&time=" + duration;
        lrcURL = lrcURL + "?" + httpArg;
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(
                Request.Method.GET, lrcURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String lrccontent = null;
                        try {

                            String status = response.getString("status");
                            Log.v("hahalrc", "----------------------" + response);
                            if (status.equals("success")) {
                                Log.v("hahalrc", "----sssssss-----");
                                JSONObject mydatalist = response.getJSONObject("data");
                                 lrccontent= mydatalist.getString("content");
                                Log.v("hahalrc","++++++++++++++++++++++++++++++:"+lrccontent);

                            } else {
                                lrccontent="抱歉，并未找到相关歌曲";
                            }
                            Log.v("hahalrc", "----a-----");
                            Bundle mbundle = new Bundle();
                            Log.v("hahalrc", "----b-----");
                            mbundle.putString("lrc", lrccontent);
                            Log.v("hahalrc", "----c-----");
                            Message msg = new Message();
                            Log.v("hahalrc", "----d-----");
                            msg.obj = mbundle;
                            msg.what = 0x333;
                            Log.v("hahalrc", "qqqqqqqqqqq");
                            handler.sendMessage(msg);

                        } catch (Exception e) {
                            Log.v("hahalrc", "--------ddd-----------");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {

                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                }
                handler.sendEmptyMessage(0x222);
                Log.v("hahalrc", "onErrorResponse, error=" + error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("apikey", "bff74d0a67456e8264411418f451c225");

                // MyLog.d(TAG, "headers=" + headers);
                return headers;
            }
        };
        // 设置取消取消http请求标签 Activity的生命周期中的onStiop()中调用
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag("volleyget");
        MusicApplication.getHttpQueue().add(jsonObjRequest);
        MusicApplication.getHttpQueue().start();


        return null;
    }

    /**
     * 这里的Intege参数对应AsyncTask中的第二个参数
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
     */
    @Override
    protected void onProgressUpdate(Integer... values) {

    }


    /**
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
     */
    @Override

    protected void onPostExecute(List<lrconline> lrconlines) {
//        Log.v("hahalrc", "lrconline=" + lrconlines.size());
    }

    public String getUTF8XMLString(String xml) {
        // A StringBuffer Object
        StringBuffer sb = new StringBuffer();
        sb.append(xml);
        String xmString = "";
        String xmlUTF8 = "";
        try {
            xmString = new String(sb.toString());
            xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // return to String Formed
        return xmlUTF8;
    }

}
