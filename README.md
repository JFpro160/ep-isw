# Ingeniería de Software – Kit para el Final

Este repositorio mezcla dos cosas que el profe mencionó: tener claro qué revisar para la parte teórica y contar con un backend de referencia que demuestre arquitectura limpia, seguridad y pruebas sin depender de BD/Frontend. Úsalo como guía rápida durante el examen y como base de implementación si te toca programar algo parecido al caso del **Character Manager** que describieron en clases.

## 1. Qué viene en el examen
- **Estructura (correo oficial)** `correos.md:1`: 25 min teoría en hoja, 60 min implementación desde terminal (sin BD ni frontend), 15 min preguntas de comprensión. Evalúan requisitos, diseño OO, calidad, pruebas y explicar decisiones.
- **Guía de repaso** `Repaso-Examen.pdf`: describe el mismo caso de estudio con stack Spring Boot + PostgreSQL + frontend y una rúbrica con énfasis en arquitectura, persistencia, patrones, seguridad (Spring Security/JWT), frontend que consuma al menos un endpoint seguro y pipeline/ Sonar. Aunque ahora dijeron “sin BD ni front”, ese documento deja claro los criterios que sí mirarán (DDD, seguridad, patrones, CI, SonarQube).
- **Asesoría de Jeremy** `asesoria/GMT20251121-011655_Recording_1440x900.mp4.txt`: insiste en usar arquitectura en capas/DDD, separar Presentation/Application/Domain/Infrastructure, configurar `application.properties`, dependencias en `pom.xml`, servicios + repositorios y excepciones. También mostraron cómo enchufar JWT y manejar tokens.
- **Slides y guías** (carpeta `material/`): repasan requerimientos, calidad, arquitectura DDD/Hexagonal, patrones (Adapter, Factory, Template), DevOps, SonarQube. Preguntas teóricas muy alineadas (RF vs RNF, ventajas de UML, beneficios de CI+VCS, etc.).
- **Correos SonarQube** `correos.md:15`: dieron llaves para proyectos Backend/Frontend y recomiendan levantar Sonar local con Docker. Aun si no lo corren en el examen, sí pueden pedirte que describas cómo medir calidad o simular un pipeline.

👉 **Conclusión:** prepara respuestas breves para arquitectura vs monolito, separación RF/RNF, diagramas UML, DevOps/CI. En la práctica aplica principios DDD, servicios y patrones de diseño, seguridad con tokens o Basic, validaciones, pruebas. Ten listo un Pitch sobre cómo integrar con Sonar/CI aunque no lo ejecutes.

## 2. Proyecto genérico incluido (`backend/`)

El folder `backend` incluye un Spring Boot 3 (Java 17) con:

- **Arquitectura DDD en capas**: `presentation` (controllers/DTO), `application` (use cases), `domain` (modelos/reglas), `infrastructure` (repositorios in-memory y config). Cambiar a Postgres solo exige crear repos que implementen las interfaces.
- **Endpoints alineados con el caso**:
  - `POST /api/auth/register` y `POST /api/auth/login` para manejar usuarios y emitir tokens firmados (HMAC) sin depender de librerías externas.
  - `POST /api/characters`, `GET /api/characters`, `GET /api/characters/{id}`; protegidos por JWT y validados con Bean Validation.
  - `POST /api/comments` y `GET /api/comments/{characterId}` con políticas sencillas anti-spam.
- **Seguridad**: Spring Security stateless con filtro propio que valida tokens creados por `TokenService`. Passwords se guardan con `BCryptPasswordEncoder`. Puedes intercambiar el servicio por Basic Auth o por JWT real (jjwt/Java JWT) si te lo piden.
- **Pruebas unitarias**: servicios y controladores tienen tests con `@WebMvcTest` + `MockMvc` y tests del dominio para cubrir reglas básicas (la cobertura y los assertions ayudan a hablar de calidad ante el profe o Sonar).
- **Configuración lista**: `application.yaml` con llaves del token, propiedades para switch a Postgres/H2 y perfiles `dev` / `test`.
- **Documentación**: en este README está el checklist de pasos para correr, probar y cómo extender.

### Ejecutar localmente
```bash
cd backend
mvn clean verify
./mvnw spring-boot:run # si decides usar wrapper
```

Por defecto usa repos in-memory, ideal si el profe insiste en “sin BD”. Cambia `CharacterRepository` por una implementación JPA si necesitas persistencia real.

