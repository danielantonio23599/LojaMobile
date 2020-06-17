package com.daniel.lojamobile.fragment.cadastro;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.modelo.beans.Endereco;
import com.daniel.lojamobile.sync.CepAPI;
import com.daniel.lojamobile.sync.SyncCEP;
import com.daniel.lojamobile.util.Localizacao;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnderecoFragment extends Fragment {
    private LinearLayout lyEndereco;
    private EditText logradouro;
    private EditText bairro;
    private EditText cidade;
    private EditText numero;
    private EditText uf;
    private EditText complemento;
    private EditText cep;
    private TextView end;
    private Endereco endereco;
    private TextInputLayout layCEP;
    public int REQUEST_CHECK_SETTINGS = 1;
    private Button btnCep;

    public EditText getCep() {
        return cep;
    }

    public LinearLayout getLyEndereco() {
        return lyEndereco;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscar_endereco, container, false);
        lyEndereco = (LinearLayout) view.findViewById(R.id.lyEndereco);
        end = (TextView) view.findViewById(R.id.tvEndereco);
        // edit text
        logradouro = (EditText) view.findViewById(R.id.input_logradouro);
        bairro = (EditText) view.findViewById(R.id.input_bairro);
        cidade = (EditText) view.findViewById(R.id.input_cidade);
        cep = (EditText) view.findViewById(R.id.input_cep);
        cep.requestFocus();
        cep.setSelection(cep.length());
        layCEP = (TextInputLayout) view.findViewById(R.id.lay_cep);
        numero = (EditText) view.findViewById(R.id.input_numero);
        uf = (EditText) view.findViewById(R.id.input_uf);
        complemento = (EditText) view.findViewById(R.id.input_complemento);
        btnCep = (Button) view.findViewById(R.id.btnCep);
        btnCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cep.getText().length() < 8) {
                    layCEP.setError("Cep incorreto, favor digite novamente!");
                } else {
                    layCEP.setErrorEnabled(false);
                    //layCEP.setHelperText("O CEP digitado sera usado como busca para pesquisa de endereço");
                    getEnderecoByCEP();
                }

            }
        });
        lyEndereco.setVisibility(view.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public Endereco getEndereco() {
        Endereco e = new Endereco();
        e.setLogradouro(logradouro.getText() + "");
        e.setBairro(bairro.getText() + "");
        e.setLocalidade(cidade.getText() + "");
        e.setUf(uf.getText() + "");
        e.setNumero(numero.getText() + "");
        e.setCep(cep.getText() + "");
        e.setComplemento(complemento.getText() + "");
        return e;
    }

    public void setEndereco(Endereco e) {
        endereco = e;
    }

    public void setDados(Endereco e) {
        logradouro.setText(e.getLogradouro() + "");
        bairro.setText(e.getBairro());
        cidade.setText(e.getLocalidade());
        uf.setText(e.getUf());
        numero.setText(e.getNumero());
        cep.setText(e.getCep());
        complemento.setText(e.getComplemento());
    }

    public void pegaEndereco() {
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest()
                .setFastestInterval(150).setFastestInterval(150)
                .setInterval(300).setInterval(300)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationRequest mLocationRequestBalancedPowerAccuracy = new LocationRequest()
                .setFastestInterval(150).setFastestInterval(150)
                .setInterval(300).setInterval(300)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy)
                .addLocationRequest(mLocationRequestBalancedPowerAccuracy);
        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Log.i("IFMG", "localização ativa ");
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    final Localizacao lo = new Localizacao(getContext(), getActivity());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Endereco location = lo.buscaEndereco(end);
                            if (location != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEndereco(location);
                                    }
                                });

                            } else {
                                Log.i("IFMG", "lo vasio");
                            }
                        }
                    }).start();


                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i("IFMG", "ativa localização");
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.i("IFMG", "nao sei");
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });

    }

    public void getEnderecoByCEP() {
        CepAPI api = SyncCEP.RETROFIT_CEP(getContext()).create(CepAPI.class);

        final Call<Endereco> call = api.getEnderecoByCEP(cep.getText() + "");

        call.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.code() == 200) {
                    Log.i("[IFMG]", response.body() + "");
                    Endereco u = response.body();
                    if (u != null) {
                        setDados(u);
                        lyEndereco.setVisibility(View.VISIBLE);
                    } else {
                        layCEP.setError("CEP INCORRETO");
                        Toast.makeText(getActivity(), "CEP incorreto, tente novamento", Toast.LENGTH_LONG).show();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
            }
        });
    }
}
