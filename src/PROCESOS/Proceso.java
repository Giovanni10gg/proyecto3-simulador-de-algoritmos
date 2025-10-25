package PROCESOS;

public class Proceso {
    private final String nombre;
    private final int instanteLlegada;
    private final int tiempoCPU;

    private int tiempoRestante;
    private int tiempoComienzo = -1;
    private int tiempoFinalizacion = 0;
    private int tiempoRetorno = 0;
    private int tiempoEspera = 0;

    // Constructor
    public Proceso(String nombre, int llegada, int cpu) {
        this.nombre = nombre;
        this.instanteLlegada = llegada;
        this.tiempoCPU = cpu;
        this.tiempoRestante = cpu;
    }

    // Método de clonación esencial para la simulación
    public Proceso reiniciar() {
        // Crea una copia con los valores iniciales.
        Proceso p = new Proceso(this.nombre, this.instanteLlegada, this.tiempoCPU);
        return p;
    }

    // Método para calcular métricas finales
    public void calcularMetricas() {
        if (tiempoFinalizacion > 0) {
            this.tiempoRetorno = this.tiempoFinalizacion - this.instanteLlegada;
            this.tiempoEspera = this.tiempoRetorno - this.tiempoCPU;
        }
    }
    
    // Calcula el índice de servicio (método simple)
    public double getIndiceServicio() {
        if (tiempoRetorno == 0) return 0.0;
        return (double) this.tiempoCPU / this.tiempoRetorno;
    }
    
    //  Getters y Setters (Método Get Set)

    public String getNombre() { return nombre; }
    public int getInstanteLlegada() { return instanteLlegada; }
    public int getTiempoCPU() { return tiempoCPU; }
    public int getTiempoRestante() { return tiempoRestante; }
    public void setTiempoRestante(int tiempoRestante) { this.tiempoRestante = tiempoRestante; }

    public int getTiempoComienzo() { return tiempoComienzo; }
    public void setTiempoComienzo(int tiempoComienzo) { this.tiempoComienzo = tiempoComienzo; }
    
    public int getTiempoFinalizacion() { return tiempoFinalizacion; }
    public void setTiempoFinalizacion(int tiempoFinalizacion) { this.tiempoFinalizacion = tiempoFinalizacion; }

    public int getTiempoRetorno() { return tiempoRetorno; }
    public int getTiempoEspera() { return tiempoEspera; }
    
    // Método para simular la ejecución de 1 unidad de tiempo
    public void ejecutarPaso(int tiempoActual) {
        if (tiempoRestante > 0) {
            if (tiempoComienzo == -1) {
                tiempoComienzo = tiempoActual;
            }
            tiempoRestante--;
            if (tiempoRestante == 0) {
                tiempoFinalizacion = tiempoActual + 1; // El fin ocurre al inicio del siguiente instante
            }
        }
    }
}