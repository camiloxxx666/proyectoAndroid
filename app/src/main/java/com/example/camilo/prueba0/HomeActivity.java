package com.example.camilo.prueba0;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements FragmentEventosNuevos.OnFragmentInteractionListener,
        FragmentCuenta.OnFragmentInteractionListener, FragmentTickets.OnFragmentInteractionListener,
        FragmentBuscar.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private TextView txtEmailNav, txtNombreNav;
    private String foto, nombre, email;
    private ListView lvEspectaculos;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lvEspectaculos = (ListView) findViewById(R.id.lvEspectaculos);

        //Enlaza el menú de arriba
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Enlaza el DrawerLayout (El menú de la izquierda)
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        View header = navigationView.getHeaderView(0);

        txtNombreNav = (TextView) header.findViewById(R.id.nav_home_nombre);
        txtEmailNav = (TextView) header.findViewById(R.id.nav_home_email);

        foto = getIntent().getExtras().getString("fotoGmailURL");
        nombre = getIntent().getExtras().getString("nombre");
        email = getIntent().getExtras().getString("email");

        txtNombreNav.setText(getIntent().getExtras().getString("nombre"));
        txtEmailNav.setText(getIntent().getExtras().getString("email"));

        try
        {
            final String tenant = Util.getProperty("tenant.name", getApplicationContext());
            SharedPreferences settings = getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
            String ip = settings.getString("ip", "");
            String puerto = settings.getString("puerto", "");

            RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "http://"+ip+":"+puerto+"/verProximosEspectaculosYSusRealizaciones/"+ "?_start=1&_end=100" ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            EspectaculoFull resultJson = new Gson().fromJson(response, EspectaculoFull.class);
                            EspectaculoAdapter adapter = new EspectaculoAdapter(HomeActivity.this, R.layout.espectaculo_row, resultJson.getContent());
                            lvEspectaculos.setAdapter(adapter);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //No hago nada.
            //Si está abierto el menu lateral lo cierro, sino no hay evento
        }
    }



    //Menú de arriba
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Para saber si ya estoy en un fragment o no estoy en ningun fragment.
        boolean fragmentTransaction = false;
        Fragment fragment = null;

        if (id == R.id.nav_buscar) {
            fragment = new FragmentBuscar();
            fragmentTransaction = true;
        } else if (id == R.id.nav_lista_comprados) {
            fragment = new FragmentTickets();
            fragmentTransaction = true;
        } else if (id == R.id.nav_nuevos_eventos) {
            fragment = new FragmentEventosNuevos();
            fragmentTransaction = true;
        } else if (id == R.id.nav_mi_cuenta)
        {
            Bundle bundle = new Bundle();
            bundle.putString("nombre", nombre);
            bundle.putString("foto", foto);
            bundle.putString("email", email);

            fragment = new FragmentCuenta();
            fragment.setArguments(bundle);
            fragmentTransaction = true;
        } else if (id == R.id.nav_logout) {
            signOut();
        }

        if(fragmentTransaction)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_home, fragment)
                    .commit();
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Un error inesperado ocurrió y no está disponible la api
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
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

}
