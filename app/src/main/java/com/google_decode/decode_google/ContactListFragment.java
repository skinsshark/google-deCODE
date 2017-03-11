package com.google_decode.decode_google;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google_decode.decode_google.entity.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yifan on 17/3/11.
 */

public class ContactListFragment extends Fragment {

    private static final String TAG = ContactListFragment.class.getSimpleName();

    @BindView(R.id.contact_list)
    RecyclerView contactList;

    private FirebaseDatabase mFirebaseDatabase;
    private List<String> contactUids;

    private ColorGenerator mColorGenerator;

    private ContactAdapter mContactAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_contact_list, null);
        ButterKnife.bind(this, layout);

        mContactAdapter = new ContactAdapter(new ArrayList<User>());
        contactList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        contactList.setAdapter(mContactAdapter);

        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebase();
    }

    private void setupFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        contactUids = App.getCurrentUser().contacts;
        if (contactUids == null || contactUids.isEmpty()) {
            Log.d(TAG, "No contacts");
            return;
        }
        mFirebaseDatabase.getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "All users?" + dataSnapshot.getValue().toString());
                        final List<User> contacts = new ArrayList<>();
                        for (User user : User.parseUserList(dataSnapshot)) {
                            if (contactUids.contains(user.uid)) {
                                Log.d(TAG, "add");

                                contacts.add(user);
                                Log.d(TAG, "Contacts size: " + contacts.size());
                            }
                        }

                        mContactAdapter.setContacts(contacts);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private static final int EMPTY = -1;
        private static final int NON_EMPTY = 1;

        private List<User> contacts;

        public ContactAdapter(List<User> contacts) {
            mColorGenerator = ColorGenerator.DEFAULT;
            this.contacts = contacts;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");
            return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {
            Log.d("onBindViewHolder", contacts.get(position).name);
            holder.setName(contacts.get(position).name);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "contacts.size():" + contacts.size());
            return contacts.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (contacts == null || contacts.size() == 0) {
                return EMPTY;
            } else {
                return NON_EMPTY;
            }
        }

        public void setContacts(List<User> contacts) {
            this.contacts = contacts;
            notifyDataSetChanged();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.profile_picture)
            ImageView profileIconView;
            @BindView(R.id.contact_name_text)
            TextView usernameText;
            @BindView(R.id.sent_status)
            TextView statusText;

            public ContactViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setStatus(String status) {
                statusText.setText(status);
            }

            public void setName(String name) {
                usernameText.setText(name);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(name.substring(0, 1), mColorGenerator.getRandomColor());
                profileIconView.setImageDrawable(drawable);
            }
        }
    }


}
