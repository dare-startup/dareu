package com.dareu.mobile.activity.shared;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.AccountProfile;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private static final int BROWSE_REQUEST_CODE = 543;
    private static final int CAPTURE_REQUEST_CODE = 123;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 354;
    private CircularImageView image;
    private Uri imageUri;

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private File capture;
    private AccountClientService accountService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        accountService = RetroFactory.getInstance()
                .create(AccountClientService.class);
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
        //load current image
        AccountProfile accountProfile = SharedUtils.getCurrentProfile(this);
        if(accountProfile != null){
            //load it
            SharedUtils.loadImagePicasso(image, this, accountProfile.getImageUrl());
            ((TextView)findViewById(R.id.settingsName)).setText(accountProfile.getName());
        }
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
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //write bytes into output stream
            int read = -1;
            byte[] bytes = new byte[1024];

            while((read = is.read(bytes)) != -1)
                out.write(bytes);

            RequestBody filePart = RequestBody.create(MediaType.parse("image/jpeg"), out.toByteArray());
            //RequestBody filePart = RequestBody.create(MediaType.parse("image/jpeg"), new File(imageUri.getPath()));
            accountService.updateProfileImage(filePart, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
            .enqueue(new Callback<UpdatedEntityResponse>() {
                @Override
                public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                    //update image url
                    AccountProfile profile = SharedUtils.getCurrentProfile(SettingsActivity.this);
                    profile.setImageUrl(response.body().getMessage());
                    Snackbar.make(coordinatorLayout, "Your profile image has been updated", Snackbar.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {
                    Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG)
                            .show();
                }
            });
        }catch(FileNotFoundException ex){

        }catch(Exception ex){

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
