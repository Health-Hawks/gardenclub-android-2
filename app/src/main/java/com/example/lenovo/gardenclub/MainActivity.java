package com.example.lenovo.gardenclub;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//from github/gardenclub-android>git add .
//...github/gardenclub-android>git commit -m "add existing files
//...github/gardenclub-android>git push origin master

/**
 * User photo images <-----
 *
 * Primary contact number edit, then update that for the call and text button
 *
 * update database with edited information:
 * --> this may be done with a php file
 *
 */


public class MainActivity extends AppCompatActivity {
    EditText UsernameEt, PasswordEt;
    Button submit;
    static String JSON_STRING;
    static String json_string;
    private static final String TAG = "MainActivity";
    WebView mWebView;
    String password, username;
    int jsonParsed = 0;
    Intent intent;
    int i = 0;
    StringBuilder str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webview);
        intent = new Intent(this, ContactList.class);
//        getSupportActionBar().hide();
        submit = findViewById(R.id.button);
        UsernameEt = findViewById(R.id.et_login);
        PasswordEt = findViewById(R.id.et_pass);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = UsernameEt.getText().toString();
                password = PasswordEt.getText().toString();
//                mWebView.setVisibility(View.VISIBLE);
                i++;
                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                final WebAppInterface webAppInterface = new WebAppInterface(MainActivity.this);
                mWebView.addJavascriptInterface(webAppInterface, "Android");

                Log.d(TAG, "onClick: starts");

                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.d(TAG, "shouldOverrideUrlLoading: called");
                        view.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.d(TAG, "onPageFinished: called");
                        Log.d(TAG, "onPageFinished: view: " + view);
                        Log.d(TAG, "onPageFinished: view.getUrl(): " + view.getUrl());

                        if (view.getUrl().equals("http://www.capefeargardenclub.org/wp-login.php?redirect_to=%2f")) {
                            Log.d(TAG, "onPageFinished: url equals haha");
                            StringBuilder sb = new StringBuilder();
//                sb.append("window.onload = function(){");
                            sb.append("var objPWD = '" + password + "';objAccount  = '" + username + "';var str = '';");
                            sb.append("var inputs = document.getElementsByTagName('input');");
                            sb.append("for (var i = 0; i < inputs.length; i++) {");
                            sb.append("if (inputs[i].name.toLowerCase() === 'pwd') {inputs[i].value = '" + password.toString() + "';}");
                            sb.append("else if (inputs[i].name.toLowerCase() === 'log') {inputs[i].value = '" + username + "';}");
                            sb.append("}");
                            sb.append("if (objAccount != null) {str += objAccount.value;}");
                            sb.append("console.log('a');");
                            sb.append("if (objPWD != null) { str += ' , ' + objPWD.value;}");
                            sb.append("console.log('b');");
                            sb.append("console.log('c');");
//                    sb.append("window.Android.processHTML(str);");
                            sb.append("console.log('d');");
                            sb.append("document.getElementById('loginform').submit();");
//                sb.append("return true;");
//                sb.append("};");
                            sb.append("console.log('e');");
//                sb.append("login();");
                            sb.append("console.log('f');");
                            Log.d(TAG, "onPageFinished: sb: " + sb);
                            view.loadUrl("javascript:" + sb.toString());
                            i++;
                        }

                        Log.d(TAG, "onPageFinished: view.getUrl(): " + view.getUrl());

                        String viewURL = view.getUrl();
                        Log.d(TAG, "onPageFinished: viewURL: " + viewURL);
                        Log.d(TAG, "onPageFinished: url: " + url);
                        if (viewURL != null && viewURL.equals("http://www.capefeargardenclub.org/")) {
                            Log.d(TAG, "onPageFinished: Success: view.getUrl() = " + view.getUrl());
                            mWebView.setVisibility(View.GONE);
                            String method = "some_json";
//                                PostData(username, password, method);
                            try {
                                parseJson(view);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (json_string != null) {
                                Log.d(TAG, "onPageFinished: json_string != null");
                                intent.putExtra("json_data", json_string);
                                Log.d(TAG, "onPageFinished: json_String =  " + json_string);
                                intent.putExtra("login_email", username);
                                intent.putExtra("password", password);
                                startActivity(intent);
                                finish();

                            } else {
                                Log.d(TAG, "onPageFinished: json_string == null / jsonParsed = " + jsonParsed);
                                try {
                                    parseJson(view);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else  {
                            Log.d(TAG, "onPageFinished: view.getUrl() = " + view.getUrl());
                            Toast.makeText(getApplicationContext(), "Email or password is incorrect. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mWebView.loadUrl("http://www.capefeargardenclub.org/wp-login.php?redirect_to=%2f");
                    }
                });

                try {
                    parseJson(v);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void getJSON(View view) throws InterruptedException {
        new BackgroundTask().execute();
        parseJson(view);

    }

    public void parseJson(View view) throws InterruptedException {
        new BackgroundTask().execute();
        if (json_string == null) {
            Log.d(TAG, "parseJson: json_string == null");
            Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();

        } else {
            Log.d(TAG, "parseJson: json_string != null");
            jsonParsed = 1;
            intent.putExtra("json_data", json_string);
            Log.d(TAG, "parseJson: json_data: " + json_string);
        }
    }



    class BackgroundTask extends AsyncTask<Void,Void,String> {
        String json_url;
        private static final String TAG = "BackgroundTask";

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(json_url);
            str = new StringBuilder();
            String url = "http://capefeargardenclub.org/cfgcTestingJSON/login.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("method", "some_json"));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                //problem starts here
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) { // Status OK
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((JSON_STRING = bufferedReader.readLine()) != null) {
                        stringBuilder.append(JSON_STRING+"\n");
                    }

                    bufferedReader.close();
                    inputStream.close();
                    return stringBuilder.toString().trim();
                } else {
                    Log.e("Log", "Failed to download result..");
                }
                Log.d(TAG, "run: str: " + str);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            json_url = "http://capefeargardenclub.org/cfgcTestingJSON/login.php";

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            json_string = result;
            Log.d(TAG, "onPostExecute: json_string: " + json_string);

        }
    }

    public boolean PostData(String email, String password, String method) throws InterruptedException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //FIX THIS HERE
        String url = "http://capefeargardenclub.org/cfgcTestingJSON/login.php";
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("method", method));

        String resultServer  = getHttpPost(url,params);
        Log.d(TAG, "resultServer - updateData: " + resultServer);

        /*** Default Value ***/
        String strStatusID = "0";
        String strMessage = "Unknown Status!";

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            Log.d(TAG, "PostData: connected to resultServer");
            strMessage = c.getString("Message");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "PostData: not connected");
            e.printStackTrace();
        }

        // Prepare Save Data
        if(strStatusID.equals("0")) {

            Toast.makeText(MainActivity.this, "Update not successful", Toast.LENGTH_SHORT).show();

            return false;
        } else {
            Toast.makeText(MainActivity.this, "Update Data Successfully", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public String getHttpPost(String strUrl, final List<NameValuePair> params) throws InterruptedException {
        final String url = strUrl;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                str = new StringBuilder();
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    HttpResponse response = client.execute(httpPost);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) { // Status OK
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            str.append(line);
                        }
                    } else {
                        Log.e("Log", "Failed to download result..");
                    }
                    Log.d(TAG, "run: str: " + str);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
        Log.d(TAG, "getHttpPost: str: " + str);
        return str.toString();
    }

}
