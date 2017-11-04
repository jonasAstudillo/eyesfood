package com.example.jonsmauricio.eyesfood.data.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
    Define un objeto alimento
    Clase utilizada para mostrar la información detallada de los alimentos
*/
public class Food implements Serializable{

    @SerializedName("codigoBarras")
    private String barCode;
    @SerializedName("nombreMarca")
    private String brandCode;
    @SerializedName("idUsuario")
    private String userId;
    @SerializedName("idPeligroAlimento")
    private String foodHazardId;
    @SerializedName("peligroAlimento")
    private float foodHazard;
    @SerializedName("producto")
    private String productId;
    @SerializedName("nombre")
    private String name;
    @SerializedName("unidadMedida")
    private String unit;
    @SerializedName("contenidoNeto")
    private int content;
    @SerializedName("energia")
    private float energy;
    @SerializedName("proteinas")
    private float protein;
    @SerializedName("grasaTotal")
    private float totalFat;
    @SerializedName("grasaSaturada")
    private float saturatedFat;
    @SerializedName("grasaTrans")
    private float transFat;
    @SerializedName("colesterol")
    private float cholesterol;
    @SerializedName("grasaMono")
    private float monoFat;
    @SerializedName("grasaPoli")
    private float poliFat;
    @SerializedName("hidratosCarbono")
    private float carbo;
    @SerializedName("azucaresTotales")
    private float totalSugar;
    @SerializedName("fibra")
    private float fiber;
    @SerializedName("sodio")
    private float sodium;
    @SerializedName("porcion")
    private String portion;
    @SerializedName("porcionGramos")
    private float portionGr;
    @SerializedName("fechaSubida")
    private String date;
    @SerializedName("indiceGlicemico")
    private float glycemicIndex;
    @SerializedName("fotoOficial")
    private String officialPhoto;

    public Food(String barCode, String brandCode, String userId, String foodHazardId, float foodHazard, String productId,
                String name, String unit, int content, float energy, float protein, float totalFat, float saturatedFat,
                float transFat, float cholesterol, float monoFat, float poliFat, float carbo, float totalSugar, float fiber,
                float sodium, String portion, float portionGr, String date, float glycemicIndex, String officialPhoto) {

        this.barCode = barCode;
        this.brandCode = brandCode;
        this.userId = userId;
        this.foodHazardId = foodHazardId;
        this.foodHazard = foodHazard;
        this.productId = productId;
        this.name = name;
        this.unit = unit;
        this.content = content;
        this.energy = energy;
        this.protein = protein;
        this.totalFat = totalFat;
        this.saturatedFat = saturatedFat;
        this.transFat = transFat;
        this.cholesterol = cholesterol;
        this.monoFat = monoFat;
        this.poliFat = poliFat;
        this.carbo = carbo;
        this.totalSugar = totalSugar;
        this.fiber = fiber;
        this.sodium = sodium;
        this.portion = portion;
        this.portionGr = portionGr;
        this.date = date;
        this.glycemicIndex = glycemicIndex;
        this.officialPhoto = officialPhoto;
    }

    public String getBarCode() {
        return barCode;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getFoodHazardId() {
        return foodHazardId;
    }

    public float getFoodHazard() {
        return foodHazard;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    //No se si este lo uso
    public void setName(String name) {
        this.name = name;
    }

    public int getContent() {
        return content;
    }

    public float getEnergy() {
        return energy;
    }

    public float getProtein() {
        return protein;
    }

    public float getTotalFat() {
        return totalFat;
    }

    public float getSaturatedFat() {
        return saturatedFat;
    }

    public float getTransFat() {
        return transFat;
    }

    public float getCholesterol() {
        return cholesterol;
    }

    public float getMonoFat() {
        return monoFat;
    }

    public float getPoliFat() {
        return poliFat;
    }

    public float getCarbo() {
        return carbo;
    }

    public float getTotalSugar() {
        return totalSugar;
    }

    public float getFiber() {
        return fiber;
    }

    public float getSodium() {
        return sodium;
    }

    public String getPortion() {
        return portion;
    }

    public float getPortionGr() {
        return portionGr;
    }

    public String getDate() {
        return date;
    }

    public float getGlycemicIndex() {
        return glycemicIndex;
    }

    public String getOfficialPhoto() {
        return officialPhoto;
    }

    @Override
    public String toString(){
        return name;
    }
}