package com.example.camilo.prueba0.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.camilo.prueba0.R;
import com.example.camilo.prueba0.Util;
import com.example.camilo.prueba0.modelo.EspectaculoFull;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentScanner.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FragmentScanner extends Fragment {
    String email;
    SurfaceView cameraPreview;
    TextView resultado;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;

    private OnFragmentInteractionListener mListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
        email = settings.getString("email", "");

        cameraPreview = (SurfaceView) view.findViewById(R.id.cameraPreview);
        resultado = (TextView) view.findViewById(R.id.resultado);

        barcodeDetector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
                .Builder(getActivity(), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        //Agrego eventos
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2){

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder){
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> codigosQR = detections.getDetectedItems();
                if(codigosQR.size() != 0)
                {
                    resultado.post(new Runnable() {
                        @Override
                        public void run() {
                            //Vibra el celular cuando se detecta un QR
                            Vibrator vibrator = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(200);

                            //Invoco a la api con el string del qr scanneado
                            try
                            {
                                final String tenant = Util.getProperty("tenant.name", getActivity().getApplicationContext());
                                SharedPreferences settings = getActivity().getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
                                String ip = settings.getString("ip", "");
                                String puerto = settings.getString("puerto", "");
                                String qr = codigosQR.valueAt(0).displayValue;
                                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                        "http://"+ip+":"+puerto+"/verificarCodigoQR/"+ "?email="+email+"&qr="+qr,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if(response!=null && response.equals("true"))
                                                {
                                                    resultado.setText("Codigo correcto.");
                                                    resultado.setTextColor(Color.GREEN);
                                                }
                                                else
                                                {
                                                    resultado.setText("Codigo Incorrecto");
                                                    resultado.setTextColor(Color.RED);
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

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

                            }
                            catch(Exception e)
                            {

                            }

                        }
                    });
                }
            }
        });

        return view;
    }


    public FragmentScanner() {
        // Required empty public constructor
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
