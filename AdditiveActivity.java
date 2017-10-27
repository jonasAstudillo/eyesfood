package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Additive;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdditiveActivity extends AppCompatActivity {

    //Instancias globales para el Card view
    private RecyclerView recycler;
    private AdditivesAdapter adapter;
    private RecyclerView.LayoutManager lManager;

    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;

    private String CodigoE;
    private String Nombre;
    private String query;
    public List<Additive> aditivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if(b != null){
            CodigoE = (String) b.get("CodigoE");
            Nombre = (String) b.get("Nombre");
            Log.d("myTag", CodigoE + Nombre);
            setTitle(Nombre);
        }
        loadAdditive(CodigoE);
    }

    //Carga el aditivo
    public void loadAdditive(String eCode){
        Call<List<Additive>> call = mEyesFoodApi.getAdditive(eCode);
        call.enqueue(new Callback<List<Additive>>() {
            @Override
            public void onResponse(Call<List<Additive>> call,
                                   Response<List<Additive>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                aditivo = response.body();
                Log.d("myTag","recibo"+aditivo.get(0).getAdditive());
                showAdditive(aditivo);
            }

            @Override
            public void onFailure(Call<List<Additive>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada de aditivo: loadAdditive");
            }
        });
    }

    //Muestra los aditivos
    //historial: Lista de aditivos del alimento
    public void showAdditive(List<Additive> aditivo) {
        Log.d("myTag","en show");
        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.additiveRecycler);
        recycler.setHasFixedSize(true);

        //Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        //Crear un nuevo adaptador
        adapter = new AdditivesAdapter(aditivo);
        recycler.setAdapter(adapter);
    }

}
