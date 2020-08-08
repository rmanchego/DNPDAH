package com.practica02.proyectonpa.Model.Holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practica02.proyectonpa.R;

public class PasosViewHolder extends RecyclerView.ViewHolder {

    private TextView txtNumeroPasos;
    private TextView txtDuracionPasos;
    private TextView txtFechaMes;
    private TextView txtFechaHora;
    private LinearLayout layaoutPrincipal;

    public PasosViewHolder(@NonNull View itemView) {
        super(itemView);

        txtNumeroPasos = itemView.findViewById(R.id.txtNumeroPasos);
        txtDuracionPasos = itemView.findViewById(R.id.txtDuracionPasos);
        txtFechaMes = itemView.findViewById(R.id.txtFechaMesPasos);
        txtFechaHora = itemView.findViewById(R.id.txtFechaHoraPasos);
        layaoutPrincipal = itemView.findViewById(R.id.layaoutPrincipal1);
    }

    public TextView getTxtNumeroPasos() {
        return txtNumeroPasos;
    }

    public void setTxtNumeroPasos(TextView txtNumeroPasos) {
        this.txtNumeroPasos = txtNumeroPasos;
    }

    public TextView getTxtDuracionPasos() {
        return txtDuracionPasos;
    }

    public void setTxtDuracionPasos(TextView txtDuracionPasos) {
        this.txtDuracionPasos = txtDuracionPasos;
    }

    public TextView getTxtFechaMes() {
        return txtFechaMes;
    }

    public void setTxtFechaMes(TextView txtFechaMes) {
        this.txtFechaMes = txtFechaMes;
    }

    public TextView getTxtFechaHora() {
        return txtFechaHora;
    }

    public void setTxtFechaHora(TextView txtFechaHora) {
        this.txtFechaHora = txtFechaHora;
    }

    public LinearLayout getLayaoutPrincipal() {
        return layaoutPrincipal;
    }

    public void setLayaoutPrincipal(LinearLayout layaoutPrincipal) {
        this.layaoutPrincipal = layaoutPrincipal;
    }
}
