# CLI-Only Deployment Guide (Vercel, Render, & Clever Cloud)

This guide shows how to provision your database, deploy your Spring Boot backend, and deploy your static frontend using only command-line tools.

---

## Step 1: Install the CLI Tools

Install the official CLI tools for Vercel and Clever Cloud:

```bash
npm install -g vercel clever-tools
```

---

## Step 2: Deploy the Database (Clever Cloud CLI)

1. **Log in to Clever Cloud**:
   ```bash
   clever login
   ```
   *(This opens a browser tab to authorize your CLI session. Once authorized, return to your terminal).*

2. **Create a MySQL database addon**:
   ```bash
   clever addon create mysql-addon hms-db --plan dev
   ```

3. **Retrieve the database credentials**:
   ```bash
   clever addon env mysql-addon
   ```
   *Copy the output. You will need the host, database name, user, and password for the next step.*

---

## Step 3: Deploy the Backend (Render Blueprint IaC)

Render uses **Infrastructure-as-Code (IaC)** called **Blueprints** to automate deployments without using their UI.

1. Create a `render.yaml` file in your repository root:

   ```yaml
   services:
     - type: web
       name: hospital-backend
       env: docker
       rootDir: backend
       plan: free
       envVars:
         - key: SPRING_DATASOURCE_URL
           value: jdbc:mysql://<DB_HOST>:3306/<DB_NAME>?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
         - key: SPRING_DATASOURCE_USERNAME
           value: <DB_USER>
         - key: SPRING_DATASOURCE_PASSWORD
           value: <DB_PASSWORD>
         - key: SPRING_JPA_HIBERNATE_DDL_AUTO
           value: update
   ```
   *(Replace `<DB_HOST>`, `<DB_NAME>`, `<DB_USER>`, and `<DB_PASSWORD>` with the credentials you copied in Step 2).*

2. Push this file to GitHub:
   ```bash
   git add render.yaml
   git commit -m "Add Render Blueprint configuration"
   git push origin main
   ```

3. Connect your repository to Render once in your life. Any subsequent git push automatically deploys the backend container!

---

## Step 4: Deploy the Frontend (Vercel CLI)

1. **Log in to Vercel**:
   ```bash
   vercel login
   ```

2. **Deploy the frontend directory**:
   Navigate to the `frontend` folder and run the deployment command:
   ```bash
   cd frontend
   vercel --prod --yes
   ```
   *Vercel will upload, build, deploy, and print your production URL directly to the terminal!*
