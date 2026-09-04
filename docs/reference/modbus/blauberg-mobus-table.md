# **Modbus table for connecting S21 automation to BMS** 

1 

## **TO ENABLE OPERATION VIA THE MODBUS RTU PROTOCOL THROUGH THE RS-485 INTERFACE, DISCONNECT ALL THE WIRED CONTROL PANELS CONNECTED TO THE AIR HANDLING UNIT THROUGH THIS INTERFACE** 

**SIMULTANEOUS OPERATION THROUGH RS-485, WI-FI, AND ETHERNET INTERFACES IS POSSIBLE** 

**TO USE WIRED CONTROL PANELS, THE BMS MUST BE CONNECTED THROUGH WI-FI AND/OR ETHERNET INTERFACES VIA MODBUS TCP PROTOCOL** 

## **MODBUS PARAMETERS** 

|||**Modbus RTU**<br>|||
|---|---|---|---|---|
|**Baud rate**|**Number of data bits**|**Stop bits**|**Parity type**|**Address**|
|9600||1|None (bydefault)|1-16|
|14400||1.5|even|1 (bydefault)|
|19200|8|2 (bydefault)|odd||
|38400|||||
|57600|||||
|115200 (bydefault)|||||



|||**Modbus TCP**||
|---|---|---|---|
|**IP address***|**Port**|**Maximum number of simultaneous TCP**<br>**connections**|**TCP connection timeout**|
|Static|502|For Ethernet = 1, for Wi-Fi = 1|30 seconds|
|DHCP (bydefault)||||



*Wi-Fi IP address in access point mode – 192.168.4.1 

The RS-485, Wi-Fi, and Ethernet network parameters for air handling units are configured using a mobile application. Maximum number of registers in one package: 125 (for 16-bit registers) and 2000 (for 1-bit registers). Supported modbus functions: 1, 2, 3, 4, 5, 6, 15, 16. 

2 

|**ress**<br><br>**iable**<br>**cription**|**imum value**|**ximum value**|**-set value**|**asurement units**|**del**<br>**ension**|
|---|---|---|---|---|---|
|**Add**<br>**R/W**<br>**Var**<br>**Des**|**Min**|**Ma**|**Pre**|**Me**|**Mo**<br>**Dim**|
|**Coils (1 bit registers)- modbus functions: 1, 5, 15**<br>0<br>R/W<br>CL_POWER<br>Coils (1-bit registers)-modbus functions: 1, 5, 15<br><br><br>|0|1|0|—|Bool<br>1<br>|
|1<br>R/W<br>CL_TIMER<br>Unit On/Of<br>2<br>R/W<br>CLWEEK<br>Main timer|0<br>0|1<br>1|0<br>0|—<br>—|Bool<br>1<br>Bool<br>1|
|_<br>3<br>R<br>CL_Boost_MODE<br>Weekly Schedule<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|4<br>R<br>CL_FPLC_MODE<br>Boost mode<br><br><br><br>|0<br>|1<br>|—<br>|—|Bool<br>1<br><br>|
|5<br>R/W<br>CL_IntRH_CTRL<br>Fireplace mode<br>6<br>R/W<br>CLExtRHCTRL<br>Main humidity sensor activation|0<br>0|1<br>1|0<br>0|—<br>—|Bool<br>1<br>Bool<br>1|
|__<br> <br>7<br>R/W<br>CL_IntCO2_CTRL<br>External humidity sensor activation<br><br><br><br>|0<br>|1<br>|0<br>|—|Bool<br>1<br><br>|
|8<br>R/W<br>CL_ExtCO2_CTRL<br>Main CO~~2~~sensor activation<br><br><br><br>|0<br>|1<br>|0<br>|—|Bool<br>1<br><br>|
|9<br>R/W<br>CL_IntPM2_5_CTRL<br>External CO~~2~~sensor activation|0|1|0|—|Bool<br>1|
|10<br>R/W<br>CL_ExtPM2_5_CTRL<br>Main PM2.5 sensor activation<br>11<br>R/W<br>CLIntVOCCTRL<br>External PM25 sensor activation|0<br>0|1<br>1|0<br>0|—<br>—|Bool<br>1<br>Bool<br>1|
|__<br>.<br>12<br>R/W<br>CL_ExtVOC_CTRL<br>Main VOC sensor activation<br><br><br><br>|0<br>|1<br>|0<br>|—|Bool<br>1<br><br>|
|13<br>R/W<br>CL_BoostSWITCH_CTRL<br>External VOC sensor activation<br>14<br>R/W<br>CLFplcSWITCHCTRL<br>Input activation for the boost mode switch|0<br>0|1<br>1|1<br>1|—<br>—|Bool<br>1<br>Bool<br>1|
|__<br> <br>15<br>R/W<br>CL_FireALARM_CTRL<br>Input activation for the freplace mode switch<br><br><br><br>|0<br>|1<br>|0<br>|—|Bool<br>1<br><br>|
|16<br>R/W<br>CL_10V_SENSOR_CTRL<br>Fire alarm sensor activation<br><br><br><br>|0<br>|1<br>|0|—|Bool<br>1<br><br>|
|17<br>W<br>CL_RESET_FILTER_TIMER<br>Input activation for the external control device 0-10 V<br>18<br>W<br>CLRESETALARM<br>Reset timer countdown to flter replacement|1<br>1|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|__<br> <br>19<br>W<br>CL_RESTORE_FACTORY<br>Reset all alarms<br><br><br><br>|1<br>|1<br>|—<br>|—|Bool<br>1<br><br>|
|20<br>R/W<br>CL_CLOUD_CTRL<br>Restore everything to factory settings<br><br><br><br>|0<br>|1<br>|0<br>|—|Bool<br>1<br><br>|
|21<br>R/W<br>CL_MinSuAirOutTEMP_CTRL<br>Activation of control via cloud server<br>22<br>R/W<br>CLWaterPRESSCTRL<br>Minimum room supply air temperature control|0<br>0|1<br>1|1<br>1|—<br>—|Bool<br>1<br>Bool<br>1|
|__<br> <br>23<br>R/W<br>CLWaterFLOWCTRL<br>Heat medium water ressure sensor activation|0|1|0|—|Bool<br>1|
|__<br>p<br>24<br>R/W<br>CL_WaterHeaterAutoRestart<br>Heat medium water fow sensor activation<br>**Discrete Inputs (1-bit registers)- modbus functions: 2**|0|1|1|—|Bool<br>1|
|<br>0<br>R<br>DI_CurBoostSWITCH<br>Current input status for the Boost mode switch<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|1<br>R<br>DI_CurFplcSWITCH<br>Current input status for the Fireplace mode switch<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|2<br>R<br>DI_CurFireALARM<br>Current status of the fre alarm sensor|0|1|—|—|Bool<br>1|
|3<br>R<br>DIStatusRH<br>Humidity setpoint excess indication|0|1|—|—|Bool<br>1|
|_<br> <br>4<br>R<br>DIStatusCO2<br>COsetpoint excess indication|0|1|—|—|Bool<br>1|
|_<br>~~2~~ <br>5<br>R<br>DI_StatusPM2_5<br>PM2.5 setpoint excess indication<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|6<br>R<br>DI_StatusVOC<br>VOC setpoint excess indication|0|1|—|—|Bool<br>1|
|7<br>R<br>DIStatusHEATER<br>Heater operation indication|0|1|—|—|Bool<br>1|
|_<br> <br>8<br>R<br>DIStatusCOOLER<br>Cooler operation indication|0|1|—|—|Bool<br>1|
|_<br> <br>9<br>R<br>DI_StatusFanBLOWING<br>Electric heater blowdown indication<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|10<br>R<br>DI_CurPreHeaterThermostat<br>Current input status for the preheating thermostat|0|1|—|—|Bool<br>1|
|11<br>R<br>DI_CurMainHeaterThermostat Current input status for the reheating thermostat<br>12<br>R<br>DICurSuFilterPRESS<br>Current input status for the diferential pressure switch of the suppl flter|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|_<br> y<br>13<br>R<br>DI_CurExFilterPRESS<br>Current input status for the diferential pressure switch of the extract flter<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|14<br>R<br>DI_CurWaterPRESS<br>Current status of the heat medium water pressure sensor<br>|0|1|—|—|Bool<br>1|
|15<br>R<br>DI_CurWaterFLOW<br>Current status of the heat medium water fow sensor<br>16<br>R<br>DICurSuFanPRESS<br>Current input status for the diferential pressure switch of the suppl fan|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|_<br> y<br>17<br>R<br>DI_CurExFanPRESS<br>Current input status for the diferential pressure switch of the extract fan<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|18<br>R<br>DI_WaterPreheatingStatus<br>Return water heating indicator before the air handling unit start-up<br>19<br>R<br>DIAlarmCODE0<br>Alarm indicator with code No. 0|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|_<br>20<br>R<br>DI_AlarmCODE1<br>Alarm indicator with code No. 1<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|21<br>R<br>DI_AlarmCODE2<br>Alarm indicator with code No. 2<br>22<br>R<br>DI_AlarmCODE3<br>Alarm indicator with code No. 3|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|23<br>R<br>DI_AlarmCODE4<br>Alarm indicator with code No. 4<br>24<br>R<br>DIAlarmCODE5<br>Alarm indicator with code No 5|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|_<br>.<br>25<br>R<br>DI_AlarmCODE6<br>Alarm indicator with code No. 6<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|26<br>R<br>DI_AlarmCODE7<br>Alarm indicator with code No. 7|0|1|—|—|Bool<br>1|
|27<br>R<br>DI_AlarmCODE8<br>Alarm indicator with code No. 8<br>28<br>R<br>DIAlarmCODE9<br>Alarm indicator with code No 9|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|_<br>.<br>29<br>R<br>DI_AlarmCODE10<br>Alarm indicator with code No. 10<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|30<br>R<br>DI_AlarmCODE11<br>Alarm indicator with code No. 11<br>31<br>R<br>DI_AlarmCODE12<br>Alarm indicator with code No. 12<br>32<br>R<br>DIAlarmCODE13<br>Alarm indicator with code No 13|0<br>0<br>0|1<br>1<br>1|—<br>—<br>—|—<br>—<br>—|Bool<br>1<br>Bool<br>1<br>Bool<br>1|
|_<br>.<br>33<br>R<br>DI_AlarmCODE14<br>Alarm indicator with code No. 14<br><br><br><br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|34<br>R<br>DI_AlarmCODE15<br>Alarm indicator with code No. 15|0|1|—|—|Bool<br>1|
|35<br>R<br>DI_AlarmCODE16<br>Alarm indicator with code No. 16|0|1|—|—|Bool<br>1|
|36<br>R<br>DI_AlarmCODE17<br>Alarm indicator with code No. 17<br>37<br>R<br>DI_AlarmCODE18<br>Alarm indicator with code No. 18<br><br><br><br>|0<br>0<br>|1<br>1<br>|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1<br><br>|
|38<br>R<br>DI_AlarmCODE19<br>Alarm indicator with code No. 19|0|1|—|—|Bool<br>1|
|39<br>R<br>DI_AlarmCODE20<br>Alarm indicator with code No. 20<br>40<br>R<br>DIAlarmCODE21<br>Alarm indicator with code No 21|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|_<br>.<br>41<br>R<br>DIAlCODE22<br>Al idi ih d N 22|0|1|||Bl<br>1|
|_arm<br>arm ncator wt coe o.<br><br><br><br>|||—|—|oo<br><br><br>|
|42<br>R<br>DI_AlarmCODE23<br>Alarm indicator with code No. 23<br>43<br>R<br>DI_AlarmCODE24<br>Alarm indicator with code No. 24|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|



