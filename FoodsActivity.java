package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.Ingredient;
import com.example.jonsmauricio.eyesfood.data.api.model.Recommendation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    Clase controladora de la actividad de alimentos
*/

// TODO: 19-10-2017 Hacer la vista de info nutricional 
// TODO: 19-10-2017 Ver si hay que hacerlo con fragments

public class FoodsActivity extends AppCompatActivity implements View.OnClickListener {

    //Para la info general
    TextView infoGeneralNombre, infoGeneralProducto, infoGeneralCodigo, infoGeneralMarca, infoGeneralNeto,
            infoGeneralFecha, tvIngredientes;

    //Para la info nutricional
    TextView porcion, porcionEnvase, energia100, energiaPorcion, proteinas100, proteinasPorcion, grasaTotal100,
    grasaTotalPorcion, grasaSaturada100, grasaSaturadaPorcion, grasaMono100, grasaMonoPorcion, grasaPoli100, grasaPoliPorcion,
    grasaTrans100, grasaTransPorcion, colesterol100, colesterolPorcion, hidratos100, hidratosPorcion, azucares100,
    azucaresPorcion, fibra100, fibraPorcion, sodio100, sodioPorcion;

    String CodigoBarras, NombreMarca, Producto, Nombre, Fecha, OfficialPhoto;

    String Porcion, PorcionUnit, Energia, Proteinas, GrasaTotal, GrasaSat, GrasaMono, GrasaPoli, GrasaTrans,
            Colesterol, Hidratos, Azucares, Fibra, Sodio, Unit;


    int Neto;

    float Peligro;
    RatingBar infoGeneralRating;

    //Para los botonos
    Button additives, recommendations;

    private List<Ingredient> listaIngredientes;
    private List<Ingredient> listaAditivos;
    private List<Recommendation> listaRecomendaciones;
    ImageView ivFoodPhoto;

    Retrofit mRestAdapter;
    private EyesFoodApi mEyesFoodApi;

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

        additives.setOnClickListener(this);
        recommendations.setOnClickListener(this);

