package com.example.minhas_receitas;

import android.Manifest; // Importar
import android.content.Intent;
import android.content.pm.PackageManager; // Importar
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build; // Importar
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CriarReceitaActivity extends AppCompatActivity {

    private EditText nomeReceita, modoPreparoReceita;
    private ImageView imagemReceita;
    private LinearLayout layoutIngredientes;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private int receitaId = -1; // -1 indica nova receita

    // Launcher para selecionar imagem da galeria
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    // Launcher para solicitar permissões
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas); // Certifique-se que este é o layout correto para CriarReceita

        nomeReceita = findViewById(R.id.nomeReceita);
        imagemReceita = findViewById(R.id.imagemReceita);
        layoutIngredientes = findViewById(R.id.layoutIngredientes);
        modoPreparoReceita = findViewById(R.id.modoPreparoReceita);
        dbHelper = new DatabaseHelper(this);

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        receitaId = getIntent().getIntExtra("RECEITA_ID", -1);

        // **1. Inicialização do imagePickerLauncher**
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            // Setar a imagem na ImageView
                            imagemReceita.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Log.e("CriarReceitaActivity", "Erro ao carregar imagem: " + e.getMessage(), e);
                            Toast.makeText(this, "Falha ao carregar a imagem.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("CriarReceitaActivity", "Seleção de imagem cancelada ou falha.");
                    }
                }
        );

        // **2. Inicialização do requestPermissionLauncher (para permissões de leitura de mídia)**
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean allPermissionsGranted = true;
                    for (Boolean granted : permissions.values()) {
                        if (!granted) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }

                    if (allPermissionsGranted) {
                        Log.d("CriarReceitaActivity", "Permissões de mídia concedidas.");
                        openImagePicker(); // Chamar o seletor de imagens após conceder a permissão
                    } else {
                        Log.w("CriarReceitaActivity", "Permissões de mídia negadas.");
                        Toast.makeText(this, "Permissão para acessar a galeria é necessária para carregar imagens.", Toast.LENGTH_LONG).show();
                    }
                }
        );

        adicionarIngrediente(null); // Adiciona um campo de ingrediente inicial

        // Imagem padrão quando não há imagem carregada
        imagemReceita.setImageResource(R.drawable.imagem_quadrado_receita);

        if (receitaId != -1) {
            carregarReceitaParaEdicao();
        }
    }

    public void adicionarIngrediente(View view) {
        EditText novoIngrediente = new EditText(this);
        novoIngrediente.setHint("Ingrediente");
        novoIngrediente.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutIngredientes.addView(novoIngrediente);
    }

    public void carregarImagem(View view) {
        // Lógica para solicitar permissões antes de abrir o seletor de imagens
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34)
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_IMAGES});
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33)
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_IMAGES});
            }
        } else { // Android 12 (API 32) e abaixo
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        }
    }

    // Método que abre o seletor de imagens da galeria
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            imagePickerLauncher.launch(intent);
        } else {
            Toast.makeText(this, "Nenhum aplicativo para selecionar imagens encontrado.", Toast.LENGTH_SHORT).show();
            Log.e("CriarReceitaActivity", "Nenhum aplicativo para selecionar imagens encontrado.");
        }
    }


    private void carregarReceitaParaEdicao() {
        Receita receita = dbHelper.getReceita(receitaId);
        if (receita != null) {
            nomeReceita.setText(receita.getTitulo());
            modoPreparoReceita.setText(receita.getModoPreparo());

            byte[] imageBytes = receita.getImagem();
            if (imageBytes != null && imageBytes.length > 0) {
                try {
                    // Carrega o bitmap a partir dos bytes. Você pode otimizar aqui também se a imagem estiver muito grande.
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (bitmap != null) {
                        imagemReceita.setImageBitmap(bitmap);
                    } else {
                        Log.e("CriarReceitaActivity", "Bitmap decodificado é nulo ao carregar receita para edição.");
                        imagemReceita.setImageResource(R.drawable.imagem_quadrado_receita);
                    }
                } catch (Exception e) {
                    Log.e("CriarReceitaActivity", "Erro ao decodificar imagem para edição: " + e.getMessage(), e);
                    imagemReceita.setImageResource(R.drawable.imagem_quadrado_receita);
                }
            } else {
                imagemReceita.setImageResource(R.drawable.imagem_quadrado_receita);
            }

            List<String> ingredientes = receita.getIngredientes();
            if (ingredientes != null && !ingredientes.isEmpty()) {
                layoutIngredientes.removeAllViews(); // Limpa os campos existentes
                for (String ingrediente : ingredientes) {
                    EditText ingredienteEditText = new EditText(this);
                    ingredienteEditText.setText(ingrediente);
                    ingredienteEditText.setHint("Ingrediente");
                    ingredienteEditText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    layoutIngredientes.addView(ingredienteEditText);
                }
            }
        } else {
            Toast.makeText(this, "Receita não encontrada.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void salvarReceita(View view) {
        String titulo = nomeReceita.getText().toString().trim();
        String modoPreparo = modoPreparoReceita.getText().toString().trim();
        byte[] imagemBytes = null;

        // **Lógica para converter imagem para bytes e otimizar**
        if (imagemReceita.getDrawable() != null && (imagemReceita.getDrawable() instanceof BitmapDrawable)) {
            Bitmap bitmap = ((BitmapDrawable) imagemReceita.getDrawable()).getBitmap();
            if (bitmap != null) {
                // Otimização da imagem: redimensionar e comprimir
                Bitmap resizedBitmap = getResizedBitmap(bitmap, 800); // Max 800 pixels na maior dimensão
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // Compressão para JPEG com qualidade 80 (ajuste conforme necessário)
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                imagemBytes = bos.toByteArray();
                Log.d("CriarReceitaActivity", "Tamanho da imagem após compressão: " + (imagemBytes != null ? imagemBytes.length : 0) + " bytes");
            } else {
                Log.d("CriarReceitaActivity", "Bitmap obtido do drawable é nulo.");
            }
        } else {
            Log.d("CriarReceitaActivity", "Drawable da ImageView é nulo ou não é um BitmapDrawable.");
        }

        List<String> ingredientes = new ArrayList<>();
        for (int i = 0; i < layoutIngredientes.getChildCount(); i++) {
            View childView = layoutIngredientes.getChildAt(i);
            if (childView instanceof EditText) {
                String ingrediente = ((EditText) childView).getText().toString().trim();
                if (!ingrediente.isEmpty()) {
                    ingredientes.add(ingrediente);
                }
            }
        }

        if (titulo.isEmpty() || modoPreparo.isEmpty() || ingredientes.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha Título, Ingredientes e Modo de Preparo.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean sucesso;
        if (receitaId == -1) {
            // Inserir nova receita
            sucesso = dbHelper.insertReceita(userEmail, titulo, imagemBytes, ingredientes, modoPreparo);
        } else {
            // Atualizar receita existente
            sucesso = dbHelper.updateReceita(receitaId, titulo, imagemBytes, ingredientes, modoPreparo);
        }

        if (sucesso) {
            Toast.makeText(this, "Receita salva com sucesso!", Toast.LENGTH_SHORT).show();
            finish(); // Volta para a tela anterior (ReceitasActivity)
        } else {
            Toast.makeText(this, "Erro ao salvar receita.", Toast.LENGTH_SHORT).show();
        }
    }

    // Método auxiliar para redimensionar o bitmap
    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= maxSize && height <= maxSize) {
            return image; // Não precisa redimensionar se já for pequena o suficiente
        }

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) { // Largura maior que altura
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else { // Altura maior ou igual à largura
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}