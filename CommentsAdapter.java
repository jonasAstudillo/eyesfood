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
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by JonásMauricio on 04-11-2017.
 */

public class CommentsAdapter extends ArrayAdapter<Comment> {

    final String baseFotoUsuario = EyesFoodApi.BASE_URL+"img/users/";
    private String session;

    public CommentsAdapter(Context context, List<Comment> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        session = SessionPrefs.get(getContext()).getUserSession();

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.list_comments_item,
                    parent,
                    false);
        }

        // Referencias UI.
        ImageView avatar = convertView.findViewById(R.id.ivCommentsAvatar);
        TextView name = convertView.findViewById(R.id.tvCommentsName);
        TextView comment = convertView.findViewById(R.id.tvCommentsComment);
        TextView date = convertView.findViewById(R.id.tvCommentsDate);
        RatingBar ratingBar = convertView.findViewById(R.id.rbCommentsRating);

        // Comentario actual.
        Comment currentComment = getItem(position);

        // Setup.
        //Cargo avatar de usuario actual
        // TODO: 07-11-2017 VER COMO ARREGLAR ESTO
        if(currentComment.getUserPhoto().contains("facebook") || currentComment.getUserPhoto().contains("google")) {
            Picasso.with(getContext()).load(currentComment.getUserPhoto()).resize(800, 800).into(avatar);
        }
        else{
            Picasso.with(getContext()).load(baseFotoUsuario + currentComment.getUserPhoto()).resize(800,800).into(avatar);
        }
        name.setText(currentComment.getUserName() + " " + currentComment.getUserLastName());
        ratingBar.setRating(currentComment.getReputation());
        comment.setText(currentComment.getComment());
        date.setText(currentComment.getDate());

        return convertView;
    }
}