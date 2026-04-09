output "repository_url" {
  description = "URL of the created repository"
  value       = github_repository.example_repo.html_url
}

output "repository_ssh_clone_url" {
  description = "SSH clone URL"
  value       = github_repository.example_repo.ssh_clone_url
}

output "repository_http_clone_url" {
  description = "HTTP clone URL"
  value       = github_repository.example_repo.http_clone_url
}

output "team_ids" {
  description = "IDs of created teams"
  value = {
    engineering = github_team.engineering.id
    devops      = github_team.devops.id
    frontend    = github_team.frontend.id
    backend     = github_team.backend.id
  }
}

output "team_slugs" {
  description = "Slugs of created teams"
  value = {
    engineering = github_team.engineering.slug
    devops      = github_team.devops.slug
    frontend    = github_team.frontend.slug
    backend     = github_team.backend.slug
  }
}
