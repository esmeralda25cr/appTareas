package com.senati.apptareas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String[] USUARIOS_VALIDOS = {"mileydi", "carlos", "esmeralda"};
    private static final String PASSWORD_VALIDA = "1234";

    private EditText edtUsuario, edtPassword;
    private TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsuario = findViewById(R.id.edtUsuario);
        edtPassword = findViewById(R.id.edtPassword);
        txtError = findViewById(R.id.txtError);
        Button btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> intentarIngresar());
    }

    private void intentarIngresar() {
        String usuario = edtUsuario.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (esUsuarioValido(usuario) && password.equals(PASSWORD_VALIDA)) {
            txtError.setVisibility(android.view.View.GONE);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("usuario", capitalizar(usuario));
            startActivity(intent);
            finish();
        } else {
            txtError.setVisibility(android.view.View.VISIBLE);
        }
    }

    private boolean esUsuarioValido(String usuario) {
        String usuarioMin = usuario.toLowerCase();
        for (String u : USUARIOS_VALIDOS) {
            if (u.equals(usuarioMin)) return true;
        }
        return false;
    }

    private String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1).toLowerCase();
    }
}
