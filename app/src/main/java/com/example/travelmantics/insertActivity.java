package com.example.travelmantics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class insertActivity extends AppCompatActivity {

    private EditText txtdeals;
    private EditText txtprice;
    private EditText txtdescription;
    private ImageView imageView_insert;
    private Button button_selectImage;

    private String downloadLink;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    public static final int RC_SIGN_IN = 1;


    ProgressDialog progressDialog;

    public Uri returnUri;


    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insertactivity);

        // FIREBASE LOGIN UI
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthlistner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(), "User already logged in ", Toast.LENGTH_LONG);
                } else {

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setIsSmartLockEnabled(true)
                                    .build(), RC_SIGN_IN);
                }
            }
        };


        //


        txtdeals = findViewById(R.id.txtdeals);
        txtprice = findViewById(R.id.txtprice);
        txtdescription = findViewById(R.id.txtdescription);
        imageView_insert = findViewById(R.id.imageView_insert);
        button_selectImage = findViewById(R.id.button_selectImage);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("ProgressDialog"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);


        button_selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startGallery();
            }
        });


    }

  /*  @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                upload_image();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return true;


    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        inflater.inflate(R.menu.list_menu_insert, menu);
        /*MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        insertMenu.setVisible(true);*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check_menu:
                Intent intent = new Intent(this, listdeals.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getApplicationContext(), " User Logged Out", Toast.LENGTH_LONG).show();

                            }
                        });
                return true;
            case R.id.save_menu:
                upload_image();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void upload_image() {

        progressDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        final StorageReference storageRef = storage.getReference("Profile_pictures/" + txtdeals.getText().toString().trim() + ".jpg");

        // Get the data from an ImageView as bytes
        imageView_insert.setDrawingCacheEnabled(true);
        imageView_insert.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView_insert.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();

                final String sdownload_url = String.valueOf(downloadUrl);

                downloadLink = sdownload_url;

                savedeal();

                Toast.makeText(getApplicationContext(), "Uploaded Successfully ", Toast.LENGTH_LONG).show();

                progressDialog.dismiss();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setTitle("Picture Upload"); // Setting Title
                progressDialog.setMessage("Upload is " + progress + "% done"); // Setting Message


            }
        });


    }

    private void savedeal() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String txtdeal = txtdeals.getText().toString();
        String txtprices = txtprice.getText().toString();
        String txtdescriptions = txtdescription.getText().toString();


        traveldeal deal = new traveldeal(txtdeal, txtprices, txtdescriptions, downloadLink);

        db.collection("TRAVEL_DEALS")
                .add(deal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {


                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "deal saved", Toast.LENGTH_LONG).show();

                clean();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void clean() {
        txtdeals.setText("");
        txtprice.setText("");
        txtdescription.setText("");

        txtdeals.requestFocus();

    }

    private void startGallery() {

        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                returnUri = data.getData();
                Bitmap bitmapImage;
                bitmapImage = null;
                try {

                    bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), returnUri);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView_insert.setImageBitmap(bitmapImage);


            }


        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthlistner);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mFirebaseAuth.removeAuthStateListener(mAuthlistner);
    }

    public void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(getApplicationContext(), "User Signed Out", Toast.LENGTH_LONG).show();

                        finish();
                    }
                });

    }
}
