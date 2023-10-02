package com.example.food;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Restaurant_View_Adaptor extends RecyclerView.Adapter<Restaurant_View_Adaptor.restaurant_view_holder>
{

    private Context context;
    private static List<foodmodel> foodmodelList;
    private DatabaseReference databaseReference;

    public Restaurant_View_Adaptor(Context context, List<foodmodel>foodmodelList1){
        this.context = context;
        this.foodmodelList = foodmodelList1;

    }




    @NonNull
    @Override
    public restaurant_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_view_item, parent, false);
        return new Restaurant_View_Adaptor.restaurant_view_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Restaurant_View_Adaptor.restaurant_view_holder holder, int position) {

    foodmodel foodmodel = foodmodelList.get(position);
        holder.food.setText(foodmodel.getFood_name());
        holder.desc.setText(foodmodel.getDesc());
      holder.price.setText(foodmodel.getPrice());
        Picasso.get().load(foodmodel.getPic()).into(holder.foodImg);

        holder.option.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.option);
            popup.inflate(R.menu.option_menu);
            popup.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.menu_edit:

                        Intent intent = new Intent(context, Food_Upload.class);

                        intent.putExtra("Food Name", foodmodel.getFood_name());
                        intent.putExtra("Description", foodmodel.getDesc());
                        intent.putExtra("Price", foodmodel.getPrice());
                        intent.putExtra("Owner ID", foodmodel.getOwnerid());
                        intent.putExtra("Food Image", foodmodel.getPic());
                        context.startActivity(intent);
                        break;

                    case R.id.menu_remove:

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        databaseReference = db.getReference("food");

                        // alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete");
                        builder.setMessage("Are you sure to delete this posted foods?");
                        //set positive/yes button
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                databaseReference.child(foodmodel.getOwnerid()).removeValue().addOnSuccessListener(suc ->
                                {
                                    Toast.makeText(context, "Food successfully Removed", Toast.LENGTH_SHORT).show();
                                    foodmodelList.remove(holder.getAdapterPosition());
                                    notifyItemChanged(holder.getAdapterPosition());

                                }).addOnFailureListener(er -> {
                                    Toast.makeText(context, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent job = new Intent(context, Restaurant_Home_View.class);
                                    context.startActivity(job);
                                });


                            }
                        });
                        //set negative/no button
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //user pressed "no", dialog dismiss dialog
                                dialogInterface.dismiss();
                            }
                        });

                        // show dialog
                        builder.create().show();
                        break;
                }

            return false;
            });
            popup.show();
        });

    }

    @Override
    public int getItemCount() {
        return foodmodelList.size();
    }


    public  void filterlist(ArrayList<foodmodel> filterlist) {
        foodmodelList = filterlist;
notifyDataSetChanged();

    }
    public class restaurant_view_holder extends RecyclerView.ViewHolder{
    TextView restaurant_name, food, desc, price, option;
    ImageView foodImg;
    CardView item_click;
    public restaurant_view_holder(@NonNull View itemView) {
        super(itemView);
        restaurant_name = itemView.findViewById(R.id.restaurant_name);
        food =  itemView.findViewById(R.id.food1);
        foodImg = itemView.findViewById(R.id.foodimg);
        desc = itemView.findViewById(R.id.Desc1);
        price= itemView.findViewById(R.id.Price2);
        option = itemView.findViewById(R.id.txt_option);
    }
}

}
