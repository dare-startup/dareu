package com.dareu.mobile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.task.AsyncTaskListener;
import com.dareu.mobile.task.SignupTask;
import com.dareu.mobile.task.request.SignupRequest;
import com.dareu.mobile.utils.SharedUtils;
import com.google.firebase.FirebaseOptions;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignupActivity extends AppCompatActivity implements ActivityListener{

    private static final String TAG = "SignupActivity";
    private static final int GALLERY_REQUEST_CODE = 123342;

    private ImageView imageView;
    private EditText nameText, emailText, usernameText, passwordText;
    private TextView birthdayView;
    private Button signupButton;
    private ProgressDialog progressDialog;
    private CoordinatorLayout coordinatorLayout;

    private final SignupRequest currentRequest = new SignupRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getComponents();
        initialize();
    }

    @Override
    public void onBackPressed(){
        //if pressed, show a confirm dialog to exit
    }

    @Override
    public void getComponents() {
        imageView = (ImageView)findViewById(R.id.signupProfilePicture);
        nameText = (EditText)findViewById(R.id.signupNameText);
        emailText = (EditText)findViewById(R.id.signupEmailText);
        usernameText = (EditText)findViewById(R.id.signupUsernameText);
        passwordText = (EditText)findViewById(R.id.signupPasswordText);
        signupButton = (Button)findViewById(R.id.signupButton);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
    }

    @Override
    public void initialize() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, ""), GALLERY_REQUEST_CODE);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get values
                String name = nameText.getText().toString();
                String email = emailText.getText().toString();
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                String birthday = birthdayView.getText().toString();

                if(! SharedUtils.validateDate(birthday)){
                    Snackbar.make(coordinatorLayout, "You must provide your birthday", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }else if(name.isEmpty()){
                    Snackbar.make(coordinatorLayout, "You must provide your name", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }else if(email.isEmpty()){
                    Snackbar.make(coordinatorLayout, "You must provide your email", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }else if(username.isEmpty()){
                    Snackbar.make(coordinatorLayout, "You must provide your username", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }else if(password.isEmpty()){
                    //TODO: validate against a regex
                    Snackbar.make(coordinatorLayout, "You must provide a password", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }else if(currentRequest.getFile() == null){
                    Snackbar.make(coordinatorLayout, "You must provide a profile image", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Signing up to DareÜ");
                progressDialog.show();

                //create a request
                currentRequest.setBirthday(birthday);
                currentRequest.setEmail(email);
                currentRequest.setName(name);
                currentRequest.setPassword(password);
                currentRequest.setUsername(username);

                SignupTask task = new SignupTask(getApplicationContext(), new AsyncTaskListener() {
                    @Override
                    public void onSuccess(String jsonText) {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onStatusCode(String jsonText, int statusCode) {
                        progressDialog.dismiss();
                    }
                }, currentRequest);
                task.execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            try{
                currentRequest.setFile(new URI(filePath));
            }catch(URISyntaxException ex){
                Log.i(TAG, "Could not get file path: " + ex.getMessage());
            }
        }
    }
}
