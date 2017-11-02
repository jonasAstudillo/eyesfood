package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.model.FoodImage;

public class ImagesDetailActivity extends AppCompatActivity {

    public static final String VIEW_NAME_HEADER_IMAGE = "imagen_compartida";
    //Necesito la ruta para cargar la imagen
    private String ruta;
    //La fecha para ponerla abajo
    private String date;
    //El id de usuario para buscar su nombre y apellido
    private String idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    //Nombre del producto para el título
    private String Nombre;
    private ImageView extendedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        /*//Recibe los datos enviados por el scanner o lista
        if(b!=null)
        {
            ruta = (String) b.get("ruta");
            date = (String) b.get("ruta");
            setTitle(Nombre);
            CodigoBarras = (String) b.get("CodigoBarras");
            loadImages(CodigoBarras);
            Log.d("myTag","cargue con "+CodigoBarras);
        }
        // Obtener la imagen con el identificador establecido en la actividad principal
        ruta = Coche.getItem(getIntent().getIntExtra("idImagen", 0));

        imagenExtendida = (ImageView) findViewById(R.id.imagen_extendida);

        cargarImagenExtendida();
    }

    private void cargarImagenExtendida() {
        Glide.with(imagenExtendida.getContext())
                .load(itemDetallado.getIdDrawable())
                .into(imagenExtendida);
    }*/
    }

}
