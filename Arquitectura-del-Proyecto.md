# Arquitectura del Proyecto

Esta página es una guía de bienvenida para entender cómo está organizado
StoreFastFoodAbraCadabra y cómo recorrer el código al realizar un cambio.
No es necesario conocer todos los detalles de Java o Swing para empezar:
puedes seguir los ejemplos y avanzar capa por capa.

## ¿Qué hace la aplicación?

Es una aplicación de escritorio construida con Java Swing. Permite trabajar
con productos, clientes, pedidos y usuarios. La interfaz muestra ventanas,
tablas, botones y formularios; los datos se consultan o guardan en una base
de datos PostgreSQL.

Una idea útil para orientarse es que el proyecto separa la presentación de
las operaciones de negocio y del acceso a los datos. Cuando se pulsa un
botón, normalmente el formulario no debería ejecutar SQL directamente.
Delega el trabajo en otras clases especializadas.

## Cómo iniciar la aplicación

El punto de entrada es el método `main` de la clase `Vista.Main`. Este método
configura el aspecto visual de Swing y crea la ventana de inicio de sesión,
`Master`, dentro del hilo de eventos de Swing.

El recorrido de inicio de sesión es:

1. `Main` muestra `Master`.
2. La persona escribe usuario y contraseña y pulsa **Iniciar**.
3. `Master` llama a `ServiciosUsuario.Ingresar(...)`.
4. `ServiciosUsuario` delega la comprobación en `UserDAO`.
5. `UserDAO` abre una conexión mediante `Data.Conexion` y consulta PostgreSQL.
6. Si las credenciales son válidas, se abre `Inicio`; si no, se muestra un
   mensaje de error.
7. `Inicio` carga los datos de la pantalla y aplica permisos según el rol.

La disponibilidad de PostgreSQL es necesaria para iniciar sesión y para las
operaciones que leen o guardan datos. Si la conexión no está disponible, hay
que revisar el servidor, la red y la configuración de conexión en `Data`.
En la implementación actual, `ServiciosUsuario` crea su `UserDAO` como parte
de la inicialización de la clase, y el constructor del DAO también intenta
preparar la tabla de usuarios y el usuario inicial. Por eso un problema de
conexión puede aparecer al arrancar la aplicación, incluso antes de pulsar
**Iniciar sesión**.

## Mapa de carpetas y paquetes

El código de la aplicación se encuentra principalmente en
`src/main/java/`:

| Paquete | Qué contiene | Ejemplos |
|---|---|---|
| `projects.abracadabra.mavenproject2.Vista` | Ventanas y formularios Swing, además del punto de entrada de la aplicación. | `Main`, `Master`, `Inicio`, `AdministracionClientes` |
| `projects.abracadabra.mavenproject2.Vista.CRUD_Interface` | Clases intermedias que conectan los formularios con los servicios y adaptan datos a los componentes visuales. | `ClienteViewManager`, `ProductosViewManager`, `Permisos` |
| `projects.abracadabra.mavenproject2.Vista.SubPantallasDeAjustes` | Formularios auxiliares de la ventana de ajustes. | `AgregarMiembro`, `ViewAgregarMiembro` |
| `Servicios` | Operaciones disponibles para cada área y coordinación con los DAO. | `ServiciosCliente`, `ServiciosProductos`, `ServiciosUsuario` |
| `Data` | Conexión y acceso a la base de datos mediante DAO. | `Conexion`, `ClienteDAO`, `UserDAO` |
| `Entidad` | Clases que representan los datos principales de la aplicación. | `Cliente`, `Producto`, `Pedidos`, `Usuario` |

En términos sencillos, una **entidad** representa un dato del sistema. Por
ejemplo, un `Producto` tiene código, descripción, precio, cantidad, estado y
categoría. Un **DAO** es la clase que sabe cómo leer o guardar esos datos en
la base de datos.

## El recorrido de una acción

La mayoría de las acciones siguen este recorrido:

