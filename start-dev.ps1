$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\services\tradehub-api'; if (-not (Test-Path target\tradehub-api-1.0.0.jar)) { mvn -DskipTests package }; java -jar target\tradehub-api-1.0.0.jar"
Start-Sleep -Seconds 2
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\apps\admin'; if (-not (Test-Path node_modules)) { npm install --registry=https://registry.npmmirror.com }; npm run dev"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\apps\storefront'; if (-not (Test-Path node_modules\nuxt)) { npm install --registry=https://registry.npmmirror.com }; npm run dev"
Write-Host "Started API :8080, admin :5173, storefront :3000/en"
