variable "node_group_name" {
  description = "Name of the EKS node group"
  type        = string
  default     = "project-node-group"
}

variable "instance_name" {
  description = "Name of the EKS node group"
  type        = string
  default     = "mydemo-eks-node"
}