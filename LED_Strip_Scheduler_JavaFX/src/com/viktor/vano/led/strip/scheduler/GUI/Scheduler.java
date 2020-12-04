package com.viktor.vano.led.strip.scheduler.GUI;

import com.sun.istack.internal.NotNull;
import com.sun.javafx.charts.Legend;
import com.viktor.vano.led.strip.scheduler.Classes.GradientListRGB;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class Scheduler extends Application {

    private XYChart.Series<Number, Number> redSeries, greenSeries, blueSeries, presentSeries;
    private Timeline timelineUpdateData;
    private final BorderPane borderPane = new BorderPane();
    private final StackPane stackPaneCenter = new StackPane();
    private final VBox vBoxRight = new VBox();
    private final HBox hBoxBottom = new HBox();
    private LineChart<Number,Number> lightChart;
    private Label labelCurrentStatus;
    private Slider sliderInvestmentDrop;
    private float investment = 0.0f, currencyDropThreshold = 0.0f;
    private int currentTime;
    private GradientListRGB gradientListRGB;
    private int phoneAbsence = 0;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        final int width = 1200;
        final int height = 750;

        //borderPane.setBottom(hBoxBottom);
        borderPane.setCenter(stackPaneCenter);
        borderPane.setRight(vBoxRight);

        hBoxBottom.setPadding(new Insets(15, 50, 15, 50));
        hBoxBottom.setSpacing(30);
        hBoxBottom.setStyle("-fx-background-color: #336699;");

        initializeLayout();

        Scene scene = new Scene(borderPane, width, height);

        stage.setTitle("LED Strip Scheduler");
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        try
        {
            Image icon = new Image(getClass().getResourceAsStream("../images/LED.jpg"));
            stage.getIcons().add(icon);
            System.out.println("Icon loaded from IDE...");
        }catch(Exception e)
        {
            try
            {
                Image icon = new Image("com/viktor/vano/led/strip/scheduler/images/LED.jpg");
                stage.getIcons().add(icon);
                System.out.println("Icon loaded from exported JAR...");
            }catch(Exception e1)
            {
                System.out.println("Icon failed to load...");
            }
        }
    }

    @Override
    public void stop()
    {
        System.out.println("Leaving the app...");
        System.exit(0);
    }

    private void initializeLayout()
    {
        sliderInvestmentDrop = new Slider();
        sliderInvestmentDrop.setMin(0);
        sliderInvestmentDrop.setMax(60.0);
        sliderInvestmentDrop.setPrefWidth(200);
        sliderInvestmentDrop.valueProperty().addListener(new ChangeListener<Number>() {
             @Override
             public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                 currencyDropThreshold = -Math.round(newValue.floatValue()*10.0f)/10.0f;
                 labelCurrentStatus.setText("Investment: " + investment + " €\t\tInvestment Threshold: "
                         + currencyDropThreshold + "%");
             }
         });
        labelCurrentStatus = new Label("Investment: " + investment + " €\t\tInvestment Threshold: "
                + currencyDropThreshold + "%");
        labelCurrentStatus.setFont(new Font("Arial", 16));

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        //creating the chart
        lightChart = new LineChart<>(xAxis,yAxis);
        lightChart.setTitle("LED Strip");
        //defining a series
        redSeries = new XYChart.Series<>();
        redSeries.setName("Red");
        greenSeries = new XYChart.Series<>();
        greenSeries.setName("Green");
        //setSeriesColor(greenSeries, Color.GREEN);
        blueSeries = new XYChart.Series<>();
        blueSeries.setName("Blue");
        //setSeriesColor(blueSeries, Color.BLUE);
        presentSeries = new XYChart.Series<>();
        presentSeries.setName("Present");
        //setSeriesColor(presentSeries, Color.ORANGE);
        lightChart.setCreateSymbols(false);
        //populating the series with data
        lightChart.setAnimated(false);
        lightChart.getData().add(redSeries);
        lightChart.getData().add(greenSeries);
        lightChart.getData().add(blueSeries);
        lightChart.getData().add(presentSeries);
        changeSeriesColor(lightChart, 0, "#FF0000");
        changeSeriesColor(lightChart, 1, "#00FF00");
        changeSeriesColor(lightChart, 2, "#0000FF");
        changeSeriesColor(lightChart, 3, "orange");

        hBoxBottom.getChildren().add(sliderInvestmentDrop);
        hBoxBottom.getChildren().add(labelCurrentStatus);
        stackPaneCenter.getChildren().add(lightChart);

        generateData();

        gradientListRGB = new GradientListRGB(redSeries, greenSeries, blueSeries);

        timelineUpdateData = new Timeline(new KeyFrame(Duration.seconds(5), event ->
        {
            boolean ping = sendPingRequest("192.168.2.100");
            System.out.println("UMIDIGI S5 Pro present: " + ping);
            if(ping)
            {
                phoneAbsence = 0;
            }else
            {
                phoneAbsence++;
            }
            presentSeries.getData().clear();
            try{
                LocalTime time = LocalTime.now(); // Gets the current time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String[] stringsTime = time.format(formatter).split(":");
                currentTime = Integer.valueOf(stringsTime[0])*3600
                            + Integer.valueOf(stringsTime[1])*60
                            + Integer.valueOf(stringsTime[2]);
                presentSeries.getData().add(new XYChart.Data<>(currentTime-0.005, 0));
                presentSeries.getData().add(new XYChart.Data<>(currentTime+0.005, 255));
                System.out.println("Time: " + time.format(formatter) + "\tin seconds: " + currentTime);
                if(phoneAbsence > 24)
                {
                    sendDataToServer("SET_LED_RGB:000,000,000");
                }else
                {
                    String redString = "";
                    String greenString = "";
                    String blueString = "";
                    int red = gradientListRGB.getRed(currentTime);
                    int green = gradientListRGB.getGreen(currentTime);
                    int blue = gradientListRGB.getBlue(currentTime);

                    if(red < 10)
                        redString = "00" + red;
                    else if(red < 100)
                        redString = "0" + red;
                    else
                        redString = String.valueOf(red);

                    if(green < 10)
                        greenString = "00" + green;
                    else if(green < 100)
                        greenString = "0" + green;
                    else
                        greenString = String.valueOf(green);

                    if(blue < 10)
                        blueString = "00" + blue;
                    else if(blue < 100)
                        blueString = "0" + blue;
                    else
                        blueString = String.valueOf(blue);

                    sendDataToServer("SET_LED_RGB:" + redString + "," + greenString + "," + blueString);
                }
            }catch (Exception e)
            {
                System.out.println("Timeline problem.");
            }
        }));
        timelineUpdateData.setCycleCount(Timeline.INDEFINITE);
        timelineUpdateData.play();
    }

    private void generateData()
    {
        addDataRGB(0, 0, 0, 0, 0);
        addDataRGB(23, 59, 0, 0, 0);
        addDataRGB(6, 0, 0, 0, 0);
        addDataRGB(6, 30, 255, 64, 0);
        addDataRGB(9,0, 255, 255, 255);
        addDataRGB(19,0, 255, 255, 255);
        addDataRGB(20,0, 255, 127, 0);
        addDataRGB(20,30, 255, 0, 0);
        addDataRGB(21, 0, 0, 0, 0);
    }

    private void addDataRGB(int hours, int minutes, int red, int green, int blue)
    {
        if(hours < 0)
            hours = 0;
        else if(hours > 23)
            hours = 23;

        if(minutes < 0)
            minutes = 0;
        else if(minutes > 59)
            minutes = 59;

        int seconds;

        if(hours == 23 && minutes == 59)
            seconds = hours*3600 + minutes*60 + 59;
        else
            seconds = hours*3600 + minutes*60;

        redSeries.getData().add(new XYChart.Data<>(seconds, red));
        greenSeries.getData().add(new XYChart.Data<>(seconds, green));
        blueSeries.getData().add(new XYChart.Data<>(seconds, blue));
    }

    public static boolean sendPingRequest(String ipAddress)
    {
        try{
            InetAddress geek = InetAddress.getByName(ipAddress);
            if (geek.isReachable(300))
                return true;
            else
                return false;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private void sendDataToServer(String message)
    {
        try
        {
            // need host and port, we want to connect to the ServerSocket at port 7777
            Socket socket = new Socket();
            socket.setSoTimeout(200);
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

    private void changeSeriesColor(@NotNull LineChart lineChart, int i, @NotNull String color)
    {
        if(i < lineChart.getData().size())
        {
            lineChart.applyCss();
            Set<Node> nodes = lineChart.lookupAll(".series" + i);
            for (Node n : nodes) {
                n.setStyle("-fx-stroke: " + color + "; -fx-background-color: " + color + ", white; ");
            }

            for(Node n : lightChart.getChildrenUnmodifiable()){
                if(n instanceof Legend){
                    Legend.LegendItem legendItem = ((Legend)n).getItems().get(i);
                    legendItem.getSymbol().setStyle("-fx-background-color: " + color + ", white;");
                }
            }
        }else System.out.println("changeSeriesColor: Index is out of boundaries.");
    }
}