package br.edu.ifpb.gugawag.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Server {

    private static String HOME = System.getProperty("user.home");

    public static void main(String[] args) throws IOException {
        System.out.println("== Servidor ==");

        ServerSocket serverSocket = new ServerSocket(7001);
        Socket socket = serverSocket.accept();

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        while (true) {
            System.out.println("Cliente: " + socket.getInetAddress());

            String mensagem = dis.readUTF();
            System.out.println(mensagem);
            String response = "";

            if( mensagem != null && !mensagem.trim().equals("") ) {
                File home = new File(HOME);

                switch (mensagem.trim().split("\\|")[0].toLowerCase(Locale.ROOT)) {
                    case "readdir": {
                        response = filesInFolder(home);
                        break;
                    }
                    case "readdirnumber": {
                        List<String> listFiles = renameFilesInFolder(home);
                        int lengthMaxOfNumber = String.valueOf(listFiles.size()).length();
                        String spaces = new String(new char[lengthMaxOfNumber]).replace("\0", " ");

                        for(int i = 0; i < listFiles.size(); i++)
                            response += (i+1) + "|" + spaces + listFiles.get(i) + "\n";

                        break;
                    }
                    case "readdirnumberlength": {
                        response = String.valueOf( renameFilesInFolder(home).size() );
                        break;
                    }
                    case "rename": {
                        List<String> commandsElement = Arrays.stream(mensagem.split("\\|")).toList();
                        int indexFile = Integer.parseInt( commandsElement.get(1) );
                        String newName = commandsElement.get(2);

                        List<String> listFiles = renameFilesInFolder(home);
                        File oldFileName = new File(HOME + "\\" + listFiles.get(indexFile-1));
                        File newFileName = new File(HOME + "\\" + newName);
                        if( !newFileName.exists() ) {
                            oldFileName.renameTo(newFileName);
                            response = "Arquivo renomeado com sucesso!";
                        } else
                            response = "Erro: Novo nome já existente";

                        break;
                    }
                    case "remove": {
                        List<String> listFiles = renameFilesInFolder(home);
                        List<String> commandsElement = Arrays.stream(mensagem.split("\\|")).toList();
                        int indexFileChange = Integer.parseInt( commandsElement.get(1) );

                        File oldFile = new File(HOME + "\\" + listFiles.get(indexFileChange-1));
                        oldFile.delete();

                        response = "Arquivo removido com sucesso!";
                        break;
                    }
                    case "create": {
                        List<String> commandsElement = Arrays.stream(mensagem.split("\\|")).toList();
                        String newFileName = commandsElement.get(1);

                        File newFile = new File(HOME + "\\" + newFileName);
                        if( !newFile.exists() ) {
                            newFile.createNewFile();
                            response = "Arquivo criado com sucesso!";
                        } else
                            response = "Erro: Novo arquivo já existente, tente novamente";

                        break;
                    }
                    default:
                        response = "Erro: Comando Invalido";
                }

                dos.writeUTF(response);
            } else
                dos.writeUTF("Mensagem Invalida: " + mensagem);

        }
    }

    public static String filesInFolder(final File folder) {
        StringBuilder response = new StringBuilder();
        for (final File fileOrFolder : folder.listFiles()) {
            response.append( fileOrFolder.isDirectory() ? "DIR| " + fileOrFolder : "FIL| " + fileOrFolder.getName());
            response.append("\n");
        }
        return response.substring(0, response.length() - 3);
    }

    public static List<String> renameFilesInFolder(final File folder) {
        List<String> response = new ArrayList<>();
        for (final File fileOrFolder : folder.listFiles())
            if (!fileOrFolder.isDirectory()) response.add(fileOrFolder.getName());

        return response;
    }
}
