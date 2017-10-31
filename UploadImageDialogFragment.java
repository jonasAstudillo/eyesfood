package com.example.jonsmauricio.eyesfood.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.ImageView;
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

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static com.example.jonsmauricio.eyesfood.R.id.imageView;

public class UploadImageDialogFragment extends DialogFragment {
    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    //Bitmap to get image from gallery
    private Bitmap bitmap;

    CardView fromCamera, fromGallery;
    ImageView photoSelected;

    String barCode;

    Retrofit mRestAdapter;
    EyesFoodApi mEyesFoodApi;

    //Obtengo token e id de Usuario
    private String userIdFinal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.dialog_upload_images, container, false);

        //barCode = getArguments().getString("barCode");

        Toolbar toolbar = view.findViewById(R.id.toolbarUploadImages);
        toolbar.setTitle(getResources().getString(R.string.title_upload_images));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_close_black_24dp);
        }

        setHasOptionsMenu(true);

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
        fromCamera = getView().findViewById(R.id.cvUploadCamera);
        fromGallery = getView().findViewById(R.id.cvUploadGallery);
        photoSelected = getView().findViewById(R.id.iv_upload_photo_selected);

        fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        fromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

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

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                Log.d("myTag",String.valueOf(filePath));
                photoSelected.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sendFood) {
            //getting the actual path of the image
            String path = getPath(filePath);

            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(getContext(), uploadId, "http://192.168.0.101/api.eyesfood.cl/v1/images/2/7802820701210/")
                        .addFileToUpload(path, "myFile") //Adding file
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
            dismiss();
            return true;
        } else if(id == android.R.id.home){
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContext().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    /*public void sendSolitude(){
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
    }*/

}