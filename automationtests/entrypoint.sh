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

## Run automationtests
java -jar -Dmodules="$MODULES" -Denv.user="$ENV_USER" -Denv.endpoint="$ENV_ENDPOINT" -Denv.testLevel="$ENV_TESTLEVEL" automationtests.jar;
