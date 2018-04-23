package com.example.lenovo.gardenclub;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ListAdapter;
import android.widget.TextView;

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
                //                contactHolder.tx_name.setText(contact.getName());

//                Contacts contact = (Contacts) this.getItem(position);
//                Log.d(TAG, "getView: contact = " + contact);
//                contactHolder.tx_name.setText(contact.getName());

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
            contactHolder.tx_name = row.findViewById(R.id.nameTV);
//            contactHolder.tx_email = row.findViewById(R.id.tx_email);
//            contactHolder.tx_mobile = row.findViewById(R.id.tx_mobile);
            contactHolder.tx_mbrstatus = row.findViewById(R.id.tv_mbrstatus);
            row.setTag(contactHolder);

        } else {
            contactHolder = (ContactHolder) row.getTag();
        }
        Contacts contact = (Contacts) this.getItem(position);
        contactHolder.tx_name.setText(contact.getName());
//        contactHolder.tx_email.setText(contact.getEmail());
//        contactHolder.tx_mobile.setText(contact.getMobile());
        contactHolder.tx_mbrstatus.setText(contact.getMbrStatus());

        return row;
    }

    static class ContactHolder {
        TextView tx_name, tx_email, tx_mobile, tx_mbrstatus;

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
