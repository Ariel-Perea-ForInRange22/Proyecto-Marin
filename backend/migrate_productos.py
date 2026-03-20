"""
Migration: Add new columns to 'productos' table.
Run once after updating models.py.
"""
import sqlite3

conn = sqlite3.connect('sql_app.db')
cur = conn.cursor()

migrations = [
    "ALTER TABLE productos ADD COLUMN categoria TEXT DEFAULT 'otros'",
    "ALTER TABLE productos ADD COLUMN imagen_url TEXT",
    "ALTER TABLE productos ADD COLUMN es_patrocinado INTEGER DEFAULT 0",
    "ALTER TABLE productos ADD COLUMN fecha_publicacion TEXT DEFAULT (datetime('now'))",
]

for sql in migrations:
    try:
        cur.execute(sql)
        print(f"OK: {sql[:60]}")
    except Exception as e:
        print(f"SKIP (already exists?): {e}")

conn.commit()
conn.close()
print("Migration complete.")
