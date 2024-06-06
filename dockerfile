# Especificar a imagem base
FROM openjdk:21-jdk-slim

# Copiar o arquivo jar construído para o diretório do container
COPY target/*.jar app.jar

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]
