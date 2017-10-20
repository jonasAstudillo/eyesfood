package com.example.jonsmauricio.eyesfood.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.data.api.model.*;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.example.jonsmauricio.eyesfood.data.api.*;
import com.example.jonsmauricio.eyesfood.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    Retrofit mRestAdapter;

    // UI references.
    private ImageView ivLoginLogo;
    private EditText mUserIdView;
    private EditText mPasswordView;
    private TextInputLayout mFloatEmail;
    private TextInputLayout mFloatLabelPassword;
    private View mProgressView;
    private View mLoginFormView;
    private EyesFoodApi mEyesFoodApi;
    TextView mSignUp;
    TextView loginProgressText;

    //Método que abre la pantalla principal
    private void showHistoryScreen() {
        startActivity(new Intent(this, HistoryActivity.class));
        showProgress(false);
        finish();
    }

    //Muestra el error en inicio de sesión
    private void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    //Verifico si hay Internet
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        ivLoginLogo = (ImageView) findViewById(R.id.image_logo);
        mUserIdView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mFloatEmail = (TextInputLayout) findViewById(R.id.float_label_user_id);
        mFloatLabelPassword = (TextInputLayout) findViewById(R.id.float_label_password);
        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mSignUp = (TextView) findViewById(R.id.link_signup);
        loginProgressText = (TextView) findViewById(R.id.login_progress_text);

        //Setup
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (!isOnline()) {
                        showLoginError(getString(R.string.error_network));
                        return false;
                    }
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButton.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
    }

    //Sustituye la acción realizada al pulsar el botón back
    @Override
    public void onBackPressed() {
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.email_sign_in_button:{
                if (!isOnline()) {
                    showLoginError(getString(R.string.error_network));
                    return;
                }
                attemptLogin();
                break;
            }
            case R.id.link_signup:{
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mFloatEmail.setError(null);
        mFloatLabelPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mUserIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mFloatLabelPassword.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mFloatLabelPassword.setError(getString(R.string.error_invalid_password));
            focusView = mFloatLabelPassword;
            cancel = true;
        }

        // Verificar si el ID tiene contenido.
        if (TextUtils.isEmpty(email)) {
            mFloatEmail.setError(getString(R.string.error_field_required));
            focusView = mFloatEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mFloatEmail.setError(getString(R.string.error_invalid_email));
            focusView = mFloatEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Mostrar el indicador de carga y luego iniciar la petición asíncrona.
            showProgress(true);
            Call<User> loginCall = mEyesFoodApi.login(new LoginBody(email, password));
            loginCall.enqueue(new Callback<User>() {
                @Override
                //Se ejecuta si hubo una respuesta HTTP normal
                public void onResponse(Call<User> call, Response<User> response) {
                    // Mostrar progreso
                    //showProgress(false);

                    // Procesar errores
                    //Es true si se obtienen códigos 200
                    if (!response.isSuccessful()) {
                        showProgress(false);
                        String error = "Ha ocurrido un error. Contacte al administrador";
                        //errorBody: El contenido plano de una respuesta con error
                        if (response.errorBody()
                                .contentType()
                                .subtype()
                                .equals("json")) {
                            ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                            error = apiError.getMessage();
                            Log.d("LoginActivity", apiError.getDeveloperMessage());
                        } else {
                            try {
                                // Reportar causas de error no relacionado con la API
                                Log.d("LoginActivity", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        showLoginError(error);
                        return;
                    }

                    // Guardar afiliado en preferencias
                    //body: Cuerpo deserializado de la petición exitosa
                    User usuario = response.body();
                    SessionPrefs.get(LoginActivity.this).saveUser(usuario);

                    // Ir a la pantalla principal
                    showHistoryScreen();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showProgress(false);
                    showLoginError(t.getMessage());
                }
            });
        }
    }

    //Verifica si el email es correcto
    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Verifica si el password es correcto
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        loginProgressText.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        ivLoginLogo.setVisibility(visibility);
        mLoginFormView.setVisibility(visibility);
    }
}

