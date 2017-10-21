package com.example.jonsmauricio.eyesfood.data.api;

import com.example.jonsmauricio.eyesfood.data.api.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/*
    API de Eyes Food
    Interfaz utilizada para exponer todos los métodos de comunicación con la API
*/

public interface EyesFoodApi {

    //Cambiar host por "10.0.0.2" para Genymotion.
    //Cambiar host por "10.0.0.3" para AVD.
    //Cambiar host por IP de tu PC para dispositivo real.
    // Esta es la ip de usach alumnos
    // public static final String BASE_URL = "http://158.170.214.219/api.eyesfood.cl/v1/";
    String BASE_URL = "http://192.168.0.105/api.eyesfood.cl/v1/";

    //Esta petición tiene un parámetro del tipo @Body que es un LoginBody llamado loginBody
    @POST("users/login")
    Call<User> login(@Body LoginBody loginBody);

    @POST("users/register")
    Call<User> signUp(@Body SignUpBody signUpBody);

    //Petición que retorna un alimento completo mediante su código de barras
    @GET("foods/{barcode}")
    Call<Food> getFood(@Header("authorization") String token,
                       @Path("barcode") String barcode);

    //Petición que retorna los ingredientes de un alimento
    @GET("foods/{barcode}/ingredients")
    Call<List<Ingredient>> getIngredients(@Header("authorization") String token,
                                          @Path("barcode") String barcode);

    //Petición que retorna los aditivos de un alimento
    @GET("foods/{barcode}/additives")
    Call<List<Ingredient>> getAdditives(@Header("authorization") String token,
                                          @Path("barcode") String barcode);

    //Petición que retorna la información detallada de un aditivo
    @GET("additives/{ECode}")
    Call<Additive> getAdditive(@Header("authorization") String token,
                                        @Path("ECode") String ECode);

    //Petición que retorna las recomendaciones de un alimento mediante su código de barras
    @GET("foods/{barcode}/recommendations")
    Call<List<Recommendation>> getRecommendations(@Header("authorization") String token,
                                        @Path("barcode") String barcode);

    //Petición que consulta si el usuario tiene un alimento en su historial
    @GET("history/{userId}/{barcode}")
    Call<ShortFood> isInHistory(@Header("authorization") String token,
                                                  @Path("userId") String userId, @Path("barcode") String barcode);

    //Petición que retorna los alimentos del historial de usuario
    @GET("history/{userId}")
    Call<List<ShortFood>> getFoodsInHistory(@Header("authorization") String token,
                                @Path("userId") String userId);

    //Petición que inserta un alimento en el historial
    @POST("history")
    Call<Food> insertInHistory(@Body HistoryFoodBody historyFoodBody);

    //Petición que actualiza un alimento en el historial
    @Headers("Content-Type: application/json")
    @PATCH("history/{userId}/{barcode}")
    Call<ShortFood> modifyHistory(@Path("userId") String userId, @Path("barcode") String barcode);
}
