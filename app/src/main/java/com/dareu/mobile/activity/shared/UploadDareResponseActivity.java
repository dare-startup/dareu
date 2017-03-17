package com.dareu.mobile.activity.shared;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dareu.mobile.R;
import com.dareu.mobile.service.UploadDareResponseIntentService;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.entity.DareDescription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadDareResponseActivity extends AppCompatActivity {

    private static final String TAG = "UploadDareResponse";
    public static final String DARE_ID = "dareId";
    public static final String UPLOADING = "uplaodingDareResponse";

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView dareName, dareDescription;
    private ImageView thumbImage;
    private EditText comment;
    private LinearLayout layout;
    private CoordinatorLayout coordinatorLayout;

    private static final int CAPTURE_REQUEST_CODE = 1231;
    private static final int GALLERY_REQUEST_CODE = 1234;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 5423;
    private File newFileName;
    private File thumbFile;
    private Bitmap currentThumbBitmap;
    private boolean videoFileReady;
    private DareDescription currentDareDescription;

    private String dareId;
    private DareClientService dareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_dare_response);
        dareService = RetroFactory.getInstance()
                .create(DareClientService.class);
        getComponents();
    }

    private void getComponents() {
        dareId = getIntent().getStringExtra(DARE_ID);
        //create directory if does not exists
        SharedUtils.VIDEO_DIRECTORY.mkdir();
        newFileName = new File(SharedUtils.VIDEO_DIRECTORY, System.currentTimeMillis() + ".mp4");
        thumbFile = new File(SharedUtils.IMAGE_DIRECTORY, System.currentTimeMillis() + ".jpg");
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        dareName= (TextView)findViewById(R.id.uploadDareResponseDareName);
        dareDescription = (TextView)findViewById(R.id.uploadDareResponseDareDescription);
        thumbImage = (ImageView)findViewById(R.id.uploadDareResponseThumb);
        comment = (EditText)findViewById(R.id.uploadDareResponseComment);
        layout = (LinearLayout)findViewById(R.id.uploadDareResponseLayout);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Upload dare response");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask if really want to cancel upload
                new AlertDialog.Builder(UploadDareResponseActivity.this)
                        .setTitle("Cancel dare upload")
                        .setMessage("Do you want to cancel upload?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        dareService.dareDescription(dareId, SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
                .enqueue(new Callback<DareDescription>() {
                    @Override
                    public void onResponse(Call<DareDescription> call, Response<DareDescription> response) {
                        dareName.setText(response.body().getName());
                        dareDescription.setText(response.body().getDescription());
                        progressBar.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        currentDareDescription = response.body();
                    }

                    @Override
                    public void onFailure(Call<DareDescription> call, Throwable t) {

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.upload_dare_response, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.uploadDareResponseMenuItem:
                //upload
                uploadDareResponse();
                break;
            case R.id.uploadDareResponseAttachFile:
                //choose file
                chooseDareResponse();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadDareResponse() {
        if(currentThumbBitmap == null)
            Snackbar.make(coordinatorLayout, "You must create a video response to upload", Snackbar.LENGTH_LONG)
            .setAction("Dismiss", null)
            .show();
        else if(comment.getText().toString().isEmpty())
            Snackbar.make(coordinatorLayout, "You must create a comment for this video response", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null)
                    .show();
        else{
            try{
                //save current bitmap to a afile
                String thumbPath = SharedUtils.IMAGE_DIRECTORY.getAbsolutePath() + System.currentTimeMillis() + ".jpg";
                SharedUtils.saveBitmapToFile(currentThumbBitmap, thumbPath);
                String videoPath = newFileName.getAbsolutePath();
                String commentValue = comment.getText().toString();

                //start service
                Intent intent = new Intent(this, UploadDareResponseIntentService.class);
                intent.putExtra(UploadDareResponseIntentService.THUMBNAIL_PATH, thumbPath);
                intent.putExtra(UploadDareResponseIntentService.VIDEO_PATH, videoPath);
                intent.putExtra(UploadDareResponseIntentService.COMMENT, commentValue);
                intent.putExtra(UploadDareResponseIntentService.DARE_ID, dareId);
                startService(intent);

                //toast
                Toast.makeText(UploadDareResponseActivity.this, "Your response will start uploading shortly", Toast.LENGTH_LONG)
                        .show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(UPLOADING, String.valueOf(Boolean.TRUE));

                setResult(RESULT_OK, resultIntent);
                //finish activity
                finish();
            }catch(IOException ex){
                //file not found
            }
        }
    }

    private void chooseDareResponse() {
        if(currentDareDescription != null){
            new AlertDialog.Builder(UploadDareResponseActivity.this)
                    .setTitle("Choose file")
                    .setItems(new String[]{ "Gallery", "Camera" }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which){
                                case 0:
                                    //gallery
                                    showGallerySelector();
                                    break;
                                case 1:
                                    //camera
                                    recordVideo();
                                    break;
                            }
                        }
                    })
                    .show();
        }else{
            Toast.makeText(UploadDareResponseActivity.this, "Wait until the dare info is fully loaded", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void showGallerySelector() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/mp4");

        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void recordVideo() {
        Intent intent = null;
        boolean writeExternalStoragePermission = ContextCompat.checkSelfPermission(UploadDareResponseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean cameraPersmission = ContextCompat.checkSelfPermission(UploadDareResponseActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean readExternalStoragePermission = ContextCompat.checkSelfPermission(UploadDareResponseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(writeExternalStoragePermission && cameraPersmission && readExternalStoragePermission){
            //camera
            startCameraIntent();
        }else{
            //ask for permissions
            ActivityCompat.requestPermissions(UploadDareResponseActivity.this, new String[]{ Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void startCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 25);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 32949120L); // 30MB
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", newFileName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, CAPTURE_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Uri uri = null;
        if(requestCode == CAPTURE_REQUEST_CODE && resultCode == RESULT_OK){
            //video has been captured
            processDareVideo(data.getData());
        }else if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            //video has been selected
            processDareVideoFile(data.getData());
        }
    }

    private void processDareVideoFile(Uri data) {
        try{
            String path = SharedUtils.getRealPathFromURI(UploadDareResponseActivity.this, data);
            newFileName = new File(path);
            currentThumbBitmap = ThumbnailUtils.createVideoThumbnail(newFileName.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            SharedUtils.saveBitmapToFile(currentThumbBitmap, thumbFile.getAbsolutePath());
            thumbImage.setImageBitmap(currentThumbBitmap);
            videoFileReady = true;
        }catch(FileNotFoundException ex){
            Log.e(TAG, ex.getMessage());
        }catch (IOException ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    private void processDareVideo(Uri uri){
        //save bitmap to file
        try{
            currentThumbBitmap = ThumbnailUtils.createVideoThumbnail(newFileName.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            SharedUtils.saveBitmapToFile(currentThumbBitmap, thumbFile.getAbsolutePath());
            //set bitmap
            thumbImage.setImageBitmap(currentThumbBitmap);
            videoFileReady = true;
        }catch(IOException ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        switch(requestCode){
            case CAMERA_PERMISSION_REQUEST_CODE:
                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    //start camera capture activity
                    startCameraIntent();
                }
                break;
        }
    }
}