3 

|**dress**<br>|**iable**|**scription**|**imum value**|**ximum value**|**-set value**|**asurement units**|**del**<br>**ension**|
|---|---|---|---|---|---|---|---|
|**Ad**<br>**R/W**|**Var**<br>|**De**<br>|**Min**|**Ma**|**Pre**|**Me**|**Mo**<br>**Dim**<br>|
|44<br>R<br>45<br>R<br>46<br>R<br><br>|DI_AlarmCODE25<br>DI_AlarmCODE26<br>DI_AlarmCODE27<br>|Alarm indicator with code No. 25<br>Alarm indicator with code No. 26<br>Alarm indicator with code No. 27<br>|0<br>0<br>0<br>|1<br>1<br>1<br>|—<br>—<br>—|—<br>—<br>—|Bool<br>1<br>Bool<br>1<br>Bool<br>1<br><br>|
|47<br>R<br>48<br>R<br>49<br>R|DI_AlarmCODE28<br>DI_AlarmCODE29<br>DIAlarmCODE30|Alarm indicator with code No. 28<br>Alarm indicator with code No. 29<br>Alarm indicator with code No. 30|0<br>0<br>0|1<br>1<br>1|—<br>—<br>—|—<br>—<br>—|Bool<br>1<br>Bool<br>1<br>Bool<br>1|
|50<br>R<br><br>|_<br>DI_AlarmCODE31<br>|Alarm indicator with code No. 31<br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|51<br>R<br><br>|DI_AlarmCODE32<br>|Alarm indicator with code No. 32<br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|52<br>R|DI_AlarmCODE33|Alarm indicator with code No. 33|0|1|—|—|Bool<br>1|
|53<br>R|DI_AlarmCODE34|Alarm indicator with code No. 34|0|1|—|—|Bool<br>1|
|54<br>R<br>55<br>R|DI_AlarmCODE35<br>DIAlCODE36|Alarm indicator with code No. 35<br>Al idit ith d N 36|0<br>0|1<br>1|—<br>|—<br>|Bool<br>1<br>Bl<br>1|
||_arm<br>|arm ncaor w coe o.<br>|||—|—|oo<br><br><br>|
|56<br>R<br>57<br>R|DI_AlarmCODE37<br>DI_AlarmCODE38|Alarm indicator with code No. 37<br>Alarm indicator with code No. 38|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|58<br>R<br>59<br>R<br><br>|DI_AlarmCODE39<br>DI_AlarmCODE40<br>|Alarm indicator with code No. 39<br>Alarm indicator with code No. 40<br>|0<br>0<br>|1<br>1<br>|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1<br><br>|
|60<br>R<br>61<br>R|DI_AlarmCODE41<br>DI_AlarmCODE42|Alarm indicator with code No. 41<br>Alarm indicator with code No. 42|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|62<br>R<br>63<br>R<br><br>|DI_AlarmCODE43<br>DI_AlarmCODE44<br>|Alarm indicator with code No. 43<br>Alarm indicator with code No. 44<br>|0<br>0<br>|1<br>1<br>|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1<br><br>|
|64<br>R<br><br>|DI_AlarmCODE45<br>|Alarm indicator with code No. 45<br>|0<br>|1<br>|—|—|Bool<br>1<br><br>|
|65<br>R|DI_AlarmCODE46|Alarm indicator with code No. 46|0|1|—|—|Bool<br>1|
|66<br>R|DI_AlarmCODE47|Alarm indicator with code No. 47|0|1|—|—|Bool<br>1|
|67<br>R<br>68<br>R|DI_AlarmCODE48<br>DIAlarmCODE49|Alarm indicator with code No. 48<br>Alarm indicator with code No 49|0<br>0|1<br>1|—<br>—|—<br>—|Bool<br>1<br>Bool<br>1|
|69<br>R<br>|_<br>DI_AlarmCODE50<br>|.<br>Alarm indicator with code No. 50<br>|0|1|—|—|Bool<br>1<br>|
|70<br>R|DI_AlarmCODE51|Alarm indicator with code No. 51|0|1|—|—|Bool<br>1|
|71<br>R|DIAlarmCODE52|Alarm indicator with code No. 52|0|1|—|—|Bool<br>1|
||_|**Int Ritr (16-bit ritr)- mdb fntin 4**||||||
|0<br>R|IR_CurSelTEMP|**pu egses egses ous ucos:**<br>Current temperature of the selected sensor, which controls the air|-32768|+32767|—|°C|Short Int<br>1|
|||temperature (see HR53).<br>  °||||||
|||Value 250=25.0C.-32768-no sensor, +32767-short circuit<br>|||||<br>|
|1<br>R|IR_CurTEMP_SuAirIn|Current temperature of the main outdoor air sensor before preheating.<br>Value 250=250°C-32768-no sensor +32767-short circuit|-32768|+32767|—|°C|Short Int<br>1|
|2<br>R|IR_CurTEMP_SuAirOut|.. , <br>Current temperature of the main supply air temperature sensor at the unit<br>|-32768|+32767|—|°C|Short Int<br>1|
|||outlet downstream of the reheater.<br>Value 250=25.0°C.-32768-no sensor, +32767-short circuit<br>||||°|<br>|
|3<br>R|IR_CurTEMP_ExAirIn|Current extract air temperature at the unit inlet.<br>Value 250=25.0°C.-32768-no sensor +32767-short circuit|-32768|+32767|—|C|Short Int<br>1|
|4<br>R|IR_CurTEMP_ExAirOut|, <br>Current exhaust air temperature at the unit outlet.<br>|-32768|+32767|—|°C|Short Int<br>1|
|5<br>R|IR_CurTEMP_Ext|Value 250=25.0°C.-32768-no sensor, +32767-short circuit<br>Current temperature of the outdoor air temperature sensor (in the control|-32768|+32767|—|°C|Short Int<br>1|
|||<br>panel, ...).<br>||||||
|||Value 250=25.0°C.-32768-no sensor, +32767-short circuit||||||
|8<br>R|IR_CurTEMP_Water|Return heat medium temperature.<br>  °|-32768|+32767|—|°C|Short Int<br>1|
|9<br>R|IR_CurVBAT|Value 250=25.0C.-32768-no sensor, +32767-short circuit<br>Current battery voltage for RTC.|0|5000|—|mV|Unsigned<br>Short Int<br>1|
|10<br>R<br><br>|IR_CurRH_Int<br>|Current humidity of the main sensor. 0–no sensor<br>|0<br>|100<br>|—|%<br>|<br>Byte<br>1<br><br>|
|11<br>R<br>12<br>R|IR_CurRH_Ext<br>IR_CurCO2_Int|Current humidity of the outdoor sensor. 0–no sensor<br>Current CO2level of the main sensor. 0 – no sensor|0<br>0|100<br>10000|—<br>—|%<br>ppm|Byte<br>1<br><br>Unsigned<br>Short Int<br>1|
|13<br>R|IRCurCO2Ext|Current COlevel of the external sensor 0 – no sensor|0|10000|—|m|<br><br>Unsined<br>1|
||__|2.||||pp|g<br> <br>|
|14<br>R|IR_CurPM2_5_Int|Current PM2.5 level of the main sensor. 0 – no sensor|0|1000|—|µg/m|Short Int<br>³ Unsigned<br>Short Int<br>1|
|||ll f h l|||||<br>³ <br>|
|15<br>R|IR_CurPM2_5_Ext|Current PM2.5 eve o te externa sensor. 0 – no sensor|0|1000|—|µg/m|Unsigned<br> <br>1|
|16<br>R<br>17<br>R|IR_CurVOC_Int<br>IRCurVOCExt|Current VOC level of the main sensor. 0–no sensor<br>Current VOC level of the external sensor 0–no sensor|0<br>0|100<br>100|—<br>—|%<br>%|Short Int<br>Byte<br>1<br>Bte<br>1|
|18<br>R|__<br>IR_Cur10V_SENSOR|. <br>Current value of the 0-10 V sensor|0|100|—|%|y<br><br>Unsigned<br> <br>1|
|19<br>R|IR_CurSuAirFLOW|Current supply air fow|0|10000|—|m³/h|Short Int<br><br>Unsigned<br>Sh I<br>1|
|20<br>R|IR_CurExAirFLOW|Current exhaust air fow|0|10000|—|m³/h|ort nt<br><br>Unsigned<br>Short Int<br>1|
|21<br>R|IRCurSuPRESS|Current pressure in the supply air duct|0|10000|—|Pa|Unsigned<br>1|
||_||||||<br>Sht It<br>|
|22<br>R|IRCurExPRESS|Current pressure in the exhaust air duct|0|10000|—|Pa|or n<br>Unsigned<br>1|
||_||||||<br>Short Int|



