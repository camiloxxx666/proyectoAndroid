package com.example.camilo.prueba0.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.camilo.prueba0.R;
import com.example.camilo.prueba0.util.AppController;
import com.example.camilo.prueba0.util.Util;
import com.example.camilo.prueba0.activitys.GestionCompraActivity;
import com.example.camilo.prueba0.modelo.Espectaculo;
import com.example.camilo.prueba0.modelo.EspectaculoFull;
import com.example.camilo.prueba0.modelo.TipoEspectaculo;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FragmentEventosNuevos extends Fragment
{
    private static final Object MY_SOCKET_TIMEOUT_MS = 5000;
    private ListView lvEspectaculos;
    private OnFragmentInteractionListener mListener;
    private String email;

    public FragmentEventosNuevos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_eventos_nuevos, container, false);

        lvEspectaculos = (ListView) view.findViewById(R.id.lvEspectaculos);
        SharedPreferences settings = getActivity().getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
        email = settings.getString("email", "");

        try
        {
            final String tenant = Util.getProperty("tenant.name", getActivity().getApplicationContext());
            String ip = settings.getString("ip", "");
            String puerto = settings.getString("puerto", "");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "http://"+ip+":"+puerto+"/verProximosEspectaculosYSusRealizaciones/"+ "?_start=1&_end=100" ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            EspectaculoFull resultJson = new Gson().fromJson(response, EspectaculoFull.class);
                            FragmentEventosNuevos.EspectaculoAdapter adapter = new FragmentEventosNuevos.EspectaculoAdapter(getActivity(), R.layout.espectaculo_row, resultJson.getContent());
                            lvEspectaculos.setAdapter(adapter);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), ioe.getMessage(), Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


        return view;
    }



    class EspectaculoAdapter extends ArrayAdapter
    {
        private List<Espectaculo> listaEspectaculos;
        private int resource;
        private LayoutInflater inflater;

        public EspectaculoAdapter(Context context, int resource, List<Espectaculo> espectaculos){
            super(context, resource, espectaculos);
            listaEspectaculos = espectaculos;
            this.resource = resource;
            inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(resource, null);

            ImageView espImagen;
            TextView espNombre;
            TextView espDescripcion;
            TextView espTipo;
            ImageView comprarBtn;
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();

            espNombre = (TextView) convertView.findViewById(R.id.espNombre);
            espDescripcion = (TextView) convertView.findViewById(R.id.espDescripcion);
            espTipo = (TextView) convertView.findViewById(R.id.espTipo);
            comprarBtn = (ImageView) convertView.findViewById(R.id.comprarBtn);
            espImagen = (ImageView) convertView.findViewById(R.id.espImagen);

            if(listaEspectaculos.get(0).getImagenesEspectaculo()!=null)
            {
                byte[] valueDecoded= Base64.decodeBase64(listaEspectaculos.get(position).getImagenesEspectaculo()[0]);

                Glide.with(getActivity().getApplicationContext()).load(valueDecoded)
                        .thumbnail(0.5f)
                        .crossFade()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(espImagen);

                //Bitmap bm = BitmapFactory.decodeByteArray(valueDecoded, 0, valueDecoded.length);
                //espImagen.setImageBitmap(bm);
            }
            else
            {
                //Una imagen por defecto ahi
            }

            espNombre.setText(listaEspectaculos.get(position).getNombre());
            espDescripcion.setText("Descripcion: " + listaEspectaculos.get(position).getDescripcion());

            StringBuilder sbTipos = new StringBuilder("Género: ");
            if (listaEspectaculos.get(position).getTipoEspectaculo() != null)
            {
                for (TipoEspectaculo te : listaEspectaculos.get(position).getTipoEspectaculo()) {
                    sbTipos.append(" - ");
                    sbTipos.append(te.getNombre());
                }

                espTipo.setText(sbTipos.toString());
            }

            comprarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentGestionCompra = new Intent(getActivity(), GestionCompraActivity.class);
                    intentGestionCompra.putExtra("idEspectaculo", listaEspectaculos.get(position).getId());
                    intentGestionCompra.putExtra("emailUsuario", email);
                    startActivity(intentGestionCompra);
                    }
            });


            return convertView;
        }
    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
