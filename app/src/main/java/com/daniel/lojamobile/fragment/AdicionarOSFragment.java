package com.daniel.lojamobile.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.AdapterFuncionario;
import com.daniel.lojamobile.adapter.AdapterItemPedido;
import com.daniel.lojamobile.adapter.holder.PedidoBEAN;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.beans.Funcionario;
import com.daniel.lojamobile.modelo.beans.OrdemServico;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;
import com.daniel.lojamobile.util.Data;
import com.daniel.lojamobile.util.DatePickerUtil;
import com.daniel.lojamobile.util.Time;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdicionarOSFragment extends Fragment implements AdapterView.OnItemClickListener {
    private AlertDialog alerta;
    private ListView listView;
    private ArrayList<Funcionario> funcionarios = new ArrayList<>();

    private int funcionario;
    private AlertDialog alerta2;
    private EditText etNome;
    private EditText etDescicao;
    private EditText etEndereco;
    private EditText etValor;
    private EditText etData;
    private EditText etHora;
    private EditText etFuncionario;
    private Spinner spinnerStatus;
    private TextInputLayout layNome;
    private TextInputLayout layDesc;
    private TextInputLayout layEnde;
    private TextInputLayout layValor;
    private TextInputLayout layData;
    private TextInputLayout layHora;
    private TextInputLayout layFuncionario;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adicionar_os, container, false);
        etNome = (EditText) view.findViewById(R.id.input_nome);
        etDescicao = (EditText) view.findViewById(R.id.input_descricao);
        etEndereco = (EditText) view.findViewById(R.id.input_endereco);
        etValor = (EditText) view.findViewById(R.id.input_valor);
        etData = (EditText) view.findViewById(R.id.input_data);
        etHora = (EditText) view.findViewById(R.id.input_hora);
        etFuncionario = (EditText) view.findViewById(R.id.input_funcionario);
        spinnerStatus = (Spinner) view.findViewById(R.id.spinner_status);
        //
        layNome = (TextInputLayout) view.findViewById(R.id.lay_nome);
        layDesc = (TextInputLayout) view.findViewById(R.id.lay_descricao);
        layEnde = (TextInputLayout) view.findViewById(R.id.lay_endereco);
        layValor = (TextInputLayout) view.findViewById(R.id.lay_valor);
        layData = (TextInputLayout) view.findViewById(R.id.lay_data);
        layHora = (TextInputLayout) view.findViewById(R.id.lay_hora);

        layFuncionario = (TextInputLayout) view.findViewById(R.id.lay_funcionario);
        List<String> lsPeso = new ArrayList<>();
        lsPeso.add("Adicionado");
        lsPeso.add("Em Trabalho");
        lsPeso.add("Aguardadendo pagamento");
        lsPeso.add("Finalizado");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lsPeso);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  spinnerStatus.setAdapter(dataAdapter);
        ImageButton btnFuncionario = view.findViewById(R.id.btnFuncionario);
        btnFuncionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarFuncionarios();
               // showFuncionario();
            }
        });
        FloatingActionButton fab = view.findViewById(R.id.fab_venda);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrdemServico os = getOs();
                if (os != null) {
                    encaminharOS(os);
                }
            }
        });
        etData.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.i("[IFMG]", "focus");
                   showData();
                }
            }
        });
        etHora.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.i("[IFMG]", "focus");
                    showHora();
                }
            }
        });
        etDescicao.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!etDescicao.getText().toString().equals("")) {
                        layDesc.setErrorEnabled(false);
                    } else {
                        // erro funcionario
                        layDesc.setError("Descrição incorreto, favor digite novamente!");
                    }
                }
            }
        });
        etNome.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!etNome.getText().toString().equals("")) {
                        layNome.setErrorEnabled(false);
                    } else {
                        // erro funcionario
                        layNome.setError("Nome incorreto, favor digite novamente!");
                    }
                }
            }
        });
        etValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!etValor.getText().toString().equals("")) {
                        layValor.setErrorEnabled(false);
                    } else {
                        // erro funcionario
                        layValor.setError("valor incorreto, favor digite novamente!");
                    }
                }
            }
        });
        etEndereco.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!etEndereco.getText().toString().equals("")) {
                        layEnde.setErrorEnabled(false);
                    } else {
                        // erro funcionario
                        layEnde.setError("Endereço incorreto, favor digite novamente!");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Adicionar OS");
    }

    private OrdemServico getOs() {
        int retorno = 1;
        OrdemServico os = new OrdemServico();
        if (!etFuncionario.getText().toString().equals("")) {
            layFuncionario.setErrorEnabled(false);
            os.setFuncionario(funcionario);
        } else {
            // erro funcionario
            layFuncionario.setError("Funcionario incorreto, favor digite novamente!");
            retorno = 0;
        }
        if (!etNome.getText().toString().equals("")) {
            Log.i("[IFMG]", etNome.getText() + "");
            layNome.setErrorEnabled(false);
            os.setNome(etNome.getText() + "");
        } else {
            Log.i("[IFMG]", "nome");
            layNome.setError("Nome incorreto, favor digite novamente!");
            retorno = 0;
        }
        if (!etValor.getText().toString().equals("")) {
            layValor.setErrorEnabled(false);
            os.setValor(Float.parseFloat(etValor.getText() + ""));
        } else {
            layValor.setError("Valor incorreto, favor digite novamente!");
            retorno = 0;
        }
        if (!etDescicao.getText().toString().equals("")) {
            layDesc.setErrorEnabled(false);
            os.setDescricao(etDescicao.getText() + "");
        } else {
            layDesc.setError("Decrição incorreto, favor digite novamente!");
            retorno = 0;
        }
        if (!etEndereco.getText().toString().equals("")) {
            layEnde.setErrorEnabled(false);
            os.setEndereco(etEndereco.getText() + "");

        } else {
            layEnde.setError("Endereço incorreto, favor digite novamente!");
            retorno = 0;

        }if (!etData.getText().toString().equals("")) {
            Log.i("[IFMG]", etNome.getText() + "");
            layData.setErrorEnabled(false);
            os.setData(Data.formataDataUS(etData.getText() + ""));
        } else {
            Log.i("[IFMG]", "nome");
            layData.setError("Data incorreto, favor digite novamente!");
            retorno = 0;
        }
        if (!etHora.getText().toString().equals("")) {
            Log.i("[IFMG]", etNome.getText() + "");
            layHora.setErrorEnabled(false);
            os.setHora(etHora.getText() + "");
        } else {
            Log.i("[IFMG]", "nome");
            layHora.setError("Hoara incorreto, favor digite novamente!");
            retorno = 0;
        }
        os.setStatus(spinnerStatus.getSelectedItem() + "");
        if (retorno == 0) {
            return null;
        } else {
            return os;
        }

    }

    private void encaminharOS(OrdemServico os) {
        //Todo selvet para cadastrar pedido
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        final Call<Void> call = api.enviarOS(new Gson().toJson(os), sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    String sucesso = response.headers().get("sucesso");
                    if (auth.equals("1")) {
                        escondeDialog();
                        Toast.makeText(getActivity(), "Os enviados com " + sucesso, Toast.LENGTH_LONG).show();
                        limparDados();

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
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("[IFMG]", "servidor com erro " + t.getMessage());
                escondeDialog();
            }
        });


    }

    private void limparDados() {
        etNome.setText("");
        etDescicao.setText("");
        etEndereco.setText("");
        etValor.setText("");
        etFuncionario.setText("");
        etHora.setText("");
        etData.setText("");
    }

    private void mostraDialog() {

        final LayoutInflater li = getLayoutInflater();
        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.alert_progress, null);
        TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
        tvDesc.setText("Enviando os...");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Aguarde...");
        builder.setView(view);
        builder.setCancelable(false);
        alerta = builder.create();
        alerta.show();
    }

    public void showFuncionario() {
        final LayoutInflater li = getLayoutInflater();
        //inflamos o layout alerta.xml na view
        final View view = li.inflate(R.layout.alert_lista_funcionario, null);
        listView = (ListView) view.findViewById(R.id.lvFuncionarios);
        AdapterFuncionario s = new AdapterFuncionario(getActivity());
        if (funcionarios.size() > 0) {
            s.setLin(funcionarios);
            listView.setAdapter(s);
            listView.setOnItemClickListener(AdicionarOSFragment.this);
        } else {
            s.setLin(funcionarios);
            listView.setAdapter(s);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "nenhuma mesa nova!! ", Toast.LENGTH_SHORT);
                }
            });

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        alerta2 = builder.create();
        alerta2.show();
    }

    public void atualizaTabela() {
        showFuncionario();
    }

    private void buscarFuncionarios() {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(getActivity());
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        final Call<ArrayList<Funcionario>> call = api.listarFuncionarios(sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<ArrayList<Funcionario>>() {
            @Override
            public void onResponse(Call<ArrayList<Funcionario>> call, Response<ArrayList<Funcionario>> response) {
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        ArrayList<Funcionario> u = response.body();
                        funcionarios = u;
                        atualizaTabela();
                        escondeDialog();

                    } else {
                        escondeDialog();
                    }
                } else {
                    escondeDialog();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Funcionario>> call, Throwable t) {
                escondeDialog();
            }
        });
    }

    private void escondeDialog() {
        if (alerta.isShowing()) {
            alerta.dismiss();
        }
    }


    public void replaceFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


    private void mudaActivity(final Class classe) {
        Log.i("[IFMG]", "passou no muda actyvity" + classe.getName());
        final Intent intent = new Intent(getActivity(), classe);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdapterFuncionario p = (AdapterFuncionario) parent.getAdapter();
        funcionario = p.getLin().get(position).getCodigo();
        etFuncionario.setText(p.getLin().get(position).getNome());
        alerta2.dismiss();
    }

    private void showData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final DatePicker picker = new DatePicker(getActivity());
        picker.setCalendarViewShown(false);
        builder.setTitle("Data do Serviço");
        builder.setView(picker);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                etData.setText(picker.getDayOfMonth()+"-"+picker.getMonth()+"-"+picker.getYear());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void showHora() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final TimePicker picker = new TimePicker(getActivity());
        picker.setEnabled(true);
        builder.setView(picker);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                etHora.setText(picker.getHour() + ":" + picker.getMinute());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
