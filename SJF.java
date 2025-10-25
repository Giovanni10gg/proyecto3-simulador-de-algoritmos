package PROCESOS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SJF implements Planificador {
    private final List<Proceso> procesosOriginales;
    // Usaremos una lista temporal que ordenaremos por ráfaga (tiempo restante)
    private final List<Proceso> procesosListos; 
    private Proceso procesoEnCPU = null;

    public SJF(List<Proceso> procesos) {
        this.procesosOriginales = procesos;
        this.procesosListos = new ArrayList<>();
    }

    @Override
    public Proceso ejecutarPaso(int tiempoActual) {
        // Mover procesos que llegan a la lista de listos
        for (Proceso p : procesosOriginales) {
            if (p.getInstanteLlegada() == tiempoActual && !procesosListos.contains(p) && p.getTiempoRestante() > 0) {
                procesosListos.add(p);
            }
        }

        // Asignar CPU si está libre
        if (procesoEnCPU == null || procesoEnCPU.getTiempoRestante() == 0) {
            if (procesoEnCPU != null && procesoEnCPU.getTiempoRestante() == 0) {
                procesoEnCPU = null; // Liberar CPU
            }
            
            // Si hay procesos listos, elegimos el SJF
            if (!procesosListos.isEmpty()) {
                // Ordenar por tiempo restante (ráfaga más corta)
                procesosListos.sort(Comparator.comparingInt(Proceso::getTiempoRestante));
                
                procesoEnCPU = procesosListos.remove(0); // Tomar el primero (más corto)
            }
        }

        // Ejecutar
        if (procesoEnCPU != null) {
            procesoEnCPU.ejecutarPaso(tiempoActual);
        }

        return procesoEnCPU;
    }

    @Override
    public List<Proceso> getResultados() {
        return procesosOriginales;
    }

    @Override
    public String getEstadoCola() {
        return procesosListos.toString();
    }
}