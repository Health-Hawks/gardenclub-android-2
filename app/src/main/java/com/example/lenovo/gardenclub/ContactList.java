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
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
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
    RecyclerView lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts contactlist");
        fa = this;
        setContentView(R.layout.activity_contact_list);
        intent = new Intent(this, Contact.class);
        Bitmap bitmap = null;

        JSONObject JO;
        lst = (RecyclerView) findViewById(R.id.ListView);

        json_string = getIntent().getExtras().getString("json_data");
        loginEmail = getIntent().getExtras().getString("login_email").trim();
        intent.putExtra("json_data", json_string);
        Log.d(TAG, "onCreate: lol");

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try {

            mJSONObject = new JSONObject(json_string);
            mJSONArray = mJSONObject.getJSONArray("server_response");
            Log.d(TAG, "onCreate: jsonArray: " + mJSONArray);
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

//                if (photoID != null && photoID != "null") {
//                    bitmap = new BitMapArrayGod().execute(photoID).get();
//                    mBitmapMap.put(photoID, bitmap);
//                }

                Contacts contact = new Contacts(name, email, mobile, mbrStatus, userID, loginEmail, photoID);
//                mContactAdapter.add(contact);
                contacts.add(contact);
                i++;

            }
            Log.d(TAG, "onCreate: contacts: " + contacts);
            Log.d(TAG, "onCreate: while loop finishes");
            Log.d(TAG, "onCreate: mbitmapMap: " + mBitmapMap);
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

    public class BitMapArrayGod extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Log.d(TAG, "ImageGetter doInBackground: starts");
            Map<String, Bitmap> userPhotoArray = new HashMap<String, Bitmap>();
            String photoID = strings[0];
            Bitmap currentBmp = null;
            final List<NameValuePair> bitmapArray = new ArrayList<NameValuePair>();
            Log.d(TAG, "doInBackground: photoID: " + photoID);
            final String url = "http://capefeargardenclub.org/cfgcTestingJSON/images_Testing/images/" + photoID + ".jpg";
            try {
                currentBmp = Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(url)
                        .submit().get();
                Log.d(TAG, "doInBackground: all good :-)");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


//            JSONArray currentJA;
//            JSONObject currentJO;
//            InputStream inStream = null;
//            HttpClient client = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost(url);
//            try {
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//                HttpResponse response = client.execute(httpPost);
//                StatusLine statusLine = response.getStatusLine();//////
//                int statusCode = statusLine.getStatusCode();
//                Bitmap bmp = null;
//                if (statusCode == 200) { // Status OK
//                    try {
//                        inStream = null;
//                        BufferedHttpEntity bufHttpEntity = null;
//
//                        currentJO = jsonArrays[0].getJSONObject(i);
//                        String currentPIDJO = currentJO.getString("PhotoID");
//                        String currentUIDJO = currentJO.getString("ID");
//                        params.add(new BasicNameValuePair("photoID", currentPIDJO));
//                        HttpEntity entity = response.getEntity();
//                        bufHttpEntity = new BufferedHttpEntity(entity);
//                        inStream = bufHttpEntity.getContent();
//                        Log.d(TAG, "doInBackground: bmp: " + bmp);
//                        Log.d(TAG, "doInBackground: currentUIDJO: " + currentUIDJO);
//                        Log.d(TAG, "doInBackground: currentPIDJO: " + currentPIDJO);
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inJustDecodeBounds = true;
//                                int imageHeight = options.outHeight;
//                                int imageWidth = options.outWidth;
//                            bmp =  Glide.with(getApplicationContext())
//                                    .asBitmap()
//                                    .load(inStream)
//                                    .into(75,75)
//                                    .get();
//                            bmp = futureTarget.get();
//                                options.inSampleSize = 2;
//                                Log.d(TAG, "doInBackground: imageHeight: " + imageHeight);
//                                Log.d(TAG, "doInBackground: imageWidth: " + imageWidth);
//                                bmp = BitmapFactory.decodeStream(inStream, null, options);
//                        userPhotoArray.put(currentPIDJO, bmp);

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
////                            inStream.close();
//
//                } else {
//                    Log.e("Log", "Failed to download result..");
//                }
            return currentBmp;
        }
    }




}


