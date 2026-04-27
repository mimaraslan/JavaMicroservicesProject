output "region" {
    description = "Jumphost Server region"
    value = var.region
}
output "jumphost_public_ip" {
  description = "Elastic IP address of the EC2 jumphost"
  value       = aws_eip.jumphost_eip.public_ip
}