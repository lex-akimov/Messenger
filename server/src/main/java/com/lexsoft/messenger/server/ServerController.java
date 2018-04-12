package com.lexsoft.messenger.server;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Алексей on 13.09.2017.
 */

//TODO логирование
//TODO статистика
//TODO криптование
//TODO отправка с enter

public class ServerController{

    private ArrayList<ClientThread> clientThreads = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        new ServerController().startServer();
    }


    /**
     * Основной серверный поток
     * - Создаёт сокет
     * - Отслеживает подключения новых пользователей
     * - Ведёт логирование
     */
    private void startServer() throws IOException {
        ServerSocket socket = new ServerSocket(5000);

        printToServerLog("Сервер запущен." +
                "\nВнешний IP сервера: " + socket.getInetAddress() +
                "\nЛокальный IP сервера: " + socket.getLocalSocketAddress().toString() +
                "\nП Р И Я Т Н О Г О   О Б Щ Е Н И Я ! ! !");

        Socket clientSocket;
        while (true) {
            if ((clientSocket = socket.accept()) != null) {
                ClientThread newClient = new ClientThread(clientSocket);
                newClient.start();
                printToServerLog("Подключён: " + newClient.getIP());
                clientThreads.add(newClient);
                clientSocket = null;
            }
        }
    }

    // Напечатать в лог сервера
    private void printToServerLog(String string) {
        System.out.println(string + "\n");
    }

    //Отправка всем
    synchronized void sendToAll(String msg) {
        for (ClientThread client : clientThreads) {
            client.sendToClient(msg);
        }
        printToServerLog(msg);
    }

    private void showStatistics() {
        System.out.println("статитика");
    }

    private void destroy() {
        System.out.println("дестрой");
    }

    private void help() {
        printToServerLog("Реализация хелпа");
    }

    public class ClientThread extends Thread {

        private Socket clientSocket;
        private BufferedReader clientReader;
        private PrintStream clientWriter;

        ClientThread(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientWriter = new PrintStream(clientSocket.getOutputStream());
        }

        String getIP() {
            return this.clientSocket.getInetAddress().getHostAddress();
        }

        void sendToClient(String msg) {
            this.clientWriter.println(msg);
            this.clientWriter.flush();
        }

        @Override
        public void run() {
            String message = null;
            while (true) {
                try {
                    if ((message = clientReader.readLine()) != null) {
                        sendToAll(message);
                        message = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}