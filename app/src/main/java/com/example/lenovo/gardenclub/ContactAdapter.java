package com.example.lenovo.gardenclub;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.FutureTarget;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by Joe on 3/19/2018.
 */


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {
    private List<Contacts> contacts;
    private List<Contacts> contactsFiltered;
    private Context context;
    ValueFilter valueFilter;
    private static final String TAG = "ContactAdapter";
    String item;
    Bitmap bmp;

    List list = new ArrayList();
    List fullList = new ArrayList();

    public ContactAdapter(@NonNull Context context, List<Contacts> contacts) {
        this.context = context;
        this.contacts = contacts;
        this.contactsFiltered = contacts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tx_email;
        private TextView tx_mobile;
        private TextView tvMbrStatus;
        private ImageView mImageView;
        private View parentView;
        Map<String, Bitmap> bitmapMap;


        public ViewHolder(@NonNull View view) {
            super(view);
            this.parentView = view;
            this.tvName = (TextView) view.findViewById(R.id.nameTV);
            this.tvMbrStatus = (TextView) view.findViewById(R.id.tv_mbrstatus);
            this.mImageView = (ImageView) view.findViewById(R.id.imageView4);


        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Contacts contact = (Contacts) contactsFiltered.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvMbrStatus.setText(contact.getMbrStatus());
        String uID = contact.getUserID();
        String uEmail = contact.getEmail();
        String pID = contact.getPhotoID();
        holder.parentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: contact.getName():" + contact.getName());
                Log.d(TAG, "onClick: contact: " + contact);
                Log.d(TAG, "onClick: contact.getloginemail: " + contact.getLoginEmail());
                Log.d(TAG, "onClick: contact.getemail: " + contact.getEmail());
                Log.d(TAG, "onClick: contact.getMbrStatus: " + contact.getMbrStatus());
                Log.d(TAG, "onClick: contact.getMobile: " + contact.getMobile());
                Log.d(TAG, "onClick: contact.getPhotoID: " + contact.getPhotoID());
                Log.d(TAG, "onClick: contact.getUserID: " + contact.getUserID());
                Intent intent = new Intent(context, Contact.class);
                    intent.putExtra("user_id", contact.getUserID());
                    intent.putExtra("login_email", contact.getLoginEmail());
                context.startActivity(intent);
                ContactList.fa.finish();
            }
        });

        Log.d(TAG, "onBindViewHolder: pID: " + pID);

        if (pID != "null" && pID != null) {
//                            Bitmap bmp = GetImage(uID, uEmail, pID);
            ArrayList<String> arrayList = new ArrayList<>(3);
            arrayList.add(uID);
            arrayList.add(uEmail);
            arrayList.add(pID);
            Log.d(TAG, "onBindViewHolder: all good");

//            final List<NameValuePair> bmpParams = new ArrayList<NameValuePair>();
//            bmpParams.add(new BasicNameValuePair("userID", uID));
//            bmpParams.add(new BasicNameValuePair("email", uEmail));
//            bmpParams.add(new BasicNameValuePair("photoID", pID));
            try {
                bmp = new ImageGetter().execute(arrayList).get();
                Log.d(TAG, "onBindViewHolder: all good? LOL???");
                Glide.with(context)
                        .load(bmp)
                        .apply(RequestOptions.overrideOf(100, 100))
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.mImageView);
                holder.mImageView.setImageBitmap(bmp);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//            try {
//                holder.mImageView.setImageBitmap(GetImage(uID, uEmail, pID));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
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
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    //problem starts here
                    HttpResponse response = client.execute(httpPost);
                    StatusLine statusLine = response.getStatusLine();
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
        Bitmap image = bmp[0];
        return bmp[0];
    }

    public class ImageGetter extends AsyncTask<ArrayList<String>, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(ArrayList<String>[] strings) {
            Log.d(TAG, "doInBackground: strings: " + strings);
            final String url = "http://capefeargardenclub.org/cfgcTestingJSON/getImage1.php";
            Bitmap bmp = null;
//            final String url = strUrl;
            String uID = strings[0].get(0);
            Log.d(TAG, "doInBackground: uID: "+ uID);
            String email = strings[0].get(1);
            String photoID = strings[0].get(2);
            final List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userID", uID));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("photoID", photoID));

            //////////runnable = new runnable() { --->
            HttpGet httpRequest = null;

            //////////////
            Log.d(TAG, "doInBackground: all gucci");
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            Log.d(TAG, "doInBackground: still fucci");
            try {
                Log.d(TAG, "doInBackground: try");
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                //problem starts here
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();//////
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) { // Status OK
                    Log.d(TAG, "doInBackground: statuscode is ok");
                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
                    InputStream instream = bufHttpEntity.getContent();
                    bmp = BitmapFactory.decodeStream(instream);
//                    FutureTarget<Bitmap> futureTarget =
//                            Glide.with(context)
//                                    .asBitmap()
//                                    .load(url)
//                                    .submit(75, 75);
//                    bmp = futureTarget.get();
                } else {
                    Log.e(TAG, "doInBackground: status code not okay");
                    Log.e("Log", "Failed to download result..");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "doInBackground: ends");
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmp = bitmap;
            Log.d(TAG, "onPostExecute: starts and ends lol");
        }
    }

    @Override
    public int getItemCount() {
        return this.contactsFiltered.size();
    }

    @Nullable
    public Object getItem(int position) {
        return list.get(position);
    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View row;
//        row = convertView;
//        ContactHolder contactHolder;
//
//        if(row == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            row = layoutInflater.inflate(R.layout.row_layout_1, parent, false);
//            contactHolder = new ContactHolder();
//            contactHolder.tvName = row.findViewById(R.id.nameTV);
//            contactHolder.tvMbrStatus = row.findViewById(R.id.tv_mbrstatus);
//            contactHolder.mImageView = row.findViewById(R.id.imageView4);
//            row.setTag(contactHolder);
//
//        } else {
//            contactHolder = (ContactHolder) row.getTag();
//        }
//        Contacts contact = (Contacts) this.getItem(position);
//        contactHolder.tvName.setText(contact.getName());
//        contactHolder.tvMbrStatus.setText(contact.getMbrStatus());
//        Bitmap image = null;
////        try {
////            image = GetImage(contact.getUserID(), contact.getLoginEmail(), contact.getPhotoID());
////            contactHolder.mImageView.setImageBitmap(image);;
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        return row;
//    }



    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
//            FilterResults results = new FilterResults();
            Log.d(TAG, "performFiltering: charSeq: " + charSequence);
            Contacts current;
            String charString = charSequence.toString();
            if (charString.isEmpty()) {
                contactsFiltered = contacts;
                Log.d(TAG, "performFiltering: contactsFiltered = contacts = " + contactsFiltered);
            } else {
                List<Contacts> filteredList = new ArrayList<>();
//                filteredList.clear();
                for (int i = 0; i < contacts.size(); i++) {
                    current = (Contacts) contacts.get(i);
                    if ((current.getName().toUpperCase().contains(charSequence.toString().toUpperCase()))) {
                        filteredList.add(current);
                    }
                }
                contactsFiltered = filteredList;
                Log.d(TAG, "performFiltering: contactsFiltered: " + contactsFiltered);
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = contactsFiltered;
            Log.d(TAG, "performFiltering: filterResults.values = " + filterResults.values);
            return filterResults;

//            prevCnstrntLength = cnstrntLength;
//            cnstrntLength = charSequence.length();
//
//            if (charSequence != null && cnstrntLength > 0) {
//                List filterList = new ArrayList<>();
//                filterList.clear();
//                for (int i = 0; i < list.size(); i++) {
//                     current = (Contacts) list.get(i);
//                    if ((current.getName().toUpperCase().contains(charSequence.toString().toUpperCase()))) {
//                        filterList.add(current);
//                    }
//                }
//                results.count = filterList.size();
//                results.values = filterList;
//                if (cnstrntLength < prevCnstrntLength) {
//                    filterList = new ArrayList();
//                    for (int i = 0; i < fullList.size(); i++) {
//                        current = (Contacts) fullList.get(i);
//                        if ((current.getName().toUpperCase().contains(charSequence.toString().toUpperCase()))) {
//                            filterList.add(current);
//                        }
//                    }
//                    results.count = filterList.size();
//                    results.values = filterList;
//                }
//
//            } else if (cnstrntLength < prevCnstrntLength) {
//                results.count = fullList.size();
//                results.values = fullList;
//            }  else {
//                results.count = fullList.size();
//                results.values = fullList;
//            }
//            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            list = (ArrayList) results.values;
//            notifyDataSetChanged();
            contactsFiltered = (ArrayList<Contacts>) results.values;
            notifyDataSetChanged();

        }
    }






}
