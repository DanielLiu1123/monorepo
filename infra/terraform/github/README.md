# GitHub Permission Management with Terraform

This directory contains a Terraform configuration for managing GitHub organization permissions, teams, and repositories.

## Features

- **Team Management**: Create and manage GitHub teams with hierarchical structure
- **Team Membership**: Assign users to teams with specific roles (member/maintainer)
- **Repository Creation**: Create and configure repositories
- **Team Repository Permissions**: Grant different permission levels to teams
- **Branch Protection**: Enforce branch protection rules on main branch

## Prerequisites

1. **Terraform**: Install Terraform >= 1.0
   ```bash
   # macOS
   brew install terraform

   # Or download from https://www.terraform.io/downloads
   ```

2. **GitHub Personal Access Token**: Create a token with the following scopes:
   - `repo` (Full control of private repositories)
   - `admin:org` (Full control of orgs and teams, read and write org projects)
   - `delete_repo` (Delete repositories)

   Generate token at: https://github.com/settings/tokens

3. **GitHub Organization**: You need to have admin access to a GitHub organization

## Project Structure

```
.
├── main.tf                    # Main Terraform configuration
├── variables.tf               # Variable definitions
├── outputs.tf                 # Output definitions
├── terraform.tfvars.example   # Example variables file
├── .gitignore                # Git ignore file for Terraform
└── README.md                  # This file
```

## Quick Start

### 1. Clone and Navigate

```bash
cd infrastructure/terraform/github
```

### 2. Configure Variables

Create a `terraform.tfvars` file from the example:

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edit `terraform.tfvars` with your values:

```hcl
github_organization = "your-organization-name"
repository_name     = "demo-repo"

engineering_members = {
  "user1" = {
    username = "alice"
    role     = "maintainer"
  }
  "user2" = {
    username = "bob"
    role     = "member"
  }
}

devops_members = {
  "devops1" = {
    username = "david"
    role     = "maintainer"
  }
}
```

### 3. Set GitHub Token

Set your GitHub token as an environment variable:

```bash
export TF_VAR_github_token="your-github-token-here"
```

Or create a `terraform.tfvars` entry (not recommended for security):
```hcl
github_token = "your-token"  # Don't commit this!
```

### 4. Initialize Terraform

```bash
terraform init
```

### 5. Plan Changes

Review what Terraform will create:

```bash
terraform plan
```

### 6. Apply Changes

Apply the configuration:

```bash
terraform apply
```

Type `yes` when prompted to confirm.

## What Gets Created

This configuration creates:

1. **Teams**:
   - Engineering (parent team)
   - DevOps
   - Frontend (child of Engineering)
   - Backend (child of Engineering)

2. **Repository**:
   - A private repository with issues, projects, and wiki enabled
   - Branch protection on `main` branch requiring 2 approvals

3. **Permissions**:
   - Engineering team: Push access
   - DevOps team: Admin access
   - Frontend team: Push access
   - Backend team: Push access

4. **Branch Protection**:
   - 2 required approving reviews
   - Dismiss stale reviews
   - Require code owner reviews
   - Required status checks: `ci/test`, `ci/build`

## Permission Levels

| Level | Description |
|-------|-------------|
| `pull` | Read-only access |
| `triage` | Read + manage issues and PRs |
| `push` | Read + write (can push) |
| `maintain` | Push + manage repo settings |
| `admin` | Full access |

## Team Roles

| Role | Description |
|------|-------------|
| `member` | Regular team member |
| `maintainer` | Can add/remove team members |

## Managing Resources

### Add a New Team Member

Edit `terraform.tfvars`:

```hcl
engineering_members = {
  "user1" = {
    username = "alice"
    role     = "maintainer"
  }
  "user2" = {
    username = "bob"
    role     = "member"
  }
  "user3" = {  # New member
    username = "frank"
    role     = "member"
  }
}
```

Then apply:
```bash
terraform apply
```

### Remove a Team Member

Remove the entry from `terraform.tfvars` and apply changes.

### Update Repository Settings

Modify the `github_repository` resource in `main.tf` and apply.

## Outputs

After applying, Terraform outputs:

- Repository URL
- Clone URLs (SSH and HTTPS)
- Team IDs and slugs

View outputs:
```bash
terraform output
```

## Security Best Practices

1. **Never commit tokens**: Keep `terraform.tfvars` in `.gitignore`
2. **Use environment variables**: Set `TF_VAR_github_token` instead of storing in files
3. **Rotate tokens regularly**: Update your GitHub token periodically
4. **Principle of least privilege**: Only grant necessary permissions
5. **Use remote state**: Store Terraform state securely (e.g., S3, Terraform Cloud)

## Cleanup

To destroy all resources:

```bash
terraform destroy
```

⚠️ **Warning**: This will delete the repository and all teams!

## Troubleshooting

### Authentication Errors

```
Error: GET https://api.github.com/user: 401 Bad credentials
```

**Solution**: Check your GitHub token is valid and has correct scopes.

### Permission Denied

```
Error: ... Resource not accessible by personal access token
```

**Solution**: Your token needs `admin:org` scope for organization resources.

### User Not Found

```
Error: ... Could not resolve to a User with the login of 'username'
```

**Solution**: Verify the username exists and is spelled correctly.

## Advanced Usage

### Managing Organization Members

Uncomment the `github_membership` resource in `main.tf` if you have organization owner permissions:

```hcl
resource "github_membership" "members" {
  for_each = var.organization_members

  username = each.value.username
  role     = each.value.role
}
```

### Using Remote State

Configure backend in `main.tf`:

```hcl
terraform {
  backend "s3" {
    bucket = "my-terraform-state"
    key    = "github/terraform.tfstate"
    region = "us-east-1"
  }
}
```

## References

- [Terraform GitHub Provider Documentation](https://registry.terraform.io/providers/integrations/github/latest/docs)
- [GitHub API Documentation](https://docs.github.com/en/rest)
- [Terraform Best Practices](https://www.terraform.io/docs/cloud/guides/recommended-practices/index.html)

## License

This configuration is provided as-is for demonstration purposes.
