package com.practica02.proyectonpa.Controller.FotosUbicacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.practica02.proyectonpa.Model.Entidades.Firebase.Foto;
import com.practica02.proyectonpa.Model.Entidades.Logica.LFoto;
import com.practica02.proyectonpa.Model.Holder.HistorialFotosViewHolder;
import com.practica02.proyectonpa.Model.Utilidades.Constantes;
import com.practica02.proyectonpa.R;

public class HistorialFotosActivity extends AppCompatActivity {

    private RecyclerView rvFotos;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_ubicacion_historial_fotos);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        rvFotos = findViewById(R.id.rvHistorialFotos);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFotos.setLayoutManager(linearLayoutManager);

        Query query = FirebaseDatabase.getInstance() //consulta
                .getReference()
                .child(Constantes.NODO_DE_FOTOS_UBICACION).child(currentUser.getUid());

        FirebaseRecyclerOptions<Foto> options =
                new FirebaseRecyclerOptions.Builder<Foto>()
                        .setQuery(query, Foto.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Foto, HistorialFotosViewHolder>(options) {
            @Override
            public HistorialFotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_historial_fotos, parent, false);
                return new HistorialFotosViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(HistorialFotosViewHolder holder, int position, Foto model) {
                Glide.with(HistorialFotosActivity.this).load(model.getFotoPerfilURL()).into(holder.getCivFoto());
                holder.getTxtNombreFoto().setText(model.getNombre());
                //Obtiene toda la data de la lista //  Key en getSpanpshot 2
                final LFoto lFoto = new LFoto(getSnapshots().getSnapshot(position).getKey(),model);
                //final LPaciente lPaciente = new LPaciente(getSnapshots().getSnapshot(position).getKey(),model);

                holder.getLayaoutPrincipal().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HistorialFotosActivity.this, VerFotoDeHistorialActivity.class);
                        intent.putExtra("key_receptor", lFoto.getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        rvFotos.setAdapter(adapter);
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