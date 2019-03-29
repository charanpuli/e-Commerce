package com.example.charanpuli.shop_online.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.charanpuli.shop_online.Interface.ItemClickListener;
import com.example.charanpuli.shop_online.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{   public TextView txtProductName,txtProductDescription,txtProductPrice;
    public ImageView ProductCardImage;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName=(TextView)itemView.findViewById(R.id.product_card_name);
        txtProductDescription=(TextView)itemView.findViewById(R.id.product_card_description);
        ProductCardImage=(ImageView)itemView.findViewById(R.id.product_card_image);
        txtProductPrice=(TextView)itemView.findViewById(R.id.product_card_price);

    }
    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener=listener;
    }


    @Override
    public void onClick(View v) {
         listener.onClick(v,getAdapterPosition(),false);
    }
}
