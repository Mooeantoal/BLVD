# -*- coding: utf-8 -*-
import sys
import os

# 设置输出编码
if sys.platform == 'win32':
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')

def modify_file(file_path, replacements):
    print(f"正在处理 {file_path}...")
    if not os.path.exists(file_path):
        print(f"错误：文件不存在 {file_path}")
        return False
        
    with open(file_path, 'r', encoding='utf-8', newline='') as f:
        content = f.read()
    
    original_content = content
    
    for old, new in replacements:
        content = content.replace(old, new)
        
    if content == original_content:
        print(f"警告：{file_path} 中没有找到需要修改的内容")
        return False
        
    with open(file_path, 'w', encoding='utf-8', newline='') as f:
        f.write(content)
        
    print(f"✅ {file_path} 修改完成")
    return True

def main():
    # 1. 修改 DynamicViewModel.kt
    dynamic_vm_path = r"bilimiao-compose\src\main\java\cn\a10miaomiao\bilimiao\compose\pages\dynamic\DynamicViewModel.kt"
    dynamic_vm_replacements = [
        ('nav.navigate(BlankPage(), navOptions', 'nav.navigate(StartPage(), navOptions'),
        ('nav.navigate(BlankPage, navOptions', 'nav.navigate(StartPage(), navOptions'),
        ('nav.navigate(HomePage, navOptions', 'nav.navigate(StartPage(), navOptions'),
        ('import cn.a10miaomiao.bilimiao.compose.pages.BlankPage', 'import cn.a10miaomiao.bilimiao.compose.pages.StartPage'),
        ('import cn.a10miaomiao.bilimiao.compose.pages.home.HomePage', 'import cn.a10miaomiao.bilimiao.compose.pages.StartPage')
    ]
    modify_file(dynamic_vm_path, dynamic_vm_replacements)
    
if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"错误: {e}")
        import traceback
        traceback.print_exc()
