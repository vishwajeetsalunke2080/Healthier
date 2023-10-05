package com.Healthier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class mainAdapter extends FirebaseRecyclerAdapter<MainModel,mainAdapter.myViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public mainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull MainModel model) {
        holder.meal.setText(model.getMeal());
        holder.dish.setText(model.getDish());
        holder.calories.setText(model.getCalories());

        Glide.with(holder.img.getContext())
                .load(model.getImgSrc())
                .placeholder(com.firebase.ui.database.R.drawable.googleg_standard_color_18)
                .circleCrop()
                .error(com.firebase.ui.database.R.drawable.notification_tile_bg)
                .into(holder.img);

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogplus = DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .create();

                View updateView = dialogplus.getHolderView();
                EditText d = updateView.findViewById(R.id.updateDish);
                EditText i = updateView.findViewById(R.id.updateImage);
                EditText c = updateView.findViewById(R.id.updateCalories);
                EditText m = updateView.findViewById(R.id.updateMeal);

                Button updateButton = updateView.findViewById(R.id.updateBtn);

                d.setText(model.getDish());
                i.setText(model.getImgSrc());
                c.setText(model.getCalories());
                m.setText(model.getMeal());
                dialogplus.show();
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("dish",d.getText().toString());
                        map.put("imgSrc",i.getText().toString());
                        map.put("calories",c.getText().toString());
                        map.put("meal",m.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("food")
                                .child(getRef(position).getKey())
                                .updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.dish.getContext(), "Information Update Successful", Toast.LENGTH_SHORT).show();
                                        dialogplus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.dish.getContext(), "Some Error Occurred !", Toast.LENGTH_SHORT).show();
                                        dialogplus.dismiss();
                                    }
                                });
                    }
                });
            }
        });

        holder.rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(holder.dish.getContext(), "none", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.img.getContext());
                builder.setTitle("Are you sure");
                builder.setMessage("This action cannot be reverted");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("food")
                                .child(getRef(position).getKey()).removeValue();
                        Toast.makeText(holder.img.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }

                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.img.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }

        });
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView meal,dish,calories;

        Button editBtn,rem;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = (CircleImageView)itemView.findViewById(R.id.img1);
            meal = (TextView) itemView.findViewById(R.id.mealName);
            dish = (TextView) itemView.findViewById(R.id.dishName);
            calories = (TextView) itemView.findViewById(R.id.caloriesCount);
            editBtn = (Button) itemView.findViewById(R.id.editBtn);
            rem = (Button) itemView.findViewById(R.id.delete);

        }

    }
}
