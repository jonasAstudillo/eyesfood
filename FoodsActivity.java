package com.example.jonsmauricio.eyesfood.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Additive;
import com.example.jonsmauricio.eyesfood.data.api.model.Comment;
import com.example.jonsmauricio.eyesfood.data.api.model.Counter;
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.FoodImage;
import com.example.jonsmauricio.eyesfood.data.api.model.HistoryFoodBody;
import com.example.jonsmauricio.eyesfood.data.api.model.Ingredient;
import com.example.jonsmauricio.eyesfood.data.api.model.InsertFromLikeBody;
import com.example.jonsmauricio.eyesfood.data.api.model.Recommendation;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    Clase controladora de la actividad de alimentos
*/

// TODO: 16-11-2017 Ver el color de los botones no presionados

public class FoodsActivity extends AppCompatActivity implements View.OnClickListener {

    //Para la info general
    TextView infoGeneralNombre, infoGeneralProducto, infoGeneralCodigo, infoGeneralMarca, infoGeneralNeto,
            infoGeneralFecha, tvIngredientes;

    //Para la info nutricional
    TextView porcion, porcionEnvase, energia100, energiaPorcion, proteinas100, proteinasPorcion, grasaTotal100,
    grasaTotalPorcion, grasaSaturada100, grasaSaturadaPorcion, grasaMono100, grasaMonoPorcion, grasaPoli100, grasaPoliPorcion,
    grasaTrans100, grasaTransPorcion, colesterol100, colesterolPorcion, hidratos100, hidratosPorcion, azucares100,
    azucaresPorcion, fibra100, fibraPorcion, sodio100, sodioPorcion;

    String CodigoBarras;
    RatingBar infoGeneralRating;
    private String userIdFinal;

    //Para los botonos
    Button additives, recommendations, images, like, dislike;

    private List<Ingredient> listaIngredientes;
    private List<Ingredient> listaAditivos;
    private List<Additive> listaAditivosFull;
    private List<Recommendation> listaRecomendaciones;
    private ArrayList<FoodImage> listaImagenes;
    private List<Comment> listaComentarios;
    ImageView ivFoodPhoto;

    Retrofit mRestAdapter;
    private EyesFoodApi mEyesFoodApi;

    private Food Alimento;
    private int MeGusta;

    private Counter likesCounter;
    private int likesCount;
    private Counter dislikesCounter;
    private int dislikesCount;

    //Permissions
    private static final int PERMISSION_CODE = 123;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    //IP de usach alumnos:
    //private final String baseFotoAlimento = "http://158.170.214.219/api.eyesfood.cl/v1/img/food/";
    //URL Base para cargar las fotos
    final String baseFotoAlimento = EyesFoodApi.BASE_URL+"img/food/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFoods);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabFoods);

        fab.setOnClickListener(this);

        //Para la info general
        infoGeneralNombre = (TextView) findViewById(R.id.tvFoodsInfoGeneralNombre);
        infoGeneralProducto = (TextView) findViewById(R.id.tvFoodsInfoGeneralProducto);
        infoGeneralCodigo = (TextView) findViewById(R.id.tvFoodsInfoGeneralCodigo);
        infoGeneralMarca = (TextView) findViewById(R.id.tvFoodsInfoGeneralMarca);
        infoGeneralNeto = (TextView) findViewById(R.id.tvFoodsInfoGeneralNeto);
        infoGeneralFecha = (TextView) findViewById(R.id.tvFoodsInfoGeneralFecha);
        infoGeneralRating = (RatingBar) findViewById(R.id.rbFoodsRating);

