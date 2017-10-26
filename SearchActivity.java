package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.BoolRes;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Additive;
import com.example.jonsmauricio.eyesfood.data.api.model.SearchResult;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.w3c.dom.Text;

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
    private List<SearchResult> resultadoAditivos;
    private ListView resultFoods;
    private ListView resultAdditives;
    private ArrayAdapter<SearchResult> adaptador;
    private View searchProgress;
    TextView searchProgressText;
    TextView searchEmptyState;
    TextView searchFoodsHeader;
    TextView searchAdditivesHeader;
    boolean noFoods;
    boolean noAdditives;
    boolean emptyState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view_search);
        resultFoods = (ListView) findViewById(R.id.lvResultFoods);
        resultAdditives = (ListView) findViewById(R.id.lvResultAdditives);
        searchProgress = findViewById(R.id.pbSearchProgress);
        searchProgressText = (TextView) findViewById(R.id.tvSearchProgressText);
        searchEmptyState = (TextView) findViewById(R.id.tvSearchEmptyState);
        searchFoodsHeader = (TextView) findViewById(R.id.tvSearchFoodsHeader);
        searchAdditivesHeader = (TextView) findViewById(R.id.tvSearchAdditivesHeader);

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
            showProgress(true);
            query = (String) b.get("query");
            makeQueryFoods(query);
            makeQueryAdditives(query);
        }
    }

    public void makeQueryFoods(String query){
        Call<List<SearchResult>> call = mEyesFoodApi.getFoodsQuery(query);
        call.enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(Call<List<SearchResult>> call,
                                   Response<List<SearchResult>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                resultadoAlimentos = response.body();
                showListFoods(resultadoAlimentos);
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada de aditivos: loadAdditives");
            }
        });
    }

    public void makeQueryAdditives(String query){
        Call<List<SearchResult>> call = mEyesFoodApi.getAdditivesQuery(query);
        call.enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(Call<List<SearchResult>> call,
                                   Response<List<SearchResult>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                resultadoAditivos = response.body();
                showListAdditives(resultadoAditivos);
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada de aditivos: loadAdditives");
            }
        });
    }

    public void showListFoods(List<SearchResult> lista){
        int tamanoLista = lista.size();
        if(tamanoLista > 0) {
            noFoods = false;
            adaptador = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    lista);
            resultFoods.setAdapter(adaptador);
        }
        else{
            noFoods = true;
        }
    }

    public void showListAdditives(List<SearchResult> lista){
        int tamanoLista = lista.size();
        if(tamanoLista > 0) {
            noAdditives = false;
            adaptador = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    lista);
            resultAdditives.setAdapter(adaptador);
        }
        else{
            noAdditives = true;
            showEmptyState(noAdditives, noFoods);
        }
        showProgress(false);
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
                //Vacío la lista anterior seteo el empty state y el progress antes de hacer la query
                resultadoAlimentos.clear();
                resultadoAditivos.clear();
                showProgress(true);
                makeQueryFoods(query);
                makeQueryAdditives(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void showProgress(boolean show) {
        //Escondo el empty state cuando muestro el progreso
        showEmptyState(false, false);
        searchProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        searchProgressText.setVisibility(show ? View.VISIBLE : View.GONE);
        //Seteo de nuevo los valores por defecto del emptyState
        showEmptyState(noAdditives, noFoods);

        if(!emptyState){
            int visibility = show ? View.GONE : View.VISIBLE;
            resultFoods.setVisibility(visibility);
            searchFoodsHeader.setVisibility(visibility);
            resultAdditives.setVisibility(visibility);
            searchAdditivesHeader.setVisibility(visibility);
        }
    }

    public void showEmptyState(boolean noAdditives, boolean noFoods){
        if(noAdditives && noFoods){
            resultFoods.setVisibility(View.GONE);
            searchFoodsHeader.setVisibility(View.GONE);
            resultAdditives.setVisibility(View.GONE);
            searchAdditivesHeader.setVisibility(View.GONE);
            searchEmptyState.setVisibility(View.VISIBLE);
            emptyState = true;
        }
        else{
            resultFoods.setVisibility(View.VISIBLE);
            searchFoodsHeader.setVisibility(View.VISIBLE);
            resultAdditives.setVisibility(View.VISIBLE);
            searchAdditivesHeader.setVisibility(View.VISIBLE);
            searchEmptyState.setVisibility(View.GONE);
            emptyState = false;
        }
    }

}