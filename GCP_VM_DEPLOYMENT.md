# GCP Free Tier VM Deployment Guide

This guide walks you through deploying the **Hospital Management System** on GCP's **Always Free `e2-micro` VM instance**. By running both the database, backend, and frontend inside Docker on the same free VM, your hosting costs will be **$0/month**.

---

## Step 1: Create the Free VM Instance

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Navigate to **Compute Engine** > **VM Instances** and click **Create Instance**.
3. Configure the VM with the following settings to qualify for the Free Tier:
   * **Name**: `hms-free-vm`
   * **Region**: Choose one of the following:
     * `us-central1` (Iowa)
     * `us-east1` (South Carolina)
     * `us-west1` (Oregon)
   * **Machine configuration**: 
     * Series: **E2**
     * Machine type: **e2-micro** (2 vCPU, 1 GB memory)
   * **Boot disk**:
     * Click **Change**
     * Operating System: **Ubuntu**
     * Version: **Ubuntu 22.04 LTS** or **24.04 LTS**
     * Boot disk type: **Standard persistent disk**
     * Size: **30 GB** (The free limit is 30 GB)
     * Click **Select**
   * **Firewall**:
     * Check **Allow HTTP traffic**
     * Check **Allow HTTPS traffic**
4. Click **Create** at the bottom of the page.

---

## Step 2: Configure GCP Firewall Rules

By default, GCP blocks all ports except SSH (22) and standard HTTP/HTTPS (80/443). Since our Docker Compose environment runs the frontend on port `3000` and the backend on port `8080`, we need to open these ports.

1. In the GCP Console search bar, search for **Firewall policies** (under VPC Network).
2. Click **Create Firewall Rule** at the top.
3. Configure the firewall rule:
   * **Name**: `allow-hms-ports`
   * **Targets**: **All instances in the network**
   * **Source IPv4 ranges**: `0.0.0.0/0` (Allows anyone to access the app)
   * **Protocols and ports**:
     * Check **Specified protocols and ports**
     * Check **TCP** and enter: `3000, 8080`
4. Click **Create**.

---

## Step 3: Install Docker and Docker Compose on the VM

1. In your VM Instances page, find your VM and click the **SSH** button in its row to open a browser-based terminal.
2. Run the following commands in the SSH terminal to update packages and install Docker:

   ```bash
   # Update package list
   sudo apt-get update

   # Install Docker
   sudo apt-get install -y docker.io

   # Install Docker Compose
   sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose

   # Start and enable Docker service
   sudo systemctl start docker
   sudo systemctl enable docker

   # Add your user to the docker group so you don't need sudo
   sudo usermod -aG docker $USER
   ```
3. **Important**: Close the SSH window and click **SSH** again to reconnect so the user group permissions take effect.

---

## Step 4: Clone the Code and Run the Application

1. In the VM's SSH terminal, clone your GitHub repository:
   ```bash
   git clone YOUR_GITHUB_REPOSITORY_URL hms
   cd hms
   ```
   *(If your repository is private, you can upload the folder using the "Upload File" option in the SSH window menu).*

2. Start the services using Docker Compose:
   ```bash
   docker-compose up -d --build
   ```
   *This command downloads MySQL, builds the Spring Boot API, compiles the Nginx web server, and starts everything in the background.*

---

## Step 5: Connect Frontend to Backend

1. Find the **External IP** address of your VM from the VM Instances page in your GCP Console (e.g., `35.200.100.50`).
2. Point your web browser to:
   ```text
   http://YOUR_VM_EXTERNAL_IP:3000/?api=http://YOUR_VM_EXTERNAL_IP:8080
   ```
   *Example:*
   `http://35.200.100.50:3000/?api=http://35.200.100.50:8080`

The system is now fully up, running, and accessible for free!
