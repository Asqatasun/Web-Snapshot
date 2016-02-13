Tested on Linux Ubuntu 12.04 LTS

## Prerequesites
* Maven
* Git
* PhantomJs
```
    cd /opt
    sudo wget https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.7-linux-x86_64.tar.bz2
    sudo tar -xvf phantomjs-1.9.7-linux-x86_64.tar.bz2
    sudo rm phantomjs-1.9.7-linux-x86_64.tar.bz2
```

## Howto build

### Clone the project from Github repository
    
    cd /home/<user>
    git clone https://github.com/Asqatasun/Web-Snapshot.git

### Build the sources

    cd /home/<user>/Web-Snapshot
    mvn clean install