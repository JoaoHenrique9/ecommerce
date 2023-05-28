FROM maven:3.8.4-openjdk-11 AS builder

WORKDIR /app

# Copie os arquivos do projeto para o diretório de trabalho
COPY . .

# Execute o comando mvn install para construir o projeto
RUN mvn install -DskipTests

# Crie uma nova imagem baseada no JRE 11
FROM openjdk:11

WORKDIR /app

# Copie o arquivo JAR construído a partir do estágio anterior
COPY --from=builder /app/target/ec-order-service-0.0.1-SNAPSHOT.jar /tmp/ec-order-service.jar

# Defina o comando de inicialização do aplicativo
ENTRYPOINT ["java", "-jar", "/tmp/ec-order-service.jar"]