```text
Persona usuaria
    │ pulsa un botón o escribe en un campo
    ▼
Formulario Swing (Vista)
    │ llama a una clase gestora
    ▼
CRUD_Interface
    │ valida, prepara los datos o actualiza la tabla
    ▼
Servicios
    │ coordina la operación
    ▼
DAO (Data)
    │ ejecuta la consulta
    ▼
PostgreSQL
```

La respuesta recorre el camino de vuelta: el DAO devuelve datos al servicio,
el servicio los entrega al gestor y el gestor actualiza la tabla o los campos
que ve la persona.

No todas las acciones tienen que recorrer todas las capas. Por ejemplo, una
acción que solo cambia el estado visual de un botón no necesita consultar la
base de datos. Sin embargo, las operaciones que persisten datos deben pasar
por los servicios y DAO correspondientes.

## Responsabilidad de cada capa

### 1. Formularios: `Vista`

Los formularios muestran la interfaz y detectan las acciones de la persona.
En Swing, un método como `jButtonGuardarActionPerformed(...)` es el manejador
que se ejecuta cuando se pulsa un botón.

En vez de contener toda la lógica, el manejador obtiene los valores de los
controles y llama a una clase de `CRUD_Interface`. Así el código generado
para diseñar la ventana no se mezcla tanto con las operaciones de datos.

### 2. Gestores de interfaz: `CRUD_Interface`

Esta es la subcapa intermedia que separa el formulario de los servicios. Sus
clases pueden recibir controles Swing —por ejemplo `JTable`, `JTextField` o
`JSpinner`— y ayudan a:

- leer la fila seleccionada o los valores de los campos;
- comprobar datos básicos antes de procesarlos;
- convertir texto a números u otros tipos;
- construir entidades como `Cliente` o `Producto`;
- crear el modelo de una tabla y mostrar los resultados;
- delegar operaciones persistentes en los servicios.

Por eso un formulario puede limitarse a decir “se pulsó guardar” y delegar
en `ClienteViewManager` cómo validar y enviar los datos del cliente.

### 3. Servicios: `Servicios`

Los servicios ofrecen operaciones con nombres relacionados con las acciones
del sistema, como mostrar clientes, agregar productos o consultar pedidos.
Reciben o devuelven entidades y usan los DAO para acceder a los datos.

Como regla sencilla: un formulario no debería conocer cómo se construye una
consulta SQL. Esa responsabilidad pertenece a `Data`; el servicio actúa como
intermediario entre la operación de la aplicación y el DAO.

### 4. Acceso a datos: `Data`

En este proyecto, las clases DAO (Data Access Object) contienen las
operaciones de lectura y escritura de PostgreSQL. `Conexion` centraliza la
creación de conexiones. Los DAO preparan y ejecutan consultas y convierten
los resultados en entidades cuando corresponde.

La configuración de conexión debe manejarse con cuidado. No copies
contraseñas ni otros datos secretos a la Wiki, capturas o commits.

### 5. Entidades: `Entidad`

Las entidades son objetos Java que agrupan datos relacionados. Por ejemplo,
un objeto `Cliente` reúne su identificación, nombre, teléfono y dirección.
Esto permite pasar el cliente como una unidad entre el gestor, el servicio y
el DAO, en lugar de pasar cada valor por separado en todas las capas.

## ¿Qué significa CRUD?

CRUD son las cuatro operaciones básicas que se realizan sobre datos:

| Letra | Operación | Ejemplo en la aplicación |
|---|---|---|
| C — Create | Crear | Agregar un cliente o producto. |
| R — Read | Leer | Cargar o buscar productos para una tabla. |
| U — Update | Actualizar | Editar los datos de un producto. |
| D — Delete | Eliminar | Eliminar un cliente o producto. |

El nombre `CRUD_Interface` indica que estas clases coordinan acciones CRUD
relacionadas con la interfaz. No significa que todo el SQL esté dentro de ese
paquete: las operaciones de base de datos están en los DAO de `Data`.

## Clases principales de `CRUD_Interface`

