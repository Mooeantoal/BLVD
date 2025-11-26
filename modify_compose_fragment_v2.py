# -*- coding: utf-8 -*-
import sys

# 设置输出编码
if sys.platform == 'win32':
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')

def modify_compose_fragment_to_start_page():
    file_path = r"bilimiao-compose\src\main\java\cn\a10miaomiao\bilimiao\compose\ComposeFragment.kt"
    
    print("正在读取文件...")
    with open(file_path, 'r', encoding='utf-8', newline='') as f:
        content = f.read()
    
    original_content = content
    
    # 1. 替换导入
    content = content.replace(
        'import cn.a10miaomiao.bilimiao.compose.pages.BlankPage',
        'import cn.a10miaomiao.bilimiao.compose.pages.StartPage'
    )
    # 如果之前没有导入 BlankPage（可能被恢复了），则替换 HomePage
    content = content.replace(
        'import cn.a10miaomiao.bilimiao.compose.pages.home.HomePage',
        'import cn.a10miaomiao.bilimiao.compose.pages.StartPage'
    )
    
    # 2. 替换 MyNavHost 中的起始页
    content = content.replace(
        'MyNavHost(composeNav, BlankPage())',
        'MyNavHost(composeNav, StartPage())'
    )
    content = content.replace(
        'MyNavHost(composeNav, BlankPage)',
        'MyNavHost(composeNav, StartPage())'
    )
    content = content.replace(
        'MyNavHost(composeNav, HomePage)',
        'MyNavHost(composeNav, StartPage())'
    )
    
    # 3. 替换 goBackHome 中的返回页
    content = content.replace(
        'composeNav.popBackStack(BlankPage(), false)',
        'composeNav.popBackStack(StartPage(), false)'
    )
    content = content.replace(
        'composeNav.popBackStack(BlankPage, false)',
        'composeNav.popBackStack(StartPage(), false)'
    )
    content = content.replace(
        'composeNav.popBackStack(HomePage, false)',
        'composeNav.popBackStack(StartPage(), false)'
    )
    
    # 4. 移除 MyStartView 调用
    # MyStartView(startViewWrapper = startViewWrapper)
    content = content.replace(
        '                        MyStartView(startViewWrapper = startViewWrapper)',
        '                        // MyStartView(startViewWrapper = startViewWrapper) // 已移至 StartPage'
    )
    
    if content == original_content:
        print("警告：没有找到需要修改的内容！")
        return False
    
    print("正在保存文件...")
    with open(file_path, 'w', encoding='utf-8', newline='') as f:
        f.write(content)
    
    print("ComposeFragment.kt 修改完成！")
    return True

if __name__ == "__main__":
    try:
        success = modify_compose_fragment_to_start_page()
        sys.exit(0 if success else 1)
    except Exception as e:
        print(f"错误: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
