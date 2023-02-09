FROM ubuntu:22.04

ARG USER_ID=1001
ARG GROUP_ID=1001

ENV DEFAULT_JAVA_VER 11
ENV JAVA_HOME /usr/lib/jvm/java-${DEFAULT_JAVA_VER}-openjdk-amd64/
ENV DEBIAN_FRONTEND noninteractive
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8


# tzdata ENVs required for installing software-properties-common
ENV TZ=US/Eastern
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get -qq update && \
    apt-get -y install \
    curl \
    docker-compose \
    sudo \
    ca-certificates \
    openjdk-11-jdk \
    vim \
    build-essential \
    maven \
    cmake \
    git \
    openssh-client \
    docker.io \
    dnsutils \
    wget \
    telnet \
    groff \
    lcov \
    bash-completion \
    mingw-w64-x86-64-dev && \
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN sudo apt-get update && \
    sudo apt-get install apt-transport-https curl gnupg -yqq && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import && \
    sudo chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg && \
    sudo apt-get update && \
    sudo apt-get install sbt

RUN curl -fL https://getcli.jfrog.io | sh
RUN mv jfrog /usr/local/bin/jfrog

RUN groupadd --gid ${GROUP_ID} jenkins \
  && useradd jenkins --uid ${USER_ID} --gid jenkins --shell /bin/bash --home-dir /var/lib/jenkins --create-home

# the container runs as jenkins: we set this to jenkins:jenkins so we can
# update it later if needed (eg the container itself may update graviton)
RUN chown -R jenkins:jenkins /usr/local/bin/

RUN useradd stardog --shell /bin/bash --create-home

RUN update-java-alternatives --set java-1.11.0-openjdk-amd64
RUN ln -s ${JAVA_HOME} /usr/lib/jvm/default-java


RUN update-ca-certificates

RUN echo "stardog  ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers.d/stardog
RUN echo "jenkins  ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers.d/jenkins

RUN chmod 777 /opt
