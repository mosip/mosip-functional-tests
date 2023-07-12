#!/bin/bash

## Add ssl certificate to JAVA cacerts keystore file
if [ "$ENABLE_INSECURE" = "true" ]; then
  HOST=$( env | grep "mosip-api-internal-host" |sed "s/mosip-api-internal-host=//g");
  if [ -z "$HOST" ]; then
    echo "HOST $HOST is empty; EXITING";
    exit 1;
  fi;
  openssl s_client -servername "$HOST" -connect "$HOST":443  > "$HOST.cer" 2>/dev/null & sleep 2 ;
  sed -i -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' "$HOST.cer";

  cat "$HOST.cer";

  keytool -trustcacerts -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit -noprompt -importcert -alias "$HOST" -file "$HOST.cer" ;

  if [ $? -gt 0 ]; then
    echo "Failed to add SSL certificate for host $host; EXITING";
    exit 1;
  fi
fi


## Run authentication demo service
if [ "$is_glowroot_env" = "present" ]; then
  wget -q --show-progress "${artifactory_url_env}"/artifactory/libs-release-local/io/mosip/testing/glowroot.zip ;
  unzip glowroot.zip ;
  rm -rf glowroot.zip ;
  sed -i 's/<service_name>/authentication-demo-service/g' glowroot/glowroot.properties ;
  java -jar -javaagent:glowroot/glowroot.jar -Dloader.path="${loader_path_env}" -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" authentication-demo-service.jar ;
else
  java -jar  -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" authentication-demo-service.jar ;
fi
