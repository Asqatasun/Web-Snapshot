
# Howto build Asqatasun-Web-Snapshot 

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
    sudo ln -s /opt/phantomjs-1.9.7-linux-x86_64 /opt/phantomjs
```

## Howto build

### Clone the project from Github repository
    
    cd /home/<user>
    git clone https://github.com/Asqatasun/Web-Snapshot.git

### Build the sources

    cd /home/<user>/Web-Snapshot
    mvn clean install
    
> Waiting for an archiva, [asqatasun](https://github.com/Asqatasun/Asqatasun) must have previously
> been built on the machine, in its 4.0.0-SNAPSHOT version (current develop branch).
> Once built, the dependencies will be copied in your local maven repository
> and then, you'll be able to build the websnapshot.
> We know it's a bit dirty, but by now, it works
>
> --------
> [#1](https://github.com/Asqatasun/Web-Snapshot/issues/1)  BUILD FAILURE - websnapshot-api 

