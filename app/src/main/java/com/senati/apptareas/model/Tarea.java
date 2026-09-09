package com.senati.apptareas.model;

/*
 * Esta clase es solo un "molde" para una tarea: título, descripción, estado,
 * fechas y a quién está asignada. No tiene lógica, solo guarda datos
 * (con sus getters/setters) para pasarlos entre las pantallas y la base de datos.
 */
public class Tarea {

    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_EN_PROGRESO = "En progreso";
    public static final String ESTADO_COMPLETADA = "Completada";

    private long id;
    private String titulo;
    private String descripcion;
    private String estado;          // Pendiente, En progreso, Completada
    private String fechaVencimiento; // dd/MM/yyyy
    private String fechaCreacion;    // dd/MM/yyyy
    private String usuarioAsignado;

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, String estado, String fechaVencimiento,
                 String fechaCreacion, String usuarioAsignado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaCreacion = fechaCreacion;
        this.usuarioAsignado = usuarioAsignado;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getUsuarioAsignado() { return usuarioAsignado; }
    public void setUsuarioAsignado(String usuarioAsignado) { this.usuarioAsignado = usuarioAsignado; }
}
