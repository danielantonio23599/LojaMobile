package com.daniel.lojamobile.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.daniel.lojamobile.adapter.AdapterItemPedido;
import com.daniel.lojamobile.adapter.AdapterVendaNota;
import com.daniel.lojamobile.adapter.AdapterVendas;
import com.daniel.lojamobile.adapter.holder.PedidoBEAN;
import com.daniel.lojamobile.adapter.holder.ProdutosGravados;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.adapter.interfaces.VendaItemClickListener;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;
import com.daniel.lojamobile.util.PermissionUtils;
import com.daniel.lojamobile.util.SalvaDownload;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class GerarNotaFragment extends Fragment {
    private AlertDialog alerta;
    private RecyclerView recyclerView;
    private AdapterVendaNota adapter;
    private ArrayList<Venda> vendas;
    private TextView tvMessage;
    private AlertDialog alerta2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vendas, container, false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvVendas);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        String[] permissoes = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        PermissionUtils.validate(getActivity(), 0, permissoes);
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
        LojaAPI api = SyncDefaut.RETROFIT_LOJA(getContext()).create(LojaAPI.class);
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
        adapter = new AdapterVendaNota(getActivity(), venda, new VendaItemClickListener() {
            @Override
            public void onItemClick(Venda venda, int position) {
                showBaixar(venda.getCodigo());
                //   Toast.makeText(getActivity(), "" + user.getNome(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void replaceFragment(Fragment fragment) {
        // getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }

    public void showBaixar(final int venda) {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        final LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        final View view = li.inflate(R.layout.alert_remover_pedido, null);

        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        tvMessage.setText("Deseja baixar a nota da venda ?");
        //definimos para o botão do layout um clickListener
        Button ok = (Button) view.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baixarNota(venda);
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

    private void baixarNota(int venda) {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA(getContext()).create(LojaAPI.class);
        final Call<ResponseBody> call = api.geraNotaVenda(venda + "", sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.isSuccessful());
                System.out.println(response);
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        String nome = response.headers().get("nome");
                        if (!nome.equals("0")) {
                            escondeDialog();
                            File arquivo = SalvaDownload.writeResponseBodyToDisk(response.body(), nome);
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                            Intent target = new Intent(Intent.ACTION_VIEW);
                            target.setDataAndType(Uri.fromFile(arquivo),"application/pdf");
                            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            Intent intent = Intent.createChooser(target, "Open File");
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                // Instruct the user to install a PDF reader here, or something
                                //ShowToast(TAG, "Unable to open PDF. Please, install a PDF reader app.");
                            }
                            Log.i("[IFMG]", "file download was a success? " + arquivo.getAbsolutePath());
                        } else {
                            Log.i("[IFMG]", "erro ao gerar arquivo");
                        }
                    } else {

                        // senha ou usuario incorreto

                    }
                } else {
                    Log.i("[IFMG]", "servidor fora do ar");
                    //servidor fora do ar
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("[IFMG]", "erro");
            }
        });
    }
}
