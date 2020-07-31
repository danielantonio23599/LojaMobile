package com.daniel.lojamobile.fragment.cadastro;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.util.UtilImageTransmit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class FotoFragment extends Fragment {
    private ImageView foto;
    private TextView tvFoto;
    private static final int REQUEST_IMAGE_CAPTURE_CODE = 1;
    private byte[] fotoUsuario;

    public byte[] getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(byte[] fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    private Button btnFoto;
    private Button btnGaleria;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_foto, container, false);
        foto = (ImageView) view.findViewById(R.id.imagem_signin);
        tvFoto = (TextView) view.findViewById(R.id.tvFoto);
        btnFoto = (Button) view.findViewById(R.id.btnFoto);
        btnGaleria = (Button) view.findViewById(R.id.btnGaleria);
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntentGaleria();
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
            }

            //imageView.setImageBitmap(imageBitmap);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Log.i("FOTO", "galeria");
            Uri imagemSelecionada = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagemSelecionada);
            } catch (IOException e) {
                e.printStackTrace();
            }
            foto.setImageBitmap(bitmap);
            foto.setBackgroundColor(Color.WHITE);
            tvFoto.setVisibility(View.GONE);
            UtilImageTransmit utilImageTransmit = new UtilImageTransmit();
            fotoUsuario = utilImageTransmit.convertImageToByte(bitmap);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_CODE);
    }

    private void dispatchTakePictureIntentGaleria() {
        Intent intentPegaFoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intentPegaFoto,2);
    }
}
