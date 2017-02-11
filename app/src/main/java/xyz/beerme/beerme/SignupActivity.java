package xyz.beerme.beerme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Josh on 2/6/2017.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {


    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextConfirm;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceSate) {
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        buttonRegister = (Button) findViewById(R.id.btn_signup);
        editTextEmail = (EditText) findViewById(R.id.input_email);
        editTextPassword = (EditText) findViewById(R.id.input_password);
        editTextName = (EditText) findViewById(R.id.input_name);
        editTextConfirm = (EditText) findViewById(R.id.input_confirm);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if(validate())
            registerUser();
    }

    public boolean validate()
    {
        boolean valid = true;

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString();
        String confirm = editTextConfirm.getText().toString();

        if(name.isEmpty())
        {
            editTextName.setError("Please enter your name");
            valid = false;
        }

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
          editTextEmail.setError("Enter a valid email address");
          valid = false;
        }
        else
        {
            editTextEmail.setError(null);
        }

        if(!passwordPattern(password))
        {
            editTextPassword.setError("You must have at least 6 characters and one number and letter");
            valid = false;
        }
        else
        {
            editTextEmail.setError(null);
        }

        if(!confirm.equals(password))
        {
            editTextPassword.setError("Passwords do not match");
            valid = false;
        }

        return valid;
    }

    private void registerUser()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "Registration unsuccessful! :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static boolean passwordPattern(final String password)
    {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,128}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
