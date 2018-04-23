package com.example.lenovo.gardenclub;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.lenovo.gardenclub.MainActivity.JSON_STRING;


public class ContactList extends AppCompatActivity {
    private static final String TAG = "ContactList";
    String json_string, loginEmail;
    JSONObject mJSONObject;
    JSONArray mJSONArray;
    ContactAdapter mContactAdapter;
    Intent intent;
    public static AppCompatActivity fa;

//    SearchView mSearchView = (SearchView) findViewById(R.id.searchView);

    ListView lst;
//    String[] names = {"A", "B","C","D","E","F"};
//    Integer[] imgid= {R.drawable.image,R.drawable.img,R.drawable.image,R.drawable.image,R.drawable.img,R.drawable.image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fa = this;
        setContentView(R.layout.activity_contact_list);
        intent = new Intent(this, Contact.class);


        JSONObject JO;
        mContactAdapter = new ContactAdapter(this, R.layout.row_layout_1);
        lst = findViewById(R.id.ListView);

        lst.setAdapter(mContactAdapter);
        lst.setTextFilterEnabled(true);

        json_string = getIntent().getExtras().getString("json_data");
        loginEmail = getIntent().getExtras().getString("login_email").trim();
        intent.putExtra("json_data", json_string);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try {

            mJSONObject = new JSONObject(json_string);
            mJSONArray = mJSONObject.getJSONArray("server_response");
            int count = 0;
            String name, email, mobile, mbrStatus, userID;

            while(count <= mJSONArray.length()) {
                JO = mJSONArray.getJSONObject(count);
                name = JO.getString("FirstName").concat(" " + JO.getString("LastName"));
                email = JO.getString("EmailAddress");
                mobile = JO.getString("PrimNum");
                mbrStatus = JO.getString("Status");
                userID = JO.getString("ID");

                Contacts contact = new Contacts(name, email, mobile, mbrStatus, userID, loginEmail);
                mContactAdapter.add(contact);

                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        // Get the intent, verify the action and get the query
//        Intent searchIntent = getIntent();
//        if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
//            String query = searchIntent.getStringExtra(SearchManager.QUERY);
//            doMySearch(query);
//        }
        lst.setOnItemClickListener(mContactAdapter.mListener);
//        lst.setOnItemClickListener(mOnItemClickListener);
    }


    //    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            try {
////                    Toast.makeText(getApplicationContext(), String.valueOf(mJSONArray.getJSONObject(i)), Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onItemClick: mJSONArray.getJSONObject(i): " + mJSONArray.getJSONObject(i).toString());
//                String json = mJSONArray.getJSONObject(i).toString();
//                intent.putExtra("json_object", json);
//                intent.putExtra("login_email", loginEmail);
//                /**
//                 * if (String.valueOf(mJSONArray.getJSONObject(i)).equals(OTHER_USER_ID_LMAO) {
//                 *      startActivity(otherActivity)
//                 *      }
//                 */
////                startActivity(intent);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_badge_ID);
        SearchView searchView = (SearchView) menuItem.getActionView();
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
                mContactAdapter.getFilter().filter(s);

                lst.setOnItemClickListener(mContactAdapter.mListener);
                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }




}


