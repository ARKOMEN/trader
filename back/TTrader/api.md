# GET /recomend
#### Нет параметров
#### Запрос на рекомендации 
&emsp;{<br>
&emsp;&emsp;**recomend**:[&emsp;*Массив **рекомендаций***<br>
&emsp;&emsp;{<br>
&emsp;&emsp;&emsp;**ticker**:"AAPL"<br>
&emsp;&emsp;&emsp;*Просто тикер*<br>
&emsp;&emsp;&emsp;**action**:"HOLD"<br>
&emsp;&emsp;&emsp;*Рекомендуемое действие, одно из **HOLD**, **BUY**, **SALE***<br>
&emsp;&emsp;&emsp;**reability**:0.8<br>
&emsp;&emsp;&emsp;*Насколько успешной будет сделка. Предполагается,
что все **рекомендации** (**recomend**) отсортированы в порядке убывания
(пока нет).*<br>
&emsp;&emsp;&emsp;**company**:"APPLE INC"<br>
&emsp;&emsp;&emsp;*Название компании. Тут пока не приводится настоящее название.*<br>
&emsp;&emsp;},...<br>
&emsp;&emsp;]<br>
&emsp;}
# GET /ticker/{ticker}
#### Параметры пути:
* **ticker** - запрашиваемый тикер, его можно получить из запроса ***/recomendations***
#### Обязательные get параметры:
* **unit** - временной юнит, шаг графика (см. запрос */units*)<br>
* **count** - количество запрашиваемых свечей
#### *Примечание: для отрисовки свечей вам понадобятся все параметры свечи, для отрисовки классического графика потребуется только **open** и **close***
#### <ins>Советую почитать про свечи в интернете</ins>
#### Запрос на получение полной информации о тикере
&emsp;{<br>
&emsp;&emsp;**company**:"APPLE INC"<br>
&emsp;&emsp;*Название компании. Тут пока не приводится настоящее название.*<br>
&emsp;&emsp;**candles**:[&emsp;*Массив **свечей***<br>
&emsp;&emsp;{<br>
&emsp;&emsp;&emsp;**t**:1747749070<br>
&emsp;&emsp;&emsp;*Таймштамп свечи*<br>
&emsp;&emsp;&emsp;**o**:88.5<br>
&emsp;&emsp;&emsp;*Открытие свечи*<br>
&emsp;&emsp;&emsp;**c**:87.3<br>
&emsp;&emsp;&emsp;*Закрытие свечи*<br>
&emsp;&emsp;&emsp;**h**:89.7<br>
&emsp;&emsp;&emsp;*Верх свечи*<br>
&emsp;&emsp;&emsp;**l**:87.2<br>
&emsp;&emsp;&emsp;*Низ свечи*<br>
&emsp;&emsp;},...<br>
&emsp;&emsp;]<br>
&emsp;}
# GET /current/{ticker}
#### Параметры пути:
* **ticker** - запрашиваемый тикер, его можно получить из запроса ***/recomendations***
#### Обязательные get параметры:
* **unit** - временной юнит, шаг графика (см. запрос */units*)
#### Запрос на получение полной информации о тикере
&emsp;{<br>
&emsp;&emsp;**present**:true<br>
&emsp;&emsp;*Есть ли текущее значение свечи. Обычно true,
false возвращается либо если нет такого тикера,
либо прошло мало времени со старта приложения и значение пока не установилось,
либо если что-то сломалось*<br>
&emsp;&emsp;**current**:{&emsp;*Текущая свеча*<br>
&emsp;&emsp;&emsp;**t**:1747749070<br>
&emsp;&emsp;&emsp;*Таймштамп свечи*<br>
&emsp;&emsp;&emsp;**o**:88.5<br>
&emsp;&emsp;&emsp;*Открытие свечи*<br>
&emsp;&emsp;&emsp;**c**:87.3<br>
&emsp;&emsp;&emsp;*Закрытие свечи*<br>
&emsp;&emsp;&emsp;**h**:89.7<br>
&emsp;&emsp;&emsp;*Верх свечи*<br>
&emsp;&emsp;&emsp;**l**:87.2<br>
&emsp;&emsp;&emsp;*Низ свечи*<br>
&emsp;&emsp;}<br>
&emsp;}
# GET /units
#### Нет параметров
#### Запрос на получение **всех** доступных юнитов. Использование несужествующего юнита выдаст ошибку
&emsp;{<br>
&emsp;&emsp;**units**:[&emsp;*Массив доступных временных шагов*<br>
&emsp;&emsp;{<br>
&emsp;&emsp;&emsp;**secs**:10<br>
&emsp;&emsp;&emsp;*Время юнита в секундах*<br>
&emsp;&emsp;&emsp;**enLong**:"10 seconds"<br>
&emsp;&emsp;&emsp;*Описание юнита на английском*<br>
&emsp;&emsp;&emsp;**short**:"10S"<br>
&emsp;&emsp;&emsp;*Короткая запись юнита*<br>
&emsp;&emsp;&emsp;**ruLong**:"10 секунд"<br>
&emsp;&emsp;&emsp;*Описание юнита на русском*<br>
&emsp;&emsp;&emsp;**maxPeriod**:3600<br>
&emsp;&emsp;&emsp;***<ins>Максимальное гарантированное количество свечей
которое хранится в бд приложения, больше этого количества свечей запрашивать
запрещено (вы получите ошибку)</ins>***<br>
&emsp;&emsp;},...<br>
&emsp;&emsp;]<br>
&emsp;}