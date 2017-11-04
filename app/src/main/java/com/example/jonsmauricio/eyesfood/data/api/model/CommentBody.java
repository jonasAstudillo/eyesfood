package com.example.jonsmauricio.eyesfood.data.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JonásMauricio on 04-11-2017.
 */

public class CommentBody {
    @SerializedName("idUsuario")
    private String userId;
    @SerializedName("comentario")
    private String comment;

    public CommentBody(String userId, String comment) {
        this.userId = userId;
        this.comment = comment;
    }
}
