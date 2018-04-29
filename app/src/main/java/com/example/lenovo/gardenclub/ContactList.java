package com.example.lenovo.gardenclub;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.request.FutureTarget;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class ContactList extends AppCompatActivity {
    private List<Contacts> contacts = new ArrayList<>();
    private static final String TAG = "ContactList";
    String json_string, loginEmail;
    JSONObject mJSONObject;
    JSONArray mJSONArray;
    ContactAdapter mContactAdapter;
    ContactAdapter nContactAdapter;
    Intent intent;
    Map<String, Bitmap> mBitmapMap;
    public static AppCompatActivity fa;

//    SearchView mSearchView = (SearchView) findViewById(R.id.searchView);

    RecyclerView lst;
//    String[] names = {"A", "B","C","D","E","F"};
//    Integer[] imgid= {R.drawable.image,R.drawable.img,R.drawable.image,R.drawable.image,R.drawable.img,R.drawable.image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts contactlist");
        fa = this;
        setContentView(R.layout.activity_contact_list);
        intent = new Intent(this, Contact.class);



        JSONObject JO;
//        mContactAdapter = new ContactAdapter(this, R.layout.row_layout_1);
        lst = (RecyclerView) findViewById(R.id.ListView);


//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recView);
//        recyclerView.setAdapter(mContactAdapter);

//        lst.setAdapter(new ContactAdapter(this, contacts));
//        lst.setAdapter(mContactAdapter);
//        lst.setTextFilterEnabled(true);

        json_string = getIntent().getExtras().getString("json_data");
        loginEmail = getIntent().getExtras().getString("login_email").trim();
        intent.putExtra("json_data", json_string);
        Log.d(TAG, "onCreate: lol");

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try {

            mJSONObject = new JSONObject(json_string);
            mJSONArray = mJSONObject.getJSONArray("server_response");
            Log.d(TAG, "onCreate: jsonArray: " + mJSONArray);
//            Map<String, Bitmap> mBitmapMap = new BitMapArrayGod().execute(mJSONArray).get();
//            Log.d(TAG, "onCreate: mBitmapMap: " + mBitmapMap);
            int i = 0;
            String name, email, mobile, mbrStatus, userID, photoID;

            Log.d(TAG, "onCreate: while loop starts");
            Log.d(TAG, "onCreate: mJSONArray: " + mJSONArray);
            Log.d(TAG, "onCreate: mJSONObject: " + mJSONObject);

            while(i < mJSONArray.length()) {
                Log.d(TAG, "onCreate: i: " + i);
                JO = mJSONArray.getJSONObject(i);
                name = JO.getString("FirstName").concat(" " + JO.getString("LastName"));
                email = JO.getString("EmailAddress");
                mobile = JO.getString("PrimNum");
                mbrStatus = JO.getString("Status");
                userID = JO.getString("ID");
                photoID = JO.getString("PhotoID");
                Log.d(TAG, "onCreate: photoID: " + photoID);

                Contacts contact = new Contacts(name, email, mobile, mbrStatus, userID, loginEmail, photoID);
//                mContactAdapter.add(contact);
                contacts.add(contact);
                i++;

            }
            Log.d(TAG, "onCreate: contacts: " + contacts);
            Log.d(TAG, "onCreate: while loop finishes");
            lst.setLayoutManager(new LinearLayoutManager(this));
            nContactAdapter = new ContactAdapter(this, contacts);
            lst.setAdapter(nContactAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
//        } catch (InterruptedException e) {
            e.printStackTrace();
//        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        lst.setOnItemClickListener(mContactAdapter.mListener);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context
                .SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.search_badge_ID);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

//        additional features
        searchView.setActivated(true);
        searchView.setQueryHint("Search for a member");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                nContactAdapter.getFilter().filter(s);

//                lst.setOnItemClickListener(mContactAdapter.mListener);
                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    public class BitMapArrayGod extends AsyncTask<JSONArray, Void, Map<String, Bitmap>> {
        @Override
        protected Map<String, Bitmap> doInBackground(JSONArray... jsonArrays) {
            Log.d(TAG, "ImageGetter doInBackground: starts");
            Map<String, Bitmap> userPhotoArray = new HashMap<String, Bitmap>();
            final List<NameValuePair> params = new ArrayList<NameValuePair>();
            final String url = "http://capefeargardenclub.org/cfgcTestingJSON/getImage1.php";
            JSONArray currentJA;
            JSONObject currentJO;
            InputStream inStream = null;

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
//            for (int i = 0; i < jsonArrays[0].length(); i++) {
//                Log.d(TAG, "doInBackground: i: " + i);
//                try {
//                    httpPost.setEntity(new UrlEncodedFormEntity(params));
//                    HttpResponse response = client.execute(httpPost);
//                    StatusLine statusLine = response.getStatusLine();//////
//                    int statusCode = statusLine.getStatusCode();
//                    Bitmap bmp = null;
//                    if (statusCode == 200) { // Status OK
//                        try {
//                            inStream = null;
//                            BufferedHttpEntity bufHttpEntity = null;
//
//                            currentJO = jsonArrays[0].getJSONObject(i);
//                            String currentPIDJO = currentJO.getString("PhotoID");
//                            String currentUIDJO = currentJO.getString("ID");
//                            params.add(new BasicNameValuePair("photoID", currentPIDJO));
//                            HttpEntity entity = response.getEntity();
//                            bufHttpEntity = new BufferedHttpEntity(entity);
//                            inStream = bufHttpEntity.getContent();
//                            Log.d(TAG, "doInBackground: bmp: " + bmp);
//                            Log.d(TAG, "doInBackground: currentUIDJO: " + currentUIDJO);
//                            Log.d(TAG, "doInBackground: currentPIDJO: " + currentPIDJO);
//                            BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inJustDecodeBounds = true;
////                                int imageHeight = options.outHeight;
////                                int imageWidth = options.outWidth;
////                            bmp =  Glide.with(getApplicationContext())
////                                    .asBitmap()
////                                    .load(inStream)
////                                    .into(75,75)
////                                    .get();
////                            bmp = futureTarget.get();
////                                options.inSampleSize = 2;
////                                Log.d(TAG, "doInBackground: imageHeight: " + imageHeight);
////                                Log.d(TAG, "doInBackground: imageWidth: " + imageWidth);
////                                bmp = BitmapFactory.decodeStream(inStream, null, options);
//                            userPhotoArray.put(currentPIDJO, bmp);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
////                            inStream.close();
//
//                    } else {
//                        Log.e("Log", "Failed to download result..");
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (ClientProtocolException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
            return userPhotoArray;
        }

        @Override
        protected void onPostExecute(Map<String, Bitmap> stringBitmapMap) {
//            Log.d(TAG, "onPostExecute: starts image");
//            Log.d(TAG, "onPostExecute: image json_string: " + json_string);
//            Log.d(TAG, "onPostExecute: image username: " + username);
//            Log.d(TAG, "onPostExecute: image password: " + password);
        }
    }




}


