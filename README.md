# MediCore Hospital Management System (HMS)

This repository contains the MediCore Hospital Management System, split into a Spring Boot backend API and an Nginx-served static frontend.

## Project Structure

```
HMS-Hospital-Management-System-/
├── backend/            # Spring Boot REST API (Java 17 / Maven)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── frontend/           # Static SPA web interface (HTML, CSS, JS)
│   ├── index.html
│   ├── styles.css
│   ├── app.js
│   └── Dockerfile
├── docker-compose.yml  # Local multi-container orchestration config
├── GCP_DEPLOYMENT.md   # Deployment instructions for Google Cloud Run
├── GCP_VM_DEPLOYMENT.md  # Always-free VM deployment guide
├── VERCEL_RENDER_DEPLOYMENT.md  # Free Vercel & Render deployment guide
├── CLI_DEPLOYMENT.md  # CLI-only deployment instructions (Vercel, Render)
└── hospital_db.session.sql
```

## Running Locally

### 1. Using Docker Compose (Recommended)
Make sure you have Docker installed and running, then execute:

```bash
docker compose up --build
```

- The frontend will be accessible at: `http://localhost:3000`
- The backend API will be accessible at: `http://localhost:8080`

### 2. Manual Development Run
If you want to run the services individually without Docker:

#### Start the Backend (API):
```bash
cd backend
mvn spring-boot:run
```

#### Start the Frontend:
Start a static web server from the `frontend` folder:
```bash
cd frontend
python3 -m http.server 3000
```
Then visit `http://localhost:3000` in your web browser.

---

## Deployment Options

We provide the following deployment guides:
1. **CLI-Only Deployment (Vercel & Render)**: Refer to [CLI_DEPLOYMENT.md](file:///Users/utkarshpatrikar/Code%20Files/HMS-Hospital-Management-System-/CLI_DEPLOYMENT.md) to deploy all resources completely via terminal CLIs.
2. **Vercel & Render Dashboard**: Refer to [VERCEL_RENDER_DEPLOYMENT.md](file:///Users/utkarshpatrikar/Code%20Files/HMS-Hospital-Management-System-/VERCEL_RENDER_DEPLOYMENT.md) to deploy using Vercel & Render web dashboards.
3. **GCP Serverless (Cloud Run & Cloud SQL)**: Refer to [GCP_DEPLOYMENT.md](file:///Users/utkarshpatrikar/Code%20Files/HMS-Hospital-Management-System-/GCP_DEPLOYMENT.md) for deploying auto-scaling containerized services.
4. **GCP Free Tier VM (Compute Engine)**: Refer to [GCP_VM_DEPLOYMENT.md](file:///Users/utkarshpatrikar/Code%20Files/HMS-Hospital-Management-System-/GCP_VM_DEPLOYMENT.md) for hosting the entire stack on GCP's always-free VM tier.
