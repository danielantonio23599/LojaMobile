package com.daniel.lojamobile.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.ProdutoActivity;
import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.AdapterPedidoVenda;
import com.daniel.lojamobile.adapter.holder.Pedido;
import com.daniel.lojamobile.adapter.holder.ProdutosGravados;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.modelo.beans.Devolucao;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.beans.PreferencesSettings;
import com.daniel.lojamobile.modelo.beans.SharedPreferences;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;
import com.daniel.lojamobile.util.Time;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetalhesVendaFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private AlertDialog alerta;
    private int venda;
    private EditText cliente;
    private EditText codigo;
    private EditText frete;
    private EditText desconto;
    private EditText devolucao;
    private EditText total;
    private TextView tvMessage;
    private AlertDialog alerta2;
    private EditText etMotivo;
    private AlertDialog alerta3;
    private EditText etQTD;

    public int getVenda() {
        return venda;
    }

    public void setVenda(int venda) {
        this.venda = venda;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalhes_venda, container, false);
        listView = (ListView) view.findViewById(R.id.lvProdutos);
        cliente = view.findViewById(R.id.input_cliente);
        codigo = view.findViewById(R.id.input_venda);
        frete = view.findViewById(R.id.input_Frete);
        desconto = view.findViewById(R.id.input_desconto);
        devolucao = view.findViewById(R.id.input_devolucao);
        total = view.findViewById(R.id.input_valor_final);
        return view;
    }

    @Override
    public void onResume() {
        mostraDialog();
        getDadosVenda();
        listarPedidosVenda();
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

    private void getDadosVenda() {

        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA(getContext()).create(LojaAPI.class);
        final Call<Venda> call = api.listarVenda(sh.getEmpEmail(), sh.getEmpSenha(), venda + "");
        call.enqueue(new Callback<Venda>() {
            @Override
            public void onResponse(Call<Venda> call, Response<Venda> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        Log.i("[IFMG]", "login correto");
                        Venda u = response.body();
                        setDados(u);

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
            public void onFailure(Call<Venda> call, Throwable t) {
                Log.i("[IFMG]", "servidor com erro " + t.getMessage());
                escondeDialog();
            }
        });
    }

    private void setDados(Venda u) {
        codigo.setText(u.getCodigo() + "");
        if (u.getCliente() != null) {
            cliente.setText(u.getCliente() + "");
        } else {
            cliente.setText("Cliente Especial");
        }
        frete.setText(u.getFrete() + "");
        desconto.setText(u.getDesconto() + "");
        devolucao.setText(u.getDevolucao() + "");
        total.setText(u.getValorFinal() + "");
        escondeDialog();
    }


    public void atualizaTabela(ArrayList<ProdutosGravados> pedidos) {
        Log.i("[IFMG]", "mesas : " + pedidos.size());
        AdapterPedidoVenda s = new AdapterPedidoVenda(getActivity());
        if (pedidos.size() > 0) {
            s.setLin(pedidos);
            listView.setAdapter(s);
            listView.setOnItemClickListener(DetalhesVendaFragment.this);
        } else {
            s.setLin(pedidos);
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
        AdapterPedidoVenda pp = (AdapterPedidoVenda) parent.getAdapter();
        ProdutosGravados p = pp.getLin().get(position);
        showDevolver(p);
    }

    private void listarPedidosVenda() {
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA(getContext()).create(LojaAPI.class);
        final Call<ArrayList<ProdutosGravados>> call = api.listarProdutosVenda(sh.getEmpEmail(), sh.getEmpSenha(), venda + "");
        call.enqueue(new Callback<ArrayList<ProdutosGravados>>() {
            @Override
            public void onResponse(Call<ArrayList<ProdutosGravados>> call, Response<ArrayList<ProdutosGravados>> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        ArrayList<ProdutosGravados> u = response.body();
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
            public void onFailure(Call<ArrayList<ProdutosGravados>> call, Throwable t) {
                escondeDialog();
            }
        });

    }

    public void showDevolver(final ProdutosGravados p) {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        final LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        final View view = li.inflate(R.layout.alert_remover_pedido, null);

        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        tvMessage.setText("Tem serteza que deseja devolver o pedido selecionado ?");
        //definimos para o botão do layout um clickListener
        Button ok = (Button) view.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMotivo(p);
                alerta2.dismiss();
            }
        });
        Button cancelar = (Button) view.findViewById(R.id.btnCancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta2.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        alerta2 = builder.create();
        alerta2.show();
    }

    public void showMotivo(final ProdutosGravados p) {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        final LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        final View view = li.inflate(R.layout.alert_motivo, null);
        etMotivo = (EditText) view.findViewById(R.id.etMotivo);
        etQTD = (EditText) view.findViewById(R.id.etQTD);
        //definimos para o botão do layout um clickListener
        Button ok = (Button) view.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMotivo.getText().equals("")) {
                    Toast.makeText(getContext(), "Insira um motivo", Toast.LENGTH_SHORT).show();

                } else if (!etQTD.getText().equals("")) {
                    Toast.makeText(getContext(), "Insira a quantidade adequada", Toast.LENGTH_SHORT).show();
                } else if (Float.parseFloat(etQTD.getText() + "") <= 0 || Float.parseFloat(etQTD.getText() + "") >= p.getQuantidade()) {
                    Toast.makeText(getContext(), "Insira a quantidade adequada", Toast.LENGTH_SHORT).show();
                } else {
                    devolverPedido(p, etMotivo.getText() + "", Float.parseFloat(etQTD.getText() + ""));
                    alerta3.dismiss();
                }
            }
        });
        Button cancelar = (Button) view.findViewById(R.id.btnCancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta3.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        alerta3 = builder.create();
        alerta3.show();
    }

    private void devolverPedido(ProdutosGravados p, String motivo, float quantidade) {
        Devolucao d = new Devolucao();
        d.setProduto(p.getNome() + "");
        d.setQuantidade(quantidade);
        d.setMotivo(motivo);
        d.setValor(p.getValorUNI() * p.getQuantidade());
        d.setTime(Time.getTime());
        Log.i("[IFMG]", "faz selveti buscando pedidos pendentes");
        mostraDialog();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA(getContext()).create(LojaAPI.class);
        SharedPreferences sh = PreferencesSettings.getAllPreferences(getContext());
        final Call<Void> call = api.devolverPedido(new Gson().toJson(d), p.getCodPedidVenda() + "", sh.getEmail(), sh.getSenha());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        escondeDialog();
                        Toast.makeText(getContext(), response.headers().get("sucesso"), Toast.LENGTH_LONG).show();
                    } else {
                        escondeDialog();
                    }
                } else {
                    escondeDialog();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                escondeDialog();
            }
        });
    }
}
