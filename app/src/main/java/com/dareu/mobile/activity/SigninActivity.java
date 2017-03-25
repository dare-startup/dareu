package com.dareu.mobile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.web.dto.client.OpenClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.SigninRequest;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.AuthenticationResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.iid.FirebaseInstanceId;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity{

    private static final String TAG = "SigninActivity";

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.signinUsernameText)
    EditText usernameView;

    @BindView(R.id.signinPasswordText)
    EditText passwordView;

    @BindView(R.id.signinButton)
    Button signinButton;

    @BindView(R.id.signinGoogleButton)
    SignInButton signinGoogleButton;

    @BindView(R.id.signinForgotPassword)
    TextView forgotPassword;

    @BindView(R.id.signinEmailLayout)
    TextInputLayout emailLayout;

    @BindView(R.id.signinPasswordLayout)
    TextInputLayout passwordLayout;

    @BindView(R.id.signinCreateAccountText)
    TextView createAccountText;

    private ProgressDialog progressDialog;
    private OpenClientService openService;
    private SigninType currentSigninType = SigninType.LOCAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        openService = RetroFactory.getInstance()
                .create(OpenClientService.class);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.signinCreateAccountText)
    public void createAccountListener(){
        Intent intent = new Intent(this, SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }


    @OnClick(R.id.signinGoogleButton)
    public void googleSigninButtonListener(){
        Intent intent = SharedUtils.getGoogleSigninIntent(SigninActivity.this);
        startActivityForResult(intent, SharedUtils.GOOGLE_SIGNIN_REQUEST_CODE);
    }

    @OnClick(R.id.signinForgotPassword)
    public void forgotPasswordListener(){
        Intent intent = new Intent(SigninActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.fade_out);
    }


    @OnClick(R.id.signinButton)
    public void signInButtonListener(){
        //check values
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        if(username.isEmpty()){
            emailLayout.setError("Email should not be empty");
            //focus on username field
            if(usernameView.requestFocus())
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return;
        }else
            emailLayout.setError("");
        if(password.isEmpty()){
            passwordLayout.setError("Password should not be empty");
            //focus on username field
            if(passwordView.requestFocus())
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return;
        } else
            emailLayout.setError("");

        //hide keyboard
        SharedUtils.hideKeyboard(getCurrentFocus(), SigninActivity.this);

        //check internet connection
        switch(SharedUtils.checkInternetConnection(SigninActivity.this)){
            case NOT_CONNECTED:
                SharedUtils.showNoInternetConnectionSnackbar(coordinatorLayout, SigninActivity.this);
                break;
            default:
                SigninRequest request = new SigninRequest(username, password);
                String fcmToken = SharedUtils.getStringPreference(SigninActivity.this, PrefName.GCM_TOKEN);
                if(fcmToken == null || fcmToken.isEmpty())
                    request.setFcmToken(fcmToken);

                progressDialog.setTitle("Connecting");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                try{
                    Call<AuthenticationResponse> call = openService
                            .signin(request);
                    call.enqueue(new Callback<AuthenticationResponse>() {
                        @Override
                        public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                            switch(response.code()){
                                case 200:
                                    if(response.body().getToken() == null){
                                        progressDialog.dismiss();
                                        Snackbar.make(coordinatorLayout, "Bad credentials", Snackbar.LENGTH_LONG)
                                                .show();
                                        return;
                                    }
                                    //save token
                                    SharedUtils.setStringPreference(SigninActivity.this, PrefName.SIGNIN_TOKEN, response.body().getToken());
                                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.fade_out);
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Snackbar.make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });
                }catch(Exception ex){
                    Log.e(TAG, ex.getMessage());
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SharedUtils.GOOGLE_SIGNIN_REQUEST_CODE && resultCode == RESULT_OK){
            GoogleSignInResult result =  Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();

                createGoogleSigninRequest(account);
            }
        }
    }

    private void createGoogleSigninRequest(GoogleSignInAccount account) {
        SigninRequest request = new SigninRequest();
        request.setGoogleId(account.getId());
        request.setCurrentSigninType(SigninRequest.SigninType.GOOGLE);
        request.setUser(account.getEmail());
        String fcmToken = SharedUtils.getStringPreference(this, PrefName.GCM_TOKEN);

        if(fcmToken == null || fcmToken.isEmpty())
            fcmToken = FirebaseInstanceId.getInstance().getToken();

        request.setFcmToken(fcmToken);
        //create progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Connecting");
        progressDialog.setMessage("Signing in");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        openService.signin(request)
                .enqueue(new Callback<AuthenticationResponse>() {
                    @Override
                    public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                        switch(response.code()){
                            case 200:
                                if(response.body().getToken() == null){
                                    progressDialog.dismiss();
                                    Snackbar.make(coordinatorLayout, "Your Google account is not registered on our host, please sign up first", Snackbar.LENGTH_LONG)
                                            .show();
                                    return;
                                }
                                //save token
                                SharedUtils.setStringPreference(SigninActivity.this, PrefName.SIGNIN_TOKEN, response.body().getToken());
                                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_from_left, R.anim.fade_out);
                                break;
                            default:
                                progressDialog.dismiss();
                                Snackbar.make(coordinatorLayout, "Your Google account is not registered on our host, please sign up first", Snackbar.LENGTH_LONG)
                                        .show();

                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthenticationResponse> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
    }

    private enum SigninType{
        LOCAL, GOOGLE
    }
}
