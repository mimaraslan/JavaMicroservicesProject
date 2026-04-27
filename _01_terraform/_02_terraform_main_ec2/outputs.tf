output "region" {
    description = "mydemo Server region"
    value = var.region
}
output "mydemo_public_ip" {
  description = "Elastic IP address of the EC2 mydemo"
  value       = aws_eip.mydemo_eip.public_ip
}