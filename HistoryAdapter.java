package com.example.jonsmauricio.eyesfood.ui;

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
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.squareup.picasso.Picasso;

import java.util.List;


/*
    Clase encargada de llenar los card views
*/
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<ShortFood> items;

    //IP de usach alumnos
    //private final String baseFotoAlimento = "http://158.170.214.219/api.eyesfood.cl/v1/img/food/";
    final String baseFotoAlimento = EyesFoodApi.BASE_URL+"img/food/";

    private ItemClickListener clickListener;

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView fecha;
        public RatingBar ratingBar;

        public HistoryViewHolder(View v) {
            super(v);
            imagen = v.findViewById(R.id.iv_history_image);
            nombre = v.findViewById(R.id.tv_history_name);
            fecha = v.findViewById(R.id.tv_history_date);
            ratingBar = v.findViewById(R.id.rbHistoryRating);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    public HistoryAdapter(List<ShortFood> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_history, viewGroup, false);
        return new HistoryViewHolder(v);
    }

    //Llena los campos del cardview
    @Override
    public void onBindViewHolder(HistoryViewHolder viewHolder, int i) {
        Picasso.with(viewHolder.imagen.getContext())
                .load(baseFotoAlimento + items.get(i).getOfficialPhoto())
                .into(viewHolder.imagen);
        viewHolder.nombre.setText(items.get(i).getName());
        viewHolder.fecha.setText("Escaneado el "+ items.get(i).getDate());
        viewHolder.ratingBar.setRating(items.get(i).getFoodHazard());
    }
}