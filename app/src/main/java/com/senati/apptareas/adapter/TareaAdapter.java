package com.senati.apptareas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.senati.apptareas.R;
import com.senati.apptareas.model.Tarea;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolder> {

    public interface OnTareaClickListener {
        void onClick(Tarea tarea);
        void onLongClick(Tarea tarea);
    }

    private final List<Tarea> lista;
    private final OnTareaClickListener listener;

    public TareaAdapter(List<Tarea> lista) {
        this(lista, null);
    }

    public TareaAdapter(List<Tarea> lista, OnTareaClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarea t = lista.get(position);
        holder.txtTitulo.setText(t.getTitulo());
        holder.txtUsuario.setText(t.getUsuarioAsignado() == null || t.getUsuarioAsignado().isEmpty()
                ? "Sin asignar" : t.getUsuarioAsignado());
        holder.txtVence.setText(t.getFechaVencimiento());
        holder.txtEstado.setText(t.getEstado());

        int color;
        switch (t.getEstado() == null ? "" : t.getEstado()) {
            case Tarea.ESTADO_EN_PROGRESO:
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.en_progreso);
                break;
            case Tarea.ESTADO_COMPLETADA:
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.completada);
                break;
            default:
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.pendiente);
                break;
        }
        holder.txtEstado.setTextColor(color);
        holder.viewEstadoColor.setBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(t);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(t);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizar(List<Tarea> nuevaLista) {
        lista.clear();
        lista.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewEstadoColor;
        TextView txtTitulo, txtUsuario, txtVence, txtEstado;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewEstadoColor = itemView.findViewById(R.id.viewEstadoColor);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtUsuario = itemView.findViewById(R.id.txtUsuario);
            txtVence = itemView.findViewById(R.id.txtVence);
            txtEstado = itemView.findViewById(R.id.txtEstado);
        }
    }
}
