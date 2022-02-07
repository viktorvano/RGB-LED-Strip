/*
 * myLibrary.c
 *
 *  Created on: Nov 7, 2020
 *      Author: vikto
 */

#include "myLibrary.h"

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
	HAL_Delay(200);
	ESP_Clear_Buffer();

	HAL_UART_Transmit(&huart1, (uint8_t*)"AT+RST\r\n", strlen("AT+RST\r\n"), 100);
	HAL_Delay(200);
	ESP_Clear_Buffer();

	HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CWMODE=1\r\n", strlen("AT+CWMODE=1\r\n"), 100);
	HAL_Delay(200);
	ESP_Clear_Buffer();

	HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CIPMUX=1\r\n", strlen("AT+CIPMUX=1\r\n"), 100);
	HAL_Delay(200);
	ESP_Clear_Buffer();

	HAL_UART_Transmit(&huart1, (uint8_t*)"AT+CIPSERVER=1,80\r\n", strlen("AT+CIPSERVER=1,80\r\n"), 100);
	HAL_Delay(200);
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
