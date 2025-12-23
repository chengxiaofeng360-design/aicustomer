# AI Chat & Knowledge Base Migration Package

This package contains all the necessary files to migrate the AI Chat and Knowledge Base functionality.

## Folder Structure

- **backend/**: Contains Java source code.
    - `src/main/java`: Backend Controllers, Services, Entities, Mappers, and Configs.
    - `src/main/resources`: Mapper XML files.
- **frontend/**: Contains Frontend assets.
    - `ai-chat.html`: Main chat interface.
    - `js/`, `css/`: Scripts and styles.
    - `lib/`: Third-party libraries (Bootstrap, etc.).
- **database/**: Contains `schema.sql` to create necessary tables.
- **embedding-service/**: Python-based vector embedding service.

## Instructions

1. **Database**: Run `database/schema.sql` in your MySQL database.
2. **Backend**:
   - Copy `backend/src` content to your Spring Boot project.
   - Add necessary dependencies to your `pom.xml` (see implementation_plan.md).
   - Configure `application.yml` with your DeepSeek API key and DB connection.
3. **Frontend**:
   - Copy `frontend` content to your web server's static directory.
4. **Embedding Service**:
   - Run `pip install -r embedding-service/requirements.txt`.
   - Start with `python embedding-service/app.py`.

For more detailed steps, refer to the provided `implementation_plan.md` artifact from the AI interaction.
