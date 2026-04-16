"""
Servicio de envío de correos electrónicos mediante SMTP (Gmail).
Requiere configurar las variables en el archivo .env del backend.
"""
import smtplib
import ssl
import os
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from dotenv import load_dotenv

load_dotenv()

SMTP_HOST     = os.getenv("SMTP_HOST", "smtp.gmail.com")
SMTP_PORT     = int(os.getenv("SMTP_PORT", "465"))
SMTP_USER     = os.getenv("SMTP_USER", "")
SMTP_PASSWORD = os.getenv("SMTP_PASSWORD", "")
FROM_NAME     = os.getenv("FROM_NAME", "DevCore UAT")


def send_recovery_code(to_email: str, code: str) -> bool:
    """
    Envía un código de recuperación de 6 dígitos al correo indicado.
    Retorna True si fue exitoso, False si hubo error.
    """
    if not SMTP_USER or not SMTP_PASSWORD:
        print(f"[EMAIL - SIN CONFIGURAR] Código para {to_email}: {code}")
        return False

    subject = "Código de recuperación - DevCore UAT"

    html_body = f"""
    <html>
    <body style="margin:0; padding:0; background-color:#1a1a1a; font-family: Arial, sans-serif;">
        <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#1a1a1a; padding: 40px 20px;">
            <tr>
                <td align="center">
                    <table width="420" cellpadding="0" cellspacing="0" style="background-color:#242424; border-radius:16px; padding:36px 40px; max-width:420px;">
                        <tr>
                            <td align="center" style="padding-bottom:6px;">
                                <span style="font-size:22px; font-weight:bold; color:#FF6B35;">DevCore UAT</span>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="padding-bottom:20px;">
                                <span style="font-size:16px; color:#cccccc; font-weight:600;">Recuperación de contraseña</span>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="padding-bottom:24px;">
                                <p style="margin:0; font-size:14px; color:#999999; line-height:1.6;">
                                    Recibimos una solicitud para restablecer tu contraseña.<br>
                                    Usa el siguiente código de verificación:
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="padding-bottom:24px;">
                                <div style="display:inline-block; background:#1a1a1a; border:2px solid #FF6B35; border-radius:12px; padding:14px 32px;">
                                    <span style="font-size:32px; font-weight:bold; letter-spacing:8px; color:#FF6B35; white-space:nowrap;">
                                        {code}
                                    </span>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="padding-bottom:12px;">
                                <p style="margin:0; font-size:13px; color:#888888;">
                                    Este código expira en <strong style="color:#FF6B35;">15 minutos</strong>.
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td align="center">
                                <p style="margin:0; font-size:11px; color:#555555;">
                                    Si no solicitaste esto, ignora este mensaje.
                                </p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </body>
    </html>
    """

    msg = MIMEMultipart("alternative")
    msg["Subject"] = subject
    msg["From"]    = f"{FROM_NAME} <{SMTP_USER}>"
    msg["To"]      = to_email
    msg.attach(MIMEText(html_body, "html"))

    try:
        context = ssl.create_default_context()
        with smtplib.SMTP_SSL(SMTP_HOST, SMTP_PORT, context=context) as server:
            server.login(SMTP_USER, SMTP_PASSWORD)
            server.sendmail(SMTP_USER, to_email, msg.as_string())
        print(f"[EMAIL] Código enviado correctamente a {to_email}")
        return True
    except Exception as e:
        print(f"[EMAIL ERROR] No se pudo enviar a {to_email}: {e}")
        return False
