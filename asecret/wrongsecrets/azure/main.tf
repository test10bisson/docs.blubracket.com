terraform {
  required_version = ">= 0.14.0"

  required_providers {
    random  = "~> 3.0"
    azurerm = "~> 3.0"
    http    = "~> 2.1"
  }

  # For shared state:
  # Set the resource group in the backend configuration below, then uncomment and apply!
  # Note that you probably already create a resource group. Don't forget to set that correctly in this file.
  # backend "azurerm" {
  #   resource_group_name  = "owasp-wrongsecrets"
  #   storage_account_name = "YOUR_ACCOUNT_NAME_HERE"
  #   container_name       = "tfstate"
  #   key                  = "terraform.tfstate"
  # }
}

provider "http" {}

data "http" "ip" {
  url = "http://ipecho.net/plain"
}

provider "azurerm" {
  features {}

  skip_provider_registration = true
}

data "azurerm_client_config" "current" {}

# If you're using an existing resource group, modify this part.
# Note that you'll need to find/replace references to "arurerm_resource_group.default" to "data.azurerm_resource_group.default"
# data "azurerm_resource_group" "default" {
#   name = "owasp-wrongsecrets"
# }

# If you're using an existing resource group, comment this.
resource "azurerm_resource_group" "default" {
  name     = "owasp-wrongsecrets"
  location = var.region
}


resource "azurerm_kubernetes_cluster" "cluster" {
  name                = var.cluster_name
  location            = azurerm_resource_group.default.location
  resource_group_name = azurerm_resource_group.default.name
  dns_prefix          = "wrongsecrets"

  kubernetes_version = var.cluster_version

  api_server_authorized_ip_ranges = ["${data.http.ip.body}/32"]

  network_profile {
    network_plugin = "azure"
  }

  default_node_pool {
    name       = "default"
    node_count = 1
    vm_size    = "Standard_A2_v2"
  }

  identity {
    type = "SystemAssigned"
  }

  role_based_access_control_enabled = true
}
