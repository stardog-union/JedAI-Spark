#!/usr/bin/env bash

BUILD_JAR_FULL_PATH=$(pwd)/$(ls target/scala-2.12/*.jar | head -n 1)
BASE_JAR_NAME=$(basename ${BUILD_JAR_FULL_PATH})
JAR_URL=https://stardog.jfrog.io/stardog/stardog-testing/nightly-develop-jedai-snapshot/binaries/complexible/stardog/${BASE_JAR_NAME}

set -eu
sbt clean compile package publish
sha256sum ${BUILD_JAR_FULL_PATH} | awk '{ print $1 }' > ${BUILD_JAR_FULL_PATH}.sha256
curl -L -X PUT -u ${artifactoryUsername}:${artifactoryPassword} -T ${BUILD_JAR_FULL_PATH} ${JAR_URL}

echo "Publishing the SHA256 " $(cat ${BUILD_JAR_FULL_PATH}.sha256) " to ${JAR_URL}.sha256"
curl -L -X PUT -u ${artifactoryUsername}:${artifactoryPassword} -T ${BUILD_JAR_FULL_PATH}.sha256 ${JAR_URL}.sha256
