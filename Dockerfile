FROM 402712822235.dkr.ecr.mx-central-1.amazonaws.com/att-dev/goldenimagej21@sha256:5b807fe0ebb111b4564cc72bf38cacb500cf3c93dcd80f48c4ebcc50f397e190
USER root
WORKDIR /opt/apps

# Combinar los comandos mkdir y chown en un solo RUN
RUN mkdir -p /opt/apps/MICRO-Management /opt/apps/logs/MICRO-Management \
    && chown -R usrapp:usrapp /opt/apps/logs/MICRO-Management

# Cambia a Agreement Management
WORKDIR /opt/apps/MICRO-Management
COPY boot/target/*.jar MICRO-management.jar

USER usrapp
EXPOSE 16600
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/opt/apps/MICRO-Management/MICRO-management.jar"]