output "cluster_id" {
  description = "EKS cluster ID"
  value       = module.this.cluster_id
}

output "cluster_arn" {
  description = "EKS cluster ARN"
  value       = module.this.cluster_arn
}

output "cluster_endpoint" {
  description = "EKS cluster API endpoint"
  value       = module.this.cluster_endpoint
}

output "cluster_security_group_id" {
  description = "Cluster security group ID"
  value       = module.this.cluster_security_group_id
}

output "oidc_provider_arn" {
  description = "OIDC provider ARN"
  value       = module.this.oidc_provider_arn
}
