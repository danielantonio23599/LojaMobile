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
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.AdapterProduto;
import com.daniel.lojamobile.adapter.holder.DialogHelper;
import com.daniel.lojamobile.adapter.holder.PedidoBEAN;
import com.daniel.lojamobile.adapter.interfaces.CustomItemClickListener;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.beans.Produto;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;
import com.daniel.lojamobile.util.Time;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdutoFragment extends Fragment {
    private int venda = 0;
    private RecyclerView recyclerView;
    private AdapterProduto adapter;
    private ArrayList<Produto> produtos;
    private AlertDialog alerta;
    private VendaFragment vendaFragment = null;

    public void setVendaFragment(VendaFragment vendaFragment) {
        this.vendaFragment = vendaFragment;
    }

    public void setVenda(int venda) {
        this.venda = venda;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produto, container, false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvProduto);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onResume() {
        getActivity().setTitle("Produtos");
        getUserListFromRestApi();
        super.onResume();
    }

    private void getUserListFromRestApi() {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA(getActivity()).create(LojaAPI.class);
        final Call<ArrayList<Produto>> call = api.listarProdutos(sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<ArrayList<Produto>>() {
            @Override
            public void onResponse(Call<ArrayList<Produto>> call, Response<ArrayList<Produto>> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        escondeDialog();
                        produtos = new ArrayList<>(response.body());
                        onItemClick(produtos);

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
            public void onFailure(Call<ArrayList<Produto>> call, Throwable t) {
                escondeDialog();
                DialogHelper.getAlertWithMessage("Hata", t.getMessage(), getActivity());
            }
        });


    }

    private void onItemClick(final ArrayList<Produto> produtos) {

        adapter = new AdapterProduto(getActivity(), produtos, new CustomItemClickListener() {
            @Override
            public void onItemClick(Produto produto, int position) {
                if (venda != 0) {
                    if (vendaFragment == null) {
                        vendaFragment = new VendaFragment();
                    }
                    PedidoBEAN i = getDadosPedido(produto);
                    vendaFragment.setVenda(venda);
                    vendaFragment.addItem(i);
                    replaceFragment(vendaFragment);
                    //   Toast.makeText(getActivity(), "" + user.getNome(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private PedidoBEAN getDadosPedido(Produto produto) {
        PedidoBEAN p = new PedidoBEAN();
        p.setTime(Time.getTime());
        p.setProNome(produto.getNome());
        p.setProduto(produto.getCodigo());
        p.setValor(produto.getPreco());
        p.setVenda(venda);
        return p;
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
