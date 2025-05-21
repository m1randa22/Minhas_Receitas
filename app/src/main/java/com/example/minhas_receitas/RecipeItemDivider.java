package com.example.minhas_receitas; // Ajuste o pacote se necessário

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeItemDivider extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;
    private final int mItemCount; // Para saber o total de itens (receitas + rodapé)

    public RecipeItemDivider(Context context, int itemCount) {
        // Obtenha o drawable da linha que criamos
        mDivider = ContextCompat.getDrawable(context, R.drawable.divider_line);
        this.mItemCount = itemCount;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // A lógica é desenhar a linha abaixo de cada item, exceto o último item de receita
        // e se houver apenas o rodapé (nenhuma receita) não desenhar nada.

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        // Itera sobre todos os itens visíveis no RecyclerView
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childAdapterPosition = parent.getChildAdapterPosition(child);

            // Desenha o divisor apenas se não for o último item de receita
            // e se não houver nenhuma receita (lista vazia + rodapé)
            // Lembre-se que o Adapter tem listaReceitas.size() + 1
            if (childAdapterPosition < mItemCount - 2 && mItemCount > 1) { // -2 para ignorar o rodapé e o último item de receita
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}