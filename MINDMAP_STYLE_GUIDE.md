# 导图页面风格样式指南

## 概述
这是一个现代化的功能导图页面设计，采用渐变背景、卡片式布局和响应式设计，适合用于系统功能介绍、产品展示等场景。

## 核心设计特点
- **渐变背景**: 使用紫蓝色渐变营造科技感
- **卡片式布局**: 每个功能模块独立卡片展示
- **响应式设计**: 适配不同屏幕尺寸
- **交互动效**: 悬停效果和过渡动画
- **图标系统**: 使用Bootstrap Icons图标库
- **现代字体**: 使用Inter字体提升可读性

## 完整CSS样式代码

```css
/* 重置样式 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* 基础样式 */
body {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    padding: 20px;
}

/* 主容器 */
.container {
    max-width: 1400px;
    margin: 0 auto;
    background: white;
    border-radius: 20px;
    box-shadow: 0 20px 40px rgba(0,0,0,0.1);
    overflow: hidden;
}

/* 页面头部 */
.header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 40px;
    text-align: center;
}

.header h1 {
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 10px;
}

.header p {
    font-size: 1.2rem;
    opacity: 0.9;
}

/* 导图容器 */
.mindmap-container {
    padding: 40px;
    background: #f8fafc;
}

.mindmap {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 30px;
}

/* 根节点（中心标题） */
.root-node {
    background: none;
    color: #2d3748;
    padding: 0;
    border-radius: 0;
    font-size: 1.8rem;
    font-weight: 700;
    box-shadow: none;
    position: relative;
    text-align: center;
}

/* 主分支网格布局 */
.main-branches {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 30px;
    width: 100%;
}

/* 分支卡片 */
.branch {
    background: white;
    border-radius: 15px;
    padding: 25px;
    box-shadow: 0 8px 25px rgba(0,0,0,0.1);
    transition: all 0.3s ease;
    position: relative;
}

.branch:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 35px rgba(0,0,0,0.15);
}

/* 分支头部 */
.branch-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid #f1f3f4;
}

/* 分支图标 */
.branch-icon {
    width: 50px;
    height: 50px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    color: white;
}

/* 分支标题 */
.branch-title {
    font-size: 1.3rem;
    font-weight: 600;
    color: #2c3e50;
}

/* 子分支容器 */
.sub-branches {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

/* 子分支项 */
.sub-branch {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 15px;
    background: #f8fafc;
    border-radius: 8px;
    transition: all 0.2s ease;
}

.sub-branch:hover {
    background: #e3f2fd;
    transform: translateX(5px);
}

/* 子分支图标 */
.sub-branch-icon {
    width: 30px;
    height: 30px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.9rem;
    color: white;
}

/* 子分支标题 */
.sub-branch-title {
    font-size: 0.95rem;
    font-weight: 500;
    color: #4a5568;
}

/* 功能标签 */
.features {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 8px;
}

.feature-tag {
    background: #e3f2fd;
    color: #1976d2;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 0.8rem;
    font-weight: 500;
}

/* 分支描述 */
.branch-description {
    margin-top: 15px;
    padding: 12px 15px;
    background: #f8fafc;
    border-radius: 8px;
    border-left: 4px solid #667eea;
    font-size: 0.9rem;
    color: #4a5568;
    line-height: 1.5;
}

.branch-description .description-title {
    font-weight: 600;
    color: #2d3748;
    margin-bottom: 5px;
}

/* 流程图样式 */
.flow-section {
    background: white;
    margin-top: 40px;
    padding: 40px;
    border-radius: 20px;
    box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.flow-title {
    text-align: center;
    font-size: 1.8rem;
    font-weight: 700;
    color: #2d3748;
    margin-bottom: 30px;
}

.flow-container {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;
    margin: 30px 0;
}

/* 流程步骤 */
.flow-step {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 12px 20px;
    border-radius: 12px;
    text-align: center;
    font-weight: 600;
    font-size: 0.9rem;
    min-width: 120px;
    box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
    transition: all 0.3s ease;
}

.flow-step:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

/* 不同步骤的颜色 */
.flow-step.step-1 { background: linear-gradient(135deg, #10b981, #059669); }
.flow-step.step-2 { background: linear-gradient(135deg, #3b82f6, #1d4ed8); }
.flow-step.step-3 { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }
.flow-step.step-4 { background: linear-gradient(135deg, #f59e0b, #d97706); }
.flow-step.step-5 { background: linear-gradient(135deg, #ef4444, #dc2626); }

/* 流程箭头 */
.flow-arrow {
    font-size: 1.2rem;
    color: #667eea;
    font-weight: bold;
}

/* 流程描述 */
.flow-description {
    text-align: center;
    color: #718096;
    font-size: 0.9rem;
    margin-top: 20px;
    line-height: 1.6;
}

/* 返回按钮 */
.back-btn {
    position: fixed;
    top: 20px;
    left: 20px;
    background: rgba(255,255,255,0.9);
    color: #667eea;
    padding: 12px 20px;
    border-radius: 25px;
    text-decoration: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    transition: all 0.3s ease;
}

.back-btn:hover {
    background: white;
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.15);
    color: #667eea;
    text-decoration: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .main-branches {
        grid-template-columns: 1fr;
    }
    
    .header h1 {
        font-size: 2rem;
    }
    
    .mindmap-container {
        padding: 20px;
    }
    
    .flow-container {
        flex-direction: column;
    }
    
    .flow-arrow {
        transform: rotate(90deg);
    }
    
    .flow-section {
        padding: 20px;
    }
}
```

