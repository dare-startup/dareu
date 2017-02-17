package com.dareu.mobile.activity.shared;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.dare.DareDescriptionTask;
import com.dareu.mobile.net.dare.UploadDareResponseTask;
import com.dareu.mobile.net.request.UploadDareResponseRequest;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.DareDescription;

import java.io.File;
import java.io.IOException;

public class UploadDareResponseActivity extends AppCompatActivity {

    private static final String TAG = "UploadDareResponse";
    public static final String DARE_ID = "dareId";

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView dareName, dareDescription;
    private ImageView thumbImage;
    private EditText comment;
    private LinearLayout layout;
    private CoordinatorLayout coordinatorLayout;

    private static final int CAPTURE_REQUEST_CODE = 1231;
    private static final int GALLERY_REQUEST_CODE = 1231;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 5423;
    private File newFileName;
    private File thumbFile;
    private Bitmap currentThumbBitmap;
    private boolean videoFileReady;
    private DareDescription currentDareDescription;

    private String dareId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_dare_response);
        getComponents();
    }

    private void getComponents() {
        dareId = getIntent().getStringExtra(DARE_ID);
        //create directory if does not exists
        SharedUtils.VIDEO_DIRECTORY.mkdir();
        newFileName = new File(SharedUtils.VIDEO_DIRECTORY, System.currentTimeMillis() + ".mp4");
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
        new DareDescriptionTask(UploadDareResponseActivity.this, new AsyncTaskListener<DareDescription>() {
            @Override
            public void onTaskResponse(DareDescription response) {
                dareName.setText(response.getName());
                dareDescription.setText(response.getDescription());
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                currentDareDescription = response;
            }

            @Override
            public void onError(String errorMessage) {

            }
        }, dareId).execute();

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
            //TODO: create a new task here
            new UploadDareResponseTask(UploadDareResponseActivity.this, )
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
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 32949120L); // 30MB
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFileName));
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
    }

    private void processDareVideo(Uri uri){
        //save bitmap to file
        try{
            thumbFile = new File(SharedUtils.IMAGE_DIRECTORY, System.currentTimeMillis() + ".jpg");
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
