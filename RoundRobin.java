package PROCESOS;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class RoundRobin implements Planificador {
    private final List<Proceso> procesosOriginales;
    private final Queue<Proceso> colaListos;
    private final int quantum;
    private Proceso procesoEnCPU = null;
    private int tiempoEnQuantum = 0;

    public RoundRobin(List<Proceso> procesos, int quantum) {
        // Ordenar por tiempo de llegada (inicialmente)
        procesos.sort(Comparator.comparingInt(Proceso::getInstanteLlegada));
        this.procesosOriginales = procesos;
        this.colaListos = new LinkedList<>();
        this.quantum = quantum;
    }

    @Override
    public Proceso ejecutarPaso(int tiempoActual) {
        // Mover procesos que llegan a la cola de listos
        for (Proceso p : procesosOriginales) {
            if (p.getInstanteLlegada() == tiempoActual && !colaListos.contains(p) && p.getTiempoRestante() > 0) {
                colaListos.add(p);
            }
        }

        // Preemptividad/Cambio de contexto
        if (procesoEnCPU != null) {
            // Caso A: El proceso terminó
            if (procesoEnCPU.getTiempoRestante() == 0) {
                procesoEnCPU = null;
                tiempoEnQuantum = 0;
            } 
            // Caso B: Se acabó el quantum
            else if (tiempoEnQuantum >= quantum) {
                colaListos.add(procesoEnCPU); // Mover al final de la cola
                procesoEnCPU = null;
                tiempoEnQuantum = 0;
            }
        }
        
        // Asignar CPU
        if (procesoEnCPU == null && !colaListos.isEmpty()) {
            procesoEnCPU = colaListos.poll();
        }

        // Ejecutar
        if (procesoEnCPU != null) {
            procesoEnCPU.ejecutarPaso(tiempoActual);
            tiempoEnQuantum++;
        }

        return procesoEnCPU;
    }

    @Override
    public List<Proceso> getResultados() {
        return procesosOriginales;
    }

    @Override
    public String getEstadoCola() {
        // Muestra los nombres de los procesos en la cola
        return colaListos.stream().map(Proceso::getNombre).collect(Collectors.joining(", "));
    }
}