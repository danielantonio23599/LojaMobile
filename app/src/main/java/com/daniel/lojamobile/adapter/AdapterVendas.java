package com.daniel.lojamobile.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.adapter.interfaces.CustomItemClickListener;
import com.daniel.lojamobile.adapter.interfaces.VendaItemClickListener;
import com.daniel.lojamobile.fragment.ClientesFragment;
import com.daniel.lojamobile.modelo.beans.Produto;
import com.daniel.lojamobile.util.UtilImageTransmit;

import java.util.ArrayList;

public class AdapterVendas extends RecyclerView.Adapter<AdapterVendas.MyViewHolder> implements Filterable {

    private ArrayList<Venda> vendas;
    private ArrayList<Venda> filteredVendas;
    private FragmentActivity context;
    private VendaItemClickListener customItemClickListener;

    public AdapterVendas(FragmentActivity context, ArrayList<Venda> userArrayList, VendaItemClickListener customItemClickListener) {
        this.context = context;
        this.vendas = userArrayList;
        this.filteredVendas = userArrayList;
        this.customItemClickListener = customItemClickListener;
    }

    @Override
    public AdapterVendas.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_venda, viewGroup, false);
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
    public void onBindViewHolder(final AdapterVendas.MyViewHolder viewHolder, final int position) {
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, viewHolder.menu);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_venda_item);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_cliente:
                                ClientesFragment c = new ClientesFragment();
                                c.setVenda(vendas.get(position).getCodigo());
                                replaceFragment(c);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

        Bitmap bitmap = UtilImageTransmit.convertBytetoImage(filteredVendas.get(position).getCliFoto());
        Bitmap bitmap2 = UtilImageTransmit.convertBytetoImage(filteredVendas.get(position).getProFoto());
        if (bitmap != null) {
            viewHolder.foto.setImageBitmap(bitmap);
        } else if (bitmap2 != null) {
            viewHolder.foto.setImageBitmap(bitmap2);
        }
        if (filteredVendas.get(position).getCliente() != null)
            viewHolder.cliente.setText(filteredVendas.get(position).getCliente());
        viewHolder.status.setText(filteredVendas.get(position).getStatus());
        if(filteredVendas.get(position).getStatus().equals("aberta")){
            viewHolder.valor.setText("R$ " + filteredVendas.get(position).getValor());
        }else{
            viewHolder.valor.setText("R$ " + filteredVendas.get(position).getValorFinal());
        }

        viewHolder.venda.setText(filteredVendas.get(position).getCodigo() + "");
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

                    ArrayList<Venda> tempFilteredList = new ArrayList<>();

                    for (Venda user : vendas) {
                        // search for user name
                        if (user.getCliente().toLowerCase().contains(searchString)) {

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
                filteredVendas = (ArrayList<Venda>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView cliente;
        private TextView menu;
        private TextView status;
        private TextView valor;
        private TextView venda;
        private ImageView foto;

        public MyViewHolder(View view) {
            super(view);
            menu = (TextView) view.findViewById(R.id.tvMenu);
            cliente = (TextView) view.findViewById(R.id.tvCliente);
            status = (TextView) view.findViewById(R.id.tvStatus);
            valor = (TextView) view.findViewById(R.id.tvPreco);
            venda = (TextView) view.findViewById(R.id.tvVenda);
            foto = (ImageView) view.findViewById(R.id.ivFoto);

        }
    }
    public void replaceFragment(Fragment fragment) {
        // getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        context.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "IFMG").addToBackStack(null).commit();
    }

}