4 

|**Address**<br>**R/W**|**Variable**|**Description**<br>|**Minimum value**|**Maximum value**|**Pre-set value**|**Measurement units**|**Model**<br>**Dimension**<br>|
|---|---|---|---|---|---|---|---|
|23<br>R|IR_SuRPM|Supply fan speed|0|5000|—|rpm|Unsigned<br>Short Int<br>1|
|24<br>R|IR_ExRPM|Extract fan speed|0|5000|—|rpm|Unsigned<br>Sht It<br>1|
|25<br>R|IR_CurTIMER_TIME|Current countdown time of the main timer|0<br>0|59<br>59|—<br>—|Min.<br>Sec.|or n<br>Byte<br>2<br>Byte<br>|
||||0|—<br>23|—|Hours|Byte<br>Byte|
|27<br>R|IR_CurFILTER_TIMER|Countdown time of the flter replacement timer|0<br>0<br>0|23<br>59<br>365|—<br>—<br>—|Hours<br>Min.<br>Days|Byte<br>2<br>Byte<br>Unsigned<br>|
|29<br>R|IR_TotalWorkingTime|Motor hours|0<br>0<br>0|23<br>59<br>65535|—<br>—<br>—|Hours<br>Min.<br>Days|Short Int<br>Byte<br>2<br>Byte<br>Unsigned|
||||||||Short Int|
|31<br>R|IR_StateFILTER|Filter condition:<br>0 - clean, 1 - the intake supply flter is clogged, 2 - the extract flter is clogged,<br>3 - both flters are clogged or the flter replacement timer has gone of<br>(hihest priorit)|0|3|—|—|<br>Byte<br>1|
|32<br>R|IR_CurWeekSpeed|g y<br>Current speed in Weekly schedule mode:<br>0 - Standby<br>1 - Speed 1<br>2 - Speed 2<br>3 - Speed 3|0|5|—|—|Byte<br>1|
|||4 - Speed 4||||||
|||<br>5-Speed 5||||||
|33<br>R|IR_CurWeekSetTemp|<br>Current temperature setpoint in Weekly schedule mode:<br>0-ventilation only, +15 ... + 30°C|0|30|—|°C|Byte<br>1|
|34<br>R|IRVerMAINFMW|<br>Firmware version|0|255|—|Major|Byte<br>3|
||__||0|255|—|Minor|Byte|
|||Firmware creation date|1<br>1|31<br>12|—<br>—|Day<br>Month|Byte<br>Bte|
||||0|65535|—|Year|y<br>Unsigned<br>Short Int|
|37<br>R|IR_DeviceTYPE|Device type (controller): 1 – S21|0|65535|—|—|<br>Unsigned<br>Short Int<br>1|
|38<br>R|IR_ALARM|Alarm/warning indicator:<br>0 – no<br>|0|2|—|—|<br>Byte<br>1|
|||1 – alarm (highest priority)||||||
|||2–warning<br>||||||
|39<br>R|IR_RH_U|Control signal from the PID humidity controller|0|100|—|%|Byte<br>1|
|40<br>R<br>41<br>R|IR_CO2_U<br>IR_PM2_5_U|Control signal from the PID CO~~2~~level controller<br>Control signal from the PID PM2.5 level controller<br>|0<br>0|100<br>100|—<br>—|%<br>%|Byte<br>1<br>Byte<br>1|
|42<br>R<br>43<br>R|IR_VOC_U<br>IRPHtU|Control signal from the PID VOC level controller<br>Ctl il f th PID hti tll|0<br>0|100<br>100|—<br>—|%<br>%|Byte<br>1<br>Bt<br>1|
||_reeaer_<br>|onro sgna rom e  preeang conroer<br>|||||ye<br><br><br>|
|44<br>R|IR_MainHeater_U|Control signal from the PID reheating controller|0|100|—|%|Byte<br>1|
|45<br>R|IRBPSROTORU|Control signal from the PID bypass/rotary heat exchanger controller|0|100|—|%|Byte<br>1|
|46<br>R<br>47<br>R|___<br>IR_KKB_U<br>IR_ReturnWater_U|<br>Control signal from the PID condenser unit controller<br>Control signal from the PID return heat medium controller|0<br>0|100<br>100|—<br>—|%<br>%|Byte<br>1<br>Byte<br>1|
|48<br>R|IR_SuAirOutSetTemp|<br>Temperature setpoint in the supply air duct. Calculated automatically when<br>the room sensor or the sensor in the exhaust air duct is selected.<br>  °|100|400|—|°C|Short Int<br>1|
|||Value 250=25.0C<br>|||||<br>|
|49<br>R|IR_WaterStandbySetTemp|Return heat medium temperature setpoint during winter in Standby mode.<br>Calculated automaticall depending on the outdoor temperature|100|400|—|°C|Short Int<br>1|
|||y     .<br>Value 250=25.0°C||||||
|50<br>R|IRWaterStartSetTem|<br>Retrn heat medim temeratre setoint in winter before the air|300|600|—|°C|Short Int<br>1|
||_p|u  u pu p<br>handling unit start-up. Calculated automatically depending on the outdoor<br>temperature.<br>Value 350=35.0°C|||||<br>|



