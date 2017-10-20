package com.example.jonsmauricio.eyesfood.data.api.model;

import com.google.gson.annotations.SerializedName;

/*
    Define un objeto usuario
    Clase utilizada para obtener los usuarios cuando inician sesión
*/
public class User {

    @SerializedName("idusuario")
    private String id;
    @SerializedName("nombre")
    private String name;
    @SerializedName("apellido")
    private String surName;
    @SerializedName("correo")
    private String email;
    @SerializedName("fotousuario")
    private String photo;
    @SerializedName("reputacion")
    private String reputation;
    @SerializedName("fechanacimiento")
    private String dateBirth;
    @SerializedName("sexo")
    private String gender;
    @SerializedName("estatura")
    private String height;
    @SerializedName("nacionalidad")
    private String country;
    @SerializedName("token")
    private String token;

    public User(String id, String name, String surName, String email, String photo, String reputation, String dateBirth,
                String gender, String height, String country, String token) {
        this.id = id;
        this.name = name;
        this.surName = surName;
        this.email = email;
        this.photo = photo;
        this.reputation = reputation;
        this.dateBirth = dateBirth;
        this.gender = gender;
        this.height = height;
        this.country = country;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public String getReputation() {
        return reputation;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) { this.height = height; }

    public String getCountry() {
        return country;
    }

    public String getToken() {
        return token;
    }
}