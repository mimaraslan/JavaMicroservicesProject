output "region" {
  description = "mydemo server region"
  value       = var.region
}
output "mydemo_public_ip" {
  description = "Elastic IP address of the EC2 mydemo"
  value       = aws_eip.mydemo_eip.public_ip
}

output "compose_api_gateway_url" {
  description = "API Gateway URL when compose stack is deployed"
  value       = "http://${aws_eip.mydemo_eip.public_ip}"
}

output "compose_keycloak_url" {
  description = "Keycloak URL when compose stack is deployed"
  value       = "http://${aws_eip.mydemo_eip.public_ip}:8180"
}