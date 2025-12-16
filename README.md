# Práctica - Iteración 2: Juego de La Escoba

## Autoras
- Marta Pérez Alonso  
- Elisa Martínez Lafuente

---

## Índice
- [Práctica - Iteración 2: Juego de La Escoba](#práctica---iteración-2-juego-de-la-escoba)
  - [Autoras](#autoras)
  - [Índice](#índice)
  - [Descripción](#descripción)
    - [Novedades de la Iteración 2](#novedades-de-la-iteración-2)
  - [Tiempo empleado](#tiempo-empleado)
  - [Estructura del proyecto](#estructura-del-proyecto)
      - [Modelo de Cartas y Baraja](#modelo-de-cartas-y-baraja)
      - [Lógica del Juego](#lógica-del-juego)
      - [Entidades de Partida (Iteración 2)](#entidades-de-partida-iteración-2)
      - [Servicios (Iteración 2)](#servicios-iteración-2)
      - [Ejecutores y Adaptadores](#ejecutores-y-adaptadores)
    - [Tests Principales](#tests-principales)
  - [Ratio code to test](#ratio-code-to-test)
    - [Ratio code to test por clase y paquete](#ratio-code-to-test-por-clase-y-paquete)
        - [Paquete: uva.tds.ejecutores](#paquete-uvatdsejecutores)
        - [Paquete: uva.tds.entidades](#paquete-uvatdsentidades)
        - [Paquete: uva.tds.servicios](#paquete-uvatdsservicios)
  - [Aplicación de Herramientas IAG](#aplicación-de-herramientas-iag)
    - [Herramientas utilizadas:](#herramientas-utilizadas)
      - [GitHub Copilot](#github-copilot)
      - [Claude](#claude)
      - [ChatGPT](#chatgpt)
  - [Cobertura de tests y limitaciones](#cobertura-de-tests-y-limitaciones)
  - [Estructura de directorios](#estructura-de-directorios)


---

## Descripción

Proyecto que implementa la lógica del juego de cartas **La Escoba** con una baraja española de 40 cartas siguiendo la metodología **Test-Driven Development (TDD)**.
La Iteración 2 añade servicios de gestión de partidas y soporte para juego multijugador (coordinación mediante una interfaz de servicio remoto).

### Novedades de la Iteración 2
- ServicioPartida:
  - Validación de partidas.
  - Cálculo de puntos y generación de resúmenes.
  - Estadísticas por rango de fechas y por jugador.
  - Gestión de partidas mediante `RepositorioPartida`.
- ServicioJuegoMultijugador:
  - Gestión de salas (públicas/privadas), unión a sala, listado de salas.
  - Flujo de partida (inicialización, reparto, jugadas).
  - Envío/recepción de jugadas y sincronización local.
- Refactor y mejoras:
  - Desacoplamiento de la lectura JSON de la ejecución de la partida.
  - `AdaptadorCartaJson` para aislar el parseo de cartas.
  - Nuevas entidades: `Partida`, `ResumenPartida`, `AdaptadorPartida`, `Sala`.
  - Tests de aislamiento con mocks para servicios.
  - Objetivo del 100% de cobertura.


---

## Tiempo empleado

- Iteración 1: 23 h 44 min (23.73 h-persona)
- Iteración 2: 25 h 35 min (27.08 h-persona)

Desglose (Iteración 2):
- Modelo de datos (Partida, ResumenPartida, Sala, Adaptadores):  4h20m
- ServicioPartida: 8h20m
- ServicioJuegoMultijugador: 5h30m
- Desacoplamiento y aislamiento (EjecutarPartida, AdaptadorCartaJson): 3h20m
- Cobertura: 5h35m

**Elisa**: ~10.5 h **Marta**: ~16.5 h 

*El tiempo se ha obtenido a partir de las issues registradas en GitLab correspondientes a cada Iteración.*


---


## Estructura del proyecto
#### Modelo de Cartas y Baraja
- **`Palo`**: Enumeración con los cuatro palos de la baraja española (OROS, COPAS, ESPADAS, BASTOS)
- **`Carta`**: Representa una carta individual con palo e índice (1-7, 10-12)
- **`Baraja`**: Baraja española de 40 cartas con funcionalidad de robo

#### Lógica del Juego
- **`Jugador`**: Representa a un jugador con su nombre, mano, cartas capturadas y escobas
- **`GestorPartida`**: Coordinador principal de la partida: Gestiona el flujo completo del juego. Controla rondas, turnos y finalización con reglas, y calcula puntuaciones finales.
- **`GestorRonda`**: Gestiona el estado de una ronda específica
- **`GestorTurno`**: Controla la alternancia de turnos entre jugadores

#### Entidades de Partida (Iteración 2)
- **`Partida`**: Encapsula toda la información de una partida: id, fecha, jugadores, rondas con jugadas, mesa inicial, estado y resumen
- **`ResumenPartida`**: Contiene el resumen de resultados de una partida (escobas, cartas, oros, sietes, guindis, puntos, ganador)
- **`AdaptadorPartida`**: Convierte el estado de un GestorPartida a ResumenPartida
- **`Sala`**: Representa una sala multijugador

#### Servicios (Iteración 2)
- **`ServicioPartida`**: Gestión completa de partidas: Validación y cálculo de puntos, Resúmenes y estadísticas, Persistencia mediante RepositorioPartida (mock)
  
- **`ServicioJuegoMultijugador`**: Coordinación de partidas multijugador: Gestión de salas (crear, unirse, listar), Inicialización y repartos, Envío/recepción de jugadas, Sincronización de estado mediante ServicioMultijugador (mock)

#### Ejecutores y Adaptadores
- **`LectorPartidaJSON`**: Lee y parsea archivos JSON con partidas pregrabadas
- **`EjecutarPartida`**: Ejecuta partidas completas desde objetos Partida (desacoplado del JSON)
- **`AdaptadorCartaJson`**: Adapta la representación JSON de cartas a objetos Carta (aislamiento)

### Tests Principales

El proyecto cuenta con una suite completa de tests organizados por funcionalidad, incluyendo:

**Tests de la Iteración 1:**
- `BarajaTest`, `CartaTest`, `JugadorTest`
- `GestorPartidaTest`, `GestorRondaTest`, `GestorTurnoTest`
- `LectorPartidaJSONTest`, `EjecutarPartidaTest`

**Tests de la Iteración 2:**
- `PartidaTest`, `ResumenPartidaTest`, `SalaTest`
- `AdaptadorPartidaTest`, `AdaptadorCartaJsonTest`
- `ServicioPartidaTest` (tests en aislamiento con mocks)
- `ServicioJuegoMultijugadorTest` (tests en aislamiento con mocks)
- Tests con tag `@Tag("Cobertura")` en los test que se han hecho para alcanzar 100% de cobertura

---

## Ratio code to test
### Ratio code to test por clase y paquete

##### Paquete: uva.tds.ejecutores

| Clase               | LOC prod | LOC test | Ratio (1:x) |
|---------------------|----------|----------|--------------|
| `AdaptadorCartaJson`  | 60       | 79       | 1:1.32     |
| `EjecutarPartida`     | 103      | 368      | 1 : 3.57     |
| `LectorPartidaJSON`   | 311      | 383      | 1 : 1.23     |
| **Total**             | 474      | 830      | 1 : 1.75     |


##### Paquete: uva.tds.entidades

| Clase          | LOC prod | LOC test | Ratio (1:x) |
|----------------|----------|----------|--------------|
| `AdaptadorPartida` | 49     | 69       | 1 : 1.41     |
| `Baraja`         | 74       | 75       | 1 : 1.01     |
| `Carta`          | 120      | 186      | 1 : 1.55     |
| `Jugador`        | 166      | 260      | 1 : 1.57     |
| `Partida`        | 196      | 287      | 1 : 1.46     |
| `ResumenPartida` | 220      | 253      | 1 : 1.15     |
| `Sala`           | 41       | 75       | 1 : 1.83     |
| **Total**        | 866      | 1205     | 1 : 1.39     |


##### Paquete: uva.tds.servicios
| Clase                       | LOC prod | LOC test   | Ratio (1:x) |
|-----------------------------|----------|------------|--------------|
| `GestorPartida`               | 443      | 196 + 732  | 1 : 2.06     |
| `GestorRonda`                 | 113      | 148        | 1 : 1.31     |
| `GestorTurno`                 | 113      | 189        | 1 : 1.67     |
| `ServicioJuegoMultijugador`   | 325      | 1172       | 1 : 3.60     |
| `ServicioPartida`             | 438      | 1136       | 1 : 2.60     |
| **Total**                     | 1432     | 3433       | 1 : 2.40     |

---

## Aplicación de Herramientas IAG

### Herramientas utilizadas:

#### GitHub Copilot
- **Modo Editor (autocompletado):**
  - Autocompletado de estructuras repetitivas (validaciones, bucles)
  - Sugerencias de nombres consistentes para tests
  - Generación de implementaciones básicas de métodos (como getters, )
  - Creación de Javadoc.
  
- **Copilot Chat:**
  - Resolución de dudas sobre el uso de EasyMock para crear mocks
  - Sugerencias para refactorización de código duplicado
  - Explicación de errores y warnings durante el desarrollo
  - Asistencia en los tests de cobertura para crear tests que cubran ramas específicas

#### Claude
- 
  - Sugerencias para mejorar la organización del código
  - Ayuda con la implementación de algoritmos de ordenación para estadísticas
  - Asistencia en el formato del README.md
 
 #### ChatGPT
 - 
    - Resolución de problemas con la configuración del plugin JaCoCo
    - Lógica de algunos métodos complejos en los servicios
    - Generación de ejemplos de JSON para tests

---

## Cobertura de tests y limitaciones
Hemos intentado maximizar la cobertura, pero hay ramas que no es posible alcanzar sin acceder o alterar el estado interno de las clases. Por ejemplo, la comprobación !creadorSalaActual.equals(nombreJugadorLocal) protege la regla “solo el creador puede repartir”; cubrir esa rama exigiría forzar campos privados. Sin embargo, hemos decidido mantener este tipo de condiciones para garantizar la integridad de la lógica de negocio y cumplir los requisitos de la práctica.



## Estructura de directorios
```
tds_practica_grupob/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── uva/
│   │           └── tds/
│   │               ├── entidades/
│   │               │   ├── AdaptadorPartida.java
│   │               │   ├── Baraja.java
│   │               │   ├── Carta.java
│   │               │   ├── Jugador.java
│   │               │   ├── Palo.java
│   │               │   ├── Partida.java
│   │               │   ├── ResumenPartida.java
│   │               │   └── Sala.java
│   │               ├── interfaces/
│   │               │   ├── RepositorioPartida.java
│   │               │   └── ServicioMultijugador.java
│   │               ├── servicios/
│   │               │   ├── GestorPartida.java
│   │               │   ├── GestorRonda.java
│   │               │   ├── GestorTurno.java
│   │               │   ├── ServicioJuegoMultijugador.java
│   │               │   └── ServicioPartida.java
│   │               └── ejecutores/
│   │                   ├── AdaptadorCartaJson.java
│   │                   ├── EjecutarPartida.java
│   │                   └── LectorPartidaJSON.java
│   └── test/
│       ├── java/
│       │   └── uva/
│       │       └── tds/
│       │           └── [Clases de test]
│       └── resources/
│           └── [Archivos JSON de prueba]
├── target/
│   └── site/
│       └── jacoco/
│           └── index.html
├── pom.xml
├── .gitignore
└── README.md
```


