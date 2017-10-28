package com.example.jonsmauricio.eyesfood.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.ApiError;
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.Ingredient;
import com.example.jonsmauricio.eyesfood.data.api.model.LoginBody;
import com.example.jonsmauricio.eyesfood.data.api.model.NewFoodBody;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.example.jonsmauricio.eyesfood.data.api.model.User;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComplaintDialogFragment extends DialogFragment {
    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    TextView tvCodigoBarras;

    EditText etNombre, etProducto, etMarca, etNeto, etPorcion, etPorcionUnit, etEnergia, etProteinas, etGrasaTotal,
            etGrasaSat, etGrasaMono, etGrasaPoli, etGrasaTrans, etColesterol, etHidratos, etAzucares, etFibra, etSodio,
            etIngredientes;

    TextInputLayout tilNombre, tilProducto, tilMarca, tilNeto, tilPorcion, tilPorcionUnit, tilEnergia, tilProteinas,
            tilGrasaTotal, tilGrasaSat, tilGrasaMono, tilGrasaPoli, tilGrasaTrans, tilColesterol, tilHidratos, tilAzucares,
            tilFibra, tilSodio, tilIngredientes;

    String Nombre, Producto, Marca, Porcion, PorcionUnit, Ingredientes, Neto, Energia, Proteinas, GrasaTotal, GrasaSat,
            GrasaMono, GrasaPoli, GrasaTrans, Colesterol, Hidratos, Azucares, Fibra, Sodio;

    String NombreOriginal, ProductoOriginal, MarcaOriginal, PorcionOriginal, PorcionUnitOriginal, NetoOriginal,
            EnergiaOriginal, ProteinasOriginal, GrasaTotalOriginal, GrasaSatOriginal, GrasaMonoOriginal,
            GrasaPoliOriginal, GrasaTransOriginal, ColesterolOriginal, HidratosOriginal, AzucaresOriginal, FibraOriginal,
            SodioOriginal, Unit;

    String Date = "";

    String barCode;

    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;

    //Obtengo token e id de Usuario
    private String userIdFinal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.dialog_new_foods, container, false);

        barCode = getArguments().getString("barCode");
        NombreOriginal = getArguments().getString("Nombre");
        ProductoOriginal = getArguments().getString("Producto");
        MarcaOriginal = getArguments().getString("Marca");
        NetoOriginal = getArguments().getString("Neto");
        PorcionOriginal = getArguments().getString("Porcion");
        PorcionUnitOriginal = getArguments().getString("PorcionUnit");
        EnergiaOriginal = getArguments().getString("Energia");
        ProteinasOriginal = getArguments().getString("Proteinas");
        GrasaTotalOriginal = getArguments().getString("GrasaTotal");
        GrasaSatOriginal = getArguments().getString("GrasaSat");
        GrasaMonoOriginal = getArguments().getString("GrasaMono");
        GrasaPoliOriginal = getArguments().getString("GrasaPoli");
        GrasaTransOriginal = getArguments().getString("GrasaTrans");
        ColesterolOriginal = getArguments().getString("Colesterol");
        HidratosOriginal = getArguments().getString("Hidratos");
        AzucaresOriginal = getArguments().getString("Azucares");
        FibraOriginal = getArguments().getString("Fibra");
        SodioOriginal = getArguments().getString("Sodio");
        Unit = getArguments().getString("Unit");

        Toolbar toolbar = view.findViewById(R.id.toolbarNewFoods);
        toolbar.setTitle(getResources().getString(R.string.title_complaint_foods));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_close_black_24dp);
        }

        setHasOptionsMenu(true);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tvCodigoBarras = getView().findViewById(R.id.tvNewFoodsInfoGeneralCodigo);
        tvCodigoBarras.append(" "+barCode);

        etNombre = getView().findViewById(R.id.etNewFoodsInfoGeneralTitulo);
        tilNombre = getView().findViewById(R.id.tvNewFoodsInfoGeneralNombre);
        tilNombre.setHint(tilNombre.getHint() + " " + NombreOriginal);
        etProducto = getView().findViewById(R.id.etNewFoodsInfoGeneralProducto);
        tilProducto = getView().findViewById(R.id.tvNewFoodsInfoGeneralProducto);
        tilProducto.setHint(tilProducto.getHint() + " " + ProductoOriginal);
        etMarca = getView().findViewById(R.id.etNewFoodsInfoGeneralMarca);
        tilMarca = getView().findViewById(R.id.tvNewFoodsInfoGeneralMarca);
        tilMarca.setHint(tilMarca.getHint() + " " + MarcaOriginal);
        etNeto = getView().findViewById(R.id.etNewFoodsInfoGeneralNeto);
        tilNeto = getView().findViewById(R.id.tvNewFoodsInfoGeneralNeto);
        tilNeto.setHint(tilNeto.getHint() + " " + NetoOriginal + Unit);

        etPorcion = getView().findViewById(R.id.etNewFoodsInfoNutricionalPorcion);
        tilPorcion = getView().findViewById(R.id.tvNewFoodsInfoNutricionalPorcion);
        tilPorcion.setHint(tilPorcion.getHint() + " " + PorcionOriginal);
        etPorcionUnit = getView().findViewById(R.id.etNewFoodsInfoNutricionalPorcionUnit);
        tilPorcionUnit = getView().findViewById(R.id.tvNewFoodsInfoNutricionalPorcionUnit);
        tilPorcionUnit.setHint(tilPorcionUnit.getHint() + " " + PorcionUnitOriginal + Unit);
        etEnergia = getView().findViewById(R.id.etNewFoodsInfoNutricionalEnergia);
        tilEnergia = getView().findViewById(R.id.tvNewFoodsInfoNutricionalEnergia);
        tilEnergia.setHint(tilEnergia.getHint() + " " + EnergiaOriginal);
        etProteinas = getView().findViewById(R.id.etNewFoodsInfoNutricionalProteinas);
        tilProteinas = getView().findViewById(R.id.tvNewFoodsInfoNutricionalProteinas);
        tilProteinas.setHint(tilProteinas.getHint() + " " + ProteinasOriginal);
        etGrasaTotal = getView().findViewById(R.id.etNewFoodsInfoNutricionalGrasaTotal);
        tilGrasaTotal = getView().findViewById(R.id.tvNewFoodsInfoNutricionalGrasaTotal);
        tilGrasaTotal.setHint(tilGrasaTotal.getHint() + " " + GrasaTotalOriginal);
        etGrasaSat = getView().findViewById(R.id.etNewFoodsInfoNutricionalGrasaSat);
        tilGrasaSat = getView().findViewById(R.id.tvNewFoodsInfoNutricionalGrasaSat);
        tilGrasaSat.setHint(tilGrasaSat.getHint() + " " + GrasaSatOriginal);
        etGrasaMono = getView().findViewById(R.id.etNewFoodsInfoNutricionalGrasaMono);
        tilGrasaMono= getView().findViewById(R.id.tvNewFoodsInfoNutricionalGrasaMono);
        tilGrasaMono.setHint(tilGrasaMono.getHint() + " " + GrasaMonoOriginal);
        etGrasaPoli = getView().findViewById(R.id.etNewFoodsInfoNutricionalGrasaPoli);
        tilGrasaPoli = getView().findViewById(R.id.tvNewFoodsInfoNutricionalGrasaPoli);
        tilGrasaPoli.setHint(tilGrasaPoli.getHint() + " " + GrasaPoliOriginal);
        etGrasaTrans = getView().findViewById(R.id.etNewFoodsInfoNutricionalGrasaTrans);
        tilGrasaTrans = getView().findViewById(R.id.tvNewFoodsInfoNutricionalGrasaTrans);
        tilGrasaTrans.setHint(tilGrasaTrans.getHint() + " " + GrasaTransOriginal);
        etColesterol = getView().findViewById(R.id.etNewFoodsInfoNutricionalColesterol);
        tilColesterol = getView().findViewById(R.id.tvNewFoodsInfoNutricionalColesterol);
        tilColesterol.setHint(tilColesterol.getHint() + " " + ColesterolOriginal);
        etHidratos = getView().findViewById(R.id.etNewFoodsInfoNutricionalHidratos);
        tilHidratos = getView().findViewById(R.id.tvNewFoodsInfoNutricionalHidratos);
        tilHidratos.setHint(tilHidratos.getHint() + " " + HidratosOriginal);
        etAzucares = getView().findViewById(R.id.etNewFoodsInfoNutricionalAzucares);
        tilAzucares = getView().findViewById(R.id.tvNewFoodsInfoNutricionalAzucares);
        tilAzucares.setHint(tilAzucares.getHint() + " " + AzucaresOriginal);
        etFibra = getView().findViewById(R.id.etNewFoodsInfoNutricionalFibra);
        tilFibra = getView().findViewById(R.id.tvNewFoodsInfoNutricionalFibra);
        tilFibra.setHint(tilFibra.getHint() + " " + FibraOriginal);
        etSodio = getView().findViewById(R.id.etNewFoodsInfoNutricionalSodio);
        tilSodio = getView().findViewById(R.id.tvNewFoodsInfoNutricionalSodio);
        tilSodio.setHint(tilSodio.getHint() + " " + SodioOriginal);

        etIngredientes = getView().findViewById(R.id.etNewFoodsIngredients);

        userIdFinal = SessionPrefs.get(getContext()).getUserId();
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_foods, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_foods_complaint).setVisible(false);
        menu.findItem(R.id.action_foods_add_photos).setVisible(false);
        menu.findItem(R.id.action_foods_settings).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sendFood) {
            Nombre = etNombre.getText().toString();
            Producto = etProducto.getText().toString();
            Marca = etMarca.getText().toString();
            Neto = etNeto.getText().toString();

            Porcion = etPorcion.getText().toString();
            PorcionUnit = etPorcionUnit.getText().toString();
            Energia = etEnergia.getText().toString();
            Proteinas = etProteinas.getText().toString();
            GrasaTotal = etGrasaTotal.getText().toString();
            GrasaSat = etGrasaSat.getText().toString();
            GrasaMono = etGrasaMono.getText().toString();
            GrasaPoli = etGrasaPoli.getText().toString();
            GrasaTrans = etGrasaTrans.getText().toString();
            Colesterol = etColesterol.getText().toString();
            Hidratos = etHidratos.getText().toString();
            Azucares = etAzucares.getText().toString();
            Fibra = etFibra.getText().toString();
            Sodio = etSodio.getText().toString();

            Ingredientes = etIngredientes.getText().toString();

            //Esta comprobación es la adecuada
            if(Nombre.equals("") && Producto.equals("") && Marca.equals("") && Neto.equals("") && Porcion.equals("") &&
                    PorcionUnit.equals("") && Energia.equals("") && Proteinas.equals("") && GrasaTotal.equals("") &&
                    GrasaSat.equals("") && GrasaMono.equals("") && GrasaPoli.equals("") && GrasaTrans.equals("") &&
                    Colesterol.equals("") && Hidratos.equals("") && Azucares.equals("") && Fibra.equals("") && Sodio.equals("")
                    && Ingredientes.equals("")){
                showEmptySolitudeDialog();
            }
            else{
                sendSolitude();
            }
            return true;
        } else if(id == android.R.id.home){
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendSolitude(){
        Call<Food> call = mEyesFoodApi.newFoodSolitude(new NewFoodBody(userIdFinal, barCode, Nombre, Producto, Marca,
                Neto, Porcion, PorcionUnit, Energia, Proteinas, GrasaTotal, GrasaSat, GrasaMono, GrasaPoli, GrasaTrans,
                Colesterol, Hidratos, Azucares, Fibra, Sodio, Ingredientes, Date));
        call.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                if (!response.isSuccessful()) {

                    return;
                }
                showSuccesDialog();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Log.d("Falla Retrofit", "Falla en new food solitude");
                Log.d("Falla", t.getMessage());
            }
        });
    }

    public void showSuccesDialog(){
        new AlertDialog.Builder(getContext())
                .setIcon(null)
                .setTitle(getResources().getString(R.string.title_success_solitude_new_foods))
                .setMessage(getResources().getString(R.string.message_success_solitude_new_foods))
                .setPositiveButton(getResources().getString(R.string.ok_dialog), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }

                })
                .show();
    }

    public void showEmptySolitudeDialog(){
        new AlertDialog.Builder(getContext())
                .setIcon(null)
                .setTitle(getResources().getString(R.string.title_failed_solitude_new_foods))
                .setMessage(getResources().getString(R.string.message_failed_solitude_new_foods))
                .setPositiveButton(getResources().getString(R.string.ok_dialog), null)
                .show();
    }

}