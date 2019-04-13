package com.example.jonsmauricio.eyesfood.ui;

import android.Manifest;
import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.HistoryFoodBody;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.facebook.login.LoginManager;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.zxing.client.android.CaptureActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryActivity extends AppCompatActivity
        implements OnClickListener, ItemClickListener, GoogleApiClient.OnConnectionFailedListener{

    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;

    //Obtengo id de Usuario y sesión
    private String userIdFinal;
    private String session;
    //Bandera para saber si vengo de perfil y empezar la animación
    int perfil = 0;
    //Bandera para ver si seteo el título, los últimos tres items no deben setear el título
    int setTitle;

    //Instancias para el card view
    private RecyclerView recycler;
    private HistoryAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private List<ShortFood> historial;

    private ProgressBar progressBar;
    private TextView emptyStateText;
    private ImageView avatar;
    private String drawerTitle;

    private String barCode;

    MaterialSearchView searchView;
    private GoogleApiClient googleApiClient;
    private FloatingActionButton fab;
    private FloatingActionsMenu fabProfile;
    //FAB del menú
    private com.getbase.floatingactionbutton.FloatingActionButton fabProfileAdd;
    private com.getbase.floatingactionbutton.FloatingActionButton fabProfileEdit;

    private int like;

    final String baseFotoUsuario = EyesFoodApi.BASE_URL+"img/users/";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userIdFinal = SessionPrefs.get(this).getUserId();
        session = SessionPrefs.get(this).getUserSession();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        recycler = (RecyclerView) findViewById(R.id.reciclador);
        progressBar = (ProgressBar) findViewById(R.id.pbMainProgress);
        emptyStateText = (TextView) findViewById(R.id.tvHistoryEmptyState);

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        drawerTitle = getResources().getString(R.string.nav_history);
        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabProfile = (FloatingActionsMenu) findViewById(R.id.fabProfile);

        fabProfileAdd = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabProfileAdd);
        fabProfileEdit = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabProfileEdit);

        fab.setOnClickListener(this);
        fabProfileAdd.setOnClickListener(this);
        fabProfileEdit.setOnClickListener(this);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        if (savedInstanceState == null) {
            selectItem(drawerTitle);
        }
    }

    //Método de actualización
    /*@Override
    protected void onResume() {
        super.onResume();
    }*/

    //Carga alimentos en el historial
    //UserId: Id de usuario
    // TODO: 19-10-2017 Cuando haya más listas los métodos son iguales a este
    public void loadHistoryFoods(String userId, String title) {
        //Hacer el if según el título
        if(title.equals(getResources().getString(R.string.nav_history))){
            //Llama a la función que carga el historial de escaneo
            Log.d("myTag","1");
            loadScan(userId);
        }
        else if(title.equals(getResources().getString(R.string.nav_uploads))){
            //Llama a la función que carga el historial de subidos
            loadUploads(userId);
        }
        else if(title.equals(getResources().getString(R.string.nav_favorites))){
            //Llama a la función que carga los destacados
            loadFavorites(userId);
        }
        //Rechazados
        else{
            //Llama a la función que carga los rechazados
            loadRejected(userId);
        }
    }

    public void loadScan(String userId){
        Log.d("myTag","En load Scan");
        Call<List<ShortFood>> call = mEyesFoodApi.getFoodsInHistory(userId);
        call.enqueue(new Callback<List<ShortFood>>() {
            @Override
            public void onResponse(Call<List<ShortFood>> call,
                                   Response<List<ShortFood>> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    Log.d("myTag", "hola"+response.errorBody().toString());
                    return;
                }

                historial = response.body();
                showHistory(historial);
            }

            @Override
            public void onFailure(Call<List<ShortFood>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada a historial: loadHistoryFoods"+t.getMessage());
            }
        });
    }

    public void loadUploads(String userId){
        Call<List<ShortFood>> call = mEyesFoodApi.getFoodsUploads(userId);
        call.enqueue(new Callback<List<ShortFood>>() {
            @Override
            public void onResponse(Call<List<ShortFood>> call,
                                   Response<List<ShortFood>> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    Log.d("myTag", "hola");
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

    public void loadFavorites(String userId){
        Call<List<ShortFood>> call = mEyesFoodApi.getFoodsFavorites(userId);
        call.enqueue(new Callback<List<ShortFood>>() {
            @Override
            public void onResponse(Call<List<ShortFood>> call,
                                   Response<List<ShortFood>> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    Log.d("myTag", "hola");
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

    public void loadRejected(String userId){
        Call<List<ShortFood>> call = mEyesFoodApi.getFoodsRejected(userId);
        call.enqueue(new Callback<List<ShortFood>>() {
            @Override
            public void onResponse(Call<List<ShortFood>> call,
                                   Response<List<ShortFood>> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    Log.d("myTag", "hola");
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

        /*if(historial.isEmpty()){
            showEmptyState(true);
            showProgress(false);
            return;
        }

        showEmptyState(false);*/
        // Obtener el Recycler
        // TODO: 20-11-2017 Aquí lo obtengo pero ya está obtenido en onCreate
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        //Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        //Crear un nuevo adaptador
        adapter = new HistoryAdapter(historial, this);
        recycler.setAdapter(adapter);
        adapter.setClickListener(this);
        showProgress(false,"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:{
                //Si no hay permiso se pide
                if (ContextCompat.checkSelfPermission(HistoryActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HistoryActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }
                //Si hay permiso se va al escáner
                else{
                    Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                    intent.setAction("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SAVE_HISTORY", false);
                    startActivityForResult(intent, 0);
                }
                break;
            }
            case R.id.fabProfileAdd:{
                showSelectedDialog(2);
                break;
            }
            case R.id.fabProfileEdit:{
                showSelectedDialog(3);
                break;
            }
        }
    }

    //OnClick para la lista de cards, se necesita sobreescribir porque la clase implementa este método
    //Sobreescribe itemClickListener
    @Override
    public void onClick(View view, int position) {
        ShortFood food = historial.get(position);
        like = food.getLike();
        loadFoodsFromHistory(food.getBarCode());
    }

    //Retorna un alimento al pinchar en el historial
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
                isFoodInHistory(userIdFinal, resultado);
                //Obtengo el like
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
                .setTitle(getResources().getString(R.string.title_new_foods_question))
                .setMessage(getResources().getString(R.string.message_new_foods_question))
                .setPositiveButton(getResources().getString(R.string.possitive_dialog), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showNewFoodsDialog();
                    }

                })
                .setNegativeButton(getResources().getString(R.string.negative_dialog), null)
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
        i.putExtra("Alimento",resultado);
        i.putExtra("MeGusta",like);
        startActivity(i);
    }

    //Pide el permiso para acceder a la cámara
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])){
            showDialogs();
        }else{
            if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, 0);
            } else{
                showDialogs();
            }
        }
    }

    public void showDialogs(){
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle(getResources().getString(R.string.title_foods_permission_rationale_camera))
                .setMessage(getResources().getString(R.string.message_history_permission_rationale_camera))
                .setPositiveButton(getResources().getString(R.string.ok_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }

                })
                .show();
    }

    //Procesa lo obtenido por el escáner
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            //Si obtiene el código
            if (resultCode == RESULT_OK) {
                barCode = intent.getStringExtra("SCAN_RESULT");
                Log.d("myTag","Barcode = "+barCode);
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
    public void isFoodInHistory(String userId, final Food alimento){
        Call<ShortFood> call = mEyesFoodApi.isInHistory(userId, alimento.getBarCode());
        call.enqueue(new Callback<ShortFood>() {
            @Override
            public void onResponse(Call<ShortFood> call,
                                   Response<ShortFood> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                //El alimento está en el historial
                //El alimento no está en ninguna lista, lista = 0, lo inserto
                if(response.body().getList() == 0){
                    //Actualizar a 1 el escaneo
                    modifyScan(userIdFinal, alimento.getBarCode());
                }
                //El alimento está en la lista de escaneo o en escaneo y subidos
                else{
                    updateHistory(userIdFinal, alimento.getBarCode());
                }
                ShortFood shortFood = response.body();
                like = shortFood.getLike();
                showFoodsScreen(alimento);

            }

            @Override
            public void onFailure(Call<ShortFood> call, Throwable t) {
                //El alimento no está y lo inserto
                insertFood(userIdFinal, alimento.getBarCode());
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

    //Modifica la bandera del escaneo
    public void modifyScan(String userId, String barcode){
        Call<ShortFood> call = mEyesFoodApi.modifyHistoryScan(userId, barcode);
        call.enqueue(new Callback<ShortFood>() {
            @Override
            public void onResponse(Call<ShortFood> call, Response<ShortFood> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                else {
                    Log.d("myTag", "Éxito en modifyScan");
                }
            }
            @Override
            public void onFailure(Call<ShortFood> call, Throwable t) {
                Log.d("myTag", "Fallo en modifyScan " + t.getMessage());
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
                    Log.d("myTag", "no Éxito en updateHistory " + response.errorBody());
                    return;
                }
                else {
                    Log.d("myTag", "Éxito en updateHistory");
                }
            }
            @Override
            public void onFailure(Call<ShortFood> call, Throwable t) {
                Log.d("myTag", "Fallo en updateHistory "+ t.getMessage());
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
            new AlertDialog.Builder(this)
                    .setIcon(null)
                    .setTitle("Salir")
                    .setMessage("¿Está seguro que desea salir de la aplicación?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
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
            if(session.equals("Facebook")) {
                LoginManager.getInstance().logOut();
                Log.d("myTag","facebook");
            }
            else if(session.equals("Gmail")) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
                Log.d("myTag","gmail");
            }
            Log.d("myTag","eyesfood");
            SessionPrefs.get(HistoryActivity.this).logOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.bt_nav_history) {
            drawerSelection = 1;
        } else if (id == R.id.bt_nav_upload) {
            drawerSelection = 2;
        } else if (id == R.id.bt_nav_favorite) {
            drawerSelection = 3;
        } else if (id == R.id.bt_nav_rejected) {
            drawerSelection = 4;
        } else if (id == R.id.bt_nav_profile) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                            //menuItem.setChecked(true);
                        // Crear nuevo fragmento
                        String title = menuItem.getTitle().toString();
                        selectItem(title);
                        return true;
                    }
                }
        );
    }

    private void selectItem(String title) {
        //Lo esconde el profile así que aquí lo reestablezco
        fab.setVisibility(View.VISIBLE);
        fabProfile.collapse();
        fabProfile.setVisibility(View.GONE);
        showProgress(true,"");

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment, fragmentHelp, fragmentExpert;
        // Enviar título como arguemento del fragmento
        if(title.equals(getResources().getString(R.string.nav_profile))){
            fab.setVisibility(View.GONE);
            fabProfile.setVisibility(View.VISIBLE);
            animateFabMenu(fabProfile);
            setTitle = 1;
            perfil = 1;
            showProgress(false,"PERFIL");
            Bundle args = new Bundle();
            args.putString(ProfileFragment.ARG_SECTION_TITLE, title);

            Fragment fragmentProfile = ProfileFragment.newInstance(title);
            fragmentProfile.setArguments(args);

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.llMainActivity, fragmentProfile, "fragmento_perfil")
                    .commit();
        }
        else if(title.equals(getResources().getString(R.string.nav_experts))){
            showSelectedDialog(0);
            //Si vengo del perfil oculto el recycler
            if (perfil == 1) {
                showProgress(false,"PERFIL");
                fab.setVisibility(View.GONE);
                fabProfile.setVisibility(View.VISIBLE);
            }
            else{
                showProgress(false,"");
            }
            setTitle = 0;
        }
        else if(title.equals(getResources().getString(R.string.nav_settings))){
            setTitle = 0;
        }
        else if(title.equals(getResources().getString(R.string.nav_help))){
            showSelectedDialog(1);
            //Si vengo del perfil oculto el recycler y muestro el fab correspondiente
            if (perfil == 1) {
                showProgress(false,"PERFIL");
                fab.setVisibility(View.GONE);
                fabProfile.setVisibility(View.VISIBLE);
            }
            else{
                showProgress(false,"");
            }
            setTitle = 0;
        }
        else{
            if(perfil == 1){
                //Si vengo de perfil animo el fab
                animateFab(fab);
                perfil = 0;
            }
            setTitle = 1;
            //Llamar a show foods con otros argumentos
            fragment = fragmentManager.findFragmentByTag("fragmento_perfil");
            fragmentExpert = fragmentManager.findFragmentByTag("fragmento_expertos");
            fragmentHelp = fragmentManager.findFragmentByTag("fragmento_ayuda");

            //Si está el fragmento del perfil se elimina
            if(fragment != null){
                Log.d("fragmento","Está el perfil");
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            if(fragmentExpert != null){
                Log.d("fragmento","Está expertos");
                fragmentManager.beginTransaction().remove(fragmentExpert).commit();
            }
            if(fragmentHelp != null){
                Log.d("fragmento","Está ayudas");
                fragmentManager.beginTransaction().remove(fragmentHelp).commit();
            }
            //Si no está se hacen las llamadas sin eliminar nada
            //Este se llama con el título de la vista para seleccionar la lista
            loadHistoryFoods(userIdFinal, title);
        }
        if(setTitle == 1) {
            // Setear título actual
            // TODO: 23-11-2017 toolbar.setTitle no funcionaba la primera vez que entraba a la aplicación
            setTitle(title);
            toolbar.setTitle(title);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showProgress(boolean show, String bandera) {
        if(show) {
            progressBar.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }
        else{
            if(bandera.equals("PERFIL")){
                progressBar.setVisibility(View.GONE);
            }
            else{
                progressBar.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
            }

        }
    }

    private void showEmptyState(boolean show){
        if(show){
            emptyStateText.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }
        else{
            emptyStateText.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    private void showSelectedDialog(int seleccion){
        //Bundle bundle = new Bundle();

        FragmentManager fragmentManager = getSupportFragmentManager();

        ExpertsFragment expertsFragment = new ExpertsFragment();
        HelpFragment helpFragment = new HelpFragment();

        NewMeasureFragment newMeasureFragment = new NewMeasureFragment();
        EditMeasureFragment editMeasureFragment = new EditMeasureFragment();
        //expertsFragment.setArguments(bundle);
        //newFragmentUpload.setArguments(bundle);


        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        //transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        /*transaction.add(android.R.id.content, expertsFragment, "fragmento_expertos").addToBackStack(null);
        transaction.add(android.R.id.content, helpFragment, "fragmento_ayuda").addToBackStack(null);*/

        if(seleccion == 0){
            //transaction.replace(android.R.id.content, expertsFragment);
            transaction.add(android.R.id.content, expertsFragment, "fragmento_expertos").addToBackStack(null);
        }
        else if (seleccion ==1){
            //transaction.replace(android.R.id.content, helpFragment);
            transaction.add(android.R.id.content, helpFragment, "fragmento_ayuda").addToBackStack(null);
        }
        else if (seleccion ==2){
            //transaction.replace(android.R.id.content, helpFragment);
            transaction.add(android.R.id.content, newMeasureFragment, "fragmento_nueva_medida").addToBackStack(null);
        }
        else if (seleccion ==3){
            //transaction.replace(android.R.id.content, helpFragment);
            transaction.add(android.R.id.content, editMeasureFragment, "fragmento_editar_medida").addToBackStack(null);
        }
        transaction.commit();
    }

    private void animateFab(final FloatingActionButton fab) {
        fab.setScaleX(0);
        fab.setScaleY(0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                    android.R.interpolator.overshoot);

            fab.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(interpolador)
                    .setDuration(600)
                    .setStartDelay(400)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fab.animate()
                                    .scaleY(1)
                                    .scaleX(1)
                                    .setInterpolator(interpolador)
                                    .setDuration(600)
                                    .start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }

    private void animateFabMenu(final FloatingActionsMenu fab){
        fab.setScaleX(0);
        fab.setScaleY(0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                    android.R.interpolator.overshoot);

            fab.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(interpolador)
                    .setDuration(600)
                    .setStartDelay(400)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fab.animate()
                                    .scaleY(1)
                                    .scaleX(1)
                                    .setInterpolator(interpolador)
                                    .setDuration(600)
                                    .start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }
}