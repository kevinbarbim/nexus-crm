#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
export DB_URL="${DB_URL:-jdbc:sqlserver://localhost:1433;databaseName=NexoImob;encrypt=true;trustServerCertificate=true}"
export DB_USER="${DB_USER:-sa}"
export ADMIN_EMAIL="${ADMIN_EMAIL:-admin@example.test}"
export ADMIN_PASSWORD="${ADMIN_PASSWORD:-NexoDemo2026!}"
if [ -z "${DB_PASSWORD:-}" ]; then
    read -rsp "Senha do SQL Server: " DB_PASSWORD
    printf '\n'
fi
export DB_PASSWORD
: "${DB_PASSWORD:?Informe a senha do SQL Server}"
exec java -Duser.timezone=America/Sao_Paulo -jar dist/nexus.jar --server.address=127.0.0.1