        ivFoodPhoto = (ImageView) findViewById(R.id.image_paralax);
        final CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.collapser);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.foods_app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

        //TODO: Revisar si esto se ve bien
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (scrollRange == -1) {
                scrollRange = appBarLayout.getTotalScrollRange();
            }
            if (scrollRange + verticalOffset == 0) {
                //collapser.setTitle("Title");
                isShow = true;
            } else if(isShow) {
                //collapser.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                isShow = false;
            }
        }
        });

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
            collapser.setTitle(Nombre); // Cambiar título
            //setTitle(Nombre);
            CodigoBarras = (String) b.get("CodigoBarras");
            loadFoods(CodigoBarras);
            loadIngredients(CodigoBarras);
            loadRecommendations(CodigoBarras);
        }
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
                showFood(resultado);
                showNutritionFacts(resultado);
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {

            }
        });
    }

    //Carga los datos del alimento al iniciar la pantalla
    //alimento: Alimento a cargar
    public void showFood(Food alimento){

        Nombre = alimento.getName();
        Producto = alimento.getProductId();
        Peligro = alimento.getFoodHazard();
        Fecha = alimento.getDate();
        NombreMarca = alimento.getBrandCode();
        Neto = alimento.getContent();

        OfficialPhoto = alimento.getOfficialPhoto();

        Picasso.with(this)
                .load(baseFotoAlimento + OfficialPhoto)
                .into(ivFoodPhoto);

        infoGeneralNombre.setText(Nombre);
        infoGeneralProducto.setText(Producto);
        infoGeneralRating.setRating(Peligro);
        infoGeneralCodigo.append(" "+CodigoBarras);
        infoGeneralMarca.append(" "+NombreMarca);
        infoGeneralNeto.append(" "+Neto+" "+alimento.getUnit());
        infoGeneralFecha.append(" "+Fecha);
    }

    //Muestra la información nutricional del alimento
    public void showNutritionFacts(Food alimento){
        Porcion = alimento.getPortion();
        PorcionUnit = String.valueOf(alimento.getPortionGr());
        Unit = alimento.getUnit();

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

        Energia = String.valueOf(energia100.getText());
        Proteinas = String.valueOf(proteinas100.getText());
        GrasaTotal = String.valueOf(grasaTotal100.getText());
        GrasaSat = String.valueOf(grasaSaturada100.getText());
        GrasaMono = String.valueOf(grasaMono100.getText());
        GrasaPoli = String.valueOf(grasaPoli100.getText());
        GrasaTrans = String.valueOf(grasaTrans100.getText());
        Colesterol = String.valueOf(colesterol100.getText());
        Hidratos = String.valueOf(hidratos100.getText());
        Azucares = String.valueOf(azucares100.getText());
        Fibra = String.valueOf(fibra100.getText());
        Sodio = String.valueOf(sodio100.getText());
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
                String cantidadAditivos = String.valueOf(listaAditivos.size());
                additives.setText("Aditivos ("+cantidadAditivos+")");
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
                String cantidadRecomendaciones = String.valueOf(listaRecomendaciones.size());
                recommendations.setText("Recomendaciones ("+cantidadRecomendaciones+")");
            }

            @Override
            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
            }
        });
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

            case R.id.btFoodsAdditives: {
                if(listaAditivos.size()>0) {
                    Intent intent = new Intent(this, AdditivesActivity.class);
                    intent.putExtra("CodigoBarras", CodigoBarras);
                    intent.putExtra("Nombre", Nombre);
                    startActivity(intent);
                }
                else{
                    hacerToast(getResources().getString(R.string.dialog_no_additives));
                }
                break;
            }
            case R.id.btFoodsRecommendations: {
                if(listaRecomendaciones.size()>0) {
                    Intent intent = new Intent(this, RecommendationsActivity.class);
                    intent.putExtra("CodigoBarras", CodigoBarras);
                    intent.putExtra("Nombre", Nombre);
                    startActivity(intent);
                }
                else{
                    hacerToast(getResources().getString(R.string.dialog_no_recommendations));
                }
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_foods_complaint:{
                Log.d("myTag", "Apreto el botón");
                showComplaintFoodsDialog();
            }
        }

        return(super.onOptionsItemSelected(item));
    }

    public void hacerToast(String contenido){
        Toast.makeText(this, contenido, Toast.LENGTH_LONG).show();
    }

    private void showComplaintFoodsDialog(){
        //TODO: Poner los otros datos en variables y mandarlos
        Bundle bundle = new Bundle();
        bundle.putString("barCode", CodigoBarras);
        bundle.putString("Nombre", Nombre);
        bundle.putString("Producto", Producto);
        bundle.putString("Marca", NombreMarca);
        bundle.putString("Neto", String.valueOf(Neto));
        bundle.putString("Porcion", String.valueOf(Porcion));
        bundle.putString("PorcionUnit", String.valueOf(PorcionUnit));
        bundle.putString("Energia", String.valueOf(Energia));
        bundle.putString("Proteinas", String.valueOf(Proteinas));
        bundle.putString("GrasaTotal", String.valueOf(GrasaTotal));
        bundle.putString("GrasaSat", String.valueOf(GrasaSat));
        bundle.putString("GrasaMono", String.valueOf(GrasaMono));
        bundle.putString("GrasaPoli", String.valueOf(GrasaPoli));
        bundle.putString("GrasaTrans", String.valueOf(GrasaTrans));
        bundle.putString("Colesterol", String.valueOf(Colesterol));
        bundle.putString("Hidratos", String.valueOf(Hidratos));
        bundle.putString("Azucares", String.valueOf(Azucares));
        bundle.putString("Fibra", String.valueOf(Fibra));
        bundle.putString("Sodio", String.valueOf(Sodio));
        bundle.putString("Unit", String.valueOf(Unit));
        // set Fragmentclass Arguments

        FragmentManager fragmentManager = getSupportFragmentManager();
        ComplaintDialogFragment newFragment = new ComplaintDialogFragment();
        newFragment.setArguments(bundle);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }
}