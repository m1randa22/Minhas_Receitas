package com.example.minhas_receitas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CadastrarActivity extends AppCompatActivity {

    private EditText emailCadastro, senhaCadastro;
    private TextView mensagemErroCadastro;
    private TextView erroSenhaCadastro;
    private TextView erroEmailInvalidoCadastro;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        emailCadastro = findViewById(R.id.emailCadastro);
        senhaCadastro = findViewById(R.id.senhaCadastro);
        mensagemErroCadastro = findViewById(R.id.mensagemErroCadastro);
        erroSenhaCadastro = findViewById(R.id.erroSenhaCadastro);
        erroEmailInvalidoCadastro = findViewById(R.id.erroEmailInvalidoCadastro);
        dbHelper = new DatabaseHelper(this);
    }

    public void cadastrarUsuario(View view) {
        String email = emailCadastro.getText().toString();
        String senha = senhaCadastro.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            mensagemErroCadastro.setText(R.string.erro_campos_vazios);
            mensagemErroCadastro.setVisibility(View.VISIBLE);
            erroSenhaCadastro.setVisibility(View.GONE);
            erroEmailInvalidoCadastro.setVisibility(View.GONE);
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensagemErroCadastro.setVisibility(View.GONE);
            erroSenhaCadastro.setVisibility(View.GONE);
            erroEmailInvalidoCadastro.setText(R.string.erro_email_invalido);
            erroEmailInvalidoCadastro.setVisibility(View.VISIBLE);

        } else {
            erroEmailInvalidoCadastro.setVisibility(View.GONE);
            mensagemErroCadastro.setVisibility(View.GONE);
            if (dbHelper.checkEmail(email)) {
                erroSenhaCadastro.setVisibility(View.GONE);
                mensagemErroCadastro.setText(R.string.erro_email_cadastrado);
                mensagemErroCadastro.setVisibility(View.VISIBLE);
            } else {
                boolean insert = dbHelper.insertUser(email, senha);
                if (insert) {
                    Toast.makeText(CadastrarActivity.this, R.string.cadastro_sucesso, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CadastrarActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Fechar CadastrarActivity e voltar para Login
                } else {
                    mensagemErroCadastro.setText(R.string.erro_cadastro_falha);
                    mensagemErroCadastro.setVisibility(View.VISIBLE);
                }
            }
        }

        // Adicionei uma validação simples para a senha (apenas numérica)
        if (!senha.matches("\\d+")) {
            mensagemErroCadastro.setVisibility(View.GONE);
            erroEmailInvalidoCadastro.setVisibility(View.GONE);
            erroSenhaCadastro.setText(R.string.erro_senha_numerica);
            erroSenhaCadastro.setVisibility(View.VISIBLE);
        } else {
            erroSenhaCadastro.setVisibility(View.GONE);
        }
    }
}