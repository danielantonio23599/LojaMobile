package com.daniel.lojamobile.fragment.cadastro;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.Cadastro2Activity;
import com.daniel.lojamobile.R;
import com.daniel.lojamobile.util.AppSignatureHashHelper;
import com.daniel.lojamobile.util.SMSReceiver;
import com.google.android.material.textfield.TextInputLayout;

//import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;

public class TelefoneFragment extends Fragment {
    private EditText telefone;
    private TextInputLayout layTelefone;
    public static final String TAG = Cadastro2Activity.class.getSimpleName();

    private SMSReceiver smsReceiver;
    AppSignatureHashHelper appSignatureHashHelper;

    public String getNome() {
        return telefone.getText() + "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_telefone, container, false);
        telefone = (EditText) view.findViewById(R.id.input_telefone);
        layTelefone = (TextInputLayout) view.findViewById(R.id.layTelefone);
       // MaskEditTextChangedListener maskTEL = new MaskEditTextChangedListener("(##)#####-####", telefone);
       // telefone.addTextChangedListener(maskTEL);
        telefone.requestFocus();
        telefone.setSelection(telefone.length());

        appSignatureHashHelper = new AppSignatureHashHelper(getActivity());

        // Este código requer um tempo para obter as chaves Hash, comentar e compartilhar as chaves
        Log.i(TAG, "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));

        return view;
    }

    public void enviarSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(telefone.getText() + "", null, "<#>Use o Codigo " + " 12345 " + " para confirmar o numero de cadastro do speed food" + appSignatureHashHelper.getAppSignatures().get(0), null, null);
    }

    public boolean verificaTelefone() {
        if (telefone.getText().length() == 14) {
            layTelefone.setErrorEnabled(false);
            return true;
        } else {
            layTelefone.setError("Telefone incorreto");
            return false;
        }
    }
}
