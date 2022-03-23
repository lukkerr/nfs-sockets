package br.edu.ifpb.gugawag.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        System.out.println("== Cliente ==");

        Socket socket = new Socket("127.0.0.1", 7001);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        while (true) {
            System.out.println("\n---------------------------------------");
            System.out.println("---------------- Opções ---------------");
            System.out.println("---------------------------------------");
            System.out.println("---- create  | criar novo arquivos ----");
            System.out.println("---- readdir | leitura de arquivos ----");
            System.out.println("---- rename  | renomear arquivos   ----");
            System.out.println("---- remove  | remover arquivos    ----");
            System.out.println("---- exit    | sair de servidor    ----");
            System.out.println("---------------------------------------\n");

            System.out.print("Digite a opção desejada: ");
            Scanner teclado = new Scanner(System.in);
            String mensagem = teclado.nextLine();

            if(mensagem.trim().toLowerCase().matches("readdir")) {
                System.out.println( enviarMensagem("readdir", null, null, dos, dis) );
            } else if(mensagem.trim().toLowerCase().matches("rename")) {
                System.out.println( enviarMensagem("readdirnumber", null, null, dos, dis) );
                int maxIndex = Integer.parseInt( enviarMensagem("readdirnumberlength", null, null, dos, dis) );

                Scanner archiveNameScanner = new Scanner(System.in);
                String archiveNumber = "";
                while(archiveNumber.equals("")) {
                    System.out.println("Digite o número do arquivo a ser renomeado ou aperte 'x' para sair: ");
                    archiveNumber = archiveNameScanner.nextLine();

                    if(archiveNumber.trim().matches("x|X")) break;

                    if(archiveNumber.equals("") || (!archiveNumber.equals("") && !archiveNumber.matches("-?\\d+(\\.\\d+)?")) ||
                            (archiveNumber.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(archiveNumber) < 1) ||
                            (archiveNumber.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(archiveNumber) > maxIndex)) {
                        System.out.println("Erro ao selecionar número do arquivo, tente novamente.");
                        archiveNumber = "";
                    }
                }

                if(archiveNumber.trim().matches("x|X")) continue;

                Scanner archiveNewNameScanner = new Scanner(System.in);
                String nameNewArchive = "";
                while(nameNewArchive.equals("")) {
                    System.out.println("Digite o nome novo do arquivo: (mínimo 3 caracteres)");
                    nameNewArchive = archiveNewNameScanner.nextLine();
                    nameNewArchive = nameNewArchive.trim();

                    if(nameNewArchive.equals("") || !nameNewArchive.equals("") && nameNewArchive.trim().length() < 3) {
                        System.out.println("Erro ao definir novo tamanho, tente novamente.");
                        nameNewArchive = "";
                    }
                }

                System.out.println( enviarMensagem("rename", archiveNumber, nameNewArchive, dos, dis) );

            } else if(mensagem.trim().toLowerCase().matches("remove")) {

                System.out.println( enviarMensagem("readdirnumber", null, null, dos, dis) );
                int maxIndex = Integer.parseInt( enviarMensagem("readdirnumberlength", null, null, dos, dis) );

                Scanner archiveNameScanner = new Scanner(System.in);
                String archiveNumber = "";
                while(archiveNumber.equals("")) {
                    System.out.println("Digite o número do arquivo a ser removido ou aperte 'x' para sair: ");
                    archiveNumber = archiveNameScanner.nextLine();
                    if(archiveNumber.trim().matches("x|X")) break;

                    if(archiveNumber.equals("") || (!archiveNumber.equals("") && !archiveNumber.matches("-?\\d+(\\.\\d+)?")) ||
                            (archiveNumber.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(archiveNumber) < 1) ||
                            (archiveNumber.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(archiveNumber) > maxIndex)) {
                        System.out.println("Erro ao selecionar número do arquivo, tente novamente.");
                        archiveNumber = "";
                    }
                }

                if(archiveNumber.trim().matches("x|X")) continue;
                System.out.println( enviarMensagem("remove", archiveNumber, null, dos, dis) );

            } else if(mensagem.trim().toLowerCase().matches("create")) {

                Scanner archiveNewNameScanner = new Scanner(System.in);
                String nameNewArchive = "";
                while(nameNewArchive.equals("")) {
                    System.out.println("Digite o nome do novo arquivo: (mínimo 3 caracteres)");
                    nameNewArchive = archiveNewNameScanner.nextLine();
                    nameNewArchive = nameNewArchive.trim();

                    if(nameNewArchive.equals("") || (!nameNewArchive.equals("") && nameNewArchive.trim().length() < 3)) {
                        System.out.println("Erro ao definir novo tamanho, tente novamente.");
                        nameNewArchive = "";
                    }
                }

                System.out.println( enviarMensagem("create", null, nameNewArchive, dos, dis) );
            } else if(mensagem.trim().toLowerCase().matches("exit")) {
                socket.close();
                break;
            } else
                System.out.println("Comando Invalido tente novamente");
        }
    }

    public static String formatarComando(String comando, String number, String novoNome) {

        String result = null;

        if( comando != null && !comando.trim().equals("") &&
                comando.trim().toLowerCase().matches("create|readdir|readdirnumber|readdirnumberlength|rename|remove") ) {
            if(comando.equals("rename"))
                result = comando + "|" + number + "|" + novoNome;
            else if(comando.equals("create"))
                result = comando + "|" + novoNome;
            else if(comando.equals("remove"))
                result = comando + "|" + number;
            else
                result = comando;
        }

        return result;
    }

    public static String enviarMensagem(String comando, String indexComando, String novoNumber, DataOutputStream dos, DataInputStream dis) throws IOException {
        dos.writeUTF(formatarComando(comando, indexComando, novoNumber));
        return dis.readUTF();
    }
}
