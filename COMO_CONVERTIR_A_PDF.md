# 📄 Cómo Convertir la Documentación a PDF

## Método 1: VS Code (Más Fácil) ⭐

1. **Instalar extensión:**
   - Abre VS Code
   - Ve a Extensions (Ctrl+Shift+X)
   - Busca: **"Markdown PDF"** por yzane
   - Haz clic en **Install**

2. **Convertir:**
   - Abre el archivo: `DOCUMENTACION_PROYECTO.md`
   - Presiona `Ctrl+Shift+P` (o F1)
   - Escribe: **"Markdown PDF: Export (pdf)"**
   - Enter
   - El PDF se guardará en la misma carpeta

---

## Método 2: Navegador Web

1. **Abrir en navegador:**
   - Clic derecho en `DOCUMENTACION_PROYECTO.md`
   - "Open with Live Server" (si tienes la extensión)
   - O arrastra el archivo a Chrome/Edge

2. **Imprimir como PDF:**
   - Presiona `Ctrl+P`
   - En "Destino" selecciona: **Guardar como PDF**
   - Ajusta márgenes y orientación
   - Haz clic en **Guardar**

---

## Método 3: Pandoc (Profesional)

1. **Instalar Pandoc:**
   ```powershell
   winget install pandoc
   ```

2. **Convertir:**
   ```powershell
   cd C:\Users\Nefta\Proyecto_Marin
   pandoc DOCUMENTACION_PROYECTO.md -o DOCUMENTACION_PROYECTO.pdf --pdf-engine=wkhtmltopdf
   ```

---

## Método 4: Online (Sin instalación)

1. Ve a: https://www.markdowntopdf.com/
2. Copia y pega el contenido del archivo
3. Haz clic en "Convert"
4. Descarga el PDF

---

## 📌 Recomendación

**Usa el Método 1** (Markdown PDF en VS Code) - Es el más simple y da mejores resultados.

El archivo ya está listo en: `Proyecto_Marin/DOCUMENTACION_PROYECTO.md`
