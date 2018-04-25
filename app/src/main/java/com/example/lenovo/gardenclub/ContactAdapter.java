package com.example.lenovo.gardenclub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 3/19/2018.
 */

public class ContactAdapter extends ArrayAdapter implements Filterable {
    ValueFilter valueFilter;
    int prevCnstrntLength;
    int cnstrntLength;
    private LayoutInflater inflater;
    private static final String TAG = "ContactAdapter";
    String item;

    List list = new ArrayList();
    List fullList = new ArrayList();

    public ContactAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (list != null) {
                Contacts item = (Contacts) adapterView.getItemAtPosition(i);
                String name = item.getName();
                String uID = item.getUserID();
                String loginEmail = item.getLoginEmail();
                Intent intent = new Intent(adapterView.getContext(), Contact.class);
                intent.putExtra("user_id", uID);
                intent.putExtra("login_email", loginEmail);
                adapterView.getContext().startActivity(intent);
                ContactList.fa.finish();

            }
        }
    };

    public void add(Contacts object) {
        super.add(object);
        list.add(object);
        fullList = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        row = convertView;
        ContactHolder contactHolder;

        if(row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.row_layout_1, parent, false);
            contactHolder = new ContactHolder();
            contactHolder.tvName = row.findViewById(R.id.nameTV);
            contactHolder.tvMbrStatus = row.findViewById(R.id.tv_mbrstatus);
            contactHolder.mImageView = row.findViewById(R.id.imageView4);
            row.setTag(contactHolder);

        } else {
            contactHolder = (ContactHolder) row.getTag();
        }
        Contacts contact = (Contacts) this.getItem(position);
        contactHolder.tvName.setText(contact.getName());
        contactHolder.tvMbrStatus.setText(contact.getMbrStatus());
        Bitmap image = null;
        try {
            image = GetImage(contact.getUserID(), contact.getLoginEmail(), contact.getPhotoID());
            contactHolder.mImageView.setImageBitmap(image);;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return row;
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
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
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
        Log.d(TAG, "GetImage: bmp: " + bmp[0]);
        return bmp[0];
    }

    static class ContactHolder {
        TextView tvName, tx_email, tx_mobile, tvMbrStatus;
        ImageView mImageView;

    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            Contacts current;

            prevCnstrntLength = cnstrntLength;
            cnstrntLength = constraint.length();

            if (constraint != null && cnstrntLength > 0) {
                List filterList = new ArrayList<>();
                filterList.clear();
                for (int i = 0; i < list.size(); i++) {
                     current = (Contacts) list.get(i);
                    if ((current.getName().toUpperCase().contains(constraint.toString().toUpperCase()))) {
                        filterList.add(current);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
                if (cnstrntLength < prevCnstrntLength) {
                    filterList = new ArrayList();
                    for (int i = 0; i < fullList.size(); i++) {
                        current = (Contacts) fullList.get(i);
                        if ((current.getName().toUpperCase().contains(constraint.toString().toUpperCase()))) {
                            filterList.add(current);
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                }

            } else if (cnstrntLength < prevCnstrntLength) {
                results.count = fullList.size();
                results.values = fullList;
            }  else {
                results.count = fullList.size();
                results.values = fullList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    }


}
