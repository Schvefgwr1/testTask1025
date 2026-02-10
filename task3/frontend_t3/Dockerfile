FROM httpd:2.4-alpine

COPY . /usr/local/apache2/htdocs/
COPY httpd.conf /usr/local/apache2/conf/extra/httpd-custom.conf
RUN echo "Include conf/extra/httpd-custom.conf" >> /usr/local/apache2/conf/httpd.conf
