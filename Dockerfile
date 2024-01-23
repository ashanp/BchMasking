FROM openjdk:21-jdk
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
CMD ["java","-jar","/usr/src/myapp/target/BchMasking-0.0.1-SNAPSHOT.jar","--spring.config.location=/usr/src/myapp/src/main/resources/application.properties"]