package com.daniel.lojamobile;


import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.daniel.lojamobile.fragment.cadastro.BuscaEnderecoFragment;
import com.daniel.lojamobile.fragment.cadastro.EmailFragment;
import com.daniel.lojamobile.fragment.cadastro.EnderecoFragment;
import com.daniel.lojamobile.fragment.cadastro.FotoFragment;
import com.daniel.lojamobile.fragment.cadastro.NomeFragment;
import com.daniel.lojamobile.fragment.cadastro.TelefoneFragment;
import com.daniel.lojamobile.fragment.cadastro.confirma.ConfirmaEmailFragment;
import com.daniel.lojamobile.fragment.cadastro.confirma.ConfirmaTelefoneFragment;
import com.daniel.lojamobile.modelo.beans.Endereco;
import com.daniel.lojamobile.sync.CepAPI;
import com.daniel.lojamobile.sync.SyncCEP;
import com.daniel.lojamobile.util.PermissionUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cadastro2Activity extends AppCompatActivity {
    private Button next;
    private ImageButton back;
    private Toolbar toolbar;
    private FragmentContainerView fragment;
    private NomeFragment n = new NomeFragment();
    private EmailFragment e = new EmailFragment();
    private TelefoneFragment t = new TelefoneFragment();
    private EnderecoFragment end = new EnderecoFragment();
    private BuscaEnderecoFragment busca_end = new BuscaEnderecoFragment();
    private FotoFragment foto = new FotoFragment();
    private ConfirmaEmailFragment conf_email = new ConfirmaEmailFragment();
    private ConfirmaTelefoneFragment conf_tele = new ConfirmaTelefoneFragment();
    private Endereco endereco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastro2);
        String[] permissoes = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.BROADCAST_SMS
        };
        PermissionUtils.validate(this, 0, permissoes);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //botão voltar na actionBar
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        back = (ImageButton) findViewById(R.id.imBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        next = (Button) findViewById(R.id.btnNext);
        fragment = (FragmentContainerView) findViewById(R.id.cadastro_fragment);

        replaceFragment(n);
        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                Log.i("IFMG", getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment) + "");
                Log.i("IFMG", R.layout.fragment_cadastro_nome + "");
                if (n == (getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment))) {
                    replaceFragment(e);
                } else if ((getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment)) == e) {
                    replaceFragment(t);
                } else if ((getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment)) == t) {
                    if (t.verificaTelefone()) {
                        t.enviarSMS();
                        replaceFragment(conf_tele);
                    }
                } else if ((getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment)) == conf_tele) {
                    replaceFragment(conf_email);
                } else if ((getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment)) == conf_email) {
                    replaceFragment(end);
                } else if ((getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment)) == end) {

                } /*else if ((getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment)) == foto) {
                    replaceFragment(end);
                }*/
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        //nao deixa adicionar a fila de fragments
        //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.cadastro_fragment, fragment, "cadastro").addToBackStack(null).commit();
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                if (n != (getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment))) {
                    getSupportFragmentManager().popBackStack();
                }
                break;
        }
        return true;
    }*/

    @Override
    public void onBackPressed() {
        if (n != (getSupportFragmentManager().findFragmentById(R.id.cadastro_fragment))) {
            getSupportFragmentManager().popBackStack();
        }
    }

    /* @SuppressLint("MissingSuperCall")
     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
         Log.i("IFMG", "entrou");
         Log.i("IFMG", "requestCode" + requestCode);
         Log.i("IFMG", "resultCode" + resultCode);
        // Log.i("IFMG", "states" + states.);
         switch (requestCode) {
             case Activity.RESULT_OK:
                 // Todas as alterações necessárias foram feitas
                 Log.i("IFMG", "ok");
                 break;
             case Activity.RESULT_CANCELED:
                 // O usuário cancelou o dialog, não fazendo as alterações requeridas
                 Log.i("IFMG", "falha");
                 break;
             default:
                 break;

         }
     }*/
   /* @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == end.REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // Todas as alterações necessárias foram feitas
                    final Localizacao lo = new Localizacao(this, this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Endereco location = lo.buscaEndereco(end.getEnd());
                            if (location != null) {
                                end.setEndereco(location);
                            } else {
                                Log.i("IFMG", "lo vasio");
                            }
                        }
                    }).start();

                    break;
                case Activity.RESULT_CANCELED:
                    // O usuário cancelou o dialog, não fazendo as alterações requeridas
                    Log.i("IFMG", "falha");
                    break;
                default:
                    break;
            }
        }
    }
*/
    public Endereco getEnderecoByCEP(String cep) {
        CepAPI api = SyncCEP.RETROFIT_CEP(this).create(CepAPI.class);

        final Call<Endereco> call = api.getEnderecoByCEP(cep);

        call.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.code() == 200) {


                    Log.i("[IFMG]", response.body() + "");
                    endereco = response.body();

                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
            }
        });
        return endereco;
    }
}
