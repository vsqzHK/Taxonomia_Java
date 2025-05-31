Sistema de Evaluación Basado en la Taxonomía de Bloom (Java GUI)

Este proyecto es una aplicación de escritorio desarrollada en Java con Swing para la asignatura Paradigmas de Programación. Su objetivo es gestionar y aplicar pruebas compuestas por ítems clasificados según la Taxonomía de Bloom, facilitando la administración de evaluaciones y la revisión de respuestas.

Tecnologias Utilizadas

    Lenguaje: Java (JDK 23)
    Interfaz Gráfica (GUI): Java Swing (GroupLayout)
    Entorno de desarrollo: IntelliJ IDEA

Funcionalidades Principales

    Carga de Ítems: Permite cargar ítems de prueba desde un archivo de texto, mostrando la cantidad y el tiempo estimado.
    Aplicación de Prueba: Presenta los ítems uno por uno, permitiendo al usuario responder y navegar, manteniendo las respuestas.
    Revisión de Respuestas: Al finalizar, muestra un resumen del porcentaje de respuestas correctas por nivel de Bloom y tipo de ítem, con una opción para revisar cada pregunta individualmente.

Alcances y Restricciones

    Modularización: Dividido en paquetes backend (lógica) y frontend (GUI), comunicándose vía notificación-suscripción.
    Validación de Archivo: Gestiona errores de formato en el archivo de ítems mediante excepciones.
    Tipos de Ítems: Soporta ítems de selección múltiple y verdadero/falso.

Formato del Archivo de Ítems

El archivo de ítems es un .txt donde cada línea es un ítem, con campos separados por ;.

Estructura: Tipo;Nivel_Bloom;Enunciado;Opciones|Separadas|Por|Barra;Respuesta_Correcta;Tiempo_Estimado_Minutos/Segundos

Ejemplo:

SELECCION MULTIPLE;RECORDAR;¿Cuál es la capital de Francia?;París|Londres|Madrid|Roma;París;30    
VERDADERO/FALSO;ENTENDER;El sol gira alrededor de la Tierra.;Verdadero|Falso;Falso;15

Compilación y Ejecución

    1. Clonar el repositorio.
    2. Abrir el proyecto en IntelliJ IDEA.
    3. Ejecutar Main.java.
    4. Cargar el archivo de ítems desde la interfaz.

Licencia y Uso

Este proyecto es para fines educativos y puede ser usado como referencia.

Autores

Proyecto desarrollado por estudiantes de tercer año de Ingeniería en Informática.
Colaboradores: Lukas Flores (@Raizexs), David Vásquez.
