package xyz.beerme.beerme;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String FIREBASE_URL = "https://beerme-b6cd6.firebaseio.com/";
    private static final String TAG = "";
    private EditText likesEditText;
    private EditText dislikesEditText;
    private EditText locationEditText;
    private EditText beerEditText;
    private ImageView mImageView;
    private Button submitButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private StorageReference mStorageRef;
    private String photoUrl;
    private Long tsLong;
    private String timestamp;
    private String imagePath;
    private UploadTask uploadTask;

    private List<Post> list_posts = new ArrayList<>();
    private Place myPlace;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_CAMERA = 20;
    private static final int SELECT_FILE = 30;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_post);

        //Create views
        beerEditText = (EditText) findViewById(R.id.input_beer);
        likesEditText = (EditText) findViewById(R.id.input_likes);
        dislikesEditText = (EditText) findViewById(R.id.input_dislikes);
        locationEditText = (EditText) findViewById(R.id.input_location);
        submitButton = (Button) findViewById(R.id.button_submit);
        mImageView = (ImageView) findViewById(R.id.user_image);

        //Google Places API connection
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        //Firebase
        initFirebase();
        addEventFirebaseListener();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //Gets context
        mContext = getApplicationContext();

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

       //Dialog opens immediately and asks for picture
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_choose_picture, null);
        Button galleryButton = (Button) v.findViewById(R.id.button_gallery);
        Button cameraButton = (Button) v.findViewById(R.id.button_camera);

        //Builds dialog
        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.show();

        //Starts camera intent and handles capture
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    File photoFile = null;
                    try{
                        photoFile = createImageFile();
                    } catch (IOException ex){
                        Snackbar message = Snackbar.make(findViewById(R.id.activity_create_post), "Error creating image", Snackbar.LENGTH_LONG);
                        message.show();
                    }
                    if(photoFile != null){
                        Uri photoURI = FileProvider.getUriForFile(CreatePostActivity.this,
                                "xyz.beerme.beerme.fileprovider",
                                photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                }
            }
        });

        //Starts gallery intent and handles selection
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_FILE);
            }
        });
    }

    //Asks user to select location
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
        //Uses result of place picker to set location
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            myPlace = PlacePicker.getPlace(data, this);
            locationEditText.setText(myPlace.getName());
        }

        //Uses result of camera intent and uploads and displays image
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mImageView.setImageBitmap(myBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            tsLong = System.currentTimeMillis()/1000;
            timestamp = tsLong.toString();
            StorageReference ref = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timestamp);
            ref.putBytes(imageBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl != null)
                        photoUrl = downloadUrl.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast toast = Toast.makeText(CreatePostActivity.this, "Image upload failed", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        //Uses result of gallery intent and uploads and displays image
        if(requestCode == SELECT_FILE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            imagePath = getPath(uri);
            Uri file = Uri.fromFile(new File(imagePath));
            Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
            mImageView.setImageBitmap(myBitmap);
            tsLong = System.currentTimeMillis()/1000;
            timestamp = tsLong.toString();
            StorageReference ref = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timestamp);
            uploadTask = ref.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast toast = Toast.makeText(CreatePostActivity.this, "Image upload failed", Toast.LENGTH_LONG);
                    toast.show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    photoUrl = downloadUrl.toString();
                }
            });
        }
    }

    //Gets path of gallery image
    private String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    //Adds firebase data reference
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

    //Initializes firebase
    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    //Submits posts to firebase
    private void submitNewPost() {
        String uid = UUID.randomUUID().toString();
        String beer = beerEditText.getText().toString();
        String likes = likesEditText.getText().toString();
        String dislikes = dislikesEditText.getText().toString();
        String author = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if(validatePost()) {

            if (photoUrl != null) {
                Post post = new Post(uid, author, beer, likes, dislikes, myPlace.getName().toString(), photoUrl);
                mDatabaseReference.child("posts").child(post.getmUid()).setValue(post);
            } else {
                Post post = new Post(uid, beer, likes, dislikes, myPlace.getName().toString());
                mDatabaseReference.child("posts").child(post.getmUid()).setValue(post);
            }
            clearEditText();
        }
    }

    //Creates file that picture from camera is written to
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_beerme";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Resets views and send user to main post activity
    private void clearEditText() {
        likesEditText.setText("");
        dislikesEditText.setText("");
        beerEditText.setText("");
        locationEditText.setText("");
        mImageView.setImageResource(android.R.color.transparent);

        Intent intent = new Intent(this, PostsActivity.class);
        startActivity(intent);
    }

    private boolean validatePost(){

       String name = beerEditText.getText().toString();
        String likes = likesEditText.getText().toString();
        String dislikes = dislikesEditText.getText().toString();
        String location = locationEditText.getText().toString();

        if(name.isEmpty())
        {
            beerEditText.setError("Beer name cannot be empty.");
            return false;
        }
        if(likes.isEmpty())
        {
            likesEditText.setError("Likes cannot be empty.");
            return false;
        }
        if(dislikes.isEmpty())
        {
            dislikesEditText.setError("Dislikes cannot be empty.");
            return false;
        }
        if(location.isEmpty())
        {
            locationEditText.setError("Location cannot be empty.");
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}