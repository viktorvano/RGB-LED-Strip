/*
 * myLibrary.h
 *
 *  Created on: Nov 7, 2020
 *      Author: vikto
 */

#ifndef INC_MYLIBRARY_H_
#define INC_MYLIBRARY_H_

#include <stdlib.h>
#include <string.h>
#include "main.h"
#include "core_cm3.h"

#define WiFi_Credentials	"AT+CWJAP=\"WiFiSSID\",\"WiFiPASSWORD\"\r\n"
#define TOKEN				"fe5g8e2a5f4e85d2e85a7c5"

extern TIM_HandleTypeDef htim2;
extern TIM_HandleTypeDef htim3;
extern UART_HandleTypeDef huart1;
uint8_t buffer[2000];
uint16_t buffer_index, timeout, messageHandlerFlag, watchdog, enable_w;

void ESP_RESET();
void ESP_Server_Init();
void ESP_Clear_Buffer();
uint8_t string_compare(char array1[], char array2[], uint16_t length);
int string_contains(char bufferArray[], char searchedString[], uint16_t length);
void messageHandler();

#endif /* INC_MYLIBRARY_H_ */
