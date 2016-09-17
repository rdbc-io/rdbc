#!/bin/sh

set -e

setup_bintray_credentials() {
    echo "Configuring bintray credentials"
    mkdir $HOME/.bintray
cat <<EOF > $HOME/.bintray/.credentials
realm = Bintray API Realm
host = api.bintray.com
user = povder
password = ${BINTRAY_API_KEY}
EOF
}

main() {
    setup_bintray_credentials
}

main
