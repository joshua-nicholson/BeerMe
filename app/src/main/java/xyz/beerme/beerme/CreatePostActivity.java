package xyz.beerme.beerme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String FIREBASE_URL = "https://beerme-b6cd6.firebaseio.com/";
    private EditText likesEditText;
    private EditText dislikesEditText;
    private Button submitButton;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        firebaseRef = FirebaseDatabase.getInstance().getReference();

        likesEditText = (EditText) findViewById(R.id.input_dislikes);
        dislikesEditText = (EditText) findViewById(R.id.input_dislikes);
        submitButton = (Button) findViewById(R.id.button_submit);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == submitButton.getId())
        {
            submitData();
        }

    }

    private void submitData() {
       String likes = likesEditText.getText().toString();
        String dislikes = dislikesEditText.getText().toString();
        Post post = new Post(likes,dislikes);
        firebaseRef.push();
    }
}