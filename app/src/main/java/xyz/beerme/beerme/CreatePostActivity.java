package xyz.beerme.beerme;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String FIREBASE_URL = "https://beerme-b6cd6.firebaseio.com/";
    private static final String TAG = "";
    private EditText likesEditText;
    private EditText dislikesEditText;
    private EditText locationEditText;
    private EditText beerEditText;
    private Button submitButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private GoogleApiClient mGoogleApiClient;

    private List<Post> list_posts = new ArrayList<>();
    private Place myPlace;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_CAMERA = 20;
    private static final int SELECT_FILE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Open camera immediately as a photo is required
        //TODO: Make this work
        //dispatchTakePictureIntent();

        setContentView(R.layout.activity_create_post);

        //Create views
        beerEditText = (EditText) findViewById(R.id.input_beer);
        likesEditText = (EditText) findViewById(R.id.input_likes);
        dislikesEditText = (EditText) findViewById(R.id.input_dislikes);
        locationEditText = (EditText) findViewById(R.id.input_location);
        submitButton = (Button) findViewById(R.id.button_submit);

        //Google Places API connection
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        //Firebase
        initFirebase();
        addEventFirebaseListener();

        //Ask for picture
        getPicture();

        //Location EditText listener
        locationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    displayPlacePicker();
                }
            }
        });
        //Submit button listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNewPost();
            }
        });
    }

    private void getPicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_choose_picture, null);
        Button galleryButton = (Button) v.findViewById(R.id.button_gallery);
        Button cameraButton = (Button) v.findViewById(R.id.button_camera);

        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.show();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
                dialog.dismiss();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_FILE);
                dialog.dismiss();
            }
        });
    }

    private void displayPlacePicker(){
       if(mGoogleApiClient == null || !mGoogleApiClient.isConnected())
           return;

       PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

       try{
          Intent intent = builder.build(this);
           startActivityForResult(intent, PLACE_PICKER_REQUEST);
       }catch (GooglePlayServicesRepairableException e){
           Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
       } catch ( GooglePlayServicesNotAvailableException e ) {
           Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
       }
   }

   @Override
   protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            myPlace = PlacePicker.getPlace(data, this);
            locationEditText.setText(myPlace.getName());
        }
    }

    private void addEventFirebaseListener() {
        //TODO: Add progress bar when submitting post
        //Progressing
        //circular_progress.setVisibility(View.VISIBLE);
        //list_data.setVisibility(View.INVISIBLE);

        mDatabaseReference.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (list_posts.size() > 0)
                    list_posts.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    list_posts.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    private void submitNewPost() {
        String uid = UUID.randomUUID().toString();
        String beer = beerEditText.getText().toString();
        String likes = likesEditText.getText().toString();
        String dislikes = dislikesEditText.getText().toString();

        Post post = new Post(uid, beer, likes, dislikes, myPlace.getName().toString());
        mDatabaseReference.child("posts").child(post.getmUid()).setValue(post);
        clearEditText();
    }



    private void clearEditText() {
        likesEditText.setText("");
        dislikesEditText.setText("");
        beerEditText.setText("");
        locationEditText.setText("");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast toast = new Toast(this);

    }
}