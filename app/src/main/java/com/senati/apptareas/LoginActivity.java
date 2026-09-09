package com.senati.apptareas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/*
 * Esta es la pantalla de inicio de sesión.
 * No usa una base de datos de usuarios real, solo revisa que el usuario esté
 * en una lista fija (USUARIOS_VALIDOS) y que la contraseña sea "1234".
 * Si todo coincide, manda al usuario a MainActivity (el dashboard).
 */
public class LoginActivity extends AppCompatActivity {

    // usuarios y contraseña "quemados" en el código, solo para la demo
    private static final String[] USUARIOS_VALIDOS = {"mileydi", "carlos", "esmeralda"};
    private static final String PASSWORD_VALIDA = "1234";

    private EditText edtUsuario, edtPassword;
    private TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // conectamos las vistas del layout con variables de java
        edtUsuario = findViewById(R.id.edtUsuario);
        edtPassword = findViewById(R.id.edtPassword);
        txtError = findViewById(R.id.txtError);
        Button btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> intentarIngresar());
    }

    // valida los datos ingresados y, si son correctos, abre el dashboard
    private void intentarIngresar() {
        String usuario = edtUsuario.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (esUsuarioValido(usuario) && password.equals(PASSWORD_VALIDA)) {
            txtError.setVisibility(View.GONE);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuario", capitalizar(usuario));
            startActivity(intent);
            finish(); // cerramos el login para que no se pueda volver con "atrás"
        } else {
            txtError.setVisibility(View.VISIBLE);
        }
    }

    // revisa si el usuario escrito está dentro de la lista permitida
    private boolean esUsuarioValido(String usuario) {
        for (String u : USUARIOS_VALIDOS) {
            if (u.equalsIgnoreCase(usuario)) return true;
        }
        return false;
    }

    // pone la primera letra en mayúscula, para mostrar el nombre bonito en el saludo
    private String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1).toLowerCase();
    }
}
