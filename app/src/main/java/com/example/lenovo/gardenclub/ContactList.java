package com.example.lenovo.gardenclub;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class ContactList extends AppCompatActivity {
    private List<Contacts> contacts = new ArrayList<>();
    private static final String TAG = "ContactList";
    String json_string, loginEmail;
    JSONObject mJSONObject;
    JSONArray mJSONArray;
    ContactAdapter mContactAdapter;
    ContactAdapter nContactAdapter;
    Intent intent;
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
        lst = findViewById(R.id.ListView);

//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recView);
//        recyclerView.setAdapter(mContactAdapter);

//        lst.setAdapter(new ContactAdapter(this, contacts));
//        lst.setAdapter(mContactAdapter);
//        lst.setTextFilterEnabled(true);

        json_string = getIntent().getExtras().getString("json_data");
        loginEmail = getIntent().getExtras().getString("login_email").trim();
        intent.putExtra("json_data", json_string);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try {

            mJSONObject = new JSONObject(json_string);
            mJSONArray = mJSONObject.getJSONArray("server_response");
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




}


