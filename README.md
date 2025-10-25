
🖥️ Simulador de Planificación de Procesos (Sistemas Operativos)

Este proyecto implementa un simulador visual de planificación de procesos a corto plazo, utilizando Java Swing para la interfaz gráfica. Permite simular distintos algoritmos clásicos de planificación de CPU, mostrando en tiempo real el avance de cada proceso, su estado en la cola y el diagrama de Gantt.

-------------------
Descripción General
-------------------

El simulador permite modelar el comportamiento de un planificador de CPU en un sistema operativo.
El usuario puede:

Ingresar procesos con su ID, tiempo de llegada y tiempo de ráfaga de CPU.

Seleccionar un algoritmo de planificación (FCFS, SJF o Round Robin).

Visualizar la ejecución paso a paso mediante un temporizador.

Analizar métricas de rendimiento como tiempo de retorno, espera e índice de servicio.

El programa simula un planificador a corto plazo donde cada unidad de tiempo equivale a un segundo real en la simulación.

--------------------------
Arquitectura del Proyecto
--------------------------

El proyecto se basa en una estructura modular dentro del paquete PROCESOS:

PROCESOS/

│
├── SimuladorGUI.java    
├── Planificador.java      
├── FCFS.java               
├── SJF.java                
├── RoundRobin.java         
└── Proceso.java       

-----------------------------
Clases Principales
-----------------------------
SimuladorGUI

Clase principal que extiende JFrame y maneja toda la interfaz gráfica y la lógica de simulación.
Funciones clave:

Agregar procesos manualmente.

Seleccionar algoritmo y quantum (para RR).

Iniciar simulación con actualización visual automática.

Mostrar resultados finales en tablas y consola.

Utiliza componentes de Swing como JTable, JLabel, JButton, y Timer para animar la simulación.     

-------------------------------
Algoritmos Soportados
-------------------------------

FCFS (First Come, First Served)

Ordena por tiempo de llegada.

No expropiativo.

SJF (Shortest Job First)

Selecciona el proceso con menor tiempo de CPU restante.

No expropiativo.

Round Robin (RR)

Usa quantum definido por el usuario.

Expropiativo: cada proceso se ejecuta por turnos circulares.

----------------------
🖼️ Interfaz Gráfica
----------------------

----------------------
Métricas Calculadas
----------------------

Para cada proceso se muestran las siguientes métricas:

Métrica	Descripción
T. Retorno (TR)	Tiempo total desde la llegada hasta la finalización.
T. Espera (TE)	Tiempo total que el proceso esperó en la cola.
I. Servicio (IS)	Índice de servicio = TR / CPU total.

Además, se calculan promedios generales de TR y TE al finalizar la simulación.

---------------------
Como usarlo
---------------------

------------------------------
💻 Requisitos del Sistema
------------------------------

Java 8 o superior.

Librerías estándar de Java (javax.swing, java.util, java.awt).

Compatible con Windows, Linux y macOS.
