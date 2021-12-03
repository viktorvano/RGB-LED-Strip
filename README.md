# RGB-LED-Strip
 STM32 based server for RGB LED strip with ESP8266 + Android App + JavaFX Desktop App  
Tutorial video: https://youtu.be/E3QZ01AMJjs  
  
  
## Info about ESP8266 12F and IP Addresses  
  
###### ESP8266 12F Boot Modes  
  
![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/boot-modes.jpg?raw=true)  
  
###### Network Settings info  
  
Your IP addresses in your LAN will be different and you need to change them in the project accortding to your router's network setting.  
ESP8266 will have a dofferent IP address in your LAN and also your smartphone will have different IP aswell.  
Android Studio project has a hardcoded IP address of the ESP8266 server and also LED Strip Scheduler has hard coded IP addresses of the ESP and the smartphone.  
  
## Wiring Diagram  
  

![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/wiring.png?raw=true)  
  
## Software  
###### JavaFX Application: LED Strip Scheduler  
  
![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/Screenshot%20LED%20Scheduler.png?raw=true)  
  
You can change your light profile schedule in function generateData().  
If you want to change color of the chart, you can do so by calling function changeSeriesColor(@NotNull LineChart lineChart, int i, @NotNull String color).  
Example:  
```Java
changeSeriesColor(lightChart, 0, "#FF0000"); // Change series 0 to RED color
changeSeriesColor(lightChart, 1, "#00FF00"); // Change series 1 to GREEN color
changeSeriesColor(lightChart, 2, "#0000FF"); // Change series 2 to BLUE color
changeSeriesColor(lightChart, 3, "orange"); // Change series 3 to Orange color
sendDataToServer("SET_LED_RGB:" + redString + "," + greenString + "," + blueString + "\n"); // Sending a custom RGB command to STM32/ESP8266 server
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
  
###### Android App: LED Strip  
  
Edit your variables:  
```Java
private final int timeout = 1000;
private final String localIP = "192.168.1.99";
private final String DDNS_Address = "example.ddns.net";
private final String token = "fe5g8e2a5f4e85d2e85a7c5";
private final int localPORT = 80;
private final int externalPORT = 9999;
```  
  
Code snippets:
```Java
	...
        buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(view -> sendDataToServer(textViewMessage.getText().toString() + ";" + token + "\n"));
	...	
	
    private void sendDataToServer(String message)
    {
        try
        {
            String address = "0.0.0.0";
            int port = 0;
            if(switchDDNS.isChecked())
            {
                address = DDNS_Address;
                port = externalPORT;
            }
            else
            {
                address = localIP;
                port = localPORT;
            }
            // need host and port, we want to connect to the ServerSocket at port 7777
            Socket socket = new Socket();
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(address, port), timeout);
            System.out.println("Connected!");

            // get the output stream from the socket.
            OutputStream outputStream = socket.getOutputStream();
            // create a data output stream from the output stream so we can send data through it
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Sending string to the ServerSocket");

            // write the message we want to send
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush(); // send the message
            dataOutputStream.close(); // close the output stream when we're done.

            System.out.println("Closing socket.");
            socket.close();
        }catch (SocketException e)
        {
            e.printStackTrace();
            Activity activity = this;
            Toast.makeText(this, "Connection Timeout",
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Activity activity = this;
            Toast.makeText(this, "Connection Exception",
                    Toast.LENGTH_SHORT).show();
        }
    }
```
  
  
![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/Screenshot1.png?raw=true)  

###### STM32 Code Snippets  

--- WiFi Credentials ---  
Update your WiFi credentials and make a custom random token in ESP8266.h:  
```C
#define WiFi_Credentials	"AT+CWJAP=\"WiFiSSID\",\"WiFiPASSWORD\"\r\n"
#define TOKEN			"fe5g8e2a5f4e85d2e85a7c5"
```  

Example of what messages STM32 server receives to the buffer[]:
```C
//Receiving a custom RGB command
Name : buffer
	Details:"2,CONNECT\r\n\r\n+IPD,2,25:\0\027SET_LED_RGB:183,102,029\n2,CLOSED\r\n", '\0' <repeats 1941 times>
	Default:0x20000284 <buffer>
	Decimal:536871556
	Hex:0x20000284
	Binary:100000000000000000001010000100
	Octal:04000001204

