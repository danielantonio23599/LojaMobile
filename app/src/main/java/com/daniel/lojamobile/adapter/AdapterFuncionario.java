package com.daniel.lojamobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.holder.PedidoBEAN;
import com.daniel.lojamobile.modelo.beans.Funcionario;
import com.daniel.lojamobile.util.UtilImageTransmit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 30/05/2018.
 */

public class AdapterFuncionario extends BaseAdapter {
    private List<Funcionario> lin = new ArrayList<Funcionario>();

    public List<Funcionario> getLin() {
        return lin;
    }

    public void setLin(List<Funcionario> lin) {
        this.lin = lin;
    }

    private Context context;

    public AdapterFuncionario(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lin.size();
    }

    @Override
    public Object getItem(int position) {
        return lin.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("[IFMG]", "view: " + lin.get(position).getNome());
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_cliente, parent, false);
        TextView nome = (TextView) view.findViewById(R.id.tvCliente);
        TextView telefo = (TextView) view.findViewById(R.id.tvProfissao);
        ImageView foto = (ImageView) view.findViewById(R.id.ivFoto);
        nome.setText(lin.get(position).getNome());
        telefo.setText(lin.get(position).getTelefone() + "");
        Bitmap bitmap = UtilImageTransmit.convertBytetoImage(lin.get(position).getFoto());
        if (bitmap != null) {
            foto.setImageBitmap(bitmap);
        }
        return view;
    }
}
