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
import android.widget.EditText;
import android.widget.ListView;
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
import com.daniel.lojamobile.adapter.holder.Pedido;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.adapter.interfaces.VendaItemClickListener;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.beans.PreferencesSettings;
import com.daniel.lojamobile.modelo.beans.SharedPreferences;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevolverFragment extends Fragment {
    private AlertDialog alerta;
    private RecyclerView recyclerView;
    private AdapterVendas adapter;
    private ArrayList<Venda> vendas;


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
        getActivity().setTitle("Vendas");
        atualizarFragment();
        super.onResume();

    }

    public void atualizarFragment() {
        getListVendasRestApi();
    }


    private void getListVendasRestApi() {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        final Call<ArrayList<Venda>> call = api.listarVendas(sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<ArrayList<Venda>>() {
            @Override
            public void onResponse(Call<ArrayList<Venda>> call, Response<ArrayList<Venda>> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        Log.i("[IFMG]", "login correto");
                        vendas = new ArrayList<>(response.body());
                        escondeDialog();
                        onItemClick(vendas);

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

    private void mostraDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LayoutInflater li = getLayoutInflater();
                //inflamos o layout alerta.xml na view
                View view = li.inflate(R.layout.alert_progress, null);
                TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
                tvDesc.setText("Buscando Vendas...");
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

    private void onItemClick(final ArrayList<Venda> venda) {
        adapter = new AdapterVendas(getActivity(), venda, new VendaItemClickListener() {
            @Override
            public void onItemClick(Venda venda, int position) {
                DetalhesVendaFragment d = new DetalhesVendaFragment();
                d.setVenda(venda.getCodigo());
                replaceFragment(d);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void replaceFragment(Fragment fragment) {
        // getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }
}
