package com.dareu.mobile.activity;

import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dareu.mobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {


    @BindView(R.id.forgotPasswordEmailLayout)
    TextInputLayout emailLayout;

    @BindView(R.id.forgotPasswordEmailText)
    EditText emailtext;

    @BindView(R.id.forgotPasswordButton)
    Button sendButton;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.forgotPasswordButton)
    public void sendButtonListener(){
        Snackbar.make(layout, "This feature does not work yet (need to implement server side)", Snackbar.LENGTH_INDEFINITE)
                .show();
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setTitle("Cancel password reset")
                .setMessage("Want to cancel?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
