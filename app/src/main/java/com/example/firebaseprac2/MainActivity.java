package com.example.firebaseprac2;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button btnSignIn, btnSignUp;
    EditText editEmailforSignUp, editEmailforSignIn, editPWforSignUp, editPWforSignIn;
    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("message");
        initView();
        setListner();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String getID = FirebaseInstanceId.getInstance().getId();
        Log.d("MSG", "!!!token=" + getID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void setListner(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    public void signUp() {
        String email = editEmailforSignUp.getText().toString();
        // 특수문자 하나 이상, 8자리 이상 이런 조건 필요
        String password = editPWforSignUp.getText().toString();

        // validation check
        // 정규식
        if(!isValidEmail(email)){
            Toast.makeText(this, "이메일 형식이 잘못 되었습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidPassword(password)){
            Toast.makeText(this, "이메일 형식이 잘못 되었습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Authentification success [" + user.getUid() + "]",
                                    Toast.LENGTH_SHORT).show();

                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainActivity.this, "이메일을 발송하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            });
                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                            userRef.child(user.getUid()).setValue(refreshedToken);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentification failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_A-Za-z0-9-]+(.[_A-Za-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidPassword(String password) {
        boolean err = false;
        // 영문자와 숫자만 허용
        String regex = "^[0-9A-Za-z]{8,}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public void initView() {
        editEmailforSignIn = (EditText) findViewById(R.id.editEmailforSignIn);
        editEmailforSignUp = (EditText) findViewById(R.id.editEmailforSignUp);
        editPWforSignIn = (EditText) findViewById(R.id.editPWforSignIn);
        editPWforSignUp = (EditText) findViewById(R.id.editPWforSignUp);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
    }


    // 사용자 로그인
    public void signIn() {
        String email = editEmailforSignIn.getText().toString();
        // 특수문자 하나 이상, 8자리 이상 이런 조건 필요
        String password = editPWforSignIn.getText().toString();

        // validation check
        // 정규식
        if(!isValidEmail(email)){
            Toast.makeText(this, "이메일 형식이 잘못 되었습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidPassword(password)){
            Toast.makeText(this, "이메일 형식이 잘못 되었습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()){
                                Intent intent = new Intent(MainActivity.this, StorageActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            Toast.makeText(MainActivity.this, "Authentification success [" + user.getUid() + "]",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentification failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void getUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            boolean emailVerified = user.isEmailVerified(); //이메일 눌렀을 때만 true로 바뀜
            String uid = user.getUid();
        }
    }
}
