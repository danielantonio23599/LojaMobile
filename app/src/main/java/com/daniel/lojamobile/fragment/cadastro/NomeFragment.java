package com.daniel.lojamobile.fragment.cadastro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.R;

public class NomeFragment extends Fragment {

    private EditText nome;

    public String getNome() {
        return nome.getText() + "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_nome, container, false);
        nome = (EditText) view.findViewById(R.id.input_name);
        nome.requestFocus();
        nome.setSelection(nome.length());
        return view;
    }



}