5 

|**Address**<br>**R/W**|**Variable**|**Description**|**Minimum value**|**Maximum value**|**Pre-set value**|**Measurement units**|**Model**<br>**Dimension**|
|---|---|---|---|---|---|---|---|
|||**Holding Register (16-bit registers)- modbus functions: 3, 6**<br>l d|**, 16**<br>|||||
|0<br>R|HR_VENTILATION_MODE|Ventiation moe:<br>0-mode 0 ... 100%, 1-constant fow, 2-constant pressure|0|2|1|—|Byte<br>1|
|1<br>R|HRMaxSPEEDMODE|<br>Maximum permissible speed number|3|5|3|—|Byte<br>1|
|2<br>R/W|__<br>HR_SPEED_MODE|<br>Speed number:|1|255|1|—|Byte<br>1|
|||1 – Speed 1, 2 – Speed 2, 3 – Speed 3, 4 - Speed 4, 5 - Speed 5, 255 – manual<br>seed settin mode (see HR17)||||||
|3<br>R<br><br>|HR_MinSPEED<br>|p g<br>Minimum possible fan speed<br>|0<br>|100<br>|30<br>|%<br>|Byte<br>1<br><br>|
|4<br>R<br>5<br>R/W|HR_MaxSPEED<br>HRSuSPEED0|Maximum possible fan speed<br>Supply fan speed in Standby mode|0<br>0|100<br>100|100<br>0|%<br>%|Byte<br>1<br>Byte<br>1|
|6<br>R/W<br>7<br>R/W<br><br>|_<br>HR_ExSPEED0<br>HR_SuSPEED1<br>|<br>Extract fan speed in Standby mode<br>Supply fan speed in Speed 1 mode<br>|0<br>0<br>|100<br>100<br>|0<br>40<br>|%<br>%<br>|Byte<br>1<br>Byte<br>1<br><br>|
|8<br>R/W|HR_ExSPEED1|Extract fan speed in Speed 1 mode|0|100|40|%|Byte<br>1|
|9<br>R/W|HRSuSPEED2|Supply fan speed in Speed 2 mode|0|100|70|%|Byte<br>1|
|10<br>R/W<br><br>RW|_<br>HR_ExSPEED2<br>HRSSPEED|<br>Extract fan speed in Speed 2 mode<br>Sl f d  Sd  d|0<br>|100<br>|70<br>|%<br>|Byte<br>1<br>B<br>|
|11<br>/<br>12<br>R/W|_u3<br>HR_ExSPEED3|uppy an spee in pee 3 moe<br>Extract fan speed in Speed 3 mode|0<br>0|100<br>100|100<br>100|%<br>%|yte<br>1<br>Byte<br>1|
|13<br>R/W<br>14<br>R/W|HR_SuSPEED4<br>HRExSPEED4|Supply fan speed in Speed 4 mode<br>Extract fan seed in Seed 4 mode|0<br>0|100<br>100|100<br>100|%<br>%|Byte<br>1<br>Bte<br>1|
|15<br>R/W<br><br>|_<br>HR_SuSPEED5<br>|p  p<br>Supply fan speed in Speed 5 mode<br>|0<br>|100<br>|100<br>|%<br>|y<br><br>Byte<br>1<br><br>|
|16<br>R/W|HR_ExSPEED5|Extract fan speed in Speed 5 mode|0|100|100|%|Byte<br>1|
|17<br>R/W|HR_ManualSPEED|Fan speed in manual speed setting mode<br>|0|100|50|%|Byte<br>1|
|||The balance between supply and exhaust air corresponds to the current<br>preset speeds 1-5||||||
|18<br>R/W|HRBlowingSPEED|<br>Fan speed while blowing electric heaters|0|100|50|%|Byte<br>1|
|19<br>R/W<br><br>|_<br>HR_Boost_SuSPEED<br>|<br>Supply fan speed in Boost mode<br>|0<br>|100<br>|100<br>|%<br>|Byte<br>1<br><br>|
|20<br>R/W<br>21<br>R/W<br>22<br>R/W|HR_Boost_ExSPEED<br>HR_FPLC_SuSPEED<br>HRFPLCExSPEED|Extract fan speed in Boost mode<br>Supply fan speed in Fireplace mode<br>Extract fan speed in Fireplace mode|0<br>0<br>0|100<br>100<br>100|100<br>60<br>40|%<br>%<br>%|Byte<br>1<br>Byte<br>1<br>Byte<br>1|
|23<br>R|__<br>HR_MinAirFLOW|<br>Minimum possible air fow of the unit|0|10000|—|m³/h|Unsigned<br> <br>1|
|24<br>R|HR_MaxAirFLOW|Maximum possible air fow of the unit|0|10000|—|m³/h|Short Int<br>Unsigned<br>Short Int<br>1|
|25<br>R/W|HR_SuSPEED0_FLOW|Supply air fow in Standby mode|0|10000|—|m³/h|<br>Unsigned<br>Short Int<br>1|
|26<br>R/W|HR_ExSPEED0_FLOW|Extract air fow in Standby mode|0|10000|—|m³/h|Unsigned<br>Sh I<br>1|
|27<br>R/W|HR_SuSPEED1_FLOW|Supply air fow in Speed 1 mode|0|10000|—|m³/h|ort nt<br>Unsigned<br>Short Int<br>1|
|28<br>R/W|HR_ExSPEED1_FLOW|Extract air fow in Speed 1 mode|0|10000|—|m³/h|<br>Unsigned<br> <br>1|
|29<br>R/W|HR_SuSPEED2_FLOW|Supply air fow in Speed 2 mode|0|10000|—|m³/h|Short Int<br>Unsigned<br>Short Int<br>1|
|30<br>R/W|HR_ExSPEED2_FLOW|Extract air fow in Speed 2 mode|0|10000|—|m³/h|<br>Unsigned<br> <br>1|
|31<br>R/W|HR_SuSPEED3_FLOW|Supply air fow in Speed 3 mode|0|10000|—|m³/h|Short Int<br>Unsigned<br>Sht It<br>1|
|32<br>R/W|HR_ExSPEED3_FLOW|Extract air fow in Speed 3 mode|0|10000|—|m³/h|or n<br>Unsigned<br>Short Int<br>1|
|33<br>R/W|HR_SuSPEED4_FLOW|Supply air fow in Speed 4 mode|0|10000|—|m³/h|<br>Unsigned<br> <br>1|
|34<br>R/W|HR_ExSPEED4_FLOW|Extract air fow in Speed 4 mode|0|10000|—|m³/h|Short Int<br>Unsigned<br>Short Int<br>1|
|||l  f  d  d||||³h|<br>d<br>|
|35<br>R/W|HR_SuSPEED5_FLOW|Suppy air ow in Spee 5 moe|0|10000|—|m/|Unsigne<br> <br>1|
|36<br>R/W|HR_ExSPEED5_FLOW|Extract air fow in Speed 5 mode|0|10000|—|m³/h|Short Int<br>Unsigned<br>Sht It<br>1|
|37<br>R|HR_MinAirPRESS|Minimum possible pressure in the air duct|0|10000|—|Pa|or n<br>Unsigned<br>1|
|38<br>R|HR_MaxAirPRESS|Maximum possible pressure in the air duct|0|10000|—|Pa|Short Int<br>Unsigned<br> <br>1|
|39<br>R/W|HR_SuSPEED0_PRESS|Pressure in the supply air duct in Standby mode|0|10000|—|Pa|Short Int<br>Unsigned<br>Short Int<br>1|
|40<br>R/W|HR_ExSPEED0_PRESS|Pressure in the exhaust air duct in Standby mode|0|10000|—|Pa|<br>Unsigned<br> <br>1|
|41<br>R/W|HRSuSPEED1PRESS|Pressure in the supply air duct in Speed 1 mode|0|10000|—|Pa|Short Int<br>Unsigned<br>1|
||__||||||<br>Short Int|
|42<br>R/W|HR_ExSPEED1_PRESS|Pressure in the exhaust air duct in Speed 1 mode|0|10000|—|Pa|<br>Unsigned<br>1|
||||||||<br>Short Int|