| Clase | Responsabilidad |
|---|---|
| `ClienteViewManager` | Carga y actualiza la tabla de clientes; valida datos antes de agregar; permite buscar, eliminar, actualizar y seleccionar clientes. |
| `ProductosViewManager` | Carga y filtra productos; agrega, elimina y edita productos; adapta la selección de la tabla a objetos `Producto` y a los campos del formulario de edición. |
| `PedidosViewManager` | Agrega productos a pedidos, carga y filtra pedidos, calcula importes y coordina el cobro mediante `ServiciosPedidos`. |
| `UsuariosViewManager` | Carga la tabla de usuarios y actualiza la etiqueta que informa cuántos usuarios tienen ciertos roles de acceso. |
| `Permisos` | Carga el rol del usuario activo y aplica restricciones a tablas, campos y botones de la ventana principal. |
| `AdministradorDePermisos` | Controla botones de administración, como Ajustes y Administración de clientes, según el rol. |

## Ejemplo paso a paso: agregar un cliente

Este ejemplo muestra cómo colaboran las capas cuando se pulsa **Guardar** en
`AdministracionClientes`:

1. El formulario lee los campos de identificación, nombre, teléfono y
   dirección.
2. Su manejador llama a
   `ClienteViewManager.AgregarCliente(identificacion, nombres, telefono, direccion)`.
3. `ClienteViewManager` comprueba que los campos requeridos no estén vacíos y
   crea un objeto `Cliente`.
4. El gestor llama a `ServiciosCliente.AgregarCliente(cliente)`.
5. `ServiciosCliente` delega en `ClienteDAO`, que se encarga de guardar el
   cliente en la base de datos.
6. El formulario vuelve a llamar a `ClienteViewManager.CargarTabla(...)` para
   mostrar la lista actualizada.

Cada clase tiene así una tarea acotada. Si cambia la forma de presentar la
tabla, se revisa el formulario o el gestor de interfaz; si cambia cómo se
guarda el cliente, se revisa el servicio o el DAO, según corresponda.

## Cómo se muestran datos en una tabla

Una `JTable` es el componente Swing que muestra filas y columnas. En este
proyecto, los gestores suelen crear un `DefaultTableModel`, que contiene los
nombres de las columnas y las filas que se mostrarán.

Por ejemplo, `ProductosViewManager.CargarTabla(...)` pide los productos a
`ServiciosProductos`, agrega cada producto como una fila y asigna el modelo a
la tabla. Al agregar o eliminar un registro, volver a cargar el modelo permite
que la pantalla refleje los cambios.

La tabla también puede servir como entrada: al seleccionar una fila, el
gestor lee las celdas y obtiene el identificador necesario para editar o
eliminar el registro. Por eso conviene comprobar que exista una fila
seleccionada antes de leer sus valores.

## Pedidos: un flujo con varias entidades

Un pedido puede relacionar un cliente con uno o más productos. En `Inicio`,
`PedidosViewManager` toma el producto seleccionado, la cantidad indicada y el
cliente actual. El gestor comprueba condiciones básicas, construye los
objetos necesarios y llama a `ServiciosPedidos`.

Para cargar pedidos, el servicio obtiene la información mediante
`PedidosDAO`; el gestor prepara las filas para la tabla. Al cobrar, se
actualiza el estado del pedido y se vuelven a calcular o mostrar los importes.
Este ejemplo tiene más pasos que agregar un cliente, pero sigue el mismo
principio: cada capa trabaja en una parte del recorrido.

## Flujo de permisos en `Inicio`

Al crear la ventana principal, `Inicio` carga las tablas y después llama a
`Permisos.VerificarPermisos(...)`. El método `CargarRoles()` obtiene los
usuarios mediante `ServiciosUsuario.MostrarUsuarios()` y busca el nombre de
usuario activo para asignar su rol.

Con el rol disponible, `VerificarPermisos(...)` aplica las restricciones que
están implementadas actualmente:

- **Visualizador:** vacía y deshabilita las tablas indicadas, deshabilita los
  botones recibidos y deshabilita el campo de búsqueda.
