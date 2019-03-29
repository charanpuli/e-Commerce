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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button createaccountbutton;
    private EditText inputusername,inputphoneno,inputpassword;
    private ProgressDialog LoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createaccountbutton=(Button)findViewById(R.id.register_btn);
        inputusername=(EditText)findViewById(R.id.register_username_input);
        inputphoneno=(EditText)findViewById(R.id.register_phone_number_input);
        inputpassword=(EditText)findViewById(R.id.register_password_input);
        LoadingBar=new ProgressDialog(this);

        createaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createaccount();
            }
        });


    }

    private void createaccount() {
        String name=inputusername.getText().toString();
        String phone=inputphoneno.getText().toString();
        String password=inputpassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "enter your name....", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "enter your phone no.....", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "enter your password....", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoadingBar.setTitle("Create Account");
            LoadingBar.setMessage("Please wait,while we are checking for credentials");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            ValidatePhonenumber(name,phone,password);
        }

    }

    private void ValidatePhonenumber(final String name, final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef=FirebaseDatabase.getInstance().getReference();
     RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             //////////////FOR ADMINS OR USERS /////////////////////
             if(!(dataSnapshot.child("Users").child(phone).exists()))
             {
                 HashMap<String,Object> userdataMap=new HashMap<>();
                 userdataMap.put("phone",phone);
                 userdataMap.put("password",password);
                 userdataMap.put("name",name);
                 RootRef.child("Users").child(phone).updateChildren(userdataMap)
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful())
                         {
                             Toast.makeText(RegisterActivity.this, "Accoount created successfully...", Toast.LENGTH_SHORT).show();
                             LoadingBar.dismiss();
                             Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                             startActivity(intent);

                         }
                         else
                         {
                             LoadingBar.dismiss();
                             Toast.makeText(RegisterActivity.this, "Network Error !! Please try again...", Toast.LENGTH_SHORT).show();
                         }

                     }
                 });

             }
             else
             {
                 Toast.makeText(RegisterActivity.this, "This"+phone+" already exists ..", Toast.LENGTH_SHORT).show();
                 LoadingBar.dismiss();
                 Toast.makeText(RegisterActivity.this, "Try with another phone number...", Toast.LENGTH_SHORT).show();
                 Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                 startActivity(intent);
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });
    }

}
