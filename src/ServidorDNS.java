import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServidorDNS {

    public static void main(String[] args) {

        HashMap<String, String> baseDNS = new HashMap<>();

        try {
            // Abrimos dns.txt para leer los registros
            BufferedReader br = new BufferedReader(new FileReader("dns.txt"));

            String linea;

            // Leemos cada línea del archivo
            while ((linea = br.readLine()) != null) {

                String[] partes = linea.split(" ");

                String dominio = partes[0]; // google.com
                String tipo = partes[1];    // A
                String ip = partes[2];   // 172.217.17.4

                // En fase 1 SOLO guardamos los de registro A
                if (tipo.equals("A")) {
                    baseDNS.put(dominio, ip);
                }
            }

            br.close();

        } catch (Exception e) {
            System.out.println("ERROR: no se pudo leer dns.txt");
            return;
        }


        try (ServerSocket serverSocket = new ServerSocket(5000)) {

            System.out.println("Servidor DNS en puerto 5000...");
            System.out.println("Esperando cliente...");

            Socket cliente = serverSocket.accept();
            System.out.println("Cliente conectado.");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

            PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true);

            String comando;

            while ((comando = entrada.readLine()) != null) {

                try {

                    if (comando.equals("EXIT")) {
                        salida.println("200 Bye");
                        break;
                    }

                    // Si empieza con LOOKUP
                    if (comando.startsWith("LOOKUP")) {
                        String[] partes = comando.split(" ");
                        if (partes.length != 3) {
                            salida.println("400 Bad request");
                            continue;
                        }

                        String tipo = partes[1];     // "A"
                        String dominio = partes[2];  //"google.com"

                        // En la fase 1 solo aceptamos A
                        if (!tipo.equals("A")) {
                            salida.println("400 Bad request");
                            continue;
                        }

                        if (baseDNS.containsKey(dominio)) {
                            String ip = baseDNS.get(dominio);
                            salida.println("200 " + ip);  // encontrado
                        } else {
                            salida.println("404 Not Found"); //no existe
                        }

                    } else {
                        // otra cosa no es válida
                        salida.println("400 Bad request");
                    }

                } catch (Exception e) {
                    // Si algo falla al procesar el  LOOKUP
                    salida.println("500 Server error");
                }
            }
            cliente.close();
            System.out.println("Cliente desconectado.");

        } catch (IOException e) {
            System.out.println("ERROR en el servidor");
        }
    }
}
