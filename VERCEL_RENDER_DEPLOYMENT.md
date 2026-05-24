# Vercel & Render Deployment Guide

This guide describes how to deploy the bifurcated **Hospital Management System** using **Vercel** (for the frontend static site) and **Render** (for the Spring Boot backend API and database). This setup is 100% free-tier eligible.

```
                  ┌──────────────────────────────┐
                  │           Vercel             │
                  │   (hospital-frontend UI)     │
                  └──────────────┬───────────────┘
                                 │
                   Rest API calls (CORS enabled)
                                 │
                                 ▼
                  ┌──────────────────────────────┐
                  │           Render             │
                  │   (hospital-backend API)     │
                  └──────────────┬───────────────┘
                                 │
                            JDBC (TCP)
                                 ▼
                  ┌──────────────────────────────┐
                  │    clever-cloud / Aiven      │
                  │       (Free MySQL DB)        │
                  └──────────────────────────────┘
```

---

## Part 1: Deploy the Database (Clever Cloud or Aiven)

Since Render does not offer a free MySQL database out-of-the-box (only PostgreSQL), we will use **Clever Cloud** or **Aiven** to host our free MySQL database.

### Clever Cloud Setup (Easiest Free MySQL)
1. Go to [Clever Cloud](https://www.clever-cloud.com/) and sign up.
2. Go to the Console, click **Create** > **an add-on** > **MySQL**.
3. Choose the **Free "Shared" (Dev) plan** and select your region.
4. Click through to create the database.
5. In your database's **Addon Dashboard**, copy the following connection details:
   * **Host** (e.g. `brw12345-mysql.services.clever-cloud.com`)
   * **Database Name** (e.g. `brw12345`)
   * **User** (e.g. `u12345`)
   * **Password**

---

## Part 2: Deploy the Backend to Render

Render can build and run Docker containers directly from your Git repository.

1. Go to [Render](https://render.com/) and sign in using your GitHub account.
2. Click **New** > **Web Service**.
3. Select your repository `HMS-Hospital-Management-System-`.
4. Configure the Web Service settings:
   * **Name**: `hospital-backend`
   * **Region**: Choose the region closest to you.
   * **Root Directory**: `backend` *(This is important!)*
   * **Runtime**: **Docker** *(Render will automatically find and use `backend/Dockerfile`)*
   * **Instance Type**: **Free**
5. Expand the **Advanced** section and add the following **Environment Variables**:
   * `SPRING_DATASOURCE_URL` = `jdbc:mysql://<YOUR_DB_HOST>:3306/<YOUR_DB_NAME>?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   * `SPRING_DATASOURCE_USERNAME` = `<YOUR_DB_USER>`
   * `SPRING_DATASOURCE_PASSWORD` = `<YOUR_DB_PASSWORD>`
   * `SPRING_JPA_HIBERNATE_DDL_AUTO` = `update`
6. Click **Create Web Service**.
   * Render will download your code, run the Maven build inside the container, and deploy it.
7. Once deployed, note down the Render URL:
   * *(e.g. `https://hospital-backend.onrender.com`)*

---

## Part 3: Deploy the Frontend to Vercel

Vercel is optimized for static sites and offers a fast, free CDN.

1. Go to [Vercel](https://vercel.com/) and sign in with GitHub.
2. Click **Add New** > **Project**.
3. Import your repository `HMS-Hospital-Management-System-`.
4. Configure the Project Settings:
   * **Root Directory**: Click *Edit* and select the `frontend` folder.
   * **Framework Preset**: **Other** (since this is plain HTML/JS).
   * **Build and Output Settings**: Leave as default.
5. Click **Deploy**.
   * Vercel will deploy your static frontend instantly.
6. Once deployed, you will get a Vercel URL:
   * *(e.g. `https://hms-frontend.vercel.app`)*

---

## Part 4: Connect the Frontend to the Backend

To link your Vercel frontend to your Render backend:

### Option A: Query Parameter (No redeployment needed)
Open your Vercel URL and pass the Render URL as the `api` parameter:
```text
https://<YOUR_VERCEL_URL>/?api=https://<YOUR_RENDER_URL>
```
*Example:*
`https://hms-frontend.vercel.app/?api=https://hospital-backend.onrender.com`

### Option B: Hardcode Render URL
1. Open `frontend/app.js` locally.
2. Change the default backend fallback address on Line 2 from `http://localhost:8080` to your Render URL:
   ```javascript
   const API_BASE = params.get("api") || window.localStorage.getItem("medicoreApiBase") || "https://hospital-backend.onrender.com";
   ```
3. Commit and push the changes to GitHub:
   ```bash
   git add frontend/app.js
   git commit -m "Configure default API URL to Render backend"
   git push origin main
   ```
4. **Vercel will automatically redeploy** the frontend. You can now visit `https://<YOUR_VERCEL_URL>` directly without any query parameters!