- **Cajero:** deshabilita algunos botones según el texto visible del botón.
- **Administrador y Propietario:** `AdministradorDePermisos` habilita los
  botones de Ajustes y Administración de clientes; para otros roles, los
  deshabilita.

Para que una restricción pueda aplicarse, el componente debe pasarse al método
que la controla. En la implementación actual, el método de permisos recibe
botones `JToggleButton`; los controles `JButton` no quedan cubiertos por ese
parámetro, a menos que se manejen por separado. Además, las restricciones del
Cajero dependen de comparar textos visibles, por lo que un cambio de etiqueta
puede alterar qué botones se bloquean. Al modificar la pantalla o las reglas
de acceso, revisa tanto los componentes enviados a `VerificarPermisos(...)`
como los manejados por `AdministradorDePermisos`.

## Cómo orientarse para hacer un cambio

Antes de editar, identifica qué parte del recorrido necesitas cambiar:

| Si necesitas... | Empieza revisando... |
|---|---|
| Cambiar el aspecto de una ventana o los controles | El formulario correspondiente en `Vista` y su archivo `.form`. |
| Cambiar qué ocurre al pulsar un botón | El método `...ActionPerformed(...)` del formulario y el gestor al que llama. |
| Validar datos escritos o mostrar resultados en una tabla | La clase correspondiente de `CRUD_Interface`. |
| Cambiar una operación de aplicación, como agregar un pedido | El servicio correspondiente de `Servicios`. |
| Cambiar cómo se consulta o guarda un dato | El DAO correspondiente en `Data`. |
| Añadir o modificar los datos que representa un objeto | La entidad correspondiente en `Entidad` y las capas que la usan. |

Un método `static` se puede llamar usando el nombre de su clase, por ejemplo
`ClienteViewManager.CargarTabla(...)`, sin crear un objeto gestor. Es un patrón
que se usa en varias clases de `CRUD_Interface`.

## Consejos para colaborar

- Sigue una operación parecida que ya exista antes de añadir una nueva.
- Mantén pequeños los manejadores de eventos: obtén datos, delega y actualiza
  la interfaz.
- No pongas consultas SQL en los formularios ni en los gestores de interfaz.
- Usa entidades para representar conjuntos de datos relacionados.
- Valida las entradas antes de convertirlas. Por ejemplo, un campo de precio
  llega como texto y debe convertirse a número.
- Muestra errores que ayuden a entender qué falló; no ocultes una operación
  fallida como si hubiera tenido éxito.
- Después de una operación que modifica datos, comprueba si hay que recargar
  la tabla o actualizar etiquetas e importes.
- Los formularios de NetBeans tienen archivos `.form` que describen su diseño.
  Al cambiar controles o eventos, mantén sincronizados el formulario visual y
  su código Java.
- No incluyas contraseñas ni datos de acceso a la base de datos en
  documentación, capturas o commits.

## Consideraciones de diseño

- Mantener en los formularios principalmente la definición visual y la
  delegación de eventos.
- Colocar en `CRUD_Interface` la adaptación entre controles Swing y los datos
  que consume la aplicación.
- Mantener en `Servicios` las operaciones del sistema y delegar la
  persistencia en los DAO.
- Mantener las entidades libres de dependencias con los formularios.
- Cuando una operación de `CRUD_Interface` necesite datos persistentes, usar
  los servicios y evitar ejecutar consultas SQL desde el formulario.
- Al agregar una acción nueva, revisar el flujo completo:
  **formulario → gestor de interfaz → servicio → DAO**, y actualizar la vista
  después de una operación de escritura cuando corresponda.

## Ubicación del código

El código de producción está bajo `src/main/java/`. Las clases de Swing están
bajo:

```text
src/main/java/projects/abracadabra/mavenproject2/Vista/
```

Y la subcapa gestora de la interfaz está en:

```text
src/main/java/projects/abracadabra/mavenproject2/Vista/CRUD_Interface/
```
