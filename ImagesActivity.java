package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.GridView;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.FoodImage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImagesActivity extends AppCompatActivity {

    Retrofit mRestAdapter;
    private EyesFoodApi mEyesFoodApi;

    private ArrayList<FoodImage> listaImagenes = new ArrayList<FoodImage>();
    private GridView gridView;
    private String Nombre, CodigoBarras;
    private ImagesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = (GridView) findViewById(R.id.gvImages);

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        //Recibe los datos enviados por el scanner o lista
        if(b!=null)
        {
            Nombre = (String) b.get("Nombre");
            setTitle(Nombre);
            CodigoBarras = (String) b.get("CodigoBarras");
            loadImages(CodigoBarras);
        }
    }

    //Carga las recomendaciones del alimento
    public void loadImages(String barcode) {
        Call<ArrayList<FoodImage>> call = mEyesFoodApi.getImages(barcode);
        call.enqueue(new Callback<ArrayList<FoodImage>>() {
            @Override
            public void onResponse(Call<ArrayList<FoodImage>> call,
                                   Response<ArrayList<FoodImage>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaImagenes = response.body();
                showImages(listaImagenes);

            }

            @Override
            public void onFailure(Call<ArrayList<FoodImage>> call, Throwable t) {
            }
        });
    }

    //Muestra el historial
    //historial: Lista de alimentos en el historial
    public void showImages(ArrayList<FoodImage> imagenes) {
        adapter = new ImagesAdapter(getApplicationContext(),imagenes);
        gridView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }
}