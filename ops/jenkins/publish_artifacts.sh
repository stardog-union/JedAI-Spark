#!/usr/bin/env bash

set -eu
sbt clean compile package publish
