#!/bin/bash


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
