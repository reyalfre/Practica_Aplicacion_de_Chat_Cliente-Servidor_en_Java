import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServidorChatMejorado {

    private static List<ClienteHandler> clientes = new ArrayList<>();

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

        public ClienteHandler(Socket socket) {
            this.socket = socket;
            try {
                this.salida = new PrintWriter(socket.getOutputStream(), true);
                Scanner entrada = new Scanner(socket.getInputStream());
                this.nombreUsuario = entrada.nextLine();
                enviarMensaje("[Servidor]: ¡Bienvenido, " + nombreUsuario + "!");
                enviarMensaje("[Servidor]: Para salir del chat, escribe 'salir'.");
                enviarMensaje("[Servidor]: Otros usuarios en el chat: " + obtenerUsuariosConectados());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try (Scanner entrada = new Scanner(socket.getInputStream())) {
                while (true) {
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
                        broadcastMensaje("[" + nombreUsuario + "]: " + mensajeCliente);
                    }
                }
            } catch (IOException e) {
                salirDelChat();
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
            clientes.remove(this);
            broadcastMensaje("[Servidor]: " + nombreUsuario + " ha salido del chat.");
            broadcastMensaje("[Servidor]: Otros usuarios en el chat: " + obtenerUsuariosConectados());
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
