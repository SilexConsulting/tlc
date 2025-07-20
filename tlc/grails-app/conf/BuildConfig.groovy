grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:true, maxPerm:256]
//]
// Force HTTPS for all repositories
System.setProperty('force.https.repo', 'true')

// Force TLS 1.2
System.setProperty('https.protocols', 'TLSv1.2')
System.setProperty('maven.wagon.http.ssl.insecure', 'true')
System.setProperty('maven.wagon.http.ssl.allowall', 'true')

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {

    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
        excludes "itext", "com.lowagie:itext"
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true

        grailsPlugins()
        grailsHome()
        mavenLocal()
        mavenCentral()

        //mavenRepo "https://repo.spring.io/release"
        //mavenRepo "https://repo.spring.io/milestone"
        //mavenRepo "https://repo.spring.io/plugins-release"


        // Add these repositories
        mavenRepo "https://repo.grails.org/grails/core"
        mavenRepo "https://repo.grails.org/grails/plugins"

        // Legacy repositories that might be needed
        mavenRepo "https://repo.maven.apache.org/maven2/"
        mavenRepo "https://repo1.maven.org/maven2/"
        mavenRepo "https://repo.orl.eng.hitachivantara.com/artifactory/pnt-mvn/"

    }

    dependencies {
        runtime 'net.sf.ehcache:ehcache-core:2.4.8'
        runtime 'xerces:xercesImpl:2.12.2'
        runtime 'xml-apis:xml-apis:1.4.01'


        runtime 'mysql:mysql-connector-java:8.0.28'


        // It might be a good idea to keep this in line with the one in the mail plugin
        runtime('org.springframework:spring-test:4.1.9.RELEASE') { transitive = false }

        // JasperReports for producing our reports
        compile('net.sf.jasperreports:jasperreports:5.6.1')

        // Cron facilities
        compile('it.sauronsoftware.cron4j:cron4j:2.2.5')

        compile ("com.lowagie:itext:2.1.7")

        compile 'org.olap4j:olap4j:1.2.0'
        compile 'opensymphony:sitemesh:2.4.2'
        compile 'org.grails:grails-web:2.5.6'
        compile 'org.grails:grails-web-gsp:2.5.6'
    }

    plugins {
        runtime ":hibernate4:4.3.10"
        runtime ':jquery:1.11.1'
        runtime ':resources:1.2.8'
        runtime ':lesscss-resources:1.3.3'

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.5"

        build ":tomcat:8.0.22"

        // TODO: Uncomment if you wish to use database migration facilities
        //runtime ':database-migration:1.2.2'

        // TODO: TLC uses its own 'application data' caching system rather than the SpringCache system.
        //compile ':cache:1.0.1'

        compile ':mail:1.0.7'
        runtime ':jquery-ui:1.10.4'
        compile ':webxml:1.4.1'
    }

}