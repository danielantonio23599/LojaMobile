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
import com.daniel.lojamobile.adapter.holder.Venda;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 30/05/2018.
 */

public class AdapterMesa extends BaseAdapter {
    private List<Venda> lin = new ArrayList<Venda>();

    public List<Venda> getLin() {
        return lin;
    }

    public void setLin(List<Venda> lin) {
        this.lin = lin;
    }

    private Context context;

    public AdapterMesa(Context context) {
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
        Log.i("[IFMG]", "view: " + lin.get(position).getCodigo());
        View view = LayoutInflater.from(context).inflate(R.layout.mesa_adapter, parent, false);
        TextView mesa = (TextView) view.findViewById(R.id.tvMesaNum);
        CardView back = (CardView) view.findViewById(R.id.back);
        mesa.setText(lin.get(position).getCodigo() + "");
        Log.i("[IFMG]", "status : " + lin.get(position).getStatus());
        switch (lin.get(position).getStatus()) {
            case "aberta":
                Log.i("[IFMG]", " Aberta ");
                back.setCardBackgroundColor(0xFF07935B);
                break;
            case "Fechada":
                back.setCardBackgroundColor(Color.RED);
                break;
            case "pendente":
                back.setBackgroundColor(Color.YELLOW);
                break;
            default:
                back.setCardBackgroundColor(0xFF07935B);
                break;
        }
        return view;
    }
}
