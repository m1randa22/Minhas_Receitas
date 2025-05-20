package com.example.minhas_receitas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReceitaAdapter extends RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder> {

    private final Context context;
    private final List<Receita> listaReceitas;
    private final OnExcluirClickListener excluirClickListener;
    private final OnEditarClickListener editarClickListener;

    // Interfaces de Listener
    public interface OnExcluirClickListener {
        void onExcluirClick(int receitaId);
    }

    public interface OnEditarClickListener {
        void onEditarClick(int receitaId);
    }

    // Construtor modificado para receber os listeners
    public ReceitaAdapter(Context context, List<Receita> listaReceitas, OnExcluirClickListener excluirClickListener, OnEditarClickListener editarClickListener) {
        this.context = context;
        this.listaReceitas = listaReceitas;
        this.excluirClickListener = excluirClickListener;
        this.editarClickListener = editarClickListener;
    }

    @NonNull
    @Override
    public ReceitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receita, parent, false);
        return new ReceitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceitaViewHolder holder, int position) {
        Receita receita = listaReceitas.get(position);

        holder.textoReceitaTitulo.setText(receita.getTitulo());

        byte[] imagemBytes = receita.getImagem();
        if (imagemBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagemBytes, 0, imagemBytes.length);
            holder.imagemReceita.setImageBitmap(bitmap);
        } else {
            holder.imagemReceita.setImageResource(R.drawable.imagem_quadrado_receita);
        }


        if (receita.getIngredientes() != null) {
            holder.textoIngredientes.setText(String.join(", ", receita.getIngredientes()));
        }
        if (receita.getModoPreparo() != null) {
            holder.textoPreparo.setText(receita.getModoPreparo());
        }

        // Definir OnClickListeners para os ícones
        holder.iconeExcluir.setOnClickListener(v -> {
            if (excluirClickListener != null) {
                excluirClickListener.onExcluirClick(receita.getId()); // Assumindo que sua classe Receita tem um getId()
            }
        });

        holder.iconeEditar.setOnClickListener(v -> {
            if (editarClickListener != null) {
                editarClickListener.onEditarClick(receita.getId()); // Assumindo que sua classe Receita tem um getId()
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaReceitas.size();
    }

    public static class ReceitaViewHolder extends RecyclerView.ViewHolder {
        TextView textoReceitaTitulo;
        ImageView imagemReceita;
        TextView textoIngredientes;
        TextView textoPreparo;
        ImageView iconeExcluir;
        ImageView iconeEditar;

        public ReceitaViewHolder(View itemView) {
            super(itemView);
            textoReceitaTitulo = itemView.findViewById(R.id.textoReceitaTitulo);
            imagemReceita = itemView.findViewById(R.id.imagemReceita);
            textoIngredientes = itemView.findViewById(R.id.textoIngredientes);
            textoPreparo = itemView.findViewById(R.id.textoPreparo);
            iconeExcluir = itemView.findViewById(R.id.iconeExcluir);
            iconeEditar = itemView.findViewById(R.id.iconeEditar);
        }
    }
}