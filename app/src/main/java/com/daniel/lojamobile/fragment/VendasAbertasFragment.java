package com.daniel.lojamobile.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.AdapterVendas;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.adapter.interfaces.VendaItemClickListener;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.beans.PreferencesSettings;
import com.daniel.lojamobile.modelo.beans.SharedPreferences;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;
import com.daniel.lojamobile.ProdutoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendasAbertasFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterVendas adapter;
    private ArrayList<Venda> mesa;
    private AlertDialog alerta;
    private EditText numMesa;
    private AlertDialog alerta2;
    ProdutoFragment produtoFragment = new ProdutoFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmente_mesas, container, false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvMesa);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAbrirVenda();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        getActivity().setTitle("Vendas Abertas");
        getUserListFromRestApi();
        super.onResume();
    }

    private void showAbrirVenda() {
        final LayoutInflater li = getLayoutInflater();
        //inflamos o layout alerta.xml na view
        final View view = li.inflate(R.layout.alert_add_mesa, null);
        //definimos para o botão do layout um clickListener
        Button ok = (Button) view.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirVenda();
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

    private void abrirVenda() {
        Log.i("[IFMG]", "abrir venda ");
        mostraDialog();
        SharedPreferences sh = PreferencesSettings.getAllPreferences(getContext());
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        final Call<Void> call = api.abrirVenda(sh.getEmail(), sh.getSenha());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    final String sucesso = response.headers().get("sucesso");
                    if (auth.equals("1")) {
                        escondeDialog();

                        if (sucesso.equals("0")) {
                            Log.i("[IFMG]", "sucesso : " + sucesso);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(getContext(), "Mesa já Aberta", Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (sucesso.equals("-1")) {
                            Toast.makeText(getContext(), "Caixa está fechado", Toast.LENGTH_LONG).show();
                        } else {
                            getUserListFromRestApi();
                        }
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
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("[IFMG]", "servidor com erro " + t.getMessage());
                escondeDialog();
            }
        });
    }

    private void getUserListFromRestApi() {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        final Call<ArrayList<Venda>> call = api.getVendasAbertas(sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<ArrayList<Venda>>() {
            @Override
            public void onResponse(Call<ArrayList<Venda>> call, Response<ArrayList<Venda>> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        Log.i("[IFMG]", "login correto");
                        mesa = new ArrayList<>(response.body());
                        escondeDialog();
                        onItemClick(mesa);
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

    private void onItemClick(ArrayList<Venda> venda) {
        adapter = new AdapterVendas(getActivity(), venda, new VendaItemClickListener() {
            @Override
            public void onItemClick(Venda user, int position) {
              /*  Intent intent = new Intent(getActivity(), ProdutoActivity.class);
                intent.putExtra("venda", user.getCodigo());
                startActivity(intent);*/

                // Toast.makeText(getContext(), "" + user.getStatus(), Toast.LENGTH_SHORT).show();
                produtoFragment.setVenda(user.getCodigo());
                replaceFragment(produtoFragment);
            }
        });
        recyclerView.setAdapter(adapter);
    }


    private void mostraDialog() {

        final LayoutInflater li = getLayoutInflater();
        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.alert_progress, null);
        TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
        tvDesc.setText("Buscando pedios Pendentes...");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Aguarde...");
        builder.setView(view);
        builder.setCancelable(false);
        alerta = builder.create();
        alerta.show();
    }

    private void escondeDialog() {
        if (alerta.isShowing()) {
            alerta.dismiss();
        }
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                // Here is where we are going to implement the filter logic
                return true;
            }

        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void replaceFragment(Fragment fragment) {
        //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }
}
