
set -o nounset 
set -o xtrace

CROCHOME=~/vellum/croc
CROCTMP=$CROCHOME/tmp
CROCSEC=$CROCHOME/security

cd $CROCHOME

command0_enroll() { # enroll cert
  cd $CROCTMP
  curl -k "https://localhost:8443/enrollUser/evan.summers@gmail.com?displayName=Evan%20Summers"
  curl -k "https://localhost:8443/enrollOrg/evan.summers@gmail.com/crocserver.org?displayName=CrocServer.Org"
  curl -k "https://localhost:8443/enrollCert/evan.summers@gmail.com/crocserver.org/evanx@desktop.crocserver.org" -o enroll-key.pem
  curl -k "https://localhost:8443/getCert/evan.summers@gmail.com/evanx@desktop.crocserver.org" -o enroll-cert.pem
  cat enroll-cert.pem | openssl x509 -text
}


command0_cpkey() {
  cp -f $CROCTMP/enroll-key.pem $CROCSEC/croc-key.pem
  cp -f $CROCTMP/enroll-cert.pem $CROCSEC/croc-cert.pem
}

command0_post () {
  curl -k "https://localhost:8444/post/evanx@desktop.crocserver.org/aide" --key enroll-key.pem --cert enroll-cert.pem -d 12345
  curl -k "https://localhost:8444/post/evanx@desktop.crocserver.org/aide/NotifyType=OUTPUT_CHANGED" --key enroll-key.pem --cert enroll-cert.pem -d 12346
}

command0_default() {
  command0_enroll
  command0_post
}

if [ $# -gt 0 ]
then
  command=$1
  shift
else
  command=default
fi

command$#_$command