6 

|**Address**<br>**R/W**<br>**Variable**|**Description**|**Minimum value**|**Maximum value**|**Pre-set value**|**Measurement units**|**Model**<br>**Dimension**|
|---|---|---|---|---|---|---|
|43<br>R/W<br>HR_OPERATION_MODE|Unit operation mode:<br>0-ventilation onl 1-heatin 2-coolin 3-auto|0|3|3|—|Byte<br>1|
|44<br>R/W<br>HR_SetTEMP<br><br><br>|yg,g, <br>Room temperature setpoint in normal mode<br>|15<br>|30<br>|23<br>|°C<br>|Byte<br>1<br><br>|
|45<br>R/W<br>HR_SetRH|Humidity threshold setpoint<br>|40|80|60|%RH|Byte<br>1|
|46<br>R/W<br>HR_SetCO2<br>47<br>R/W<br>HRSetPM25|CO~~2~~threshold setpoint<br>PM25 threshold setpoint|400<br>100|2000<br>1000|1200<br>400|ppm<br>µg/m³|Byte<br>1<br>Byte<br>1|
|__<br>48<br>R/W<br>HR_SetVOC<br><br><br>|.<br>VOC threshold setpoint<br>|20<br>|100<br>|40<br>|%|Byte<br>1<br><br>|
|49<br>R/W<br>HR_TIMER_MODE|Timer mode:<br>0-Standby 1-Speed 1 2-Speed 2 3-Speed 3 4-Speed 4 5-Speed 5|0|5|1|—|Byte<br>1|
|50<br>R/W<br>HR_SetTIMER_TEMP|,,,,, <br>Room temperature setpoint for the main timer:<br>  °|0|30|23|°C|Byte<br>1|
|51<br>R/W<br>HRSetTIMERTIME|0-ventilation only, +15...+ 30C<br>Time setpoint of the main timer|0|23|0|Hours|Byte<br>1|
|__||0|59|30|Min|Byte|
|52<br>R/W<br>HR_SetTEMP_WinterSummer|Transition temperature winter/summer|5|15|7|.<br>°C|Byte<br>1|
|53<br>R/W<br>HR_SelTEMP_SENSOR|Selecting a temperature sensor for controlling room temperature:<br>0 - in the exhaust air duct, 1 - external sensor in the control panel, 2 - in the<br>l  d|0|2|2|—|Byte<br>1|
|54<br>R/W<br>HR_MainHEATER_TYPE|suppy air uct<br>Main heater type:<br>0-turn of1-electric 2-water|0|2|—|—|Byte<br>1|
|55<br>R/W<br>HR_COOLER_TYPE|,, <br>Cooler control type:<br>0-turn of1-discrete 2-analogue 0-10 V (integrated)|0|2|—|—|Byte<br>1|
|56<br>R/W<br>HR_DEF_MODE|,,  <br>Heat exchanger Freeze protection mode:<br>|0|3|—|—|Byte<br>1|
|57<br>R<br>HR_BPS_ROTOR_TYPE|0-turn of, 1-preheating, 2-bypass/rotor, 3-fan imbalance<br>Bypass/rotary heat exchanger type:|0|4|—|—|Byte<br>1|
||<br>0 - not available, 1 - bypass with two-point control, 2 - bypass with analogue<br>control, 3 - rotary heat exchanger with discrete control, 4 - rotary heat<br>exchanger with analogue control 5-bypass with three-point control||||||
|58<br>R/W<br>HR_SetFILTER_TIMER|,  <br>Filter timer setpoint: 0 - turn of the timer, 70...365 days|0|365|90|Days|Unsigned<br>Sht It<br>1|
|59<br>R/W<br>HR_BoostDelaySwitchingOf|Setpoint of the Boost mode turn-ofdelay|0|60|0|Min.|or n<br>Byte<br>1|
|60<br>R/W<br>HR_BoostDelaySwitchingOn<br>61<br>R/W<br>HR_RTC_TIME|Setpoint of the Boost mode turn-on delay<br>RTC time|0<br>0<br>|15<br>59<br>|0<br>—|Min.<br>Min.<br>S|Byte<br>1<br>Byte<br>2<br>B|
|||0<br>—|59|—|ec.|yte<br>Byte|
|||0|23|—|Hours|Byte|
|63<br>R/W<br>HRRTCCALENDAR|RTC calendar|1|31|—|Day|Byte<br>2|
|__||1|7|—|Wk|Bt|
||||||ee<br>|ye|
|||1|12|—|day<br>Month|Byte|
|||0|99|—|Year|Byte|
|65<br>R/W<br>HR_MaxCO2_Int|Maximum value of the main CO2sensor|500|10000|2000|ppm|Unsigned<br>Short Int<br>1|
|66<br>R/W<br>HR_MaxPM2_5_Int|Maximum value of the main PM2.5 sensor|500|10000|1000|µg/m³|Unsigned<br>Short Int<br>1|
|67<br>R/W<br>HR_SetMinSuAirOutTEMP<br><br><br>|Minimum room supply air temperature control setpoint<br>|5<br>|12<br>|10<br>|°C|<br>Byte<br>1<br><br>|
|68<br>R/W<br>HR_MainHeaterMODE|Main heater operation mode:<br>1-tl 0-100 % 2-AUTO|1|2|2|—|Byte<br>1|
|69<br>R/W<br>HR_SetMainHeaterMANUAL<br><br><br>|conro, <br>Manual control of the main heater<br>|0<br>|100<br>|50<br>|%|Byte<br>1<br><br>|
|70<br>R/W<br>HR_CoolerMODE|Cooler operation mode:|1|2|2|—|Byte<br>1|
||1 - turn on the cooler with discrete confguration, control 0-100 % with<br>||||||
|71<br>R/W<br>HR_SetCoolerMANUAL|analogue confguration, 2-AUTO<br>Manual cooler control with analogue confguration|0|100|0|%|Byte<br>1|
|72<br>R/W<br>HR_PreHeaterMODE|Preheating operation mode:|1|2|2|—|Byte<br>1|
||<br>1-tl 0-100 % 2-AUTO||||||
|73<br>R/W<br>HR_SetPreHeaterMANUAL|conro, <br>Manual preheating control|0|100|50|%|Byte<br>1|
|74<br>R/W<br>HR_BPS_ROTOR_MODE|Bypass/rotary heat exchanger operation mode:|0|2|2|—|Byte<br>1|
||<br>0 - close the bypass/start the rotor, 1 - open the bypass/stop the rotor with<br>||||||
|75<br>R/W<br>HR_SetBpsRotorMANUAL|discrete confguration, control 0-100 % with analogue confguration, 2-AUTO<br>Manual bypass/rotor control with analogue confguration:<br>0 % - bypass closed/rotor rotates at maximum speed, 100 %/bypass open,<br>|0|100|100|%|Byte<br>1|
|76<br>R/W<br>HR_RH_Kp|rotor stopped<br>Kp coefcient of the PID humidity controller|0|1000|150|—|Unsigned<br>Sht It<br>1|
|77<br>R/W<br>HR_RH_Ki|Ki coefcient of the PID humidity controller|0|1000|150|—|or n<br>Unsigned<br>Short Int<br>1|
|78<br>R/W<br>HR_RH_Kd|Kd coefcient of the PID humidity controller|0|1000|0|—|<br>Unsigned<br>Sh I<br>1|
|79<br>R/W<br>HR_CO2_Kp|Kp coefcient of the PID CO2level controller|0|1000|150|—|ort nt<br>Unsigned<br>Short Int<br>1|



