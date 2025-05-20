package com.example.minhas_receitas;

// MainActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Verificar se o usuário já está logado (usar SharedPreferences ou similar)
        //  Se estiver logado, ir para ReceitasActivity
        //  Caso contrário, ir para LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); //  Fechar MainActivity para que o usuário não volte para ela ao pressionar "Voltar"
    }
}