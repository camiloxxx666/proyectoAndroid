package com.example.camilo.prueba0;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.camilo.prueba0.modelo.Espectaculo;
import com.example.camilo.prueba0.modelo.EspectaculoFull;
import com.example.camilo.prueba0.modelo.Realizacion;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FragmentEventosNuevos extends Fragment
{
    private ListView lvEspectaculos;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); //Agregar la hora aca cuando se empiece a mandar desde el back
    private OnFragmentInteractionListener mListener;

    public FragmentEventosNuevos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_eventos_nuevos, container, false);

        lvEspectaculos = (ListView) view.findViewById(R.id.lvEspectaculos);

        try
        {
            final String tenant = Util.getProperty("tenant.name", getActivity().getApplicationContext());
            SharedPreferences settings = getActivity().getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
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
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
            ioe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
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
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null)
                convertView = inflater.inflate(resource, null);

            ImageView espImagen;
            TextView espNombre;
            TextView espDescripcion;
            TextView espTipo;
            TextView espFechas;
            TextView espSalas;
            RatingBar espRating;

            espImagen = (ImageView) convertView.findViewById(R.id.espImagen);
            espNombre = (TextView) convertView.findViewById(R.id.espNombre);
            espDescripcion = (TextView) convertView.findViewById(R.id.espDescripcion);
            espTipo = (TextView) convertView.findViewById(R.id.espTipo);
            espFechas = (TextView) convertView.findViewById(R.id.espFechas);
            espSalas = (TextView) convertView.findViewById(R.id.espSalas);

            espNombre.setText(listaEspectaculos.get(position).getNombre());
            espDescripcion.setText("Descripcion: " + listaEspectaculos.get(position).getDescripcion());
            espTipo.setText("Categoría: " + listaEspectaculos.get(position).getTipoEspectaculo().getNombre());
            StringBuilder sb = new StringBuilder("Presentaciones: ");
            for(Realizacion realizacion : listaEspectaculos.get(position).getRealizacionEspectaculo())
            {
                sb.append(realizacion.getSala().getNombre());
                sb.append(" - ");
                Date date = new Date();
                date.setTime(Long.valueOf(realizacion.getFecha()));
                sb.append(formatter.format(date));
            }
            espFechas.setText(sb.toString());
            espSalas.setText("");

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
