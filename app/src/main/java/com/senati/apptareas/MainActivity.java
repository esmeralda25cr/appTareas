package com.senati.apptareas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.senati.apptareas.adapter.TareaAdapter;
import com.senati.apptareas.db.DatabaseHelper;
import com.senati.apptareas.model.Tarea;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

/*
 * Pantalla principal (dashboard) que se ve justo después de iniciar sesión.
 * Muestra un saludo, contadores de tareas pendientes/completadas, las últimas
 * 5 tareas creadas, y el menú lateral (drawer) para ir a Nueva tarea o Historial.
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView txtPendientes, txtCompletadas, txtSinRegistros;
    private RecyclerView recyclerUltimos;
    private TareaAdapter adapter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // botón hamburguesa que abre el menú lateral
        drawerLayout = findViewById(R.id.drawerLayout);
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> drawerLayout.open());

        NavigationView navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(this::onMenuSeleccionado);

        txtPendientes = findViewById(R.id.txtPendientes);
        txtCompletadas = findViewById(R.id.txtCompletadas);
        txtSinRegistros = findViewById(R.id.txtSinRegistros);

        // lista de las últimas tareas creadas
        recyclerUltimos = findViewById(R.id.recyclerUltimos);
        recyclerUltimos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TareaAdapter(new ArrayList<>(), new TareaAdapter.OnTareaClickListener() {
            @Override
            public void onClick(Tarea tarea) {
                // al tocar una tarea, la abrimos para editarla
                Intent intent = new Intent(MainActivity.this, NuevaTareaActivity.class);
                intent.putExtra(NuevaTareaActivity.EXTRA_TAREA_ID, tarea.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(Tarea tarea) {
                // aquí no hacemos nada; para editar/eliminar con opciones se usa el Historial
            }
        });
        recyclerUltimos.setAdapter(adapter);

        // nombre del usuario que inició sesión, viene del LoginActivity
        String nombreOperario = getIntent().getStringExtra("usuario");
        if (nombreOperario == null) nombreOperario = "Usuario";
        TextView txtSaludo = findViewById(R.id.txtSaludo);
        txtSaludo.setText("¡Hola, " + nombreOperario + "!");

        View btnNuevaTarea = findViewById(R.id.btnNuevaTarea);
        btnNuevaTarea.setOnClickListener(v ->
                startActivity(new Intent(this, NuevaTareaActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos(); // recargamos por si se creó/editó algo en otra pantalla
    }

    // se ejecuta cuando el usuario toca una opción del menú lateral
    private boolean onMenuSeleccionado(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_nuevo) {
            startActivity(new Intent(this, NuevaTareaActivity.class));
        } else if (id == R.id.nav_historial) {
            startActivity(new Intent(this, HistorialActivity.class));
        } else if (id == R.id.nav_cerrar_sesion) {
            // volvemos al login y borramos el historial de pantallas
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        drawerLayout.close();
        return true;
    }

    // trae de la base de datos los contadores y las últimas 5 tareas
    private void cargarDatos() {
        int pendientes = dbHelper.contarPorEstado(Tarea.ESTADO_PENDIENTE)
                + dbHelper.contarPorEstado(Tarea.ESTADO_EN_PROGRESO);
        int completadas = dbHelper.contarPorEstado(Tarea.ESTADO_COMPLETADA);

        txtPendientes.setText(String.valueOf(pendientes));
        txtCompletadas.setText(String.valueOf(completadas));

        List<Tarea> ultimas = dbHelper.obtenerUltimas(5);
        adapter.actualizar(ultimas);

        // si no hay tareas, mostramos el mensaje "sin registros" en vez de la lista vacía
        boolean vacio = ultimas.isEmpty();
        txtSinRegistros.setVisibility(vacio ? View.VISIBLE : View.GONE);
        recyclerUltimos.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }
}
