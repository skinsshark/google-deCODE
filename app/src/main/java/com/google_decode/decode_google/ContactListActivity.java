package com.google_decode.decode_google;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google_decode.decode_google.entity.ImageMessage;
import com.google_decode.decode_google.entity.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListActivity extends AppCompatActivity {

    private static final String TAG = ContactListActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE_CAPTURE = 189;

    @BindView(R.id.contact_list)
    RecyclerView contactList;

    @BindView(R.id.frame_layout)
    FrameLayout mFrameLayout;

    private FirebaseDatabase mFirebaseDatabase;
    private List<String> contactUids;

    private ColorGenerator mColorGenerator;

    private ContactAdapter mContactAdapter;

    private DatabaseReference mMailboxRef;

    private User sendTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        setupFirebase();
        ButterKnife.bind(this);

        mContactAdapter = new ContactAdapter(new ArrayList<User>());
        contactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        contactList.setAdapter(mContactAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMailboxRef = mFirebaseDatabase.getReference("users").child(App.getCurrentUser().uid).child("mailbox");
        contactUids = App.getCurrentUser().contacts;
        if (contactUids == null || contactUids.isEmpty()) {
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
                                contacts.add(user);
                            }
                        }

                        mContactAdapter.setContacts(contacts);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

        mMailboxRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ImageMessage message = ImageMessage.parseSingleImage(dataSnapshot);
                Log.d("Child Added", message.toString());
                displayImage(message.getUri());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ImageMessage message = ImageMessage.parseSingleImage(dataSnapshot);
                Log.d("Child Added", message.toString());
                displayImage(message.getUri());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayImage(String uri) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(ImageActivity.EXTRA_URI, uri);
        startActivity(intent);
    }


    //Accepts the local file location of the image as well as the name that should be used to store the image on FireBase.
    private void uploadPic(String fileLoc, String fileName) {
        //Setting up FireBase
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //Retrieving Local Image URI
        Uri file = Uri.fromFile(new File(fileLoc));
        StorageReference riversRef = mStorageRef.child("/images/" + fileName);

        //Uploading the file to FireBase storage
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri uri = taskSnapshot.getDownloadUrl();
                        ImageMessage message = new ImageMessage(
                                App.getCurrentUser().name,
                                App.getCurrentUser().uid,
                                sendTo.name,
                                sendTo.uid,
                                uri.toString()
                        );
                        DatabaseRetriever.sendMessage(message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ContactListActivity.this, "Sorry the file wasn't sent!", Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void takePic() {
        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
        File imageFile = new File(imageFilePath);

        Uri imageFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", imageFile);

        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(camera_intent, REQUEST_IMAGE_CAPTURE);
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
        public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ContactAdapter.ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, final int position) {
            holder.setName(contacts.get(position).name);
            holder.takePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendTo = contacts.get(position);
                    takePic();
                }
            });
        }

        @Override
        public int getItemCount() {
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

            View root;
            @BindView(R.id.profile_picture)
            ImageView profileIconView;
            @BindView(R.id.contact_name_text)
            TextView usernameText;
            @BindView(R.id.sent_status)
            TextView statusText;
            @BindView(R.id.take_picture)
            ImageView takePicture;


            public ContactViewHolder(View itemView) {
                super(itemView);
                root = itemView;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { //add && requestCode == REQUEST_IMAGE_CAPTURE ???
            String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
            uploadPic(imageFilePath, "picture.jpg");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
