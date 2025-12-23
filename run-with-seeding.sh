#!/bin/bash
# Script to run application with DataSeeder enabled
cd "$(dirname "$0")"

# Prefer JWT secret from .env to avoid weak key errors, but don't pull in other vars.
if [ -f .env ]; then
    env_jwt=$(rg -m 1 '^JWT_SECRET=' .env | sed 's/^JWT_SECRET=//')
    if [ -n "$env_jwt" ]; then
        export JWT_SECRET="$env_jwt"
    fi
fi

if [ -z "$JWT_SECRET" ] || [ ${#JWT_SECRET} -lt 32 ]; then
    export JWT_SECRET="change-me-32-chars-minimum-1234567890"
fi

mvn -pl p2p_p2p -am spring-boot:run -Dspring-boot.run.arguments="--app.seed.enabled=true"
