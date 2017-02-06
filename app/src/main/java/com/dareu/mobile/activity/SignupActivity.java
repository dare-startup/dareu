package com.dareu.mobile.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.SignupTask;
import com.dareu.web.dto.request.SignupRequest;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.AuthenticationResponse;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity implements ActivityListener{

    private static final String TAG = "SignupActivity";

    private EditText nameText, emailText, passwordText;
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
        new AlertDialog.Builder(SignupActivity.this)
                .setTitle("Cancel registration")
                .setMessage("Are you sure you want to quit registration?")
                .setPositiveButton("Yes, cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    @Override
    public void getComponents() {
        nameText = (EditText)findViewById(R.id.signupNameText);
        emailText = (EditText)findViewById(R.id.signupEmailText);
        passwordText = (EditText)findViewById(R.id.signupPasswordText);
        signupButton = (Button)findViewById(R.id.signupButton);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        birthdayView = (TextView)findViewById(R.id.signupDateView);
    }

    @Override
    public void initialize() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get values
                String name = nameText.getText().toString();
                String email = emailText.getText().toString();
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
                }else if(password.isEmpty()){
                    //TODO: validate against a regex
                    Snackbar.make(coordinatorLayout, "You must provide a password", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if(! SharedUtils.checkInternetConnection(SignupActivity.this)){
                    SharedUtils.showNoInternetConnectionSnackbar(coordinatorLayout);
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


                SignupTask task = new SignupTask(SignupActivity.this, currentRequest, new AsyncTaskListener<AuthenticationResponse>() {
                    @Override
                    public void onTaskResponse(AuthenticationResponse response) {
                        if(response == null){

                        }else if(response.getToken() != null){
                            SharedUtils.setStringPreference(SignupActivity.this, PrefName.SIGNIN_TOKEN, response.getToken());
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else{
                            Snackbar.make(coordinatorLayout, response.getMessage(), Snackbar.LENGTH_LONG)
                                    .show();
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
}
