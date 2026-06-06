#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== Starting all services ==="

docker compose -f "$SCRIPT_DIR/docker-compose-mysql.yml" up -d
docker compose -f "$SCRIPT_DIR/docker-compose-redis.yml" up -d

echo ""
echo "=== Waiting for healthchecks ==="

wait_healthy() {
  local name=$1
  local max=30
  local count=0
  echo -n "Waiting for $name"
  until [ "$(docker inspect --format='{{.State.Health.Status}}' "$name" 2>/dev/null)" = "healthy" ]; do
    echo -n "."
    sleep 2
    count=$((count + 1))
    if [ $count -ge $max ]; then
      echo " TIMEOUT"
      return 1
    fi
  done
  echo " OK"
}

wait_healthy global-mysql
wait_healthy global-redis

echo ""
echo "=== All services are up ==="
docker compose -f "$SCRIPT_DIR/docker-compose-mysql.yml" ps
docker compose -f "$SCRIPT_DIR/docker-compose-redis.yml" ps
