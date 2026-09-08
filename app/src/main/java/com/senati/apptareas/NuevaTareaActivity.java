package com.senati.apptareas;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.senati.apptareas.db.DatabaseHelper;
import com.senati.apptareas.model.Tarea;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//formulario para crear o editar una tarea
public class NuevaTareaActivity extends AppCompatActivity {

    public static final String EXTRA_TAREA_ID = "extra_tarea_id";

    private EditText editTitulo, editDescripcion, editFechaVencimiento, editUsuario;
    private Spinner spinnerEstado;
    private DatabaseHelper dbHelper;
    private Tarea tareaActual;
    private boolean esEdicion = false;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_tarea);

        dbHelper = new DatabaseHelper(this);

        ImageButton btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        editTitulo = findViewById(R.id.editTitulo);
        editDescripcion = findViewById(R.id.editDescripcion);
        editFechaVencimiento = findViewById(R.id.editFechaVencimiento);
        editUsuario = findViewById(R.id.editUsuario);
        spinnerEstado = findViewById(R.id.spinnerEstado);

        String[] estados = {Tarea.ESTADO_PENDIENTE, Tarea.ESTADO_EN_PROGRESO, Tarea.ESTADO_COMPLETADA};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estados);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(estadoAdapter);

        editFechaVencimiento.setOnClickListener(v -> mostrarDatePicker());

        TextView titulo = findViewById(R.id.txtTituloHeader);
        long tareaId = getIntent().getLongExtra(EXTRA_TAREA_ID, -1);
        if (tareaId != -1) {
            esEdicion = true;
            titulo.setText("Editar Tarea");
            cargarTarea(tareaId);
        } else {
            titulo.setText("Nueva Tarea");
        }

        TextView btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> guardarTarea());
    }

    private void cargarTarea(long id) {
        for (Tarea t : dbHelper.obtenerTodas()) {
            if (t.getId() == id) {
                tareaActual = t;
                break;
            }
        }
        if (tareaActual != null) {
            editTitulo.setText(tareaActual.getTitulo());
            editDescripcion.setText(tareaActual.getDescripcion());
            editFechaVencimiento.setText(tareaActual.getFechaVencimiento());
            editUsuario.setText(tareaActual.getUsuarioAsignado());

            String[] estados = {Tarea.ESTADO_PENDIENTE, Tarea.ESTADO_EN_PROGRESO, Tarea.ESTADO_COMPLETADA};
            for (int i = 0; i < estados.length; i++) {
                if (estados[i].equals(tareaActual.getEstado())) {
                    spinnerEstado.setSelection(i);
                    break;
                }
            }
        }
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editFechaVencimiento.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    /** Valida el formulario y guarda la tarea en la base de datos local. */
    private void guardarTarea() {
        String titulo = editTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            editTitulo.setError("Ingrese el título de la tarea");
            editTitulo.requestFocus();
            return;
        }

        String descripcion = editDescripcion.getText().toString().trim();
        String estado = spinnerEstado.getSelectedItem().toString();
        String fechaVencimiento = editFechaVencimiento.getText().toString().trim();
        String usuario = editUsuario.getText().toString().trim();

        if (esEdicion && tareaActual != null) {
            tareaActual.setTitulo(titulo);
            tareaActual.setDescripcion(descripcion);
            tareaActual.setEstado(estado);
            tareaActual.setFechaVencimiento(fechaVencimiento);
            tareaActual.setUsuarioAsignado(usuario);
            dbHelper.actualizarTarea(tareaActual);
            Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
        } else {
            Tarea nueva = new Tarea();
            nueva.setTitulo(titulo);
            nueva.setDescripcion(descripcion);
            nueva.setEstado(estado);
            nueva.setFechaVencimiento(fechaVencimiento);
            nueva.setFechaCreacion(sdf.format(Calendar.getInstance().getTime()));
            nueva.setUsuarioAsignado(usuario);
            long id = dbHelper.insertarTarea(nueva);
            if (id == -1) {
                Toast.makeText(this, "Ocurrió un error al guardar la tarea", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Tarea creada", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
