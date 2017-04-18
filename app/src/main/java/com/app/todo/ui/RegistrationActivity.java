package com.app.todo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;

import com.app.todo.R;
import com.app.todo.model.UserInfoModel;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatEditText edittextName, edittextemail, edittextpswrd, edittextmobNo;
    AppCompatButton buttonSave;
    AppCompatTextView textView;
    Pattern pattern, pattern2;
    Matcher matcher, matcher2;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        initView();

    }

    public void initView() {
        buttonSave = (AppCompatButton) findViewById(R.id.save_button);
        edittextName = (AppCompatEditText) findViewById(R.id.name_edittext);
        edittextemail = (AppCompatEditText) findViewById(R.id.email_Edittext);
        edittextpswrd = (AppCompatEditText) findViewById(R.id.password_edittext);
        edittextmobNo = (AppCompatEditText) findViewById(R.id.mobilenumber_edittext);
        textView = (AppCompatTextView) findViewById(R.id.allreadyacc_textview);
        setClicklistener();
    }

    public void setClicklistener() {
        buttonSave.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                registerUser();

                break;

            case R.id.allreadyacc_textview:
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                break;
        }
    }

    public void registerUser() {

        pattern = Pattern.compile(Constants.Password_Pattern);
        matcher = pattern.matcher(edittextpswrd.getText().toString());
        pattern2 = Pattern.compile(Constants.Mobile_Pattern);
        matcher2 = pattern2.matcher(edittextmobNo.getText().toString());
        final UserInfoModel userInfoModel = new UserInfoModel();
        final String userName = edittextName.getText().toString();
        final String userEmail = edittextemail.getText().toString();
        final String userPassword = edittextpswrd.getText().toString();
        final String userMobileNo = edittextmobNo.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Name, userInfoModel.getName());
        bundle.putString(Constants.Email, userInfoModel.getEmail());
        bundle.putString(Constants.Password, userInfoModel.getPassword());
        bundle.putString(Constants.MobileNo, String.valueOf(userInfoModel.getMobile()));
       /* userInfoModel.setName(Name);
        userInfoModel.setEmail(Email);
        userInfoModel.setPassword(Password);
        userInfoModel.setMobile(MobileNo);*/
        if (TextUtils.isEmpty(userName)) {
            edittextName.setError("field should'nt be empty");
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            edittextemail.setError("field should'nt be empty");
            return;

        } else if (!isValidEmail(userEmail)) {
            edittextemail.setError("Invalid Format");
        }
        if (TextUtils.isEmpty(userPassword)) {
            edittextpswrd.setError("field should'nt be empty");
            return;
        } else if (matcher.matches()) {

        } else {
            edittextpswrd.setError("wrong format");
            edittextpswrd.requestFocus();
        }
        if (TextUtils.isEmpty(userMobileNo)) {
            edittextmobNo.setError("field should'nt be empty");
            return;
        } else if (matcher2.matches()) {

        } else {
            edittextmobNo.setError("wrong format");
            edittextmobNo.requestFocus();
        }

        progressDialog.setMessage("Registering data plz wait...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //finish();
                    //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    userInfoModel.setName(userName);
                    userInfoModel.setEmail(userEmail);
                    userInfoModel.setPassword(userPassword);
                    userInfoModel.setMobile(userMobileNo);
                    String id = databaseReference.push().getKey();
                    databaseReference.child(id).setValue(userInfoModel);
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    //Toast.makeText(RegistrationActivity.this, "Registration error...", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                    builder.setMessage(task.getException().getMessage())
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                progressDialog.dismiss();
            }
        });


        /* boolean checkName = false, checkMail = false, checkPassword = false, checkMobNo = false;
        if (Name.isEmpty()) {
            edittextName.setError(getString(R.string.first_name));
        } else {
            checkName = true;
        }

        if (Email.isEmpty()) {
            edittextemail.setError(getString(R.string.email_field_condition));
        } else if (!isValidEmail(Email)) {
            edittextemail.setError(getString(R.string.invalid_mail));
        } else {
            checkMail = true;
        }


        if (Password.isEmpty()) {
            edittextpswrd.setError(getString(R.string.password_field_condition));
            edittextpswrd.requestFocus();
        } else if (matcher.matches()) {
            checkPassword = true;
        } else {
            edittextpswrd.setError(getString(R.string.format_invalid));
            edittextpswrd.requestFocus();

        }


        if (MobileNo.isEmpty()) {
            edittextmobNo.setError(getString(R.string.enter_mobile));
        } else {
            checkMobNo = true;
        }
*/

        /*if (checkName && checkMail && checkMobNo && checkPassword) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);

            SharedPreferences.Editor shEditor = sharedPreferences.edit();
            shEditor.putString("Username", edittextName.getText().toString());
            shEditor.putString("email", edittextemail.getText().toString());
            shEditor.putString("password", edittextpswrd.getText().toString() + "");
            shEditor.putString("mobileno", edittextmobNo.getText().toString());
            shEditor.commit();
            Toast.makeText(getApplicationContext(), getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }*/
    }

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}



