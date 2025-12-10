#!/usr/bin/env bash
# wait-for-it.sh: Wait for a service to be available

set -e

host="$1"
shift
cmd="$@"

until PGPASSWORD=postgres123 psql -h "db" -U "postgres" -d "p2p_trading_dev" -c '\q'; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up - executing command"
exec $cmd
