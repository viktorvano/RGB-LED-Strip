# RGB-LED-Strip
 STM32 based server for RGB LED strip with ESP8266 + Android App + JavaFX Desktop App  
 
## Wiring Diagram  
  

![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/wiring.png?raw=true)  
  
## Software  
###### JavaFX Application: LED Strip Scheduler  
  
![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/Screenshot%20LED%20Scheduler.png?raw=true)  
  
You can change your light profile schedule in function generateData().  
If you want to change color of the chart, you can do so by calling function changeSeriesColor(@NotNull LineChart lineChart, int i, @NotNull String color).  
Example:  
```Java
        changeSeriesColor(lightChart, 0, "#FF0000");
        changeSeriesColor(lightChart, 1, "#00FF00");
        changeSeriesColor(lightChart, 2, "#0000FF");
        changeSeriesColor(lightChart, 3, "orange");
        sendDataToServer("SET_LED_RGB:" + redString + "," + greenString + "," + blueString + "\n"); // Example of sending a command to STM32/ESP8266 server
```
  
Code Snippet:
```Java
    private void generateData()
    {
        addDataRGB(0, 0, 0, 0, 0);
        addDataRGB(23, 59, 0, 0, 0);
        addDataRGB(6, 0, 0, 0, 0);
        addDataRGB(6, 15, 127, 0, 0);
        addDataRGB(6, 30, 255, 64, 0);
        addDataRGB(9,0, 255, 255, 255);
        addDataRGB(19,0, 255, 255, 255);
        addDataRGB(20,0, 255, 127, 0);
        addDataRGB(20,30, 255, 0, 0);
        addDataRGB(21, 0, 0, 0, 0);
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
```  
  
  
