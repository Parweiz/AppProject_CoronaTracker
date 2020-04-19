package com.example.appproject_coronatracker.activities;

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

import java.util.regex.Pattern;

// reference: https://stackoverflow.com/questions/37859582/how-to-catch-a-firebase-auth-specific-exceptions
// resource: https://www.techiedelight.com/validate-password-java/
public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = SignupActivity.class.getSimpleName();
    String msg_valid_password = getResources().getString(R.string.msg_valid_password);
    String msg_invalid_password = getResources().getString(R.string.msg_invalid_password);

    public static final String EMAIL_STRING = "emailstring";
    public static final String PASSWORD_STRING = "passwordstring";
    public static final String REPASSWORD_STRING = "repasswordstring";

    // Widgets
    EditText etEmail, etPassword, etRepassword;
    Button btnSignUp;
    TextView tvAlreadyUser;

    // Password validator setup
    // 4-8 character password requiring numbers and alphabets of both cases
    private static final String PASSWORD_REGEX =
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,8}$";

    // 4-32 character password requiring at least 3 out of 4 (uppercase
    // and lowercase letters, numbers & special characters) and at-most
    // 2 equal consecutive chars.
    private static final String COMPLEX_PASSWORD_REGEX =
            "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|" +
                    "(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|" +
                    "(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|" +
                    "(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})" +
                    "[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]" +
                    "{8,32}$";

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(PASSWORD_REGEX);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if(savedInstanceState != null ){
            etEmail.setText(savedInstanceState.getString(EMAIL_STRING));
            etPassword.setText(savedInstanceState.getString(PASSWORD_STRING));
            etRepassword.setText(savedInstanceState.getString(REPASSWORD_STRING));
        }

        etEmail = findViewById(R.id.et_email_signup);
        etPassword = findViewById(R.id.et_password_signup);
        etRepassword = findViewById(R.id.et_repassword_signup);
        btnSignUp = findViewById(R.id.btn_signup_signup);
        tvAlreadyUser = findViewById(R.id.tv_alreadyuser_signup);

    }

    @Override
    public void onClick(View view) {
        if(view == tvAlreadyUser)switchToLoginActivity();
        if(view == btnSignUp)signUpButtonPressed();
    }

    private void signUpButtonPressed() {
        String email = etEmail.getText().toString();
        String pword = etPassword.getText().toString();
        String repword = etRepassword.getText().toString();
        
        if(!hasUserFilledOutFormCorrectly(email, pword, repword)) return;
    }

    private boolean hasUserFilledOutFormCorrectly(String email, String pword, String repword) {
        String temp_email = email;
        String temp_pword = pword;
        String temp_repword = repword;

        if(temp_email.isEmpty()){
            Toast.makeText(this, R.string.error_form_field_must_be_filled, Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return false;
        }

        if(temp_pword.isEmpty()){
            Toast.makeText(this, R.string.error_form_field_must_be_filled, Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return false;
        }

        if(temp_repword.isEmpty()){
            Toast.makeText(this, R.string.error_form_field_must_be_filled, Toast.LENGTH_SHORT).show();
            etRepassword.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(temp_email).matches()){
            Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return false;
        }

        if(!pword.equals(repword)){
            Toast.makeText(this, R.string.passwords_not_matching, Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etRepassword.setText("");
            etRepassword.requestFocus();
            return false;
        }

        if(!PASSWORD_PATTERN.matcher(temp_pword).matches()){
            Toast.makeText(this, R.string.msg_invalid_password, Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etRepassword.setText("");
            etRepassword.requestFocus();
            return false;
        }

        return true;
    }

    private void switchToLoginActivity() {
        Log.d(TAG, "Switching to login activity");
        clearFields();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void clearFields() {
        Log.d(TAG, "Clearing fields");
        etEmail.setText("");
        etPassword.setText("");
        etRepassword.setText("");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EMAIL_STRING, etEmail.getText().toString());
        savedInstanceState.putString(PASSWORD_STRING, etPassword.getText().toString());
        savedInstanceState.putString(REPASSWORD_STRING, etRepassword.getText().toString());
    }
}
