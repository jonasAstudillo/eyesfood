package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Additive;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdditivesActivity extends AppCompatActivity {

    //Instancias globales para el Card view
    private RecyclerView recycler;
    private AdditivesAdapter adapter;
    private RecyclerView.LayoutManager lManager;

    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;

    private String CodigoBarras;
    private String Nombre;
    private List<Additive> listaAditivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additives);
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
            CodigoBarras = (String) b.get("CodigoBarras");
            Nombre = (String) b.get("Nombre");
            setTitle(Nombre);
        }

        loadAdditives(CodigoBarras);
        Log.d("myTag", "en on create");
    }

    //Carga la lista de aditivos
    public void loadAdditives(String barcode){
        Call<List<Additive>> call = mEyesFoodApi.getFullAdditives(barcode);
        call.enqueue(new Callback<List<Additive>>() {
            @Override
            public void onResponse(Call<List<Additive>> call,
                                   Response<List<Additive>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaAditivos = response.body();
                showAdditives(listaAditivos);
            }

            @Override
            public void onFailure(Call<List<Additive>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada de aditivos: loadAdditives");
            }
        });
    }

    //Muestra los aditivos
    //historial: Lista de aditivos del alimento
    public void showAdditives(List<Additive> listaAditivos) {

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.additivesRecycler);
        recycler.setHasFixedSize(true);

        //Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        //Crear un nuevo adaptador
        adapter = new AdditivesAdapter(listaAditivos);
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
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }
}
