package com.daniel.lojamobile.fragment.cadastro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.util.UtilImageTransmit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FotoFragment extends Fragment {
    private ImageView foto;
    private TextView tvFoto;
    private static final int REQUEST_IMAGE_CAPTURE_CODE = 1;
    private byte[] fotoUsuario;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_foto, container, false);
        foto = (ImageView) view.findViewById(R.id.imagem_signin);
        tvFoto = (TextView) view.findViewById(R.id.tvFoto);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE_CODE && resultCode == getActivity().RESULT_OK) {

            Bundle bundle = data.getExtras();

            //Todo arrumar aq
            Bitmap bitmap = (Bitmap) bundle.get("data");

            if (bitmap != null) {
                foto.setImageBitmap(bitmap);
                foto.setBackgroundColor(Color.WHITE);
                tvFoto.setVisibility(View.GONE);
                UtilImageTransmit utilImageTransmit = new UtilImageTransmit();
                fotoUsuario = utilImageTransmit.convertImageToByte(bitmap);

                Log.i("FOTO", "" + fotoUsuario);
            } else Log.i("imagem", "imagemNula");

            //imageView.setImageBitmap(imageBitmap);
        } // TODO:
        /*else if (requestCode == REQUEST_WIFI_CONNECTION && resultCode == getActivity().RESULT_OK) {

            cadastrar2();

        } */
    }


    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_CODE);
    }
}
