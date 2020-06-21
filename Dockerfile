FROM groovy

LABEL maintainer="fduchardt"

COPY . .

ENV NGINX_CONFIG_PATH "/etc/nginx/conf.d/default.conf"

./nginx-environment-configurator.groovy
