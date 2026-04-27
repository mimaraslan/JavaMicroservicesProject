#!/bin/bash

# Hata durumunda scripti durdur
set -e

echo "🚀 En güncel araçların kurulumu başlatılıyor..."

# 0. Paket yöneticisini temizle
sudo dnf clean all

# 1. Sistem Güncelleme ve Temel Araçlar (Conflict hatasını --allowerasing çözer)
sudo dnf update -y
sudo dnf install -y git wget unzip curl yum-utils --allowerasing

# 2. Java 21
sudo dnf install -y java-21-amazon-corretto

# 3. Node.js ve NPM
sudo dnf install nodejs -y

# 4. Jenkins Kurulumu
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
sudo dnf install -y jenkins
sudo systemctl enable --now jenkins

# 5. Terraform
sudo yum-config-manager --add-repo https://rpm.releases.hashicorp.com/AmazonLinux/hashicorp.repo
sudo dnf install -y terraform

# 6. Maven ve Ansible
sudo dnf install -y maven ansible --allowerasing

# 7. Docker Kurulumu
sudo dnf install -y docker
sudo usermod -aG docker ec2-user
sudo usermod -aG docker jenkins
sudo systemctl enable --now docker
sudo chmod 777 /var/run/docker.sock

# Docker Compose V2 (Kurulum yolu düzeltildi)
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 8. SonarQube (Docker Image)
# Not: Makine açılır açılmaz docker daemon hazır olmayabilir, kısa bir bekleme ekliyoruz
sleep 5
sudo docker run -d --name sonar -p 9000:9000 --restart unless-stopped sonarqube:latest

# 9. TRIVY
RELEASE_VERSION=$(curl --silent "https://api.github.com/repos/aquasecurity/trivy/releases/latest" | grep '"tag_name":' | sed -E 's/.*"([^"]+)".*/\1/')
wget "https://github.com/aquasecurity/trivy/releases/download/${RELEASE_VERSION}/trivy_${RELEASE_VERSION:1}_Linux-64bit.rpm"
sudo dnf localinstall -y "trivy_${RELEASE_VERSION:1}_Linux-64bit.rpm"
rm "trivy_${RELEASE_VERSION:1}_Linux-64bit.rpm"

# 10. Kubernetes Araçları
# Kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x ./kubectl
sudo mv ./kubectl /usr/local/bin/

# Eksctl
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin/

# Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# 11. Database Kurulumları
sudo dnf install -y mariadb105-server
sudo systemctl enable --now mariadb

sudo dnf install -y postgresql16-server postgresql16
sudo postgresql-setup --initdb
sudo systemctl enable --now postgresql

# 12. AWS CLI v2
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip -q awscliv2.zip
sudo ./aws/install
rm -rf awscliv2.zip aws

echo "✅ KURULUM TAMAMLANDI!"