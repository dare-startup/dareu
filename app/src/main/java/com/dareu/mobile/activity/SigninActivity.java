package com.dareu.mobile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.dareu.mobile.R;
import com.dareu.mobile.task.AsyncTaskListener;
import com.dareu.mobile.task.SigninTask;
import com.dareu.mobile.task.request.SigninRequest;
import com.dareu.mobile.task.response.AuthenticationResponse;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.google.gson.Gson;

public class SigninActivity extends AppCompatActivity implements ActivityListener{

    private static final String TAG = "SigninActivity";

    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog progressDialog;
    private EditText usernameView;
    private EditText passwordView;
    private Button signinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getComponents();
        initialize();
    }

    @Override
    public void getComponents() {
        usernameView = (EditText)findViewById(R.id.signinUsernameText);
        passwordView = (EditText)findViewById(R.id.signinPasswordText);
        signinButton = (Button)findViewById(R.id.signinButton);
        progressDialog = new ProgressDialog(SigninActivity.this);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
    }

    @Override
    public void initialize() {
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check values
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();

                if(username.isEmpty()){
                    Snackbar.make(coordinatorLayout, "Username should not be empty", Snackbar.LENGTH_LONG)
                            .show();
                    //focus on username field
                    if(usernameView.requestFocus())
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    return;
                }else if(password.isEmpty()){
                    Snackbar.make(coordinatorLayout, "Password should not be empty", Snackbar.LENGTH_LONG)
                            .show();
                    //focus on username field
                    if(passwordView.requestFocus())
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    return;
                }
                progressDialog.setMessage("Signing in to " + getString(R.string.app_name));
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                SigninTask task = new SigninTask(SigninActivity.this, new SigninRequest(username, password));
                task.setListener(new AsyncTaskListener() {
                    @Override
                    public void onSuccess(String jsonText) {
                        AuthenticationResponse response = new Gson().fromJson(jsonText, AuthenticationResponse.class);
                        if(response != null){
                            //save token
                            String token = response.getToken();
                            SharedUtils.setStringPreference(SigninActivity.this, PrefName.SIGNIN_TOKEN, token);

                            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            progressDialog.dismiss();

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onStatusCode(String jsonText, int statusCode) {
                        Log.e(TAG, "Got json " + jsonText + " with statusCode " + statusCode);
                        if(statusCode == 401){
                            //unauthorized
                            Snackbar.make(coordinatorLayout, "Username and/or password are incorrect", Snackbar.LENGTH_LONG)
                                    .show();
                        }else if(statusCode == 500){
                            //internal server error
                            Snackbar.make(coordinatorLayout, "Something went wrong, try again", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        progressDialog.dismiss();
                    }
                });
                task.execute();
            }
        });
    }
}
