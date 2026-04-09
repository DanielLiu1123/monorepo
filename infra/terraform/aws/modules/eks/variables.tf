variable "cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
}

variable "kubernetes_version" {
  description = "Kubernetes version for the EKS control plane"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID for the cluster"
  type        = string
}

variable "subnet_ids" {
  description = "Subnet IDs for worker nodes"
  type        = list(string)
}

variable "control_plane_subnet_ids" {
  description = "Subnet IDs for control plane"
  type        = list(string)
}

variable "cluster_endpoint_public_access" {
  description = "Whether the cluster endpoint is publicly accessible"
  type        = bool
}

variable "cluster_endpoint_private_access" {
  description = "Whether the cluster endpoint is privately accessible"
  type        = bool
}

variable "enable_irsa" {
  description = "Enable IAM roles for service accounts"
  type        = bool
}

variable "enable_cluster_creator_admin_permissions" {
  description = "Grant cluster creator admin permissions"
  type        = bool
}

variable "cluster_addons" {
  description = "EKS cluster addons configuration"
  type        = any
  default     = {}
}

variable "node_pools" {
  description = "Auto Mode node pools"
  type        = list(string)
  default     = ["general-purpose"]
}

variable "tags" {
  description = "Common tags applied to resources"
  type        = map(string)
  default     = {}
}
