terraform {
  required_version = ">= 1.0"

  required_providers {
    github = {
      source  = "integrations/github"
      version = "~> 6.0"
    }
  }
}

# Configure the GitHub Provider
provider "github" {
  token = var.github_token
  owner = var.github_organization
}

# Create Teams
resource "github_team" "engineering" {
  name        = "engineering"
  description = "Engineering team"
  privacy     = "closed"
}

resource "github_team" "devops" {
  name        = "devops"
  description = "DevOps team"
  privacy     = "closed"
}

resource "github_team" "frontend" {
  name        = "frontend"
  description = "Frontend team"
  privacy     = "closed"
  parent_team_id = github_team.engineering.id
}

resource "github_team" "backend" {
  name        = "backend"
  description = "Backend team"
  privacy     = "closed"
  parent_team_id = github_team.engineering.id
}

# Add Team Members
resource "github_team_membership" "engineering_members" {
  for_each = var.engineering_members

  team_id  = github_team.engineering.id
  username = each.value.username
  role     = each.value.role
}

resource "github_team_membership" "devops_members" {
  for_each = var.devops_members

  team_id  = github_team.devops.id
  username = each.value.username
  role     = each.value.role
}

# Create a Repository
resource "github_repository" "example_repo" {
  name        = var.repository_name
  description = "Example repository for demonstrating permissions"
  visibility  = "private"

  has_issues   = true
  has_projects = true
  has_wiki     = true

  # Enable branch protection
  auto_init = true
}

# Configure Team Repository Permissions
resource "github_team_repository" "engineering_repo_access" {
  team_id    = github_team.engineering.id
  repository = github_repository.example_repo.name
  permission = "push"
}

resource "github_team_repository" "devops_repo_access" {
  team_id    = github_team.devops.id
  repository = github_repository.example_repo.name
  permission = "admin"
}

resource "github_team_repository" "frontend_repo_access" {
  team_id    = github_team.frontend.id
  repository = github_repository.example_repo.name
  permission = "push"
}

resource "github_team_repository" "backend_repo_access" {
  team_id    = github_team.backend.id
  repository = github_repository.example_repo.name
  permission = "push"
}

# Branch Protection Rule
resource "github_branch_protection" "main_protection" {
  repository_id = github_repository.example_repo.node_id
  pattern       = "main"

  required_pull_request_reviews {
    required_approving_review_count = 2
    dismiss_stale_reviews           = true
    require_code_owner_reviews      = true
  }

  required_status_checks {
    strict   = true
    contexts = ["ci/test", "ci/build"]
  }

  enforce_admins = false

  # Only allow admins to push directly
  push_restrictions = []
}

# Organization Members (Optional - requires organization owner token)
# Uncomment if you have organization owner permissions
# resource "github_membership" "members" {
#   for_each = var.organization_members
#
#   username = each.value.username
#   role     = each.value.role
# }
