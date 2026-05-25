#!/bin/bash

# Configuration Variables
ZONE="us-central1-a"
VM_NAME="hms-free-vm"
FIREWALL_RULE="allow-hms-ports"
REPO_URL="https://github.com/utkarsh232005/HMS-Hospital-Management-System-.git"

# Get active project ID
PROJECT_ID=$(gcloud config get-value project 2>/dev/null)

if [ -z "$PROJECT_ID" ]; then
    echo "Error: No active GCP project set. Please set your project first using:"
    echo "gcloud config set project YOUR_PROJECT_ID"
    exit 1
fi

echo "==========================================================="
echo "Deploying HMS to VM in GCP Project: $PROJECT_ID"
echo "==========================================================="

# 1. Enable Compute Engine API
echo "Step 1: Enabling Compute Engine API (this may take a minute)..."
gcloud services enable compute.googleapis.com

# 2. Create VPC Firewall Rule for Ports 3000 and 8080
echo "Step 2: Creating firewall rule '$FIREWALL_RULE'..."
# Check if firewall rule already exists
if gcloud compute firewall-rules describe "$FIREWALL_RULE" &>/dev/null; then
    echo "Firewall rule already exists. Skipping creation."
else
    gcloud compute firewall-rules create "$FIREWALL_RULE" \
        --direction=INGRESS \
        --priority=1000 \
        --network=default \
        --action=ALLOW \
        --rules=tcp:3000,tcp:8080 \
        --source-ranges=0.0.0.0/0 \
        --target-tags=hms-server
fi

# 3. Define startup script that will execute inside the VM on boot
echo "Step 3: Creating VM instance '$VM_NAME' in zone '$ZONE'..."

# Create VM with the E2-Micro Always-Free tier configuration
gcloud compute instances create "$VM_NAME" \
    --project="$PROJECT_ID" \
    --zone="$ZONE" \
    --machine-type=e2-micro \
    --image-family=ubuntu-2204-lts \
    --image-project=ubuntu-os-cloud \
    --boot-disk-size=30GB \
    --boot-disk-type=pd-standard \
    --tags=hms-server \
    --metadata=startup-script="#!/bin/bash
# Update packages
apt-get update

# Install Docker and Git
apt-get install -y docker.io git

# Install Docker Compose
curl -L \"https://github.com/docker/compose/releases/latest/download/docker-compose-\$(uname -s)-\$(uname -m)\" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Start Docker service
systemctl start docker
systemctl enable docker

# Clone repository and deploy
mkdir -p /opt
cd /opt
git clone $REPO_URL hms
cd hms
docker-compose up -d --build
"

echo "==========================================================="
echo "Instance creation initiated!"
echo "==========================================================="
echo "Please wait 2-3 minutes for the VM to boot, install Docker, and download images."
echo ""
echo "To check the deployment progress, you can SSH into the instance and run:"
echo "sudo journalctl -u google-startup-scripts.service -f"
echo ""
echo "To find your VM's public IP, run:"
echo "gcloud compute instances describe $VM_NAME --zone=$ZONE --format='value(networkInterfaces[0].accessConfigs[0].natIP)'"
echo "==========================================================="
