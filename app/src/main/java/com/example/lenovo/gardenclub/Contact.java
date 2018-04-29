package com.example.lenovo.gardenclub;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Contact extends AppCompatActivity {
    int viewId_placeholder = 0;
    Button btnEmail, btnCall, btnText;
    TextView emailID;
    static String json_string;
    JSONObject jsonObject;
    String json, JSON_STRING, type, userID;
    private static final String TAG = "Contact";
    String finalEmail = null;
    JSONObject JO;
    String SBString, loginEmail;
    StringBuilder str;
    String udFN, udLN, udSpouse, udAddress, udCAS, udZip, udPrim, udSec, password, bio;
    TextView tvMoreInfo, tvBack;
    Intent backIntent;


    public Contact() throws JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Contact.class);
        intent.getExtras();
        setContentView(R.layout.activity_contact);
        userID = getIntent().getExtras().getString("user_id");
        loginEmail = getIntent().getExtras().getString("login_email");
        password = getIntent().getExtras().getString("password");
        String json_data = getIntent().getExtras().getString("json_data");
        backIntent = new Intent(this, ContactList.class);
        backIntent.putExtra("json_data", json_data);
        getSupportActionBar().hide();
        tvMoreInfo = findViewById(R.id.tv_more_info);
        tvBack = findViewById(R.id.tv_back);


        new BackgroundTask1().execute("get_info", userID);
    }

    public void getJSON(View view) {
        new BackgroundTask1().execute();
        parseJson(view);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backIntent.putExtra("login_email", loginEmail);
            startActivity(backIntent);

    }



    class BackgroundTask1 extends AsyncTask<String, Void, String> {
        String json_post, json_get, json_url;
        private static final String TAG = "BackgroundTask";


        @Override
        protected String doInBackground(String... params) {
            type = params[0];
            if (type.equals("get_info")) {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(json_url);
                str = new StringBuilder();
                String url = "http://capefeargardenclub.org/cfgcTestingJSON/login.php";
                List<NameValuePair> postParams = new ArrayList<NameValuePair>();

                postParams.add(new BasicNameValuePair("email", finalEmail));
                postParams.add(new BasicNameValuePair("password", password));
                postParams.add(new BasicNameValuePair("method", "all_json"));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(postParams));
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


                    JSONObject jsonObject = new JSONObject(String.valueOf(stringBuilder));
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");

                    Log.d(TAG, "Contact.java doInBackground: stringBuilder: " + stringBuilder);
                    Log.d(TAG, "Contact.java doInBackground: jsonObject: " + String.valueOf(jsonObject));
                    backIntent.putExtra("json_data", String.valueOf(jsonObject));
                    Log.d(TAG, "Contact.java doInBackground: jsonArray: " + jsonArray);


                    String email;
                    String mbrStatus;
                    String photoID;
                    String yearTurnedActive;
                    String firstName;
                    String lastName;
                    String spouse;
                    String StreetAddress;
                    String CityState, WorkNum, Officers;
                    String ZipCode, PrimaryContactNumber, SecondaryContactNumber;
                    String BiographicalInfo;

                    final TextView nameTV = (TextView) findViewById(R.id.nameTV);
                    final TextView mbrStatusTV = (TextView) findViewById(R.id.mbrStatusTV);
                    final TextView spouseTV = (TextView) findViewById(R.id.spouseTV);
                    final TextView addressTV = (TextView) findViewById(R.id.addressTV);
                    final TextView primaryContactTV = (TextView) findViewById(R.id.primaryContactTV);
                    final TextView secondaryContactTV = (TextView) findViewById(R.id.secondaryContactTV);
                    final TextView emailTV = (TextView) findViewById(R.id.emailTV);
                    final ImageView imageView = findViewById(R.id.imageView2);
                    int isUser = 0;

                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Log.d(TAG, "doInBackground: jsonArray ->" + jsonArray.getJSONObject(i));

                        if (jsonArray.getJSONObject(i).getString("ID").equals(userID)) {
                            email = jsonArray.getJSONObject(i).getString("EmailAddress");
                            mbrStatus = jsonArray.getJSONObject(i).getString("Status");
                            userID = jsonArray.getJSONObject(i).getString("ID");
                            photoID = jsonArray.getJSONObject(i).getString("PhotoID");
                            yearTurnedActive = jsonArray.getJSONObject(i).getString("YearTA");
                            firstName = jsonArray.getJSONObject(i).getString("FirstName");
                            lastName = jsonArray.getJSONObject(i).getString("LastName");
                            spouse = jsonArray.getJSONObject(i).getString("Spouse");
                            StreetAddress = jsonArray.getJSONObject(i).getString("StreetAddress");
                            CityState = jsonArray.getJSONObject(i).getString("CityAndState");
                            ZipCode = jsonArray.getJSONObject(i).getString("ZipCode");
                            PrimaryContactNumber = jsonArray.getJSONObject(i).getString("PrimNum");
                            SecondaryContactNumber = jsonArray.getJSONObject(i).getString("SecNum");
                            BiographicalInfo = jsonArray.getJSONObject(i).getString("bioInfo");

                            final String finalFirstName = firstName;
                            final String finalLastName = lastName;
                            final String finalMbrStatus = mbrStatus;
                            final String finalSpouse = spouse;
                            final String finalStreetAddress = StreetAddress;
                            final String finalCityState = CityState;
                            final String finalZipCode = ZipCode;
                            final String finalPrimaryContactNumber = PrimaryContactNumber;
                            final String finalSecondaryContactNumber = SecondaryContactNumber;
                            final String finalEmail = email;
                            final String finalBiographicalInfo = BiographicalInfo;

                            final String finalYearTurnedActive = yearTurnedActive;

                            btnEmail = findViewById(R.id.btn_email);
                            btnCall = findViewById(R.id.btn_call);
                            btnText = findViewById(R.id.btn_text);

                            final String finalPhotoID = photoID;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    nameTV.setText(finalFirstName.concat(" " + finalLastName));
                                    mbrStatusTV.setText(finalMbrStatus);
                                    spouseTV.setText(finalSpouse);
                                    addressTV.setText(finalStreetAddress.concat(",\n" + finalCityState + ", " + " " + finalZipCode));
                                    primaryContactTV.setText(finalPrimaryContactNumber);
                                    secondaryContactTV.setText(finalSecondaryContactNumber);
                                    emailTV.setText(finalEmail);
                                    int p_id = 0;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                    if (finalPhotoID != null && finalPhotoID != "null") {
                                        //                                            Bitmap image = GetImage(userID, finalEmail, finalPhotoID);
//                                            imageView.setImageBitmap(image);
                                        Glide.with(getApplicationContext())
                                                .load("http://capefeargardenclub.org/cfgcTestingJSON/images_Testing/images/" + finalPhotoID + ".jpg")
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(imageView);

                                    } else {
                                        Glide.with(getApplicationContext())
                                                .load(R.drawable.carolinayellowjessaminemed1)
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(imageView);
                                    }

                                    if (finalEmail.equals(loginEmail)) {
                                        if (nameTV == null || nameTV.getText() == "null") {
                                            nameTV.setText("No name is set, tap here to set one.");
                                        }
                                        if (spouseTV == null || spouseTV.getText() == "null") {
                                            spouseTV.setText("No spouse name is set, tap here to set one.");
                                        }
                                        if (addressTV == null || addressTV.getText() == "null") {
                                            addressTV.setText("No address is set, tap here to set one.");
                                        }
                                        if (primaryContactTV == null || primaryContactTV.getText() == "null") {
                                            primaryContactTV.setText("No primary contact is set, tap here to set one.");
                                        }
                                        if (secondaryContactTV == null || secondaryContactTV.getText() == "null") {
                                            secondaryContactTV.setText("No secondary contact is set, tap here to set one.");
                                        }

                                        final TextView tvEdit = findViewById(R.id.tv_edit);
                                        tvEdit.setText("Edit");

                                        final int nameId = nameTV.getId();
                                        final int spouseId = spouseTV.getId();
                                        final int addressId = addressTV.getId();
                                        final int primId = primaryContactTV.getId();
                                        final int secId = secondaryContactTV.getId();
                                        final int emailId = emailTV.getId();

                                        AlertDialog alertDialog = null;
                                        final AlertDialog.Builder builder;
                                        builder = new AlertDialog.Builder(new ContextThemeWrapper(Contact.this, R.style.myDialog));
                                        View viewAD = getLayoutInflater().inflate(R.layout.dialog_general, null);

                                        final TextView adTitle = viewAD.findViewById(R.id.genTitle);
                                        final EditText etGen = viewAD.findViewById(R.id.etGen);
                                        builder.setView(viewAD)
                                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (viewId_placeholder == spouseId) {
                                                            spouseTV.setText(etGen.getText());
                                                            udSpouse = etGen.getText().toString();
                                                            if (udFN == null) {
                                                                udFN = finalFirstName;
                                                            }
                                                            if (udLN == null) {
                                                                udLN = finalLastName;
                                                            }
                                                            if (udSpouse == null) {
                                                                udSpouse = finalSpouse;
                                                            }
                                                            if (udCAS == null) {
                                                                udCAS = finalCityState;
                                                            }
                                                            if (udPrim == null) {
                                                                udPrim = finalPrimaryContactNumber;
                                                            }
                                                            if (udSec == null) {
                                                                udSec = finalSecondaryContactNumber;
                                                            }
                                                            if (udZip == null) {
                                                                udZip = finalZipCode;
                                                            }
                                                            try {
                                                                SaveData(userID, loginEmail, udFN, udLN, udSpouse, udAddress, udCAS, udZip, udPrim, udSec);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                        if (viewId_placeholder == primId) {
                                                            primaryContactTV.setText(etGen.getText());
                                                            udPrim = etGen.getText().toString();
                                                            if (udFN == null) {
                                                                udFN = finalFirstName;
                                                            }
                                                            if (udLN == null) {
                                                                udLN = finalLastName;
                                                            }
                                                            if (udSpouse == null) {
                                                                udSpouse = finalSpouse;
                                                            }
                                                            if (udCAS == null) {
                                                                udCAS = finalCityState;
                                                            }
                                                            if (udPrim == null) {
                                                                udPrim = finalPrimaryContactNumber;
                                                            }
                                                            if (udSec == null) {
                                                                udSec = finalSecondaryContactNumber;
                                                            }
                                                            if (udZip == null) {
                                                                udZip = finalZipCode;
                                                            }
                                                            try {
                                                                SaveData(userID, loginEmail, udFN, udLN, udSpouse, udAddress, udCAS, udZip, udPrim, udSec);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                        if (viewId_placeholder == secId) {
                                                            secondaryContactTV.setText(etGen.getText());
                                                            udSec = etGen.getText().toString();
                                                            if (udFN == null) {
                                                                udFN = finalFirstName;
                                                            }
                                                            if (udLN == null) {
                                                                udLN = finalLastName;
                                                            }
                                                            if (udSpouse == null) {
                                                                udSpouse = finalSpouse;
                                                            }
                                                            if (udCAS == null) {
                                                                udCAS = finalCityState;
                                                            }
                                                            if (udPrim == null) {
                                                                udPrim = finalPrimaryContactNumber;
                                                            }
                                                            if (udSec == null) {
                                                                udSec = finalSecondaryContactNumber;
                                                            }
                                                            if (udZip == null) {
                                                                udZip = finalZipCode;
                                                            }
                                                            try {
                                                                SaveData(userID, loginEmail, udFN, udLN, udSpouse, udAddress, udCAS, udZip, udPrim, udSec);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }


                                                        }

                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        return;
                                                    }
                                                });



                                        alertDialog = builder.create();

                                        tvEdit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(getApplicationContext(), "Tap the fields you wish to change", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        final AlertDialog finalGenAD = alertDialog;
                                        View.OnClickListener editFields = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //need to indicate which alert dialog needs to be shown: firstName or LastName? City or State or Zip?
                                                int viewId = view.getId();

                                                if (viewId == nameTV.getId()) {
                                                    viewId_placeholder = viewId;
                                                    AlertDialog adName = null;
                                                    AlertDialog.Builder builderName;
                                                    builderName = new AlertDialog.Builder(new ContextThemeWrapper(Contact.this, R.style.myDialog));
                                                    View viewName = getLayoutInflater().inflate(R.layout.dialog_name, null);
                                                    final EditText etFN = viewName.findViewById(R.id.etFirstName);
                                                    final EditText etLN = viewName.findViewById(R.id.etLastName);
                                                    builderName.setView(viewName)
                                                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                udFN = etFN.getText().toString();
                                                                udLN = etLN.getText().toString();
                                                                if (udAddress == null) {
                                                                    udAddress = finalStreetAddress;
                                                                }
                                                                if (udSpouse == null) {
                                                                    udSpouse = finalSpouse;
                                                                }
                                                                if (udCAS == null) {
                                                                    udCAS = finalCityState;
                                                                }
                                                                if (udPrim == null) {
                                                                    udPrim = finalPrimaryContactNumber;
                                                                }
                                                                if (udSec == null) {
                                                                    udSec = finalSecondaryContactNumber;
                                                                }
                                                                if (udZip == null) {
                                                                    udZip = finalZipCode;
                                                                }
                                                                try {
                                                                    SaveData(userID, loginEmail, udFN, udLN, udSpouse, udAddress, udCAS, udZip, udPrim, udSec);
                                                                } catch (InterruptedException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                nameTV.setText(udFN.concat(" " + udLN));

                                                            }})
                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                            }
                                                    });
                                                    adName = builderName.create();
                                                    adName.show();


                                                    etFN.setText(finalFirstName);
                                                    etLN.setText(finalLastName);
                                                }
                                                if (viewId == mbrStatusTV.getId()) {
                                                    viewId_placeholder = viewId;
                                                    Toast.makeText(getApplicationContext(), "You cannot edit your Member Status from this application", Toast.LENGTH_LONG).show();

                                                }
                                                if (viewId == spouseTV.getId()) {
                                                    viewId_placeholder = viewId;
                                                    finalGenAD.show();
                                                    etGen.setText(finalSpouse);
                                                    adTitle.setText("Edit Spouse Name");
//                                                    etAlertDialog.setText(spouseTV.getText());
                                                }
                                                if (viewId == addressTV.getId()) {
                                                    viewId_placeholder = viewId;
                                                    AlertDialog adAddress = null;
                                                    AlertDialog.Builder builderAddress;
                                                    builderAddress = new AlertDialog.Builder(new ContextThemeWrapper(Contact.this, R.style.myDialog));
                                                    View viewAd = getLayoutInflater().inflate(R.layout.dialog_address, null);
                                                    final EditText etStreet = viewAd.findViewById(R.id.etStreet);
                                                    EditText etCAS = viewAd.findViewById(R.id.etCityAndState);
                                                    EditText etZip = viewAd.findViewById(R.id.etZip);
                                                    etStreet.setText(finalStreetAddress);
                                                    etCAS.setText(finalCityState);
                                                    etZip.setText(finalZipCode);

                                                    builderAddress.setView(viewAd);
                                                    builderAddress.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    udAddress = etStreet.getText().toString();
                                                                    if (udFN == null) {
                                                                        udFN = finalFirstName;
                                                                    }
                                                                    if (udLN == null) {
                                                                        udLN = finalLastName;
                                                                    }
                                                                    if (udSpouse == null) {
                                                                        udSpouse = finalSpouse;
                                                                    }
                                                                    if (udCAS == null) {
                                                                        udCAS = finalCityState;
                                                                    }
                                                                    if (udPrim == null) {
                                                                        udPrim = finalPrimaryContactNumber;
                                                                    }
                                                                    if (udSec == null) {
                                                                        udSec = finalSecondaryContactNumber;
                                                                    }
                                                                    if (udZip == null) {
                                                                        udZip = finalZipCode;
                                                                    }
                                                                    try {
                                                                        SaveData(userID, loginEmail, udFN, udLN, udSpouse, udAddress, udCAS, udZip, udPrim, udSec);
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    addressTV.setText(udAddress.concat("\n" + udCAS + ", " + " " + udZip));

                                                                }})
                                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    return;
                                                                }
                                                            });
                                                    adAddress = builderAddress.create();
                                                    adAddress.show();
                                                }
                                                if (viewId == primaryContactTV.getId()) {
                                                    viewId_placeholder = viewId;
                                                    finalGenAD.show();
                                                    etGen.setText(finalPrimaryContactNumber);
                                                    adTitle.setText("Edit Primary Contact Number");
                                                }
                                                if (viewId == secondaryContactTV.getId()) {
                                                    viewId_placeholder = viewId;
                                                    finalGenAD.show();
                                                    etGen.setText(finalSecondaryContactNumber);
                                                    adTitle.setText("Edit Secondary Contact Number");
                                                }
                                                if (viewId == emailTV.getId()) {
                                                    viewId_placeholder = viewId;
                                                    Toast.makeText(getApplicationContext(), "You cannot edit your email address from this application", Toast.LENGTH_LONG).show();
                                                }
