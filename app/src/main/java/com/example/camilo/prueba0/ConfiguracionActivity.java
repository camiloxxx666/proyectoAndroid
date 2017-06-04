package com.example.camilo.prueba0;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfiguracionActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private EditText editIP, editPuerto;
    private Button guardarBoton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String ip = settings.getString("ip", "");
        String puerto = settings.getString("puerto", "");

        editIP = (EditText) findViewById(R.id.txtIP);
        editPuerto = (EditText) findViewById(R.id.txtPuerto);

        if(!"".equals(ip))
            editIP.setText(ip);
        if(!"".equals(puerto))
            editPuerto.setText(puerto);

        guardarBoton = (Button) findViewById(R.id.btnGuardar);
        guardarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("ip", editIP.getText().toString());
                editor.putString("puerto", editPuerto.getText().toString());

                editor.commit();
                finish();
            }
        });

    }
}
