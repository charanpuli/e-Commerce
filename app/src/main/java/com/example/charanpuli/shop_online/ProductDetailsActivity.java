package com.example.charanpuli.shop_online;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.charanpuli.shop_online.Model.Products;
import com.example.charanpuli.shop_online.previlege.previlege;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

   // private FloatingActionButton addToCartBtn;
    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productName,productDescription,productPrice;
    private String productID="";
    private String imageId="",state="Normal";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        productID=getIntent().getStringExtra("pid");




        setContentView(R.layout.activity_product_details);
        addToCartBtn=(Button)findViewById(R.id.pd_add_to_cart_btn);
       //  addToCartBtn=(FloatingActionButton)findViewById(R.id.add_product_to_cart_btn);
        numberButton=(ElegantNumberButton)findViewById(R.id.number_btn);
        productImage=(ImageView)findViewById(R.id.product_image_details);
        productName=(TextView)findViewById(R.id.product_name_details);
        productDescription=(TextView)findViewById(R.id.product_description_details);
        productPrice=(TextView)findViewById(R.id.product_price_details);


        getProductDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state.equals("Order Placed")||state.equals("Order Shipped"))
                {
                    Toast.makeText(ProductDetailsActivity.this, "You can order more products,Once your order is placed or shipped", Toast.LENGTH_LONG).show();
                }
                else
                {
                    addingToCartList();
                }
            }
        });
    }
    private void getProductDetails(final String productID)
    {
        DatabaseReference productsRef=FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Products products=dataSnapshot.getValue(Products.class);
                    imageId=products.getImage();
                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    private void addingToCartList()
    {   String saveCurrentDate,saveCurrentTime;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=simpleDate.format(calendar.getTime());
        SimpleDateFormat simpleTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=simpleTime.format(calendar.getTime());

        final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap=new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("description",productDescription.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");
        cartMap.put("image",imageId);

        cartListRef.child("User View").child(previlege.CurrentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    cartListRef.child("Admin View").child(previlege.CurrentOnlineUser.getPhone())
                            .child("Products").child(productID)
                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ProductDetailsActivity.this, "Added to Cart Succesfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });



    }
    private void CheckOrderState(){
        DatabaseReference ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(previlege.CurrentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String shippingState=dataSnapshot.child("state").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                       state="Order Shipped";
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                       state="Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
