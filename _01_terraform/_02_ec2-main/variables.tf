
variable "region" {
  description = "AWS region"
  type = string
  default = "us-east-1"
}

variable "vpc-name" {
  description = "VPC Name for our mydemo server"
  type = string
  default = "mydemo-vpc"
}

variable "igw-name" {
  description = "Internet Gate Way Name for our mydemo server"
  type = string
  default = "mydemo-igw"
}

variable "subnet-name1" {
  description = "Public Subnet 1 Name"
  type = string
  default = "Public-Subnet-1"
}

variable "subnet-name2" {
  description = "Subnet Name for our mydemo server"
  type = string
  default = "Public-subnet2"
}

# Private subnet name variables
variable "private_subnet_name1" {
  description = "Private Subnet 1 Name"
  type = string
  default = "Private-subnet1"
}

variable "private_subnet_name2" {
  description = "Private Subnet 2 Name"
  type = string
  default = "Private-subnet2"
}

variable "rt-name" {
  description = "Route Table Name for our mydemo server"
  type = string
  default = "mydemo-rt"
}

variable "sg-name" {
  description = "Security Group for our mydemo server"
  type = string
  default = "mydemo-sg"
}


variable "iam-role" {
  description = "IAM Role for the mydemo Server"
  type = string
  default = "mydemo-iam-role1"
}

variable "ami_id" {
  description = "AMI ID for the EC2 instance"
  type        = string
  default     = "ami-098e39bafa7e7303d" // Replace with the latest AMI ID for your region
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.xlarge"
}

variable "key_name" {
  description = "EC2 keypair"
  type        = string
  default     = "My-Key-Linux-Amazon"
}

variable "instance_name" {
  description = "EC2 Instance name for the mydemo server"
  type        = string
  default     = "mydemo-server"
}

variable "allowed_cidr_blocks" {
  description = "Allowed IPv4 CIDR blocks for inbound access (use x.x.x.x/32 for single IP)"
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "allowed_ipv6_cidr_blocks" {
  description = "Allowed IPv6 CIDR blocks for inbound access"
  type        = list(string)
  default     = ["::/0"]
}

variable "auto_detect_public_ip" {
  description = "If true, auto-detect current public IP and allow only that /32"
  type        = bool
  default     = false
}