## HTML结构模板

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统功能介绍导图</title>
    <link href="/lib/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="/lib/bootstrap-icons/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        /* 上述CSS样式代码 */
    </style>
</head>
<body>
    <a href="/" class="back-btn">
        <i class="bi bi-arrow-left"></i> 返回首页
    </a>
    
    <div class="container">
        <div class="header">
            <h1><i class="bi bi-diagram-3"></i> 系统名称</h1>
            <p>功能介绍导图 - 系统描述</p>
        </div>
        
        <div class="mindmap-container">
            <!-- 流程图部分 -->
            <div class="flow-section">
                <h2 class="flow-title">
                    <i class="bi bi-diagram-2"></i> 使用流程
                </h2>
                
                <div class="flow-container">
                    <div class="flow-step step-1">
                        <i class="bi bi-person-plus"></i><br>
                        步骤1
                    </div>
                    <div class="flow-arrow">→</div>
                    
                    <div class="flow-step step-2">
                        <i class="bi bi-chat-dots"></i><br>
                        步骤2
                    </div>
                    <div class="flow-arrow">→</div>
                    
                    <div class="flow-step step-3">
                        <i class="bi bi-cpu"></i><br>
                        步骤3
                    </div>
                </div>
                
                <div class="flow-description">
                    流程描述文字
                </div>
            </div>
            
            <!-- 功能导图部分 -->
            <div class="mindmap">
                <div class="root-node">
                    <i class="bi bi-robot"></i> 功能介绍导图
                </div>
                
                <div class="main-branches">
                    <!-- 功能模块1 -->
                    <div class="branch">
                        <div class="branch-header">
                            <div class="branch-icon" style="background: linear-gradient(135deg, #10b981, #059669);">
                                <i class="bi bi-people"></i>
                            </div>
                            <div class="branch-title">功能模块1</div>
                        </div>
                        <div class="sub-branches">
                            <div class="sub-branch">
                                <div class="sub-branch-icon" style="background: #10b981;">
                                    <i class="bi bi-person-badge"></i>
                                </div>
                                <div class="sub-branch-title">子功能1</div>
                            </div>
                            <div class="sub-branch">
                                <div class="sub-branch-icon" style="background: #059669;">
                                    <i class="bi bi-graph-up"></i>
                                </div>
                                <div class="sub-branch-title">子功能2</div>
                            </div>
                        </div>
                        <div class="features">
                            <span class="feature-tag">特性1</span>
                            <span class="feature-tag">特性2</span>
                            <span class="feature-tag">特性3</span>
                        </div>
                        <div class="branch-description">
                            <div class="description-title">作用和意义：</div>
                            功能描述文字
                        </div>
                    </div>
                    
                    <!-- 更多功能模块... -->
                </div>
            </div>
        </div>
    </div>
    
    <script src="/lib/bootstrap/bootstrap.bundle.min.js"></script>
</body>
</html>
```

## 颜色方案

### 主色调
- **主渐变**: `linear-gradient(135deg, #667eea 0%, #764ba2 100%)`
- **背景色**: `#f8fafc`
- **文字色**: `#2d3748`, `#4a5568`

### 功能模块颜色
- **客户管理**: `#10b981` (绿色)
- **沟通记录**: `#3b82f6` (蓝色)
- **AI智能**: `#8b5cf6` (紫色)
- **团队协作**: `#f59e0b` (橙色)
- **消息中心**: `#ef4444` (红色)
- **数据报表**: `#06b6d4` (青色)
- **系统设置**: `#6b7280` (灰色)

### 流程步骤颜色
- **步骤1**: `#10b981` (绿色)
- **步骤2**: `#3b82f6` (蓝色)
- **步骤3**: `#8b5cf6` (紫色)
- **步骤4**: `#f59e0b` (橙色)
- **步骤5**: `#ef4444` (红色)

## 使用说明

1. **依赖库**:
   - Bootstrap 5.x
   - Bootstrap Icons
   - Inter 字体 (Google Fonts)

2. **自定义内容**:
   - 修改 `.header` 中的标题和描述
   - 在 `.main-branches` 中添加功能模块
   - 调整 `.flow-container` 中的流程步骤
   - 根据需要修改颜色方案

3. **响应式适配**:
   - 移动端自动调整为单列布局
   - 流程箭头在移动端旋转90度
   - 字体大小和间距自动调整

4. **扩展功能**:
   - 可以添加更多功能模块
   - 可以调整网格列数
   - 可以自定义动画效果
   - 可以添加更多交互功能

## 技术特点

- **现代CSS**: 使用CSS Grid和Flexbox布局
- **渐变效果**: 丰富的渐变背景和按钮效果
- **动画过渡**: 平滑的悬停和点击效果
- **响应式**: 完美适配各种设备尺寸
- **可维护**: 清晰的CSS结构和命名规范
- **可扩展**: 易于添加新功能和样式

这个设计风格适合用于企业级系统的功能介绍、产品展示、功能导览等场景，具有专业性和现代感。
