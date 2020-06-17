package com.daniel.lojamobile.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.modelo.beans.Endereco;

import java.io.IOException;
import java.util.List;


/**
 * Created by Daniel on 29/05/2018.
 */

public class Localizacao implements LocationListener {

    protected static final String TAG = null;
    private AlertDialog alert2;
    private Context context;
    private Activity activity;
    private LocationManager lm;
    private Location location;
    private volatile boolean stop = false;
    private static final int UM_SEGUNDO = 1000;
    private int tempoTotalBusca = 10;
    private TextView tvDesc;
    protected ProgressDialog progressDialog;
    private Endereco endereco;
    private List<Address> enderecos = null;

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Localizacao(Context context, Activity activity) {
        lm = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
        this.activity = activity;
    }

    public boolean estado() {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public Location capturarCoordenadaGPS(final TextView text) {

        try {
            new Thread(new Runnable() {
                public void run() {
                    Looper.myLooper();
                    Looper.prepare();
                    text.setText("Aguarde...");
                    //progressDialog = ProgressDialog.show(context, null, "Aguarde...", true);
                    ativaGPS();
                    Looper.loop();
                }
            }).start();
            // Thread.sleep(10*1000);

            int tempoBusca = 0;

            while (!stop) {
                if (tempoTotalBusca <= tempoBusca) {
                    break;
                }

                Thread.sleep(UM_SEGUNDO);
                tempoBusca++;
            }
            return location;
        } catch (Exception e) {
            // TODO - Trate a exceção;
        } finally {
            desativaGPS();
            if (alert2 != null)
                alert2.dismiss();
        }
        return null;
    }

    private void ativaGPS() {
        try {

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        } catch (SecurityException s) {
            Log.i("IFMG", "Erro ao inicializar a busca pelo gps");
        }
        // lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this, Looper.myLooper());
        // Looper.loop();
    }

    private void desativaGPS() {
        lm.removeUpdates(Localizacao.this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location != null) {
            stop = true;
            Log.i("IFMG", "sucesso");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        alert2.dismiss();
        tempoTotalBusca = 0;
        Log.i("IFMG", "erro gps");
        //Util.displayPromptForEnablingGPS(activity);
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Provider habilitado
        Log.i("IFMG", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Status do provider alterado
        Log.i("IFMG", "charget");
    }

    public void showAletProgress(final String descricao) {
        final LayoutInflater li = activity.getLayoutInflater();
        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.alert_progress, null);
        tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
        tvDesc.setText(descricao);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Aguarde...");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stop = true;
                alert2.dismiss();
            }
        });
        alert2 = builder.create();
        alert2.show();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

/*    public Endereco buscaEndereco(final TextView text) {

        final AsyncTask<Void, Integer, String> asyncTask = new AsyncTask<Void, Integer, String>() {
            private Location location;
            private LatLng latLng;
            private List<Address> enderecos = null;

            @Override
            protected void onPostExecute(String resultado) {
                Log.i("IFMG", "resultado = " + resultado);
                if (resultado.equals("sim")) {
                    text.setText(enderecos.get(0).getAddressLine(0));
                    Endereco end = new Endereco();
                    end.setLogradouro(enderecos.get(0).getAddressLine(0));
                    end.setCidade(enderecos.get(0).getLocality());
                    end.setUf(enderecos.get(0).getAdminArea());
                    end.setCep(enderecos.get(0).getPostalCode());
                    end.setNumero(enderecos.get(0).getPhone());
                    end.setBairro(enderecos.get(0).getFeatureName());
                    end.setLongitude((float) enderecos.get(0).getLatitude());
                    end.setLongitude((float) enderecos.get(0).getLongitude());
                    setEndereco(end);

                }

            }

            @Override
            protected void onPreExecute() {
                location = capturarCoordenadaGPS(text);
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {

            }

            @Override
            protected String doInBackground(Void... params) {
                //metodo do geocuder
                Geocoder geocoder = new Geocoder(context);
                try {
                    enderecos = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.getMessage();
                }
                if (enderecos != null) {
                    if (enderecos.size() > 0) {
                        return "sim";

                    } else {
                        return null;
                    }
                }
                return null;
            }

        };
        asyncTask.execute();
        return getEndereco();
    }*/

    public Endereco buscaEndereco(final TextView text) {
        Endereco end = new Endereco();
        capturarCoordenadaGPS(text);
        Geocoder geocoder = new Geocoder(context);
        if(location!= null){
        try {
            enderecos = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.getMessage();
        }
        if (enderecos != null) {
            if (enderecos.size() > 0) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(enderecos.get(0).getAddressLine(0));
                    }
                });
                end.setLogradouro(enderecos.get(0).getThoroughfare());
                end.setLocalidade(enderecos.get(0).getSubAdminArea());
                end.setUf(enderecos.get(0).getAdminArea());
                end.setCep(enderecos.get(0).getPostalCode());
                end.setNumero(enderecos.get(0).getSubThoroughfare());
                end.setBairro(enderecos.get(0).getSubLocality());
                end.setLongitude((float) enderecos.get(0).getLatitude());
                end.setLongitude((float) enderecos.get(0).getLongitude());
            }
        }
        return end;
        }else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.setText("não encontrado");
                }
            });
        }
        return null;
    }
}
