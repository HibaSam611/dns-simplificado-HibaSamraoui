import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServidorDNS {

    public static void main(String[] args) {

        HashMap<String, String> baseDNS = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("dns.txt"));
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(" ");

                String dominio = partes[0];
                String tipo = partes[1];
                String valor = partes[2];

                if (tipo.equals("A")) {
                    baseDNS.put(dominio, valor);
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

                    if (comando.equalsIgnoreCase("EXIT")) {
                        salida.println("200 Bye");
                        break; // salir del bucle
                    }

                    // LOOKUP
                    else if (comando.startsWith("LOOKUP")) {

                        String[] partes = comando.split("\\s+");
                        if (partes.length != 3) {
                            salida.println("400 Bad request");
                            continue;
                        }

                        String tipo = partes[1];
                        String dominio = partes[2];

                        if (!tipo.equals("A")) {
                            salida.println("400 Bad request");
                            continue;
                        }

                        if (baseDNS.containsKey(dominio)) {
                            salida.println("200 " + baseDNS.get(dominio));
                        } else {
                            salida.println("404 Not Found");
                        }
                    }

                    // LIST
                    else if (comando.equalsIgnoreCase("LIST")) {

                        salida.println("150 Inicio listado");

                        for (String dominio : baseDNS.keySet()) {
                            String ip = baseDNS.get(dominio);
                            salida.println(dominio + " A " + ip);
                        }

                        salida.println("226 Fin listado");
                    }

                    else {
                        salida.println("400 Bad request");
                    }

                } catch (Exception e) {
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

