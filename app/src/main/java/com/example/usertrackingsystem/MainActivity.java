package com.example.usertrackingsystem;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    //Declaration EditTexts
    EditText editTextEmail;
    EditText editPassword;
    //Declaration TextInputLayout
    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPassword;

    //Declaration Button
    Button buttonLogin;

    //Declaration SQLiteHelper
    SqliteHelper sqliteHelper;

    private Session session;

    @RequiresApi(api = N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqliteHelper = new SqliteHelper(this);
        initCreateAccountTextView();
        initViews();

        session = new Session(this);
        //set click event of login button
        buttonLogin.setOnClickListener(view -> {
            //Check values from EditText fields
            String Email = editTextEmail.getText().toString();
            String Password = editPassword.getText().toString();

            //check input format
            validate();

            //Authenticate user
            User currentUser = sqliteHelper.Authenticate(new User(null, null, Email, Password));
            String user = sqliteHelper.getUsername(new User(null, null, Email, Password));
            session.setUsername(user);
            //Check Authentication is successful or not
            if(currentUser != null){
                //Launch the home screen on successful login
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }else{
                //User Logged in Failed
                Snackbar.make(buttonLogin, "Failed to login, please try again", Snackbar.LENGTH_LONG).show();
            }
        });


    }
    @RequiresApi(api = N)
    private void initCreateAccountTextView(){
        TextView textViewCreateAccount = findViewById(R.id.textViewCreateAccount);
        textViewCreateAccount.setText(fromHtml("<font color='#0000'>I don't have account yet. </font><font color='#0c0099'> create one</font>"));
        textViewCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    private void initViews(){
        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editPassword = findViewById(R.id.editTextPasswordLogin);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
    }

    //This method is for handling fromHtml method deprecation
    @RequiresApi(api = N)
    public static Spanned fromHtml(String html){
        Spanned result;

        if(SDK_INT >= N){
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }else {
            result = Html.fromHtml(html);
        }
        return result;
    }
    //This method is used to validate input given by user
    public void validate(){
        boolean valid = false;

        //Get values from EditText fields
        String Email = editTextEmail.getText().toString();
        String Password = editPassword.getText().toString();

        //Handling validation for Email field
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            textInputLayoutEmail.setError("Please enter valid email!");
        }else{
            textInputLayoutEmail.setError(null);
        }
        //Handling validation for password field
        if (Password.isEmpty()){
            textInputLayoutPassword.setError("Please enter valid password!");
        }else{
            if (Password.length()>5){
                textInputLayoutPassword.setError(null);
            }else{
                textInputLayoutPassword.setError("Password is too short!");
            }
        }
    }
}