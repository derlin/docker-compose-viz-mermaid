# see https://github.com/mermaid-js/mermaid-cli/blob/master/Dockerfile
FROM minlag/mermaid-cli:10.1.0

ENV MERMAID_RENDERER=mermaid-cli \
    MMDC_EXECUTABLE_PATH=/mmdc-wrapper

# Install java
USER root
RUN apk add --no-cache openjdk11-jre
# Create a wrapper. We need it in order to pass the puppeteer config on each call
RUN echo -e '#!/bin/ash\n/home/mermaidcli/node_modules/.bin/mmdc -p /puppeteer-config.json $@' > $MMDC_EXECUTABLE_PATH && \
    chmod +x $MMDC_EXECUTABLE_PATH && chown mermaidcli $MMDC_EXECUTABLE_PATH
COPY build/libs/*no_local*.jar /docker-compose-viz-mermaid.jar

# Test that it works as expected
USER mermaidcli
RUN echo -e "services:\n  web:\n    image: derlin/rickroller" > /tmp/dc.yaml && \
      echo y | java -jar /docker-compose-viz-mermaid.jar /tmp/dc.yaml -f svg -o /tmp/dc.svg && \
      rm /tmp/dc*

ENTRYPOINT ["java", "-jar", "/docker-compose-viz-mermaid.jar"]
CMD [ "--help" ]
