package com.example.jonsmauricio.eyesfood.data.api.model;

import com.google.gson.annotations.SerializedName;

/*
    Define un objeto usuario
    Clase utilizada para obtener los usuarios cuando inician sesión
*/
public class User {
    @SerializedName("idUsuario")
    private String id;
    @SerializedName("Nombre")
    private String name;
    @SerializedName("Apellido")
    private String surName;
    @SerializedName("Correo")
    private String email;
    @SerializedName("FotoUsuario")
    private String photo;
    @SerializedName("Reputacion")
    private String reputation;
    @SerializedName("FechaNacimiento")
    private String dateBirth;
    @SerializedName("Sexo")
    private String gender;
    @SerializedName("Estatura")
    private String height;
    @SerializedName("Nacionalidad")
    private String country;

    public User(String id, String name, String surName, String email, String photo, String reputation, String dateBirth,
                String gender, String height, String country) {
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
}