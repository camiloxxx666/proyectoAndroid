package com.example.camilo.prueba0.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.camilo.prueba0.R;

public class GestionCompraActivity extends AppCompatActivity {

    private TextView prueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        prueba = (TextView) findViewById(R.id.prueba);
        prueba.setText(intent.getStringExtra("idEspectaculo"));

        setContentView(R.layout.activity_gestion_compra);
    }
}
