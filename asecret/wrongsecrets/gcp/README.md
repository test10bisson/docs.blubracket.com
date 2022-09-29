# Setup your secrets in GCP

In this setup we integrate the secrets exercise with GCP GKE and let pods consume secrets from the GCP Secret manager. If you want to know more about integrating secrets with GKE, check [this link](https://github.com/GoogleCloudPlatform/secrets-store-csi-driver-provider-gcp).
Please make sure that the account in which you run this exercise has either Cloud Audit Logs enabled, or is not linked to your current organization and/or DTAP environment.
## Pre-requisites

Have the following tools installed:

- gcloud CLI - [Installation](https://cloud.google.com/sdk/docs/install)
- Tfenv (Optional) - [Installation](https://github.com/tfutils/tfenv)
- Terraform CLI - [Installation](https://learn.hashicorp.com/tutorials/terraform/install-cli)
- Wget - [Installation](https://www.jcchouinard.com/wget/)
- Helm [Installation](https://helm.sh/docs/intro/install/)
- Kubectl [Installation](https://kubernetes.io/docs/tasks/tools/)
- jq [Installation](https://stedolan.github.io/jq/download/)

Make sure you have an active account at GCP for which you have configured the credentials on the system where you will execute the steps below.

## Installation

**Note**: Applying the Terraform means you are creating cloud infrastructure which actually costs you money. The authors are not responsible for any cost coming from following the instructions below. If you have a brand new GCP account, you could use the $300 in credits to set up the infrastructure for free.

**Note-II**: We create resources in `europe-west4` by default. You can set the region by editing `terraform.tfvars`.

**Note-III**: The cluster you create has its access bound to the public IP of the creator. In other words: the cluster you create with this code has its access bound to your public IP-address if you apply it locally.

1. check whether you have the right project by doing `gcloud config list`. Otherwise configure it by doing `gcloud init`.
2. Run `gcloud auth application-default login` to be able to use your account credentials for terraform.
3. Enable the required gcloud services using `gcloud services enable compute.googleapis.com container.googleapis.com`
4. Run `terraform init` (if required, use tfenv to select TF 0.14.0 or higher )
5. Run `terraform plan`
6. Run `terraform apply`. Note: the apply will take 10 to 20 minutes depending on the speed of the GCP backplane.
7. When creation is done, run `gcloud container clusters get-credentials wrongsecrets-exercise-cluster --region YOUR_REGION`
8. Run `./k8s-vault-gcp-start.sh`

### GKE ingres for shared deployment

By default the deployment uses a nodePort tunneled to localhost. For a larger audiance deployment the wrongsecrets app can deployed with a GKE ingress, run `k8s-vault-gcp-ingress-start.sh`
Please note that the GKE ingress can take a few minues to deploy and is publicly available. A connection URL will be returned once the ingress is available. Note that, after the connection URL is returned, a first lookup might still take a minute, after which it is much faster.

Your GKE cluster should be visible in [EU-West4](https://console.cloud.google.com/kubernetes?referrer=search&project=wrongsecrets) by default. Want a different region? You can modify `terraform.tfvars` or input it directly using the `region` variable in plan/apply.

Are you done playing? Please run `terraform destroy` twice to clean up.

### Test it

Run `./k8s-vault-gcp-start.sh` and connect to [http://localhost:8080](http://localhost:8080) when it's ready to accept connections (you'll read the line `Forwarding from 127.0.0.1:8080 -> 8080` in your console). Now challenge 9 and 10 should be available as well.

### Resume it
When you stopped the `k8s-vault-gcp-start.sh` script and want to resume the port forward run: `k8s-vault-gcp-resume.sh`. This is because if you run the start script again it will replace the secret in the vault and not update the secret-challenge application with the new secret.

### Clean it up

When you're done:

1. Kill the port forward.
2. Run `terraform destroy` to clean up the infrastructure.
3. Run `unset KUBECONFIG` to unset the KUBECONFIG env var.
4. Run `rm ~/.kube/wrongsecrets` to remove the kubeconfig file.
5. Run `rm terraform.ts*` to remove local state files.

### A few things to consider

1. Does your worker node now have access as well?
2. Can you easily obtain the GCP IAM role of the Node?
3. Can you get the secrets in the SSM Parameter Store and Secret Manager easily? Which paths do you see?
4. You should see at the configuration details of the cluster that `databaseEncryption` is `DECRYPTED` (`gcloud container clusters describe wrongsecrets-exercise-cluster --region europe-west4`). What does that mean?
