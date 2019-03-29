package com.example.charanpuli.shop_online;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charanpuli.shop_online.Model.Users;
import com.example.charanpuli.shop_online.previlege.previlege;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputphoneno,inputpassword;
    private ProgressDialog LoadingBar;
  public static String data="Users";
    private TextView AdminLink,NotAdminLink;
    private com.rey.material.widget.CheckBox remember_me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button login_button = (Button) findViewById(R.id.login_btn);
        inputphoneno = (EditText) findViewById(R.id.login_phone_number_input);
        inputpassword = (EditText) findViewById(R.id.login_password_input);
        LoadingBar = new ProgressDialog(this);
        AdminLink=(TextView)findViewById(R.id.admin_panel_link);
        NotAdminLink=(TextView)findViewById(R.id.not_admin_panel_link);
        remember_me = (com.rey.material.widget.CheckBox) findViewById(R.id.remember_me_chk);
        Paper.init(this);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginaccount();
            }
        });

       AdminLink.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               login_button.setText("Admin Login");
               AdminLink.setVisibility(View.INVISIBLE);
               NotAdminLink.setVisibility(View.VISIBLE);
               data="Admins";
           }
       });
       NotAdminLink.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               login_button.setText("Login");
               AdminLink.setVisibility(View.VISIBLE);
               NotAdminLink.setVisibility(View.INVISIBLE);
               data="Users";
           }
       });
    }

    private void loginaccount() {

        String phone=inputphoneno.getText().toString();
        String password=inputpassword.getText().toString();
        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "enter your phone no.....", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "enter your password....", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoadingBar.setTitle("Login Account");
            LoadingBar.setMessage("Please wait,while we are checking for credentials");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            AllowAccesstoAccount(phone,password);
        }

    }

    private void AllowAccesstoAccount(final String phone, final String password) {
        if(remember_me.isChecked())
        {
           Paper.book().write(previlege.userphonekey,phone);
           Paper.book().write(previlege.userpasswordkey,password);
        }
        final DatabaseReference RootRef;
        RootRef=FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(data).child(phone).exists())
                {
                    Users userdata=dataSnapshot.child(data).child(phone).getValue(Users.class);

                    if(userdata.getPhone().equals(phone))
                    {
                        if(userdata.getPassword().equals(password))
                        {
                           if(data.equals("Admins"))
                           {
                               Toast.makeText(LoginActivity.this, "Logged in successfully as Admin.....", Toast.LENGTH_SHORT).show();
                               LoadingBar.dismiss();
                               Intent intent=new Intent(LoginActivity.this,AdminCategoryActivity.class);
                              startActivity(intent);
                           }
                           else if(data.equals("Users"))
                           {
                               Toast.makeText(LoginActivity.this, "Logged in successfully.....", Toast.LENGTH_SHORT).show();
                               LoadingBar.dismiss();
                               previlege.CurrentOnlineUser=userdata;
                               Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                               startActivity(intent);
                           }
                        }
                      else
                        {
                            LoadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect password...", Toast.LENGTH_SHORT).show();

                        }


                }
                }
                else if(data.equals("Users"))
                {    LoadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "There is no account on this "+phone+" Number", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);

                }
                else
                {   LoadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "You are presently not admin...", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}

