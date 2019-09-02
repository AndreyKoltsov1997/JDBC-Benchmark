#!/bin/sh

# WARNING: Some systems could protect docker.sock file from binding. Consider ...
# ... running this script in root mode to exclude it from happening.

# NOTE: User homepath should be configured with respect to the host.
USER_HOME=/Users/Andrey/

docker run \
  --rm \
  -u root \
  -p 8080:8080 \
  -v jenkins-data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$USER_HOME":/home \
  jenkinsci/blueocean
