package com.example.food;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DisplayAdaptor extends RecyclerView.Adapter<DisplayAdaptor.displayviewholder> {

    private Context context;
    private List<foodmodel>foodmodelList;

    public DisplayAdaptor(Context context, List<foodmodel>foodmodelList){
        this.context = context;
        this.foodmodelList = foodmodelList;

    }



    @NonNull
    @Override
    public displayviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.displayview, parent, false);
        return new displayviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull displayviewholder holder, int position)
    {
        foodmodel foodmodel = foodmodelList.get(position);
        holder.restaurant_name.setText(foodmodel.getfull_name());
        holder.food.setText(foodmodel.getFood_name());
        Picasso.get().load(foodmodel.getPic()).into(holder.foodImg);
        holder.item_click.setOnClickListener(x->
        {
            Intent intent = new Intent(context, Detail.class);
            intent.putExtra("Restaurant Name",foodmodel.getfull_name());
            intent.putExtra("Email",foodmodel.getEmail());
            intent.putExtra("Food Name",foodmodel.getFood_name());
            intent.putExtra("Description",foodmodel.getDesc());
            intent.putExtra("Price",foodmodel.getPrice());
            intent.putExtra("Phone Number",foodmodel.getPhone());
            intent.putExtra("Food Image",foodmodel.getPic());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return foodmodelList.size();
    }

    public void filterlist(ArrayList<foodmodel> filter) {
        foodmodelList =filter;
        notifyDataSetChanged();
    }

    class displayviewholder extends RecyclerView.ViewHolder {
        TextView restaurant_name, food, desc, price, phone;
        ImageView foodImg;
        CardView item_click;
        public displayviewholder(@NonNull View itemView) {
            super(itemView);
            restaurant_name = itemView.findViewById(R.id.restaurant_name);
            food =  itemView.findViewById(R.id.food);
            foodImg = itemView.findViewById(R.id.foodimg);
            item_click= itemView.findViewById(R.id.item_click);

        }
    }
}
