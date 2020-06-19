package com.daniel.lojamobile.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.holder.PedidoBEAN;
import com.daniel.lojamobile.modelo.beans.Despesa;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 30/05/2018.
 */

public class AdapterDespesa extends BaseAdapter {
    private List<Despesa> lin = new ArrayList<Despesa>();

    public List<Despesa> getLin() {
        return lin;
    }

    public void setLin(List<Despesa> lin) {
        this.lin = lin;
    }

    private Context context;

    public AdapterDespesa(Context context) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_despesa, parent, false);
        TextView nome = (TextView) view.findViewById(R.id.tvNome);
        TextView valor = (TextView) view.findViewById(R.id.tvValor);
        TextView desc = (TextView) view.findViewById(R.id.tvDescricao);
        nome.setText(lin.get(position).getNome());
        desc.setText(lin.get(position).getDescricao());
        float val = (lin.get(position).getPreco());
        valor.setText("R$ " + val);
        return view;
    }
}
