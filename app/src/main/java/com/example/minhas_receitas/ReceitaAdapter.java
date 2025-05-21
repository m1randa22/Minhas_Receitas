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

public class ReceitaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Receita> listaReceitas;
    private final OnExcluirClickListener excluirClickListener;
    private final OnEditarClickListener editarClickListener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    public interface OnExcluirClickListener {
        void onExcluirClick(int receitaId);
    }

    public interface OnEditarClickListener {
        void onEditarClick(int receitaId);
    }

    public ReceitaAdapter(Context context, List<Receita> listaReceitas, OnExcluirClickListener excluirClickListener, OnEditarClickListener editarClickListener) {
        this.context = context;
        this.listaReceitas = listaReceitas;
        this.excluirClickListener = excluirClickListener;
        this.editarClickListener = editarClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == listaReceitas.size()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.item_receita, parent, false);
            return new ReceitaViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReceitaViewHolder && position < listaReceitas.size()) {
            Receita receita = listaReceitas.get(position);
            ReceitaViewHolder receitaHolder = (ReceitaViewHolder) holder;

            receitaHolder.textoReceitaTitulo.setText(receita.getTitulo());

            byte[] imagemBytes = receita.getImagem();
            if (imagemBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imagemBytes, 0, imagemBytes.length);
                receitaHolder.imagemReceita.setImageBitmap(bitmap);
            } else {
                receitaHolder.imagemReceita.setImageResource(R.drawable.imagem_quadrado_receita);
            }

            // MODIFICAÇÃO AQUI: Use o método formatarIngredientesEmTopicos
            if (receita.getIngredientes() != null) {
                receitaHolder.textoIngredientes.setText(formatarIngredientesEmTopicos(receita.getIngredientes()));
            }

            if (receita.getModoPreparo() != null) {
                receitaHolder.textoPreparo.setText(receita.getModoPreparo());
            }

            receitaHolder.iconeExcluir.setOnClickListener(v -> {
                if (excluirClickListener != null) {
                    excluirClickListener.onExcluirClick(receita.getId());
                }
            });

            receitaHolder.iconeEditar.setOnClickListener(v -> {
                if (editarClickListener != null) {
                    editarClickListener.onEditarClick(receita.getId());
                }
            });
        }
        // Não precisa fazer nada no rodapé — ele é apenas decorativo
    }

    @Override
    public int getItemCount() {
        // Retorna +1 para o rodapé
        return listaReceitas.size() + 1;
    }

    // NOVO MÉTODO: Adicione este método auxiliar dentro da classe ReceitaAdapter
    private String formatarIngredientesEmTopicos(List<String> ingredientes) {
        if (ingredientes == null || ingredientes.isEmpty()) {
            return "Nenhum ingrediente."; // Ou uma string vazia, se preferir
        }

        StringBuilder builder = new StringBuilder();
        for (String ingrediente : ingredientes) {
            builder.append("* ").append(ingrediente).append(";\n");
        }
        // Remover a última quebra de linha se não for desejada
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1); // Remove o último '\n'
        }
        return builder.toString();
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

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        // Se você quiser que o texto e a imagem do rodapé apareçam sempre,
        // eles já devem estar visíveis por padrão no item_footer.xml
        // Você não precisa de findViewByIds aqui se não for manipular seu conteúdo dinamicamente.
        public FooterViewHolder(View itemView) {
            super(itemView);
            // Se você tivesse TextViews ou ImageViews dentro do item_footer.xml que precisassem ser modificados,
            // você faria o findViewById aqui, por exemplo:
            // TextView textoRodape = itemView.findViewById(R.id.textoRodape);
            // ImageView imagemRodape = itemView.findViewById(R.id.imagemRodape);
        }
    }
}
