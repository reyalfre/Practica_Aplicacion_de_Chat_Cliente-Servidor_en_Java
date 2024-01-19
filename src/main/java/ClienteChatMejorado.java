import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteChatMejorado {

    public static void main(String[] args) {
        final String SERVIDOR_IP = "localhost";
        final int PUERTO = 12345;

        try {
            Socket socket = new Socket(SERVIDOR_IP, PUERTO);

            // Obtener el nombre de usuario del cliente
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingresa tu nombre de usuario: ");
            String nombreUsuario = scanner.nextLine();

            // Enviar el nombre de usuario al servidor
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println(nombreUsuario);

            // Crear hilos para manejar la entrada y salida de mensajes simultáneamente
            Thread hiloEntrada = new Thread(new EntradaMensajes(socket));
            Thread hiloSalida = new Thread(new SalidaMensajes(socket));

            hiloEntrada.start();
            hiloSalida.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase para manejar la entrada de mensajes desde el servidor
    static class EntradaMensajes implements Runnable {
        private Socket socket;

        public EntradaMensajes(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Scanner entrada = new Scanner(socket.getInputStream())) {
                while (entrada.hasNextLine()) {
                    // Mostrar mensajes recibidos en la consola
                    String mensaje = entrada.nextLine();
                    System.out.println(mensaje);

                    // Comprobar si el usuario está saliendo del chat
                    if (mensaje.contains(" ha salido del chat.")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Clase para manejar la salida de mensajes hacia el servidor
    static class SalidaMensajes implements Runnable {
        private Socket socket;

        public SalidaMensajes(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                 Scanner scanner = new Scanner(System.in)) {

                while (true) {
                    // Permitir al usuario enviar mensajes al servidor
                    String mensaje = scanner.nextLine();

                    // Comprobar si el mensaje es privado
                    if (mensaje.startsWith("@")) {
                        salida.println(mensaje);
                    } else {
                        // Enviar el mensaje al servidor
                        salida.println(mensaje);

                        // Comprobar si el usuario quiere salir del chat
                        if (mensaje.equalsIgnoreCase("salir")) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
