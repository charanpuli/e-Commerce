package com.example.charanpuli.shop_online;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charanpuli.shop_online.previlege.previlege;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullNameEdiT,userPhoneEditText,addressEditText;
    private TextView profileChangeTextBtn,closeTextBtn,saveTextBtn;

    private Uri imageUri;
    private String myUrl="";
    private StorageReference storageProfilePicRef;
    private String checker="";
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageView=(CircleImageView)findViewById(R.id.settings_profile_image);
        fullNameEdiT=(EditText)findViewById(R.id.settings_name);
        userPhoneEditText=(EditText)findViewById(R.id.settings_phonenumber);
        addressEditText=(EditText)findViewById(R.id.settings_address);

        profileChangeTextBtn=(TextView)findViewById(R.id.profile_image_change_btn);
        closeTextBtn=(TextView)findViewById(R.id.close_settings);
        saveTextBtn=(TextView)findViewById(R.id.update_account_settings_btn);

        userInfoDisplay(profileImageView,fullNameEdiT,userPhoneEditText,addressEditText);

        storageProfilePicRef=FirebaseStorage.getInstance().getReference().child("Profile Images");

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });
        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult Result=CropImage.getActivityResult(data);

            imageUri=Result.getUri();
            profileImageView.setImageURI(imageUri);

        }
        else
        {
            Toast.makeText(this, "Error..Try Again!!!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));

            finish();
        }
    }

    private void userInfoSaved()
    {
          if(TextUtils.isEmpty(userPhoneEditText.getText().toString()))
          {
              Toast.makeText(this, "Phone Number is Mandatory...", Toast.LENGTH_SHORT).show();
          }
        else if(TextUtils.isEmpty(fullNameEdiT.getText().toString()))
        {
            Toast.makeText(this, "Name is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Address is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            UploadImage();
        }
    }

    private void UploadImage()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait,We are updating your Profile INFO...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null)
        {
            final StorageReference fileRef=storageProfilePicRef
                    .child(previlege.CurrentOnlineUser.getPhone()+".jpg");

            uploadTask=fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                   if(!task.isSuccessful())
                   {
                       throw task.getException();
                   }
                   return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                  if(task.isSuccessful())
                  {
                      Uri downloadUrl=task.getResult();
                      myUrl=downloadUrl.toString();
                      DatabaseReference Ref=FirebaseDatabase.getInstance().getReference().child("Users");

                      HashMap<String,Object> userMap=new HashMap<>();

                      userMap.put("name",fullNameEdiT.getText().toString());
                      userMap.put("address",addressEditText.getText().toString());
                      userMap.put("PhoneOrder",userPhoneEditText.getText().toString());
                      userMap.put("image",myUrl);
                      Ref.child(previlege.CurrentOnlineUser.getPhone()).updateChildren(userMap);
                      progressDialog.dismiss();

                      startActivity(new Intent(SettingsActivity.this,MainActivity.class));
                      Toast.makeText(SettingsActivity.this, "Profile INFO updated successfully...", Toast.LENGTH_SHORT).show();
                      Toast.makeText(SettingsActivity.this, "Sign in again for getting updates done...", Toast.LENGTH_LONG).show();
                      finish();
                  }
                  else
                  {
                      progressDialog.dismiss();
                      Toast.makeText(SettingsActivity.this, "Error Occurred...", Toast.LENGTH_SHORT).show();
                  }
                }
            });

        }
        else
        {
            Toast.makeText(this, "Image is not selected...", Toast.LENGTH_SHORT).show();
        }

    }
    private void updateOnlyUserInfo()
    {
        DatabaseReference Ref=FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object> userMap=new HashMap<>();

        userMap.put("name",fullNameEdiT.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("PhoneOrder",userPhoneEditText.getText().toString());
        Ref.child(previlege.CurrentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile INFO updated successfully...", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEdiT, final EditText userPhoneEditText , final EditText addressEditText)
    {
        DatabaseReference UsersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(previlege.CurrentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists())
                    {
                        String image=dataSnapshot.child("image").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        String phone=dataSnapshot.child("phone").getValue().toString();
                        String address=dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);

                        fullNameEdiT.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