//                                            editText.setText(view.getId().get tvEdit.getText());/////<---------
                                            }
                                        };


                                        nameTV.setOnClickListener(editFields);
                                        mbrStatusTV.setOnClickListener(editFields);
                                        spouseTV.setOnClickListener(editFields);
                                        addressTV.setOnClickListener(editFields);
                                        primaryContactTV.setOnClickListener(editFields);
                                        secondaryContactTV.setOnClickListener(editFields);
                                        emailTV.setOnClickListener(editFields);

                                    }

                                    tvMoreInfo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent moreInfoIntent = new Intent(getApplicationContext(), MoreInfo.class);
                                            moreInfoIntent.putExtra("bio", finalBiographicalInfo);
                                            moreInfoIntent.putExtra("YTA", finalYearTurnedActive);
                                            moreInfoIntent.putExtra("firstName", finalFirstName);
                                            moreInfoIntent.putExtra("lastName", finalLastName);
                                            moreInfoIntent.putExtra("login_email", loginEmail);
                                            moreInfoIntent.putExtra("user_email", finalEmail);
                                            moreInfoIntent.putExtra("user_id", userID);
                                            startActivity(moreInfoIntent);
                                            finish();

                                        }
                                    });



                                    btnCall.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel: " + finalPrimaryContactNumber));
                                            if (ActivityCompat.checkSelfPermission(Contact.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                Intent otherCallIntent = new Intent(Intent.ACTION_DIAL);
                                                otherCallIntent.setData(Uri.parse("tel: " + finalPrimaryContactNumber));
                                                startActivity(otherCallIntent);
                                                return;
                                            }
                                            startActivity(callIntent);
                                        }
                                    });

                                    btnEmail.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{finalEmail});
                                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject: ");
                                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body: ");

                                            emailIntent.setType("message/rfc822");
                                            startActivity(Intent.createChooser(emailIntent, "Choose email client... "));

                                        }
                                    });

                                    btnText.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                            sendIntent.setData(Uri.parse("sms: " + finalPrimaryContactNumber));
                                            startActivity(sendIntent);
                                        }
                                    });
                                }
                            });
                            Intent intent = new Intent(String.valueOf(this));
