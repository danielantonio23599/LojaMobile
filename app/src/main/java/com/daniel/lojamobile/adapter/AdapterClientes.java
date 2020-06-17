package com.daniel.lojamobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.daniel.lojamobile.R;
import com.daniel.lojamobile.adapter.holder.Cliente;

import com.daniel.lojamobile.adapter.interfaces.ClienteItemClickListener;

import com.daniel.lojamobile.util.UtilImageTransmit;

import java.util.ArrayList;

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.MyViewHolder> implements Filterable {

    private ArrayList<Cliente> clientes;
    private ArrayList<Cliente> filteredClientes;
    private Context context;
    private ClienteItemClickListener customItemClickListener;

    public AdapterClientes(Context context, ArrayList<Cliente> userArrayList, ClienteItemClickListener customItemClickListener) {
        this.context = context;
        this.clientes = userArrayList;
        this.filteredClientes = userArrayList;
        this.customItemClickListener = customItemClickListener;
    }

    @Override
    public AdapterClientes.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_cliente, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for click item listener
                //Toast.makeText(context,"click " + myViewHolder.getAdapterPosition(),Toast.LENGTH_SHORT);
                customItemClickListener.onItemClick(filteredClientes.get(myViewHolder.getAdapterPosition()), myViewHolder.getAdapterPosition());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterClientes.MyViewHolder viewHolder, int position) {

        Bitmap bitmap = UtilImageTransmit.convertBytetoImage(filteredClientes.get(position).getFoto());
        if (bitmap != null) {
            viewHolder.foto.setImageBitmap(bitmap);
        }
        viewHolder.cliente.setText(filteredClientes.get(position).getNome());

        viewHolder.profissao.setText(filteredClientes.get(position).getProfissao() + "");
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.isEmpty()) {

                    filteredClientes = clientes;

                } else {

                    ArrayList<Cliente> tempFilteredList = new ArrayList<>();

                    for (Cliente user : clientes) {
                        // search for user name
                        if (user.getNome().toLowerCase().contains(searchString)) {

                            tempFilteredList.add(user);
                        } else if ((user.getCodigo() + "").toLowerCase().contains(searchString)) {
                            tempFilteredList.add(user);
                        }
                    }

                    filteredClientes = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredClientes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredClientes = (ArrayList<Cliente>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView cliente;
        private TextView profissao;
        private ImageView foto;

        public MyViewHolder(View view) {
            super(view);
            cliente = (TextView) view.findViewById(R.id.tvCliente);
            profissao = (TextView) view.findViewById(R.id.tvProfissao);
            foto = (ImageView) view.findViewById(R.id.ivFoto);

        }
    }
}