        //Para la info nutricional
        porcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalPorcion);
        porcionEnvase = (TextView) findViewById(R.id.tvFoodsInfoNutricionalPorcionEnvase);
        energia100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalEnergia100);
        energiaPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalEnergiaPorcion);
        proteinas100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalProteinas100);
        proteinasPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalProteinasPorcion);
        grasaTotal100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaTotal100);
        grasaTotalPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaTotalPorcion);
        grasaSaturada100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaSaturada100);
        grasaSaturadaPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaSaturadaPorcion);
        grasaMono100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaMono100);
        grasaMonoPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaMonoPorcion);
        grasaPoli100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaPoli100);
        grasaPoliPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaPoliPorcion);
        grasaTrans100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaTrans100);
        grasaTransPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalGrasaTransPorcion);
        colesterol100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalColesterol100);
        colesterolPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalColesterolPorcion);
        hidratos100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalHidratos100);
        hidratosPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalHidratosPorcion);
        azucares100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalAzucares100);
        azucaresPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalAzucaresPorcion);
        fibra100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalFibra100);
        fibraPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalFibraPorcion);
        sodio100 = (TextView) findViewById(R.id.tvFoodsInfoNutricionalSodio100);
        sodioPorcion = (TextView) findViewById(R.id.tvFoodsInfoNutricionalSodioPorcion);

        //Para los ingredientes
        tvIngredientes = (TextView) findViewById(R.id.tvFoodsIngredients);

        //Para los botones
        additives = (Button) findViewById(R.id.btFoodsAdditives);
        recommendations = (Button) findViewById(R.id.btFoodsRecommendations);
        images = (Button) findViewById(R.id.btFoodsImages);
        like = (Button) findViewById(R.id.btFoodsLike);
        dislike = (Button) findViewById(R.id.btFoodsDisLike);

        additives.setOnClickListener(this);
        recommendations.setOnClickListener(this);
        images.setOnClickListener(this);
        like.setOnClickListener(this);
        dislike.setOnClickListener(this);

        ivFoodPhoto = (ImageView) findViewById(R.id.image_paralax);
        final CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.collapser);

        userIdFinal = SessionPrefs.get(this).getUserId();

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
            Alimento = (Food) b.get("Alimento");
            collapser.setTitle(Alimento.getName()); // Cambiar título
            //setTitle(Nombre);
            CodigoBarras = Alimento.getBarCode();
            MeGusta = (int) b.get("MeGusta");
            Log.d("myTag","Like: "+MeGusta);
            showFood(Alimento);
            showNutritionFacts(Alimento);
            loadIngredients(CodigoBarras);
        }

        getLikesCount(CodigoBarras, like);
        getDisLikesCount(CodigoBarras, dislike);

        if(MeGusta == 1) {
            like.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        }
        else if(MeGusta == 2){
            dislike.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        }
    }

    //Carga los datos del alimento al iniciar la pantalla
    //alimento: Alimento a cargar
    public void showFood(Food alimento){

        Picasso.with(this)
                .load(baseFotoAlimento + alimento.getOfficialPhoto())
                .into(ivFoodPhoto);

        infoGeneralNombre.setText(alimento.getName());
        infoGeneralProducto.setText(alimento.getProductId());
        infoGeneralRating.setRating(alimento.getFoodHazard());
        infoGeneralCodigo.append(" "+alimento.getBarCode());
        infoGeneralMarca.append(" "+alimento.getBrandCode());
        infoGeneralNeto.append(" "+alimento.getContent()+" "+alimento.getUnit());
        infoGeneralFecha.append(" "+alimento.getDate());
    }

    //Muestra la información nutricional del alimento
    public void showNutritionFacts(Food alimento){

        float portion = alimento.getPortionGr();
        porcion.append(" "+alimento.getPortion());
        porcion.append(" ("+portion+" "+alimento.getUnit()+")");
        porcionEnvase.append(" " + Float.toString(calculatePortions(alimento.getContent(), portion)));

        setTextNutrition(alimento.getEnergy(), portion, energia100, energiaPorcion);
        setTextNutrition(alimento.getProtein(), portion, proteinas100, proteinasPorcion);
        setTextNutrition(alimento.getTotalFat(), portion, grasaTotal100, grasaTotalPorcion);
        setTextNutrition(alimento.getSaturatedFat(), portion, grasaSaturada100, grasaSaturadaPorcion);
        setTextNutrition(alimento.getMonoFat(), portion, grasaMono100, grasaMonoPorcion);
        setTextNutrition(alimento.getPoliFat(), portion, grasaPoli100, grasaPoliPorcion);
        setTextNutrition(alimento.getTransFat(), portion, grasaTrans100, grasaTransPorcion);
        setTextNutrition(alimento.getCholesterol(), portion, colesterol100, colesterolPorcion);
        setTextNutrition(alimento.getCarbo(), portion, hidratos100, hidratosPorcion);
        setTextNutrition(alimento.getTotalSugar(), portion, azucares100, azucaresPorcion);
        setTextNutrition(alimento.getFiber(), portion, fibra100, fibraPorcion);
        setTextNutrition(alimento.getSodium(), portion, sodio100, sodioPorcion);
    }

    public void setTextNutrition(float content, float portion, TextView tv100, TextView tvPortion){
        if(content < 0){
            tv100.setText("*");
            tvPortion.setText("*");
        }
        else{
            tv100.setText(Float.toString(content));
            tvPortion.setText(Float.toString(calculatePortion(portion, content)));
        }
    }

    //Calcula la cantidad de porciones por envase
    public float calculatePortions(float neto, float portion){
        float porcionesEnvase = neto/portion;
        return porcionesEnvase;
    }

    //Calcula los datos por porción
    public float calculatePortion(float portion, float data100){
        float resultado = (data100*portion)/100;
        return resultado;
    }

    //Carga los ingredientes del alimento
    //token: Autorización
    //barcode: Código de barras del alimento
    public void loadIngredients(String barcode) {
        Call<List<Ingredient>> call = mEyesFoodApi.getIngredients(barcode);
        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call,
                                   Response<List<Ingredient>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaIngredientes = response.body();
                loadAdditives(CodigoBarras, listaIngredientes);
            }

            @Override
            //Si no existe la URL
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                //Log.d("Falla Retrofit", t.getMessage());
            }
        });
    }

    //Carga los aditivos del alimento
    //listaIngredientes: Lista obtenida en load ingredients
    public void loadAdditives(String barcode, final List<Ingredient> listaIngredientes) {
        Call<List<Ingredient>> call = mEyesFoodApi.getAdditives(barcode);
        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call,
                                   Response<List<Ingredient>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaAditivos = response.body();
                //String cantidadAditivos = String.valueOf(listaAditivos.size());
                //additives.setText("Aditivos ("+cantidadAditivos+")");
                //Une las listas de ingredientes y de aditivos para efectuar el orden
                List<Ingredient> listaFinal = unirListas(listaIngredientes, listaAditivos);
                mostrarIngredientes(listaFinal);
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {

            }
        });
    }

    //Muestra los ingredientes ordenados en pantalla, incluye aditivos
    public void mostrarIngredientes(List <Ingredient> lista){
        int tamano = lista.size();
        int i = 0;
        String ingredientes = "";
        while(i < tamano){
            if(i==tamano-1) {
                ingredientes = ingredientes + lista.get(i).getIngredient() + ".";
            }
            else {
                ingredientes = ingredientes + lista.get(i).getIngredient() + ", ";
            }
            i++;
        }
        tvIngredientes.setText(ingredientes);
    }

    //Une las listas de ingredientes y aditivos para realizar el orden
    public List<Ingredient> unirListas(List<Ingredient> listaIngredientes, List<Ingredient> listaAditivos){

        List <Ingredient> listaIngredientesFinal = new ArrayList<>();
        int ingredientes = listaIngredientes.size();
        int aditivos = listaAditivos.size();
        int indiceIngredientes = 0;
        int indiceAditivos = 0;

        while (indiceIngredientes < ingredientes || indiceAditivos < aditivos) {
            if(indiceIngredientes == ingredientes){
                while(indiceAditivos < aditivos){
                    listaIngredientesFinal.add(listaAditivos.get(indiceAditivos));
                    indiceAditivos++;
                }
            }
            else if(indiceAditivos == aditivos){
                while(indiceIngredientes < ingredientes){
                    listaIngredientesFinal.add(listaIngredientes.get(indiceIngredientes));
                    indiceIngredientes++;
                }
            }
            else {
                int ordenIngrediente = listaIngredientes.get(indiceIngredientes).getOrder();
                int ordenAditivo = listaAditivos.get(indiceAditivos).getOrder();

                if (ordenIngrediente < ordenAditivo) {
                    listaIngredientesFinal.add(listaIngredientes.get(indiceIngredientes));
                    indiceIngredientes++;
                } else {
                    listaIngredientesFinal.add(listaAditivos.get(indiceAditivos));
                    indiceAditivos++;
                }
            }
        }
        return listaIngredientesFinal;
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
                listaRecomendaciones = response.body();
                showRecommendations(listaRecomendaciones);
            }

            @Override
            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
            }
        });
    }

    public void showRecommendations(List<Recommendation> lista){
        if(lista.size()>0) {
            Intent intent = new Intent(this, RecommendationsActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("Recomendaciones",(Serializable) lista);
            intent.putExtra("BUNDLE",args);

            intent.putExtra("Alimento",Alimento);
            intent.putExtra("MeGusta",MeGusta);
            startActivity(intent);
        }
        else{
            hacerToast(getResources().getString(R.string.dialog_no_recommendations));
        }
    }

    //Carga la lista de aditivos completa
    public void loadAdditivesFull(String barcode){
        Call<List<Additive>> call = mEyesFoodApi.getFullAdditives(barcode);
        call.enqueue(new Callback<List<Additive>>() {
            @Override
            public void onResponse(Call<List<Additive>> call,
                                   Response<List<Additive>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaAditivosFull = response.body();
                showAdditives(listaAditivosFull);
            }

            @Override
            public void onFailure(Call<List<Additive>> call, Throwable t) {
                Log.d("Falla", "Falla en la llamada de aditivos: loadAdditives");
            }
        });
    }

    public void showAdditives(List<Additive> listaAditivos){
        if(listaAditivos.size()>0) {
            Intent intent = new Intent(this, AdditivesActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("Aditivos",(Serializable) listaAditivos);
            intent.putExtra("BUNDLE",args);

            intent.putExtra("Alimento",Alimento);
            intent.putExtra("MeGusta",MeGusta);
            startActivity(intent);
        }
        else{
            hacerToast(getResources().getString(R.string.dialog_no_additives));
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

    public void showImages(ArrayList<FoodImage> lista){
        if(lista.size()>0) {
            Intent intent = new Intent(this, ImagesActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("Imagenes",lista);
            intent.putExtra("BUNDLE",args);

            intent.putExtra("Alimento",Alimento);
            intent.putExtra("MeGusta",MeGusta);
            startActivity(intent);
        }
        else{
            hacerToast(getResources().getString(R.string.dialog_no_images));
        }
    }

    //Carga los comentarios del alimento
    public void loadComments(String barcode) {
        Call<List<Comment>> call = mEyesFoodApi.getComments(barcode);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call,
                                   Response<List<Comment>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaComentarios = response.body();
                showComments(listaComentarios);
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
            }
        });
    }

    public void showComments(List<Comment> lista){
            Intent intent = new Intent(this, CommentsActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("Comentarios",(Serializable) lista);
            intent.putExtra("BUNDLE",args);

            intent.putExtra("Alimento",Alimento);
            intent.putExtra("MeGusta",MeGusta);
            startActivity(intent);
    }

    //Carga el menú a la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_foods, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fabFoods: {
                loadComments(CodigoBarras);
                break;
            }
            case R.id.btFoodsAdditives: {
                loadAdditivesFull(CodigoBarras);
                break;
            }
            case R.id.btFoodsRecommendations: {
                loadRecommendations(CodigoBarras);
                break;
            }
            case R.id.btFoodsImages: {
                loadImages(CodigoBarras);
                break;
            }
            case R.id.btFoodsLike: {
                Log.d("myTag",String.valueOf(MeGusta));
                if(MeGusta == 2){
                    //Si es 2 cambio el color a normal al dislike, acento para like
                    dislike.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.button_not_pressed, null));
                    dislikesCount--;
                    dislike.setText(String.valueOf(dislikesCount));
                    //dislike.setBackgroundResource(android.R.drawable.btn_default);
                    like.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                    likesCount++;
                    like.setText(String.valueOf(likesCount));
                    MeGusta = 1;
                    //Hacer patch
                }
                else if(MeGusta == 1){
                    //Si es 1 cambio el color a normal al like
                    like.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.button_not_pressed, null));
                    likesCount--;
                    like.setText(String.valueOf(likesCount));
                    MeGusta=0;
                    //Hacer patch
                }
                else{
                    //Si es 0 cambio el color a acento a like
                    like.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                    likesCount++;
                    like.setText(String.valueOf(likesCount));
                    MeGusta = 1;
                }
                //Veo si está en el historial
                isFoodInHistory(userIdFinal, CodigoBarras);
                break;
            }
            case R.id.btFoodsDisLike: {
                Log.d("myTag",String.valueOf(MeGusta));
                if(MeGusta == 2){
                    dislike.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.button_not_pressed, null));
                    dislikesCount--;
                    dislike.setText(String.valueOf(dislikesCount));
                    MeGusta = 0;
                    //Hacer patch
                }
                else if(MeGusta == 1){
                    like.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.button_not_pressed, null));
                    likesCount--;
                    like.setText(String.valueOf(likesCount));
                    dislike.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                    dislikesCount++;
                    dislike.setText(String.valueOf(dislikesCount));
                    MeGusta = 2;
                    //Hacer patch
                }
                else{
                    //Si es 0 cambio el color a acento a like
                    dislike.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                    dislikesCount++;
                    dislike.setText(String.valueOf(dislikesCount));
                    MeGusta = 2;
                }
                //Veo si está en el historial
                isFoodInHistory(userIdFinal, CodigoBarras);
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_foods_complaint:{
                showSelectedDialog(0);
                break;
            }
            case R.id.action_foods_add_photos:{
                if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED) {
                    showSelectedDialog(1);
                }
                else{
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
                    }
                    if (ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
                    }
                    if (ContextCompat.checkSelfPermission(this, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
                    }
                }
                break;
            }
        }

        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int i = 0;
        int permisos = 0;
        for(String permission: permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                showDialogs(i);
            }else{
                if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                    permisos++;
                    if(permisos==3){
                        Toast.makeText(this, getResources().getString(R.string.success_permission_add_photos), Toast.LENGTH_LONG).show();
                    }
                } else{
                    showDialogs(i);
                }
            }
            i++;
        }
    }

    public void showDialogs(int seleccion){
        if(seleccion == 0 || seleccion == 1){
            new AlertDialog.Builder(this)
                    .setIcon(null)
                    .setTitle(getResources().getString(R.string.title_foods_permission_rationale_storage))
                    .setMessage(getResources().getString(R.string.message_foods_permission_rationale_storage))
                    .setPositiveButton(getResources().getString(R.string.ok_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }

                    })
                    .show();
        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(null)
                    .setTitle(getResources().getString(R.string.title_foods_permission_rationale_camera))
                    .setMessage(getResources().getString(R.string.message_foods_permission_rationale_camera))
                    .setPositiveButton(getResources().getString(R.string.ok_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }

                    })
                    .show();
        }

    }

    public void hacerToast(String contenido){
        Toast.makeText(this, contenido, Toast.LENGTH_LONG).show();
    }

    private void showSelectedDialog(int seleccion){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Alimento", Alimento);
        // set Fragmentclass Arguments

        FragmentManager fragmentManager = getSupportFragmentManager();
        ComplaintDialogFragment newFragmentComplaint = new ComplaintDialogFragment();
        UploadImageDialogFragment newFragmentUpload = new UploadImageDialogFragment();
        newFragmentComplaint.setArguments(bundle);
        newFragmentUpload.setArguments(bundle);


        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        //transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        transaction.add(android.R.id.content, newFragmentUpload).addToBackStack(null);
        transaction.add(android.R.id.content, newFragmentComplaint).addToBackStack(null);

        if(seleccion == 0){
            transaction.replace(android.R.id.content, newFragmentComplaint);
        }
        else{
            transaction.replace(android.R.id.content, newFragmentUpload);
        }
        transaction.commit();
    }

    //Actualiza los me gusta del alimento
    public void getLikesCount(String barcode, final Button likes){
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
                    likesCount = likesCounter.getCount();
                    likes.setText(String.valueOf(likesCount));
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
    public void getDisLikesCount(String barcode, final Button dislikes){

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
                    dislikesCount = dislikesCounter.getCount();
                    dislikes.setText(String.valueOf(dislikesCount));
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

    //Actualiza los me gusta del alimento
    public void updateLikeHistory(String userId, final String barcode, int like){
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

    //Comprueba si el alimento consultado está en el historial del usuario
    public void isFoodInHistory(final String userId, final String barcode){
        Call<ShortFood> call = mEyesFoodApi.isInHistory(userId, barcode);
        call.enqueue(new Callback<ShortFood>() {
            @Override
            public void onResponse(Call<ShortFood> call,
                                   Response<ShortFood> response) {
                if (!response.isSuccessful()) {
                    Log.d("myTag","NO EXITOSO");
                    return;
                }
                //El alimento está en el historial
                Log.d("myTag","ESTÁ");
                updateLikeHistory(userIdFinal, CodigoBarras, MeGusta);
            }

            @Override
            public void onFailure(Call<ShortFood> call, Throwable t) {
                //El alimento no está y lo inserto
                Log.d("myTag","NO ESTÁ");
                insertNoScan(userIdFinal, CodigoBarras);
            }
        });
    }

    private void insertNoScan(String userIdFinal, String codigoBarras) {
        Call<Food> call = mEyesFoodApi.insertNoScan(new InsertFromLikeBody(userIdFinal, codigoBarras, MeGusta));
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
                Log.d("myTag", "Fallo en insertFood " + t.getMessage());
                return;
            }
        });
    }
}