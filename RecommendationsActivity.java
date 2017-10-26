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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Additive;
import com.example.jonsmauricio.eyesfood.data.api.model.Recommendation;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecommendationsActivity extends AppCompatActivity{

    //Instancias globales para el Card view
    private RecyclerView recycler;
    private RecommendationsAdapter adapter;
    private RecyclerView.LayoutManager lManager;

    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;

    //Obtengo token de Usuario
    private String tokenFinal;

    private String CodigoBarras;
    private String Nombre;
    private List<Recommendation> recommendations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
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

        tokenFinal = SessionPrefs.get(this).getToken();

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if(b != null){
            CodigoBarras = (String) b.get("CodigoBarras");
            Nombre = (String) b.get("Nombre");
            setTitle(Nombre);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadRecommendations(CodigoBarras);
    }

    //Carga las recomendaciones del alimento
    public void loadRecommendations(String barcode) {
        Call<List<Recommendation>> call = mEyesFoodApi.getRecommendations(barcode);
        call.enqueue(new Callback<List<Recommendation>>() {
            @Override
            public void onResponse(Call<List<Recommendation>> call,
                                   Response<List<Recommendation>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                recommendations = response.body();
                showRecommendations(recommendations);
            }

            @Override
            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada de recomendaciones: loadRecommendations");
            }
        });
    }

    //Muestra las recomendaciones
    //recommendations: Lista de recomendaciones del alimento
    public void showRecommendations(List<Recommendation> recommendations) {

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recommendationsRecycler);
        recycler.setHasFixedSize(true);

        //Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        //Crear un nuevo adaptador
        adapter = new RecommendationsAdapter(recommendations);
        recycler.setAdapter(adapter);
    }

    //Carga el menú a la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_no_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, FoodsActivity.class);
                i.putExtra("CodigoBarras", CodigoBarras);
                i.putExtra("Nombre", Nombre);
                startActivity(i);
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

}