package com.example.jonsmauricio.eyesfood.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.HistoryFoodBody;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.google.zxing.client.android.CaptureActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnClickListener, ItemClickListener {

    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;
    private ArrayAdapter<ShortFood> adaptador;

    //Obtengo token e id de Usuario
    private String userIdFinal;

    //Instancias globales para el Card view
    private RecyclerView recycler;
    private HistoryAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private List<ShortFood> historial;

    private String barCode;

    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userIdFinal = SessionPrefs.get(this).getUserId();
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        // TODO: 19-10-2017 Borrar este botón de prueba
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);
    }

    //Método de actualización
    // TODO: 19-10-2017 Cuando hayan más listas filtrar según la lista que esté viendo 
    @Override
    protected void onResume() {
        super.onResume();

        loadHistoryFoods(userIdFinal);
    }

    //Carga alimentos en el historial
    //Token: token de autorización
    //UserId: Id de usuario
    // TODO: 19-10-2017 Cuando haya más listas los métodos son iguales a este
    public void loadHistoryFoods(String userId) {
        Call<List<ShortFood>> call = mEyesFoodApi.getFoodsInHistory(userId);
        call.enqueue(new Callback<List<ShortFood>>() {
            @Override
            public void onResponse(Call<List<ShortFood>> call,
                                   Response<List<ShortFood>> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    return;
                }
                historial = response.body();
                showHistory(historial);
            }

            @Override
            public void onFailure(Call<List<ShortFood>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada a historial: loadHistoryFoods");
            }
        });
    }

    //Muestra el historial
    //historial: Lista de alimentos en el historial
    public void showHistory(List<ShortFood> historial) {

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        //Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        //Crear un nuevo adaptador
        adapter = new HistoryAdapter(historial);
        recycler.setAdapter(adapter);
        adapter.setClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:{

                if (ContextCompat.checkSelfPermission(HistoryActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HistoryActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                    intent.setAction("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SAVE_HISTORY", false);
                    startActivityForResult(intent, 0);
                }
                break;
            }
        }
    }

    //OnClick para la lista de cards, se necesita sobreescribir porque la clase implementa este método
    //Sobreescribe itemClickListener
    @Override
    public void onClick(View view, int position) {
        ShortFood food = historial.get(position);
        loadFoodsFromHistory(food.getBarCode());
    }

    //Retorna un alimento al pinchar en el historial
    //Token: Token de autorización
    //Barcode: Código de barras del alimento a retornar
    public void loadFoodsFromHistory(String barcode) {
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
                showFoodsScreen(resultado);
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                noFood();
            }
        });
    }

    //Retorna un alimento
    //Token: Token de autorización
    //Barcode: Código de barras del alimento a retornar
    public void loadFoods(String barcode) {
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
                //Veo si está en el historial
                isFoodInHistory(userIdFinal, resultado.getBarCode());

                showFoodsScreen(resultado);
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                noFood();
            }
        });
    }

    //Muestra el diálogo si es que no existe el alimento para agregarlo
    // TODO: 19-10-2017 Actualizar agregar alimento
    public void noFood(){
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle("El producto aún no está agregado")
                .setMessage("¿Deseas agregarlo?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showNewFoodsDialog();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showNewFoodsDialog(){
        Bundle bundle = new Bundle();
        bundle.putString("barCode", barCode);
        // set Fragmentclass Arguments

        FragmentManager fragmentManager = getSupportFragmentManager();
        NewFoodsDialogFragment newFragment = new NewFoodsDialogFragment();
        newFragment.setArguments(bundle);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    //Envía los datos del alimento escaneado a la activity que va a mostrar esos datos
    //resultado: Alimento a mostrar
    public void showFoodsScreen(Food resultado){
        Intent i = new Intent(this, FoodsActivity.class);
        //String strName = resultado.getName();
        i.putExtra("CodigoBarras",resultado.getBarCode());
        i.putExtra("Nombre", resultado.getName());
        startActivity(i);
    }

    //Pide el permiso para acceder a la cámara
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
            intent.setAction("com.google.zxing.client.android.SCAN");
            intent.putExtra("SAVE_HISTORY", false);
            startActivityForResult(intent, 0);
        }else{
            Toast.makeText(this, "Necesitas dar permiso para usar la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    //Procesa lo obtenido por el escáner
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            //Si obtiene el código
            if (resultCode == RESULT_OK) {
                barCode = intent.getStringExtra("SCAN_RESULT");
                loadFoods(barCode);
            }
            //Si no obtiene el código
            else if (resultCode == RESULT_CANCELED) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //Comprueba si el alimento consultado está en el historial del usuario
    public void isFoodInHistory(String userId, final String barcode){
        Call<ShortFood> call = mEyesFoodApi.isInHistory(userId, barcode);
        call.enqueue(new Callback<ShortFood>() {
            @Override
            public void onResponse(Call<ShortFood> call,
                                   Response<ShortFood> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                //El alimento está en el historial y lo actualizo
                updateHistory(userIdFinal, barcode);
            }

            @Override
            public void onFailure(Call<ShortFood> call, Throwable t) {
                //El alimento no está y lo inserto
                insertFood(userIdFinal, barcode);
            }
        });

    }

    //Inserta un alimento en el historial
    public void insertFood(String userId, String barcode){
        Call<Food> call = mEyesFoodApi.insertInHistory(new HistoryFoodBody(userId, barcode));
        call.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                else {
                    Log.d("myTag", "Éxito en insertFood");
                }
            }
            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Log.d("myTag", "Fallo en insertFood");
                return;
            }
        });
    }

    //Actualiza la fecha de un alimento en el historial
    public void updateHistory(String userId, String barcode){
        Call<ShortFood> call = mEyesFoodApi.modifyHistory(userId, barcode);
        call.enqueue(new Callback<ShortFood>() {
            @Override
            public void onResponse(Call<ShortFood> call, Response<ShortFood> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                else {
                    Log.d("myTag", "Éxito en updateHistory");
                }
            }
            @Override
            public void onFailure(Call<ShortFood> call, Throwable t) {
                Log.d("myTag", "Fallo en updateHistory");
                return;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);

        MenuItem item = menu.findItem(R.id.searchHistory);
        searchView.setMenuItem(item);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("query", query);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            SessionPrefs.get(HistoryActivity.this).logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
