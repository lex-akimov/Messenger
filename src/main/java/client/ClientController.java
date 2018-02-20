package client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by Алексей on 11.09.2017.
 */
public class ClientController extends Application {
    public ClientController() throws IOException {
    }
    //TODO логины
    //TODO лог сообщений с именами пользователей

    public static void main(String[] args) {
        launch(args);
    }

    private Stage clientFormStage;

    @Override
    public void start(Stage clientFormStage) throws Exception {
        clientFormStage.setTitle("Вход");
        clientFormStage.setResizable(false);
        Parent connectionForm = FXMLLoader.load(getClass().getResource("ConnectionForm.fxml"));
        Scene connectionScene = new Scene(connectionForm);
        clientFormStage.setScene(connectionScene);

        clientFormStage.show();
    }

    @FXML
    private ComboBox ipAddresCombobox = new ComboBox();
    @FXML
    private TextField nickNameTextField = new TextField();
    private String nickname;

    private JTextArea messageHistoryArea = new JTextArea(20, 20);
    private JTextField messageInputField = new JTextField(21);

    private BufferedReader clientReader;
    private PrintStream clientWriter;

    public void setConnectionWithServer(ActionEvent actionEvent) throws IOException {
        if (!ipAddresCombobox.getEditor().getText().isEmpty() & !nickNameTextField.getText().isEmpty()) {
            nickname = nickNameTextField.getText();
            startClient();
        } else {
            System.out.println("Введите адрес и своё имя");
        }
    }

    private void startClient() throws IOException {
        try {
            Socket server = new Socket(ipAddresCombobox.getEditor().getText(), 5000);
            clientReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            clientWriter = new PrintStream(server.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Ошибка соединения.");
        }


        clientFormStage.hide();
        Parent clientForm = FXMLLoader.load(getClass().getResource("ClientForm.fxml"));
        Scene clientScene = new Scene(clientForm);
        clientFormStage.setScene(clientScene);
        clientFormStage.show();


        addMsg("Соединение установлено!");

        IncomingReader incomeMessagesReader = new IncomingReader();
        incomeMessagesReader.start();

    }

    private void addMsg(String msg) {
        messageHistoryArea.append(msg + "\n\n");
    }

    public void btnSendAction(ActionEvent actionEvent) {
        clientWriter.println(messageInputField.getText());
        clientWriter.flush();
        messageInputField.setText("");
        messageInputField.requestFocus();

    }

    private class IncomingReader extends Thread {
        @Override
        public void run() {
            String msg;
            try {
                while ((msg = clientReader.readLine()) != null) {
                    addMsg(msg);
                    msg = null;
                }
            } catch (IOException e) {
                System.out.println("Соединение с сервером потеряно");
                System.exit(0);
            }
        }
    }
}
