package com.example.minhas_receitas;

// ReceitasActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Importe as classes necessárias para o divisor
import androidx.core.content.ContextCompat;
import android.graphics.drawable.Drawable; // Certifique-se de que este import está presente
import android.graphics.Canvas; // Certifique-se de que este import está presente


public class ReceitasActivity extends AppCompatActivity implements ReceitaAdapter.OnExcluirClickListener, ReceitaAdapter.OnEditarClickListener {

    private RecyclerView listaReceitasRecyclerView;
    private final List<Receita> listaReceitas = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private String userEmail;
    private ReceitaAdapter adapter;

    // Adicione uma referência para o ItemDecoration para que possamos removê-lo e adicioná-lo novamente
    private RecipeItemDivider recipeItemDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_receitas);

        listaReceitasRecyclerView = findViewById(R.id.listaReceitas);
        dbHelper = new DatabaseHelper(this);

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            userEmail = "default@example.com";
        }

        listaReceitasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReceitaAdapter(this, listaReceitas, this, this);
        listaReceitasRecyclerView.setAdapter(adapter);

        // Inicialize o RecipeItemDivider aqui.
        // Ele será adicionado/removido em carregarReceitas() para lidar com a lógica de não mostrar linhas vazias.
        // A contagem inicial de itens será 0 (apenas o rodapé) ou o tamanho da lista carregada.
        recipeItemDivider = new RecipeItemDivider(this, adapter.getItemCount());
        listaReceitasRecyclerView.addItemDecoration(recipeItemDivider); // Adiciona inicialmente

        carregarReceitas(); // Isso irá recalcular e atualizar o divisor
    }

    private void carregarReceitas() {
        listaReceitas.clear();
        List<Receita> novasReceitas = dbHelper.getUserRecipes(userEmail);
        listaReceitas.addAll(novasReceitas);

        // ATENÇÃO: Se você atualiza a lista de receitas, precisa informar ao divisor
        // sobre a nova contagem de itens.
        // A forma mais robusta é remover e adicionar o ItemDecoration novamente.
        // Você pode otimizar isso no futuro para apenas atualizar o ItemDecoration,
        // mas essa abordagem garante que ele seja redesenhado corretamente.
        if (recipeItemDivider != null) {
            listaReceitasRecyclerView.removeItemDecoration(recipeItemDivider);
        }
        recipeItemDivider = new RecipeItemDivider(this, adapter.getItemCount());
        listaReceitasRecyclerView.addItemDecoration(recipeItemDivider);


        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void abrirTelaCriarReceita(View view) {
        Intent intent = new Intent(ReceitasActivity.this, CriarReceitaActivity.class);
        intent.putExtra("USER_EMAIL", userEmail);
        startActivity(intent);
    }

    @Override
    public void onExcluirClick(int receitaId) {
        excluirReceita(receitaId);
    }

    @Override
    public void onEditarClick(int receitaId) {
        editarReceita(receitaId);
    }

    public void excluirReceita(int receitaId) {
        if (dbHelper.deleteReceita(receitaId)) {
            Toast.makeText(this, "Receita excluída com sucesso!", Toast.LENGTH_SHORT).show();
            carregarReceitas(); // Recarrega as receitas e atualiza o divisor
        } else {
            Toast.makeText(this, "Erro ao excluir receita.", Toast.LENGTH_SHORT).show();
        }
    }

    public void editarReceita(int receitaId) {
        Intent intent = new Intent(this, CriarReceitaActivity.class);
        intent.putExtra("RECEITA_ID", receitaId);
        intent.putExtra("USER_EMAIL", userEmail);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // É crucial recarregar receitas aqui para refletir mudanças feitas em CriarReceitaActivity
        carregarReceitas();
    }
}