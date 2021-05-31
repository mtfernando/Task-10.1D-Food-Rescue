package com.example.foodrescueapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrescueapp.model.FoodItem;
import com.example.foodrescueapp.util.Util;

import java.util.List;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder> {
    public static final String TAG = "RecyclerViewAdapter";
    private List<FoodItem> foodItemList;
    private Context context;

    public CartRecyclerViewAdapter(List<FoodItem> foodItemList, Context context) {
        this.foodItemList = foodItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerViewAdapter.ViewHolder holder, int position) {


        if(this.getItemCount()>0){

            //Setting up each Food Item's viewholder in the cart
            holder.foodHeaderTextView.setText(foodItemList.get(position).getTitle());
            holder.itemNoTextView.setText(String.valueOf(holder.getAdapterPosition()+1));
            holder.priceTextView.setText(String.valueOf(foodItemList.get(holder.getAdapterPosition()).getPrice()));
        }
        else{
            Toast.makeText(context, "No item added to cart", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No items in cart. getItemCount returns = " + this.getItemCount());
        }
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView foodHeaderTextView, itemNoTextView, priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assigning views from itemView(destination_vertical.xml) to local variables.
            foodHeaderTextView = itemView.findViewById(R.id.foodItemTextView);
            itemNoTextView = itemView.findViewById(R.id.cartNumberTextView);
            priceTextView = itemView.findViewById(R.id.cartPriceTextView);

        }
    }
}
