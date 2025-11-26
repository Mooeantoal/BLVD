# -*- coding: utf-8 -*-
import sys

# 设置输出编码
if sys.platform == 'win32':
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')

def modify_compose_fragment():
    file_path = r"bilimiao-compose\src\main\java\cn\a10miaomiao\bilimiao\compose\ComposeFragment.kt"
    
    print("正在读取文件...")
    with open(file_path, 'r', encoding='utf-8', newline='') as f:
        content = f.read()
    
    original_content = content
    
    # 尝试替换 BlankPage 为 BlankPage()
    # 注意：这里可能会匹配到 import 语句，所以要小心
    # import cn.a10miaomiao.bilimiao.compose.pages.BlankPage 是不需要加 () 的
    
    # 替换 MyNavHost 中的 BlankPage
    content = content.replace(
        'MyNavHost(composeNav, BlankPage)',
        'MyNavHost(composeNav, BlankPage())'
    )
    
    # 替换 goBackHome 中的 BlankPage
    content = content.replace(
        'composeNav.popBackStack(BlankPage, false)',
        'composeNav.popBackStack(BlankPage(), false)'
    )
    
    # 如果还没有替换（可能是原始的 HomePage），则尝试替换 HomePage
    content = content.replace(
        'MyNavHost(composeNav, HomePage)',
        'MyNavHost(composeNav, BlankPage())'
    )
    content = content.replace(
        'composeNav.popBackStack(HomePage, false)',
        'composeNav.popBackStack(BlankPage(), false)'
    )
    
    # 替换导入（如果还是 HomePage）
    content = content.replace(
        'import cn.a10miaomiao.bilimiao.compose.pages.home.HomePage',
        'import cn.a10miaomiao.bilimiao.compose.pages.BlankPage'
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
        success = modify_compose_fragment()
        sys.exit(0 if success else 1)
    except Exception as e:
        print(f"错误: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
