package com.example.camilo.prueba0;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class FragmentCuenta extends Fragment {

    private TextView txtName, txtEmail;
    private ImageView imgProfile;
    private OnFragmentInteractionListener mListener;
    private static final String NO_IMAGEN_PERFIL = "http://www.karar.com/assets/default/img/yorum.png";

    public FragmentCuenta() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle)
    {
        View view = inflater.inflate(R.layout.fragment_cuenta, container, false);
        imgProfile = (ImageView) view.findViewById(R.id.imgProfilePic);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtName.setText(getArguments().getString("nombre"));
        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtEmail.setText(getArguments().getString("email"));

        String foto = getArguments().getString("foto");

        Glide.with(getActivity().getApplicationContext()).load(foto != null ? foto : NO_IMAGEN_PERFIL)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        return view;
    }

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
