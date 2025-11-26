# -*- coding: utf-8 -*-
"""
bilimiao 精简版修改脚本
移除主页、时光机、收藏、追番、历史、稍后看等功能
"""

import re
import sys

# 设置输出编码
if sys.platform == 'win32':
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')

def modify_route_file():
    file_path = r"bilimiao-compose\src\main\java\cn\a10miaomiao\bilimiao\compose\BilimiaoPageRoute.kt"
    
    print("正在读取文件...")
    with open(file_path, 'r', encoding='utf-8', newline='') as f:
        content = f.read()
    
    original_content = content
    
    # 1. 注释掉 HomePage
    content = content.replace(
        '        // home\r\n        composable<HomePage>()',
        '        // home - 精简版：移除主页（推荐、时光机等功能）\r\n        // composable<HomePage>()'
    )
    
    # 2. 注释掉时光机
    content = content.replace(
        '        // time\r\n        composable<TimeSettingPage>()\r\n        composable<TimeRegionDetailPage>()',
        '        // time - 精简版：移除时光机功能\r\n        // composable<TimeSettingPage>()\r\n        // composable<TimeRegionDetailPage>()'
    )
    
    # 3. 注释掉"我的"相关功能
    # 找到并替换 MyBangumiPage
    content = re.sub(
        r'        composable<MyBangumiPage>\(\r\n            deepLinks = listOf\(\r\n                navDeepLink<MyBangumiPage>\(\r\n                    basePath = "bilimiao://mine/bangumi"\r\n                \)\r\n            \)\r\n        \)',
        '        // composable<MyBangumiPage>(...)',
        content
    )
    
    # 找到并替换 MyFollowPage
    content = re.sub(
        r'        composable<MyFollowPage>\(\r\n            deepLinks = listOf\(\r\n                navDeepLink<MyFollowPage>\(\r\n                    basePath = "bilimiao://mine/follow"\r\n                \)\r\n            \)\r\n        \)',
        '        // composable<MyFollowPage>(...)',
        content
    )
    
    # 找到并替换 HistoryPage
    content = re.sub(
        r'        composable<HistoryPage>\(\r\n            deepLinks = listOf\(\r\n                navDeepLink<HistoryPage>\(\r\n                    basePath = "bilimiao://mine/history"\r\n                \)\r\n            \)\r\n        \)',
        '        // composable<HistoryPage>(...)',
        content
    )
    
    # 找到并替换 WatchLaterPage
    content = re.sub(
        r'        composable<WatchLaterPage>\(\r\n            deepLinks = listOf\(\r\n                navDeepLink<WatchLaterPage>\(\r\n                    basePath = "bilimiao://mine/watchlater"\r\n                \)\r\n            \)\r\n        \)',
        '        // composable<WatchLaterPage>(...)',
        content
    )
    
    if content == original_content:
        print("警告：没有找到需要修改的内容！")
        return False
    
    print("正在保存文件...")
    with open(file_path, 'w', encoding='utf-8', newline='') as f:
        f.write(content)
    
    print("修改完成！")
    print("\n已注释掉以下路由：")
    print("  - HomePage (主页)")
    print("  - TimeSettingPage (时光机设置)")
    print("  - TimeRegionDetailPage (时光机详情)")
    print("  - MyBangumiPage (追番)")
    print("  - MyFollowPage (关注)")
    print("  - HistoryPage (历史)")
    print("  - WatchLaterPage (稍后看)")
    return True

if __name__ == "__main__":
    try:
        success = modify_route_file()
        sys.exit(0 if success else 1)
    except Exception as e:
        print(f"错误: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
