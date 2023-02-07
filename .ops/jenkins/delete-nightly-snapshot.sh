#!/usr/bin/env bash

export CI=true
jfrog rt del --url "https://stardog.jfrog.io/stardog" \
             --user ${artifactoryUsername} \
             --password ${artifactoryPassword}  \
             --quiet=true \
             --spec=delete.spec || true