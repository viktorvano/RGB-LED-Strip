################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include Drivers/STM32F1xx_HAL_Driver/Src/subdir.mk
-include Core/Startup/subdir.mk
-include Core/Src/subdir.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(S_DEPS)),)
-include $(S_DEPS)
endif
ifneq ($(strip $(S_UPPER_DEPS)),)
-include $(S_UPPER_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 
EXECUTABLES += \
STM32F103C8T6_ESP8266_LED_Strip.elf \

SIZE_OUTPUT += \
default.size.stdout \

OBJDUMP_LIST += \
STM32F103C8T6_ESP8266_LED_Strip.list \

OBJCOPY_BIN += \
STM32F103C8T6_ESP8266_LED_Strip.bin \


# All Target
all: STM32F103C8T6_ESP8266_LED_Strip.elf secondary-outputs

# Tool invocations
STM32F103C8T6_ESP8266_LED_Strip.elf: $(OBJS) $(USER_OBJS) D:\Google\ Drive\STM32CubeIDE\STM32F103C8T6_ESP8266_LED_Strip\STM32F103C8TX_FLASH.ld
	arm-none-eabi-gcc -o "STM32F103C8T6_ESP8266_LED_Strip.elf" @"objects.list" $(USER_OBJS) $(LIBS) -mcpu=cortex-m3 -T"D:\Google Drive\STM32CubeIDE\STM32F103C8T6_ESP8266_LED_Strip\STM32F103C8TX_FLASH.ld" --specs=nosys.specs -Wl,-Map="STM32F103C8T6_ESP8266_LED_Strip.map" -Wl,--gc-sections -static --specs=nano.specs -mfloat-abi=soft -mthumb -Wl,--start-group -lc -lm -Wl,--end-group
	@echo 'Finished building target: $@'
	@echo ' '

default.size.stdout: $(EXECUTABLES)
	arm-none-eabi-size  $(EXECUTABLES)
	@echo 'Finished building: $@'
	@echo ' '

STM32F103C8T6_ESP8266_LED_Strip.list: $(EXECUTABLES)
	arm-none-eabi-objdump -h -S $(EXECUTABLES) > "STM32F103C8T6_ESP8266_LED_Strip.list"
	@echo 'Finished building: $@'
	@echo ' '

STM32F103C8T6_ESP8266_LED_Strip.bin: $(EXECUTABLES)
	arm-none-eabi-objcopy  -O binary $(EXECUTABLES) "STM32F103C8T6_ESP8266_LED_Strip.bin"
	@echo 'Finished building: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) *
	-@echo ' '

secondary-outputs: $(SIZE_OUTPUT) $(OBJDUMP_LIST) $(OBJCOPY_BIN)

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
