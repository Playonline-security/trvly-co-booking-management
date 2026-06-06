# Documentación del proceso de mantenimiento de software

**Proyecto:** trvly.co Booking Management  
**Asignatura:** Mantenimiento de Software  
**Tipo de mantenimiento:** Perfeccionamiento  
**Técnica aplicada:** Refactorización de código  

**Equipo:**
- Yeimy Nohemí Lozano Amaya
- Euclides Segundo Pérez Fernández
- Jonathan Uzcátegui González

**Repositorio:** Playonline-security/trvly-co-booking-management

---

## 1. Introducción

Este documento registra el proceso de mantenimiento realizado sobre **trvly.co Booking Management**, un sistema web full-stack basado en React, Spring Boot y PostgreSQL. La descripción funcional y arquitectónica del producto se encuentra en el `README.md` del repositorio; aquí se documenta únicamente la intervención de mantenimiento.

La actividad académica consistió en aplicar **mantenimiento perfectivo** mediante refactorización de código sobre un sistema ya operativo. No se incorporaron nuevas funcionalidades, no se migraron tecnologías ni se modificó el comportamiento observable del sistema. El objetivo fue mejorar la estructura interna del código para facilitar su lectura, evolución y sostenimiento futuro.

El equipo planificó nueve refactorizaciones (RF-01 a RF-09), las distribuyó por área técnica, las implementó de forma incremental y dejó evidencia del proceso en GitHub mediante ramas, commits y Pull Requests.

---

## 2. Técnica seleccionada

### Refactorización y mantenimiento de perfeccionamiento

La **refactorización** consiste en modificar la estructura interna del software sin alterar su comportamiento externo. Según la clasificación del mantenimiento de software, esta técnica pertenece al **mantenimiento de perfeccionamiento**: mejora atributos de calidad, legibilidad, organización y reutilización sobre un producto que ya cumple su función.

En trvly.co, la refactorización respondió a señales concretas de deuda técnica: fragmentos repetidos en varios archivos, componentes extensos con responsabilidades mezcladas y lógica de validación dispersa entre la interfaz y los controladores REST. Intervenir con cambios acotados y verificables resultó más adecuado que una reescritura completa, dado el alcance académico y el riesgo de regresión.

### Objetivos de mejora

El mantenimiento buscó reducir duplicación de código, centralizar reglas reutilizables, extraer utilidades y constantes desde componentes extensos, y estandarizar respuestas de error en el backend. La premisa rectora fue que ningún usuario ni consumidor de la API debiera percibir cambios en pantallas, flujos o contratos JSON tras la intervención.

---

## 3. Análisis realizado

Antes de modificar el código, el equipo revisó el repositorio identificando patrones que encarecían el mantenimiento futuro.

En el **frontend**, los dashboards de los roles Advisor, Supervisor y Admin repetían la misma estructura de tarjeta de navegación. Las páginas de clientes y reservas duplicaban expresiones de permisos basadas en roles (`ADMIN`, `SUPERVISOR`, `ADVISOR`). El componente `ReservationsPage.js` concentraba cerca de 660 líneas, incluyendo funciones para traducir estados de reserva y asignar estilos visuales, además del estado inicial del formulario de creación repetido en más de un punto. En `Login.js` y `ClientsPage.js` se observaron cadenas largas de clases CSS repetidas, y el manejo de errores HTTP seguía un patrón similar de extracción de mensajes desde las respuestas Axios.

En el **backend**, los controladores de clientes y autenticación construían respuestas de error repitiendo la creación de un `HashMap` con la clave `"message"` y un `ResponseEntity` con el código HTTP correspondiente. La búsqueda de clientes validaba el término con una condición inline, sin normalizar espacios en blanco.

Con base en este diagnóstico se definieron las refactorizaciones RF-01 a RF-09. Se descartaron intervenciones de mayor alcance como dividir por completo `ReservationsPage.js` o unificar mappers globales en servicios por tiempo y riesgo, optando por mejoras parciales pero con impacto claro en la mantenibilidad.

---

## 4. Planificación del mantenimiento

### Organización del trabajo

El equipo de tres integrantes distribuyó las refactorizaciones según afinidad con frontend o backend. **Jonathan Uzcátegui** asumió RF-01, RF-02 y RF-03: utilidades de estado de reserva, permisos centralizados y componente `DashboardCard`. **Yeimy Nohemí Lozano** abordó RF-04, RF-05 y RF-06: estilos de formulario, manejo de errores en la interfaz y estado inicial del formulario de reservas. **Euclides Segundo Pérez** concentró RF-07, RF-08 y RF-09 en el backend: simplificación de errores en `ClientController`, optimización de búsqueda de clientes y estandarización de respuestas REST.

