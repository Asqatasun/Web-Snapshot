Tested on Linux Ubuntu 12.04 LTS

## Prerequesites
* Tomcat
* Maven
* Git

## Howto actually build
    git clone https://github.com/Tanaguru/Web-snapshot.git
    cd Contrast-finder
    mvn clean install
    sudo cp color-finder-webapp/target/contrast-finder-webapp-1.0-SNAPSHOT.war /var/lib/tomcat6/webapps/
    sudo mkdir /var/log/contrast-finder/
    sudo chown -R tomcat6 contrast-finder/
    sudo invoke-rc.d tomcat6 restart