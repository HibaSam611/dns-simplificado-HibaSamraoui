import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClienteDNS {

    public static void main(String[] args) {

        try {

            Socket socket = new Socket("localhost", 5000);
            System.out.println("Cliente conectado al servidor DNS.");

            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Scanner teclado = new Scanner(System.in);

            String comando;

            while (true) {

                System.out.print("--> ");
                comando = teclado.nextLine();

                salida.println(comando);

                if (comando.equals("EXIT")) {
                    break;
                }

                String respuesta = entrada.readLine();

                if (respuesta == null) {
                    break;
                }
                System.out.println(respuesta);
            }

            socket.close();
            System.out.println("Cliente desconectado.");

        } catch (IOException e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }
}

