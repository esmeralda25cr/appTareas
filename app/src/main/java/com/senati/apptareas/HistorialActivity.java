package com.senati.apptareas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.senati.apptareas.adapter.TareaAdapter;
import com.senati.apptareas.db.DatabaseHelper;
import com.senati.apptareas.model.Tarea;

import java.util.List;

//muestra todas las tareas guardadas, con filtro por estado
public class HistorialActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recycler;
    private TextView txtSinDatos;
    private Spinner spinnerFiltro;

    private final String[] filtros = {"Todas", Tarea.ESTADO_PENDIENTE, Tarea.ESTADO_EN_PROGRESO, Tarea.ESTADO_COMPLETADA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        dbHelper = new DatabaseHelper(this);

        ImageButton btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        recycler = findViewById(R.id.recyclerHistorial);
        txtSinDatos = findViewById(R.id.txtSinDatos);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<String> filtroAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, filtros);
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(filtroAdapter);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarTareas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTareas();
    }

    private void cargarTareas() {
        String filtro = (String) spinnerFiltro.getSelectedItem();
        List<Tarea> lista = (filtro == null || filtro.equals("Todas"))
                ? dbHelper.obtenerTodas()
                : dbHelper.obtenerPorEstado(filtro);

        TareaAdapter adapter = new TareaAdapter(lista, new TareaAdapter.OnTareaClickListener() {
            @Override
            public void onClick(Tarea tarea) {
                Intent intent = new Intent(HistorialActivity.this, NuevaTareaActivity.class);
                intent.putExtra(NuevaTareaActivity.EXTRA_TAREA_ID, tarea.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(Tarea tarea) {
                mostrarOpciones(tarea);
            }
        });
        recycler.setAdapter(adapter);

        boolean vacio = lista.isEmpty();
        txtSinDatos.setVisibility(vacio ? View.VISIBLE : View.GONE);
        recycler.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }

    private void mostrarOpciones(Tarea tarea) {
        String[] opciones;
        if (!tarea.getEstado().equals(Tarea.ESTADO_COMPLETADA)) {
            opciones = new String[]{"Editar", "Marcar como completada", "Eliminar"};
        } else {
            opciones = new String[]{"Editar", "Eliminar"};
        }

        new AlertDialog.Builder(this)
                .setTitle(tarea.getTitulo())
                .setItems(opciones, (dialog, which) -> {
                    String seleccion = opciones[which];
                    if (seleccion.equals("Editar")) {
                        Intent intent = new Intent(HistorialActivity.this, NuevaTareaActivity.class);
                        intent.putExtra(NuevaTareaActivity.EXTRA_TAREA_ID, tarea.getId());
                        startActivity(intent);
                    } else if (seleccion.equals("Marcar como completada")) {
                        tarea.setEstado(Tarea.ESTADO_COMPLETADA);
                        dbHelper.actualizarTarea(tarea);
                        Toast.makeText(this, "Tarea completada", Toast.LENGTH_SHORT).show();
                        cargarTareas();
                    } else if (seleccion.equals("Eliminar")) {
                        confirmarEliminar(tarea);
                    }
                })
                .show();
    }

    private void confirmarEliminar(Tarea tarea) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que deseas eliminar \"" + tarea.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.eliminarTarea(tarea.getId());
                    Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                    cargarTareas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
