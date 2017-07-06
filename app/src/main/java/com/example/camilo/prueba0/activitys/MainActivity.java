package com.example.camilo.prueba0.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton btnSignIn;
    private ImageView logo;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        logo = (ImageView) findViewById(R.id.ticketsLogo);
        logo.setOnLongClickListener(mostrarOpciones());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());

    }

    //Para no poder volver al home, que se cierre.
    public void onBackPressed() {
        finish();
    }

    public View.OnLongClickListener mostrarOpciones()
    {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intentConfiguracion = new Intent(MainActivity.this, ConfiguracionActivity.class);
                startActivity(intentConfiguracion);
                return true;
            }
        };
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            showProgressDialog();

            // Success de Login en Gmail se toman los datos del usuario y se mandan al HomeActivity
            GoogleSignInAccount acct = result.getSignInAccount();

            final String personName = acct.getDisplayName();
            final String personPhotoUrl = acct.getPhotoUrl()!=null ? acct.getPhotoUrl().toString() : null;
            final String idGoogle = acct.getId();
            final String email = acct.getEmail();

            //Hago el login en el backend

            try
            {
                SharedPreferences settings = getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("email", email);
                editor.commit();
                finish();

                final String tenant = Util.getProperty("tenant.name", getApplicationContext());
                String ip = settings.getString("ip", "");
                String puerto = settings.getString("puerto", "");

                //Con esto se mantiene la sesion de este lado.
                CookieHandler.setDefault(new CookieManager());

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        "http://"+ip+":"+puerto+"/loginUsuarioFinalGmail/"+ "?id=" + idGoogle + "&email=" + email,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.putExtra("idGoogle", idGoogle);
                                intent.putExtra("nombre", personName);
                                intent.putExtra("email", email);
                                intent.putExtra("fotoGmailURL", personPhotoUrl);

                                hideProgressDialog();
                                startActivity(intent);
                                //finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error en el login. Vuelva a inentarlo.", Toast.LENGTH_SHORT).show();
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

        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Resultado retornado desde el Intent de GoogleSignInApi
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone())
        {
            //Si hay credenciales validas cacheadas del usuario, OptionalPendingResult va a ser "done"
            //y el GoogleSignInResult va a estar disponible instantáneamente.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        else
        {
            //Si el usuario no se ha logueado previamente en este dispositivo o el login expiró
            //esta rama asyncronica va a intentar loguear de forma silenciosa.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Un error inesperado ocurrió y no está disponible la api
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
//            btnSignOut.setVisibility(View.VISIBLE);
//            btnRevokeAccess.setVisibility(View.VISIBLE);
//            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
//            btnSignOut.setVisibility(View.GONE);
//            btnRevokeAccess.setVisibility(View.GONE);
//            llProfileLayout.setVisibility(View.GONE);
        }
    }

}
