#  Application for processing and analyzing logs of transfers between users.

## Требования:
- Java 8 или новее

## Запуск:

### maven:

``` bash    
    mvn clean package
    java -jar target/logs_analyzer-1.0-SNAPSHOT.jar <Путь до директории с логами>
```

### Ручная сборка:
```bash
mkdir -p out/classes

javac  -d out/classes src/main/java/org/webbee/Main.java src/main/java/org/webbee/model/*.java src/main/java/org/webbee/services/*.java src/main/java/org/webbee/exceptions/*.java

jar cfe log_analyzer_app.jar org.webbee.Main -C out/classes .

java -jar log_analyzer_app.jar logs
```

## Функциональность:
1. Считывать все файлы в указанной директории (в которой могут присутствовать другие вложенные директории).
2. Каждый файл может содержать информацию об операциях одного или нескольких пользователей в формате:
```%дата и время лога% %пользователь% %операция%:```
3. Объединить все записи в рамках каждого пользователя в один файл %user%.log (прим. user1.log)
4. Сохранить полученный файл(ы) в отдельную директорию transactions_by_users, которая должна находиться в корне директории, которая содержит лог файлы.
5. Внутри файла отсортировать записи по дате лога (по возрастанию).
6. В конце каждого файла добавить строку с финальным, рассчитанным на основе анализа логов, значением баланса с текущей датой и временем:
   ```[2025-05-10 11:00:03] user001 final balance 1770```

### Поддерживаемые операции:
```
balance inquiry %количество%
transferred %количество% to %пользователь%
withdrew %количество%
```
### Примеры лог-файлов:
log1.log

```
[2025-05-10 09:00:22] user001 balance inquiry 1000.00
[2025-05-10 09:05:44] user001 transferred 100.00 to user002
[2025-05-10 09:06:00] user001 transferred 120.00 to user002
[2025-05-10 10:30:55] user005 transferred 10.00 to user003
[2025-05-10 11:09:01] user001 transferred 235.54 to user004
[2025-05-10 12:38:31] user003 transferred 150.00 to user002
[2025-05-11 10:00:31] user002 balance inquiry 210.00
```
log2.log

```
[2025-05-10 10:03:23] user002 transferred 990.00 to user001
[2025-05-10 10:15:56] user002 balance inquiry 110.00
[2025-05-10 10:25:43] user003 transferred 120.00 to user002
[2025-05-10 11:00:03] user001 balance inquiry 1770
[2025-05-10 11:01:12] user001 transferred 102.00 to user003
[2025-05-10 17:04:09] user001 transferred 235.54 to user004
[2025-05-10 23:45:32] user003 transferred 150.00 to user002
[2025-05-10 23:55:32] user002 withdrew 50
```