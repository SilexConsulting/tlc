# Build stage
FROM openjdk:8-jdk AS builder

# Install required dependencies for building
RUN apt-get update && apt-get install -y \
    curl \
    zip \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Set Grails version
ENV GRAILS_VERSION=2.5.6

# Install Grails
RUN curl -L https://github.com/grails/grails-core/releases/download/v$GRAILS_VERSION/grails-$GRAILS_VERSION.zip -o /tmp/grails.zip \
    && unzip /tmp/grails.zip -d /opt/ \
    && rm /tmp/grails.zip \
    && ln -s /opt/grails-$GRAILS_VERSION /opt/grails

# Set environment variables for build
ENV GRAILS_HOME=/opt/grails
ENV PATH=$GRAILS_HOME/bin:$PATH
ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:MaxPermSize=256m -Djava.util.logging.config.file=/usr/local/tomcat/conf/logging.properties"
ENV GRAILS_OPTS="-server -Xmx1024m -Xms512m -XX:MaxPermSize=256m"

# Set working directory
WORKDIR /app

# Copy only dependency-related files first
COPY grails-app/conf/BuildConfig.groovy grails-app/conf/
COPY application.properties .

# Pre-download dependencies
RUN grails compile --non-interactive

# Now copy the rest of the application
COPY . .

# Build the application
RUN grails -Dgrails.env=prod war

# Runtime stage
FROM tomcat:8-jre8-slim

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the built war file from builder stage
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# copy logging properties
COPY logging.properties /usr/local/tomcat/conf/logging.properties

# Set environment variables for runtime
ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:MaxPermSize=256m"

RUN echo '<?xml version="1.0" encoding="UTF-8"?>\n\
<Context>\n\
    <Resource name="jdbc/dataSource"\n\
              auth="Container"\n\
              type="javax.sql.DataSource"\n\
              driverClassName="${hibernate.connection.driver_class}"\n\
              url="${hibernate.connection.url}"\n\
              username="${hibernate.connection.username}"\n\
              password="${hibernate.connection.password}"\n\
              maxTotal="20"\n\
              maxIdle="10"\n\
              maxWaitMillis="-1"/>\n\
</Context>' > /usr/local/tomcat/conf/context.xml

# Expose the port the app runs on
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
