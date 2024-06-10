FROM amazoncorretto:11 as builder
#FROM arm64v8/amazoncorretto:11.0.18-al2022-RC as builder
#RUN addgroup -g 1001 -S datllas && adduser -u 1001 -S svc -G datllas
#USER svc
ENV APP_DIR /app
VOLUME /tmp
#ARG JAR_FILE
ENV JAR_FILE engine.jar
COPY ${JAR_FILE} $APP_DIR/app.jar
WORKDIR $APP_DIR
RUN java -Djarmode=layertools -jar app.jar extract
COPY . .

FROM amazoncorretto:11
#RUN addgroup -g 1001 -S datllas && adduser -u 1001 -S svc -G datllas
#USER svc
WORKDIR /app
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/application/ ./

EXPOSE 8081
#CMD ["java","-Dfile.encoding=UTF-8","-Dhostname=localhost","-Djava.security.egd=file:/dev/./urandom","org.springframework.boot.loader.JarLauncher"]