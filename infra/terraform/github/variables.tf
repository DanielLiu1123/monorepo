variable "github_token" {
  description = "GitHub Personal Access Token with appropriate permissions"
  type        = string
  sensitive   = true
}

variable "github_organization" {
  description = "GitHub organization name"
  type        = string
}

variable "repository_name" {
  description = "Name of the repository to create"
  type        = string
  default     = "example-repo"
}

variable "engineering_members" {
  description = "Engineering team members with their roles"
  type = map(object({
    username = string
    role     = string # "member" or "maintainer"
  }))
  default = {}
}

variable "devops_members" {
  description = "DevOps team members with their roles"
  type = map(object({
    username = string
    role     = string # "member" or "maintainer"
  }))
  default = {}
}

variable "organization_members" {
  description = "Organization members and their roles"
  type = map(object({
    username = string
    role     = string # "member" or "admin"
  }))
  default = {}
}
