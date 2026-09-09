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

/*
 * Muestra todas las tareas guardadas y permite filtrarlas por estado
 * (Todas / Pendiente / En progreso / Completada) usando el spinner de arriba.
 * Al mantener presionada una tarea se abre un menú para editarla, marcarla
 * como completada o eliminarla.
 */
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

        // cada vez que cambia el filtro, recargamos la lista
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarTareas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no hace falta hacer nada aquí
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTareas(); // por si se creó, editó o eliminó algo en otra pantalla
    }

    // trae la lista según el filtro elegido y arma el adapter del RecyclerView
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

        // si no hay tareas con ese filtro, mostramos el mensaje "sin datos"
        boolean vacio = lista.isEmpty();
        txtSinDatos.setVisibility(vacio ? View.VISIBLE : View.GONE);
        recycler.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }

    // menú de acciones que aparece al mantener presionada una tarea
    private void mostrarOpciones(Tarea tarea) {
        boolean estaCompletada = tarea.getEstado().equals(Tarea.ESTADO_COMPLETADA);
        String[] opciones = estaCompletada
                ? new String[]{"Editar", "Eliminar"}
                : new String[]{"Editar", "Marcar como completada", "Eliminar"};

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

    // pide confirmación antes de borrar una tarea, para evitar borrados por error
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
