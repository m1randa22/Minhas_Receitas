package com.example.minhas_receitas;
// ReceitasActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ReceitasActivity extends AppCompatActivity implements ReceitaAdapter.OnExcluirClickListener, ReceitaAdapter.OnEditarClickListener {

    private RecyclerView listaReceitasRecyclerView;
    private TextView textoNenhumaReceita;
    private ImageView imagemVentoReceita;
    private final List<Receita> listaReceitas = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private String userEmail;
    private ReceitaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_receitas);

        listaReceitasRecyclerView = findViewById(R.id.listaReceitas);
        textoNenhumaReceita = findViewById(R.id.textoNenhumaReceita);
        imagemVentoReceita = findViewById(R.id.imagemVentoReceita);
        dbHelper = new DatabaseHelper(this);

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null) {
            userEmail = "default@example.com";
        }

        listaReceitasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReceitaAdapter(this, listaReceitas, this, this);
        listaReceitasRecyclerView.setAdapter(adapter);

        carregarReceitas();
        atualizarVisibilidadeListaVazia();
    }

    private void carregarReceitas() {
        listaReceitas.clear();
        List<Receita> novasReceitas = dbHelper.getUserRecipes(userEmail);
        listaReceitas.addAll(novasReceitas);
        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Usar notifyDataSetChanged como último recurso
        }
        atualizarVisibilidadeListaVazia();
    }

    private void atualizarVisibilidadeListaVazia() {
        textoNenhumaReceita.setVisibility(listaReceitas.isEmpty() ? View.VISIBLE : View.GONE);
        imagemVentoReceita.setVisibility(listaReceitas.isEmpty() ? View.VISIBLE : View.GONE);
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
            carregarReceitas();
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
        carregarReceitas();
    }
}