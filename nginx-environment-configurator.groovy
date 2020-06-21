#! /usr/bin/groovy
def confLocation = System.getenv("NGINX_CONFIG_PATH")

if (!confLocation) {
    System.err.println("No NGINX_CONFIG_PATH environment variable set. Should point to the nginx default.conf file that will be overwritten.");
    System.exit(1)
}

println "Generating $confLocation based on environment"

def counter = 1
def locations = []
def proxypaths = []

while(true) {

    def location = System.getenv("LOCATION_$counter")
    def proxypath = System.getenv("PROXYPATH_$counter")

    if (!location || !proxypath) {
        break;
    }
    locations << location
    proxypaths << proxypath
    counter++
}

if (locations.size() == 0 || proxypaths.size() == 0) {
    System.err.println("No LOCATION environment variable with matching PROXYPATH found, e.g.: LOCATION_1=/ and PROXYPATH_1=http://localhost:9090");
    System.exit(1)
}

def locationDirectives = ""

locations.eachWithIndex { it, index ->
    def proxypath = proxypaths[index]
    locationDirectives = locationDirectives << "\n        location $it {\n            proxy_path $proxypath\n        }\n"
}

def confTemplate = """
    server {
        listen       80;
        listen  [::]:80;
        server_name  localhost;
        ${locationDirectives}
    }
""";

println confTemplate

new File(confLocation).write confTemplate