7 

|**Address**<br>**R/W**|**Variable**|**Description**<br>|**Minimum value**|**Maximum value**|**Pre-set value**|**Measurement units**|**Model**<br>**Dimension**|
|---|---|---|---|---|---|---|---|
|80<br>R/W|HR_CO2_Ki|Ki coefcient of the PID CO2level controller|0|1000|150|—|Unsigned<br>Sht It<br>1|
|81<br>R/W|HR_CO2_Kd|Kd coefcient of the PID CO2level controller|0|1000|0|—|or n<br>Unsigned<br>Short Int<br>1|
|82<br>R/W|HRPM25Kp|Kp coefcient of the PID PM2.5 level controller|0|1000|150|—|<br>Unsigned<br>1|
||___||||||<br>Short Int|
|83<br>R/W|HR_PM2_5_Ki|Ki coefcient of the PID PM2.5 level controller|0|1000|150|—|<br>Unsigned<br>Short Int<br>1|
|84<br>R/W|HR_PM2_5_Kd|Kd coefcient of the PID PM2.5 level controller|0|1000|0|—|<br>Unsigned<br>h<br>1|
|85<br>R/W|HR_VOC_Kp|Kp coefcient of the PID VOC level controller|0|1000|150|—|Sort Int<br>Unsigned<br>Short Int<br>1|
|86<br>R/W|HR_VOC_Ki|Ki coefcient of the PID VOC level controller|0|1000|150|—|<br>Unsigned<br> <br>1|
|87<br>R/W|HR_VOC_Kd|Kd coefcient of the PID VOC level controller|0|1000|0|—|Short Int<br>Unsigned<br>Sh I<br>1|
|88<br>R/W|HR_PreHeater_Kp|Kp coefcient of the PID preheating controller|0|1000|200|—|ort nt<br>Unsigned<br> <br>1|
|89<br>R/W|HR_PreHeater_Ki|Ki coefcient of the PID preheating controller|0|1000|200|—|Short Int<br>Unsigned<br>Sht It<br>1|
|90<br>R/W|HR_PreHeater_Kd|Kd coefcient of the PID preheating controller|0|1000|500|—|or n<br>Unsigned<br>Sht It<br>1|
|91<br>R/W|HR_MainHeater_Kp|Kp coefcient of the PID reheating controller|0|1000|400|—|or n<br>Unsigned<br>h<br>1|
|92<br>R/W|HR_MainHeater_Ki|Ki coefcient of the PID reheating controller|0|1000|400|—|Sort Int<br>Unsigned<br>Short Int<br>1|
|93<br>R/W|HR_MainHeater_Kd|Kd coefcient of the PID reheating controller|0|1000|600|—|<br>Unsigned<br> <br>1|
|94<br>R/W|HR_BPS_ROTOR_Kp|Kp coefcient of the PID bypass/rotary heat exchanger controller|0|1000|200|—|Short Int<br>Unsigned<br>Sht It<br>1|
|95<br>R/W|HR_BPS_ROTOR_Ki|Ki coefcient of the PID bypass/rotary heat exchanger controller|0|1000|200|—|or n<br>Unsigned<br>Short Int<br>1|
|96<br>R/W|HR_BPS_ROTOR_Kd|Kd coefcient of the PID bypass/rotary heat exchanger controller|0|1000|500|—|<br>Unsigned<br> <br>1|
|97<br>R/W|HR_KKB_Kp|Kp coefcient of the PID condenser unit controller|0|1000|200|—|Short Int<br>Unsigned<br>Shrt Int<br>1|
|98<br>R/W|HR_KKB_Ki|Ki coefcient of the PID condenser unit controller|0|1000|200|—|o<br>Unsigned<br> <br>1|
|99<br>R/W|HR_KKB_Kd|Kd coefcient of the PID condenser unit controller|0|1000|500|—|Short Int<br>Unsigned<br>Sht It<br>1|
|100<br>R/W|HR_ReturnWater_Kp|Kp coefcient of the PID return heat medium controller|0|1000|120|—|or n<br>Unsigned<br> <br>1|
|101<br>R/W|HRReturnWaterKi|Ki coefcient of the PID return heat medium controller|0|1000|120|—|Short Int<br>Unsigned<br>1|
||__||||||<br>Sht It<br>|
|102<br>R/W|HR_ReturnWater_Kd|Kd coefcient of the PID return heat medium controller|0|1000|350|—|or n<br>Unsigned<br>Short Int<br>1|
|103<br>R|HRFAlCTRL|F l tl t|0|255|2|—|<br>Bt<br>1|
||_anarm|an aarm conro ype:<br>|||||ye<br>|
|||0 - no alarm control, 1...254 - number of tacho pulses per fan rotation, 255 - fan<br>alarm control using a diferential pressure switch||||||
|104<br>R<br><br>|HR_SetTimeDetectFanALAR<br>|<br>M Time for fan alarm detection<br>|5<br>|120<br>|30<br>|Sec.<br>|Byte<br>1<br><br>|
|105<br>R/W<br>106<br>R/W|HR_SetTimeOpenVALVE<br>HR_SetTimeFanBLOWING|Damper opening time (fan turn-on delay)<br>Electric heater blowdown time|0<br>20|240<br>240|0<br>120|Sec.<br>Sec.|Byte<br>1<br>Byte<br>1|
|107<br>R/W<br>108<br>R/W|HR_KKB_MinTimeOFF<br>HRKKBMiTiON|Minimum downtime of the condenser unit before restarting<br>Mii ti ti f th d it bf htd|0<br>0|20<br>20|3<br>1|Min.<br>Mi|Byte<br>1<br><br>Bt<br>1|
||__nme<br>|nmum operang me o e conenser un eore suown<br>||||n.<br>°|ye<br><br><br>|
|109<br>R/W<br>110<br>R|HR_KKB_HYSTERESIS<br>HR_BPS_Position|Hysteresis for turning the condenser unit on/ofwith discrete control<br>Bypass location: 0-from outdoors, 1-from indoors|1<br>0|10<br>1|2<br>—|C<br>—|Byte<br>1<br>Byte<br>1|
|111<br>R<br>112<br>R/W|HR_TimeOpenBPS<br>HR_CorrTEMP_SuAirIn|Opening time of the bypass with three-point control<br>Correction of the intake air temperature sensor at the unit inlet. Value 250 =<br> °|2<br>-500|300<br>+500|—<br>0|Sec.<br>°C|Byte<br>1<br>Short Int<br>1|
|113<br>R/W|HR_CorrTEMP_SuAirOut|25.0C<br>Correction of the supply air temperature sensor at the unit outlet (downstream|-500|+500|0|°C|Short Int<br>1|
|114<br>R/W|HRCTEMPEAiI|of the heat exchanger/downstream of the heater). Value 250=25.0°C<br>Ci f h  i    h i il|00|00|0|°C|Sh I<br>1|
||_orr_xrn|orrecton o te extract ar temperature sensor at te unt net.<br>|-5|+5|||ort nt<br>|
|115<br>R/W|HR_CorrTEMP_ExAirOut|Value 250=25.0°C<br>Correction of the exhaust air temperature sensor at the unit outlet. Value 250<br> °|-500|+500|0|°C|Short Int<br>1|
|116<br>R/W|HR_CorrTEMP_Water|=25.0C<br>Correction of the return heat medium temperature sensor.<br>Vl 250=250°C|-500|+500|0|°C|Short Int<br>1|
|117<br>R/W<br>118<br>R/W|HR_CorrTEMP_Ext<br>HR_WaterValveMinPos|aue.<br>Correction of the outdoor air temperature sensor Value 250=25.0°C<br>Minimum position of the water heater valve in winter|-500<br>0|+500<br>100|0<br>0|°C<br>%|Short Int<br>1<br>Byte<br>1|



