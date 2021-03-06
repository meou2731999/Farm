package com.example.farm.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farm.model.MainActivity;
import com.example.farm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText edtpassword, edtuserName;
    TextView showPass;
    CheckBox cbSave;
    private ActionBar toolbar;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    boolean showPassFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mapping();

        /* truy cập username và password đã lưu */
        sharedPreferences = getSharedPreferences("dataSignin",MODE_PRIVATE);
        edtuserName.setText(sharedPreferences.getString("email",""));
        edtpassword.setText(sharedPreferences.getString("password",""));
        cbSave.setChecked(sharedPreferences.getBoolean("cbCheck",false));

        toolbar = getSupportActionBar();
        toolbar.setTitle("Đăng nhập");

        /* Chuyển activity SignIn -> Main nếu đăng nhập thành công*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtuserName.getText().toString();
                String password = edtpassword.getText().toString();

                if (email.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Hãy điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Đang đăng nhập...");
                    progressDialog.show();

                    /* Lưu username, password */
                    if (cbSave.isChecked()){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email",email);
                        editor.putString("password",password);
                        editor.putBoolean("cbCheck",true);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("email");
                        editor.remove("password");
                        editor.remove("cbCheck");
                        editor.commit();
                    }
                    startLogin(email, password);
                }
            }
        });

        /* Show Password */
        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPassFlag == false){
                    edtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassFlag = true;
                } else {
                    edtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassFlag = false;
                }
            }
        });

//        /* ------------------ Quên mật khẩu -------------- */
//        resetPass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (edtuserName.getText().toString().equals("")) {
//                    Toast.makeText(LoginActivity.this, "Điền email của bạn vào để nhận mật khẩu mới nhé!", Toast.LENGTH_SHORT).show();
//                } else {
//                    FirebaseAuth auth = FirebaseAuth.getInstance();
//                    auth.sendPasswordResetEmail(edtuserName.getText().toString())
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        Log.d("RESET","Email sent.");
//                                        Toast.makeText(LoginActivity.this, "Kiểm tra email của bạn để đặt lại mật khẩu.", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                }
//            }
//        });
    }

    private void startLogin(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mapping() {
        cbSave          = (CheckBox)    findViewById(R.id.signin_cbSave);
        btnLogin        = (Button)      findViewById(R.id.signin_btnSignin);
        edtpassword     = (EditText)    findViewById(R.id.signup_edtPassword);
        edtuserName     = (EditText)    findViewById(R.id.signin_edtUserName);
        showPass        = (TextView)    findViewById(R.id.signin_tvShowPassword);
        toolbar         = getSupportActionBar();
        progressDialog  = new ProgressDialog(this);
        mAuth           = FirebaseAuth.getInstance();
        mDatabase       = FirebaseDatabase.getInstance().getReference().child("Users");
    }

}