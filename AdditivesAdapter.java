package com.example.jonsmauricio.eyesfood.ui;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Additive;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.squareup.picasso.Picasso;

import java.util.List;


/*
    Clase encargada de llenar los card views
*/
public class AdditivesAdapter extends RecyclerView.Adapter<AdditivesAdapter.AdditivesViewHolder> {

    private List<Additive> items;

    public String peligro;
    public String origen;

    public class AdditivesViewHolder extends RecyclerView.ViewHolder{
        // Campos respectivos de un item
        public TextView title;
        public TextView shortDescription;
        public TextView description;
        public TextView usage;
        public TextView secondaryEffects;
        public CardView additivesCard;

        public AdditivesViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.tvAdditivesTitle);
            shortDescription = v.findViewById(R.id.tvAdditivesShortDescription);
            description = v.findViewById(R.id.tvAdditivesDescription);
            usage = v.findViewById(R.id.tvAdditivesUsage);
            secondaryEffects = v.findViewById(R.id.tvAdditivesSecondaryEffects);
            additivesCard = v.findViewById(R.id.cvAdditivesCard);
        }
    }

    public AdditivesAdapter(List<Additive> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public AdditivesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_additive, viewGroup, false);
        return new AdditivesViewHolder(v);
    }

    //Llena los campos del cardview
    @Override
    public void onBindViewHolder(AdditivesViewHolder viewHolder, int i) {
        peligro = items.get(i).getHazard().toLowerCase();
        origen = items.get(i).getSource().toLowerCase();
        viewHolder.title.setText(items.get(i).getAdditive() + " ("+items.get(i).geteCode()+")");
        viewHolder.shortDescription.setText(items.get(i).getClassification() + " " + peligro + " de origen " + origen +".");
        viewHolder.description.setText("Descripción: " + items.get(i).getDescription());
        viewHolder.usage.setText("Uso: " + items.get(i).getUsage());
        viewHolder.secondaryEffects.setText("Efectos secundarios " + items.get(i).getSecondaryEffects());

        switch (peligro){
            case "inocuo":
                viewHolder.additivesCard.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
                break;
            case "sospechoso":
                viewHolder.additivesCard.setCardBackgroundColor(Color.parseColor("#FFF9C4"));
                break;
            case "peligroso":
                viewHolder.additivesCard.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
                break;
        }
    }
}