8 

|**dress**<br><br>**iable**|**cription**|**imum value**|**ximum value**|**-set value**|**asurement units**|**del**<br>**ension**|
|---|---|---|---|---|---|---|
|**Ad**<br>**R/W**<br>**Var**|**Des**|**Min**|**Ma**|**Pre**|**Me**|**Mo**<br>**Dim**|
|119<br>R/W<br>HR_WaterMaxStartTime|Time for detecting return heat medium underheating alarm before the AHU<br>start in winter|2|30|5|Min.|Byte<br>1|
|120<br>R/W<br>HRWaterMinStartTem|Initial value of the return heat medium temerature reuired for the AHU start|30|60|30|°C|Short Int<br>1|
|_p|p q<br>in winter at outdoor temperature >=+10°C|||||<br>|
|121<br>R/W<br>HRWaterMaxStartTemp|<br>Final value of the return heat medium temperature required for the AHU start|30|60|50|°C|Short Int<br>1|
|_|<br>in winter at outdoor temperature <= -30°C|||||<br>|
|122<br>R/W<br>HRWaterMinAlarmTemp|<br>Initial value of the return heat medium temperature for the AHU shutdown|10|30|12|°C|Short Int<br>1|
|_|<br>caused by a freeze alarm in winter at outdoor temperature  >=+10°C|||||<br>|
|123<br>R/W<br>HRWaterMaxAlarmTemp|<br>Final value of the return heat medium temperature for the AHU shutdown|10|30|20|°C|Short Int<br>1|
|_|<br>caused by a freeze alarm in winter at outdoor temperature <= -30°C||||||
|124<br>R/W<br>HRENGINEERPWD|Password to enter the engineering menu. The string should be 1-4 characters|48|57|49|Char|String<br>2|
|__|<br>long.  The end of the string is determined by the Null character|48<br>48|57<br>57|49<br>49|Char<br>Char||
|||48|57|49|Char||
|126<br>R/W<br>HR_SetWEEK_Mo|Speed number for Mo. in the 1st time period|0|5|1|—|Byte<br>1|
|127<br>R/W|Temperature setpoint for Mo. in the 1st period<br>H f h d f h 1 id  M|15<br>0|30<br>23|23<br>6|°C<br>H|Byte<br>B<br>1|
||ours o te en o te st pero on o.<br>||||ours<br>|yte<br><br>|
|128<br>R/W|Minutes of the end of the 1st period on Mo.<br>Speed number for Mo. in the 2nd time period<br>|0<br>0<br>|59<br>5<br>|0<br>1<br>|Min.<br>—<br>°|Byte<br>Byte<br>1<br>|
||Temperature setpoint for Mo. in the 2nd period|15|30|23|C|Byte|
|129<br>R/W|Hours of the end of the 2nd period on Mo.|0|23|9|Hours|Byte<br>1|
||<br>Minutes of the end of the 2nd period on Mo.<br>|0|59|0|Min.|Byte|
|130<br>R/W|Speed number for Mo. in the 3rd time period<br>Temerature setoint for Mo in the 3rd eriod|0<br>15|5<br>30|1<br>23|—<br>°C|Byte<br>1<br>Bte|
|131<br>R/W|p p  .    p<br>Hours of the end of the 3rd period on Mo.|0|23|19|Hours|y<br>Byte<br>1|
||Minutes of the end of the 3rd period on Mo.|0|59|0|Min.|Byte|
|132<br>R/W|<br>Speed number for Mo. in the 4th time period<br>|0|5|1|—<br>|Byte<br>1<br>|
||Temperature setpoint for Mo. in the 4th period|15|30|23|°C|Byte|
|133<br>R|<br>Reserved. The end of the 4th period is always at 23:59|0<br>0|23<br>59|23<br>59|Hours<br>Min.|Byte<br>1<br>Byte|
|134<br>R/W<br>HRSetWEEKTu|Speed number for Tu. in the 1st time period|0|5|1|—|Byte<br>1|
|__|<br>Temperature setpoint for Tu. in the 1st period<br>|15|30|23|°C|Byte|
|135<br>R/W|Hours of the end of the 1st period on Tu.<br>Minutes of the end of the 1st eriod on Tu|0<br>0|23<br>59|6<br>0|Hours<br>Min|Byte<br>1<br>Bte|
|136<br>R/W|p  .<br>Speed number for Tu. in the 2nd time period<br>Temperature setpoint for Tu in the 2nd period|0<br>15|5<br>30|1<br>23|.<br>—<br>°C|y<br>Byte<br>1<br>Byte|
|137<br>R/W|.<br>Hours of the end of the 2nd period on Tu.<br>Minutes of the end of the 2nd period on Tu.|0<br>0|23<br>59|9<br>0|Hours<br>Min.|Byte<br>1<br>Byte|
|138<br>R/W|<br>Seed number for Tu in the 3rd time eriod|0|5|1|—|Bte<br>1|
||p   .     p<br>Temperature setpoint for Tu. in the 3rd period|15|30|23|°C|y<br><br>Byte|
|139<br>R/W|Hours of the end of the 3rd period on Tu<br>|0<br>|23<br>|19<br>|Hours<br>|Byte<br>1<br>|
||Minutes of the end of the 3rd period on Tu.|0|59|0|Min.|Byte|
|140<br>R/W|Speed number for Tu. in the 4th time period|0|5|1|—|Byte<br>1|
||<br>Temerature setoint for Tu in the 4th eriod|15|30|23|°C|Bte|
|141<br>R|p p  .    p<br>Reserved. The end of the 4th period is always at 23:59|0|23|23|Hours|y<br>Byte<br>1|
|||0|59|59|Min|Byte|
|142<br>R/W<br>HR_SetWEEK_We|Speed number for We. in the 1st time period<br>Temperature setpoint for We. in the 1st period|0<br>15|5<br>30|1<br>23|.<br>—<br>°C|Byte<br>1<br>Byte|
|143<br>R/W|<br>Hors of the end of the 1st eriod on We 0 23 6 Hors Bte 1|0|23|6|Hors|Bte<br>1|
||u       p  .    u y<br>Minutes of the end of the 1st period on We.|0|59|0|u<br>Min.|y<br><br>Byte|
|144<br>R/W|Speed number for We. in the 2nd time period<br>|0<br>|5<br>|1<br>|—<br>°|Byte<br>1<br>|
||Temperature setpoint for We. in the 2nd period|15|30|23|C|Byte|
|145<br>R/W|Hours of the end of the 2nd period on We.<br>Mit f th d f th 2d id  W|0<br>0|23<br>59|9<br>0|Hours<br>Mi|Byte<br>1<br>Bt|
||nues o e en o e n pero on e.<br>||||n.|ye<br><br>|
|146<br>R/W|Speed number for We. in the 3rd time period|0|5|1|—|Byte<br>1|
||Temperature setpoint for We. in the 3rd period<br>|15<br>|30<br>|23<br>|°C<br>|Byte<br><br>|
|147<br>R/W|Hours of the end of the 3rd period on We.|0|23|19|Hours|Byte<br>1|
|148<br>R/W|Minutes of the end of the 3rd period on We.<br>Sd b f W i h 4h i id|0<br>0|59<br>5|0<br>1|Min.|Byte<br>B<br>1|
||pee numer or e. n te t tme pero<br>||||—<br>°|yte<br><br>|
|149<br>R|Temperature setpoint for We. in the 4th period<br>Reserved The end of the 4th period is always at 23:59|15<br>0|30<br>23|23<br>23|C<br>Hours|Byte<br>Byte<br>1|
||.|0|59|59|Min.|Byte|



9 

