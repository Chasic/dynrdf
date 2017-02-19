FROM bitnami/tomcat:latest

##### Manage privilegies
RUN useradd -ms /bin/bash dynrdf
RUN useradd -ms /bin/bash tomcat
RUN usermod -G dynrdf tomcat
## Objects dir
RUN mkdir /home/dynrdf/objects
RUN chmod 775 /home/dynrdf/objects
## Logging
RUN mkdir /var/log/dynrdf
RUN chmod 775 /var/log/dynrdf
#####
