package com.daniel.lojamobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;


import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.holder.ProdutosGravados;
import com.daniel.lojamobile.adapter.holder.Venda;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 30/05/2018.
 */

public class AdapterPedidoVenda extends BaseAdapter {
    private List<ProdutosGravados> lin = new ArrayList<ProdutosGravados>();

    public List<ProdutosGravados> getLin() {
        return lin;
    }

    public void setLin(List<ProdutosGravados> lin) {
        this.lin = lin;
    }

    private Context context;

    public AdapterPedidoVenda(Context context) {
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

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("[IFMG]", "view: " + lin.get(position).getCodPedidVenda());
        View view = LayoutInflater.from(context).inflate(R.layout.pedido_adapter, parent, false);
        TextView codigo = (TextView) view.findViewById(R.id.tvCodigo);
        TextView nome = (TextView) view.findViewById(R.id.tvItemPedido);
        TextView qtd = (TextView) view.findViewById(R.id.tvQTD);
        TextView valor = (TextView) view.findViewById(R.id.tvValor);
        codigo.setText(lin.get(position).getCodProduto() + "");
        nome.setText(lin.get(position).getNome() + "");
        qtd.setText(lin.get(position).getQuantidade() + "");
        valor.setText(lin.get(position).getValorFinal() + "");
        return view;
    }
}
