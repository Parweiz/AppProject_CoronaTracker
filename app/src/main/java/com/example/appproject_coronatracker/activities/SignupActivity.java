package com.example.appproject_coronatracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.appproject_coronatracker.R;

import java.util.regex.Pattern;

// reference: https://stackoverflow.com/questions/37859582/how-to-catch-a-firebase-auth-specific-exceptions
// resource: https://www.techiedelight.com/validate-password-java/
public class SignupActivity extends AppCompatActivity {

    String msg_valid_password = getResources().getString(R.string.msg_valid_password);
    String msg_invalid_password = getResources().getString(R.string.msg_invalid_password);

    EditText etEmail, etPassword, etRepassword;
    Button btnSignUp;
    TextView tvAlreadyUser;

    // Password validator
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

        etEmail = findViewById(R.id.et_email_signup);
        etPassword = findViewById(R.id.et_password_signup);
        etRepassword = findViewById(R.id.et_repassword_signup);
        btnSignUp = findViewById(R.id.btn_signup_signup);
        tvAlreadyUser = findViewById(R.id.tv_alreadyuser_signup);

    }
}
