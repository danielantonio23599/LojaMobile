package com.daniel.lojamobile.adapter;

import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.adapter.interfaces.OsItemClickListener;
import com.daniel.lojamobile.adapter.interfaces.VendaItemClickListener;
import com.daniel.lojamobile.fragment.ClientesFragment;
import com.daniel.lojamobile.fragment.DetalhesVendaFragment;
import com.daniel.lojamobile.modelo.beans.OrdemServico;
import com.daniel.lojamobile.util.Data;
import com.daniel.lojamobile.util.Time;
import com.daniel.lojamobile.util.UtilImageTransmit;

import java.util.ArrayList;

public class AdapterOS extends RecyclerView.Adapter<AdapterOS.MyViewHolder> implements Filterable {

    private ArrayList<OrdemServico> vendas;
    private ArrayList<OrdemServico> filteredVendas;
    private FragmentActivity context;
    private OsItemClickListener customItemClickListener;

    public AdapterOS(FragmentActivity context, ArrayList<OrdemServico> userArrayList, OsItemClickListener customItemClickListener) {
        this.context = context;
        this.vendas = userArrayList;
        this.filteredVendas = userArrayList;
        this.customItemClickListener = customItemClickListener;
    }

    @Override
    public AdapterOS.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_os, viewGroup, false);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final AdapterOS.MyViewHolder viewHolder, final int position) {
        if (filteredVendas.size() > 0) {
            viewHolder.hora.setText(Time.formataHora(filteredVendas.get(position).getHora()));
            viewHolder.data.setText(Data.formataDataBROS(filteredVendas.get(position).getData()));
        }
    }

    @Override
    public int getItemCount() {
        return vendas.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.isEmpty()) {

                    filteredVendas = vendas;

                } else {

                    ArrayList<OrdemServico> tempFilteredList = new ArrayList<>();

                    for (OrdemServico user : vendas) {
                        // search for user name
                        if (user.getNome().toLowerCase().contains(searchString)) {

                            tempFilteredList.add(user);
                        } else if ((user.getCodigo() + "").toLowerCase().contains(searchString)) {
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
                filteredVendas = (ArrayList<OrdemServico>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView hora;
        private TextView data;


        public MyViewHolder(View view) {
            super(view);
            data = (TextView) view.findViewById(R.id.tvData);
            hora = (TextView) view.findViewById(R.id.tvHora);

        }
    }

    public void replaceFragment(Fragment fragment) {
        // getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        context.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }

}
