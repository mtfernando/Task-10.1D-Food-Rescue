package com.example.foodrescueapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrescueapp.model.FoodItem;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<FoodItem> foodItemList;
    private Context context;

    public RecyclerViewAdapter(List<FoodItem> foodItemList, Context context) {
        this.foodItemList = foodItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.food_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {


        if(this.getItemCount()>0){
            holder.foodHeader.setText(foodItemList.get(position).getTitle());
            holder.foodDesc.setText(foodItemList.get(position).getDetails());
            holder.destinationImage.setImageResource(context.getResources().getIdentifier("drawable/" + foodItemList.get(position).getImageRes(), null, context.getPackageName()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView destinationImage;
        TextView foodHeader, foodDesc;
        ImageButton shareButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assigning views from itemView(destination_vertical.xml) to local variables.
            destinationImage = itemView.findViewById(R.id.cardImageView);
            foodHeader = itemView.findViewById(R.id.HeaderTextView);
            foodDesc = itemView.findViewById(R.id.descTextView);
            shareButton = itemView.findViewById(R.id.shareImgButton);
        }
    }
}
