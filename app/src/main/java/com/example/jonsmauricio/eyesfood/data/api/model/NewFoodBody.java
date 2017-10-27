package com.example.jonsmauricio.eyesfood.data.api.model;

import com.google.gson.annotations.SerializedName;

/*
    Define el cuerpo JSON del método POST para recibir un alimento para el historial
*/
public class NewFoodBody {
    @SerializedName("idUsuario")
    private String userId;
    @SerializedName("codigoBarras")
    private String barcode;
    @SerializedName("nombre")
    private String name;
    @SerializedName("producto")
    private String product;
    @SerializedName("marca")
    private String brand;
    @SerializedName("contenidoNeto")
    private String content;
    @SerializedName("porcion")
    private String portion;
    @SerializedName("porcionGramos")
    private String portionUnit;
    @SerializedName("energia")
    private String energy;
    @SerializedName("proteinas")
    private String protein;
    @SerializedName("grasaTotal")
    private String totalFat;
    @SerializedName("grasaSaturada")
    private String saturatedFat;
    @SerializedName("grasaMono")
    private String monoFat;
    @SerializedName("grasaPoli")
    private String poliFat;
    @SerializedName("grasaTrans")
    private String transFat;
    @SerializedName("colesterol")
    private String cholesterol;
    @SerializedName("hidratosCarbono")
    private String carbo;
    @SerializedName("azucaresTotales")
    private String totalSugar;
    @SerializedName("fibra")
    private String fyber;
    @SerializedName("sodio")
    private String sodium;
    @SerializedName("ingredientes")
    private String ingredients;
    @SerializedName("date")
    private String date;

    public NewFoodBody(String userId, String barcode, String name, String product, String brand, String content,
                       String portion, String portionUnit, String energy, String protein, String totalFat,
                       String saturatedFat, String monoFat, String poliFat, String transFat, String cholesterol,
                       String carbo, String totalSugar, String fyber, String sodium, String ingredients, String date) {
        this.userId = userId;
        this.barcode = barcode;
        this.name = name;
        this.product = product;
        this.brand = brand;
        this.content = content;
        this.portion = portion;
        this.portionUnit = portionUnit;
        this.energy = energy;
        this.protein = protein;
        this.totalFat = totalFat;
        this.saturatedFat = saturatedFat;
        this.monoFat = monoFat;
        this.poliFat = poliFat;
        this.transFat = transFat;
        this.cholesterol = cholesterol;
        this.carbo = carbo;
        this.totalSugar = totalSugar;
        this.fyber = fyber;
        this.sodium = sodium;
        this.ingredients = ingredients;
        this.date = date;
    }
}
