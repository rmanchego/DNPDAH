package com.practica02.proyectonpa.Model.Holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practica02.proyectonpa.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistorialFotosViewHolder extends RecyclerView.ViewHolder{

    private CircleImageView civFoto;
    private TextView txtNombreFoto;
    private LinearLayout layaoutPrincipal;

    public HistorialFotosViewHolder(@NonNull View itemView) {
        super(itemView);

        civFoto = itemView.findViewById(R.id.civFotoHistorial);
        txtNombreFoto = itemView.findViewById(R.id.txtNombreFotoHistorial);
        layaoutPrincipal = itemView.findViewById(R.id.layaoutPrincipal3);
    }

    public CircleImageView getCivFoto() {
        return civFoto;
    }

    public void setCivFoto(CircleImageView civFoto) {
        this.civFoto = civFoto;
    }

    public TextView getTxtNombreFoto() {
        return txtNombreFoto;
    }

    public void setTxtNombreFoto(TextView txtNombreFoto) {
        this.txtNombreFoto = txtNombreFoto;
    }

    public LinearLayout getLayaoutPrincipal() {
        return layaoutPrincipal;
    }

    public void setLayaoutPrincipal(LinearLayout layaoutPrincipal) {
        this.layaoutPrincipal = layaoutPrincipal;
    }
}
