#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
PaddleOCR 识别服务
提供 HTTP API 接口，接收图片并返回识别的文本
"""

from flask import Flask, request, jsonify
from paddleocr import PaddleOCR
import base64
import io
from PIL import Image
import numpy as np
import logging

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# 初始化 PaddleOCR（只初始化一次，提高性能）
# use_angle_cls=True: 使用方向分类器
# lang='ch': 中文识别
logger.info("正在初始化 PaddleOCR...")
ocr = PaddleOCR(use_angle_cls=True, lang='ch', show_log=False)
logger.info("✅ PaddleOCR 初始化完成")

@app.route('/health', methods=['GET'])
def health():
    """健康检查接口"""
    return jsonify({
        "status": "healthy",
        "service": "PaddleOCR",
        "version": "1.0.0"
    })

@app.route('/ocr', methods=['POST'])
def recognize():
    """
    OCR 识别接口
    
    请求格式:
    {
        "image_base64": "base64编码的图片数据"
    }
    
    返回格式:
    {
        "success": true,
        "text": "识别的完整文本",
        "lines": ["行1", "行2", ...],
        "details": [...原始识别结果...]
    }
    """
    try:
        data = request.json
        
        if not data or 'image_base64' not in data:
            return jsonify({
                "success": False,
                "error": "缺少 image_base64 参数"
            }), 400
        
        # 从 base64 获取图片
        image_data = data['image_base64']
        
        # 处理可能带有 data:image 前缀的 base64
        if ',' in image_data:
            image_data = image_data.split(',', 1)[1]
        
        # 解码图片
        img_bytes = base64.b64decode(image_data)
        img = Image.open(io.BytesIO(img_bytes))
        
        # 转换为 numpy array
        img_array = np.array(img)
        
        logger.info(f"开始识别图片，尺寸: {img.size}")
        
        # PaddleOCR 识别
        result = ocr.ocr(img_array, cls=True)
        
        # 提取所有文本
        lines = []
        if result and result[0]:
            for line in result[0]:
                if line and len(line) >= 2:
                    text = line[1][0]  # line[1][0] 是识别的文字
                    confidence = line[1][1]  # line[1][1] 是置信度
                    lines.append({
                        "text": text,
                        "confidence": float(confidence)
                    })
        
        # 合并为完整文本
        full_text = '\n'.join([line['text'] for line in lines])
        
        logger.info(f"识别完成，共识别 {len(lines)} 行文本")
        
        return jsonify({
            "success": True,
            "text": full_text,
            "lines": lines,
            "line_count": len(lines)
        })
        
    except base64.binascii.Error as e:
        logger.error(f"Base64 解码错误: {str(e)}")
        return jsonify({
            "success": False,
            "error": f"图片数据格式错误: {str(e)}"
        }), 400
        
    except Exception as e:
        logger.error(f"OCR 识别失败: {str(e)}", exc_info=True)
        return jsonify({
            "success": False,
            "error": f"OCR 识别失败: {str(e)}"
        }), 500

if __name__ == '__main__':
    logger.info("🚀 PaddleOCR 服务启动中...")
    logger.info("📍 监听地址: http://0.0.0.0:5000")
    app.run(host='0.0.0.0', port=5000, debug=False, threaded=True)
