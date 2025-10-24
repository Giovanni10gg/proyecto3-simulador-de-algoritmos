package PROCESOS;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSeparator;

public class SimuladorGUI extends JFrame {
    
    // Lista de procesos ingresados por el usuario
    private final List<Proceso> procesosIniciales = new ArrayList<>();
    // Usa la interfaz Planificador
    private Planificador planificadorActual; 
    private int tiempoGlobal = 0;
    private Timer timer;
    private int quantumSeleccionado = 0;
    
    // --- Componentes ESENCIALES ---
    private javax.swing.JComboBox<String> jComboBoxAlgoritmo;
    private javax.swing.JTextField jTextFieldQuantum;
    private javax.swing.JButton jButtonComenzar;
    private javax.swing.JLabel jLabelTiempo;
    private JLabel etiquetaPrueba; 

    // --- Componentes para INGRESO DE DATOS ---
    private javax.swing.JTextField jTextFieldID;
    private javax.swing.JTextField jTextFieldLlegada;
    private javax.swing.JTextField jTextFieldCPU;
    private javax.swing.JButton jButtonAgregar;
    private int procesoCounter = 1;

    // --- Tablas y Modelos ---
    private javax.swing.JScrollPane jScrollPaneTabla;
    private javax.swing.JTable jTableProcesos;
    private javax.swing.table.DefaultTableModel procesosTableModel; 

    private javax.swing.JScrollPane jScrollPaneGantt;
    private javax.swing.JTable jTableGantt;
    private javax.swing.table.DefaultTableModel ganttTableModel;

    public static void main(String args[]) {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SimuladorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        EventQueue.invokeLater(() -> {
            SimuladorGUI ventana = new SimuladorGUI();
            ventana.setVisible(true);
        });
    }

    // CONSTRUCTOR - LUGAR DONDE SE INICIALIZAN LOS COMPONENTES
    
    public SimuladorGUI() {
        setTitle("Simulador SO");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // --- Panel de Ingreso y Control ---
        javax.swing.JPanel panelTop = new javax.swing.JPanel(new GridLayout(2, 1));
        
        // Sub-Panel 1: Ingreso de Datos
        javax.swing.JPanel panelIngreso = new javax.swing.JPanel(new FlowLayout(FlowLayout.LEFT));
        jTextFieldID = new javax.swing.JTextField("P" + procesoCounter, 4);
        jTextFieldLlegada = new javax.swing.JTextField("0", 3);
        jTextFieldCPU = new javax.swing.JTextField("5", 3);
        jButtonAgregar = new javax.swing.JButton("Agregar Proceso");
        
        panelIngreso.add(new javax.swing.JLabel("ID:"));
        panelIngreso.add(jTextFieldID);
        panelIngreso.add(new javax.swing.JLabel("Llegada (ti):"));
        panelIngreso.add(jTextFieldLlegada);
        panelIngreso.add(new javax.swing.JLabel("CPU (t):"));
        panelIngreso.add(jTextFieldCPU);
        panelIngreso.add(jButtonAgregar);

        // Sub-Panel 2: Selector y Botón Comenzar
        javax.swing.JPanel panelControl = new javax.swing.JPanel(new FlowLayout(FlowLayout.LEFT));
        jComboBoxAlgoritmo = new javax.swing.JComboBox<>(new String[] { "FCFS", "SJF", "RoundRobin" });
        jTextFieldQuantum = new javax.swing.JTextField("2", 3);
        jButtonComenzar = new javax.swing.JButton("Comenzar Simulación");
        jLabelTiempo = new javax.swing.JLabel("Tiempo: 0s | Ejecutando: IDLE");
        etiquetaPrueba = jLabelTiempo; 
        
        panelControl.add(new javax.swing.JLabel("Algoritmo:"));
        panelControl.add(jComboBoxAlgoritmo);
        panelControl.add(new javax.swing.JLabel("Quantum (RR):"));
        panelControl.add(jTextFieldQuantum);
        panelControl.add(jButtonComenzar);
        panelControl.add(new JSeparator(javax.swing.SwingConstants.VERTICAL)); 
        panelControl.add(jLabelTiempo);
        
        panelTop.add(panelIngreso);
        panelTop.add(panelControl);
        add(panelTop, BorderLayout.NORTH);
        
        // --- Tablas ---
        javax.swing.JPanel panelTablas = new javax.swing.JPanel(new GridLayout(2, 1));

        // Tabla de Procesos (Datos ti, t, tf, etc.)
        String[] procColumnNames = {"ID", "Llegada", "CPU Total", "Restante", "Comienzo", "Fin", "T. Retorno", "T. Espera", "I. Servicio"};
        // Inicialización de procesosTableModel
        procesosTableModel = new DefaultTableModel(new Object[0][procColumnNames.length], procColumnNames);
        jTableProcesos = new javax.swing.JTable(procesosTableModel);
        jScrollPaneTabla = new javax.swing.JScrollPane(jTableProcesos);
        panelTablas.add(jScrollPaneTabla);

        // Tabla de Seguimiento/Gantt (Dinámica)
        String[] ganttColumnNames = {"Proceso"};
        // Inicialización de ganttTableModel
        ganttTableModel = new DefaultTableModel(new Object[0][ganttColumnNames.length], ganttColumnNames);
        jTableGantt = new javax.swing.JTable(ganttTableModel);
        jScrollPaneGantt = new javax.swing.JScrollPane(jTableGantt);
        panelTablas.add(jScrollPaneGantt);
        
        add(panelTablas, BorderLayout.CENTER);
        
        // --- Listeners ---
        jButtonAgregar.addActionListener(e -> agregarProceso());
        jButtonComenzar.addActionListener(e -> comenzarSimulacion());

        pack();
    }
  
