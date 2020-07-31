package com.daniel.lojamobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;


import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.beans.Servidor;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.modelo.persistencia.BdServidor;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;
import com.daniel.lojamobile.util.Criptografia;
import com.daniel.lojamobile.util.TecladoUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by polo on 30/06/2018.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail, user_pwd;
    private Button buttonLogin;
    private AlertDialog alerta;
    private AlertDialog alerta2;
    EditText ip;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //settings.setUserLogOff(getBaseContext());
        //mostraDialog();
        getWindow().setStatusBarColor(R.color.colorPrimaryDark);
        setContentView(R.layout.activity_login);

        userEmail = (EditText) findViewById(R.id.input_email);
        user_pwd = (EditText) findViewById(R.id.input_senha);
        buttonLogin = (Button) findViewById(R.id.btnLogin);
        //todo validar todas as permissões de uma vez aqui

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo Verificar credenciais e rodar SplashScreen
                fazLogin(userEmail.getText() + "", user_pwd.getText() + "");
                //Com MD5: -> Cadastrar Adm com MD5
//                fazLogin(userName.getText() + "", StringUtils.md5(user_pwd.getText() + ""));

            }
        });


    }

    private void mudaActivity(final Class classe) {
        Log.i("[IFMG]", "passou no muda actyvity" + classe.getName());
        final Intent intent = new Intent(this, classe);
        startActivity(intent);
        finish();

    }


    public void fazLogin(String nomeUsuario, String senha) {
        Log.i("[IFMG]", "faz login");
        mostraDialog();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        final Call<Empresa> call = api.fazLoginEmpresa(nomeUsuario, Criptografia.criptografar(senha));
        call.enqueue(new Callback<Empresa>() {
            @Override
            public void onResponse(Call<Empresa> call, Response<Empresa> response) {
                if (response.code() == 200) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        Empresa u = response.body();
                        BdEmpresa bd = new BdEmpresa(getApplication());
                        bd.deleteAll();
                        bd.insert(u);
                        bd.close();
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        escondeDialog();

                        mudaActivity(LoginGarcomActivity.class);


                    } else {
                        escondeDialog();
                        Toast.makeText(getBaseContext(), "Nome de usuário ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    escondeDialog();
                    Toast.makeText(getBaseContext(), "Erro ao fazer login, erro servidor", Toast.LENGTH_SHORT).show();
                    Log.i("[IFMG]", "t1: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Empresa> call, Throwable t) {
                escondeDialog();
                Toast.makeText(getBaseContext(), "Erro ao acessar servidor", Toast.LENGTH_SHORT).show();
                Log.i("[IFMG]", "t2: " + t.getMessage());
                Log.i("[IFMG]", "t2: " + t.getLocalizedMessage());
                //mudaActivity(MainActivity.class);
            }
        });
    }

    private void mostraDialog() {
        final LayoutInflater li = getLayoutInflater();
        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.alert_progress, null);
        TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
        tvDesc.setText("Fazendo Login...");
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

    public void showConfg() {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        final LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        final View view = li.inflate(R.layout.alert_settings, null);
        BdServidor bd = new BdServidor(getApplication());
        ip = (EditText) view.findViewById(R.id.etIP);
        ip.setText(bd.listar().getIp()+"");
        //definimos para o botão do layout um clickListener
        Button ok = (Button) view.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TecladoUtil.hideKeyboard(getApplication(), view);
                BdServidor bd = new BdServidor(getApplication());
                Servidor s = new Servidor();
                s.setIp(ip.getText() + "");
                bd.insert(s);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alerta2 = builder.create();
        alerta2.show();
    }
}
