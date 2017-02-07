package xyz.beerme.beerme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Josh on 2/6/2017.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {


    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceSate) {
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        buttonRegister = (Button) findViewById(R.id.register);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        registerUser();
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
                            Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(RegistrationActivity.this, "Registration unsuccessful! :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}