//Receiving a "GET" request from a web browser
Name : buffer
	Details:"0,CONNECT\r\n\r\n+IPD,0,456:GET / HTTP/1.1\r\nHost: 192.168.2.239\r\nConnection: keep-alive\r\nUpgrade-Insecure-Requests: 1\r\nUser-Agent: Mozilla/5.0 (Linux; Android 10; S5 Pro) AppleWebKit/537.36 (KHTML, like G"...
	Default:0x20000284 <buffer>
	Decimal:536871556
	Hex:0x20000284
	Binary:100000000000000000001010000100
	Octal:04000001204
```
  
MyLibrary.c  
```C
void ESP_RESET()
{
	HAL_GPIO_WritePin(ESP_ENABLE_GPIO_Port, ESP_ENABLE_Pin, GPIO_PIN_RESET);
	HAL_GPIO_WritePin(ESP_RESET_GPIO_Port, ESP_RESET_Pin, GPIO_PIN_RESET);
	HAL_Delay(30);
	HAL_GPIO_WritePin(ESP_ENABLE_GPIO_Port, ESP_ENABLE_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(ESP_RESET_GPIO_Port, ESP_RESET_Pin, GPIO_PIN_SET);
}

void ESP_Server_Init()
{
	ESP_RESET();
	HAL_Delay(2000);
	ESP_Clear_Buffer();

	HAL_UART_Transmit(&huart1, (uint8_t*)"AT+RST\r\n", strlen("AT+RST\r\n"), 100);
	HAL_Delay(1500);
	ESP_Clear_Buffer();

	HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CWMODE=1\r\n", strlen("AT+CWMODE=1\r\n"), 100);
	HAL_Delay(1000);
	ESP_Clear_Buffer();

	HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CIPMUX=1\r\n", strlen("AT+CIPMUX=1\r\n"), 100);
	HAL_Delay(1000);
	ESP_Clear_Buffer();

	/*HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CIPDOMAIN=\"stm32.led.strip.iot\"\r\n", strlen("AT+CIPDOMAIN=\"stm32.led.strip.iot\"\r\n"), 100);
	HAL_Delay(1000);
	ESP_Clear_Buffer();*/

	HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CIPSERVER=1,80\r\n", strlen("AT+CIPSERVER=1,80\r\n"), 100);
	HAL_Delay(1000);
	ESP_Clear_Buffer();

	HAL_UART_Transmit(&huart1, (uint8_t*)WiFi_Credentials, strlen(WiFi_Credentials), 100);
}

void ESP_Clear_Buffer()
{
	memset(buffer, 0, 2000);
	buffer_index = 0;
}

uint8_t string_compare(char array1[], char array2[], uint16_t length)
{
	 uint16_t comVAR=0, i;
	 for(i=0;i<length;i++)
	   	{
	   		  if(array1[i]==array2[i])
	   	  		  comVAR++;
	   	  	  else comVAR=0;
	   	}
	 if (comVAR==length)
		 	return 1;
	 else 	return 0;
}

int string_contains(char bufferArray[], char searchedString[], uint16_t length)
{
	uint8_t result=0;
	for(uint16_t i=0; i<length; i++)
	{
		result = string_compare(&bufferArray[i], &searchedString[0], strlen(searchedString));
		if(result == 1)
			return i;
	}
	return -1;
}

void messageHandler()
{
	__HAL_UART_DISABLE_IT(&huart1, UART_IT_RXNE);
	int position = 0;
	if((position = string_contains((char*)buffer, "GET", buffer_index)) != -1)
	{
		HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CIPSEND=0,22\r\n", strlen("AT+CIPSEND=0,22\r\n"), 100);
		HAL_Delay(300);
		HAL_UART_Transmit(&huart1, (uint8_t*)"STM32 LED Strip Server", strlen("STM32 LED Strip Server"), 100);
		HAL_Delay(300);
		HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CIPCLOSE=0\r\n", strlen("AT+CIPCLOSE=0\r\n"), 100);
		HAL_Delay(300);
	}else if((position = string_contains((char*)buffer, "SET_LED_RGB:", buffer_index)) != -1
			&& string_contains((char*)buffer, TOKEN, buffer_index) != -1)
	{
		uint8_t red, green, blue;
		red = atoi((char*)&buffer[position + 12]);
		green = atoi((char*)&buffer[position + 16]);
		blue = atoi((char*)&buffer[position + 20]);
		__HAL_TIM_SET_COMPARE(&htim2, TIM_CHANNEL_1, red);
		__HAL_TIM_SET_COMPARE(&htim2, TIM_CHANNEL_2, green);
		__HAL_TIM_SET_COMPARE(&htim2, TIM_CHANNEL_3, blue);
		HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CIPCLOSE=0\r\n", strlen("AT+CIPCLOSE=0\r\n"), 100);
	}else if(string_contains((char*)buffer, "+CWJAP:", buffer_index) != -1
			&& (string_contains((char*)buffer, "FAIL", buffer_index) != -1
			|| string_contains((char*)buffer, "DISCONNECT", buffer_index) != -1))
	{
		HAL_UART_Transmit(&huart1, (uint8_t*)WiFi_Credentials, strlen(WiFi_Credentials), 100);
	}
	ESP_Clear_Buffer();
	__HAL_UART_ENABLE_IT(&huart1, UART_IT_RXNE);
}
```
  
Main.c
```C
  /* USER CODE BEGIN 2 */
  HAL_TIM_PWM_Start(&htim2, TIM_CHANNEL_1);
  HAL_TIM_PWM_Start(&htim2, TIM_CHANNEL_2);
  HAL_TIM_PWM_Start(&htim2, TIM_CHANNEL_3);
  HAL_TIM_Base_Start_IT(&htim3);
  ESP_Clear_Buffer();
  __HAL_UART_ENABLE_IT(&huart1, UART_IT_RXNE);
  ESP_Server_Init();
  enable_w = 1;
  /* USER CODE END 2 */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
	  if(messageHandlerFlag)
	  {
		  messageHandlerFlag = 0;
		  messageHandler();
	  }
	  watchdog = 0;
  }
  /* USER CODE END 3 */
