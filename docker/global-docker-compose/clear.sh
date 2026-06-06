#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== WARNING: This will remove all containers AND volumes (data will be lost) ==="
read -p "Are you sure? (y/N): " confirm

if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
  echo "Aborted."
  exit 0
fi

echo ""
echo "=== Removing all services and volumes ==="

docker compose -f "$SCRIPT_DIR/docker-compose-mysql.yml" down -v
docker compose -f "$SCRIPT_DIR/docker-compose-redis.yml" down -v

echo ""
echo "=== All services and volumes removed ==="
