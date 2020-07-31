package com.daniel.lojamobile.fragment.cadastro.confirma;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.R;
import com.google.android.material.textfield.TextInputLayout;


public class ConfirmaEmailFragment extends Fragment {
    private EditText email;

    public String getEmail() {
        return email.getText()+"";
    }

    public void setEmail(String email) {
        this.email.setText(email);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirma_email, container, false);
        email = (EditText) view.findViewById(R.id.input_email);

        return view;
    }

    public boolean verificaEmail() {
        if (email.getText().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
