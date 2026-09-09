package com.senati.apptareas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.senati.apptareas.model.Tarea;

import java.util.ArrayList;
import java.util.List;

/*
 * Esta clase maneja toda la base de datos SQLite de la app: crea la tabla
 * "tareas" y tiene los métodos para insertar, actualizar, eliminar y
 * consultar tareas. Es el único lugar donde se escribe SQL.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tareas.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_TAREAS = "tareas";
    public static final String COL_ID = "id";
    public static final String COL_TITULO = "titulo";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_ESTADO = "estado";
    public static final String COL_FECHA_VENCIMIENTO = "fecha_vencimiento";
    public static final String COL_FECHA_CREACION = "fecha_creacion";
    public static final String COL_USUARIO = "usuario_asignado";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_TAREAS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TITULO + " TEXT, " +
                    COL_DESCRIPCION + " TEXT, " +
                    COL_ESTADO + " TEXT, " +
                    COL_FECHA_VENCIMIENTO + " TEXT, " +
                    COL_FECHA_CREACION + " TEXT, " +
                    COL_USUARIO + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        onCreate(db);
    }

    /** Inserta una nueva tarea. Devuelve el id generado (-1 si falla). */
    public long insertarTarea(Tarea t) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_TAREAS, null, tareaAValues(t));
        db.close();
        return id;
    }

    public int actualizarTarea(Tarea t) {
        SQLiteDatabase db = getWritableDatabase();
        int filas = db.update(TABLE_TAREAS, tareaAValues(t), COL_ID + " = ?",
                new String[]{String.valueOf(t.getId())});
        db.close();
        return filas;
    }

    public void eliminarTarea(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TAREAS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /** Trae todas las tareas, las más nuevas primero. */
    public List<Tarea> obtenerTodas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TAREAS, null, null, null, null, null, COL_ID + " DESC");
        List<Tarea> lista = listaDesdeCursor(c);
        db.close();
        return lista;
    }

    /** Trae solo las últimas N tareas creadas (para el dashboard). */
    public List<Tarea> obtenerUltimas(int limite) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TAREAS, null, null, null, null, null,
                COL_ID + " DESC", String.valueOf(limite));
        List<Tarea> lista = listaDesdeCursor(c);
        db.close();
        return lista;
    }

    /** Trae solo las tareas que tengan un estado específico (para el filtro del Historial). */
    public List<Tarea> obtenerPorEstado(String estado) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TAREAS, null,
                COL_ESTADO + " = ?", new String[]{estado},
                null, null, COL_ID + " DESC");
        List<Tarea> lista = listaDesdeCursor(c);
        db.close();
        return lista;
    }

    // recorre un cursor y arma la lista de tareas; usado por los 3 métodos "obtener..." de arriba
    private List<Tarea> listaDesdeCursor(Cursor c) {
        List<Tarea> lista = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                lista.add(cursorATarea(c));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    /** Cuenta cuántas tareas hay con un estado específico (para los contadores del dashboard). */
    public int contarPorEstado(String estado) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TAREAS, new String[]{COL_ID},
                COL_ESTADO + "=?", new String[]{estado}, null, null, null);
        int total = c.getCount();
        c.close();
        db.close();
        return total;
    }

    // convierte un objeto Tarea a ContentValues (formato que pide SQLite para insertar/actualizar)
    private ContentValues tareaAValues(Tarea t) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TITULO, t.getTitulo());
        cv.put(COL_DESCRIPCION, t.getDescripcion());
        cv.put(COL_ESTADO, t.getEstado());
        cv.put(COL_FECHA_VENCIMIENTO, t.getFechaVencimiento());
        cv.put(COL_FECHA_CREACION, t.getFechaCreacion());
        cv.put(COL_USUARIO, t.getUsuarioAsignado());
        return cv;
    }

    // hace lo contrario: convierte una fila del cursor de vuelta a un objeto Tarea
    private Tarea cursorATarea(Cursor c) {
        Tarea t = new Tarea();
        t.setId(c.getLong(c.getColumnIndexOrThrow(COL_ID)));
        t.setTitulo(c.getString(c.getColumnIndexOrThrow(COL_TITULO)));
        t.setDescripcion(c.getString(c.getColumnIndexOrThrow(COL_DESCRIPCION)));
        t.setEstado(c.getString(c.getColumnIndexOrThrow(COL_ESTADO)));
        t.setFechaVencimiento(c.getString(c.getColumnIndexOrThrow(COL_FECHA_VENCIMIENTO)));
        t.setFechaCreacion(c.getString(c.getColumnIndexOrThrow(COL_FECHA_CREACION)));
        t.setUsuarioAsignado(c.getString(c.getColumnIndexOrThrow(COL_USUARIO)));
        return t;
    }
}
