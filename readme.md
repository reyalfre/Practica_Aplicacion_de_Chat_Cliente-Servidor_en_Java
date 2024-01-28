Práctica: Aplicación de Chat Cliente-Servidor en Java


Objetivo:


Desarrollar una aplicación de chat en Java que utilice sockets TCP para permitir la comunicación
en tiempo real entre múltiples clientes a través de un servidor central.


Tareas:


1. Desarrollo del Servidor de Chat:


   • Implementa un servidor de chat utilizando ServerSocket que pueda aceptar múltiples
   conexiones de clientes.


   • Gestiona cada cliente en un hilo separado.


   • Permite que el servidor reenvíe los mensajes recibidos de un cliente a todos los demás
   clientes.


2. Desarrollo del Cliente de Chat:


   • Crea una aplicación cliente que se conecte al servidor utilizando la clase Socket.


   • Piensa cómo podemos hacer que el usuario pueda enviar mensajes y también reciba
   mensajes del servidor simultáneamente.


   • Haz que en la consola se muestren de diferente manera los mensajes que envía el cliente, y
   los que recibe de los demás. Al estilo de cómo lo muestran las aplicaciones de mensajería
   actuales (tus mensajes en un lado, los de los demás al otro).


   • También deberá aparecer el Nick/nombre de usuario de la persona que está escribiendo. Por
   ejemplo: [paco]: Hola chicos, ¿cómo va?


3. Gestión de Usuarios y Mensajes:


   • Implementa la funcionalidad para que los usuarios puedan ingresar y mostrar su nombre de
   usuario, y eso se envíe al servidor.


   • Permite que los usuarios puedan salir del chat cuando escriban alguna palabra determinada.


   • Asegúrate de que el servidor maneje adecuadamente la desconexión de los usuarios
   (controlada o incontrolada), y alerte al resto de clientes que el usuario ha salido de la sala
   de chat.


4. Pruebas y Depuración:


   • Realiza pruebas con múltiples clientes conectándose al servidor simultáneamente.


   • Asegúrate de que los mensajes se distribuyen correctamente entre todos los clientes
   conectados.


   • Prueba casos de desconexión inesperada y verifica la estabilidad del servidor.
   Consideraciones Adicionales:


   Considera la posibilidad de manejar errores y excepciones de red de manera efectiva.


   Entregables:


   Código fuente del proyecto.


   Si se realiza correctamente la funcionalidad descrita anteriormente, se obtendrá un 5 en la
   práctica.


   Si se desea obtener más calificación, se deberán realizar CORRECTAMENTE alguna/s de las
   siguientes funcionalidades:


   Mensajes en diferentes colores (2 puntos)


   Los mensajes de los usuarios están coloreados. A cada usuario se le asignará un color fijo, y
   conservará ese color a lo largo de la sesión. Si el usuario sale del chat, ese color quedará libre
   para el siguiente usuario que entre. Si el número de usuarios supera la cantidad de colores
   disponible, no se le asignará color.


   Envío de mensajes privados (2 puntos)


   En el modo “normal”, todos los mensajes se envían a todos los participantes del chat. Con esta
   ampliación deberemos permitir que, usando el carácter “@” acompañado del Nick del usuario,
   se pueda conseguir que sólo el usuario al que estamos mencionando pueda visualizar dicho
   mensaje.
   Por ejemplo, el cliente “Boberto” escribe un mensaje privado a Paco. Esto es lo que vería él:
   [paco]: Hola chicos cómo va?
   [boberto]: @paco hola Paco! Te escribo por privado
   Y “Paco”, recibiría:
   [boberto PRIVADO]: hola Paco! Te escribo por privado
   [paco]: @boberto Qué pasa golfo!


   Envío de chat anterior (1 puntos)


   El servidor deberá ir almacenando los chats enviados en la sesión, y cuando un cliente se
   incorpora, debería enviarle sólo los últimos 50 mensajes hablados hasta ese momento en la
   sesión mientras él no estaba. De esa forma podrá ver lo que se estaba hablando en los últimos
   mensajes.