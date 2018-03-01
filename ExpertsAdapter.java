package com.example.jonsmauricio.eyesfood.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Comment;
import com.example.jonsmauricio.eyesfood.data.api.model.Expert;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by JonásMauricio on 04-11-2017.
 */

public class ExpertsAdapter extends ArrayAdapter<Expert> {

    final String baseFotoExperto = EyesFoodApi.BASE_URL+"img/experts/";

    public ExpertsAdapter(Context context, List<Expert> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.list_experts_item,
                    parent,
                    false);
        }

        // Referencias UI.
        ImageView avatar = convertView.findViewById(R.id.ivExpertsAvatar);
        TextView name = convertView.findViewById(R.id.tvExpertsName);
        TextView specialty = convertView.findViewById(R.id.tvExpertsSpecialty);
        TextView phone = convertView.findViewById(R.id.tvExpertsPhone);
        RatingBar ratingBar = convertView.findViewById(R.id.rbExpertsRating);

        // Experto actual.
        Expert currentExpert = getItem(position);

        // Setup.
        //Cargo avatar de usuario actual
        Picasso.with(getContext()).load(baseFotoExperto + currentExpert.getPhoto()).resize(800,800).into(avatar);

        name.setText(currentExpert.getName() + " " + currentExpert.getLastName());
        ratingBar.setRating(currentExpert.getReputation());
        specialty.setText(currentExpert.getSpecialty());
        phone.setText(currentExpert.getPhone());

        return convertView;
    }
}