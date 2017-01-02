FROM asqatasun/asqatasun:pre-requisites_with-empty-DB
MAINTAINER Matthieu Faure <mfaure@asqatasun.org>

# ##########################################################
#
#                      DISCLAIMER
#
# This is a fat container, that is absolutly not compliant to Docker best-practices
# https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/
# Don't use it for production as all data are wiped out at reboot / rebuild
# BUT for quick testing, that does the job :)
#
# #### FROM  ######################################################
#   Ubuntu:14.04
#   |-- asqatasun/asqatasun:base_wget
#       |-- asqatasun/asqatasun:only-DB
#           |-- asqatasun/asqatasun:pre-requisites_without-DB
#               |-- asqatasun/asqatasun:pre-requisites_with-empty-DB
###################################################################

USER root
ENV  WWWPORT="8080"                                    \
     DATABASE_DB="asqatasun"                           \
     DATABASE_HOST="localhost"                         \
     DATABASE_USER="asqatasun"                         \
     DATABASE_PASSWD="asqaP4sswd"                      \
     TOMCAT_WEBAPP_DIR="/var/lib/tomcat7/webapps"      \
     TOMCAT_USER="tomcat7"                             \
     RELEASE="1.0-RC2_dev"                             \
     PHANTOMJS_FILE="phantomjs-1.9.7-linux-x86_64"     \
     PHANTOMJS_URL_PREFIX="https://bitbucket.org/ariya/phantomjs/downloads"

EXPOSE $WWWPORT



# Add WebSnapshot
ADD ./websnapshot_${RELEASE}.i386.tar.gz /root/

                                                                                                                # Install WebSnapshot


RUN   cd          /opt                                                        && \
      wget        ${PHANTOMJS_URL_PREFIX}/${PHANTOMJS_FILE}.tar.bz2           && \
      tar   -xvf  *.tar.bz2                                                   && \
      rm          *.tar.bz2                                                   && \
      ln    -s    /opt/phantomjs*       /opt/phantomjs                        && \
      mkdir -p    /var/tmp/websnapshot                                        && \
      cd          /var/tmp/websnapshot                                        && \
      touch       phantomjsdriver.log                                         && \
      ln    -s    phantomjsdriver.log   /var/lib/tomcat7/phantomjsdriver.log  && \
      chown -R    ${TOMCAT_USER} phantomjsdriver.log

RUN  mkdir  -p    /var/log/websnapshot/                           && \
     touch        /var/log/websnapshot/websnapshot.log            && \
     chown  -R    ${TOMCAT_USER} /var/log/websnapshot/

RUN  mv           /root/websnapshot*/   /root/websnapshot/                     && \
     cd           /root/websnapshot/install/                                   && \
     mv     -v    websnapshot*.war /var/lib/tomcat7/webapps/websnapshot.war    && \
     cd           /root/websnapshot/install/conf                               && \
    sed -i -e "s#\$SQL_SERVER_URL#${DATABASE_HOST}#"     \
        -e    "s#\$USER#${DATABASE_USER}#"               \
        -e    "s#\$PASSWORD#${DATABASE_PASSWD}#"         \
        -e    "s#DATABASE_NAME#${DATABASE_DB}#"          \
        websnapshot.conf                                                    && \
      mkdir -p    /etc/websnapshot/                    && \
     mv     -v    websnapshot.conf /etc/websnapshot/websnapshot.conf                       && \
     cat  /etc/websnapshot/websnapshot.conf       && \
      service mysql start                            && \
     sleep 5                                        && \
 cd   /root/websnapshot/install/sql             && \
    cat *.sql |                                         \
            mysql --user="${DATABASE_USER}"             \
                  --password="${DATABASE_PASSWD}"       \
                  --host="${DATABASE_HOST}"             \
                  --database="${DATABASE_DB}"           && \
     du -hs *
  #  rm -rf  /root/websnapshot*



CMD  service mysql start                            && \
     sleep   5                                      && \
     service xvfb start                             && \
     service tomcat7 start                          ;  \
     tail -f -n 100 /var/log/tomcat7/catalina.out     \
             /var/log/websnapshot/websnapshot.log


