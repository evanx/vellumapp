
set -o nounset 
set -o xtrace

CROCHOME=~/vellum/croc
CROCTMP=$CROCHOME/tmp

cd $CROCHOME

command0_genkeypair() {
  ~/jdk7/jre/bin/keytool -keystore security/croc.jks -storepass crocserver -alias crocserver -keypass crocserver \
    -genkeypair -keyalg RSA -keysize 2048 \
    -dname "CN=crocserver, OU=crocserver-ou, O=crocserver-org, L=CPT, S=WP, C=ZA"
  keytool -keystore security/croc.jks -storepass crocserver -list
}

command0_run() {
  java -Djavax.net.ssl.keyStore=security/croc.jks -Djavax.net.ssl.keyStorePassword=crocserver -Djavax.net.ssl.keyPassword=crocserver \
    -Djavax.net.ssl.trustStore=security/croc.jks -Djavax.net.ssl.trustStorePassword=crocserver
}

ommand0_default() {
  command0_run
}

if [ $# -gt 0 ]
then
  command=$1
  shift
else
  command=default
fi

command$#_$command

