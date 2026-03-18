import sqlite3

con = sqlite3.connect('sql_app.db')
cur = con.cursor()

# ✏️ CAMBIA AQUÍ TU CONSULTA:
cur.execute('SELECT ID, Nombre, Email, nivel_confianza FROM usuarios')

rows = cur.fetchall()
cols = [description[0] for description in cur.description]

# Imprimir encabezados automáticamente
col_width = 20
header = '  '.join(f"{c:<{col_width}}" for c in cols)
print(header)
print('-' * len(header))

# Imprimir filas
for r in rows:
    row_str = '  '.join(f"{str(v):<{col_width}}" for v in r)
    print(row_str)

con.close()
