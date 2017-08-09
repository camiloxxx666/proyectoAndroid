package com.example.camilo.prueba0.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.camilo.prueba0.R;
import com.example.camilo.prueba0.util.Util;
import com.example.camilo.prueba0.fragments.FragmentBuscar;
import com.example.camilo.prueba0.fragments.FragmentCuenta;
import com.example.camilo.prueba0.fragments.FragmentEventosNuevos;
import com.example.camilo.prueba0.fragments.FragmentTickets;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;



public class HomeActivity extends AppCompatActivity
        implements FragmentEventosNuevos.OnFragmentInteractionListener,
        FragmentCuenta.OnFragmentInteractionListener, FragmentTickets.OnFragmentInteractionListener,
        FragmentBuscar.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private TextView txtEmailNav, txtNombreNav;
    protected String foto, nombre, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        SharedPreferences settings = getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
        email = settings.getString("email", "");

        txtNombreNav.setText(getIntent().getExtras().getString("nombre"));
        txtEmailNav.setText(getIntent().getExtras().getString("email"));


        //Crear el fragment
        Fragment fragment = new FragmentEventosNuevos();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_home, fragment)
                .commit();

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

        if (id == R.id.nav_lista_comprados)
        {
            Bundle bundle = new Bundle();
            bundle.putString("email", email);

            fragment = new FragmentTickets();
            fragment.setArguments(bundle);
            fragmentTransaction = true;
        } else if (id == R.id.nav_nuevos_eventos) {
            Bundle bundle = new Bundle();
            bundle.putString("email", email);

            fragment = new FragmentEventosNuevos();
            fragment.setArguments(bundle);
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
    public void onFragmentInteraction(Uri uri) {}



}
