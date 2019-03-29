package com.example.charanpuli.shop_online;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.charanpuli.shop_online.Model.Users;
import com.example.charanpuli.shop_online.previlege.previlege;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button loginbtn;
    private Button joinbtn;
    private ProgressDialog LoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinbtn = (Button) findViewById(R.id.main_join_now_btn);
        loginbtn = (Button) findViewById(R.id.main_login_btn);
        LoadingBar = new ProgressDialog(this);
        Paper.init(this);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
        String UserPhoneKey=Paper.book().read(previlege.userphonekey);
        String UserPasswordKey=Paper.book().read(previlege.userpasswordkey);


        if(UserPhoneKey!="" && UserPasswordKey!="") {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                AllowAccess(UserPhoneKey, UserPasswordKey);
                LoadingBar.setTitle("Already Logged in");
                LoadingBar.setMessage("Please wait.......");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();

            }
        }

    }
    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef=FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(phone).exists())
                {
                    Users userdata=dataSnapshot.child("Users").child(phone).getValue(Users.class);


                    if(userdata.getPhone().equals(phone))
                    {
                        if(userdata.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Logged in successfully.....", Toast.LENGTH_SHORT).show();
                            LoadingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            previlege.CurrentOnlineUser=userdata;
                            startActivity(intent);
                        }
                        else
                        {
                            LoadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Incorrect password...", Toast.LENGTH_SHORT).show();

                        }
                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this, "There is no acount on this "+phone+" Number", Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

