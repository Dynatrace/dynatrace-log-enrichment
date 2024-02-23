# Workaround solutions for Log Enrichment with Dynatrace

# Log4J v1.x
Dynatrace OneAgent currently doesn't support log enrichment for Log4J v1.x out of the box.

* Add this library to the classpath of your application
* Add `%X{dynatrace}` to the conversion pattern of the Pattern Layouts configured within `log4j.properties`
* Specify `log4j.loggerFactory=dynatraceoss.LoggerFactory` within your `log4j.properties`

# Log4J2
Although Dynatrace OneAgent comes with out of the box support for Log Enrichment for Log4J 2.x we've seen deployments where Log4J has been customized in a way that disables that feature of OneAgent.

Just add this library to the classpath you will nevertheless get the same results.

# Maintainer
This repo is maintained by Asad Ali <asad.ali@dynatrace.com>
