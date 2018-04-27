package com.example.lenovo.gardenclub;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Joe on 4/27/2018.
 */

public class ContactsAdapter extends ArrayAdapter<Contacts>{
    public ContactsAdapter (Context context, List<Contacts> contacts) {
        super(context, -1);

    }
}
