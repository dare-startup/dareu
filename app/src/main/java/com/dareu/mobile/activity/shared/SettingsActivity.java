package com.dareu.mobile.activity.shared;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dareu.mobile.R;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.LoadProfileImageTask;
import com.dareu.mobile.net.account.UpdateProfileImageTask;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class SettingsActivity extends AppCompatActivity {

    private static final int BROWSE_REQUEST_CODE = 543;
    private static final int CAPTURE_REQUEST_CODE = 123;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 354;
    private CircularImageView image;
    private Uri imageUri;

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private File capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getComponents();
    }

    private void getComponents() {
        //create directory if not exists
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/DareU/");
        directory.mkdir();
        capture = new File(directory, System.currentTimeMillis() + ".jpg");
        try{
            capture.createNewFile();
        }catch(IOException ex){

        }
        image = (CircularImageView)findViewById(R.id.settingsImage);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setItems(new String[]{ "Camera", "Browse..."}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                switch (which){
                                    case 0:
                                        boolean writeExternalStoragePermission = ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                                        boolean cameraPersmission = ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                                        boolean readExternalStoragePermission = ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                                        if(writeExternalStoragePermission && cameraPersmission && readExternalStoragePermission){
                                            //camera
                                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capture));
                                            startActivityForResult(intent, CAPTURE_REQUEST_CODE);
                                        }else{
                                            //ask for permissions
                                            ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{ Manifest.permission.CAMERA,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    CAMERA_PERMISSION_REQUEST_CODE);
                                        }

                                        break;
                                    case 1:
                                        //browse
                                        intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        startActivityForResult(intent, BROWSE_REQUEST_CODE);
                                        break;
                                }
                            }
                        })
                        .setTitle("Update image")
                        .create()
                        .show();
            }
        });
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

        //load current image profile
        LoadProfileImageTask task = new LoadProfileImageTask(SettingsActivity.this, null, new AsyncTaskListener<Bitmap>() {
            @Override
            public void onTaskResponse(Bitmap response) {
                if(response != null)
                    image.setImageBitmap(response);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == BROWSE_REQUEST_CODE && resultCode == RESULT_OK){
            //get file to udate image
            image.setImageURI(data.getData());
            //save image path
            imageUri = data.getData();
            processCapturedImage();
        }else if(requestCode == CAPTURE_REQUEST_CODE && resultCode == RESULT_OK){
            imageUri = Uri.fromFile(capture);
            //get file to udate image
            image.setImageURI(imageUri);
            //save image path
            processCapturedImage();
        }
    }

    private void processCapturedImage(){
        try{
            InputStream is = getContentResolver().openInputStream(imageUri);
            UpdateProfileImageTask task = new UpdateProfileImageTask(SettingsActivity.this, is, new AsyncTaskListener<UpdatedEntityResponse>() {
                @Override
                public void onTaskResponse(UpdatedEntityResponse response) {
                    Snackbar.make(coordinatorLayout, "Your profile image has been updated", Snackbar.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onError(String errorMessage) {
                    Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG)
                            .show();
                }
            });
            task.execute();
        }catch(FileNotFoundException ex){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        switch(requestCode){
            case CAMERA_PERMISSION_REQUEST_CODE:
                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    //start camera capture activity
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capture));
                    startActivityForResult(intent, CAPTURE_REQUEST_CODE);
                }
                break;
        }
    }

}
