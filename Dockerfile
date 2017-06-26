FROM openjdk:8

MAINTAINER Niels Boecker

WORKDIR /app

COPY ./target/universal/solveservice-0.1-SNAPSHOT.tgz /app/tmp/app.tgz
COPY log4j.properties /app/

RUN tar xzf /app/tmp/app.tgz --directory /app/tmp
RUN mv /app/tmp/solveservice-0.1-SNAPSHOT/* /app
RUN rm -r -f /app/tmp/

CMD /app/bin/solveservice

EXPOSE 8080
