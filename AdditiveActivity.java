package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.jonsmauricio.eyesfood.R;

/*
    Clase controladora de la actividad de aditivos
*/
//TODO: Hacer esta clase como un diálogo de pantalla completa
// TODO: 19-10-2017 Estilizar esta activity
public class AdditiveActivity extends AppCompatActivity {

    String CodigoE, Aditivo, Peligro, Origen, Clasificacion, Descripcion, Uso, EfectosSecundarios, CodigoBarras, Nombre;
    TextView aditivo_codigo_e, aditivo_aditivo, aditivo_peligro, aditivo_origen, aditivo_clasificacion,
            aditivo_descripcion, aditivo_uso, aditivo_efectos_secundarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAdditive);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        aditivo_codigo_e = (TextView) findViewById(R.id.aditivo_codigo_e);
        aditivo_aditivo = (TextView) findViewById(R.id.aditivo_aditivo);
        aditivo_peligro = (TextView) findViewById(R.id.aditivo_peligro);
        aditivo_origen = (TextView) findViewById(R.id.aditivo_origen);
        aditivo_clasificacion = (TextView) findViewById(R.id.aditivo_clasificacion);
        aditivo_descripcion = (TextView) findViewById(R.id.aditivo_descripcion);
        aditivo_uso = (TextView) findViewById(R.id.aditivo_uso);
        aditivo_efectos_secundarios = (TextView) findViewById(R.id.aditivo_efectos_secundarios);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        if(b!=null)
        {
            CodigoE = (String) b.get("CodigoE");
            Aditivo = (String) b.get("Aditivo");
            Peligro  = (String) b.get("Peligro");
            Origen = (String) b.get("Origen");
            Clasificacion = (String) b.get("Clasificacion");
            Descripcion = (String) b.get("Descripcion");
            Uso = (String) b.get("Uso");
            EfectosSecundarios = (String) b.get("EfectosSecundarios");

            setTitle(Aditivo + " ("+CodigoE+")");

            aditivo_codigo_e.setText(CodigoE);
            aditivo_aditivo.setText(Aditivo);
            aditivo_peligro.setText(Peligro);
            aditivo_origen.setText(Origen);
            aditivo_clasificacion.setText(Clasificacion);
            aditivo_descripcion.setText(Descripcion);
            aditivo_uso.setText(Uso);
            aditivo_efectos_secundarios.setText(EfectosSecundarios);

            CodigoBarras = (String) b.get("CodigoBarras");
            Nombre = (String) b.get("Nombre");
        }

    }

    //Carga el menú a la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_no_settings, menu);
        return true;
    }

    //Método para manejar el botón up
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
