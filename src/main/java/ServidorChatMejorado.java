import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServidorChatMejorado {

    private static List<ClienteHandler> clientes = new CopyOnWriteArrayList<>();
    private static Queue<String> mensajesRecientes = new LinkedList<>();

    private static Set<String> coloresAsignados = new HashSet<>();

    public static final String RESET = "\033[0m";  // Text Reset
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE
    private static final String[] COLORES_DISPONIBLES = {RESET, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE};
    public static void main(String[] args) {
        final int PUERTO = 12345;

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de chat iniciado en el puerto " + PUERTO);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                ClienteHandler clienteHandler = new ClienteHandler(socketCliente);
                clientes.add(clienteHandler);
                new Thread(clienteHandler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClienteHandler implements Runnable {
        private Socket socket;
        private PrintWriter salida;
        private String nombreUsuario;
        private String color="";

        public ClienteHandler(Socket socket) {
            this.socket = socket;
            try {
                this.salida = new PrintWriter(socket.getOutputStream(), true);
                Scanner entrada = new Scanner(socket.getInputStream());
                this.nombreUsuario = entrada.nextLine();

                // Asignar color al usuario
                asignarColor();

                enviarMensajesRecientes();
                enviarMensaje("[Servidor]: ¡Bienvenido, " + nombreUsuario + "!");
                enviarMensaje("[Servidor]: Para salir del chat, escribe 'salir'.");
             //   enviarMensaje("[Servidor]: Otros usuarios en el chat: " + obtenerUsuariosConectados());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void asignarColor() {
            for (String color : COLORES_DISPONIBLES) {
                if (!coloresAsignados.contains(color)) {
                    this.color = color;
                    coloresAsignados.add(color);
                    break;
                }
            }
        }

        @Override
        public void run() {
            try (Scanner entrada = new Scanner(socket.getInputStream())) {
                while (true) {
                    try {
                        String mensajeCliente = entrada.nextLine();

                        // Comprobar si el usuario quiere salir del chat
                        if (mensajeCliente.equalsIgnoreCase("salir")) {
                            salirDelChat();
                            break;
                        }

                        // Comprobar si el mensaje es privado
                        if (mensajeCliente.startsWith("@")) {
                            procesarMensajePrivado(mensajeCliente);
                        } else {
                            // Reenviar el mensaje a todos los demás clientes
                            broadcastMensaje(color + "[" + nombreUsuario + "]: " + mensajeCliente);
                            // Almacenar mensaje
                            almacenarMensaje(color + "[" + nombreUsuario + "]: " + mensajeCliente);
                        }
                    } catch (NoSuchElementException e) {
                        // El cliente se desconectó inesperadamente
                        salirDelChat();
                        break;
                    }
                }
            } catch (IOException e) {
                salirDelChat();
            } finally {
                // Liberar el color asignado cuando el usuario sale del chat
                coloresAsignados.remove(color);
            }
        }

        private void enviarMensajesRecientes() {
            for (String mensaje : mensajesRecientes) {
                salida.println(mensaje);
            }
        }

        private void almacenarMensaje(String mensaje) {
            // Almacenar el mensaje en la cola de mensajes recientes
            mensajesRecientes.offer(mensaje);

            // Mantener un máximo de 50 mensajes recientes
            while (mensajesRecientes.size() > 50) {
                mensajesRecientes.poll();
            }
        }

        public void enviarMensaje(String mensaje) {
            salida.println(mensaje);
        }

        private void broadcastMensaje(String mensaje) {
            for (ClienteHandler cliente : clientes) {
                cliente.enviarMensaje(mensaje);
            }
        }

        private void salirDelChat() {
            Iterator<ClienteHandler> iterator = clientes.iterator();
            while (iterator.hasNext()) {
                ClienteHandler cliente = iterator.next();
                if (cliente == this) {
                    System.out.println("un usuario salió con éxito");
                    broadcastMensaje("[Servidor]: " + nombreUsuario + " ha salido del chat.");
                  //  iterator.remove();
                    break;
                }
            }
            broadcastMensaje("[Servidor]: " + nombreUsuario + " ha salido del chat.");
          //  broadcastMensaje("[Servidor]: Otros usuarios en el chat: " + obtenerUsuariosConectados());
        }

        private String obtenerUsuariosConectados() {
            StringBuilder usuarios = new StringBuilder();
            for (ClienteHandler cliente : clientes) {
                usuarios.append(cliente.nombreUsuario).append(", ");
            }
            return usuarios.length() > 2 ? usuarios.substring(0, usuarios.length() - 2) : "Ninguno";
        }

        private void procesarMensajePrivado(String mensaje) {
            // Obtener el nombre de usuario al que se envía el mensaje privado
            int indexEspacio = mensaje.indexOf(' ');
            if (indexEspacio != -1) {
                String destinatario = mensaje.substring(1, indexEspacio);
                String mensajePrivado = mensaje.substring(indexEspacio + 1);

                // Enviar el mensaje privado al destinatario
                enviarMensajePrivado(destinatario, "[" + nombreUsuario + " PRIVADO]: " + mensajePrivado);
            }
        }

        private void enviarMensajePrivado(String destinatario, String mensaje) {
            for (ClienteHandler cliente : clientes) {
                if (cliente.nombreUsuario.equals(destinatario)) {
                    cliente.enviarMensaje(mensaje);
                    return;
                }
            }
            // Enviar un mensaje al remitente si el destinatario no está en línea
            enviarMensaje("[Servidor]: El usuario '" + destinatario + "' no está en línea.");
        }
    }
}
