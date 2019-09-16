# How to run Jenkins 

`
$ sudo docker run -p 8080:8080 -v ${HOME}:/home -v ${JENKINS_HOME}:/var/jenkins-home -v /var/run/docker.sock:/var/run/docker.sock -v $(which docker):/usr/bin/docker -u root jenkinsci/blueocean
`
* ${HOME} - home directory (e.g.: /Users/Username);
* ${JENKINS_HOME} - a Jenkins-containing directory;

# Troubleshooting 

## 1. Unable to connect to remote git repository.
You should add SSH keys both into Jenkins' host and into your SCM host (in this case - GitHub). <br/>
Please, refer to: <br/>
* [Generating a new SSH key and adding it to the ssh-agent](https://help.github.com/en/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)<br/>
* [Adding a new SSH key to your GitHub account](https://help.github.com/en/articles/adding-a-new-ssh-key-to-your-github-account)<br/>

## 2. POST condition doesn't get executed.
You should wrap the error-prone step into ```catchError {...}``` block. Example: <br/>
```
...
stage('Potentially Error Stage') {
    steps {
        catchError {
            sh './scriptThatThrowsException'
        }
    }
}
post { 
    failure { 
        script { 
            echo "Exception has been caught."
        }
    }
}
```
