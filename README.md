# Sistema de Gestión de Intercambio Estudiantil
Gestión de Programas de Intercambio Estudiantil: Administración de convenios internacionales, registro de estudiantes, y manejo de trámites y documentación.

## Requisitos
- *Java JDK 11+* (recomendado 17)
- *Apache Maven 3.8+*
- (Opcional) *NetBeans / IntelliJ / VS Code* para ejecución desde IDE

Verifica versiones:
~
java -version
mvn -v
~

## Estructura del proyecto
- *Paquete principal:* com.mycompany.intercambioEstudiantil
- *Clase de entrada:* com.mycompany.intercambioEstudiantil.Main
- *Persistencia:* En memoria (no usa BD). Al cerrar el programa, se pierde el estado.

Estructura de carpetas esperada:
~
src/
 └─ main/
     └─ java/
