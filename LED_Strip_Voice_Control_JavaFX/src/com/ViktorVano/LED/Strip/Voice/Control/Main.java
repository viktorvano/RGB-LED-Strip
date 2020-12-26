package com.ViktorVano.LED.Strip.Voice.Control;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Main extends Application {
    private Pane pane;
    private String message = "";
    private Label label;
    private Timeline timeline;
    private MyServer myServer;

    @Override
    public void start(Stage stage){
        final int width = 400;
        final int height = 300;

        pane = new Pane();

        label = new Label("PORT: 7777");
        label.setFont(Font.font("Arial", 24));
        label.setStyle("-fx-background-color: #FFFFFF");
        pane.getChildren().add(label);

        Scene scene = new Scene(pane, width, height);

        stage.setTitle("LED Strip Voice Control");
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.setMaxWidth(stage.getWidth());
        stage.setMaxHeight(stage.getHeight());
        stage.setResizable(false);

        timeline = new Timeline(new KeyFrame(Duration.millis(1), event ->{
            parseMessage();
        }));
        timeline.setCycleCount(1);

        changePaneColor("000000");
        myServer = new MyServer();
        myServer.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        myServer.setActive(false);
        System.out.println("Server will be stopped soon...");
        System.out.println("Closing the application.");
    }

    public static void main(String[] args){
        launch(args);
    }

    private void changePaneColor(String color)
    {
        pane.setStyle("-fx-background-color: #" + color);
    }

    private void parseMessage()
    {
        label.setText("PORT: 7777\n" + message);
        if (message.contains("red") &&
           (message.contains("color") || message.contains("light") || message.contains("lights")))
        {
            sendDataToServer("SET_LED_RGB:255,000,000\n");
            changePaneColor("FF0000");
        }else if (message.contains("green") &&
                (message.contains("color") || message.contains("light") || message.contains("lights")))
        {
            sendDataToServer("SET_LED_RGB:000,255,000\n");
            changePaneColor("00FF00");
        }else if (message.contains("blue") &&
                (message.contains("color") || message.contains("light") || message.contains("lights")))
        {
            sendDataToServer("SET_LED_RGB:000,000,255\n");
            changePaneColor("0000FF");
        }else if ((message.contains("on") || message.contains("white")) &&
                (message.contains("color") || message.contains("light") || message.contains("lights")))
        {
            sendDataToServer("SET_LED_RGB:255,255,255\n");
            changePaneColor("FFFFFF");
        }else if (message.contains("off") &&
                (message.contains("color") || message.contains("light") || message.contains("lights")))
        {
            sendDataToServer("SET_LED_RGB:000,000,000\n");
            changePaneColor("000000");
        }
        message = "";
    }

    private void sendDataToServer(String message)
    {
        try
        {
            // need host and port, we want to connect to the ESP8266 ServerSocket at port 80
            Socket socket = new Socket();
            socket.setSoTimeout(300);
            socket.connect(new InetSocketAddress("192.168.2.239", 80), 300);
            System.out.println("Connected!");

            // get the output stream from the socket.
            OutputStream outputStream = socket.getOutputStream();
            // create a data output stream from the output stream so we can send data through it
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Sending string to the ServerSocket: " + message);

            // write the message we want to send
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush(); // send the message
            dataOutputStream.close(); // close the output stream when we're done.

            System.out.println("Closing socket.");
            socket.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class MyServer extends Thread{
        private boolean active = true;

        public void setActive(boolean active){
            this.active = active;
        }

        @Override
        public void run() {
            super.run();
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(7777);
                ss.setSoTimeout(5000);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (ss == null)
            {
                System.exit(-99);
            }
            System.out.println("ServerSocket awaiting connections...");
            Socket socket = null;// = ss.accept(); // blocking call, this will wait until a connection is attempted on this port.
            while (active)
            {
                try {
                    socket = ss.accept(); // blocking call, this will wait until a connection is attempted on this port.
                    System.out.println("Connection from " + socket + "!");

                    // get the input stream from the connected socket
                    InputStream inputStream = null;
                    try {
                        assert socket != null;
                        inputStream = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // create a DataInputStream so we can read data from it.
                    assert inputStream != null;
                    DataInputStream dataInputStream = new DataInputStream(inputStream);

                    // read the message from the socket
                    try {
                        message = dataInputStream.readUTF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Received message:\n" + message + "\n");

                    if(message != null && !message.equals(""))
                        timeline.play();
                }catch (SocketTimeoutException e)
                {
                    System.out.println("Socket timed out");
                } catch (Exception e) {
                    System.out.println("Something went wrong.");
                    e.printStackTrace();
                }
            }
        }
    }
}
