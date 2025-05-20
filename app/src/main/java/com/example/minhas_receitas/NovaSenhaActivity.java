package com.example.minhas_receitas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NovaSenhaActivity extends AppCompatActivity {

    private EditText emailNovaSenhaEditText, senhaNovaSenhaEditText;
    private TextView mensagemErroNovaSenhaTextView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novasenha);

        emailNovaSenhaEditText = findViewById(R.id.emailNovaSenha);
        senhaNovaSenhaEditText = findViewById(R.id.senhaNovaSenha);
        mensagemErroNovaSenhaTextView = findViewById(R.id.erroEmailNovaSenha); // Corrigido o ID para corresponder ao XML
        dbHelper = new DatabaseHelper(this);
    }

    public void criarNovaSenha(View view) {
        String email = emailNovaSenhaEditText.getText().toString();
        String senha = senhaNovaSenhaEditText.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            mensagemErroNovaSenhaTextView.setText(R.string.erro_campos_vazios); // Usando string resource
            mensagemErroNovaSenhaTextView.setVisibility(View.VISIBLE);
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensagemErroNovaSenhaTextView.setText(R.string.erro_email_invalido); // Usando string resource
            mensagemErroNovaSenhaTextView.setVisibility(View.VISIBLE);
        } else {
            if (dbHelper.checkEmail(email)) {
                boolean update = dbHelper.updatePassword(email, senha);
                if (update) {
                    Toast.makeText(NovaSenhaActivity.this, R.string.senha_atualizada_sucesso, Toast.LENGTH_SHORT).show(); // Usando string resource
                    Intent intent = new Intent(NovaSenhaActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    mensagemErroNovaSenhaTextView.setText(R.string.erro_atualizar_senha); // Usando string resource
                    mensagemErroNovaSenhaTextView.setVisibility(View.VISIBLE);
                }
            } else {
                mensagemErroNovaSenhaTextView.setText(R.string.erro_email_nao_cadastrado); // Usando string resource
                mensagemErroNovaSenhaTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}