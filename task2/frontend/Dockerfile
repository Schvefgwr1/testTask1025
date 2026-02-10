FROM httpd:2.4-alpine

# Copy frontend files
COPY . /usr/local/apache2/htdocs/

# Copy and include custom config for URL routing
COPY httpd.conf /usr/local/apache2/conf/extra/httpd-custom.conf
RUN echo "Include conf/extra/httpd-custom.conf" >> /usr/local/apache2/conf/httpd.conf
