package com.example.camilo.prueba0;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Camilo on 29-may-17.
 */

public final class HTTPUtils extends AsyncTask<String, Void, Void>
{
    private static String email2, id2, tenant2, url2;

    public static final JSONObject getJSONObject(Context context, String serverUrl, Map<String, Object> params) throws Exception {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isOnline = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
        if (!isOnline) {  /*decir que no esta en linea */  }
        URL url = new URL(serverUrl);
        JSONObject jsonParam = new JSONObject();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            jsonParam.put(param.getKey(), param.getValue());
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.getOutputStream().write(jsonParam.toString().getBytes());
        conn.getOutputStream().flush();
        int responseCode = conn.getResponseCode(); //can call this instead of con.connect()
        if (responseCode >= 400 && responseCode <= 499) {
            throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
        } else if (responseCode != 200) {
            throw new Exception("HTTP Error: " + responseCode); //provide a more meaningful exception message
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        reader.close();
        reader = null;
        jsonParam = new JSONObject(sb.toString());
        sb = null;
        conn.disconnect();
        return jsonParam;
    }

    public static final String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public static final String  performPostCall(String requestURL,HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void descargarImagen(String url, ImageView imagen){
        new DownloadImagen(imagen).execute(url);
    }

    //descargar imagen desde url
    private static class DownloadImagen extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImagen(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            return getBitmapFromURL(urls[0]);
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result) ;
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getJSON(String direccionURL) throws Exception {
        try {
            URL url = new URL(direccionURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String resultado ="";
            String output;
            while ((output = br.readLine()) != null) {
                resultado+=output;
            }
            conn.disconnect();
            System.out.println(resultado);
            return resultado;
        } catch (MalformedURLException e) {
            throw new Exception("El formato de la URL no es correcto.");
        } catch (IOException e) {
            throw new Exception("Ocurrio un error al obtener el contenido de la URL.");
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(url2 + "?id=" + id2 + "&email=" + email2);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-TenantID", tenant2);
            //String input = stringJSON;
            //OutputStream os = conn.getOutputStream();
            //os.write(input.getBytes());
            //os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String resultado = "";
            String output;
            while ((output = br.readLine()) != null) {
                resultado += output;
            }
            conn.disconnect();
            System.out.println(resultado);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void postJSON(String direccionURL, String stringId, String email, String tenant) throws Exception {


        email2 = email;
        url2 = direccionURL;
        id2 = stringId;
        tenant2 = tenant;



    }
}
