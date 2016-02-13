# Howto install Asqatasun-Web-Snapshot 

Tested on Linux Ubuntu 12.04 LTS

## Prerequesites
* Tomcat (it's one of several solutions)
```
    sudo aptitude install tomcat6 libspring-instrument-java
    sudo ln -s /usr/share/java/spring3-instrument-tomcat.jar /usr/share/tomcat6/lib/spring3-instrument-tomcat.jar
    sudo ln -s /usr/share/java/mysql-connector-java.jar /usr/share/tomcat6/lib/mysql-connector-java.jar
```
* MySQL

Installation : 
<code>sudo aptitude install mysql-server-5.5 libmysql-java</code>

Configuration :

Edit the my.cnf mysql configuration file.
```
sudo vi /etc/mysql/my.cnf
```
Set the max_allowed_packet option to 16M (default is 1M)
```
max_allowed_packet      = 16M
```
Restart mysql service
```
sudo service mysql restart
```

## Howto install

### Create the configuration file
    sudo touch /etc/websnapshot.conf
You can use [the web-snapshot configuration file](https://github.com/Asqatasun/Web-Snapshot/blob/master/websnapshot-resources/src/main/resources/conf/websnapshot.conf) and replace the variable.

### Create the database
You should use [the mysql database configuration file](https://github.com/Asqatasun/Web-Snapshot/blob/master/websnapshot-resources/src/main/resources/sql/webthumbnail.sql) and execute the request in your MySQL installation.

### Create the log files

    sudo mkdir /var/log/websnapshot/
    sudo touch /var/log/websnapshot/websnapshot.log
    sudo chown -R tomcat6 websnapshot/
    cd /var/tmp
    mkdir websnapshot
    cd websnapshot
    touch phantomjsdriver.log
    ln -s phantomjsdriver.log /var/lib/tomcat6/phantomjsdriver.log
    chown tomcat6 phantomjsdriver.log

### Deploy the api

    cd <path_to_websnapshot>/Web-Snapshot
    mvn clean install
    sudo cp websnapshot-webapp/target/websnapshot-webapp-1.0-SNAPSHOT.war /var/lib/tomcat6/webapps/
    sudo invoke-rc.d tomcat6 restart