package com.daniel.lojamobile.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.AdapterDespesa;
import com.daniel.lojamobile.modelo.beans.Despesa;
import com.daniel.lojamobile.modelo.beans.Empresa;

import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DespesaFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private AlertDialog alerta;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_despesa, container, false);
        listView = (ListView) view.findViewById(R.id.lvDespesa);
        return view;
    }

    @Override
    public void onResume() {
        mostraDialog();
       listarDespesas();
        super.onResume();
    }

    private void mostraDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LayoutInflater li = getLayoutInflater();
                //inflamos o layout alerta.xml na view
                View view = li.inflate(R.layout.alert_progress, null);
                TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
                tvDesc.setText("Fazendo comunicação com o servidor...");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Aguarde...");
                builder.setView(view);
                builder.setCancelable(false);
                alerta = builder.create();
                alerta.show();
            }
        });

    }

    private void escondeDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (alerta.isShowing()) {
                    alerta.dismiss();
                }
            }
        });

    }



    public void atualizaTabela(ArrayList<Despesa> despesa) {
        Log.i("[IFMG]", "mesas : " + despesa.size());
        AdapterDespesa s = new AdapterDespesa(getActivity());
        if (despesa.size() > 0) {
            s.setLin(despesa);
            listView.setAdapter(s);
            listView.setOnItemClickListener(DespesaFragment.this);
        } else {
            s.setLin(despesa);
            listView.setAdapter(s);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "nenhuma despesa nova!! ", Toast.LENGTH_SHORT);
                }
            });

        }
        escondeDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private void listarDespesas() {
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA(getContext()).create(LojaAPI.class);
        final Call<ArrayList<Despesa>> call = api.listarDespesas(sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<ArrayList<Despesa>>() {
            @Override
            public void onResponse(Call<ArrayList<Despesa>> call, Response<ArrayList<Despesa>> response) {
                System.out.println(response.isSuccessful());
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {

                        ArrayList<Despesa> u = response.body();
                        escondeDialog();
                      atualizaTabela(u);


                    } else {
                        escondeDialog();
                        // senha ou usuario incorreto

                    }
                } else {
                    escondeDialog();
                    //servidor fora do ar
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Despesa>> call, Throwable t) {
                escondeDialog();
            }
        });

    }
}
