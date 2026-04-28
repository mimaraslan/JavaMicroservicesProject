resource "aws_instance" "ec2" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  key_name               = var.key_name
  subnet_id              = aws_subnet.public-subnet1.id
  vpc_security_group_ids = [aws_security_group.security-group.id]
  iam_instance_profile   = aws_iam_instance_profile.instance-profile.name
  root_block_device {
    volume_size = 30
  }
  

  tags = {
    Name = var.instance_name
  }
}

# --- MEVCUT MAKİNEYE KURULUMU ZORLAYAN BÖLÜM ---
resource "null_resource" "provisioner" {
  # Script dosyası her değiştiğinde veya makine her değiştiğinde bu blok tekrar tetiklenir
  triggers = {
    script_hash = filemd5("./install-tools.sh")
    instance_id = aws_instance.ec2.id
  }

  connection {
    type        = "ssh"
    user        = "ec2-user"
    # Bir üst klasördeki pem dosyasını kullanıyoruz
    private_key = file("../My-Key-Linux-Amazon.pem") 
    host        = aws_eip.mydemo_eip.public_ip
  }

  # 1. Scripti makinenin içine kopyala
  provisioner "file" {
    source      = "install-tools.sh"
    destination = "/tmp/install-tools.sh"
  }

  # 2. Scripti çalıştır (Kurulum loglarını terminalde göreceksin)
  provisioner "remote-exec" {
    inline = [
      "chmod +x /tmp/install-tools.sh",
      "sudo /tmp/install-tools.sh"
    ]
  }

  # EIP atanmadan SSH yapılamayacağı için bağımlılık ekliyoruz
  depends_on = [aws_eip_association.mydemo_eip_assoc]
}

resource "aws_eip" "mydemo_eip" {
  domain = "vpc"

  tags = {
    Name = "${var.instance_name}-eip"
  }

  depends_on = [aws_internet_gateway.igw]
}

resource "aws_eip_association" "mydemo_eip_assoc" {
  instance_id   = aws_instance.ec2.id
  allocation_id = aws_eip.mydemo_eip.id
}