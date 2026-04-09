module "this" {
  source  = "terraform-aws-modules/eks/aws"
  version = "21.15.1"

  name               = var.cluster_name
  kubernetes_version = var.kubernetes_version

  # vpc_id                   = var.vpc_id
  # subnet_ids               = var.subnet_ids
  # control_plane_subnet_ids = var.control_plane_subnet_ids

  endpoint_public_access  = var.cluster_endpoint_public_access
  endpoint_private_access = var.cluster_endpoint_private_access

  enable_irsa                              = var.enable_irsa
  enable_cluster_creator_admin_permissions = var.enable_cluster_creator_admin_permissions

  create_auto_mode_iam_resources = true
  compute_config = {
    enabled    = true
    node_pools = var.node_pools
  }

  tags = var.tags
}
