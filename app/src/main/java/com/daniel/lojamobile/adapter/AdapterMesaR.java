package com.daniel.lojamobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.adapter.interfaces.VendaItemClickListener;

import java.util.ArrayList;

public class AdapterMesaR extends RecyclerView.Adapter<AdapterMesaR.MyViewHolder> implements Filterable {

    private ArrayList<Venda> Vendas;
    private ArrayList<Venda> filteredVendas;
    private Context context;
    private VendaItemClickListener customItemClickListener;

    public AdapterMesaR(Context context, ArrayList<Venda> userArrayList, VendaItemClickListener customItemClickListener) {
        this.context = context;
        this.Vendas = userArrayList;
        this.filteredVendas = userArrayList;
        this.customItemClickListener = customItemClickListener;
    }

    @Override
    public AdapterMesaR.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mesa_adapter, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for click item listener
                //Toast.makeText(context,"click " + myViewHolder.getAdapterPosition(),Toast.LENGTH_SHORT);
                customItemClickListener.onItemClick(filteredVendas.get(myViewHolder.getAdapterPosition()), myViewHolder.getAdapterPosition());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterMesaR.MyViewHolder viewHolder, int position) {
        Log.i("[IFMG]", " mesa : " + filteredVendas.get(position).getCodigo() + "");
        viewHolder.venda.setText(filteredVendas.get(position).getCodigo() + "");
        switch (filteredVendas.get(position).getStatus()) {
            case "aberta":
                Log.i("[IFMG]", " aberta ");
                viewHolder.back.setCardBackgroundColor(0xFF07935B);
                break;
            case "fechada":
                viewHolder.back.setCardBackgroundColor(Color.RED);
                break;
            case "pendente":
                viewHolder.back.setBackgroundColor(Color.YELLOW);
                break;
            default:
                viewHolder.back.setCardBackgroundColor(0xFF07935B);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return filteredVendas.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.isEmpty()) {

                    filteredVendas = Vendas;

                } else {

                    ArrayList<Venda> tempFilteredList = new ArrayList<>();

                    for (Venda user : Vendas) {
                        // search for user name
                        if ((user.getCodigo() + "").toLowerCase().contains(searchString)) {
                            tempFilteredList.add(user);
                        }
                    }

                    filteredVendas = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredVendas;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredVendas = (ArrayList<Venda>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView venda;
        private CardView back;

        public MyViewHolder(View view) {
            super(view);
            venda = (TextView) view.findViewById(R.id.tvMesaNum);
            back = (CardView) view.findViewById(R.id.back);


        }
    }
}