Se acordó un orden de integración en el frontend: primero las utilidades base (Jonathan), después estilos y constantes (Yeimy), dado que ambos tocaban archivos compartidos como `ReservationsPage.js` y `ClientsPage.js`. El backend pudo avanzar en paralelo, con dependencia lógica entre RF-09 (`ApiResponseUtil`) y RF-07 (uso de la utilidad en el controlador de clientes).

### Flujo general

El proceso siguió cuatro etapas: análisis y acuerdo del plan, implementación en ramas feature locales, validación funcional y de compilación, e integración mediante Pull Requests hacia `develop`. Se definió un checklist de pruebas manuales para autenticación, dashboards, clientes y reservas, complementado con `mvn test` tras los cambios de backend.

### Git y GitHub

Se adoptó un flujo simplificado con rama `main` para entrega estable, `develop` para integración del equipo y ramas `refactor/...` por integrante o bloque de RF. Los commits utilizaron el prefijo `refactor(...)` para distinguir el mantenimiento perfectivo de correcciones o nuevas funcionalidades. Cada Pull Request documentó el problema abordado, los archivos modificados y el procedimiento de validación.

---

## 5. Refactorizaciones implementadas

### RF-01 — Utilidades de estado de reserva

**Objetivo.** Extraer la lógica de traducción y estilizado de estados de reserva desde un componente extenso hacia un módulo reutilizable.

**Módulo afectado.** `frontend/src/utils/reservationStatus.js` (nuevo) y `ReservationsPage.js`.

**Mejora aplicada.** Las funciones `getStatusLabel` y `getStatusColor` se trasladaron a un archivo de utilidades con exportación nombrada. El componente de reservas importa estas funciones en lugar de definirlas localmente.

**Resultado esperado.** Etiquetas en español y colores de badge idénticos al comportamiento anterior, con un único punto de cambio para estados de reserva.

---

### RF-02 — Centralización de permisos

**Objetivo.** Evitar la repetición de expresiones complejas sobre roles de usuario en distintas páginas.

**Módulo afectado.** `frontend/src/utils/permissions.js` (nuevo), `ClientsPage.js` y `ReservationsPage.js`.

**Mejora aplicada.** Se crearon funciones como `canDeleteRecords(user)` y `canEditReservations(user)` que encapsulan las reglas previamente escritas inline.

**Resultado esperado.** Misma visibilidad de acciones según rol, con lógica de permisos concentrada y más legible.

---

### RF-03 — Componente DashboardCard

**Objetivo.** Eliminar duplicación de markup en los dashboards de los tres roles.

**Módulo afectado.** `frontend/src/components/common/DashboardCard.js` (nuevo) y los archivos `AdvisorDashboard.js`, `SupervisorDashboard.js` y `AdminDashboard.js`.

**Mejora aplicada.** Se extrajo un componente parametrizable que recibe ruta, título, descripción, icono y estilos de borde.

**Resultado esperado.** Navegación y apariencia de dashboards sin cambios; menor cantidad de JSX repetido.

---

### RF-04 — Estilos centralizados en formularios

**Objetivo.** Unificar clases de estilo utilizadas en inputs, botones y campos modales.

**Módulo afectado.** `frontend/src/constants/formStyles.js` (nuevo), `Login.js` y `ClientsPage.js`.

**Mejora aplicada.** Se definieron constantes como `INPUT_BASE_CLASS`, `BUTTON_PRIMARY_CLASS` e `INPUT_MODAL_CLASS`, referenciadas desde los componentes.

**Resultado esperado.** Interfaz visual equivalente, con estilos mantenibles desde un solo archivo.

---

### RF-05 — Manejo de errores API en frontend

**Objetivo.** Centralizar la extracción de mensajes de error desde respuestas Axios.

**Módulo afectado.** `frontend/src/utils/apiErrors.js` (nuevo) y `ClientsPage.js`.

**Mejora aplicada.** Se implementó `getApiErrorMessage(error, defaultMessage)` y se adoptó en los manejadores de guardado y eliminación de clientes.

**Resultado esperado.** Mensajes de error equivalentes para el usuario, con lógica de parsing en un único lugar.

---

### RF-06 — Estado inicial del formulario de reservas

**Objetivo.** Definir en un solo lugar la estructura inicial del formulario de creación de reservas.

**Módulo afectado.** `frontend/src/constants/reservationForm.js` (nuevo) y `ReservationsPage.js`.

**Mejora aplicada.** Se creó la constante `INITIAL_RESERVATION_FORM`, utilizada en el `useState` inicial y en el reinicio del modal.

**Resultado esperado.** Comportamiento del formulario sin cambios; campos iniciales definidos una sola vez.

---

### RF-07 — Simplificación de errores en ClientController

**Objetivo.** Reducir duplicación en los bloques `catch` del controlador de clientes.

**Módulo afectado.** `ClientController.java`, en coordinación con `ApiResponseUtil` (RF-09).

**Mejora aplicada.** Los bloques que creaban manualmente un `HashMap` para errores se sustituyeron por llamadas a `ApiResponseUtil.messageResponse(...)`.

