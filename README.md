# 🏨 HotelApp

Aplicación de escritorio desarrollada en Java con JavaFX, orientada a la creación de una solución informática para el entorno hotelero.

El proyecto utiliza Maven para la gestión de dependencias y la configuración de la compilación, junto con FXML para definir la interfaz gráfica de usuario.

## 📋 Descripción del proyecto

**HotelApp** es un proyecto de desarrollo de software que utiliza tecnologías del ecosistema Java para construir una aplicación de escritorio con interfaz gráfica.

Su objetivo es servir como base para una aplicación del ámbito hotelero, con una estructura preparada para desarrollar la interfaz y ampliar la lógica de negocio según las necesidades del proyecto.

## 🛠️ Tecnologías utilizadas

| Tecnología | Descripción |
|---|---|
| Java 17 | Lenguaje de programación principal. |
| JavaFX 21.0.6 | Desarrollo de la interfaz gráfica. |
| FXML | Definición declarativa de la interfaz de usuario. |
| Maven | Gestión de dependencias y construcción del proyecto. |
| JUnit 5 | Framework configurado para realizar pruebas unitarias. |
| Git y GitHub | Control de versiones y alojamiento del código fuente. |

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
│       └── resources/
│       │     └── com/
│       │         └── gui/
│       │             └── hotelapp/
│       │                 └── reservas.xml
│       └── java/
│           └── com/
│               └── gui/
│                   └── hotelapp/
│                       ├── HelloApplication.java
│                       └── ...
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

### Componentes principales

- **`HelloApplication.java`**: clase de entrada que inicia la aplicación JavaFX, carga la vista `vista.fxml` y configura la ventana principal.
- **`pom.xml`**: archivo de configuración de Maven, donde se definen las dependencias, la versión de Java y los plugins necesarios.
- **`mvnw` y `mvnw.cmd`**: scripts para ejecutar Maven en sistemas Unix y Windows, respectivamente.
- **`.mvn/wrapper/`**: configuración del Maven Wrapper.

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

- Desarrollo de módulos de gestión hotelera.
- Implementación de formularios y validaciones.
- Separación de la interfaz gráfica y la lógica de negocio.
- Persistencia de datos mediante una base de datos.
- Incorporación de pruebas unitarias y de integración.
- Mejora de la experiencia de usuario y del diseño visual.

Estas propuestas son posibles ampliaciones, no funcionalidades que se den por implementadas.

## 👨‍💻 Autor

**Joel GC98**

- GitHub: [@joel-gc98](https://github.com/joel-gc98)
- Repositorio: [hotelApp](https://github.com/joel-gc98/hotelApp)

## 📄 Licencia

No se ha especificado una licencia en esta documentación. Antes de distribuir o reutilizar el proyecto, conviene definir una licencia adecuada y añadir el archivo correspondiente al repositorio.
