import sqlite3
import pprint

conn = sqlite3.connect('sql_app.db')
conn.row_factory = sqlite3.Row
cur = conn.cursor()

print("--- USERS ---")
cur.execute("SELECT * FROM usuarios")
for row in cur.fetchall():
    print(dict(row))