### Cómo adaptarlo durante el examen
1. **Rebrand rápido**: renombra paquetes/clases para el nuevo dominio (por ejemplo, `Character` → `Mission` o `Event`).
2. **Ajusta DTOs** en `presentation.dto` y el dominio; las validaciones ya están montadas.
3. **Extiende el dominio** agregando nuevas entidades/servicios manteniendo la misma separación. Los use cases reciben comandos, así que puedes enchufar validaciones extras sin tocar controllers.
4. **Seguridad**: cambia roles/permisos en `SecurityConfig` o crea un `@PreAuthorize`.
5. **Quality hooks**: hay scripts en `backend/pom.xml` para `spotless`, pruebas y un profile `sonar`. Agrega tus tokens en variables de entorno y corres `mvn -Psonar sonar:sonar`.

### Integración con SonarQube
1. **Levanta Sonar**: usa el servidor que dieron o `docker run -d -p 9000:9000 sonarqube:community`.
2. **Configura credenciales** (backend/token que compartieron).
3. **Ejecuta el análisis** desde `backend/`:
   ```bash
   export SONAR_HOST_URL=http://localhost:9000 # o el que te dieron
   export SONAR_TOKEN=sqp_xxx
   mvn clean verify # genera reportes de pruebas + JaCoCo (lo pide sonar-project.properties)
   mvn -Psonar sonar:sonar \
     -Dsonar.login=$SONAR_TOKEN \
     -Dsonar.host.url=$SONAR_HOST_URL \
     -Dsonar.projectKey=Backend-Student-40
   ```
   Cambia `sonar.projectKey` por el que te asignaron o edita `sonar-project.properties`. El perfil `sonar` agrega defaults (`pom.xml:65`) y el archivo `backend/sonar-project.properties` define rutas de código y reportes, así que solo pasas las overrides necesarias.
4. **Explica al profe**: mencionas que “corro `mvn clean verify` para asegurarme de pasar los tests y Spotless, luego `mvn -Psonar sonar:sonar` para subir métricas de cobertura, smells y vulnerabilidades contra el Quality Gate”.

### Docker Compose listo
- `docker-compose.yml` levanta dos servicios:
  1. `sonarqube` con la imagen `sonarqube:lts-community`, listo para exponer el dashboard en `http://localhost:9000`.
  2. `backend` que construye el jar con el `Dockerfile` (multi-stage Maven → JRE) y expone `http://localhost:8080`.
- Para usarlo:
  ```bash
  docker compose up -d sonarqube        # inicia Sonar
  # espera ~1 min, cambia la contraseña admin/admin y genera tu token
  docker compose up -d backend          # construye y levanta el API (opcional si solo quieres el server)
  ```
- Si quieres solo el backend sin Sonar: `docker compose up -d backend`.
- Cuando termines: `docker compose down` y `docker volume rm ep-isw_sonarqube_*` si deseas limpiar los datos.

### Cobertura y calidad local
- El pipeline de `mvn clean verify` ejecuta Spotless + pruebas + JaCoCo. Con los tests actuales la cobertura global supera el 80 % (≈83 % según `target/site/jacoco/jacoco.xml`), así que puedes hablar de “Quality Gate listo” incluso sin Sonar.
- Si agregas nuevas features, escribe o actualiza tests antes de repetir `mvn clean verify`; así mantienes la cobertura por encima del umbral sin sorpresas.

## 3. Cheatsheet para responder al profe
- **Arquitectura REST vs Monolito**: resalta escalabilidad, despliegues independientes, alineación con consumo multiplataforma. Señala costos extra de complejidad si el dominio es pequeño.
- **RF vs RNF**: RF describen comportamientos observables; RNF limitan cómo se cumple (seguridad, performance, usabilidad). Separarlos evita que pruebas omitan constraints y que la arquitectura ignore SLAs.
- **UML**: Casos de uso para clarificar expectativas con stakeholders; secuencia ayuda a descubrir integraciones, mensajes y responsabilidades → menos bugs de integración.
- **DevOps / CI**: pipelines automáticos garantizan build reproducible, ejecutan tests/sonar en cada push, reducen riesgo de regresiones y mantienen trazabilidad de cambios.
- **SonarQube**: conoce métricas (Bugs, Vulnerabilidades, Code Smells, Coverage, Duplication). Explica cómo Quality Gates evitan deuda técnica antes de fusionar.
- **Docker local**: compone `postgres + sonar` si lo piden (usa el `docker-compose.yml` de clase o prepara un snippet).

Con esto tienes tanto el mapa conceptual como el proyecto base para aterrizar rápido cualquier requerimiento del examen.
