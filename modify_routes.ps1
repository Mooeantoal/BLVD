# bilimiao 精简版修改脚本
# 此脚本将注释掉不需要的路由

$file = "bilimiao-compose\src\main\java\cn\a10miaomiao\bilimiao\compose\BilimiaoPageRoute.kt"

Write-Host "正在修改 BilimiaoPageRoute.kt..." -ForegroundColor Green

# 读取文件内容
$content = Get-Content $file -Raw

# 1. 注释掉时光机路由
$content = $content -replace '        // time\r\n        composable<TimeSettingPage>\(\)\r\n        composable<TimeRegionDetailPage>\(\)', '        // time - 精简版：移除时光机功能\r\n        // composable<TimeSettingPage>()\r\n        // composable<TimeRegionDetailPage>()'

# 2. 注释掉 MyBangumiPage 路由
$content = $content -replace '        // mine\r\n        composable<MyBangumiPage>\([\s\S]*?basePath = "bilimiao://mine/bangumi"[\s\S]*?\)\r\n        \)', '        // mine - 精简版：移除收藏、追番、历史、稍后看功能\r\n        // composable<MyBangumiPage>(...)'

# 3. 注释掉 MyFollowPage 路由
$content = $content -replace '        composable<MyFollowPage>\([\s\S]*?basePath = "bilimiao://mine/follow"[\s\S]*?\)\r\n        \)', '        // composable<MyFollowPage>(...)'

# 4. 注释掉 HistoryPage 路由
$content = $content -replace '        composable<HistoryPage>\([\s\S]*?basePath = "bilimiao://mine/history"[\s\S]*?\)\r\n        \)', '        // composable<HistoryPage>(...)'

# 5. 注释掉 WatchLaterPage 路由
$content = $content -replace '        composable<WatchLaterPage>\([\s\S]*?basePath = "bilimiao://mine/watchlater"[\s\S]*?\)\r\n        \)', '        // composable<WatchLaterPage>(...)'

# 保存修改后的内容
$content | Set-Content $file -NoNewline

Write-Host "修改完成！" -ForegroundColor Green
Write-Host "已注释掉以下路由：" -ForegroundColor Yellow
Write-Host "  - HomePage (主页)" -ForegroundColor Yellow
Write-Host "  - TimeSettingPage (时光机设置)" -ForegroundColor Yellow
Write-Host "  - TimeRegionDetailPage (时光机详情)" -ForegroundColor Yellow
Write-Host "  - MyBangumiPage (追番)" -ForegroundColor Yellow
Write-Host "  - MyFollowPage (关注)" -ForegroundColor Yellow
Write-Host "  - HistoryPage (历史)" -ForegroundColor Yellow
Write-Host "  - WatchLaterPage (稍后看)" -ForegroundColor Yellow
