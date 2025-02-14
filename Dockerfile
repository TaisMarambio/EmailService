FROM eclipse-temurin:21-jdk

# Configuramos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos los archivos de Gradle y el código fuente
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
COPY src src

# Damos permisos al script de Gradle Wrapper y construimos la aplicación
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# Copiamos el JAR generado
COPY build/libs/*.jar app.jar

# Exponemos el puerto (asegúrate de que coincida con el de tu aplicación)
EXPOSE 8080

# Comando para ejecutar el servicio
ENTRYPOINT ["java", "-jar", "app.jar"]
