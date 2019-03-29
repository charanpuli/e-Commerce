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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    private String categoryname,downloadimageurl;
    private Button addnewproduct;
    private EditText inputproductname,inputproductdescription,inputproductprice;
    private ImageView inputproductimage;
    private static final int gallerypick=1;
    private Uri imageuri;
    private String pname,description,price,savecurrentdate,savecurrenttime,productrandomkey;
    private StorageReference ProductImageRef;
    private DatabaseReference productref;
    ProgressDialog Loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryname=getIntent().getExtras().get("categories").toString();
        ProductImageRef=FirebaseStorage.getInstance().getReference().child("Product Images");
        Toast.makeText(this, categoryname, Toast.LENGTH_SHORT).show();

        addnewproduct=(Button)findViewById(R.id.add_new_product);
        inputproductname=(EditText)findViewById(R.id.product_name);
        inputproductdescription=(EditText)findViewById(R.id.product_description);
        inputproductprice=(EditText)findViewById(R.id.product_price);
        inputproductimage=(ImageView)findViewById(R.id.select_product_image);
        productref=FirebaseDatabase.getInstance().getReference().child("Products");
        Loadingbar=new ProgressDialog(this);
        inputproductimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });
        addnewproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });


    }


   private void openGallery() {
        Intent galleryintent=new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,gallerypick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallerypick && resultCode==RESULT_OK && data!=null)
        {
             imageuri=data.getData();
             inputproductimage.setImageURI(imageuri);
        }
    }
    private void ValidateProductData() {
       pname=inputproductname.getText().toString();
        description=inputproductdescription.getText().toString();
        price=inputproductprice.getText().toString();
        if(imageuri==null)
        {
            Toast.makeText(this, "Product Image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pname))
        {
            Toast.makeText(this, "Provide Product name..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Provide Product Description..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price))
        {
            Toast.makeText(this, "Provide Product Price..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }



    }

    private void StoreProductInformation() {
        Loadingbar.setTitle("Adding new Product");
        Loadingbar.setMessage("Please wait,while we are adding new product");
        Loadingbar.setCanceledOnTouchOutside(false);
        Loadingbar.show();
        Calendar calender=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdate=currentdate.format(calender.getTime());
        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss a");
        savecurrenttime=currenttime.format(calender.getTime());
        productrandomkey=savecurrentdate+savecurrenttime;

       final StorageReference filepath;
       ///////////////////////////////////////////////////
        filepath = ProductImageRef.child(imageuri.getLastPathSegment() + productrandomkey + ".jpg");

        final UploadTask uploadTask=filepath.putFile(imageuri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg=e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error : "+msg, Toast.LENGTH_SHORT).show();
                Loadingbar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urltask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        downloadimageurl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {   downloadimageurl=task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Got Image URL successfully...", Toast.LENGTH_SHORT).show();
                            saveproductInfotoDatabase();
                        }

                    }
                });

            }

        });

      }

    private void saveproductInfotoDatabase() {
        HashMap<String,Object> productmap=new HashMap<>();
        productmap.put("pid",productrandomkey);
        productmap.put("date",savecurrentdate);
        productmap.put("time",savecurrenttime);
        productmap.put("description",description);
        productmap.put("image",downloadimageurl);
        productmap.put("category",categoryname);
        productmap.put("price",price);
        productmap.put("pname",pname);
        productref.child(productrandomkey).updateChildren(productmap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {    Intent intent=new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                    startActivity(intent);
                    Loadingbar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully...", Toast.LENGTH_SHORT).show();
                }
                else
                {    Loadingbar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}



