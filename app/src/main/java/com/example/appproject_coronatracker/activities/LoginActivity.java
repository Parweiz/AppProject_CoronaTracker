package com.example.appproject_coronatracker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.service.CoronaTrackerService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

// resource: SMAP L10: Mobile Backends (w/ Firebase)
// resource: https://stackoverflow.com/questions/16812039/how-to-check-valid-email-format-entered-in-edittext
// resource: https://www.techiedelight.com/validate-password-java/
// resource: https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RC_SIGN_IN = 1337;
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String EMAIL_STRING = "emailstring";
    public static final String PASSWORD_STRING = "passwordstring";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // Widgets
    EditText etEmail, etPassword;
    Button btnLogin, btnExit;
    TextView tvNotUserYet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);
        btnLogin = findViewById(R.id.btn_login_login);
        btnExit = findViewById(R.id.btn_exit_login);
        tvNotUserYet = findViewById(R.id.tv_notauseryet_login);

        if(savedInstanceState != null ){
            etEmail.setText(savedInstanceState.getString(EMAIL_STRING));
            etPassword.setText(savedInstanceState.getString(PASSWORD_STRING));
        }

        if(mUser != null){
            Log.d(TAG, "User is already signed in - going directly to MenuActivity");
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        }

        btnLogin.setOnClickListener(this);
        tvNotUserYet.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, CoronaTrackerService.class);
        startService(intent);
    }

    @Override
    public void onClick(View view){
        if(view == tvNotUserYet)switchToSignupActivity();
        if(view == btnLogin)loginButtonPressed();
        if (view == btnExit) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void loginButtonPressed() {
        String email = etEmail.getText().toString();
        String pword = etPassword.getText().toString();

        // validate input
        if(!hasUserFilledOutFormCorrectly(email, pword)) return;

        mAuth.signInWithEmailAndPassword(email, pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), getString(R.string.welcome_login), Toast.LENGTH_SHORT).show();
                    clearFields();
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP); // prevent going back + prevent issues with double-tap
                    startActivity(intent);
                } else{
                    // TODO: "One account per email address" setting must be enabled in the Firebase console (recommended).
                    // TODO: Implement "I'm not a robot"-verification
                    Toast.makeText(getApplicationContext(), "Oops, something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                    clearFields();
                    etEmail.requestFocus();
                }
            }
        });
    }

    private boolean hasUserFilledOutFormCorrectly(String email, String pword) {
        String temp_email = email; // TODO: remove redundancies..
        String temp_pword = pword;
        String error_userHasNotFilledOutField = getResources().getString(R.string.error_form_field_must_be_filled);
        String error_invalid_email = getResources().getString(R.string.error_invalid_email);

        if(temp_email.isEmpty()){
            Toast.makeText(this, error_userHasNotFilledOutField, Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return false;
        }

        if(temp_pword.isEmpty()){
            Toast.makeText(this, error_userHasNotFilledOutField, Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(temp_email).matches()){
            Toast.makeText(this, error_invalid_email, Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void switchToSignupActivity() {
        Log.d(TAG, "Switching to sign up activity");
        clearFields();
        startActivity(new Intent(this, SignupActivity.class));
    }

    private void clearFields(){
        Log.d(TAG, "Clearing fields");
        etEmail.setText("");
        etPassword.setText("");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EMAIL_STRING, etEmail.getText().toString());
        savedInstanceState.putString(PASSWORD_STRING, etPassword.getText().toString());
    }
}
