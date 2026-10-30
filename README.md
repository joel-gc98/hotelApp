# 🏨 HotelApp

Aplicación de escritorio desarrollada en Java con JavaFX, orientada a la gestión de habitaciones y reservas en un entorno hotelero.

El proyecto utiliza Maven para la gestión de dependencias y configuración de la compilación, junto con FXML para definir la interfaz gráfica. También incorpora AtlantaFX y ControlsFX para mejorar el diseño visual y las funcionalidades de validación de la aplicación.

## 📋 Descripción del proyecto

**HotelApp** es un proyecto de desarrollo de software que utiliza tecnologías del ecosistema Java para construir una aplicación de escritorio con interfaz gráfica.

Actualmente, dispone de una pantalla de gestión de habitaciones que permite consultar información como el número, tipo, capacidad, precio por noche y estado de cada habitación.

La aplicación obtiene los datos de un archivo XML y permite buscar habitaciones mediante su número, mostrando información sobre su disponibilidad.

El proyecto también contempla mejoras de accesibilidad y experiencia de usuario, prestando especial atención a la legibilidad, el contraste, las etiquetas de los controles y la navegación mediante teclado.

## 🛠️ Tecnologías utilizadas

| Tecnología    | Descripción                                                                      |
| ------------- | -------------------------------------------------------------------------------- |
| Java 17       | Lenguaje de programación principal.                                              |
| JavaFX 21.0.6 | Desarrollo de la interfaz gráfica.                                               |
| FXML          | Definición declarativa de la interfaz de usuario.                                |
| AtlantaFX     | Temas visuales para JavaFX.                                                      |
| ControlsFX    | Componentes y funcionalidades adicionales, incluyendo validación de formularios. |
| CSS           | Personalización de estilos, colores y componentes visuales.                      |
| Maven         | Gestión de dependencias y construcción del proyecto.                             |
| JUnit 5       | Framework configurado para realizar pruebas unitarias.                           |
| Git y GitHub  | Control de versiones y alojamiento del código fuente.                            |

## ⚙️ Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **JDK 17** o una versión compatible con la configuración del proyecto.
- **Maven**, si no vas a utilizar el wrapper incluido en el repositorio.
- Un IDE compatible con Java, como IntelliJ IDEA, Eclipse o Visual Studio Code con las extensiones correspondientes.
- Git para clonar el repositorio.

## 🚀 Instalación y ejecución

### 1. Clonar el repositorio

Abre una terminal y ejecuta:

```bash
git clone https://github.com/joel-gc98/hotelApp.git
```

### 2. Acceder al directorio

```bash
cd hotelApp
```

### 3. Ejecutar la aplicación

El proyecto incluye el Maven Wrapper, por lo que puedes ejecutarlo sin instalar Maven globalmente.

**En Windows:**

```bash
mvnw.cmd clean javafx:run
```

**En Linux o macOS:**

```bash
chmod +x mvnw
./mvnw clean javafx:run
```

También puedes importar el proyecto desde tu IDE utilizando el archivo `pom.xml` y ejecutar la clase principal `HelloApplication`.

## 📁 Estructura del proyecto

La estructura principal del repositorio es la siguiente:

