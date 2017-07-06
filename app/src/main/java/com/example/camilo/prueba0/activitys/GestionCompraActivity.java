package com.example.camilo.prueba0.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.camilo.prueba0.R;
import com.example.camilo.prueba0.util.Util;
import com.example.camilo.prueba0.modelo.Fecha_realizacion;
import com.example.camilo.prueba0.modelo.Realizacion;
import com.example.camilo.prueba0.modelo.Sala_fecha;
import com.example.camilo.prueba0.modelo.Sector;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionCompraActivity extends AppCompatActivity {

    private String idEspectaculo, email;
    private ListView lvRealizaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_compra);
        lvRealizaciones = (ListView) findViewById(R.id.lvRealizaciones);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idEspectaculo = extras.getString("idEspectaculo");
            email = extras.getString("emailUsuario");
        }

        SharedPreferences settings = getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
        email = settings.getString("email", "");

        try
        {
            final String tenant = Util.getProperty("tenant.name", GestionCompraActivity.this);
            String ip = settings.getString("ip", "");
            String puerto = settings.getString("puerto", "");

            RequestQueue requestQueue = Volley.newRequestQueue(GestionCompraActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "http://"+ip+":"+puerto+"/verRealizacionesDeEspectaculo/"+ "?idEspectaculo="+idEspectaculo ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Type listaRealizacion = new TypeToken<List<Realizacion>>(){}.getType();
                            List<Realizacion> resultJson = new Gson().fromJson(response, listaRealizacion);

                            List<Sala_fecha> salasFechas = new ArrayList<Sala_fecha>();

                            for(Realizacion realizacion : resultJson)
                            {

                                if(salasFechas.isEmpty())
                                {

                                    List<Fecha_realizacion> listFechaR = new ArrayList<Fecha_realizacion>();
                                    Fecha_realizacion fechaRealizacion = new Fecha_realizacion(realizacion.getFecha(), realizacion.getId());
                                    listFechaR.add(fechaRealizacion);

                                    Sala_fecha sala_fecha = new Sala_fecha();
                                    sala_fecha.setSala(realizacion.getSala());
                                    sala_fecha.setFechas(listFechaR);
                                    sala_fecha.setSectores(realizacion.getSectores());
                                    salasFechas.add(sala_fecha);
                                }
                                else
                                {
                                    boolean salaAgregada = false;
                                    for(Sala_fecha sf : salasFechas)
                                    {
                                        if(sf.getSala().equals(realizacion.getSala()))
                                        {
                                            sf.getFechas().add(new Fecha_realizacion(realizacion.getFecha(), realizacion.getId()));
                                            salaAgregada = true;
                                            break;
                                        }
                                    }
                                    if(!salaAgregada)
                                    {
                                        List<Fecha_realizacion> listFechaR = new ArrayList<Fecha_realizacion>();
                                        Fecha_realizacion fechaRealizacion = new Fecha_realizacion(realizacion.getFecha(), realizacion.getId());
                                        listFechaR.add(fechaRealizacion);

                                        Sala_fecha sala_fecha = new Sala_fecha();
                                        sala_fecha.setSala(realizacion.getSala());
                                        sala_fecha.setFechas(listFechaR);
                                        sala_fecha.setSectores(realizacion.getSectores());
                                        salasFechas.add(sala_fecha);
                                    }
                                }
                            }

                            RealizacionAdapter adapter = new RealizacionAdapter(GestionCompraActivity.this, R.layout.realizacion_row, salasFechas);
                            lvRealizaciones.setAdapter(adapter);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(GestionCompraActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("X-TenantID", tenant);

                    return params;
                }
            };

            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);

            requestQueue.add(stringRequest);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    class RealizacionAdapter extends ArrayAdapter
    {
        private List<Sala_fecha> listaSalas;
        private int resource;
        private LayoutInflater inflater;

        public RealizacionAdapter(Context context, int resource, List<Sala_fecha> salas){
            super(context, resource, salas);
            listaSalas = salas;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(resource, null);

            ImageView espImagen; //Esta imagen se cargaría una sola vez pero igual no hay imagen por ahora asi que queda aca
            TextView realNombreSala;
            final Spinner realSectores, realFechas;
            ImageView compraBtn;

            espImagen = (ImageView) convertView.findViewById(R.id.espImagen);
            realNombreSala = (TextView) convertView.findViewById(R.id.realNombreSala);
            realFechas = (Spinner) convertView.findViewById(R.id.realFechas);
            realSectores = (Spinner) convertView.findViewById(R.id.realSectores);
            compraBtn = (ImageView) convertView.findViewById(R.id.compraBtn);

            realNombreSala.setText(listaSalas.get(position).getSala().getNombre()); //Puede sacarse la descripcion la capacidad, etc

            String[] arrayFechas = new String[listaSalas.get(position).getFechas().size()];
            final HashMap<Integer,String> fechasMap = new HashMap<Integer, String>();

            int posicion = 0;
            for (Fecha_realizacion fr : listaSalas.get(position).getFechas())
            {
                Date date = new Date();
                date.setTime(Long.valueOf(fr.getFecha()));
                fechasMap.put(posicion, fr.getIdRealizacion());
                arrayFechas[posicion] = Util.formatter.format(date);
                posicion ++;
            }

            ArrayAdapter<String> spinnerAdapterFechas = new ArrayAdapter<String>(GestionCompraActivity.this, R.layout.realizacion_item, arrayFechas);
            spinnerAdapterFechas.setDropDownViewResource(R.layout.realizacion_item);
            realFechas.setAdapter(spinnerAdapterFechas);

            spinnerAdapterFechas.notifyDataSetChanged();

            //Defino un array con el largo igual a cantidad de sectores en la sala
            String[] arraySectores = new String[listaSalas.get(position).getSectores().size()];
            final HashMap<Integer,String> sectoresMap = new HashMap<Integer, String>();

            posicion = 0;
            for (Sector s : listaSalas.get(position).getSectores())
            {
                sectoresMap.put(posicion, s.getId());
                arraySectores[posicion] = s.getNombre()+ " - $" +s.getPrecio();
                posicion ++;
            }

            ArrayAdapter<String> spinnerAdapterSectores = new ArrayAdapter<String>(GestionCompraActivity.this, R.layout.realizacion_item, arraySectores);
            spinnerAdapterSectores.setDropDownViewResource(R.layout.realizacion_item);
            realSectores.setAdapter(spinnerAdapterSectores);

            spinnerAdapterSectores.notifyDataSetChanged();


            compraBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String idSector = sectoresMap.get(realSectores.getSelectedItemPosition());
                    String idRealizacion = fechasMap.get(realFechas.getSelectedItemPosition());

                    try
                    {
                        final String tenant = Util.getProperty("tenant.name", getApplicationContext());
                        SharedPreferences settings = getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
                        String ip = settings.getString("ip", "");
                        String puerto = settings.getString("puerto", "");

                        RequestQueue requestQueue = Volley.newRequestQueue(GestionCompraActivity.this);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                "http://"+ip+":"+puerto+"/comprarEntradaEspectaculo/"+ "?email="+email+"&idRealizacion="+idRealizacion+"&idSector="+idSector ,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        AlertDialog.Builder builder;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            builder = new AlertDialog.Builder(GestionCompraActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                                        } else {
                                            builder = new AlertDialog.Builder(GestionCompraActivity.this);
                                        }
                                        builder.setTitle("Felicitaciones!")
                                                .setMessage("Compra realizada con exito.")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(GestionCompraActivity.this, HomeActivity.class);
                                                        intent.putExtra("goToMisTickets", true);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(GestionCompraActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("Content-Type", "application/json");
                                params.put("X-TenantID", tenant);

                                return params;
                            }
                        };

                        requestQueue.add(stringRequest);
                    }
                    catch(IOException ioe)
                    {
                        Toast.makeText(GestionCompraActivity.this, ioe.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(GestionCompraActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });


            return convertView;
        }
    }
}
