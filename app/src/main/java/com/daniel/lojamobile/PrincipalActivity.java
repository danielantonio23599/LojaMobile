package com.daniel.lojamobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daniel.lojamobile.fragment.CardapioFragment;
import com.daniel.lojamobile.fragment.DevolverFragment;
import com.daniel.lojamobile.fragment.HomeFragment;
import com.daniel.lojamobile.fragment.VendasAbertasFragment;
import com.daniel.lojamobile.fragment.VendasFragment;
import com.daniel.lojamobile.modelo.beans.Usuario;
import com.daniel.lojamobile.modelo.persistencia.BdUsuario;
import com.daniel.lojamobile.util.UtilImageTransmit;
import com.google.android.material.navigation.NavigationView;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView perfil;
    private TextView nome, email;

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
            //replaceFragment(new PerfilFragment());
            mudaActivity(PerfilActivity.class);
        } else if (id == R.id.nav_vendas) {
            replaceFragment(new VendasAbertasFragment());
            //getSupportActionBar().setTitle("Vendas Abertas");


        } else if (id == R.id.nav_pesquisa_vendas) {
            replaceFragment(new VendasFragment());

        } else if (id == R.id.nav_devolver) {
            replaceFragment(new DevolverFragment());


        } else if (id == R.id.nav_pesquisa_vendas) {
            replaceFragment(new VendasFragment());


        } else if (id == R.id.nav_promocao) {
            // replaceFragment(new MesasFragment());


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment) {
        // getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }

}
