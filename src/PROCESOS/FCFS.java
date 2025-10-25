package PROCESOS;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FCFS implements Planificador {
    private final List<Proceso> procesosOriginales;
    private final Queue<Proceso> colaListos;
    private Proceso procesoEnCPU = null;

    public FCFS(List<Proceso> procesos) {
        // Ordenar por tiempo de llegada
        procesos.sort(Comparator.comparingInt(Proceso::getInstanteLlegada));
        this.procesosOriginales = procesos;
        this.colaListos = new LinkedList<>();
    }

    @Override
    public Proceso ejecutarPaso(int tiempoActual) {
        //Mover procesos que llegan a la cola de listos
        for (Proceso p : procesosOriginales) {
            if (p.getInstanteLlegada() == tiempoActual && !colaListos.contains(p) && p.getTiempoRestante() > 0) {
                colaListos.add(p);
            }
        }

        //Asignar CPU si está libre
        if (procesoEnCPU == null || procesoEnCPU.getTiempoRestante() == 0) {
            if (procesoEnCPU != null && procesoEnCPU.getTiempoRestante() == 0) {
                procesoEnCPU = null; // Liberar CPU
            }
            if (!colaListos.isEmpty()) {
                procesoEnCPU = colaListos.poll(); // Asignar el siguiente de la cola (FCFS)
            }
        }

        //Ejecutar
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
        return colaListos.toString();
    }
}