```text
hotelApp/
├── .idea/
├── .mvn/
│   └── wrapper/
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java
│       │   └── com/
│       │       └── gui/
│       │           └── hotelapp/
│       │               ├── HelloApplication.java
│       │               ├── HotelController.java
│       │               ├── HotelRepository.java
│       │               ├── Habitacion.java
│       │               └── ...
│       └── resources/
│           └── reservas.xml
│           └── com.gui.hotelapp/
│                       ├── vista.fxml
│                       └── estilos.css                       
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

### Componentes principales

- **`HelloApplication.java`**: clase de entrada que inicia la aplicación JavaFX, configura el tema visual de AtlantaFX y carga la vista principal junto con su hoja de estilos CSS.
- **`HotelController.java`**: controla las interacciones de la interfaz, la búsqueda de habitaciones, la validación de los campos y la presentación de resultados.
- **`HotelRepository.java`**: encargado de recuperar la información de las habitaciones almacenada en el archivo XML.
- **`Habitacion.java`**: modelo que representa los datos de una habitación.
- **`vista.fxml`**: define la estructura de la interfaz gráfica mediante FXML.
- **`hotel.css`**: contiene los estilos personalizados de la aplicación.
- **`reservas.xml`**: archivo XML que almacena los datos utilizados por la aplicación.
- **`pom.xml`**: archivo de configuración de Maven, donde se definen las dependencias, la versión de Java y los plugins necesarios.
- **`module-info.java`**: declara los módulos y paquetes necesarios para el funcionamiento de la aplicación.

## ♿ Accesibilidad y mejoras de interfaz

Se ha trabajado en el rediseño de la pantalla de gestión de habitaciones con el objetivo de mejorar la experiencia de usuario y facilitar el acceso a la información.

### Problemas detectados inicialmente

- Textos con poca legibilidad.
- Contraste mejorable entre los elementos visuales.
- Campos de entrada con etiquetas poco claras.
- Distribución de componentes mejorable.
- Indicadores de foco poco destacados.
- Necesidad de facilitar la navegación sin utilizar el ratón.

### Mejoras implementadas

**1. Contraste y legibilidad**

- Incorporación de una paleta de colores basada en tonos azules, blancos y grises.
- Mejora de la diferenciación visual entre los distintos componentes.
- Personalización de los encabezados y filas de la tabla.
- Aumento de la jerarquía visual mediante títulos y subtítulos.

**2. Etiquetas y campos de entrada**

- Incorporación de etiquetas visibles y descriptivas.
- Asociación de etiquetas con sus respectivos campos.
- Uso de propiedades `accessibleText` y `accessibleHelp` para proporcionar información adicional sobre los controles.

**3. Navegación mediante teclado**

- Incorporación del atajo `Ctrl + F` para acceder directamente al campo de búsqueda.
- Configuración del botón Buscar como acción predeterminada.
- Uso de indicadores visuales para identificar el control que recibe el foco.
- Aprovechamiento de la navegación por teclado proporcionada por los controles JavaFX.

**4. Diferenciación de estados**

- Verde para habitaciones disponibles.
- Rojo para habitaciones ocupadas.
- Amarillo para habitaciones en mantenimiento.
- Incorporación de mensajes textuales que complementan la información transmitida mediante los colores.

**5. Organización de la interfaz**

- Separación visual del encabezado, el listado de habitaciones y el panel de búsqueda.
- Creación de una hoja de estilos CSS independiente.
- Mejora de los espacios y dimensiones de los componentes.

### Tecnologías aplicadas

- **AtlantaFX:** aplicación de un tema visual mediante `PrimerLight`.
- **ControlsFX:** incorporación de herramientas adicionales para la validación de campos.
- **CSS:** personalización de los estilos y estados visuales.
- **JavaFX:** gestión de eventos, controles y navegación mediante teclado.

### Evidencias visuales

Se incluirán capturas y prototipos para mostrar la evolución de la interfaz:

| Evidencia         | Descripción                                                  |
| ----------------- | ------------------------------------------------------------ |
| Wireframe inicial | Representación de la estructura original de la pantalla.     |
| Mockup mejorado   | Propuesta visual del rediseño.                               |
| Interfaz final    | Captura de la aplicación después de implementar las mejoras. |

Para consultar el proceso de rediseño y su justificación con mayor detalle, se puede consultar la documentación específica de accesibilidad en [`docs/ACCESIBILIDAD.md`](docs/ACCESIBILIDAD.md).

## 🧪 Pruebas

El proyecto tiene configuradas dependencias de JUnit 5 para el desarrollo de pruebas automatizadas.

Para ejecutar los tests disponibles:

**Windows:**

```bash
mvnw.cmd test
```

**Linux o macOS:**

```bash
./mvnw test
```

La existencia de las dependencias de JUnit no implica que ya haya una batería de pruebas implementada.

## 💻 Desarrollo

Para contribuir al desarrollo de la aplicación:

1. Clona el repositorio.
2. Importa el proyecto en tu IDE.
3. Comprueba la configuración de Java y Maven.
4. Realiza los cambios necesarios en el código fuente y la interfaz.
5. Ejecuta las pruebas disponibles.
6. Comprueba que la aplicación se inicia correctamente antes de enviar los cambios.

## 🔮 Posibles mejoras

Entre las posibles líneas de evolución del proyecto se encuentran:

- Desarrollo de nuevos módulos de gestión hotelera.
- Ampliación de las funcionalidades de gestión de reservas.
- Incorporación de formularios para registrar y modificar reservas.
- Separación y ampliación de la lógica de negocio.
- Persistencia de datos mediante una base de datos.
- Incorporación de pruebas unitarias y de integración.
- Ampliación de las funcionalidades de accesibilidad y validación.
- Mejora de la experiencia de usuario y del diseño visual.

Estas propuestas son posibles ampliaciones, no funcionalidades que se den por implementadas.

## 👨‍💻 Autor

**Joel GC98**

- GitHub: [@joel-gc98](https://github.com/joel-gc98)
- Repositorio: [hotelApp](https://github.com/joel-gc98/hotelApp)

## 📄 Licencia

No se ha especificado una licencia en esta documentación. Antes de distribuir o reutilizar el proyecto, conviene definir una licencia adecuada y añadir el archivo correspondiente al repositorio.
