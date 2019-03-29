package com.example.charanpuli.shop_online.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.charanpuli.shop_online.Interface.ItemClickListener;
import com.example.charanpuli.shop_online.R;

public class CartViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName,txtProductPrice,txtProductQuantity;
    public ImageView cartProductImage;
    private ItemClickListener itemClickListener;

    public CartViewHolder2(@NonNull View itemView)
    {
        super(itemView);
        txtProductName=itemView.findViewById(R.id.cart_product_name2);
        txtProductPrice=itemView.findViewById(R.id.cart_product_price2);
        txtProductQuantity=itemView.findViewById(R.id.cart_product_quantity2);
        cartProductImage=itemView.findViewById(R.id.cart_product_image2);

    }

    @Override
    public void onClick(View v)
    {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
