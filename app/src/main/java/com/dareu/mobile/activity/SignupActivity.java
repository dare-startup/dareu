package com.dareu.mobile.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.net.MultiparListener;
import com.dareu.mobile.net.SignupTask;
import com.dareu.mobile.net.request.SignupRequest;
import com.dareu.mobile.net.response.AuthenticationResponse;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity implements ActivityListener{

    private static final String TAG = "SignupActivity";
    private static final int GALLERY_REQUEST_CODE = 122;

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
        birthdayView = (TextView)findViewById(R.id.signupDateView);
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
                String birthday = currentRequest.getBirthday();

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
                }else if(currentRequest.getBitmap() == null){
                    Snackbar.make(coordinatorLayout, "You must provide a profile image", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                progressDialog = new ProgressDialog(SignupActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Signing up to DareÜ");
                progressDialog.show();

                //create a request
                currentRequest.setBirthday(birthday);
                currentRequest.setEmail(email);
                currentRequest.setName(name);
                currentRequest.setPassword(password);
                currentRequest.setUsername(username);

                SignupTask task = new SignupTask(SignupActivity.this, new MultiparListener() {
                    @Override
                    public void onResponse(int statusCode, String jsonResponse) {
                        if(statusCode == 500){
                            //show message
                            Snackbar.make(coordinatorLayout, "Something went wrong, try again", Snackbar.LENGTH_LONG)
                                    .show();
                        }else if(statusCode == 400){
                            //bad request
                            Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG)
                                    .show();
                        }else if(statusCode == 200){
                            //ok
                            AuthenticationResponse response = new Gson().fromJson(jsonResponse, AuthenticationResponse.class);
                            if(response != null){
                                //save token
                                SharedUtils.setStringPreference(SignupActivity.this, PrefName.SIGNIN_TOKEN, response.getToken());
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onError(String message) {

                    }
                }, currentRequest);
                task.execute();
            }
        });
        birthdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR) - 18;
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                System.out.println("the selected " + mDay);
                DatePickerDialog dialog = new DatePickerDialog(SignupActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String date = String.valueOf(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                currentRequest.setBirthday(date);
                                birthdayView.setText("Date of birth: " + date);
                            }
                        }, mYear, mMonth, mDay);
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);
                currentRequest.setBitmap(bitmap);
            } catch (IOException ex) {
                Log.e(TAG, "Could not load bitmap: " + ex.getMessage());
            }
        }
    }
}