**Resultado esperado.** Respuestas JSON `{ "message": "..." }` con los mismos códigos HTTP; controlador más legible.

---

### RF-08 — Optimización de búsqueda de clientes

**Objetivo.** Mejorar claridad y robustez en la validación del parámetro de búsqueda.

**Módulo afectado.** `ClientController.java`.

**Mejora aplicada.** Se extrajo el método `hasSearchTerm(search)` y se normalizó el término con `trim()` antes de invocar el servicio.

**Resultado esperado.** Búsquedas consistentes ante espacios en blanco; validación expresada con mayor claridad.

---

### RF-09 — Estandarización de respuestas REST

**Objetivo.** Disponer de una utilidad común para construir respuestas de error uniformes.

**Módulo afectado.** `src/main/java/co/trvly/util/ApiResponseUtil.java` (nuevo), `AuthController.java` y soporte a RF-07.

**Mejora aplicada.** Se creó la clase `ApiResponseUtil` con el método `messageResponse`, adoptada en el login fallido y en los errores de clientes.

**Resultado esperado.** Patrón coherente de respuestas de error en el backend, preparado para evolucionar sin duplicar lógica.

---

## 6. Ejecución del mantenimiento

### Aplicación de cambios

Las modificaciones se realizaron de forma incremental en el entorno local de cada integrante. En el frontend, el patrón consistió en crear primero el módulo reutilizable de utilidad, constante o componente y después adaptar los archivos consumidores. En el backend, se implementó primero `ApiResponseUtil` para contar con la base de las refactorizaciones en controladores.

Se respetó el orden Jonathan → Yeimy en el frontend por la dependencia sobre archivos compartidos. Euclides avanzó en el backend de forma independiente, coordinando RF-09 antes de RF-07.

### Validaciones

Tras cada bloque de cambios se ejecutaron pruebas locales. En el frontend: inicio de sesión, navegación por dashboards, operaciones de clientes y visualización de estados en reservas, revisando la consola del navegador. En el backend: `mvn test` y pruebas manuales de escenarios de error (cliente duplicado, credenciales inválidas), verificando que las respuestas JSON conservaran el campo `message`.

### Integración y sincronización

La integración se realizó mediante Pull Requests hacia `develop`. Los conflictos en `ReservationsPage.js` y `ClientsPage.js` se resolvieron preservando los imports de todas las refactorizaciones aplicadas. Cada integrante sincronizó su rama con `develop` antes de abrir o actualizar su PR.

---

## 7. Evidencias del proceso

El control de versiones constituye la principal evidencia del mantenimiento. Cada refactorización quedó reflejada en commits con prefijo `refactor`, lo que permite distinguir este trabajo de correcciones o nuevas funcionalidades. Ejemplos de mensajes: *refactor(reservations): extract status utils*, *refactor(dashboard): add DashboardCard component*, *refactor(api): add ApiResponseUtil*.

Las ramas utilizadas `refactor/frontend-structure`, `refactor/ui-maintenance`, `refactor/api-standardization`muestran la distribución del trabajo y facilitan la revisión independiente de cada bloque. La rama `develop` concentró la integración progresiva; `main` se reservó para la versión estable de entrega.

Como evidencias complementarias, el equipo documentó capturas del historial de commits, la pestaña de ramas en GitHub, los Pull Requests mergeados y los resultados del checklist de pruebas. La correspondencia entre cada RF y su commit permite reconstruir la trazabilidad: qué problema se detectó, qué cambio se aplicó, quién lo implementó y cómo se verificó.

---

## 8. Resultados obtenidos

El frontend incorporó carpetas con convenciones más claras: `utils/` para lógica auxiliar, `constants/` para estilos y estados iniciales, y `components/common/` para elementos visuales compartidos. La duplicación más visible tarjetas de dashboard, expresiones de permisos, bloques de error en controladores quedó concentrada en módulos únicos.

Archivos extensos como `ReservationsPage.js` y `ClientController.java` ganaron legibilidad al delegar responsabilidades auxiliares. Nombres como `canDeleteRecords`, `getApiErrorMessage` o `hasSearchTerm` comunican intención con mayor claridad que bloques repetidos de condiciones o construcción manual de mapas.

Tras las validaciones acordadas, el equipo confirmó que los flujos de autenticación, dashboards por rol, CRUD de clientes, reservas y mensajes de error se mantuvieron equivalentes. No se registraron cambios funcionales deliberados.

Se reconocen limitaciones: `ReservationsPage.js` sigue siendo extenso; solo se intervino la porción de utilidades y estado inicial. El uso de `alert()` en el frontend permanece pendiente de evolución. En el backend, no se implementó un manejador global con `@ControllerAdvice`. Estas mejoras quedan como trabajo futuro.

---
*Documento elaborado como complemento del trabajo universitario de Mantenimiento de Software.*
