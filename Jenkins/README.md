# How to run Jenkins 

`
$ sudo docker run -p 8080:8080 -v ${HOME}:/home -v ${JENKINS_HOME}:/var/jenkins-home -v /var/run/docker.sock:/var/run/docker.sock -v $(which docker):/usr/bin/docker -u root jenkinsci/blueocean
`
* ${HOME} - home directory (e.g.: /Users/Username);
* ${JENKINS_HOME} - a Jenkins-containing directory;
