package xyz.beerme.beerme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://beerme-b6cd6.firebaseio.com/";
    private EditText likesEditText;
    private EditText dislikesEditText;
    private Button submitButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    private List<Post> list_posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //Create views
        likesEditText = (EditText) findViewById(R.id.input_likes);
        dislikesEditText = (EditText) findViewById(R.id.input_dislikes);
        submitButton = (Button) findViewById(R.id.button_submit);

        //Firebase
        initFirebase();
        addEventFirebaseListener();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNewPost();
            }
        });
    }

    private void addEventFirebaseListener() {
        //Progressing
        //circular_progress.setVisibility(View.VISIBLE);
        //list_data.setVisibility(View.INVISIBLE);

        mDatabaseReference.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(list_posts.size() > 0)
                    list_posts.clear();
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    list_posts.add(post);
                }
                //ListViewAdapter adapter = new ListViewAdapter(CreatePostActivity.this,list_posts);
                //list_data.setAdapter(adapter);

                //circular_progress.setVisibility(View.INVISIBLE);
                //list_data.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClick(View v) {
        if(v.getId() ==  R.id.button_submit)
        {
            Toast toast = Toast.makeText(this, "yup", Toast.LENGTH_SHORT);
            toast.show();
            submitNewPost();
        }

    }

    private void initFirebase()
    {
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    private void submitNewPost() {

        Post post = new Post(UUID.randomUUID().toString(),likesEditText.getText().toString(),dislikesEditText.getText().toString());
        mDatabaseReference.child("posts").child(post.getmUid()).setValue(post);
        clearEditText();
    }

    private void clearEditText() {
       likesEditText.setText("");
        dislikesEditText.setText("");
    }
}