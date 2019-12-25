FROM docker-devops.art.lmru.tech/img-oracle-jdk8

RUN apt update && apt install maven -y

# CMD ["/bin/sh"] 

# Add ADEO certificates to OS & Java
# RUN curl -fsl http://igc.groupeadeo.com/ADEO_ROOT_CA1.crt -o /usr/local/share/ca-certificates/ADEO_ROOT_CA1.crt  && \
#    curl -fsl http://igc.groupeadeo.com/ADEO_SERVICES_INFRA_CA1.crt -o /usr/local/share/ca-certificates/ADEO_SERVICES_INFRA_CA1.crt && \
#    curl -fsl http://igc.groupeadeo.com/ADEO_SERVICES_INFRA_CA2.crt -o /usr/local/share/ca-certificates/ADEO_SERVICES_INFRA_CA2.crt && \
#    update-ca-certificates && \
#    echo "systemProp.javax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/adeocerts\n" > /root/local.properties \
#    ${JAVA_HOME}/jre/bin/keytool -import -trustcacerts -noprompt -alias adeo_root_ca -keystore ${JAVA_HOME}/jre/lib/security/adeocerts -storepass changeit -file "ADEO_ROOT_CA1.crt" \
#    ${JAVA_HOME}/jre/bin/keytool -import -trustcacerts -noprompt -alias adeo_services_infra_ca1 -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit -file "ADEO_SERVI CES_INFRA_CA1.crt" \
#    ${JAVA_HOME}/jre/bin/keytool -import -trustcacerts -noprompt -alias adeo_services_infra_ca2 -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit -file "ADEO_SERVICES_INFRA_CA2.crt"
