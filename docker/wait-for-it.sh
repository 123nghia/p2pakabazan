#!/usr/bin/env bash
# wait-for-it.sh: Wait for a service to be available

set -e

# Extract host and port from first argument (format: host:port)
HOSTPORT="$1"
shift
cmd="$@"

# Default values
HOST="${HOSTPORT%:*}"
PORT="${HOSTPORT#*:}"

# If no port specified, use default
if [ "$HOST" = "$PORT" ]; then
  PORT=5432
fi

>&2 echo "Waiting for $HOST:$PORT to be ready..."

# Wait for the host:port to be available
RETRIES=60
COUNT=0
until nc -z "$HOST" "$PORT" 2>/dev/null || [ $COUNT -eq $RETRIES ]; do
  >&2 echo "Database is unavailable - sleeping (attempt $COUNT/$RETRIES)"
  sleep 2
  COUNT=$((COUNT + 1))
done

if [ $COUNT -eq $RETRIES ]; then
  >&2 echo "Timeout waiting for $HOST:$PORT"
  exit 1
fi

>&2 echo "$HOST:$PORT is up - executing command"
exec $cmd
