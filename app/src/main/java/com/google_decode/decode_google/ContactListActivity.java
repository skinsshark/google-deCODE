package com.google_decode.decode_google;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ContactListActivity extends AppCompatActivity {

    private ContactListFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFragment = new ContactListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.list_place_holder, mFragment)
                .commit();
    }
}
