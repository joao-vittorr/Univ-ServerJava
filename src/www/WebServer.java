package www;

import java.net.*;

public final class WebServer {

    public static void main(String arvg[]) throws Exception {

        int porta = 80;
        ServerSocket socketServ = new ServerSocket(porta);
        Socket socketCli;
        while (true) {
            System.out.println("Servidor Ativo, porta:"+porta);

            socketCli = socketServ.accept();

            System.out.println("IP do meliante");
            System.out.println(socketCli.getRemoteSocketAddress());

            HttpRequest requisicao = new HttpRequest(socketCli);
            Thread thread = new Thread(requisicao);
            thread.start();
        }
    }
}