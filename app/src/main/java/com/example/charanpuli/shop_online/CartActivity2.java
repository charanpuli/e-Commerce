package com.example.charanpuli.shop_online;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charanpuli.shop_online.Model.Cart2;
import com.example.charanpuli.shop_online.ViewHolder.CartViewHolder2;
import com.example.charanpuli.shop_online.previlege.previlege;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CartActivity2 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount,txtMsg1;

    private int overTotalPrice=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart2);

        recyclerView=findViewById(R.id.cart_list2);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn=(Button)findViewById(R.id.next_process_btn2);
        txtTotalAmount=(TextView)findViewById(R.id.total_price2);
        txtMsg1=(TextView)findViewById(R.id.msg1);
        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTotalAmount.setText("Total Price = Rs."+String.valueOf(overTotalPrice));
                Intent intent=new Intent(CartActivity2.this,ConformFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                Toast.makeText(CartActivity2.this, "Your Total Price is : Rs."+String.valueOf(overTotalPrice), Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
        final DatabaseReference cartListRef2=FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart2> options=new FirebaseRecyclerOptions.Builder<Cart2>()
                .setQuery(cartListRef2.child("User View")
                        .child(previlege.CurrentOnlineUser.getPhone())
                        .child("Products"),Cart2.class)
                .build();
        FirebaseRecyclerAdapter<Cart2, CartViewHolder2> adapter=new FirebaseRecyclerAdapter<Cart2, CartViewHolder2>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder2 holder2, int position, @NonNull final Cart2 model)
            {
                holder2.txtProductName.setText("Name = "+model.getPname());
                holder2.txtProductPrice.setText("Price : Rs."+model.getPrice());
                holder2.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                Picasso.get().load(model.getImage()).into(holder2.cartProductImage);
                int oneTypeProductTPrice=(Integer.valueOf(model.getPrice())) * (Integer.valueOf(model.getQuantity()));
                    overTotalPrice=overTotalPrice+oneTypeProductTPrice;

                holder2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[]=new CharSequence[]{
                                "Edit",
                                "Remove"
                        };

                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity2.this);

                        builder.setTitle("Cart Options :");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(which==0)
                                {
                                    Intent intent=new Intent(CartActivity2.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if(which==1)
                                {   cartListRef2.child("Admin View")
                                        .child(previlege.CurrentOnlineUser.getPhone())
                                        .child("Products")
                                        .child(model.getPid())
                                        .removeValue();
                                    cartListRef2.child("User View")
                                            .child(previlege.CurrentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                  if(task.isSuccessful())
                                                  {

                                                      Toast.makeText(CartActivity2.this, "Removed from cart successfully...", Toast.LENGTH_SHORT).show();
                                                       Intent intent=new Intent(CartActivity2.this,HomeActivity.class);
                                                        startActivity(intent);
                                                  }
                                                }
                                            });
                                }

                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout2,parent,false);
                CartViewHolder2 holder2=new CartViewHolder2(view);
                return holder2;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                    String userName=dataSnapshot.child("name").getValue().toString();
                    if(shippingState.equals("shipped"))
                    {
                        txtTotalAmount.setText("Dear "+userName+" \n" + "Order is shipped successfully");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);

                        nextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity2.this, "You can purchase more orders ,Once you receive your order", Toast.LENGTH_SHORT).show();
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        txtTotalAmount.setText("Shipping State = Not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);

                        nextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity2.this, "You can purchase more orders ,Once you receive your order", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
