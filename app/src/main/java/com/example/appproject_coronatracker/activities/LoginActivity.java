package com.example.appproject_coronatracker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appproject_coronatracker.R;
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
    Button btnLogin;
    TextView tvNotUserYet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(savedInstanceState != null ){
            etEmail.setText(savedInstanceState.getString(EMAIL_STRING));
            etPassword.setText(savedInstanceState.getString(PASSWORD_STRING));
        }

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);
        btnLogin = findViewById(R.id.btn_login_login);
        tvNotUserYet = findViewById(R.id.tv_notauseryet_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build());
                        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onClick(View view){
        if(view == tvNotUserYet)switchToSignupActivity();
        if(view == btnLogin)loginButtonPressed();
    }

    private void loginButtonPressed() {
        String email = etEmail.getText().toString();
        String pword = etPassword.getText().toString();

        if(hasUserFilledOutFormCorrectly(email, pword) == false) return;

        mAuth.signInWithEmailAndPassword(email, pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), getString(R.string.welcome_login), Toast.LENGTH_SHORT).show();
                    clearFields();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class); // TODO: Change to MenuActivity - Main chosen temp. for test purposes
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
        String temp_email = email;
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
        startActivity(new Intent(this, SignupActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                Toast.makeText(getApplicationContext(), getString(R.string.welcome_login), Toast.LENGTH_SHORT).show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, user.getUid()); // print userid if succeeded.
            } else
                Log.d(TAG, response.getError().getMessage());
        }
    }

    private void clearFields(){
        Log.d(TAG, "Clearing fields");
        etEmail.setText("");
        etPassword.setText("");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EMAIL_STRING, etEmail.getText().toString());
        savedInstanceState.putString(PASSWORD_STRING, etPassword.getText().toString());
    }
}
