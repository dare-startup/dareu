package com.dareu.mobile.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.web.dto.client.OpenClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.GoogleSignupRequest;
import com.dareu.web.dto.request.SignupRequest;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.AuthenticationResponse;
import com.dareu.web.dto.response.ResourceAvailableResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.signupNameText)
    EditText nameText;

    @BindView(R.id.signupEmailText)
    EditText emailText;

    @BindView(R.id.signupPasswordText)
    EditText passwordText;

    @BindView(R.id.signupDateView)
    TextView birthdayView;

    @BindView(R.id.signupButton)
    Button signupButton;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.signupGPlusButton)
    SignInButton signupGPlusButton;

    @BindView(R.id.signoutNameLayout)
    TextInputLayout nameLayout;

    @BindView(R.id.signupEmailLayout)
    TextInputLayout emailLayout;

    @BindView(R.id.signupPasswordLayout)
    TextInputLayout passwordLayout;

    private ProgressDialog progressDialog;
    private final GoogleSignupRequest currentGoogleRequest = new GoogleSignupRequest();
    private final SignupRequest currentRequest = new SignupRequest();
    private OpenClientService openService;
    private SignupType signupType = SignupType.LOCAL;
    private boolean emailAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        openService = RetroFactory.getInstance()
                .create(OpenClientService.class);
        ButterKnife.bind(this);
        initialize();
    }


    @Override
    public void onBackPressed() {
        //if pressed, show a confirm dialog to exit
        new AlertDialog.Builder(SignupActivity.this)
                .setTitle("Cancel registration")
                .setMessage("Are you sure you want to quit registration?")
                .setPositiveButton("Yes, cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }


    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @OnClick(R.id.signupGPlusButton)
    public void googleSignupButtonListener(){
        Intent intent = SharedUtils.getGoogleSigninIntent(SignupActivity.this);
        startActivityForResult(intent, SharedUtils.GOOGLE_SIGNIN_REQUEST_CODE);
    }

    @OnFocusChange(R.id.signupEmailText)
    public void onEmailFocusChangedListener(boolean b){
        if(! b){
            openService.isEmailAvailable(emailText.getText().toString())
                    .enqueue(new Callback<ResourceAvailableResponse>() {
                        @Override
                        public void onResponse(Call<ResourceAvailableResponse> call, Response<ResourceAvailableResponse> response) {
                            switch(response.code()){
                                case 200:
                                    if(! response.body().isAvailable())
                                        emailLayout.setError("This email is already registered");
                                    else emailAvailable = true;
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResourceAvailableResponse> call, Throwable t) {

                        }
                    });
        }
    }

    @OnClick(R.id.signupButton)
    public void signupButtonListener(){
        //get values
        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String birthday;

        TextInputLayout layout = null;
        if(name.isEmpty()){
            nameLayout.setError("You must provide a name");
            return;
        }else{
            nameLayout.setError("");
        }
        if(email.isEmpty()){
            emailLayout.setError("You must provide an email");
            return;
        }else emailLayout.setError("");

        if(! emailAvailable){
            Snackbar.make(coordinatorLayout, "Email has already been registered", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        String regId = SharedUtils
                .getStringPreference(SignupActivity.this, PrefName.GCM_TOKEN);
        if(regId == null || regId.isEmpty())
            regId = FirebaseInstanceId.getInstance().getToken();
        switch(SharedUtils.checkInternetConnection(SignupActivity.this)){
            case NOT_CONNECTED:
                SharedUtils.showNoInternetConnectionSnackbar(coordinatorLayout, SignupActivity.this);
                break;
            default:
                switch(signupType){
                    case GOOGLE:
                        birthday = currentGoogleRequest.getBirthdate();
                        if(! SharedUtils.validateDate(birthday)){
                            Snackbar.make(coordinatorLayout, "You must provide a birth date", Snackbar.LENGTH_LONG)
                                    .show();
                            return;
                        }
                        //connected
                        progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppAlertDialog);
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Creating new account");
                        progressDialog.show();
                        //creates a new request
                        currentGoogleRequest.setFcm(regId);
                        openService.signupGoogle(currentGoogleRequest)
                                .enqueue(new Callback<AuthenticationResponse>() {
                                    @Override
                                    public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                                        if(response.body() == null){

                                        }else if(response.body().getToken() != null){
                                            SharedUtils.setStringPreference(SignupActivity.this, PrefName.SIGNIN_TOKEN, response.body().getToken());
                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_from_left, R.anim.fade_out);
                                        }else{
                                            Snackbar.make(coordinatorLayout, response.body().getMessage(), Snackbar.LENGTH_LONG)
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                                        Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG)
                                                .show();
                                    }
                                });
                        break;
                    case LOCAL:
                        //connected
                        if(password.isEmpty()){
                            passwordLayout.setError("You must provide a password");
                            return;
                        } else passwordLayout.setError("");
                        birthday = currentRequest.getBirthday();
                        if(! SharedUtils.validateDate(birthday)){
                            Snackbar.make(coordinatorLayout, "You must provide a birth date", Snackbar.LENGTH_LONG)
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
                        currentRequest.setFcm(regId);

                        Call<AuthenticationResponse> call = openService
                                .signup(currentRequest);
                        call.enqueue(new Callback<AuthenticationResponse>() {
                            @Override
                            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                                if(response.body() == null){

                                }else if(response.body().getToken() != null){
                                    SharedUtils.setStringPreference(SignupActivity.this, PrefName.SIGNIN_TOKEN, response.body().getToken());
                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.fade_out);
                                }else{
                                    Snackbar.make(coordinatorLayout, response.body().getMessage(), Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                                Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });
                        break;
                }
                break;
        }
    }

    @OnClick(R.id.signupDateView)
    public void birthDateListener(){
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
                        switch(signupType){
                            case LOCAL:
                                currentRequest.setBirthday(date);
                                break;
                            case GOOGLE:
                                currentGoogleRequest.setBirthdate(date);
                                break;
                        }
                        birthdayView.setText("Date of birth: " + date);
                    }
                }, mYear, mMonth, mDay);
        dialog.show();
    }

    public void initialize() {
        setGooglePlusButtonText(signupGPlusButton, "Sign up with Google");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Connecting");
        progressDialog.setMessage("Signing in to Google");
        progressDialog.show();
        if(requestCode == SharedUtils.GOOGLE_SIGNIN_REQUEST_CODE && resultCode == RESULT_OK){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();

                //load data here
                loadAccountData(account);
            }
        }
    }

    private void loadAccountData(GoogleSignInAccount account) {
        String email = account.getEmail();
        String name = account.getDisplayName();
        String googleId = account.getId();
        String imageUrl = account.getPhotoUrl().toString();

        nameText.setText(name);
        emailText.setText(email);
        //ask user to enter a password
        passwordLayout.setError("No password needed, proceed to registration");
        passwordText.setEnabled(false);
        progressDialog.dismiss();
        signupType = SignupType.GOOGLE;
        signupGPlusButton.setEnabled(false);

        //creates a new google sign up request
        currentGoogleRequest.setName(name);
        currentGoogleRequest.setEmail(email);
        currentGoogleRequest.setFcm(SharedUtils.getStringPreference(this, PrefName.GCM_TOKEN));
        currentGoogleRequest.setGoogleId(googleId);
        currentGoogleRequest.setImageUrl(imageUrl);

        //check email availability
        openService.isEmailAvailable(email)
                .enqueue(new Callback<ResourceAvailableResponse>() {
                    @Override
                    public void onResponse(Call<ResourceAvailableResponse> call, Response<ResourceAvailableResponse> response) {
                        switch(response.code()){
                            case 200:
                                if(! response.body().isAvailable()){
                                    emailAvailable = false;
                                    emailLayout.setError("This email is already registered");
                                }else emailAvailable = true;
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResourceAvailableResponse> call, Throwable t) {

                    }
                });
    }

    private enum SignupType{
        GOOGLE, LOCAL
    }
}