|**dress**<br><br>**iable**|**scription**|**imum value**|**ximum value**<br>**-set value**<br>**asurement units**|**del**<br>**ension**|
|---|---|---|---|---|
|**Ad**<br>**R/W**<br>**Var**|**De**|**Min**|**Ma**<br>**Pre**<br>**Me**|**Mo**<br>**Dim**|
|150<br>R/W<br>HR_SetWEEK_Th|Speed number for Th. in the 1st time period<br>Temperature setpoint for Th. in the 1st period<br>|0<br>0|5<br>1<br>—<br>30<br>23<br>°C|Byte<br>1<br>Byte|
|151<br>R/W|Hours of the end of the 1st period on Th.|0|23<br>6<br>Hours|Byte<br>1|
||<br>Minutes of the end of the 1st period on Th.<br>|0<br>|59<br>0<br>Min.<br><br>|Byte<br><br>|
|152<br>R/W|Speed number for Th. in the 2nd time period|0|5<br>1<br>—|Byte<br>1|
||Temperature setpoint for Th. in the 2nd period<br>|0<br>|30<br>23<br>°C<br><br><br>|Byte<br><br>|
|153<br>R/W|Hours of the end of the 2nd period on Th.<br>Minutes of the end of the 2nd period on Th.|0<br>0|23<br>9<br>Hours<br>59<br>0<br>Min.|Byte<br>1<br>Byte|
|154<br>R/W|Speed number for Th in the 3rd time period|0|5<br>1<br>—|Byte<br>1|
||.<br>Temperature setpoint for Th. in the 3rd period<br>|0|30<br>23<br>°C<br>|Byte<br>|
|155<br>R/W|Hours of the end of the 3rd period on Th.|0|23<br>19<br>Hours|Byte<br>1|
||<br>Minutes of the end of the 3rd period on Th.<br>|0<br>|59<br>0<br>Min.<br><br>|Byte<br><br>|
|156<br>R/W|Speed number for Th. in the 4th time period<br>Temperature setpoint for Th. in the 4th period|0<br>0|5<br>1<br>—<br>30<br>23<br>°C|Byte<br>1<br>Byte|
|157<br>R|<br>Reserved. The end of the 4th period is always at 23:59|0<br>0|23<br>23<br>Hours<br>59<br>59<br>Min.|Byte<br>1<br>Byte|
|158<br>R/W<br>HR_SetWEEK_Fr|Speed number for Fr. in the 1st time period<br>|0<br>|5<br>1<br>—<br><br><br>°|Byte<br>1<br>|
||Temperature setpoint for Fr. in the 1st period<br>|0<br>|30<br>23<br>C<br><br><br>|Byte<br>|
|159<br>R/W|Hours of the end of the 1st period on Fr.|0|23<br>6<br>Hours|Byte<br>1|
||<br>Minutes of the end of the 1st period on Fr|0|59<br>0<br>Min|Bte|
|160<br>R/W|.<br>Speed number for Fr. in the 2nd time period|0|.<br>5<br>1<br>—|y<br>Byte<br>1|
|161<br>R/W|Temperature setpoint for Fr. in the 2nd period<br>H f th d f th 2d id  F|0<br>0|30<br>23<br>°C<br>23<br>9<br>H|Byte<br>Bt<br>1|
||ours o e en o e n pero on r.<br>||ours<br><br><br>|ye<br><br>|
||Minutes of the end of the 2nd period on Fr.|0|59<br>0<br>Min.|Byte|
|162<br>R/W|Speed number for Fr. in the 3rd time period<br>Temperature setpoint for Fr. in the 3rd period<br>|0<br>0|5<br>1<br>—<br>30<br>23<br>°C|Byte<br>1<br>Byte|
|163<br>R/W|Hours of the end of the 3rd period on Fr.|0|23<br>19<br>Hours|Byte<br>1|
||<br>Minutes of the end of the 3rd period on Fr.<br>|0<br>|59<br>0<br>Min.<br><br>|Byte<br><br>|
|164<br>R/W|Speed number for Fr. in the 4th time period|0|5<br>1<br>—|Byte<br>1|
||Temperature setpoint for Fr. in the 4th period|0|30<br>23<br>°C|Byte|
|165<br>R|<br>Reserved. The end of the 4th period is always at 23:59|0<br>0|23<br>23<br>Hours<br>59<br>59<br>Min.|Byte<br>1<br>Byte|
|166<br>R/W<br>HRSetWEEKSa|Speed number for Sa. in the 1st time period|0|5<br>1<br>—|Byte<br>1|
|__|<br>Temperature setpoint for Sa. in the 1st period<br>|0|30<br>23<br>°C|Byte|
|167<br>R/W|Hours of the end of the 1st period on Sa.|0|23<br>6<br>Hours|Byte<br>1|
||<br>Minutes of the end of the 1st period on Sa.<br>|0<br>|59<br>0<br>Min.<br><br>|Byte<br><br>|
|168<br>R/W|Speed number for Sa. in the 2nd time period<br>Temperature setpoint for Sa. in the 2nd period|0<br>0|5<br>1<br>—<br>30<br>23<br>°C|Byte<br>1<br>Byte|
|169<br>R/W|<br>H f th d f th 2d id  S|0|23<br>9<br>H|Bt<br>1|
||ours o e en o e n pero on a.<br>||ours<br><br><br>|ye<br><br>|
||Minutes of the end of the 2nd period on Sa.|0|59<br>0<br>Min.|Byte|
|170<br>R/W|Speed number for Sa in the 3rd time period|0|5<br>1<br>—|Byte<br>1|
||.<br>Temperature setpoint for Sa. in the 3rd period<br>|0|30<br>23<br>°C|Byte|
|171<br>R/W|Hours of the end of the 3rd period on Sa.|0|23<br>19<br>Hours|Byte<br>1|
||<br>Minutes of the end of the 3rd period on Sa|0|59<br>0<br>Min|Bte|
|172<br>R/W|.<br>Speed number for Sa. in the 4th time period|0|.<br>5<br>1<br>—|y<br>Byte<br>1|
||Temperature setpoint for Sa. in the 4th period|0|30<br>23<br>°C|Byte|
|173<br>R|<br>Reserved. The end of the 4th period is always at 23:59|0<br>0|23<br>23<br>Hours<br>59<br>59<br>Min.|Byte<br>1<br>Byte|
|174<br>R/W<br>HRSetWEEKSu|Speed number for Su in the 1st time period|0|5<br>1<br>—|Byte<br>1|
|__|.<br>T  f S  h  d||°C|B|
|175<br>R/W|emperature setpoint or u. in te 1st perio<br>Hours of the end of the 1st period on Su.|0<br>0|30<br>23<br><br>23<br>6<br>Hours|yte<br>Byte<br>1|
||<br>Minutes of the end of the 1st period on Su.<br>|0<br>|59<br>0<br>Min.<br><br>|Byte<br><br>|
|176<br>R/W|Speed number for Su. in the 2nd time period|0|5<br>1<br>—|Byte<br>1|
||Temperature setpoint for Su. in the 2nd period|0|30<br>23<br>°C|Byte|
|177<br>R/W|<br>Hours of the end of the 2nd period on Su.<br>|0<br>|23<br>9<br>Hours<br><br><br>|Byte<br>1<br>|
|178<br>R/W|Minutes of the end of the 2nd period on Su.<br>Speed number for Su in the 3rd time period|0<br>0|59<br>0<br>Min.<br>5<br>1<br>—|Byte<br>Byte<br>1|
||.<br>Temperature setpoint for Su. in the 3rd period<br>|0|30<br>23<br>°C|Byte|
|179<br>R/W|Hours of the end of the 3rd period on Su.|0|23<br>19<br>Hours|Byte<br>1|
||<br>Minutes of the end of the 3rd period on Su.<br>|0<br>|59<br>0<br>Min.<br><br>|Byte<br><br>|
|180<br>R/W|Speed number for Su. in the 4th time period<br>Temperature setpoint for Su. in the 4th period|0<br>0|5<br>1<br>—<br>30<br>23<br>°C|Byte<br>1<br>Byte|
|181<br>R|<br>Rd Th d f th 4th id i l t 2359|0|23<br>23<br>H|Bt<br>1|
||eserve. e en o e  pero s aways a :|0|ours<br>59<br>59<br>Min.|ye<br><br>Byte|



10 

11 

B55-8-1EN-01 

12 

