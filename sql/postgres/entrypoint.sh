#!/bin/bash
set -e

# PostgreSQL'in orijinal entrypoint'ini arka planda başlat
/usr/local/bin/docker-entrypoint.sh "$@" &
POSTGRES_PID=$!

# PostgreSQL'in hazır olmasını bekle (maksimum 60 saniye)
echo "Waiting for PostgreSQL to be ready..."
for i in {1..60}; do
    if pg_isready -U postgres >/dev/null 2>&1; then
        echo "PostgreSQL is ready!"
        sleep 2
        break
    fi
    sleep 1
done

# Init script'i çalıştır (veritabanları oluştur)
if [ -f /docker-entrypoint-initdb.d/01-init-databases.sh ]; then
    echo "Running database initialization script..."
    bash /docker-entrypoint-initdb.d/01-init-databases.sh || echo "Init script completed with warnings"
fi

# PostgreSQL process'ini ön plana getir ve çalışır durumda tut
wait $POSTGRES_PID

