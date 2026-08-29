# Backup drill: dump MySQL (or copy H2) + uploads.
# Usage:
#   ./scripts/backup.sh
#   powershell -File scripts/backup.ps1
# Restore MySQL: mysql -u tradehub -ptradehub tradehub < backups/latest.sql

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
STAMP="$(date +%Y%m%d-%H%M%S)"
OUT="${TRADEHUB_BACKUP_DIR:-$ROOT/backups}/$STAMP"
mkdir -p "$OUT"

if command -v mysqldump >/dev/null 2>&1; then
  mysqldump \
    -h "${MYSQL_HOST:-127.0.0.1}" \
    -P "${MYSQL_PORT:-3306}" \
    -u "${MYSQL_USER:-tradehub}" \
    -p"${MYSQL_PASSWORD:-tradehub}" \
    "${MYSQL_DB:-tradehub}" > "$OUT/tradehub.sql"
  echo "mysql dump -> $OUT/tradehub.sql"
else
  echo "mysqldump not found, skip SQL dump"
fi

if [ -d "$ROOT/services/tradehub-api/data" ]; then
  cp -R "$ROOT/services/tradehub-api/data" "$OUT/h2-data"
fi
if [ -d "$ROOT/services/tradehub-api/uploads" ]; then
  cp -R "$ROOT/services/tradehub-api/uploads" "$OUT/uploads"
fi

ln -sfn "$STAMP" "${TRADEHUB_BACKUP_DIR:-$ROOT/backups}/latest" || true
echo "backup complete: $OUT"
