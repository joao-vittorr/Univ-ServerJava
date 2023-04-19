package www;

import java.io.*;
import java.net.*;
import java.util.*;

final class HttpRequest implements Runnable {

    //Carriage Return + Line Feed
    final static String CRLF = "\r\n";

    Socket socket;

    //Construtor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {

        // Objeto 'isr' referência para os trechos de entrada
        InputStreamReader isr = new InputStreamReader(socket.getInputStream());

        // Objeto 'dos' referência para os trechos de saida
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        // Ajustar os filtros do trecho de entrada
        BufferedReader br = new BufferedReader(isr);

        // Obter a linha de requisição da mensagem de requisição HTTP
        String requestLine = br.readLine();

        // Exibir a linha de requisição no Console
        System.out.println(""); // pula uma linha
        System.out.println(requestLine + "o que foi pedido");
        String headerLine = null;

        // Dados que irao compor o Log
        String log = requestLine + System.getProperty("line.separator");

        // Percorre todas linhas da mensagem
        while ((headerLine = br.readLine()).length() != 0) {

            // Obtendo linhas do cabecalho para log
            log = log + (headerLine + System.getProperty("line.separator"));
            System.out.println(headerLine);
        }

        // Extrair o nome do arquivo a linha de requisição
        StringTokenizer requisicao = new StringTokenizer(requestLine);




        // pula método mostrado na requisicao (GET, POST)
        String metodo = requisicao.nextToken();
        String arquivo = requisicao.nextToken();


        // Acrescente um "." de modo que a requisi��o do arquivo
        // esteja dentro do dirt�rio atual
        arquivo = "." + arquivo;

        String linhaStatus = null;
        String linhaContentType = null;
        String msgHtml = null;



        FileInputStream fis = null;
        boolean existeArq = true;
        if (metodo.equals("GET")) {

            try {
                fis = new FileInputStream(arquivo);
            } catch (FileNotFoundException e) {
                existeArq = false;
            }



            if (existeArq) {
                linhaStatus = "HTTP/1.0 200 OK" + CRLF;
                linhaContentType = "Content-type: " + contentType(arquivo) + CRLF;
            } else {
                linhaStatus = "HTTP/1.0 404 Not found" + CRLF;
                linhaContentType = "Content-type: " + contentType(arquivo) + CRLF;
                msgHtml = "<HTML><HEAD><TITLE> Arquivo Nao Encontrado" + "</TITLE></HEAD>"
                        + "<BODY> Arquivo Nao Encontrado </BODY></HTML>";
            }

            // Enviar a linha de status
            dos.writeBytes(linhaStatus);
            dos.writeBytes(linhaContentType);
            dos.writeBytes(CRLF);
        }

        if (existeArq) {
            if (arquivo.endsWith(".php")) {
                String html = PHP.exec(arquivo);
                dos.writeBytes(html);
            } else {
                sendBytes(fis, dos);
                fis.close();
            }
        } else {
            dos.writeBytes(msgHtml);
        }

        Log(dos, log, socket);
        dos.close();
        br.close();
        socket.close();
    }

    private void sendBytes(FileInputStream fis, DataOutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String arquivo) {
        if (arquivo.endsWith(".htm") || arquivo.endsWith(".html") || arquivo.endsWith(".txt") || arquivo.endsWith(".php"))
            return "text/html";
        if (arquivo.endsWith(".gif"))
            return "image/gif";
        if (arquivo.endsWith(".jpeg"))
            return "image/jpeg";
        // caso a extensão do arquivo seja desconhecida
        return "application/octet-stream";
    }

    private void Log(DataOutputStream dos, String log, Socket socket) {
        try {
            // Data de requisicao
            Date date = new Date(System.currentTimeMillis());
            String dataRequisicao = date.toString();
            String pulaLinha = System.getProperty("line.separator");
            FileWriter fw = new FileWriter("arquivo_de_log.txt", true);
            fw.write("------------------------------------------------------" + pulaLinha);
            fw.write("Data de Requisicao: " + dataRequisicao + " GMT " + pulaLinha);
            fw.write("ENDEREÇO DE ORIGEM:PORTA: " + socket.getLocalSocketAddress().toString() + pulaLinha);
            fw.write("Conteúdo Requisitado: " + log + pulaLinha);
            fw.write("Quantidade de bytes transmitidos: " + dos.size() + pulaLinha);
            fw.write("------------------------------------------------------" + pulaLinha);
            fw.write(pulaLinha);
            fw.close();
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }
    }
}