```  
  
stm32f1xx_it.c
```C
/**
  * @brief This function handles Hard fault interrupt.
  */
void HardFault_Handler(void)
{
  /* USER CODE BEGIN HardFault_IRQn 0 */
	__NVIC_SystemReset();
  /* USER CODE END HardFault_IRQn 0 */
  while (1)
  {
    /* USER CODE BEGIN W1_HardFault_IRQn 0 */
    /* USER CODE END W1_HardFault_IRQn 0 */
  }
}

/**
  * @brief This function handles TIM3 global interrupt.
  */
void TIM3_IRQHandler(void)
{
  /* USER CODE BEGIN TIM3_IRQn 0 */
	if(timeout != 0 && timeout < 10)
		timeout++;

	if(timeout > 5)
	{
		timeout = 0;
		messageHandlerFlag = 1;
	}

	if(enable_w == 1)
	{
		watchdog++;
		if(watchdog>250)
			__NVIC_SystemReset();
	}
  /* USER CODE END TIM3_IRQn 0 */
  HAL_TIM_IRQHandler(&htim3);
  /* USER CODE BEGIN TIM3_IRQn 1 */

  /* USER CODE END TIM3_IRQn 1 */
}

/**
  * @brief This function handles USART1 global interrupt.
  */
void USART1_IRQHandler(void)
{
  /* USER CODE BEGIN USART1_IRQn 0 */
	timeout = 1;

	if(buffer_index < 2000)
		HAL_UART_Receive(&huart1, &buffer[buffer_index++], 1, 10);
	else
		HAL_UART_Receive(&huart1, &buffer[1999], 1, 10);
  /* USER CODE END USART1_IRQn 0 */
  HAL_UART_IRQHandler(&huart1);
  /* USER CODE BEGIN USART1_IRQn 1 */

  /* USER CODE END USART1_IRQn 1 */
}
```

![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/screenshot_rgb_command.png?raw=true)  

![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/screenshot_rgb_web.png?raw=true)  

###### Web Browser Screenshot
  
![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/Screenshot2.png?raw=true)  
  
###### LED Strip Voice Control - ScreenShot  
  
![alt text](https://github.com/viktorvano/RGB-LED-Strip/blob/main/screenshots%20and%20files/LED%20Strip%20Voice%20Control.png?raw=true)  

###### Code snippet of LED Strip Voice Control  
  
Message Parsing:  
```Java
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
```  
  
Server Socket:  
  
```Java
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
```
