Tested on Linux Ubuntu 12.04 LTS

## Prerequesites
* Tomcat
* Maven
* Git

## Howto actually build
    git clone https://github.com/Tanaguru/Web-snapshot.git
    cd Web-Snapshot
    mvn clean install
    sudo cp websnapshot-webapp/target/websnapshot-webapp-1.0-SNAPSHOT.war /var/lib/tomcat6/webapps/
    sudo mkdir /var/log/websnapshot/
    sudo touch /var/log/websnapshot/websnapshot.log
    sudo chown -R tomcat6 websnapshot/
    sudo invoke-rc.d tomcat6 restart