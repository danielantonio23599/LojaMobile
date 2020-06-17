package com.daniel.lojamobile.fragment.cadastro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.R;


public class EmailFragment extends Fragment {
    private EditText email;

    public String getEmail() {
        return email.getText() + "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_email, container, false);
        email = (EditText) view.findViewById(R.id.input_email);
        email.requestFocus();
        email.setSelection(email.length());
        return view;
    }
}
