#  Application for processing and analyzing logs of transfers between users.

## Требования:
- Java 8 или новее

## Запуск:

### maven:

``` bash    
    mvn clean package
    java -jar target/logs_analyzer-1.0-SNAPSHOT.jar <Путь до директории с логами>
```

### Ручная сборка
```bash
mkdir -p out/classes

javac  -d out/classes src/main/java/org/Webbee/Main.java src/main/java/org/Webbee/model/*.java src/main/java/org/Webbee/services/*.java src/main/java/org/Webbee/exceptions/*.java

# Собираем JAR-файл
jar cfe log_analyzer_app.jar org.Webbee.Main -C out/production/classes .

# Запуск
java -jar log_analyzer_app.jar logs