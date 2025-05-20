package com.example.minhas_receitas;

// LoginActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin, senhaLogin;
    private TextView mensagemErroLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emailLogin);
        senhaLogin = findViewById(R.id.senhaLogin);
        mensagemErroLogin = findViewById(R.id.erroLogin);
        dbHelper = new DatabaseHelper(this);
    }

    public void login(View view) {
        String email = emailLogin.getText().toString();
        String senha = senhaLogin.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            mensagemErroLogin.setText(R.string.erro_campos_vazios);
            mensagemErroLogin.setVisibility(View.VISIBLE);
        } else {
            if (dbHelper.checkEmail(email)) { // Primeiro, verifica se o email existe
                if (dbHelper.checkUser(email, senha)) {
                    // Login bem-sucedido
                    Intent intent = new Intent(LoginActivity.this, ReceitasActivity.class);
                    intent.putExtra("USER_EMAIL", email);
                    startActivity(intent);
                    finish();
                } else {
                    mensagemErroLogin.setText(R.string.erro_login_falha); // Senha incorreta
                    mensagemErroLogin.setVisibility(View.VISIBLE);
                }
            } else {
                mensagemErroLogin.setText(R.string.erro_login_conta_nao_existe); // Email não encontrado
                mensagemErroLogin.setVisibility(View.VISIBLE);
            }
        }
    }

    public void abrirTelaCadastrar(View view) {
        Intent intent = new Intent(LoginActivity.this, CadastrarActivity.class);
        startActivity(intent);
    }

    public void abrirTelaNovaSenha(View view) {
        Intent intent = new Intent(LoginActivity.this, NovaSenhaActivity.class);
        startActivity(intent);
    }
}