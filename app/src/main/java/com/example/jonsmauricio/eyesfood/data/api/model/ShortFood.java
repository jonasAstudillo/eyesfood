package com.example.jonsmauricio.eyesfood.data.api.model;

import com.google.gson.annotations.SerializedName;

/*
    Define un objeto alimento incompleto
    Clase utilizada para mostrar la información general de un alimento en el historial
*/

public class ShortFood {
    @SerializedName("codigoBarras")
    private String barCode;
    @SerializedName("nombre")
    private String name;
    @SerializedName("peligroAlimento")
    private float foodHazard;
    @SerializedName("fechaEscaneo")
    private String date;
    @SerializedName("fotoOficial")
    private String officialPhoto;

    public ShortFood(String barCode, String name, float foodHazard, String date, String officialPhoto) {
        this.barCode = barCode;
        this.name = name;
        this.foodHazard = foodHazard;
        this.date = date;
        this.officialPhoto = officialPhoto;
    }

    public String getBarCode() {
        return barCode;
    }

    public String getName() {
        return name;
    }

    //No se si lo uso
    public void setName(String name) {
        this.name = name;
    }

    public float getFoodHazard() {
        return foodHazard;
    }

    public String getDate() {
        return date;
    }

    public String getOfficialPhoto() {
        return officialPhoto;
    }

    @Override
    public String toString(){
        return name + " - " + foodHazard + " - " + date;
    }
}