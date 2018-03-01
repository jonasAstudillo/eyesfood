package com.example.jonsmauricio.eyesfood.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Comment;
import com.example.jonsmauricio.eyesfood.data.api.model.Counter;
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.security.AccessController.getContext;


/*
    Clase encargada de llenar los card views
*/
// TODO: 16-11-2017 Ver si puedo borrar algunas declaraciones de Retrofit
// TODO: 20-11-2017 Ver el progress bar
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private Context context;
    private List<ShortFood> items;
    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;
    private Counter likesCounter;
    private int likes;
    private Counter dislikesCounter;
    private int dislikes;
    private List<Comment> listaComentarios;
    private Counter commentsCounter;
    private int comments;
    private ProgressBar progressBar;

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
        public ImageView like;
        public TextView likesCount;
        public ImageView dislike;
        public TextView dislikesCount;
        public ImageView comment;
        public TextView commentsCount;

        public HistoryViewHolder(View v) {
            super(v);
            imagen = v.findViewById(R.id.iv_history_image);
            nombre = v.findViewById(R.id.tv_history_name);
            fecha = v.findViewById(R.id.tv_history_date);
            ratingBar = v.findViewById(R.id.rbHistoryRating);
            like = v.findViewById(R.id.destacado);
            likesCount = v.findViewById(R.id.tvLikesCount);
            dislike = v.findViewById(R.id.rechazado);
            dislikesCount = v.findViewById(R.id.tvDisLikesCount);
            comment = v.findViewById(R.id.comentarios);
            commentsCount = v.findViewById(R.id.tvCommentsCount);
            progressBar = v.findViewById(R.id.pbCardProgress);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    public HistoryAdapter(List<ShortFood> items, Context context) {
        this.items = items;
        this.context = context;
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
    public void onBindViewHolder(final HistoryViewHolder viewHolder, final int i) {
        //showProgress(true);
        Picasso.with(viewHolder.imagen.getContext())
                .load(baseFotoAlimento + items.get(i).getOfficialPhoto())
                .into(viewHolder.imagen);
        viewHolder.nombre.setText(items.get(i).getName());
        viewHolder.fecha.setText("Escaneado el "+ items.get(i).getDate());
        viewHolder.ratingBar.setRating(items.get(i).getFoodHazard());
        getLikesCount(viewHolder, items.get(i).getBarCode());
        getDisLikesCount(viewHolder, items.get(i).getBarCode());
        getCommentsCount(viewHolder, items.get(i).getBarCode());

        if(items.get(i).getLike() == 2){
            viewHolder.dislike.setColorFilter(Color.parseColor("#3D5AFE"));
        }
        else if(items.get(i).getLike() == 1){
            viewHolder.like.setColorFilter(Color.parseColor("#3D5AFE"));
        }

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //0 para ninguno 1 para like 2 para dislike
                int seleccion = items.get(i).getLike();
                if(seleccion == 2){
                    //Si es 2 cambio el color a normal al dislike, acento para like
                    viewHolder.dislike.setColorFilter(Color.parseColor("#000000"));
                    //viewHolder.dislikesCount.setText(String.valueOf(dislikes--));
                    viewHolder.like.setColorFilter(Color.parseColor("#3D5AFE"));
                    //viewHolder.likesCount.setText(String.valueOf(likes++));
                    items.get(i).setLike(1);
                    //Hacer patch
                }
                else if(seleccion == 1){
                    //Si es 1 cambio el color a normal al like
                    viewHolder.like.setColorFilter(Color.parseColor("#000000"));
                    //viewHolder.likesCount.setText(String.valueOf(likes--));
                    items.get(i).setLike(0);
                    //Hacer patch
                }
                else{
                    //Si es 0 cambio el color a acento a like
                    viewHolder.like.setColorFilter(Color.parseColor("#3D5AFE"));
                    //viewHolder.likesCount.setText(String.valueOf(likes++));
                    items.get(i).setLike(1);
                }
                seleccion = items.get(i).getLike();
                updateLikeHistory(viewHolder, items.get(i).getUserId(), items.get(i).getBarCode(), seleccion);
                Log.d("myTag", items.get(i).getBarCode() + " 2)" + seleccion);
            }
        });
        viewHolder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int seleccion = items.get(i).getLike();
                if(seleccion == 2){
                    viewHolder.dislike.setColorFilter(Color.parseColor("#000000"));
                    //viewHolder.dislikesCount.setText(String.valueOf(dislikes--));
                    items.get(i).setLike(0);
                    //Hacer patch
                }
                else if(seleccion == 1){
                    viewHolder.like.setColorFilter(Color.parseColor("#000000"));
                    //viewHolder.likesCount.setText(String.valueOf(likes--));
                    viewHolder.dislike.setColorFilter(Color.parseColor("#3D5AFE"));
                    //viewHolder.dislikesCount.setText(String.valueOf(dislikes++));
                    items.get(i).setLike(2);
                    //Hacer patch
                }
                else{
                    //Si es 0 cambio el color a acento a like
                    viewHolder.dislike.setColorFilter(Color.parseColor("#3D5AFE"));
                    //viewHolder.dislikesCount.setText(String.valueOf(dislikes++));
                    items.get(i).setLike(2);
                }
                seleccion = items.get(i).getLike();
                updateLikeHistory(viewHolder, items.get(i).getUserId(), items.get(i).getBarCode(), seleccion);
                Log.d("myTag", items.get(i).getBarCode() + " 2)" + seleccion);
            }
        });
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFood(items.get(i).getBarCode(), items.get(i).getLike());
            }
        });
    }

    //Actualiza los me gusta del alimento
    public void updateLikeHistory(final HistoryViewHolder viewHolder, String userId, final String barcode, int like){
        // Crear conexión al servicio REST

        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        Call<ShortFood> call = mEyesFoodApi.modifyHistoryLike(userId, barcode, like);
        call.enqueue(new Callback<ShortFood>() {
            @Override
            public void onResponse(Call<ShortFood> call, Response<ShortFood> response) {
                if (!response.isSuccessful()) {
                    Log.d("myTag", "no Éxito en updateLikeHistory " + response.errorBody().toString());
                    return;
                }
                else {
                    Log.d("myTag", "Éxito en updateLikeHistory");
                    getLikesCount(viewHolder, barcode);
                    getDisLikesCount(viewHolder, barcode);
                }
            }
            @Override
            public void onFailure(Call<ShortFood> call, Throwable t) {
                Log.d("myTag", "Fallo en updateLikeHistory "+ t.getMessage() + " " + t.getLocalizedMessage());
                t.printStackTrace();
                return;
            }
        });
    }

    //Actualiza los me gusta del alimento
    public void getLikesCount(final HistoryViewHolder viewHolder, String barcode){
        // Crear conexión al servicio REST

        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        Call<Counter> call = mEyesFoodApi.getLikes(barcode);
        call.enqueue(new Callback<Counter>() {
            @Override
            public void onResponse(Call<Counter> call, Response<Counter> response) {
                if (!response.isSuccessful()) {
                    Log.d("myTag", "no Éxito en getLikes " + response.errorBody().toString());
                    return;
                }
                else {
                    likesCounter = response.body();
                    likes = likesCounter.getCount();
                    Log.d("myTag", "likes: "+likes);
                    viewHolder.likesCount.setText(String.valueOf(likes));
                }
            }
            @Override
            public void onFailure(Call<Counter> call, Throwable t) {
                Log.d("myTag", "Fallo en getLikes "+ t.getMessage() + " " + t.getLocalizedMessage());
                t.printStackTrace();
                return;
            }
        });
    }

    //Actualiza los me gusta del alimento
    public void getDisLikesCount(final HistoryViewHolder viewHolder, String barcode){
        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        Call<Counter> call = mEyesFoodApi.getDislikes(barcode);
        call.enqueue(new Callback<Counter>() {
            @Override
            public void onResponse(Call<Counter> call, Response<Counter> response) {
                if (!response.isSuccessful()) {
                    Log.d("myTag", "no Éxito en getDisLikes " + response.errorBody().toString());
                    return;
                }
                else {
                    dislikesCounter = response.body();
                    dislikes = dislikesCounter.getCount();
                    Log.d("myTag", "dislikes: "+dislikes);
                    viewHolder.dislikesCount.setText(String.valueOf(dislikes));
                }
            }
            @Override
            public void onFailure(Call<Counter> call, Throwable t) {
                Log.d("myTag", "Fallo en getDisLikes "+ t.getMessage() + " " + t.getLocalizedMessage());
                t.printStackTrace();
                return;
            }
        });
    }

    //Retorna la cantidad de comentarios del alimento
    public void getCommentsCount(final HistoryViewHolder viewHolder, String barcode){
        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        Call<Counter> call = mEyesFoodApi.getCommentsCount(barcode);
        call.enqueue(new Callback<Counter>() {
            @Override
            public void onResponse(Call<Counter> call, Response<Counter> response) {
                if (!response.isSuccessful()) {
                    Log.d("myTag", "no Éxito en getComments " + response.errorBody().toString());
                    return;
                }
                else {
                    commentsCounter = response.body();
                    comments = commentsCounter.getCount();
                    Log.d("myTag", "comments: "+comments);
                    viewHolder.commentsCount.setText(String.valueOf(comments));
                    //showProgress(false);
                }
            }
            @Override
            public void onFailure(Call<Counter> call, Throwable t) {
                Log.d("myTag", "Fallo en getComments "+ t.getMessage() + " " + t.getLocalizedMessage());
                t.printStackTrace();
                return;
            }
        });
    }

    //Retorna un alimento al pinchar en el historial
    //Barcode: Código de barras del alimento a retornar
    public void loadFood(final String barcode, final int like) {
        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        Call<Food> call = mEyesFoodApi.getFood(barcode);
        call.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call,
                                   Response<Food> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    return;
                }
                //Si entro acá el alimento existe en la BD y lo obtengo
                Food resultado = response.body();
                //Muestro el alimento
                loadComments(resultado, barcode, like);
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
            }
        });
    }

    //Carga los comentarios del alimento
    public void loadComments(final Food alimento, String barcode, final int like) {
        Call<List<Comment>> call = mEyesFoodApi.getComments(barcode);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call,
                                   Response<List<Comment>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaComentarios = response.body();
                showComments(alimento, listaComentarios, like);
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
            }
        });
    }

    public void showComments(Food alimento, List<Comment> lista, int like){
        Intent intent = new Intent(context, CommentsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("Comentarios",(Serializable) lista);
        intent.putExtra("BUNDLE",args);

        intent.putExtra("Alimento",alimento);
        intent.putExtra("MeGusta",like);
        context.startActivity(intent);
    }

    private void showProgress(boolean show) {
        if(show) {
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            progressBar.setVisibility(View.GONE);
        }
    }
}