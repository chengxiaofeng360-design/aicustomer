"""
向量化服务 - 使用sentence-transformers生成文本向量
完全免费，本地运行
"""

from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
import numpy as np

app = Flask(__name__)

# 加载多语言模型（支持中文）- 首次运行会自动下载
print("正在加载多语言向量模型...")
model = SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2')
print("模型加载完成！")

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ok", "model": "paraphrase-multilingual-MiniLM-L12-v2"})

@app.route('/embed', methods=['POST'])
def embed():
    """
    将文本转换为向量
    请求体: {"text": "要转换的文本"} 或 {"texts": ["文本1", "文本2"]}
    返回: {"vector": [...]} 或 {"vectors": [[...], [...]]}
    """
    data = request.json
    
    if 'text' in data:
        # 单个文本
        text = data['text']
        vector = model.encode(text).tolist()
        return jsonify({"vector": vector, "dimension": len(vector)})
    
    elif 'texts' in data:
        # 批量文本
        texts = data['texts']
        vectors = model.encode(texts).tolist()
        return jsonify({"vectors": vectors, "dimension": len(vectors[0]) if vectors else 0})
    
    else:
        return jsonify({"error": "请提供 'text' 或 'texts' 参数"}), 400

@app.route('/similarity', methods=['POST'])
def similarity():
    """
    计算两个文本的相似度
    请求体: {"text1": "...", "text2": "..."}
    返回: {"similarity": 0.85}
    """
    data = request.json
    text1 = data.get('text1', '')
    text2 = data.get('text2', '')
    
    vectors = model.encode([text1, text2])
    # 计算余弦相似度
    similarity = np.dot(vectors[0], vectors[1]) / (np.linalg.norm(vectors[0]) * np.linalg.norm(vectors[1]))
    
    return jsonify({"similarity": float(similarity)})

if __name__ == '__main__':
    print("启动向量化服务，端口: 5001")
    app.run(host='0.0.0.0', port=5001, debug=False)