//                            intent.putExtra("json_data", json_string);
                            Log.d(TAG, "Contact.java doInBackground: json_string in oncreate: " + json_string);
                        }
                    }
                    bufferedReader.close();
                    inputStream.close();
                    return stringBuilder.toString().trim();

                }
            } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        public boolean SaveData(String uID, String email, String fName, String lName, String spouse, String streetAddress, String CAS, String zipCode, String primNum, String secNum) throws InterruptedException {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String url = "http://capefeargardenclub.org/cfgcTestingJSON/update.php";
            params.add(new BasicNameValuePair("userID", uID));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("firstName", fName));
            params.add(new BasicNameValuePair("lastName", lName));
            params.add(new BasicNameValuePair("spouse", spouse));
            params.add(new BasicNameValuePair("streetAddress", streetAddress));
            params.add(new BasicNameValuePair("CAS", CAS));
            params.add(new BasicNameValuePair("zipCode", zipCode));
            params.add(new BasicNameValuePair("primNum", primNum));
            params.add(new BasicNameValuePair("secNum", secNum));
            params.add(new BasicNameValuePair("bioUpdate", "no"));

            String resultServer  = getHttpPost(url,params,"savedata");

            /*** Default Value ***/
            String strStatusID = "0";
            String strMessage = "Unknown Status!";

            JSONObject c;
            try {
                c = new JSONObject(resultServer);
                strStatusID = c.getString("StatusID");
                strMessage = c.getString("Message");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Prepare Save Data
            if(strStatusID.equals("0"))
            {
//                ad.setMessage(strMessage);
//                ad.show();
                Toast.makeText(Contact.this, "Edit not successful", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                Toast.makeText(Contact.this, "Edit successful", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        public Bitmap GetImage(String uID, String email, String photoID) throws InterruptedException {
            final List<NameValuePair> params = new ArrayList<NameValuePair>();
            final String url = "http://capefeargardenclub.org/cfgcTestingJSON/getImage1.php";
            params.add(new BasicNameValuePair("userID", uID));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("photoID", photoID));
            final Bitmap[] bmp = new Bitmap[1];
//            final String url = strUrl;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    HttpGet httpRequest = null;

                    //////////////
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    str = new StringBuilder();
                    try {
                        httpPost.setEntity(new UrlEncodedFormEntity(params));
                        //problem starts here
                        HttpResponse response = client.execute(httpPost);
                        StatusLine statusLine = response.getStatusLine();//////
                        int statusCode = statusLine.getStatusCode();
                        if (statusCode == 200) { // Status OK
                            HttpEntity entity = response.getEntity();
                            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
                            InputStream instream = bufHttpEntity.getContent();
                            bmp[0] = BitmapFactory.decodeStream(instream);
                        } else {
                            Log.e("Log", "Failed to download result..");
                        }
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
            return bmp[0];
        }

        public String getHttpPost(String strUrl, final List<NameValuePair> params, final String purpose) throws InterruptedException {
            final String url = strUrl;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (purpose.equals("savedata")) {
                        HttpClient client = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(url);
                        str = new StringBuilder();

                        try {
                            httpPost.setEntity(new UrlEncodedFormEntity(params));
                            //problem starts here
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
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
            thread.join();
            return str.toString();
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
            tvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(String.valueOf(SBString));
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                String sssss = jsonObject.getString("userID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseJson(View view) {
        new BackgroundTask1().execute();
        Log.d(TAG, "Contact.java parseJson: json_string: ");
        if (json_string == null) {
//            Toast.makeText(getApplicationContext(), "First Get JSON", Toast.LENGTH_LONG).show();

        } else {
//            backIntent.putExtra("json_data", json_string);
            Intent intent = new Intent(this, ContactList.class);
            intent.putExtra("json_data", json_string);
            Log.d(TAG, "Contact.java parseJson: json_string" + json_string);

            startActivity(intent);
        }
    }

}


