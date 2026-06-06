#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== Stopping all services ==="

docker compose -f "$SCRIPT_DIR/docker-compose-mysql.yml" stop
docker compose -f "$SCRIPT_DIR/docker-compose-redis.yml" stop

echo ""
echo "=== All services stopped (data preserved) ==="
