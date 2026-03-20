import sqlite3
from passlib.context import CryptContext

pwd_context = CryptContext(schemes=["pbkdf2_sha256"], deprecated="auto")
hashed_password = pwd_context.hash("123456")

conn = sqlite3.connect('sql_app.db')
cur = conn.cursor()
cur.execute("UPDATE usuarios SET hashed_password = ? WHERE email = ?", (hashed_password, "a9099392125@uat.edu.mx"))
conn.commit()
print("Password updated successfully!")
