package com.practica02.proyectonpa.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.practica02.proyectonpa.Controller.ScannerQR.AgregarPacienteQRActivity;
import com.practica02.proyectonpa.Controller.ScannerQR.QrScannerActivity;
import com.practica02.proyectonpa.Model.Entidades.Firebase.Paciente;
import com.practica02.proyectonpa.Model.Entidades.Logica.LPaciente;
import com.practica02.proyectonpa.Model.Holder.PacienteViewHolder;
import com.practica02.proyectonpa.Model.Utilidades.Constantes;
import com.practica02.proyectonpa.Controller.ScannerQR.PerfilPacienteActivity;
import com.practica02.proyectonpa.R;

public class Fragment_CodigoQR extends Fragment{
    private Button btnEscanearQR;
    private Button btnAgregarQR;

    private RecyclerView rvPacientesQR;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_codigoqr_layout,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FragmentActivity c = getActivity();

        btnEscanearQR = getView().findViewById(R.id.btnScan);
        btnAgregarQR = getView().findViewById(R.id.btnAgregarQR);


        btnEscanearQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), QrScannerActivity.class));
            }
        });

        btnAgregarQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AgregarPacienteQRActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        rvPacientesQR = getView().findViewById(R.id.rvPacientesQR);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(c);
        rvPacientesQR.setLayoutManager(linearLayoutManager);

        Query query = FirebaseDatabase.getInstance() //consulta
                .getReference()
                .child(Constantes.NODO_DE_PACIENTES_QR).child(currentUser.getUid());

        FirebaseRecyclerOptions<Paciente> options =
                new FirebaseRecyclerOptions.Builder<Paciente>()
                        .setQuery(query, Paciente.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Paciente, PacienteViewHolder>(options) {
            @Override
            public PacienteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_paciente, parent, false);
                return new PacienteViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(PacienteViewHolder holder, int position, Paciente model) {
                Glide.with(Fragment_CodigoQR.this).load(model.getFotoPacienteURL()).into(holder.getCivFotoPerfil());
                holder.getTxtNombreUsuario().setText(model.getNombre());
                //Obtiene toda la data de la lista //  Key en getSpanpshot 2
                final LPaciente lPaciente = new LPaciente(getSnapshots().getSnapshot(position).getKey(),model);

                holder.getLayaoutPrincipal().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PerfilPacienteActivity.class);
                        intent.putExtra("key_receptor", lPaciente.getKey());
                        startActivity(intent);
                    }
                });

            }
        };
        rvPacientesQR.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
