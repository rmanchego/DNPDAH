package com.practica02.proyectonpa.Model.Holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practica02.proyectonpa.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PacienteViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView civFotoPerfil;
    private TextView txtNombreUsuario;
    private RelativeLayout layaoutPrincipal;

    public PacienteViewHolder(@NonNull View itemView) {
        super(itemView);

        civFotoPerfil = itemView.findViewById(R.id.civFotoPerfil);
        txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
        layaoutPrincipal = itemView.findViewById(R.id.layaoutPrincipal2);
    }

    public CircleImageView getCivFotoPerfil() {
        return civFotoPerfil;
    }

    public void setCivFotoPerfil(CircleImageView civFotoPerfil) {
        this.civFotoPerfil = civFotoPerfil;
    }

    public TextView getTxtNombreUsuario() {
        return txtNombreUsuario;
    }

    public void setTxtNombreUsuario(TextView txtNombreUsuario) {
        this.txtNombreUsuario = txtNombreUsuario;
    }

    public RelativeLayout getLayaoutPrincipal() {
        return layaoutPrincipal;
    }

    public void setLayaoutPrincipal(RelativeLayout layaoutPrincipal) {
        this.layaoutPrincipal = layaoutPrincipal;
    }
}
