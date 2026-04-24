$envFile = Join-Path $PSScriptRoot ".env"
$envVars = @{}

if (Test-Path $envFile) {
	Write-Host "=== Loading .env for dev build args ==="
	Get-Content $envFile | ForEach-Object {
		$line = $_.Trim()
		if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
			return
		}

		$parts = $line -split '=', 2
		if ($parts.Length -eq 2) {
			$envVars[$parts[0].Trim()] = $parts[1].Trim()
		}
	}
}
else {
	Write-Warning ".env not found; dev image will build with empty build args."
}

docker stop agilecontainer
docker rm -f agilecontainer
docker rmi agileimage
mvn clean verify
docker build -f DockerfileDev --platform linux/amd64 -t agileimage:0.1 `
	--build-arg "ui_username=$($envVars['ui_username'])" `
	--build-arg "ui_password=$($envVars['ui_password'])" `
	--build-arg "db_user=$($envVars['db_user'])" `
	--build-arg "dbpassword=$($envVars['dbpassword'])" `
	--build-arg "db_url=$($envVars['db_url'])" `
	--build-arg "TELEGRAM_BOT_TOKEN=$($envVars('TELEGRAM_BOT_TOKEN'))"	`
	--build-arg "TELEGRAM_BOT_NAME=$($envVars('TELEGRAM_BOT_NAME'))"	`
	.
docker run --name agilecontainer --volume "${PWD}/target:/tmp/target:rw" -p 8080:8080 -d agileimage:0.1