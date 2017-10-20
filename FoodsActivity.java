package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Additive;
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.Ingredient;
import com.example.jonsmauricio.eyesfood.data.api.model.Recommendation;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
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

//TODO: Hacer con expandible view
//TODO: Hacer que las listas no sean scrollable y muestren todos los datos 
// TODO: 19-10-2017 Hacer la vista de info nutricional 
// TODO: 19-10-2017 Ver si hay que hacerlo con fragments

public class FoodsActivity extends AppCompatActivity {

    TextView infoGeneral, infoNutricional, tvIngredientes, tvAditivos;
    String CodigoBarras, NombreMarca, PeligroAlimento, Producto, Nombre, Porcion, OfficialPhoto, eCode;
    int ContenidoNeto;
    float Energia, Proteinas, GrasaTotal, GrasaSaturada, GrasaTrans, GrasaMono, GrasaPoli, HidratosCarbono,
            AzucaresTotales, Fibra, Sodio, PorcionGramos, IndiceGlicemico;

    private List<Ingredient> listaIngredientes;
    private List<Ingredient> listaAditivos;
    private ArrayAdapter<Ingredient> adaptador;
    private ListView lvAditivos;
    ImageView ivFoodPhoto;

    Retrofit mRestAdapter;
    private EyesFoodApi mEyesFoodApi;

    //Obtengo token de Usuario
    private String tokenFinal;

    //IP de usach alumnos:
    //private final String baseFotoAlimento = "http://158.170.214.219/api.eyesfood.cl/v1/img/food/";
    //URL Base para cargar las fotos
    final String baseFotoAlimento = EyesFoodApi.BASE_URL+"img/food/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        infoNutricional = (TextView) findViewById(R.id.tvInfoNutricional);
        infoGeneral = (TextView) findViewById(R.id.tvInfoGeneral);
        tvIngredientes = (TextView) findViewById(R.id.tvIngredientes);
        tvAditivos = (TextView) findViewById(R.id.tvAditivos);
        lvAditivos = (ListView) findViewById(R.id.lvAditivos);
        ivFoodPhoto = (ImageView) findViewById(R.id.ivPrueba);

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

        //Recibe los datos enviados por el scanner o lista
        if(b!=null)
        {
            Nombre = (String) b.get("Nombre");
            setTitle(Nombre);
            CodigoBarras = (String) b.get("CodigoBarras");
            loadFoods(tokenFinal, CodigoBarras);
            loadIngredients(tokenFinal, CodigoBarras);
            loadRecommendations(tokenFinal, CodigoBarras);
        }

        //On click para la lista de aditivos
        lvAditivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Ingredient currentIngredient = adaptador.getItem(position);
                eCode = currentIngredient.getIdIngredient();
                loadAdditive(tokenFinal, eCode);
            }
        });
    }

    //Envía a la página de aditivos al pinchar en la lista
    //Token: Token de autorización
    //eCode: Código del aditivo
    public void loadAdditive(String token, String eCode) {
        Call<Additive> call = mEyesFoodApi.getAdditive(token, eCode);
        call.enqueue(new Callback<Additive>() {
            @Override
            public void onResponse(Call<Additive> call,
                                   Response<Additive> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                Additive aditivo = response.body();
                showAdditive(aditivo);
            }

            @Override
            //Si no existe la URL
            public void onFailure(Call<Additive> call, Throwable t) {
            }
        });
    }

    //Retorna un alimento
    //Token: Token de autorización
    //Barcode: Código de barras del alimento a retornar
    public void loadFoods(String token, String barcode) {
        Call<Food> call = mEyesFoodApi.getFood(token, barcode);
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
        NombreMarca = alimento.getBrandCode();
        PeligroAlimento = alimento.getFoodHazardId();
        Energia = alimento.getEnergy();
        Proteinas = alimento.getProtein();
        GrasaTotal = alimento.getTotalFat();
        HidratosCarbono = alimento.getCarbo();
        AzucaresTotales = alimento.getTotalFat();
        Sodio = alimento.getSodium();
        OfficialPhoto = alimento.getOfficialPhoto();

        Picasso.with(this)
                .load(baseFotoAlimento + OfficialPhoto)
                .into(ivFoodPhoto);

        infoGeneral.setText("La marca del producto " + Nombre + " es " + NombreMarca + " y su calificación es de " +
                PeligroAlimento);
        infoNutricional.setText("El producto tiene "+Energia+" calorías, "+Proteinas+"g de proteínas, "+GrasaTotal+
                "g de grasa total, " + HidratosCarbono+"g de hidratos de carbono, "+AzucaresTotales+"g de azúcares " +
                "totales, "+Sodio+"g de sodio cada 100 ml o g.");
    }

    //Carga los ingredientes del alimento
    //token: Autorización
    //barcode: Código de barras del alimento
    public void loadIngredients(String token, String barcode) {
        Call<List<Ingredient>> call = mEyesFoodApi.getIngredients(token, barcode);
        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call,
                                   Response<List<Ingredient>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaIngredientes = response.body();
                loadAdditives(tokenFinal, CodigoBarras, listaIngredientes);
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
    public void loadAdditives(String token, String barcode, final List<Ingredient> listaIngredientes) {
        Call<List<Ingredient>> call = mEyesFoodApi.getAdditives(token, barcode);
        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call,
                                   Response<List<Ingredient>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                listaAditivos = response.body();
                mostrarAditivos(listaAditivos);
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
        String ingredientes = "Ingredientes: ";
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

    //Muestra sólo los aditivos en una lista
    public void mostrarAditivos(List <Ingredient> lista){
        int tamano = lista.size();
        int i = 0;
        String aditivos = "Aditivos: ";
        adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                lista);
        lvAditivos.setAdapter(adaptador);
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
    public void loadRecommendations(String token, String barcode) {
        Call<List<Recommendation>> call = mEyesFoodApi.getRecommendations(token, barcode);
        call.enqueue(new Callback<List<Recommendation>>() {
            @Override
            public void onResponse(Call<List<Recommendation>> call,
                                   Response<List<Recommendation>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Recommendation> listaRecomendaciones = response.body();
                String recomendacion = listaRecomendaciones.get(0).getRecommendation();
                hacerToast(recomendacion);
            }

            @Override
            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
            }
        });
    }

    //Va hacia la actividad de aditivos para mostrar su información detallada
    public void showAdditive(Additive aditivo){
        Intent i = new Intent(this, AdditiveActivity.class);

        i.putExtra("Aditivo",aditivo.getAdditive());
        i.putExtra("Peligro",aditivo.getHazard());
        i.putExtra("Origen",aditivo.getSource());
        i.putExtra("Clasificacion",aditivo.getClassification());
        i.putExtra("Descripcion",aditivo.getDescription());
        i.putExtra("Uso",aditivo.getUsage());
        i.putExtra("EfectosSecundarios",aditivo.getSecondaryEffects());

        i.putExtra("CodigoE",eCode);
        i.putExtra("CodigoBarras",CodigoBarras);
        i.putExtra("Nombre",Nombre);

        startActivity(i);
    }

    //TODO: Método de prueba
    public void hacerToast(String i){
        Toast.makeText(this, i, Toast.LENGTH_LONG).show();
    }
}

