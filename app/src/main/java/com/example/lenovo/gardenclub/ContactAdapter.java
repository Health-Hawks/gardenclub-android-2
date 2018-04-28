package com.example.lenovo.gardenclub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by Joe on 3/19/2018.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {
    private List<Contacts> contacts;
    private List<Contacts> contactsFiltered;
    private Context context;
    ValueFilter valueFilter;
    int prevCnstrntLength;
    int cnstrntLength;
    private static final String TAG = "ContactAdapter";
    String item;

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
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        row = layoutInflater.inflate(R.layout.row_layout_1, parent, false);
//        contactHolder = new ContactHolder();
//        contactHolder.tvName = row.findViewById(R.id.nameTV);
//        contactHolder.tvMbrStatus = row.findViewById(R.id.tv_mbrstatus);
//        contactHolder.mImageView = row.findViewById(R.id.imageView4);
//        row.setTag(contactHolder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Contacts contact = (Contacts) contactsFiltered.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvMbrStatus.setText(contact.getMbrStatus());
        //ONCLICKLISTENER<----------------------------------------------------
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
        
    }

    @Override
    public int getItemCount() {
        return this.contactsFiltered.size();
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
//
//    public void add(Contacts object) {
//        super.add(object);
//        list.add(object);
//        fullList = list;
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
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

    /////////////////////////////////////////////
    ///////////////////////////////////////////
    //////////////////////////////////////////
    //////////////////////////////////////

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
