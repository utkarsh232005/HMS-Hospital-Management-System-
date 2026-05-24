# Google Cloud Platform (GCP) Deployment Guide

This guide describes how to deploy the bifurcated **Hospital Management System** to Google Cloud Platform using **Google Cloud Run** for the services and **Google Cloud SQL (MySQL)** for the database.

```
                  ┌──────────────────────────────┐
                  │      Google Cloud Run        │
                  │   (hospital-frontend:80)     │
                  └──────────────┬───────────────┘
                                 │
                   Rest API calls (CORS enabled)
                                 │
                                 ▼
                  ┌──────────────────────────────┐
                  │      Google Cloud Run        │
                  │   (hospital-backend:8080)    │
                  └──────────────┬───────────────┘
                                 │
                       Cloud SQL Auth Proxy
                       (Unix Socket SocketFactory)
                                 ▼
                  ┌──────────────────────────────┐
                  │      Google Cloud SQL        │
                  │      (MySQL Instance)        │
                  └──────────────────────────────┘
```

---

## Prerequisites
1. Installed [Google Cloud SDK (gcloud CLI)](https://cloud.google.com/sdk/docs/install).
2. A billing-enabled GCP Project.
3. Docker installed locally (only if you wish to run builds locally, otherwise Cloud Build handles everything in the cloud).

---

## Step 1: Initialize GCP Project & APIs

Authenticate your CLI and set your target project ID:

```bash
# Log in to Google Cloud
gcloud auth login

# Set your active GCP project ID
gcloud config set project YOUR_PROJECT_ID
```

Enable the required GCP services:

```bash
gcloud services enable \
    run.googleapis.com \
    sqladmin.googleapis.com \
    cloudbuild.googleapis.com \
    artifactregistry.googleapis.com
```

---

## Step 2: Create Artifact Registry Repository

Create a Docker registry repository in your target region (default is `us-central1`):

```bash
gcloud artifacts repositories create hms-repo \
    --repository-format=docker \
    --location=us-central1 \
    --description="Docker repository for Hospital Management System"
```

---

## Step 3: Set Up Cloud SQL (MySQL) Database

1. Create a MySQL 8.0 Cloud SQL Instance (using a lightweight `db-f1-micro` tier for cost savings):

   ```bash
   gcloud sql instances create hms-db \
       --database-version=MYSQL_8_0 \
       --tier=db-f1-micro \
       --region=us-central1 \
       --root-password="YourSecureDBPassword"
   ```

2. Create the application database inside the instance:

   ```bash
   gcloud sql databases create hospital_db --instance=hms-db
   ```

3. Retrieve the **Connection Name** of your Cloud SQL Instance:

   ```bash
   gcloud sql instances describe hms-db --format="value(connectionName)"
   ```
   *This will output a string like `your-project-id:us-central1:hms-db`. Keep this handy.*

---

## Step 4: Deploy the Backend (Spring Boot API)

1. Use Cloud Build to build, tag, and upload the backend container, and deploy it initially:

   ```bash
   gcloud builds submit --config=backend/cloudbuild.yaml backend
   ```

2. Configure the Backend Service to connect securely to Cloud SQL. Replace `<CONNECTION_NAME>` with your Cloud SQL connection name from Step 3:

   ```bash
   # Attach the Cloud SQL instance connection to the service
   gcloud run services update hospital-backend \
       --add-cloudsql-instances=<CONNECTION_NAME> \
       --region=us-central1

   # Configure connection environment variables
   gcloud run services update hospital-backend \
       --set-env-vars="SPRING_DATASOURCE_URL=jdbc:mysql:///hospital_db?cloudSqlInstance=<CONNECTION_NAME>&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false,SPRING_DATASOURCE_USERNAME=root,SPRING_DATASOURCE_PASSWORD=YourSecureDBPassword" \
       --region=us-central1
   ```

3. Note down the deployed **Service URL** of your backend:
   ```bash
   gcloud run services describe hospital-backend --region=us-central1 --format="value(status.url)"
   ```
   *(e.g., `https://hospital-backend-xxxxxx.a.run.app`)*

---

## Step 5: Deploy the Frontend (Nginx Static Site)

1. Submit the frontend files to Cloud Build for compilation and deployment:

   ```bash
   gcloud builds submit --config=frontend/cloudbuild.yaml frontend
   ```

2. Note down the deployed **Service URL** of your frontend:
   ```bash
   gcloud run services describe hospital-frontend --region=us-central1 --format="value(status.url)"
   ```
   *(e.g., `https://hospital-frontend-xxxxxx.a.run.app`)*

---

## Step 6: Connect Frontend to Backend

The frontend application uses a flexible connection configuration mechanism inside `app.js`.

### Option A: Query Parameter (No Redeployment Required)
Navigate to your frontend URL with the `api` query parameter pointing to the backend's URL:
```text
https://<FRONTEND_URL>/?api=https://<BACKEND_URL>
```
*Example:*
`https://hospital-frontend-xxxxxx.a.run.app/?api=https://hospital-backend-xxxxxx.a.run.app`

### Option B: Hardcode Default API URL
If you want the frontend to automatically connect without needing the query parameter, you can edit `frontend/app.js` locally and change line 2:

```javascript
// From:
const API_BASE = params.get("api") || window.localStorage.getItem("medicoreApiBase") || "http://localhost:8080";

// To:
const API_BASE = params.get("api") || window.localStorage.getItem("medicoreApiBase") || "https://hospital-backend-xxxxxx.a.run.app";
```

Then redeploy the frontend:
```bash
gcloud builds submit --config=frontend/cloudbuild.yaml frontend
```
Now, users can navigate directly to `https://<FRONTEND_URL>` without query parameters!
