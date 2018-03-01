package com.example.jonsmauricio.eyesfood.data.api;

import android.provider.ContactsContract;

import com.example.jonsmauricio.eyesfood.data.api.model.Counter;
import com.example.jonsmauricio.eyesfood.data.api.model.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    //String BASE_URL = "http://192.168.0.105/api.eyesfood.cl/v1/";
    String BASE_URL = "https://eyesfood.000webhostapp.com/api.eyesfood.cl/v1/";

    //Esta petición tiene un parámetro del tipo @Body que es un LoginBody llamado loginBody
    @POST("users/login")
    Call<User> login(@Body LoginBody loginBody);

    @POST("users/register")
    Call<User> signUp(@Body SignUpBody signUpBody);

    //Petición que retorna un alimento completo mediante su código de barras
    @GET("foods/{barcode}")
    Call<Food> getFood(@Path("barcode") String barcode);

    //Petición que retorna los ingredientes de un alimento
    @GET("foods/{barcode}/ingredients")
    Call<List<Ingredient>> getIngredients(@Path("barcode") String barcode);

    //Petición que retorna los aditivos de un alimento
    @GET("foods/{barcode}/additives")
    Call<List<Ingredient>> getAdditives(@Path("barcode") String barcode);

    //Petición que retorna las recomendaciones de un alimento mediante su código de barras
    @GET("foods/{barcode}/recommendations")
    Call<List<Recommendation>> getRecommendations(@Path("barcode") String barcode);

    //Petición que consulta si el usuario tiene un alimento en su historial de escaneo
    @GET("history/{userId}/{barcode}")
    Call<ShortFood> isInHistory(@Path("userId") String userId, @Path("barcode") String barcode);

    //Petición que retorna los alimentos del historial de usuario
    @GET("history/{userId}")
    Call<List<ShortFood>> getFoodsInHistory(@Path("userId") String userId);

    //Petición que retorna los alimentos del historial de subida del usuario
    @GET("history/{userId}/uploads")
    Call<List<ShortFood>> getFoodsUploads(@Path("userId") String userId);

    //Petición que retorna los alimentos destacados del usuario
    @GET("history/{userId}/favorites")
    Call<List<ShortFood>> getFoodsFavorites(@Path("userId") String userId);

    //Petición que retorna los alimentos rechazados del usuario
    @GET("history/{userId}/rejected")
    Call<List<ShortFood>> getFoodsRejected(@Path("userId") String userId);

    //Petición que inserta un alimento en el historial
    @POST("history")
    Call<Food> insertInHistory(@Body HistoryFoodBody historyFoodBody);

    //Petición que actualiza un alimento en el historial
    //No se usa Patch porque no resulta
    @Headers("Content-Type: application/json")
    @POST("history/{userId}/{barcode}")
    Call<ShortFood> modifyHistory(@Path("userId") String userId, @Path("barcode") String barcode);

    //Petición que actualiza un alimento en el historial
    //No se usa Patch porque no resulta
    @Headers("Content-Type: application/json")
    @POST("history/{userId}/{barcode}/scan")
    Call<ShortFood> modifyHistoryScan(@Path("userId") String userId, @Path("barcode") String barcode);

    //Petición que retorna los aditivos de un alimento con todos sus datos
    @GET("foods/{barcode}/additives/full")
    Call<List<Additive>> getFullAdditives(@Path("barcode") String barcode);

    //Petición que retorna alimentos desde una búsqueda
    @GET("search/foods/{query}")
    Call<List<Food>> getFoodsQuery(@Path("query") String query);

    //Petición que retorna aditivos desde una búsqueda
    @GET("search/additives/{query}")
    Call<List<Additive>> getAdditivesQuery(@Path("query") String query);

    //Petición que retorna un aditivo, usado para los resultados de búsqueda
    @GET("additives/{eCode}")
    Call<List<Additive>> getAdditive(@Path("eCode") String eCode);

    //Inserta una solicitud de nuevo alimento
    @POST("foods/new")
    Call<Food> newFoodSolitude(@Body NewFoodBody newFoodBody);

    //Inserta una solicitud de edición de alimento
    @POST("foods/complaint")
    Call<Food> newFoodComplaint(@Body NewFoodBody newFoodBody);

    //Retorna las imágenes autorizadas de un alimento
    @GET("images/{barcode}")
    Call<ArrayList<FoodImage>> getImages(@Path("barcode") String barcode);

    //Retorna los comentarios de un alimento
    @GET("comments/{barcode}")
    Call<List<Comment>> getComments(@Path("barcode") String barcode);

    //Inserta una solicitud de edición de alimento
    @POST("comments/{barcode}")
    Call<Comment> newComment(@Body CommentBody commentBody, @Path("barcode") String barcode);

    //Verifica si el correo de inicio de sesión de gmail o facebook existe en la base de datos de usuarios
    @POST("users/external")
    Call<User> findExternalUser(@Body GmailUserBody gmailUserBody);

    //Inserta una solicitud de edición de alimento
    @POST("users/external/register")
    Call<User> registerExternalUser(@Body GmailRegisterBody gmailRegisterBody);

    //Petición que actualiza un alimento en el historial
    //No se usa Patch porque no resulta
    @Headers("Content-Type: application/json")
    @POST("history/{userId}/{barcode}/{like}/")
    Call<ShortFood> modifyHistoryLike(@Path("userId") String userId, @Path("barcode") String barcode, @Path("like") int like);

    //Petición que retorna la cantidad de likes de un alimento
    @GET("history/{barcode}/likes")
    Call<Counter> getLikes(@Path("barcode") String barcode);

    //Petición que retorna la cantidad de dislikes de un alimento
    @GET("history/{barcode}/dislikes")
    Call<Counter> getDislikes(@Path("barcode") String barcode);

    //Petición que retorna la cantidad de comentarios de un alimento
    @GET("history/{barcode}/comments")
    Call<Counter> getCommentsCount(@Path("barcode") String barcode);

    //Petición que inserta un alimento en el historial
    @POST("history/noscan")
    Call<Food> insertNoScan(@Body InsertFromLikeBody insertFromLikeBody);

    //Retorna los expertos
    @GET("experts")
    Call<List<Expert>> getExperts();

    //Retorna los expertos
    @GET("help")
    Call<List<Help>> getHelp();

    //Retorna las medidas
    @GET("measures/weight/{userId}")
    Call<List<Measure>> getWeight(@Path("userId") String userId);

    @GET("measures/fat/{userId}")
    Call<List<Measure>> getFat(@Path("userId") String userId);

    @GET("measures/waist/{userId}")
    Call<List<Measure>> getWaist(@Path("userId") String userId);

    @GET("measures/a1c/{userId}")
    Call<List<Measure>> getA1c(@Path("userId") String userId);

    @GET("measures/preglucose/{userId}")
    Call<List<Measure>> getPreglucose(@Path("userId") String userId);

    @GET("measures/postglucose/{userId}")
    Call<List<Measure>> getPostglucose(@Path("userId") String userId);

    @GET("measures/pressure/{userId}")
    Call<List<Measure>> getPressure(@Path("userId") String userId);

    //Edita las medidas
    @POST("measures/height/edit")
    Call<Measure> editHeight(@Body EditMeasureBody editMeasureBody);

    @POST("measures/weight/edit")
    Call<Measure> editWeight(@Body EditMeasureBody editMeasureBody);

    @POST("measures/fat/edit")
    Call<Measure> editFat(@Body EditMeasureBody editMeasureBody);

    @POST("measures/waist/edit")
    Call<Measure> editWaist(@Body EditMeasureBody editMeasureBody);

    @POST("measures/a1c/edit")
    Call<Measure> editA1c(@Body EditMeasureBody editMeasureBody);

    @POST("measures/preglucose/edit")
    Call<Measure> editPreGlu(@Body EditMeasureBody editMeasureBody);

    @POST("measures/postglucose/edit")
    Call<Measure> editPostGlu(@Body EditMeasureBody editMeasureBody);

    @POST("measures/pressure/edit")
    Call<Measure> editPressure(@Body EditMeasureBody editMeasureBody);

    //Eliminar datos, se hace con POST porque el delete no funciona Stream was reset:internal error
    @POST("measures/weight/delete/{measureId}")
    Call<Measure> deleteWeight(@Path("measureId") int measureId);

    @POST("measures/fat/delete/{measureId}")
    Call<Measure> deleteFat(@Path("measureId") int measureId);

    @POST("measures/waist/delete/{measureId}")
    Call<Measure> deleteWaist(@Path("measureId") int measureId);

    @POST("measures/a1c/delete/{measureId}")
    Call<Measure> deleteA1c(@Path("measureId") int measureId);

    @POST("measures/preglucose/delete/{measureId}")
    Call<Measure> deletePreGlu(@Path("measureId") int measureId);

    @POST("measures/postglucose/delete/{measureId}")
    Call<Measure> deletePostGlu(@Path("measureId") int measureId);

    @POST("measures/pressure/delete/{measureId}")
    Call<Measure> deletePressure(@Path("measureId") int measureId);

    //Insertar datos
    @POST("measures/weight")
    Call<Measure> insertWeight(@Body InsertMeasureBody insertMeasureBody);

    @POST("measures/fat")
    Call<Measure> insertFat(@Body InsertMeasureBody insertMeasureBody);

    @POST("measures/waist")
    Call<Measure> insertWaist(@Body InsertMeasureBody insertMeasureBody);

    @POST("measures/a1c")
    Call<Measure> insertA1c(@Body InsertMeasureBody insertMeasureBody);

    @POST("measures/preglucose")
    Call<Measure> insertPreGlu(@Body InsertMeasureBody insertMeasureBody);

    @POST("measures/postglucose")
    Call<Measure> insertPostGlu(@Body InsertMeasureBody insertMeasureBody);

    @POST("measures/pressure")
    Call<Measure> insertPressure(@Body InsertMeasureBody insertMeasureBody);
}