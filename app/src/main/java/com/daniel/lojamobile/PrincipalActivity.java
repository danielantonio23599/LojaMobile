package com.daniel.lojamobile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.daniel.lojamobile.fragment.AdicionarOSFragment;
import com.daniel.lojamobile.fragment.DespesaFragment;
import com.daniel.lojamobile.fragment.DevolverFragment;
import com.daniel.lojamobile.fragment.GerarNotaFragment;
import com.daniel.lojamobile.fragment.GerarReciboFragment;
import com.daniel.lojamobile.fragment.HomeFragment;
import com.daniel.lojamobile.fragment.OSFragment;
import com.daniel.lojamobile.fragment.OSHojeFragment;
import com.daniel.lojamobile.fragment.ProdutoFragment;
import com.daniel.lojamobile.fragment.VendasAbertasFragment;
import com.daniel.lojamobile.fragment.VendasFragment;
import com.daniel.lojamobile.modelo.beans.Caixa;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.beans.Usuario;
import com.daniel.lojamobile.modelo.persistencia.BdEmpresa;
import com.daniel.lojamobile.modelo.persistencia.BdUsuario;
import com.daniel.lojamobile.sync.LojaAPI;
import com.daniel.lojamobile.sync.SyncDefaut;
import com.daniel.lojamobile.util.UtilImageTransmit;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView perfil;
    private TextView nome, email;
    private TextView tvMessage;
    private AlertDialog alerta;
    private AlertDialog alerta2;
    private AlertDialog alerta3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header_principal);
        perfil = (ImageView) view.findViewById(R.id.imPerfil);
        nome = (TextView) view.findViewById(R.id.tvUser);
        email = (TextView) view.findViewById(R.id.tvEmail);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaActivity(PerfilActivity.class);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                }
            }
        });
    }

    private void setDados() {
        BdUsuario bdUsuario = new BdUsuario(getApplicationContext());
        Usuario u = bdUsuario.listar();
        //fecha conexao
        bdUsuario.close();
        Bitmap bitmap = UtilImageTransmit.convertBytetoImage(u.getFoto());
        if (bitmap != null) {
            perfil.setImageBitmap(bitmap);
        }
        nome.setText(u.getNome());
        email.setText(u.getEmail());

    }

    @Override
    protected void onResume() {
        replaceFragment(new HomeFragment());
        setDados();
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
        }
        super.onBackPressed();
    }

    private void mudaActivity(final Class classe) {
        Log.i("[IFMG]", "passou no muda actyvity" + classe.getName());
        final Intent intent = new Intent(this, classe);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            mudaActivity(PerfilActivity.class);

        } else if (id == R.id.nav_vendas) {
            replaceFragment(new VendasAbertasFragment());

        } else if (id == R.id.nav_produto) {
            replaceFragment(new ProdutoFragment());

        } else if (id == R.id.nav_pesquisa_vendas) {
            replaceFragment(new VendasFragment());

        } else if (id == R.id.nav_devolver) {
            replaceFragment(new DevolverFragment());

        } else if (id == R.id.nav_pesquisa_vendas) {
            replaceFragment(new VendasFragment());

        } else if (id == R.id.nav_gerar_nota) {
            replaceFragment(new GerarNotaFragment());

        } else if (id == R.id.nav_gerar_recibo) {
            replaceFragment(new GerarReciboFragment());

        } else if (id == R.id.nav_caixa_abrir) {
            showAbrirCaixa();
        } else if (id == R.id.nav_caixa_despesas) {
            replaceFragment(new DespesaFragment());
        } else if (id == R.id.nav_os_hoje) {
            replaceFragment(new OSHojeFragment());
        } else if (id == R.id.nav_add_os) {
            replaceFragment(new AdicionarOSFragment());
        } else if (id == R.id.nav_oss) {
            replaceFragment(new OSFragment());
        } else if (id == R.id.nav_sair) {
            BdUsuario bd = new BdUsuario(getApplicationContext());
            bd.deleteAll();
            bd.close();
            mudaActivity(LoginGarcomActivity.class);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment) {
        // getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }

    public void showAbrirCaixa() {
        final LayoutInflater li = getLayoutInflater();
        final View view = li.inflate(R.layout.alert_remover_pedido, null);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        tvMessage.setText("Deseja abrir o caixa ?");
        //definimos para o botão do layout um clickListener
        Button ok = (Button) view.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificaCaixaAberto();
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

    public void showAbrir() {
        final LayoutInflater li = getLayoutInflater();
        final View view = li.inflate(R.layout.alert_abrir_caixa, null);

        final EditText etValor = (EditText) view.findViewById(R.id.etValor);
        Button ok = (Button) view.findViewById(R.id.btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etValor.getText().equals("")) {
                    Toast.makeText(getApplicationContext(), "Insira uma quantidade adequada", Toast.LENGTH_SHORT).show();
                } else if (Float.parseFloat(etValor.getText() + "") <= 0) {
                    Toast.makeText(getApplicationContext(), "Insira uma quantidade adequada", Toast.LENGTH_SHORT).show();
                } else {
                    abrirCaixa(Float.parseFloat(etValor.getText() + ""));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alerta3 = builder.create();
        alerta3.show();
    }

    private void verificaCaixaAberto() {
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(this);
        Empresa sh = bd.listar();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);

        final Call<Void> call = api.isCaixaAberto(sh.getEmpEmail(), sh.getEmpSenha());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println(response.isSuccessful());
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        String sucesso = response.headers().get("sucesso");
                        int cod = Integer.parseInt(sucesso);
                        if (cod > 0) {
                            escondeDialog();
                            showToast("Caixa já aberto!");
                        } else {
                            escondeDialog();
                            showAbrir();
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
                escondeDialog();
            }
        });

    }

    private void abrirCaixa(float valor) {
        Caixa c = new Caixa();
        c.setTrocoIn(valor);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
        c.setIn(sdf.format(hora));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        c.setData(dateFormat.format(date));
        mostraDialog();
        BdEmpresa bd = new BdEmpresa(this);
        Empresa sh1 = bd.listar();
        BdUsuario bd2 = new BdUsuario(this);
        Usuario sh = bd2.listar();
        bd2.close();
        bd.close();
        LojaAPI api = SyncDefaut.RETROFIT_LOJA().create(LojaAPI.class);
        c.setFuncionario(sh.getCodigo());
        final Call<Void> call = api.abrirCaixa(new Gson().toJson(c), sh1.getEmpEmail(), sh1.getEmpSenha());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println(response.isSuccessful());
                if (response.isSuccessful()) {
                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        escondeDialog();
                        showToast(response.headers().get("sucesso"));

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
                escondeDialog();
            }
        });
    }

    private void mostraDialog() {

        final LayoutInflater li = getLayoutInflater();
        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.alert_progress, null);
        TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
        tvDesc.setText("Buscando pedios Pendentes...");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
