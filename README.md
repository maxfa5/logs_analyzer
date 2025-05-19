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

jar cfe log_analyzer_app.jar org.webbee.Main -C out/production/classes .

java -jar log_analyzer_app.jar logs