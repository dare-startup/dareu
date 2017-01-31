package com.dareu.mobile.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dareu.mobile.R;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.SigninTask;
import com.dareu.web.dto.request.SigninRequest;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.AuthenticationResponse;

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

                //hide keyboard
                SharedUtils.hideKeyboard(getCurrentFocus(), SigninActivity.this);

                //check internet connection
                if(! SharedUtils.checkInternetConnection(SigninActivity.this)){
                    SharedUtils.showNoInternetConnectionSnackbar(coordinatorLayout);
                    return;
                }

                progressDialog.setMessage("Signing in to " + getString(R.string.app_name));
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                //TODO: create request here
                SigninTask task = new SigninTask(SigninActivity.this, new SigninRequest(username, password), new AsyncTaskListener<AuthenticationResponse>() {


                    @Override
                    public void onTaskResponse(AuthenticationResponse response) {
                        if(response != null){
                            if(response.getToken() == null){
                                progressDialog.dismiss();
                                Snackbar.make(coordinatorLayout, "Bad credentials", Snackbar.LENGTH_LONG)
                                        .show();
                                return;
                            }
                            //save token
                            SharedUtils.setStringPreference(SigninActivity.this, PrefName.SIGNIN_TOKEN, response.getToken());
                            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                task.execute();
            }
        });
    }
}
