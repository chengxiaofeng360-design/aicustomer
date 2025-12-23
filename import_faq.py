#!/usr/bin/env python3
import json
import requests
import time

# 读取FAQ数据
with open('faq_data.json', 'r', encoding='utf-8') as f:
    faq_list = json.load(f)

# API地址
api_url = 'http://localhost:8085/api/faq'

# 逐条导入
success_count = 0
fail_count = 0

print(f"开始导入 {len(faq_list)} 条FAQ数据...")

for i, faq in enumerate(faq_list, 1):
    try:
        response = requests.post(api_url, json=faq, headers={'Content-Type': 'application/json'})
        if response.status_code == 200:
            result = response.json()
            if result.get('code') == 200:
                success_count += 1
                print(f"[{i}/{len(faq_list)}] ✓ {faq['question'][:30]}...")
            else:
                fail_count += 1
                print(f"[{i}/{len(faq_list)}] ✗ {faq['question'][:30]}... - {result.get('message')}")
        else:
            fail_count += 1
            print(f"[{i}/{len(faq_list)}] ✗ {faq['question'][:30]}... - HTTP {response.status_code}")
        
        # 避免请求过快
        time.sleep(0.1)
    except Exception as e:
        fail_count += 1
        print(f"[{i}/{len(faq_list)}] ✗ {faq['question'][:30]}... - {str(e)}")

print(f"\n导入完成！")
print(f"成功: {success_count} 条")
print(f"失败: {fail_count} 条")
