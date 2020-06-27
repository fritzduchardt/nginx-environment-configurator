# nginx-environment-configurator
**Generates a NGINX reverse proxy configuration based on environment variables**. 

Can be used in a Kubernetes setup as an [InitContainer](https://kubernetes.io/docs/concepts/workloads/pods/init-containers/) to overwrite NGINX default configuration **rendering configuration with a ConfigMap obsolete**.

## Usage

The container is translating environment variable with prefix "LOCATION_" and "PROXYPASS_" to equivalent Nginx reverse proxy configuration.

E.g. `LOCATION_1=/` and `PROXYPASS_1`=http://localhost:8080/ would result in the following NGINX `default.conf`: 

``` 
server {
    listen       80;
    listen  [::]:80;
    server_name  localhost;
    
    location / {
        proxy_pass http://localhost:8080;
    }
}
```

This can be placed in `/etc/nginx/conf.d/`to change NGINX proxy behavior.

**In a Kubernetes setup** the configuration could look as follows:


``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: your-deployment-name
spec:
  replicas: 1
  selector:
    matchLabels:
      app.kubernetes.io/name: your-application-name
  strategy: {}
  template:
    metadata:
      labels:
        app.kubernetes.io/name: your-application-name
    spec:
      initContainers:
        - image: fritzduchardt/nginx-environment-configurator:latest
          name: nginx-environment-configurator
          volumeMounts:
            - name: nginx-conf
              mountPath: "/work-dir"
          env:
            - name: NGINX_CONFIG_PATH
              value: "/work-dir/default.conf"
            - name: LOCATION_1
              value: "/"
            - name: PROXYPASS_1
              value: "http://localhost:8080/"
            - name: LOCATION_2
              value: "/swagger"
            - name: PROXYPASS_2
              value: "http://localhost:8081/"
      containers:
        - image: fritzduchardt/your-application-image:latest
          imagePullPolicy: Always
          name: your-application-name
        - image: swaggerapi/swagger-ui:latest
          name: swaggerapi
          ports:
            - name: http
              containerPort: 8081
              protocol: TCP
          env:
            - name: PORT
              value: "8081"
            - name: SWAGGER_JSON_URL
              value: http://localhost:8080/v2/api-docs
        - image: nginx:latest
          name: nginx
          volumeMounts:
            - mountPath: /etc/nginx/conf.d/
              name: nginx-conf
      volumes:
        - name: nginx-conf
          emptyDir: {}
```
