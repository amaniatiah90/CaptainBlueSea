package com.barmej.captain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText emailTextInputEditText;
    private TextInputEditText passwordTextInputEditText;
    private Button loginBt;
    private ProgressBar progressBar;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextInputLayout = findViewById(R.id.input_layout_email);
        passwordTextInputLayout = findViewById(R.id.input_layout_password);
        emailTextInputEditText = findViewById(R.id.edit_text_email);
        passwordTextInputEditText = findViewById(R.id.edit_text_password);
        loginBt = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.progress_bar);

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            hideForm(true);
            sendUser();
        }

    }

    private void loginClicked() {
        if(!isValidEmail(emailTextInputEditText.getText())) {
            emailTextInputLayout.setError(getString(R.string.invalid_email));
            return;
        }

        if (passwordTextInputEditText.getText().length() < 6) {
            passwordTextInputLayout.setError(getString(R.string.invalid_password));
            return;
        }
        hideForm(true);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailTextInputEditText.getText().toString(), passwordTextInputEditText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          //  String driverId = task.getResult().getUser().getUid();
                            sendUser();
                        } else {
                            hideForm(false);
                            Toast.makeText(LoginActivity.this,R.string.login_error,Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    private void hideForm(boolean hide) {
        if (hide) {
            progressBar.setVisibility(View.VISIBLE);
            passwordTextInputLayout.setVisibility(View.INVISIBLE);
            emailTextInputLayout.setVisibility(View.INVISIBLE);
            loginBt.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            passwordTextInputLayout.setVisibility(View.VISIBLE);
            emailTextInputLayout.setVisibility(View.VISIBLE);
            loginBt.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void sendUser() {
        startActivity(MainActivity.getStartIntent(LoginActivity.this));
        finish();

    }


}
