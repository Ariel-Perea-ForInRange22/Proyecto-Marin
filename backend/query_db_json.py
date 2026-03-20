import sqlite3
import json

conn = sqlite3.connect('sql_app.db')
conn.row_factory = sqlite3.Row
cur = conn.cursor()

cur.execute("SELECT id, nombre, email, hashed_password FROM usuarios")
rows = [dict(row) for row in cur.fetchall()]
with open('users_dump.json', 'w') as f:
    json.dump(rows, f, indent=2)
