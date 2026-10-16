# Diagramas de clases

La clase `Main` solo arranca el programa y no forma parte de los diagramas.

## Jerarquía de cabeceras y composición del paquete

![Jerarquía de cabeceras](diagrama-jerarquia.png)

```mermaid
classDiagram
    class Cabecera {
        <<abstract>>
        #nombre : String
        #campos : Map
        #validar()
        +describir() String
    }
    class CabeceraTransporte {
        <<abstract>>
        +usaPuertos() boolean
        +getPuertoOrigen() int
        +getPuertoDestino() int
        +getFlags() String
    }
    class CabeceraAPP {
        +esConsulta() boolean
        +esRespuesta() boolean
    }
    class Paquete {
        -numeroLinea : int
        +describir() String
    }
    Cabecera <|-- CabeceraTransporte
    Cabecera <|-- CabeceraEthernet
    Cabecera <|-- CabeceraIP
    Cabecera <|-- CabeceraAPP
    CabeceraTransporte <|-- CabeceraTCP
    CabeceraTransporte <|-- CabeceraUDP
    CabeceraTransporte <|-- CabeceraICMP
    Paquete *-- CabeceraEthernet
    Paquete *-- CabeceraIP
    Paquete *-- CabeceraTransporte
    Paquete *-- CabeceraAPP
```

## Colaboración entre clases

![Colaboración entre clases](diagrama-colaboracion.png)

```mermaid
classDiagram
    class LocalizadorArchivo {
        +localizar() Path
    }
    class LectorArchivo {
        +leerLineas() List~String~
    }
    class AnalizadorPaquetes {
        +analizar(List lineas)
        +analizarLinea(int numero, String linea) Paquete
    }
    class Estadisticas {
        +contarTransporte(String tipo) int
        +masFrecuentes(Map m) List
        +buscarPorIp(String ip) List
    }
    class GeneradorReporte {
        +detalle(List lista) String
        +resumen() String
        +reporteCompleto(AnalizadorPaquetes an) String
        +exportar(String ruta, String contenido)
    }
    class PaqueteInvalidoException
    LocalizadorArchivo --> LectorArchivo : ruta
    LectorArchivo --> AnalizadorPaquetes : líneas
    AnalizadorPaquetes ..> Paquete : crea
    AnalizadorPaquetes ..> PaqueteInvalidoException : captura
    Estadisticas ..> Paquete : consulta
    GeneradorReporte --> Estadisticas : usa
    GeneradorReporte ..> AnalizadorPaquetes : incidencias
```

Para regenerar las imágenes a partir de los archivos `.dot` (requiere Graphviz):

```
dot -Tpng -Gdpi=150 diagrama-jerarquia.dot -o diagrama-jerarquia.png
dot -Tpng -Gdpi=150 diagrama-colaboracion.dot -o diagrama-colaboracion.png
```
