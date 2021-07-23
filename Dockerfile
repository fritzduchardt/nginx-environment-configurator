FROM groovy
ENV test=hi
LABEL maintainer="fduchardt"

COPY . .

ENV NGINX_CONFIG_PATH "/etc/nginx/conf.d/default.conf"

CMD ./nginx-environment-configurator.groovy
