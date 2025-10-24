package PROCESOS;

import java.util.List;

public interface Planificador {
    Proceso ejecutarPaso(int tiempoActual);
    List<Proceso> getResultados();
    String getEstadoCola();
}