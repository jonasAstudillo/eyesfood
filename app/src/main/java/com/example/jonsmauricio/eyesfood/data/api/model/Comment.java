package com.example.jonsmauricio.eyesfood.data.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by JonásMauricio on 04-11-2017.
 */

public class Comment implements Serializable {
    @SerializedName("idComentario")
    private String id;
    @SerializedName("Nombre")
    private String userName;
    @SerializedName("Apellido")
    private String userLastName;
    @SerializedName("FotoUsuario")
    private String userPhoto;
    @SerializedName("Reputacion")
    private String reputation;
    @SerializedName("comentario")
    private String comment;
    @SerializedName("fecha")
    private String date;

    public Comment(String id, String userName, String userLastName, String userPhoto, String reputation, String comment,
                   String date) {
        this.id = id;
        this.userName = userName;
        this.userLastName = userLastName;
        this.userPhoto = userPhoto;
        this.reputation = reputation;
        this.comment = comment;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getReputation() {
        return reputation;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
}
