package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Additive;
import com.example.jonsmauricio.eyesfood.data.api.model.SearchResult;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    MaterialSearchView searchView;
    private String query;
    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;
    private List<SearchResult> resultadoAlimentos;
    private ListView resultFoods;
    private ListView resultAdditives;
    private ArrayAdapter<SearchResult> adaptador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view_search);
        resultFoods = (ListView) findViewById(R.id.lvResultFoods);
        //resultAdditives = (ListView) findViewById(R.id.lvResultAdditives);

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SearchResult currentSearch = adaptador.getItem(position);
                Intent i = new Intent(getApplicationContext(), FoodsActivity.class);
                i.putExtra("CodigoBarras",currentSearch.getCodigo());
                i.putExtra("Nombre",currentSearch.getNombre());
                startActivity(i);
            }
        });

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if(b != null){
            query = (String) b.get("query");
            makeQuery(query);
        }
    }

    public void makeQuery(String query){
        Call<List<SearchResult>> call = mEyesFoodApi.getFoodsQuery(query);
        call.enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(Call<List<SearchResult>> call,
                                   Response<List<SearchResult>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                resultadoAlimentos = response.body();
                showList(resultadoAlimentos);
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada de aditivos: loadAdditives");
            }
        });
    }

    public void showList(List<SearchResult> lista){

        adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                lista);
        resultFoods.setAdapter(adaptador);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.searchSearch);
        searchView.setMenuItem(item);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

}
