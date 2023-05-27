FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /app

# Copie os arquivos do projeto para o diretório de trabalho
COPY . .

# Execute o comando mvn install para construir o projeto
RUN mvn install -DskipTests

# Crie uma nova imagem baseada no JRE 11
FROM openjdk:17

WORKDIR /app

# Copie o arquivo JAR construído a partir do estágio anterior
COPY --from=builder /app/target/ec-eureka-server-0.0.1-SNAPSHOT.jar /tmp/c-eureka.jar

# Defina o comando de inicialização do aplicativo
ENTRYPOINT ["java", "-jar", "/tmp/c-eureka.jar"]