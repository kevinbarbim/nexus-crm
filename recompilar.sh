#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mvn clean package
cp target/nexus-0.3.1.jar dist/nexus.jar
printf 'Executavel dist/nexus.jar atualizado.\n'
