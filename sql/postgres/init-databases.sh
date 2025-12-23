#!/bin/bash
set +e

# PostgreSQL bağlantı bilgileri
PGHOST="${PGHOST:-localhost}"
PGPORT="${PGPORT:-5432}"
PGUSER="${PGUSER:-${POSTGRES_USER:-postgres}}"
PGPASSWORD="${PGPASSWORD:-${POSTGRES_PASSWORD}}"

# Veritabanı listesi
DATABASES=("Micro_AccountServiceDB" "Micro_LedgerServiceDB" "Micro_FraudServiceDB" "Micro_NotificationServiceDB")

# Her veritabanını kontrol et ve yoksa oluştur
for DB_NAME in "${DATABASES[@]}"; do
    if PGPASSWORD="$PGPASSWORD" psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "postgres" -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" 2>/dev/null | grep -q 1; then
        echo "Database $DB_NAME already exists, skipping..."
    else
        echo "Creating database $DB_NAME..."
        PGPASSWORD="$PGPASSWORD" psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "postgres" -c "CREATE DATABASE \"$DB_NAME\";" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "Database $DB_NAME created successfully."
        else
            echo "Note: Failed to create $DB_NAME (might already exist)"
        fi
    fi
done

echo "All databases checked/created successfully"
