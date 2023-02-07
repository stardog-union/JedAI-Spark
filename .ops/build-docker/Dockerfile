FROM ubuntu:22.04

ARG USER_ID=1001
ARG GROUP_ID=1001

ENV DEFAULT_JAVA_VER 11
ENV JAVA_HOME /usr/lib/jvm/java-${DEFAULT_JAVA_VER}-openjdk-amd64/
ENV DEBIAN_FRONTEND noninteractive
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8
ENV JDK_JAVA_OPTIONS "-Xmx512m -Xms512m -XX:MaxDirectMemorySize=700m -XX:ConcGCThreads=4 -XX:ParallelGCThreads=4"

# tzdata ENVs required for installing software-properties-common
ENV TZ=US/Eastern
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get -qq update && \
    apt-get install -y software-properties-common && \
    add-apt-repository 'deb http://security.ubuntu.com/ubuntu/ xenial main restricted' && \
    add-apt-repository 'deb http://security.ubuntu.com/ubuntu/ xenial universe' && \
    add-apt-repository 'deb http://security.ubuntu.com/ubuntu/ xenial-updates universe' && \
    add-apt-repository ppa:openjdk-r/ppa && \
    add-apt-repository ppa:ts.sch.gr/ppa && \
    apt-get -qq -y install curl && \


RUN apt-get -qq update && \
    apt-get -y install \
    docker-compose \
    sudo \
    python2.7 \
    python-minimal \
    python-dev \
    python3-pip \
    openjdk-8-jdk \
    openjdk-11-jdk \
    vim \
    python3-virtualenv \
    build-essential \
    maven \
    cmake \
    git \
    openssh-client \
    docker.io \
    curl \
    dnsutils \
    wget \
    telnet \
    groff \
    lcov \
    bash-completion \
    mingw-w64-x86-64-dev \
    ca-certificates \
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN sudo apt-get update \
    sudo apt-get install apt-transport-https curl gnupg -yqq \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import \
    sudo chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg \
    sudo apt-get update \
    sudo apt-get install sbt

RUN locale-gen en_US.UTF-8 && \
    echo 'LANG="en_US.UTF-8"' > /etc/default/locale

RUN pip3 install awscli --upgrade

RUN curl -fL https://getcli.jfrog.io | sh
RUN mv jfrog /usr/local/bin/jfrog

RUN groupadd --gid ${GROUP_ID} jenkins \
  && useradd jenkins --uid ${USER_ID} --gid jenkins --shell /bin/bash --home-dir /var/lib/jenkins --create-home

# the container runs as jenkins: we set this to jenkins:jenkins so we can
# update it later if needed (eg the container itself may update graviton)
RUN chown -R jenkins:jenkins /usr/local/bin/

RUN useradd stardog --shell /bin/bash --create-home

RUN update-java-alternatives --set java-${DEFAULT_JAVA_VER}-openjdk-amd64
RUN ln -s ${JAVA_HOME} /usr/lib/jvm/default-java

RUN update-ca-certificates

RUN echo "stardog  ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers.d/stardog
RUN echo "jenkins  ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers.d/jenkins

RUN chmod 777 /opt
