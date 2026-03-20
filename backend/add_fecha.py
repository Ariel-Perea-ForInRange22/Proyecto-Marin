import sqlite3

conn = sqlite3.connect('sql_app.db')
cur = conn.cursor()

cur.execute("ALTER TABLE productos ADD COLUMN fecha_publicacion TEXT DEFAULT '2026-01-01 00:00:00'")
conn.commit()
print("fecha_publicacion column added.")

cur.execute("PRAGMA table_info(productos)")
cols = [r[1] for r in cur.fetchall()]
print("Columns now:", cols)
conn.close()
