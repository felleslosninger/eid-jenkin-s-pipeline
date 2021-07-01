FROM docker:18.06.3-ce as docker
FROM maven:3.6.3 as maven
FROM openjdk:11.0.11-9-jdk

RUN apt-get update && apt-get -y install coreutils git openssh-client curl zip unzip bash fonts-dejavu-core ca-certificates openssl groff python-pip python jq
RUN pip install awscli && apt-get -y purge python-pip && apt -y autoremove

COPY --from=docker /usr/local/bin/docker /usr/bin/docker
COPY --from=maven /usr/share/maven /usr/share/maven
RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
RUN groupadd -g 1001 jenkins-agent
RUN groupadd -g 999 docker 
RUN useradd -rm  -s /bin/bash -g jenkins-agent -g docker -u 1001 jenkins-agent
USER jenkins-agent
