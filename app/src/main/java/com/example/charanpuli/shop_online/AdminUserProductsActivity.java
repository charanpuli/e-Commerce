package com.example.charanpuli.shop_online;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.charanpuli.shop_online.Model.Cart2;
import com.example.charanpuli.shop_online.ViewHolder.CartViewHolder2;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminUserProductsActivity extends AppCompatActivity {
    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;
    private String userID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        userID=getIntent().getStringExtra("uid");
        cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("Admin View").child(userID).child("Products");
        productsList=findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart2> options=new FirebaseRecyclerOptions.Builder<Cart2>()
                .setQuery(cartListRef,Cart2.class)
                .build();

        FirebaseRecyclerAdapter<Cart2,CartViewHolder2> adapter=new FirebaseRecyclerAdapter<Cart2, CartViewHolder2>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder2 holder2, int position, @NonNull Cart2 model)
            {
                holder2.txtProductName.setText("Name = "+model.getPname());
                holder2.txtProductPrice.setText("Price : Rs."+model.getPrice());
                holder2.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                Picasso.get().load(model.getImage()).into(holder2.cartProductImage);
            }

            @NonNull
            @Override
            public CartViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout2,viewGroup,false);
                CartViewHolder2 holder2=new CartViewHolder2(view);
                return holder2;
            }
        };
        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}
