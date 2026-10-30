# Rediseño de interfaz y mejoras de accesibilidad

## 1. Objetivo

El objetivo de esta mejora es rediseñar la pantalla de gestión de habitaciones de HotelApp para facilitar su utilización, mejorar la legibilidad de la información y permitir que un mayor número de usuarios pueda interactuar con la aplicación.

Se han aplicado principios de accesibilidad y diseño de interfaces, utilizando JavaFX, AtlantaFX y ControlsFX.

## 2. Problemas detectados en la interfaz inicial

En la versión inicial se identificaron los siguientes problemas:

- Textos y elementos visuales con poca diferenciación.
- Campos de entrada con etiquetas poco visibles.
- Distribución de componentes mejorable.
- Escasa diferenciación visual de los resultados.
- Navegación mediante teclado mejorable.
- Falta de personalización de los estados de foco.

## 3. Tecnologías utilizadas

- **JavaFX:** desarrollo de la interfaz gráfica.
- **AtlantaFX:** aplicación de un tema visual moderno mediante PrimerLight.
- **ControlsFX:** funcionalidades adicionales de validación.
- **CSS:** personalización de colores, tipografías, bordes y estados visuales.
- **FXML:** reorganización de los componentes de la interfaz.

## 4. Mejoras implementadas

### 4.1. Contraste y legibilidad

Se ha aplicado una paleta de colores basada en tonos azules, blancos y grises para mejorar la diferenciación entre elementos.

También se han aumentado los tamaños de algunos textos y se han personalizado los encabezados de la tabla.

### 4.2. Etiquetas y campos de entrada

Se han incorporado etiquetas visibles para identificar los campos de entrada.

Se han añadido propiedades de accesibilidad como accessibleText y accessibleHelp para proporcionar información adicional sobre los controles.

### 4.3. Navegación mediante teclado

Se ha incorporado el atajo Ctrl + F para acceder directamente al campo de búsqueda.

También se ha configurado el botón Buscar como acción predeterminada y se han mejorado los indicadores visuales de foco.

### 4.4. Diferenciación de estados

Los resultados de las consultas se presentan mediante estilos diferenciados:

- Verde: habitación disponible.
- Rojo: habitación ocupada.
- Amarillo: habitación en mantenimiento.

Los mensajes incluyen también información textual para evitar depender exclusivamente de los colores.

### 4.5. Organización visual

La pantalla se ha reorganizado en tres zonas principales:

- Encabezado de la aplicación.
- Tabla de habitaciones.
- Panel de consulta y resultados.

Esta distribución permite identificar más fácilmente las diferentes funcionalidades.

## 5. Justificación de las mejoras de accesibilidad

| Mejora | Beneficio |
|---|---|
| Mayor contraste | Facilita la lectura de los textos. |
| Etiquetas visibles | Permite identificar los campos. |
| Textos accesibles | Proporciona información adicional a tecnologías de asistencia. |
| Navegación por teclado | Reduce la dependencia del ratón. |
| Indicadores de foco | Facilita localizar el control activo. |
| Mensajes diferenciados | Mejora la comprensión de los resultados. |

## 6. Conceptos de diseño aplicados

### Accesibilidad de una interfaz

Consiste en diseñar aplicaciones que puedan ser utilizadas por personas con diferentes capacidades y necesidades.

### Wireframe

Es un esquema inicial de baja fidelidad que representa la estructura y distribución de los componentes sin centrarse en los detalles visuales.

### Mockup

Es una representación visual de mayor fidelidad que muestra cómo será la interfaz final, incluyendo colores, tipografías y estilos.

### Contraste y navegación por teclado

El contraste permite distinguir correctamente los elementos visuales, mientras que la navegación por teclado permite utilizar la aplicación sin depender de un ratón.

## 7. Evidencias visuales

Se incluirán capturas de la interfaz original y de la versión mejorada para mostrar la evolución del diseño.

También se incorporarán el wireframe inicial y el mockup de la propuesta mejorada.

## 8. Conclusión

Las mejoras realizadas permiten disponer de una interfaz más organizada, legible y sencilla de utilizar.

La incorporación de AtlantaFX, ControlsFX y CSS facilita la personalización visual y la validación de los controles, mientras que las mejoras de accesibilidad favorecen una interacción más clara mediante teclado y tecnologías de asistencia.