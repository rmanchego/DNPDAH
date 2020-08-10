package com.practica02.proyectonpa.Controller.Pasos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Pasos;
import com.practica02.proyectonpa.Model.Entidades.Logica.LPasos;
import com.practica02.proyectonpa.Model.Holder.PasosViewHolder;
import com.practica02.proyectonpa.Model.Utilidades.Constantes;
import com.practica02.proyectonpa.R;

public class HistorialPasosActivity extends AppCompatActivity {

    private RecyclerView rvPasos;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pasos);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        rvPasos = findViewById(R.id.rvHistorialPasos);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPasos.setLayoutManager(linearLayoutManager);

        Query query = FirebaseDatabase.getInstance() //consulta
                .getReference()
                .child(Constantes.NODO_DE_PASOS).child(currentUser.getUid());

        FirebaseRecyclerOptions<Pasos> options =
                new FirebaseRecyclerOptions.Builder<Pasos>()
                        .setQuery(query, Pasos.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Pasos, PasosViewHolder>(options) {
            @Override
            public PasosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_pasos, parent, false);
                return new PasosViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(PasosViewHolder holder, int position, Pasos model) {
                holder.getTxtNumeroPasos().setText("" + model.getPasos());
                holder.getTxtDuracionPasos().setText(""+model.getDuracion()+""  );
                holder.getTxtFechaMes().setText(LPasos.obtenerFechaDeRegistro(model.getFecha()));
                holder.getTxtFechaHora().setText(LPasos.obtenerHoraDeRegistro(model.getFecha()));
            }
        };
        rvPasos.setAdapter(adapter);
    }

    @Override
    protected void onStart() {  //Cuando cerremos la aplicacion , y entremos automaticamente se inicie el adaptador
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() { //No ocurra error cuando la actividad este cerrada
        super.onStop();
        adapter.stopListening();
    }

}