package com.daniel.lojamobile.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.AdapterClientes;
import com.daniel.lojamobile.adapter.holder.Cliente;

import com.daniel.lojamobile.adapter.interfaces.ClienteItemClickListener;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientesFragment extends Fragment {
    private AlertDialog alerta;
    private AlertDialog alerta2;
    private RecyclerView recyclerView;
    private AdapterClientes adapter;
    private ArrayList<Cliente> clientes;
    private int venda;

    public int getVenda() {
        return venda;
    }

    public void setVenda(int venda) {
        this.venda = venda;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vendas, container, false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvVendas);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizarFragment();
    }

    public void atualizarFragment() {
        getListClientesRestApi();
    }


    private void getListClientesRestApi() {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        final Call<ArrayList<Cliente>> call = api.listarClientes(sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<ArrayList<Cliente>>() {
            @Override
            public void onResponse(Call<ArrayList<Cliente>> call, Response<ArrayList<Cliente>> response) {

                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        Log.i("[IFMG]", "login correto");
                        clientes = new ArrayList<>(response.body());
                        escondeDialog();
                        onItemClick(clientes);

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
            public void onFailure(Call<ArrayList<Cliente>> call, Throwable t) {
                Log.i("[IFMG]", "servidor com erro " + t.getMessage());
                escondeDialog();
            }
        });


    }

    private void mostraDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LayoutInflater li = getLayoutInflater();
                //inflamos o layout alerta.xml na view
                View view = li.inflate(R.layout.alert_progress, null);
                TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
                tvDesc.setText("Buscando Clientes...");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        //item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                // Here is where we are going to implement the filter logic
                return false;
            }

        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void onItemClick(final ArrayList<Cliente> cliente) {
        adapter = new AdapterClientes(getActivity(), cliente, new ClienteItemClickListener() {
            @Override
            public void onItemClick(Cliente cliente, int position) {
                showAdicionarCliente(cliente.getCodigo(), venda);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void adicionarClienteVenda(int cliente, int venda) {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        final Call<Void> call = api.adicionarClienteVenda(sh.getEmpEmail(), sh.getEmpSenha(), venda + "", cliente + "");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        escondeDialog();
                        String a = response.headers().get("sucesso");
                        Toast.makeText(getContext(), a, Toast.LENGTH_LONG).show();
                        replaceFragment(new VendasAbertasFragment());
                    } else {
                        escondeDialog();
                    }
                } else {
                    escondeDialog();
                    //servidor fora do ar
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                escondeDialog();
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        // getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }

    private void showAdicionarCliente(final int cliente, final int venda) {
        final LayoutInflater li = getLayoutInflater();
        //inflamos o layout alerta.xml na view
        final View view = li.inflate(R.layout.alert_add_cliente, null);
        //definimos para o botão do layout um clickListener
        Button ok = (Button) view.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta2.dismiss();
                adicionarClienteVenda(cliente, venda);

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
}