    // FIN CONSTRUCTOR
    
    private void agregarProceso() {
        try {
            String id = jTextFieldID.getText().trim();
            int llegada = Integer.parseInt(jTextFieldLlegada.getText().trim());
            int cpu = Integer.parseInt(jTextFieldCPU.getText().trim());

            if (id.isEmpty() || llegada < 0 || cpu <= 0) {
                JOptionPane.showMessageDialog(this, "Por favor, introduce valores válidos (CPU > 0).", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Proceso nuevoProceso = new Proceso(id, llegada, cpu);
            procesosIniciales.add(nuevoProceso);

            procesosTableModel.addRow(new Object[]{
                id, llegada, cpu, cpu, "N/A", "N/A", "N/A", "N/A", "N/A"
            });
            
            // Solo añadir a ganttTableModel la fila (ya que solo tiene una columna inicial)
            ganttTableModel.addRow(new Object[]{id}); 

            procesoCounter++;
            jTextFieldID.setText("P" + procesoCounter);
            jTextFieldLlegada.setText("0");
            jTextFieldCPU.setText("5");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Llegada y CPU deben ser números enteros.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void comenzarSimulacion() {
        if (procesosIniciales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega al menos un proceso para comenzar la simulación.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String algoritmo = (String) jComboBoxAlgoritmo.getSelectedItem();
        int quantum = 0; 
        
        if ("RoundRobin".equals(algoritmo)) {
            try {
                quantum = Integer.parseInt(jTextFieldQuantum.getText().trim());
                if (quantum <= 0) {
                     JOptionPane.showMessageDialog(this, "El Quantum debe ser un número entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El Quantum debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        List<Proceso> procesosParaSimulacion = new ArrayList<>();
        for(Proceso p : procesosIniciales) {
            procesosParaSimulacion.add(p.reiniciar()); 
        }
        
        jButtonComenzar.setEnabled(false);
        jButtonAgregar.setEnabled(false);
        jComboBoxAlgoritmo.setEnabled(false);
        jTextFieldQuantum.setEnabled(false);
        jTextFieldID.setEnabled(false);
        jTextFieldLlegada.setEnabled(false);
        jTextFieldCPU.setEnabled(false);

        iniciarSimulacion(procesosParaSimulacion, algoritmo, quantum);
    }

    private void iniciarSimulacion(List<Proceso> procesos, String algoritmo, int quantum) {
        tiempoGlobal = 0;
        
        switch (algoritmo) {
            case "FCFS":
                planificadorActual = new FCFS(procesos);
                break;
            case "SJF":
                planificadorActual = new SJF(procesos);
                break;
            case "RoundRobin":
                this.quantumSeleccionado = quantum;
                planificadorActual = new RoundRobin(procesos, quantum);
                break;
            default:
                System.err.println("Algoritmo no reconocido: " + algoritmo);
                return;
            }
        
        prepararTablaGantt(); 

        if (timer != null) timer.stop();
        
        timer = new Timer(1000, (ActionEvent e) -> { 
            ejecutarUnidadDeTiempo();
            
            if (todosLosProcesosHanTerminado()) {
                timer.stop();
                etiquetaPrueba.setText("Simulación Terminada. Calculando resultados...");
                List<Proceso> resultados = obtenerResultadosDelPlanificador();
                mostrarResultadoFinales(resultados);
                
                jButtonComenzar.setEnabled(true);
                jButtonAgregar.setEnabled(true);
                jComboBoxAlgoritmo.setEnabled(true);
                jTextFieldQuantum.setEnabled(true);
                jTextFieldID.setEnabled(true);
                jTextFieldLlegada.setEnabled(true);
                jTextFieldCPU.setEnabled(true);
            }
            tiempoGlobal++;
        });
        timer.start();
    }
    
    private void ejecutarUnidadDeTiempo() {
        Proceso procesoEnCPU = null;

        // Llama a ejecutarPaso a través de la interfaz Planificador
        procesoEnCPU = planificadorActual.ejecutarPaso(tiempoGlobal);
        
        String pNombre = (procesoEnCPU != null) ? procesoEnCPU.getNombre() : "IDLE";
        
        // Uso de getEstadoCola() del Planificador
        jLabelTiempo.setText("Tiempo: " + tiempoGlobal + "s | Ejecutando: " + pNombre + " | Cola: " + planificadorActual.getEstadoCola()); 
        actualizarTablaVisual(tiempoGlobal, procesoEnCPU);
    }

    private void actualizarTablaVisual(int tiempoGlobal, Proceso procesoEnCPU) {
        
        if (procesosTableModel == null || ganttTableModel == null) {
            Logger.getLogger(SimuladorGUI.class.getName()).log(Level.SEVERE, "Modelos de tabla no inicializados. Deteniendo actualización.");
            if (timer != null) timer.stop();
            return;
        }
        
        List<Proceso> resultados = obtenerResultadosDelPlanificador();
        
        // 1. Actualizar la tabla de Procesos (Métricas en tiempo real)
        for (int i = 0; i < procesosIniciales.size(); i++) {
            final String nombreProcesoInicial = procesosIniciales.get(i).getNombre();
            
            Proceso pSimulacion = resultados.stream()
                .filter(res -> res.getNombre().equals(nombreProcesoInicial))
                .findFirst().orElse(null);

            if (pSimulacion != null) {
                // Actualiza el tiempo restante en la fila i, columna 3 ("Restante")
                procesosTableModel.setValueAt(pSimulacion.getTiempoRestante(), i, 3);
                
                // Obtener el valor de la celda de forma segura
                Object valorCeldaComienzo = procesosTableModel.getValueAt(i, 4);
                
                if (pSimulacion.getTiempoComienzo() != -1 && "N/A".equals(valorCeldaComienzo)) {
                    procesosTableModel.setValueAt(pSimulacion.getTiempoComienzo(), i, 4);
                }
                
                // Obtener el valor de la celda de forma segura
                Object valorCeldaFin = procesosTableModel.getValueAt(i, 5);
                
                if (pSimulacion.getTiempoRestante() == 0 && pSimulacion.getTiempoFinalizacion() != 0 && "N/A".equals(valorCeldaFin)) {
                    procesosTableModel.setValueAt(pSimulacion.getTiempoFinalizacion(), i, 5);
                }
            }
        }
        
        // 2. Actualizar la tabla de Seguimiento (Gantt Dinámico)
        
        int columnIndex = ganttTableModel.getColumnCount();
        
        if (tiempoGlobal >= 0) { 
            ganttTableModel.addColumn(String.valueOf(tiempoGlobal)); 

            for (int i = 0; i < ganttTableModel.getRowCount(); i++) {
                ganttTableModel.setValueAt("", i, columnIndex);
            }

            if (procesoEnCPU != null) {
                for (int row = 0; row < ganttTableModel.getRowCount(); row++) {
                    // La columna 0 es el nombre del proceso
                    Object nombreEnGantt = ganttTableModel.getValueAt(row, 0); 
                    
                    if (procesoEnCPU.getNombre().equals(nombreEnGantt)) { 
                        ganttTableModel.setValueAt("X", row, columnIndex); 
                        break;
                    }
                }
            }
        }
        
        jTableGantt.repaint();
        jTableGantt.revalidate();
    }
    
    private List<Proceso> obtenerResultadosDelPlanificador() {
        if (planificadorActual == null) return new ArrayList<>();
        return planificadorActual.getResultados();
    }
    
    private boolean todosLosProcesosHanTerminado() {
        List<Proceso> procesos = obtenerResultadosDelPlanificador();
        if (procesos.isEmpty()) {
            return false;
        }
        return procesos.stream().allMatch(p -> p.getTiempoRestante() == 0);
    }
    
    private void mostrarResultadoFinales(List<Proceso> resultados) {
        
        // 1. Calcular métricas y llenar la tabla de la GUI (tabla superior)
        for(int i = 0; i < procesosIniciales.size(); i++) {
            final String nombreProcesoInicial = procesosIniciales.get(i).getNombre();
            Proceso p = resultados.stream()
                .filter(res -> res.getNombre().equals(nombreProcesoInicial))
                .findFirst().orElse(null);

            if (p != null) {
                p.calcularMetricas();
                int rowIndex = i;
                
                procesosTableModel.setValueAt(p.getTiempoRetorno(), rowIndex, 6);
                procesosTableModel.setValueAt(p.getTiempoEspera(), rowIndex, 7);
                procesosTableModel.setValueAt(String.format("%.4f", p.getIndiceServicio()), rowIndex, 8);
            }
        }

        // 2. Mostrar promedios en consola
        System.out.println("--- Resultados Finales ---");
        System.out.println("Proceso | T_Llegada | T_CPU | T_Fin | T_Retorno | T_Espera | I.S.");
        
        int sumaRetorno = 0; 
        int sumaEspera = 0;
        double numProcesos = 0;
        
        for(Proceso p : resultados) { 
            if (p.getTiempoFinalizacion() > 0) {
                 sumaRetorno += p.getTiempoRetorno();
                 sumaEspera += p.getTiempoEspera();
                 numProcesos++;
            }
           
            System.out.printf("%-7s | %-9d | %-5d | %-5d | %-9d | %-8d | %.4f%n",
                                p.getNombre(), 
                                p.getInstanteLlegada(),
                                p.getTiempoCPU(),
                                p.getTiempoFinalizacion(),
                                p.getTiempoRetorno(),
                                p.getTiempoEspera(),
                                p.getIndiceServicio()); 
        } 
        
        if (numProcesos > 0) {
            System.out.println("-----------------------------------------------------------------");
            System.out.printf("PROMEDIOS: | T_Retorno: %.2f | T_Espera: %.2f%n",
                            sumaRetorno / numProcesos,
                            sumaEspera / numProcesos);
        }
    }

    private void prepararTablaGantt() {
        // Limpiar encabezados de la tabla Gantt
        ganttTableModel.setColumnCount(0);
        ganttTableModel.addColumn("Proceso"); 
        
        // Limpiar el contenido de las columnas de métricas en la tabla superior
        for (int i = 0; i < procesosTableModel.getRowCount(); i++) {
            // Resetear columna "Restante" (se asume que columna 2 es CPU Total)
            procesosTableModel.setValueAt(procesosTableModel.getValueAt(i, 2), i, 3);
            
            // Resetear columnas de resultados (4 a 8)
            for (int j = 4; j < procesosTableModel.getColumnCount(); j++) {
                procesosTableModel.setValueAt("N/A", i, j);
            }
        }
    }
}