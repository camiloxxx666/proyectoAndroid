package com.example.camilo.prueba0.fragments;

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
import com.example.camilo.prueba0.modelo.TicketUsuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class FragmentTickets extends Fragment {

    private ListView lvTickets;
    private OnFragmentInteractionListener mListener;
    private String email;

    public FragmentTickets() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);
        lvTickets = (ListView) view.findViewById(R.id.lvTickets);
        SharedPreferences settings = getActivity().getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
        email = settings.getString("email", "");

        try
        {
            final String tenant = Util.getProperty("tenant.name", getActivity().getApplicationContext());
            String ip = settings.getString("ip", "");
            String puerto = settings.getString("puerto", "");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "http://"+ip+":"+puerto+"/obtenerEspectaculosUsuario/"+ "?email="+email ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            Type listaTickets = new TypeToken<List<TicketUsuario>>(){}.getType();
                            List<TicketUsuario> resultJson = new Gson().fromJson(response, listaTickets);
                            FragmentTickets.TicketAdapter adapter = new FragmentTickets.TicketAdapter(getActivity(), R.layout.ticket_row, resultJson);
                            lvTickets.setAdapter(adapter);

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

    class TicketAdapter extends ArrayAdapter
    {
        private List<TicketUsuario> listaTickets;
        private int resource;
        private LayoutInflater inflater;

        public TicketAdapter(Context context, int resource, List<TicketUsuario> tickets){
            super(context, resource, tickets);
            listaTickets = tickets;
            this.resource = resource;
            inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(resource, null);

            ImageView ticketImagen;
            TextView ticketNombre;
            TextView ticketSala;
            TextView ticketSector;
            TextView ticketHorario;
            TextView ticketAsiento;

            ticketImagen = (ImageView) convertView.findViewById(R.id.ticketImagen);
            ticketNombre = (TextView) convertView.findViewById(R.id.ticketNombre);
            ticketSala = (TextView) convertView.findViewById(R.id.ticketSala);
            ticketSector = (TextView) convertView.findViewById(R.id.ticketSector);
            ticketHorario = (TextView) convertView.findViewById(R.id.ticketHorario);
            ticketAsiento = (TextView) convertView.findViewById(R.id.ticketAsiento);

            ticketNombre.setText(listaTickets.get(position).getNombre());
            ticketSala.setText(listaTickets.get(position).getRealizacionEspectaculo().getSala().getNombre());
            ticketSector.setText(listaTickets.get(position).getRealizacionEspectaculo().getSector().getNombre());

            Date date = new Date();
            date.setTime(Long.valueOf(listaTickets.get(position).getRealizacionEspectaculo().getFecha()));
            ticketHorario.setText(Util.formatter.format(date));
            ticketAsiento.setText("N° Asiento: "+listaTickets.get(position).getIdAsiento());


            return convertView;
        }
    }

}
