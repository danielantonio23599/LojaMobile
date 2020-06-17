package com.daniel.lojamobile.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.ProdutoActivity;
import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.AdapterMesa;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MesasFragment extends Fragment implements AdapterView.OnItemClickListener {
    private GridView listView;
    private AlertDialog alerta;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmente_mesas, container, false);
        //listView = (GridView) view.findViewById(R.id.lvMesas);
        buscarMesas();
        return view;
    }

    private void mostraDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LayoutInflater li = getLayoutInflater();
                //inflamos o layout alerta.xml na view
                View view = li.inflate(R.layout.alert_progress, null);
                TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
                tvDesc.setText("Buscando pedios Pendentes...");
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

    private void buscarMesas() {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA(getContext()).create(LojaAPI.class);
        final Call<ArrayList<Venda>> call = api.getVendasAbertas(sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<ArrayList<Venda>>() {
            @Override
            public void onResponse(Call<ArrayList<Venda>> call, Response<ArrayList<Venda>> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        Log.i("[IFMG]", "login correto");
                        ArrayList<Venda> u = response.body();
                        atualizaTabela(u);
                    } else {
                        Log.i("[IFMG]", "login incorreto");
                        escondeDialog();
                        // senha ou usuario incorreto

                    }
                } else {
                    Log.i("[IFMG]", "servidor fora do ar");
                    escondeDialog();

                    //servidor fora do ar
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Venda>> call, Throwable t) {
                Log.i("[IFMG]", "servidor com erro " + t.getMessage());
                escondeDialog();
            }
        });
    }


    public void atualizaTabela(ArrayList<Venda> vendas) {
        Log.i("[IFMG]", "mesas : " + vendas.size());
        AdapterMesa s = new AdapterMesa(getActivity());
        if (vendas.size() > 0) {
            s.setLin(vendas);
            listView.setAdapter(s);
            listView.setOnItemClickListener(MesasFragment.this);
        } else {
            s.setLin(vendas);
            listView.setAdapter(s);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "nenhuma mesa nova!! ", Toast.LENGTH_SHORT);
                }
            });

        }
        escondeDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdapterMesa p = (AdapterMesa) parent.getAdapter();
        Intent intent = new Intent(getActivity(), ProdutoActivity.class);
        intent.putExtra("venda", p.getLin().get(position).getCodigo());
        startActivity(intent);

    }
}
