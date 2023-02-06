#!/usr/bin/env bash

jfrog rt del --url "https://stardog.jfrog.io/stardog" \
             --user ${artifactoryUsername} \
             --password ${artifactoryPassword}  \
             --quiet=true \
             --spec=./ops/jenkins/snapshot/delete.spec || true
