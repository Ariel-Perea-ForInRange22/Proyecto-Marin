import random
from sqlalchemy.orm import Session
from app.database import engine, Base, SessionLocal
from app import models
from app.auth import get_password_hash

def run_seed():
    print("Recreando las tablas en la base de datos...")
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    
    db: Session = SessionLocal()
    
    try:
        print("1. Creando usuarios...")
        usuarios = []
        # Crear usuario principal (del dev/tester) para asegurar acceso
        admin = models.Usuario(
            nombre="Jesus Marin",
            email="jesus@uat.edu.mx",
            hashed_password=get_password_hash("123456"),
            es_staff=True,
            nivel_confianza=100,
            racha_diaria=7
        )
        db.add(admin)
        usuarios.append(admin)
        
        nombres = ["Ana Martinez", "Carlos Ruiz", "Maria Lopez", "Juan Perez", "Sofia Torres", 
                   "Luis Gonzalez", "Diego Hernandez", "Fernanda Diaz", "Jorge Sanchez", "Valeria Mora"]
        
        for n in nombres:
            u = models.Usuario(
                nombre=n,
                email=f"{n.split()[0].lower()}@uat.edu.mx",
                hashed_password=get_password_hash("123456"),
                nivel_confianza=random.randint(50, 95),
                racha_diaria=random.randint(0, 10)
            )
            db.add(u)
            usuarios.append(u)
            
        db.commit()
        for u in usuarios: db.refresh(u)
        
        print("2. Creando comunidades...")
        comunidades_data = [
            ("Ingeniería en Sistemas", "Mural oficial de FIME Sistemas"),
            ("Gaming UAT", "Comunidad para buscar equipo en e-sports"),
            ("Mercadito UAT", "Compra y venta de artículos escolares"),
            ("Facultad de Medicina", "Avisos y material de estudio")
        ]
        
        coms = []
        for c in comunidades_data:
            com = models.Comunidad(nombre=c[0], descripcion=c[1])
            db.add(com)
            coms.append(com)
        db.commit()
        for c in coms: db.refresh(c)
        
        # Unir al admin a TODAS las comunidades
        for c in coms:
            uc = models.UsuarioComunidad(usuario_id=admin.id, comunidad_id=c.id, rol="ADMIN")
            db.add(uc)
            
        # Unir aleatoriamente a los demás
        for i in range(1, len(usuarios)):
            mis_coms = random.sample(coms, random.randint(1, 3))
            for mc in mis_coms:
                uc = models.UsuarioComunidad(usuario_id=usuarios[i].id, comunidad_id=mc.id, rol="MIEMBRO")
                db.add(uc)
        db.commit()
        
        print("3. Creando publicaciones (Fake Data)")
        publicaciones = []
        mensajes = [
            "¿Alguien tiene el PDF de matemáticas discretas?",
            "Hoy no hubo clases de programación, raza.",
            "Vendo mi laptop a buen precio. Urge.",
            "¿A qué hora pasa el autobús de la ruta 1?",
            "El profe de base de datos pidió un proyecto en SQL.",
            "Alguien para echar retas de Valorant hoy a las 8pm?",
            "Perdí mi credencial cerca de la cafetería, si la ven echen un grito",
            "¿Saben si la biblioteca estará abierta este sábado?",
            "Duda con la inscripción, ¿dónde se entrega el formato B?",
            "Busco equipo para el proyecto final de ingeniería de software",
            "Ya publicaron los horarios del siguiente semestre!",
            "¿Alguien más no puede entrar al portal académico?",
            "Recomienden buenos lugares para comer cerca de FADU",
            "Pasen apuntes de la clase de anatomía de ayer plis",
            "Mañana hay paro en la facultad de derecho"
        ]
        
        for msg in mensajes:
            com = random.choice(coms)
            autor = random.choice(usuarios)
            pub = models.PublicacionComunidad(
                comunidad_id=com.id,
                autor_id=autor.id,
                contenido=msg,
                estado="APROBADA",
                likes_count=random.randint(0, 15)
            )
            db.add(pub)
            publicaciones.append(pub)
        db.commit()
        for p in publicaciones: db.refresh(p)
        
        print("4. Generando reacciones y comentarios...")
        comentarios_txt = ["Me interesa", "Simón", "Pasa info", "Yo también ocupo", "Gracias bro", "A mí me pasó lo mismo", "Jalo", "x2"]
        
        for p in publicaciones:
            # Reacciones
            num_likes = p.likes_count
            likers = random.sample(usuarios, min(num_likes, len(usuarios)))
            for liker in likers:
                reac = models.ReaccionPublicacion(publicacion_id=p.id, usuario_id=liker.id, tipo="LIKE")
                db.add(reac)
                
            # Comentarios
            for _ in range(random.randint(0, 3)):
                cm_txt = random.choice(comentarios_txt)
                cm_autor = random.choice(usuarios)
                coment = models.ComentarioPublicacion(
                    publicacion_id=p.id,
                    autor_id=cm_autor.id,
                    contenido=cm_txt
                )
                db.add(coment)
                
        db.commit()
        print("Base de datos recreada e hidratada con Fake Data exitosamente.")
        
    except Exception as e:
        print(f"Error generando datos: {e}")
        db.rollback()
    finally:
        db.close()

if __name__ == "__main__":
    run_seed()
