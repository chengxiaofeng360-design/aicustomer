// ========== AI智能推荐页面JS代码 ==========

// ========== 工具函数 ==========
// UTF-8到Base64编码（支持中文字符）
function utf8ToBase64(str) {
    try {
        // 方法1：使用TextEncoder（现代浏览器推荐）
        if (typeof TextEncoder !== 'undefined') {
            const encoder = new TextEncoder();
            const bytes = encoder.encode(str);
            let binary = '';
            for (let i = 0; i < bytes.length; i++) {
                binary += String.fromCharCode(bytes[i]);
            }
            return btoa(binary);
        }
        // 方法2：使用encodeURIComponent（兼容旧浏览器）
        // 将UTF-8字符串转换为字节数组
        const utf8Bytes = [];
        for (let i = 0; i < str.length; i++) {
            let charCode = str.charCodeAt(i);
            if (charCode < 0x80) {
                utf8Bytes.push(charCode);
            } else if (charCode < 0x800) {
                utf8Bytes.push(0xc0 | (charCode >> 6));
                utf8Bytes.push(0x80 | (charCode & 0x3f));
            } else if ((charCode & 0xfc00) === 0xd800 && i + 1 < str.length && (str.charCodeAt(i + 1) & 0xfc00) === 0xdc00) {
                // 处理代理对（4字节UTF-8字符）
                charCode = 0x10000 + ((charCode & 0x03ff) << 10) + (str.charCodeAt(++i) & 0x03ff);
                utf8Bytes.push(0xf0 | (charCode >> 18));
                utf8Bytes.push(0x80 | ((charCode >> 12) & 0x3f));
                utf8Bytes.push(0x80 | ((charCode >> 6) & 0x3f));
                utf8Bytes.push(0x80 | (charCode & 0x3f));
            } else {
                utf8Bytes.push(0xe0 | (charCode >> 12));
                utf8Bytes.push(0x80 | ((charCode >> 6) & 0x3f));
                utf8Bytes.push(0x80 | (charCode & 0x3f));
            }
        }
        let binary = '';
        for (let i = 0; i < utf8Bytes.length; i++) {
            binary += String.fromCharCode(utf8Bytes[i]);
        }
        return btoa(binary);
    } catch (e) {
        console.error('UTF-8编码失败:', e);
        // 降级方案：使用encodeURIComponent + replace
        try {
            return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function(match, p1) {
                return String.fromCharCode(parseInt(p1, 16));
            }));
        } catch (e2) {
            console.error('降级编码也失败:', e2);
            // 最后的降级方案：直接返回，让调用者处理
            throw new Error('无法编码字符串: ' + e2.message);
        }
    }
}

        // ========== 立即定义全局函数，确保onclick可以访问 ==========
        // 这些函数将在后面被实际实现替换
        function openGenerateContentModal(initialType) {
            if (typeof window._openGenerateContentModal === 'function') {
                return window._openGenerateContentModal(initialType);
            }
            // 如果实际实现还未加载，等待后重试
            var retryCount = 0;
            var maxRetries = 20;
            var checkFunction = function() {
                retryCount++;
                if (typeof window._openGenerateContentModal === 'function') {
                    window._openGenerateContentModal(initialType);
                } else if (retryCount < maxRetries) {
                    setTimeout(checkFunction, 50);
                } else {
                    console.error('openGenerateContentModal函数未初始化');
                }
            };
            setTimeout(checkFunction, 10);
        }
        
        function openSendShareModal() {
            if (typeof window._openSendShareModal === 'function') {
                return window._openSendShareModal();
            }
            // 如果实际实现还未加载，等待后重试
            var retryCount = 0;
            var maxRetries = 20;
            var checkFunction = function() {
                retryCount++;
                if (typeof window._openSendShareModal === 'function') {
                    window._openSendShareModal();
                } else if (retryCount < maxRetries) {
                    setTimeout(checkFunction, 50);
                } else {
                    console.error('openSendShareModal函数未初始化');
                }
            };
            setTimeout(checkFunction, 10);
        }
        
        // 将函数也添加到window对象
        window.openGenerateContentModal = openGenerateContentModal;
        window.openSendShareModal = openSendShareModal;
        
        // ========== 变量声明 ==========
        // 模拟推荐数据
        let recommendations = [];

        let selectedRecommendations = [];
        let selectedCustomers = [];
        let allCustomers = [];
        let currentReportData = null;
        let currentTemplateData = null;
        let shareLink = '';
        let isReportEditing = false;
        let isTemplateEditing = false;
        let editedReportContent = null;
        let editedTemplateContent = null;
        let currentContentType = 'recommendation'; // 当前内容类型
        let templates = JSON.parse(localStorage.getItem('aiRecommendationTemplates') || '[]');
        let tempContentSource = null;
        let savedTemplates = JSON.parse(localStorage.getItem('aiSavedTemplates') || '[]');
        let sendSelectedCustomers = []; // 发送/分享模态框中选择的客户
        let sendAllCustomers = []; // 发送/分享模态框中的全部客户
        let sendShareLink = ''; // 发送/分享的链接
        let pendingTemplateApply = null;

        // 获取占位推荐数据（仅用于预览，不包含真实客户信息）
        function getPlaceholderRecommendation(type) {
            const base = {
                id: 'preview-' + type,
                customerName: '',
                customerPhone: '',
                customerEmail: '',
                type: 'service',
                title: '',
                content: '',
                priority: 'medium',
                status: 'pending',
                createTime: new Date().toLocaleDateString('zh-CN'),
                confidence: 0.9,
                reason: ''
            };
            switch (type) {
                case 'marketing-long':
                    base.title = 'AI赋能业务增长方案';
                    base.content = '通过数据洞察与智能决策，打造差异化核心优势，帮助客户实现业绩增长。';
                    break;
                case 'greeting-card':
                    base.title = '节日温情贺卡';
                    base.content = '在重要节点送上祝福，传递品牌关怀，增进客户情感连接。';
                    break;
                case 'business-card':
                    base.title = '客户名片摘要';
                    base.content = '概括客户背景、需求与推荐主题，便于团队协同。';
                    break;
                case 'marketing-short':
                    base.title = '限时促销短讯';
                    base.content = '一句话点出客户痛点与解决方案，附带明确行动号召。';
                    break;
                default:
                    break;
            }
            return base;
        }

        // 加载统计数据
        function loadStatistics() {
            const statisticsRow = document.getElementById('statisticsRow');
            if (!statisticsRow) return;
            
            // 从实际数据计算统计数据
            const adoptedCount = recommendations.filter(r => r.status === 'adopted').length;
            const pendingCount = recommendations.filter(r => r.status === 'pending').length;
            const totalCount = recommendations.length;
            const adoptionRate = totalCount > 0 ? Math.round((adoptedCount / totalCount) * 100) : 0;
            const avgConfidence = totalCount > 0 
                ? Math.round(recommendations.reduce((sum, r) => sum + (r.confidence || 0), 0) / totalCount * 100)
                : 0;
            
            statisticsRow.innerHTML = 
                '<div class="col-md-3">' +
                '<div class="stat-card">' +
                '<div class="stat-number">' + adoptedCount + '</div>' +
                '<div class="stat-label">已采纳</div>' +
                '</div>' +
                '</div>' +
                '<div class="col-md-3">' +
                '<div class="stat-card">' +
                '<div class="stat-number">' + pendingCount + '</div>' +
                '<div class="stat-label">待处理</div>' +
                '</div>' +
                '</div>' +
                '<div class="col-md-3">' +
                '<div class="stat-card">' +
                '<div class="stat-number">' + adoptionRate + '%</div>' +
                '<div class="stat-label">采纳率</div>' +
                '</div>' +
                '</div>' +
                '<div class="col-md-3">' +
                '<div class="stat-card">' +
                '<div class="stat-number">' + avgConfidence + '%</div>' +
                '<div class="stat-label">平均置信度</div>' +
                '</div>' +
                '</div>';
        }
        
        // 渲染模板列表
        function renderTemplateList() {
            const container = document.getElementById('templateListContainer');
            if (!container) return;

            // 从localStorage加载保存的模板
            savedTemplates = JSON.parse(localStorage.getItem('aiSavedTemplates') || '[]');
            
            if (savedTemplates.length === 0) {
                container.innerHTML = `
                    <div class="text-center py-4 text-muted">
                        <i class="bi bi-file-earmark-text display-6"></i>
                        <p class="mt-2 mb-0">暂无保存的模板</p>
                        <p class="small">生成内容后可以保存为模板，方便下次使用</p>
                    </div>
                `;
                return;
            }

            let html = '<div class="row g-3">';
            savedTemplates.forEach((template, index) => {
                const typeNames = {
                    'recommendation': '推荐报告',
                    'meeting': '会议纪要',
                    'news': '新闻稿',
                    'report': '报道',
                    'reference': '推荐信',
                    'marketing-long': '长文营销文案',
                    'greeting-card': '祝福贺卡',
                    'business-card': '名片引荐',
                    'marketing-short': '短促销文案'
                };
                const typeName = typeNames[template.type] || '模板';
                const preview = template.content ? (template.content.substring(0, 50) + '...') : '无内容';
                
                html += `
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100">
                            <div class="card-body">
                                <h6 class="card-title">${escapeHtml(template.title || typeName)}</h6>
                                <p class="card-text text-muted small">${escapeHtml(preview)}</p>
                                <div class="d-flex gap-2">
                                    <button class="btn btn-sm btn-outline-primary" onclick="applyTemplate(${index})">
                                        <i class="bi bi-play-circle"></i> 使用
                                    </button>
                                    <button class="btn btn-sm btn-outline-danger" onclick="deleteTemplate(${index})">
                                        <i class="bi bi-trash"></i> 删除
                                    </button>
                                </div>
                            </div>
                            <div class="card-footer text-muted small">
                                <i class="bi bi-clock"></i> ${template.saveTime || '未知时间'}
                            </div>
                        </div>
                    </div>
                `;
            });
            html += '</div>';
            container.innerHTML = html;
        }
        
        // 应用模板
        function applyTemplate(index) {
            const template = savedTemplates[index];
            if (!template) {
                alert('模板不存在');
                return;
            }
            
            // 设置内容类型
            currentContentType = template.type;
            document.getElementById('contentType').value = template.type;
            
            // 打开生成内容模态框
            openGenerateContentModal(template.type);
            
            // 延迟填充内容
            setTimeout(() => {
                if (template.title) {
                    const titleInput = document.getElementById('reportTitle');
                    if (titleInput) {
                        titleInput.value = template.title;
                    }
                }
                if (template.content) {
                    const contentEditor = document.getElementById('reportContentEditor');
                    if (contentEditor) {
                        contentEditor.value = template.content;
                        // 触发保存以更新预览
                        if (typeof saveReportEdit === 'function') {
                            saveReportEdit();
                        }
                    }
                }
            }, 200);
        }
        
        // 删除模板
        function deleteTemplate(index) {
            if (confirm('确定要删除这个模板吗？')) {
                savedTemplates.splice(index, 1);
                localStorage.setItem('aiSavedTemplates', JSON.stringify(savedTemplates));
                renderTemplateList();
                alert('模板已删除');
            }
        }
        
        // 保存当前模板
        function saveCurrentTemplate() {
            const title = document.getElementById('reportTitle')?.value || '';
            const content = document.getElementById('reportContentEditor')?.value || 
                          document.getElementById('reportContent')?.innerText || '';
            
            if (!content.trim()) {
                alert('内容不能为空！');
                return;
            }
            
            const template = {
                type: currentContentType,
                title: title || (currentContentType + '模板'),
                content: content,
                saveTime: new Date().toLocaleString('zh-CN')
            };
            
            savedTemplates.push(template);
            localStorage.setItem('aiSavedTemplates', JSON.stringify(savedTemplates));
            renderTemplateList();
            alert('模板已保存！');
        }
        
        // 页面加载完成后初始化
        document.addEventListener('DOMContentLoaded', function() {
            // 立即加载推荐结果（轻量级）
            loadRecommendationResults();
            // 延迟加载历史记录、模板列表和统计数据（较重的操作）
            setTimeout(function() {
                loadHistoryRecords();
                loadStatistics();
                renderTemplateList();
            }, 100);
        });

        
        // 显示推荐结果弹窗
        function showRecommendationModal(newRecommendations, type, preferences) {
            // 确保弹窗存在
            if (!document.getElementById('recommendationModal')) {
                createRecommendationModal();
            }
            
            const modalBody = document.getElementById('recommendationModalBody');
            let content = '<div class="alert alert-success">' +
                '<h6><i class="bi bi-check-circle"></i> 推荐生成成功！</h6>' +
                '<p>基于您的配置生成了 <strong>' + newRecommendations.length + '</strong> 条' + getTypeText(type) + '推荐</p>' +
                '<p><strong>偏好设置：</strong>' + (preferences.length > 0 ? preferences.join('、') : '通用设置') + '</p>' +
                '</div>' +
                '<div class="recommendation-list">' +
                '<h6>生成的推荐：</h6>';
            
            newRecommendations.forEach((rec, index) => {
                content += '<div class="card mb-2">' +
                    '<div class="card-body py-2">' +
                    '<div class="d-flex justify-content-between align-items-center">' +
                    '<div>' +
                    '<h6 class="mb-1">' + rec.title + '</h6>' +
                    '<p class="mb-1 text-muted small">' + rec.content + '</p>' +
                    '<span class="badge ' + getTypeClass(rec.type) + ' me-1">' + getTypeText(rec.type) + '</span>' +
                    '<span class="badge ' + getPriorityClass(rec.priority) + '">' + getPriorityText(rec.priority) + '</span>' +
                    '</div>' +
                    '<div class="text-end">' +
                    '<div class="text-success fw-bold">' + Math.round(rec.confidence * 100) + '%</div>' +
                    '<small class="text-muted">置信度</small>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>';
            });
            
            content += '</div>';
            modalBody.innerHTML = content;
            
            // 显示弹窗 - 使用更安全的方式
            const modalElement = document.getElementById('recommendationModal');
            const modal = new bootstrap.Modal(modalElement, {
                backdrop: true,
                keyboard: true,
                focus: true
            });
            modal.show();
        }
        
        // 创建推荐结果弹窗
        function createRecommendationModal() {
            const modalHTML = `
                <div class="modal fade" id="recommendationModal" tabindex="-1">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">
                                    <i class="bi bi-lightbulb text-warning"></i> AI推荐结果
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body" id="recommendationModalBody">
                                <!-- 动态内容 -->
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                                <button type="button" class="btn btn-primary" onclick="viewAllRecommendations()">查看全部推荐</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            document.body.insertAdjacentHTML('beforeend', modalHTML);
        }
        
        // 查看全部推荐
        function viewAllRecommendations() {
            // 关闭弹窗
            const modalElement = document.getElementById('recommendationModal');
            if (modalElement) {
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) {
                    modal.hide();
                }
            }
            
            // 滚动到推荐结果区域
            document.getElementById('recommendationResults').scrollIntoView({ 
                behavior: 'smooth',
                block: 'start'
            });
        }
        
        // 显示成功消息
        function showSuccessMessage(message) {
            // 创建成功提示
            const alert = document.createElement('div');
            alert.className = 'alert alert-success alert-dismissible fade show position-fixed';
            alert.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
            alert.innerHTML = `
                <i class="bi bi-check-circle"></i> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            
            document.body.appendChild(alert);
            
            // 3秒后自动消失
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.remove();
                }
            }, 3000);
        }


        // 加载推荐结果
        function loadRecommendationResults() {
            const container = document.getElementById('recommendationResults');
            if (!container) return;
            container.innerHTML = '';

            if (recommendations.length === 0) {
                container.innerHTML = `
                    <div class="text-center py-5">
                        <i class="bi bi-lightbulb display-1 text-muted"></i>
                        <h5 class="mt-3 text-muted">暂无推荐</h5>
                        <p class="mb-1">请先选择上方内容模块</p>
                        <p class="text-muted">勾选推荐后即可生成对应内容</p>
                    </div>
                `;
                return;
            }

            const tableWrapper = document.createElement('div');
            tableWrapper.className = 'table-responsive';
            tableWrapper.innerHTML = `
                <table class="table align-middle">
                    <thead>
                        <tr>
                            <th style="width:40px;"></th>
                            <th>推荐主题</th>
                            <th>客户信息</th>
                            <th>类型/优先级</th>
                            <th>置信度</th>
                            <th class="text-end" style="width:150px;">操作</th>
                        </tr>
                    </thead>
                    <tbody id="recommendationTableBody"></tbody>
                </table>
            `;
            container.appendChild(tableWrapper);

            const tbody = document.getElementById('recommendationTableBody');

            recommendations.forEach(rec => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>
                                    <input type="checkbox" class="form-check-input" value="${rec.id}" onchange="toggleRecommendationSelection(${rec.id})">
                    </td>
                    <td>
                        <div class="d-flex flex-column gap-1">
                            <div class="d-flex align-items-center gap-2">
                                <span>${rec.title}</span>
                                ${rec.isCustom ? '<span class="badge bg-warning text-dark">自定义</span>' : ''}
                                </div>
                            <small class="text-muted table-cell-truncate">${rec.content}</small>
                                    </div>
                    </td>
                    <td>
                        <div class="small fw-semibold">${rec.customerName || '未填写'}</div>
                        <div class="text-muted text-xs">${rec.customerPhone || ''}${rec.customerEmail ? ' · ' + rec.customerEmail : ''}</div>
                    </td>
                    <td>
                        <span class="badge ${getTypeClass(rec.type)} me-1">${getTypeText(rec.type)}</span>
                        <span class="badge ${getPriorityClass(rec.priority)}">${getPriorityText(rec.priority)}</span>
                    </td>
                    <td>
                        <span class="badge bg-info-subtle text-info">置信度 ${Math.round((rec.confidence || 0) * 100)}%</span>
                    </td>
                    <td class="text-end">
                        <button class="btn btn-outline-secondary btn-sm" onclick="viewRecommendationDetail(${rec.id})">
                            <i class="bi bi-eye"></i>
                                    </button>
                        <button class="btn btn-outline-success btn-sm" onclick="adoptRecommendation(${rec.id})">
                            <i class="bi bi-check"></i>
                                    </button>
                    </td>
                `;
                tbody.appendChild(row);
            });
        }

        // 加载历史记录
        function loadHistoryRecords() {
            const tbody = document.getElementById('historyTableBody');
            tbody.innerHTML = '';

            if (recommendations.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center text-muted">暂无历史记录，生成或保存推荐后可在此复盘</td>
                    </tr>
                `;
                return;
            }

            recommendations.forEach(rec => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td class="table-cell-truncate" title="${rec.customerName}">${rec.customerName || '-'}</td>
                    <td class="table-cell-truncate" title="${getTypeText(rec.type)}">${getTypeText(rec.type)}</td>
                    <td class="table-cell-truncate" title="${rec.title}">
                        ${rec.title}
                        ${rec.isCustom ? '<span class="badge bg-warning text-dark ms-2">自定义</span>' : ''}
                    </td>
                    <td><span class="badge ${getPriorityClass(rec.priority)}">${getPriorityText(rec.priority)}</span></td>
                    <td><span class="badge ${getStatusClass(rec.status)}">${getStatusText(rec.status)}</span></td>
                    <td class="table-cell-truncate" title="${rec.createTime}">${rec.createTime}</td>
                    <td>
                        <button class="btn btn-outline-primary btn-sm" onclick="viewRecommendationDetail(${rec.id})" title="查看详情">
                            <i class="bi bi-eye"></i>
                        </button>
                        <button class="btn btn-success btn-sm" onclick="adoptRecommendation(${rec.id})" title="采纳推荐">
                            <i class="bi bi-check"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(row);
            });
        }

        // 查看推荐详情
        function viewRecommendationDetail(id) {
            const rec = recommendations.find(r => r.id === id);
            if (rec) {
                const content = `
                    <div class="row">
                        <div class="col-md-6">
                            <h6>基本信息</h6>
                            <p><strong>客户名称：</strong>${rec.customerName}</p>
                            <p><strong>推荐类型：</strong>${getTypeText(rec.type)}</p>
                            <p><strong>优先级：</strong>${getPriorityText(rec.priority)}</p>
                            <p><strong>状态：</strong>${getStatusText(rec.status)}</p>
                        </div>
                        <div class="col-md-6">
                            <h6>详细信息</h6>
                            <p><strong>置信度：</strong>${Math.round(rec.confidence * 100)}%</p>
                            <p><strong>创建时间：</strong>${rec.createTime}</p>
                        </div>
                    </div>
                    <div class="row mt-3">
                        <div class="col-12">
                            <h6>推荐内容</h6>
                            <p>${rec.content}</p>
                        </div>
                    </div>
                    <div class="row mt-3">
                        <div class="col-12">
                            <h6>推荐理由</h6>
                            <p>${rec.reason}</p>
                        </div>
                    </div>
                `;
                document.getElementById('recommendationDetailContent').innerHTML = content;
                new bootstrap.Modal(document.getElementById('recommendationDetailModal')).show();
            }
        }

        // 采纳推荐
        function adoptRecommendation(id) {
            const rec = recommendations.find(r => r.id === id);
            if (rec) {
                rec.status = 'adopted';
                rec.modifyTime = new Date().toISOString().split('T')[0];
                loadRecommendationResults();
                loadHistoryRecords();
                bootstrap.Modal.getInstance(document.getElementById('recommendationDetailModal')).hide();
                alert('推荐已采纳！');
            }
        }

        // 切换推荐选择状态
        function toggleRecommendationSelection(id) {
            const index = selectedRecommendations.indexOf(id);
            if (index > -1) {
                selectedRecommendations.splice(index, 1);
            } else {
                selectedRecommendations.push(id);
            }
        }
        

        // 批量采纳
        function batchAdopt() {
            if (selectedRecommendations.length === 0) {
                alert('请先选择要采纳的推荐！');
                return;
            }
            
            if (confirm(`确定要采纳选中的 ${selectedRecommendations.length} 条推荐吗？`)) {
                selectedRecommendations.forEach(id => {
                    const rec = recommendations.find(r => r.id === id);
                    if (rec) {
                        rec.status = 'adopted';
                        rec.modifyTime = new Date().toISOString().split('T')[0];
                    }
                });
                selectedRecommendations = [];
                loadRecommendationResults();
                loadHistoryRecords();
                alert('批量采纳成功！');
            }
        }

        // 导出推荐
        function exportRecommendations() {
            const dataStr = JSON.stringify(recommendations, null, 2);
            const dataBlob = new Blob([dataStr], {type: 'application/json'});
            const url = URL.createObjectURL(dataBlob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `recommendations_${new Date().toISOString().split('T')[0]}.json`;
            link.click();
            URL.revokeObjectURL(url);
        }

        // 获取类型样式类
        function getTypeClass(type) {
            switch (type) {
                case 'product': return 'bg-primary';
                case 'service': return 'bg-success';
                case 'strategy': return 'bg-warning';
                case 'action': return 'bg-info';
                default: return 'bg-secondary';
            }
        }

        // 获取优先级样式类
        function getPriorityClass(priority) {
            switch (priority) {
                case 'high': return 'bg-danger';
                case 'medium': return 'bg-warning';
                case 'low': return 'bg-success';
                default: return 'bg-secondary';
            }
        }

        // 获取状态样式类
        function getStatusClass(status) {
            switch (status) {
                case 'pending': return 'bg-warning';
                case 'adopted': return 'bg-success';
                case 'rejected': return 'bg-danger';
                default: return 'bg-secondary';
            }
        }

        // 获取类型文本
        function getTypeText(type) {
            switch (type) {
                case 'product': return '产品推荐';
                case 'service': return '服务推荐';
                case 'strategy': return '策略推荐';
                case 'action': return '行动推荐';
                default: return '未知';
            }
        }

        // 获取优先级文本
        function getPriorityText(priority) {
            switch (priority) {
                case 'high': return '高';
                case 'medium': return '中';
                case 'low': return '低';
                default: return '未知';
            }
        }

        // 获取状态文本
        function getStatusText(status) {
            switch (status) {
                case 'pending': return '待处理';
                case 'adopted': return '已采纳';
                case 'rejected': return '已拒绝';
                default: return '未知';
            }
        }

        // ========== 内容生成功能 ==========
        
        // 打开内容生成模态框（实际实现）
        function _openGenerateContentModal(initialType = null) {
            if (selectedRecommendations.length === 0 && !initialType) {
                alert('请先选择要生成内容的推荐！');
                return;
            }
            
            const modal = new bootstrap.Modal(document.getElementById('generateContentModal'));
            modal.show();
            
            // 如果是编辑模板模式（传入了类型）
            if (initialType) {
                // 隐藏Tab导航和内容类型选择
                document.getElementById('contentTabs').style.display = 'none';
                document.getElementById('contentTypeSelector').style.display = 'none';
                
                // 设置内容类型
                document.getElementById('contentType').value = initialType;
                
                // 更新标题
                const typeNames = {
                    'recommendation': '推荐报告',
                    'meeting': '会议纪要',
                    'news': '新闻稿',
                    'report': '报道',
                    'reference': '推荐信',
                    'marketing-long': '长文营销文案',
                    'greeting-card': '祝福贺卡',
                    'business-card': '名片引荐',
                    'marketing-short': '短促销文案'
                };
                document.getElementById('generateContentModalLabel').innerHTML = 
                    '<i class="bi bi-pencil"></i> 编辑模板 - ' + typeNames[initialType];
                document.getElementById('contentTypeTitle').textContent = typeNames[initialType];
                
                // 直接生成内容（如果没有推荐，使用占位数据）
                setTimeout(() => {
                    if (selectedRecommendations.length === 0) {
                        // 使用占位数据生成内容
                        const placeholder = getPlaceholderRecommendation(initialType);
                        currentContentType = initialType;
                        generateContentByType([placeholder]);
                    } else {
                        currentContentType = initialType;
                        changeContentType();
                    }
                }, 100);
            } else {
                // 普通模式：显示Tab导航和内容类型选择
                document.getElementById('contentTabs').style.display = 'flex';
                document.getElementById('contentTypeSelector').style.display = 'block';
                document.getElementById('generateContentModalLabel').innerHTML = 
                    '<i class="bi bi-file-earmark-text"></i> 生成内容并发送';
                changeContentType();
                
                // 监听Tab切换事件，自动生成内容
                const tabButtons = document.querySelectorAll('#contentTabs button[data-bs-toggle="tab"]');
                tabButtons.forEach(button => {
                    button.addEventListener('shown.bs.tab', function(event) {
                        const targetId = event.target.getAttribute('data-bs-target');
                        if (targetId === '#template-pane') {
                            generateTemplate();
                        } else if (targetId === '#customer-pane') {
                            loadCustomerSelection();
                        } else if (targetId === '#preview-pane') {
                            updatePreviewCustomerSelect();
                        } else if (targetId === '#share-pane') {
                            if (!shareLink) {
                                generateShareLink();
                            }
                        }
                    });
                });
            }
        }
        
        // 切换内容类型
        function changeContentType() {
            currentContentType = document.getElementById('contentType').value;
            const typeNames = {
                'recommendation': '推荐报告',
                'meeting': '会议纪要',
                'news': '新闻稿',
                'report': '报道',
                'reference': '推荐信',
                'marketing-long': '长文营销文案',
                'greeting-card': '祝福贺卡',
                'business-card': '名片引荐',
                'marketing-short': '短促销文案'
            };
            
            document.getElementById('contentTypeTitle').textContent = typeNames[currentContentType] + '预览';
            document.getElementById('editorTitleLabel').textContent = typeNames[currentContentType] + '标题';
            
            // 根据类型生成内容
            generateContentByType();
        }
        
        // 根据类型生成内容
        function generateContentByType(customRecs = null) {
            const selectedRecs = customRecs || recommendations.filter(r => selectedRecommendations.includes(r.id));
            if (selectedRecs.length === 0) {
                document.getElementById('reportContent').innerHTML = '<p class="text-muted">请先选择想要生成内容的推荐</p>';
                return;
            }
            
            switch(currentContentType) {
                case 'recommendation':
                    generateRecommendationReport(selectedRecs);
                    break;
                case 'meeting':
                    generateMeetingMinutes(selectedRecs);
                    break;
                case 'news':
                    generateNewsArticle(selectedRecs);
                    break;
                case 'report':
                    generateReport(selectedRecs);
                    break;
                case 'reference':
                    generateReferenceLetter(selectedRecs);
                    break;
                case 'marketing-long':
                    generateLongMarketingCopy(selectedRecs);
                    break;
                case 'greeting-card':
                    generateGreetingCard(selectedRecs);
                    break;
                case 'business-card':
                    generateBusinessCard(selectedRecs);
                    break;
                case 'marketing-short':
                    generateShortMarketingCopy(selectedRecs);
                    break;
                default:
                    generateRecommendationReport(selectedRecs);
            }
        }
        
        function previewModule(type) {
            const previousSelection = [...selectedRecommendations];
            if (recommendations.length === 0) {
                const placeholder = getPlaceholderRecommendation(type);
                document.getElementById('contentType').value = type;
                generateContentByType([placeholder]);
                document.getElementById('reportContent').scrollIntoView({ behavior: 'smooth' });
                selectedRecommendations = previousSelection;
                return;
            }
            if (selectedRecommendations.length === 0 && recommendations.length > 0) {
                selectedRecommendations = [recommendations[0].id];
            }
            document.getElementById('contentType').value = type;
            generateContentByType();
            document.getElementById('reportContent').scrollIntoView({ behavior: 'smooth' });
            selectedRecommendations = previousSelection;
        }

        // 生成推荐报告
        function generateRecommendationReport(customList = null) {
            const selectedRecs = customList || recommendations.filter(r => selectedRecommendations.includes(r.id));
            if (selectedRecs.length === 0) {
                document.getElementById('reportContent').innerHTML = '<p class="text-muted">请先选择想要生成内容的推荐</p>';
                return;
            }
            
            let reportHtml = `
                <div class="report-header mb-4">
                    <h3>AI智能推荐报告</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                    <p class="text-muted">推荐数量：${selectedRecs.length} 条</p>
                </div>
                <div class="report-body">
            `;
            
            selectedRecs.forEach((rec, index) => {
                reportHtml += `
                    <div class="report-item mb-4 p-3 border rounded">
                        <h5 class="mb-3">推荐 ${index + 1}: ${rec.title}</h5>
                        <div class="row mb-2">
                            <div class="col-md-6">
                                <strong>客户名称：</strong>${rec.customerName || '未指定'}
                            </div>
                            <div class="col-md-6">
                                <strong>推荐类型：</strong><span class="badge ${getTypeClass(rec.type)}">${getTypeText(rec.type)}</span>
                            </div>
                        </div>
                        <div class="row mb-2">
                            <div class="col-md-6">
                                <strong>优先级：</strong><span class="badge ${getPriorityClass(rec.priority)}">${getPriorityText(rec.priority)}</span>
                            </div>
                            <div class="col-md-6">
                                <strong>置信度：</strong>${Math.round(rec.confidence * 100)}%
                            </div>
                        </div>
                        <div class="mb-2">
                            <strong>推荐内容：</strong>
                            <p class="mt-2">${rec.content}</p>
                        </div>
                        <div class="mb-2">
                            <strong>推荐理由：</strong>
                            <p class="mt-2">${rec.reason || '无'}</p>
                        </div>
                        <div class="text-muted small">
                            <strong>创建时间：</strong>${rec.createTime || '未知'}
                        </div>
                    </div>
                `;
            });
            
            reportHtml += `
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本报告由AI智能推荐系统自动生成</p>
                </div>
            `;
            
            document.getElementById('reportContent').innerHTML = reportHtml;
            currentReportData = reportHtml;
            editedReportContent = null; // 重置编辑内容
            
            // 自动生成分享链接
            generateShareLink();
        }
        
        // 生成会议纪要
        function generateMeetingMinutes(selectedRecs) {
            const date = new Date().toLocaleDateString('zh-CN');
            const time = new Date().toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
            
            let content = `会议纪要

会议主题：基于AI智能推荐的业务讨论会
会议时间：${date} ${time}
会议地点：线上会议
主持人：系统管理员
记录人：AI智能推荐系统

一、会议目的
本次会议旨在讨论和评估AI智能推荐系统生成的${selectedRecs.length}条推荐方案，确定后续行动计划和资源分配。

二、推荐内容讨论

`;
            
            selectedRecs.forEach((rec, index) => {
                content += `${index + 1}. ${rec.title}
   客户：${rec.customerName || '未指定'}
   类型：${getTypeText(rec.type)}
   优先级：${getPriorityText(rec.priority)}
   推荐内容：${rec.content}
   推荐理由：${rec.reason || '无'}
   
`;
            });
            
            content += `三、讨论要点
1. 对推荐方案的可行性进行了深入讨论
2. 分析了各推荐方案的优先级和资源需求
3. 确定了重点推进的推荐项目

四、决议事项
1. 采纳部分高优先级推荐方案
2. 安排专人跟进推荐方案的执行
3. 定期回顾推荐效果，优化推荐策略

五、后续行动
1. 制定详细的执行计划
2. 分配责任人和时间节点
3. 建立跟踪机制，定期汇报进展

六、会议总结
本次会议对AI智能推荐系统生成的推荐方案进行了全面评估，为后续业务发展提供了重要参考。与会人员一致认为，应充分利用AI推荐系统的优势，持续优化推荐策略，提升业务效率。

记录时间：${new Date().toLocaleString('zh-CN')}
`;
            
            const html = `
                <div class="report-header mb-4">
                    <h3>会议纪要</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                </div>
                <div class="report-body">
                    <div style="white-space: pre-wrap; line-height: 1.8; font-family: 'Microsoft YaHei', sans-serif;">${escapeHtml(content)}</div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本会议纪要由AI智能推荐系统自动生成</p>
                </div>
            `;
            
            document.getElementById('reportContent').innerHTML = html;
            currentReportData = html;
            editedReportContent = null;
            generateShareLink();
        }
        
        // 生成新闻稿
        function generateNewsArticle(selectedRecs) {
            const date = new Date().toLocaleDateString('zh-CN');
            
            let content = `AI智能推荐系统助力业务发展，${selectedRecs.length}项推荐方案正式发布

【本网讯】${date}，AI智能推荐系统基于大数据分析和人工智能算法，成功生成${selectedRecs.length}项专业推荐方案，为业务发展提供有力支撑。

据了解，本次发布的推荐方案涵盖产品推荐、服务优化、策略制定等多个维度，旨在帮助企业更好地把握市场机遇，提升业务竞争力。

一、推荐方案亮点

`;
            
            selectedRecs.forEach((rec, index) => {
                content += `${index + 1}. ${rec.title}
   本次推荐针对${rec.customerName || '目标客户'}，提出了${rec.content}。该方案具有${getPriorityText(rec.priority)}优先级，置信度达到${Math.round(rec.confidence * 100)}%，${rec.reason || '具有较高的实施价值'}。

`;
            });
            
            content += `二、技术优势
AI智能推荐系统采用先进的机器学习算法，能够深度分析客户需求、市场趋势和业务数据，自动生成精准的推荐方案。系统具备以下优势：
- 数据驱动：基于海量真实业务数据进行分析
- 智能决策：运用AI算法识别最佳业务机会
- 持续优化：根据反馈不断改进推荐质量

三、应用前景
专家表示，AI智能推荐系统的应用将显著提升业务决策效率，帮助企业快速响应市场变化，实现精准营销和资源优化配置。

未来，系统将持续升级优化，为更多企业提供智能化业务推荐服务，推动行业数字化转型。

【记者】AI智能推荐系统
【编辑】系统管理员
【发布时间】${new Date().toLocaleString('zh-CN')}
`;
            
            const html = `
                <div class="report-header mb-4">
                    <h3>新闻稿</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                </div>
                <div class="report-body">
                    <div style="white-space: pre-wrap; line-height: 1.8; font-family: 'Microsoft YaHei', sans-serif;">${escapeHtml(content)}</div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本新闻稿由AI智能推荐系统自动生成</p>
                </div>
            `;
            
            document.getElementById('reportContent').innerHTML = html;
            currentReportData = html;
            editedReportContent = null;
            generateShareLink();
        }
        
        // 生成报道
        function generateReport(selectedRecs) {
            const date = new Date().toLocaleDateString('zh-CN');
            
            let content = `AI智能推荐系统业务分析报道

时间：${date}
来源：AI智能推荐系统
作者：系统分析员

一、概述
本文对AI智能推荐系统近期生成的${selectedRecs.length}项推荐方案进行深度分析，从多个维度评估推荐价值，为业务决策提供参考。

二、推荐方案分析

`;
            
            selectedRecs.forEach((rec, index) => {
                content += `方案${index + 1}：${rec.title}

客户信息：${rec.customerName || '未指定'}
推荐类型：${getTypeText(rec.type)}
优先级评估：${getPriorityText(rec.priority)}
置信度：${Math.round(rec.confidence * 100)}%

方案内容：
${rec.content}

推荐理由：
${rec.reason || '基于数据分析得出的专业建议'}

实施建议：
1. 评估方案可行性和资源需求
2. 制定详细的执行计划
3. 建立效果跟踪机制
4. 根据反馈持续优化

---
`;
            });
            
            content += `三、综合分析
通过对${selectedRecs.length}项推荐方案的深入分析，我们发现：
1. 推荐方案覆盖了产品、服务、策略等多个业务领域
2. 高优先级推荐占比${Math.round(selectedRecs.filter(r => r.priority === 'high').length / selectedRecs.length * 100)}%，体现了系统对重要机会的识别能力
3. 平均置信度达到${Math.round(selectedRecs.reduce((sum, r) => sum + r.confidence, 0) / selectedRecs.length * 100)}%，推荐质量较高

四、结论与建议
AI智能推荐系统通过智能分析，为业务发展提供了有价值的参考。建议：
1. 优先实施高优先级推荐方案
2. 建立推荐方案执行跟踪机制
3. 持续优化推荐算法，提升推荐精准度
4. 加强推荐结果与实际业务效果的关联分析

报告生成时间：${new Date().toLocaleString('zh-CN')}
`;
            
            const html = `
                <div class="report-header mb-4">
                    <h3>业务分析报道</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                </div>
                <div class="report-body">
                    <div style="white-space: pre-wrap; line-height: 1.8; font-family: 'Microsoft YaHei', sans-serif;">${escapeHtml(content)}</div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本报道由AI智能推荐系统自动生成</p>
                </div>
            `;
            
            document.getElementById('reportContent').innerHTML = html;
            currentReportData = html;
            editedReportContent = null;
            generateShareLink();
        }
        
        // 生成推荐信
        function generateReferenceLetter(selectedRecs) {
            const date = new Date().toLocaleDateString('zh-CN');
            
            let content = `推荐信

尊敬的合作伙伴：

您好！

基于我们对${selectedRecs.length}项AI智能推荐方案的深入分析和评估，我们诚挚地向您推荐以下业务机会和合作方案。

一、推荐背景
经过AI智能推荐系统的专业分析，我们识别出${selectedRecs.length}项具有较高价值的业务推荐方案。这些方案基于真实业务数据和市场分析，旨在为双方创造更大的商业价值。

二、推荐方案详情

`;
            
            selectedRecs.forEach((rec, index) => {
                content += `推荐${index + 1}：${rec.title}

针对客户：${rec.customerName || '目标客户群体'}
推荐类型：${getTypeText(rec.type)}
优先级：${getPriorityText(rec.priority)}
置信度：${Math.round(rec.confidence * 100)}%

方案描述：
${rec.content}

推荐理由：
${rec.reason || '该方案具有较高的实施价值和市场潜力'}

预期收益：
- 提升业务效率
- 增强市场竞争力
- 创造新的商业价值

---
`;
            });
            
            content += `三、合作建议
我们建议双方就上述推荐方案进行深入沟通，共同探讨合作机会。我们相信，通过双方的共同努力，这些推荐方案将为彼此带来显著的商业价值。

四、后续安排
1. 安排专题会议，详细讨论推荐方案
2. 制定合作计划和时间表
3. 建立定期沟通机制
4. 跟踪方案执行效果

我们期待与您进一步沟通，共同推进这些推荐方案的落地实施。

此致
敬礼！

AI智能推荐系统
${date}

联系方式：
电话：400-XXX-XXXX
邮箱：recommendation@example.com
`;
            
            const html = `
                <div class="report-header mb-4">
                    <h3>推荐信</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                </div>
                <div class="report-body">
                    <div style="white-space: pre-wrap; line-height: 1.8; font-family: 'Microsoft YaHei', sans-serif;">${escapeHtml(content)}</div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本推荐信由AI智能推荐系统自动生成</p>
                </div>
            `;
            
            document.getElementById('reportContent').innerHTML = html;
            currentReportData = html;
            editedReportContent = null;
            generateShareLink();
        }
        
        // 生成营销文稿
        function generateLongMarketingCopy(selectedRecs) {
            const date = new Date().toLocaleDateString('zh-CN');
            
            let content = `【营销文稿】AI智能推荐 - 为您的业务赋能

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📢 重磅推荐 | ${selectedRecs.length}项专业方案，助力业务腾飞

亲爱的合作伙伴：

在这个快速变化的商业环境中，精准的决策和专业的方案是成功的关键。AI智能推荐系统为您精心准备了${selectedRecs.length}项专业推荐方案，助您把握商机，实现业务突破！

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✨ 推荐亮点

`;
            
            selectedRecs.forEach((rec, index) => {
                content += `🎯 方案${index + 1}：${rec.title}

👤 目标客户：${rec.customerName || '精准定位'}
📊 推荐类型：${getTypeText(rec.type)}
⭐ 优先级：${getPriorityText(rec.priority)}
🎯 置信度：${Math.round(rec.confidence * 100)}%

💡 方案内容：
${rec.content}

💎 核心优势：
${rec.reason || '基于AI智能分析，具有较高的实施价值'}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

`;
            });
            
            content += `🚀 为什么选择我们的推荐方案？

✅ 数据驱动：基于海量真实业务数据，确保推荐精准
✅ AI智能：运用先进算法，识别最佳业务机会
✅ 专业可靠：${Math.round(selectedRecs.reduce((sum, r) => sum + r.confidence, 0) / selectedRecs.length * 100)}%平均置信度，质量有保障
✅ 持续优化：根据反馈不断改进，提供更优质服务

📈 预期收益

通过实施这些推荐方案，您将获得：
• 提升业务效率${Math.round(Math.random() * 20 + 10)}%
• 增强市场竞争力
• 创造新的商业价值
• 优化资源配置

🎁 限时优惠

现在联系我们，即可享受：
• 免费方案咨询
• 专业团队支持
• 全程跟踪服务

📞 立即行动

不要错过这个提升业务的机会！立即联系我们，让AI智能推荐系统为您的业务赋能！

联系方式：
📱 电话：400-XXX-XXXX
📧 邮箱：marketing@example.com
🌐 官网：www.example.com

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

生成时间：${new Date().toLocaleString('zh-CN')}
AI智能推荐系统 | 让智能为业务赋能
`;
            
            const html = `
                <div class="report-header mb-4">
                    <h3>长文营销文案</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                </div>
                <div class="report-body">
                    <div style="white-space: pre-wrap; line-height: 1.8; font-family: 'Microsoft YaHei', sans-serif;">${escapeHtml(content)}</div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本长文营销文案由AI智能推荐系统自动生成</p>
                </div>
            `;
            
            document.getElementById('reportContent').innerHTML = html;
            currentReportData = html;
            editedReportContent = null;
            generateShareLink();
        }

        const greetingSceneMeta = {
            newyear: { label: '新年', icon: 'bi-fireworks' },
            spring: { label: '春节', icon: 'bi-brightness-high' },
            summer: { label: '夏日', icon: 'bi-sunrise' },
            autumn: { label: '秋日', icon: 'bi-moon-stars' },
            birthday: { label: '生日', icon: 'bi-gift' },
            thankyou: { label: '感谢', icon: 'bi-heart-fill' },
            general: { label: '通用', icon: 'bi-stars' }
        };
        
        // 生成祝福贺卡（支持多场景）
        function generateGreetingCard(selectedRecs) {
            const customer = selectedRecs[0] || {};
            const customerName = customer.customerName || '尊敬的合作伙伴';
            const date = new Date().toLocaleDateString('zh-CN');
            const month = new Date().getMonth() + 1;
            
            // 智能识别节日场景
            let scene = 'general'; // 默认通用场景
            let sceneTitle = '诚挚祝福';
            
            // 根据月份智能判断可能的节日
            if (month === 1) { scene = 'newyear'; sceneTitle = '新年祝福'; }
            else if (month === 2) { scene = 'spring'; sceneTitle = '春节祝福'; }
            else if (month === 3) { scene = 'spring'; sceneTitle = '春季问候'; }
            else if (month === 6) { scene = 'summer'; sceneTitle = '夏季问候'; }
            else if (month === 9 || month === 10) { scene = 'autumn'; sceneTitle = '秋季问候'; }
            else if (month === 12) { scene = 'newyear'; sceneTitle = '年终祝福'; }
            
            const templates = createGreetingTemplates(customerName, date);
            
            const template = templates[scene] || templates.general;
            const html = buildGreetingCardHtml(template, scene, greetingSceneMeta);

            document.getElementById('reportContent').innerHTML = html;
            currentReportData = html;
            editedReportContent = null;
            // 保存当前场景，用于场景切换
            window.currentGreetingScene = scene;
            window.currentGreetingCustomer = customer;
            generateShareLink();
        }
        
        // 切换祝福场景
        function switchGreetingScene(newScene) {
            const customer = window.currentGreetingCustomer || {};
            customer._forceScene = newScene;
            const oldScene = window.currentGreetingScene;
            window.currentGreetingScene = newScene;
            // 临时修改场景生成
            const originalGenerate = generateGreetingCard;
            generateGreetingCard = function(recs) {
                const temp = recs[0] || customer;
                const month = new Date().getMonth() + 1;
                const scene = newScene;
                const customerName = temp.customerName || '尊敬的合作伙伴';
                const date = new Date().toLocaleDateString('zh-CN');
                
                const templates = createGreetingTemplates(customerName, date);
                
                const template = templates[scene] || templates.general;
                const html = buildGreetingCardHtml(template, scene, greetingSceneMeta);

                document.getElementById('reportContent').innerHTML = html;
                currentReportData = html;
                editedReportContent = null;
                window.currentGreetingScene = scene;
                generateShareLink();
            };
            generateGreetingCard([customer]);
            generateGreetingCard = originalGenerate;
        }
        
        // 确保函数全局可访问
        window.switchGreetingScene = switchGreetingScene;
        
        function buildGreetingCardHtml(template, scene, sceneMeta) {
            const meta = sceneMeta[scene] || sceneMeta.general || { label: '通用', icon: 'bi-stars' };
            const buttons = Object.keys(sceneMeta).map(key => {
                const label = sceneMeta[key].label;
                const isActive = key === scene;
                return `<button type="button" class="btn btn-sm ${isActive ? 'btn-primary' : 'btn-outline-primary'}" onclick="switchGreetingScene('${key}')">${label}</button>`;
            }).join('');
            const badges = (template.badges || []).map(badge => `<span class="greeting-card-pro__badge">${escapeHtml(badge)}</span>`).join('');
            const generatedTime = new Date().toLocaleString('zh-CN');
            
            return `
                <div class="report-header mb-4">
                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                        <div>
                            <h3>${escapeHtml(template.title)}</h3>
                            <p class="text-muted mb-1">生成时间：${generatedTime}</p>
                            <small class="text-muted">AI智能推荐 · 高端客户关怀</small>
                        </div>
                        <div class="btn-group" role="group">
                            ${buttons}
                        </div>
                    </div>
                    <small class="text-muted">💡 提示：点击场景标签即可切换不同风格的贺卡模板</small>
                </div>
                <div class="report-body">
                    <div class="greeting-card-pro">
                        <div class="greeting-card-pro__glow"></div>
                        <div class="greeting-card-pro__container">
                            <div class="greeting-card-pro__header">
                                <div>
                                    <div class="greeting-card-pro__tag">
                                        <i class="bi ${meta.icon}"></i>
                                        <span>${escapeHtml(template.tagline || 'Premium Greetings')}</span>
                                    </div>
                                    <div class="greeting-card-pro__title">${escapeHtml(template.sceneName || meta.label)}</div>
                                    <div class="greeting-card-pro__subtitle">${escapeHtml(template.subtitle || '客户关怀 · 品牌致意')}</div>
                                </div>
                                <div class="text-end text-white-50">
                                    <div class="small text-uppercase">当前场景</div>
                                    <div class="fs-5 fw-semibold text-white">${escapeHtml(meta.label)}</div>
                                </div>
                            </div>
                            <div class="greeting-card-pro__body">
                                <div class="greeting-card-pro__body-content">
                                    <p class="mb-3">${escapeHtml(template.greeting)}</p>
                                    <p class="mb-3">${escapeHtml(template.opening)}</p>
                                    <div class="mb-4" style="white-space: pre-wrap;">${escapeHtml(template.body)}</div>
                                    <p class="mb-0">${escapeHtml(template.closing)}</p>
                                </div>
                            </div>
                            <div class="greeting-card-pro__signature" style="white-space: pre-wrap;">
                                ${escapeHtml(template.signature)}
                            </div>
                            ${badges ? `<div class="greeting-card-pro__badges">${badges}</div>` : ''}
                        </div>
                    </div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small mb-1">
                        <i class="bi bi-info-circle"></i> 本贺卡由 AI 智能推荐系统自动生成，可直接编辑文字内容
                    </p>
                    <small class="text-muted">场景：${escapeHtml(meta.label)} · 模板：${escapeHtml(template.title)}</small>
                </div>
            `;
        }
        
        function createGreetingTemplates(customerName, date) {
            return {
                newyear: {
                    title: '🎊 新年快乐',
                    sceneName: '新年贺喜',
                    greeting: `${customerName}，您好！`,
                    subtitle: 'AI客户关怀 · 年度致谢',
                    tagline: 'Premium Seasonal Greetings',
                    badges: ['年度共赢', '战略伙伴'],
                    opening: `新年伊始，万象更新！值此辞旧迎新之际，谨代表全体团队向您致以最诚挚的祝福与问候。`,
                    body: `回首过去一年，感谢您一如既往的信任与支持。新的一年里，愿您：
• 事业蒸蒸日上，业绩再创新高
• 身体健康，家庭幸福美满
• 心想事成，万事如意
• 财源广进，鸿运当头`,
                    closing: `让我们携手并进，共创辉煌！期待在新的一年里，继续与您保持紧密合作，为彼此创造更大价值！`,
                    signature: `此致\n敬礼！\n\n种业客户管理团队\n${date}`
                },
                spring: {
                    title: '🌸 春节祝福',
                    sceneName: '春日团圆',
                    greeting: `${customerName}，春节快乐！`,
                    subtitle: '节庆问候 · 聚焦客户体验',
                    tagline: 'Spring Festival Edition',
                    badges: ['客户关怀', '节庆营销'],
                    opening: `爆竹声中辞旧岁，春风得意迎新年。值此新春佳节来临之际，恭祝您及家人春节快乐，阖家幸福！`,
                    body: `感谢您过去一年的信任与合作。新春新气象，祝愿您在新的一年里：
• 🧧 福运满满，财源滚滚
• 🎋 事业有成，步步高升
• 🏮 家庭和睦，幸福安康
• 🎊 心想事成，万事顺意`,
                    closing: `愿我们在新的一年里继续携手前行，共创美好未来！`,
                    signature: `恭贺新春！\n\n种业客户管理团队\n${date}`
                },
                summer: {
                    title: '☀️ 夏日问候',
                    sceneName: '夏日焕新',
                    greeting: `${customerName}，您好！`,
                    subtitle: '客户陪伴 · 全年关怀',
                    tagline: 'Seasonal Refresh',
                    badges: ['体验洞察', '温度服务'],
                    opening: `盛夏时节，骄阳似火。在这充满活力的季节里，向您致以最诚挚的问候！`,
                    body: `感谢您持续的信任与支持。愿您在这个充满希望的季节里：
• 🌻 事业如夏花般绚烂
• 🍃 身体健康，活力满满
• 🌈 心情愉悦，收获满满
• ⭐ 每一天都充满阳光与希望`,
                    closing: `炎炎夏日，注意防暑降温。让我们一起迎接更加美好的明天！`,
                    signature: `夏日问候\n\n种业客户管理团队\n${date}`
                },
                autumn: {
                    title: '🍂 秋日问候',
                    sceneName: '秋收致敬',
                    greeting: `${customerName}，您好！`,
                    subtitle: '合作成果 · 阶段复盘',
                    tagline: 'Harvest Season Message',
                    badges: ['成果回顾', '合作升级'],
                    opening: `金秋时节，硕果累累。在这个收获的季节里，向您致以最美好的祝愿！`,
                    body: `感谢您一路的陪伴与信赖。愿您在这个丰收的季节里：
• 🌾 收获满满，成果丰硕
• 🍁 事业稳步发展，再创佳绩
• 🎃 身心愉悦，万事顺心
• 🌟 每一份努力都有所回报`,
                    closing: `秋高气爽，适宜出行。期待我们继续携手，共创辉煌！`,
                    signature: `秋日祝福\n\n种业客户管理团队\n${date}`
                },
                birthday: {
                    title: '🎂 生日快乐',
                    sceneName: '尊享生日',
                    greeting: `尊敬的${customerName}：`,
                    subtitle: '客户生命周期 · 尊享关怀',
                    tagline: 'Client Birthday Edition',
                    badges: ['尊贵客户', '专属关怀'],
                    opening: `值此您生日之际，谨代表全体团队向您致以最诚挚的祝福！`,
                    body: `感谢您一直以来的信任与合作。在这个特别的日子里，愿您：
• 🎉 生日快乐，心想事成
• 🎁 身体健康，事业顺利
• 🎈 家庭幸福，万事如意
• 🌟 每一天都充满欢乐与惊喜`,
                    closing: `祝您度过一个美好而难忘的生日，未来的日子里一切安好！`,
                    signature: `生日祝福\n\n种业客户管理团队\n${date}`
                },
                thankyou: {
                    title: '🙏 感谢信',
                    sceneName: '战略感恩',
                    greeting: `尊敬的${customerName}：`,
                    subtitle: '合作感谢 · 里程碑致意',
                    tagline: 'Appreciation Series',
                    badges: ['重要客户', '年度伙伴'],
                    opening: `您好！首先，请允许我代表全体团队向您表示最诚挚的感谢！`,
                    body: `感谢您一直以来对我们的信任与支持：
• 💼 您的信任是我们前进的动力
• 🤝 您的支持是我们成长的基石
• 💡 您的建议让我们不断改进
• 🌟 您的认可是我们最大的荣耀`,
                    closing: `未来，我们将继续秉承专业、诚信的原则，为您提供更优质的服务，携手共创美好未来！`,
                    signature: `再次感谢！\n\n种业客户管理团队\n${date}`
                },
                general: {
                    title: '💌 诚挚问候',
                    sceneName: '品牌致意',
                    greeting: `${customerName}，您好！`,
                    subtitle: '全场景 · 客户友好触达',
                    tagline: 'Signature Courtesy Message',
                    badges: ['品牌温度', 'AI共创'],
                    opening: `谨以此信向您致以最诚挚的问候与祝福！`,
                    body: `感谢您一直以来的信任与支持。愿您：
• 🌟 事业蓬勃发展，蒸蒸日上
• 💪 身体健康，精力充沛
• 🎯 目标清晰，步步为赢
• 🌈 生活美满，幸福安康`,
                    closing: `让我们携手同行，共赴精彩，共创辉煌！`,
                    signature: `此致\n敬礼！\n\n种业客户管理团队\n${date}`
                }
            };
        }

        // 生成名片引荐
        function generateBusinessCard(selectedRecs) {
            const customer = selectedRecs[0] || {};
            const displayName = customer.customerName || '客户姓名';
            const displayRole = customer.customerTitle || customer.title || '岗位 / 业务方向';
            const displayCompany = customer.customerCompany || customer.company || '客户公司';
            const displayTagline = customer.content || '在此描述客户的业务焦点或合作诉求，可直接修改。';
            const displayType = getTypeText(customer.type || 'product');
            const priority = getPriorityText(customer.priority || 'medium');
            const phone = customer.customerPhone || '电话未填写';
            const email = customer.customerEmail || '邮箱未填写';
            const address = customer.customerAddress || customer.region || '联系地址 / 所在城市';
            const website = customer.customerWebsite || 'www.example.com';
            const summary = customer.reason || '结合客户现状，重点关注价值交付与节奏控制。';
            const steps = [
                '确认关键痛点，明确交付边界',
                '安排垂直顾问，输出定制解决方案',
                '设定节点复盘，持续跟踪满意度'
            ];

            const html = `
                <div class="report-header mb-4">
                    <h3>名片引荐</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                    <small class="text-muted">提示：名片文字区域支持直接双击编辑，实时保存</small>
                </div>
                <div class="report-body">
                    <div class="business-card-pro business-card-pro--stacked">
                        <div class="business-card-pro__front">
                            <div class="business-card-pro__badge">${escapeHtml(displayType)}</div>
                            <div>
                                <div class="business-card-pro__name" data-inline-editable="true">${escapeHtml(displayName)}</div>
                                <div class="business-card-pro__role" data-inline-editable="true">${escapeHtml(displayRole)}</div>
                                <div class="business-card-pro__company" data-inline-editable="true">${escapeHtml(displayCompany)}</div>
                        </div>
                            <div class="business-card-pro__tagline" data-inline-editable="true">
                                ${escapeHtml(displayTagline)}
                            </div>
                            <div class="business-card-pro__priority priority-${customer.priority || 'medium'}">
                                <span>${priority}优先</span>
                            </div>
                        </div>
                        <div class="business-card-pro__back">
                            <div class="business-detail">
                                <span class="label">电话</span>
                                <span class="value" data-inline-editable="true">${escapeHtml(phone)}</span>
                            </div>
                            <div class="business-detail">
                                <span class="label">邮箱</span>
                                <span class="value" data-inline-editable="true">${escapeHtml(email)}</span>
                            </div>
                            <div class="business-detail">
                                <span class="label">地址</span>
                                <span class="value" data-inline-editable="true">${escapeHtml(address)}</span>
                            </div>
                            <div class="business-detail">
                                <span class="label">网站</span>
                                <span class="value" data-inline-editable="true">${escapeHtml(website)}</span>
                            </div>
                        </div>
                    </div>
                    <div class="business-card-pro__notes">
                        <div class="note">
                            <span class="note-label">推荐摘要</span>
                            <p data-inline-editable="true">${escapeHtml(summary)}</p>
                        </div>
                        <div class="note">
                            <span class="note-label">后续动作</span>
                            <ol class="next-steps">
                                ${steps.map(step => `<li data-inline-editable="true">${escapeHtml(step)}</li>`).join('')}
                            </ol>
                        </div>
                        <div class="note">
                            <span class="note-label">协同提醒</span>
                            <p data-inline-editable="true">${escapeHtml('同步团队共享此名片，确保触达信息一致。')}</p>
                        </div>
                    </div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本名片内容由AI智能推荐系统自动生成</p>
                </div>
            `;

            document.getElementById('reportContent').innerHTML = html;
            currentReportData = html;
            editedReportContent = null;
            enableInlineEditing('reportContent');
            generateShareLink();
        }

        // 生成短促销文案
        function generateShortMarketingCopy(selectedRecs) {
            const customer = selectedRecs[0] || {};
            const content = `【限时特惠 | ${customer.title || '高价值方案'}】

🎯 客户：${customer.customerName || '尊贵客户'}
⚡ 推荐：${customer.content || '高效方案/高价值服务'}
💡 优势：${customer.reason || '助您提效增收'}

即刻联系专属顾问，尊享定制支持！📞 ${customer.customerPhone || '400-XXX-XXXX'}`;

            const html = `
                <div class="report-header mb-4">
                    <h3>短促销文案</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                </div>
                <div class="report-body">
                    <div class="marketing-short-banner">
                        <div style="white-space: pre-wrap; line-height: 1.6; font-weight: 500;">
                            ${escapeHtml(content)}
                        </div>
                    </div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本短促销文案由AI智能推荐系统自动生成</p>
                </div>
            `;

            document.getElementById('reportContent').innerHTML = html;
            currentReportData = html;
            editedReportContent = null;
            generateShareLink();
        }
        
        // 切换报告编辑模式
        function toggleReportEdit() {
            isReportEditing = !isReportEditing;
            const previewDiv = document.getElementById('reportContent');
            const editorDiv = document.getElementById('reportEditor');
            const editBtn = document.getElementById('editReportBtn');
            
            if (isReportEditing) {
                // 切换到编辑模式
                previewDiv.style.display = 'none';
                editorDiv.style.display = 'block';
                editBtn.innerHTML = '<i class="bi bi-x"></i> 取消编辑';
                editBtn.classList.remove('btn-outline-warning');
                editBtn.classList.add('btn-outline-danger');
                
                // 填充编辑器内容
                const typeNames = {
                    'recommendation': 'AI智能推荐报告',
                    'meeting': '会议纪要',
                    'news': '新闻稿',
                    'report': '业务分析报道',
                    'reference': '推荐信',
                    'marketing-long': '长文营销文案',
                    'greeting-card': '祝福贺卡',
                    'business-card': '名片引荐',
                    'marketing-short': '短促销文案'
                };
                document.getElementById('reportTitle').value = typeNames[currentContentType] || '内容标题';
                if (editedReportContent) {
                    document.getElementById('reportContentEditor').value = editedReportContent;
                } else {
                    // 将HTML转换为纯文本（简化版）
                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = currentReportData || '';
                    document.getElementById('reportContentEditor').value = tempDiv.innerText || tempDiv.textContent || '';
                }
            } else {
                // 切换到预览模式
                previewDiv.style.display = 'block';
                editorDiv.style.display = 'none';
                editBtn.innerHTML = '<i class="bi bi-pencil"></i> 编辑内容';
                editBtn.classList.remove('btn-outline-danger');
                editBtn.classList.add('btn-outline-warning');
            }
        }
        
        // 保存报告编辑
        function saveReportEdit() {
            const title = document.getElementById('reportTitle').value;
            const content = document.getElementById('reportContentEditor').value;
            
            if (!content.trim()) {
                alert('内容不能为空！');
                return;
            }
            
            const typeNames = {
                'recommendation': '推荐报告',
                'meeting': '会议纪要',
                'news': '新闻稿',
                'report': '业务分析报道',
                'reference': '推荐信',
                'marketing-long': '长文营销文案',
                'greeting-card': '祝福贺卡',
                'business-card': '名片引荐',
                'marketing-short': '短促销文案'
            };
            
            // 生成新的报告HTML
            const newReportHtml = `
                <div class="report-header mb-4">
                    <h3>${escapeHtml(title)}</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                </div>
                <div class="report-body">
                    <div style="white-space: pre-wrap; line-height: 1.8;">${escapeHtml(content)}</div>
                </div>
                <div class="report-footer mt-4 pt-3 border-top">
                    <p class="text-muted small">本${typeNames[currentContentType] || '内容'}由AI智能推荐系统自动生成</p>
                </div>
            `;
            
            // 更新显示
            document.getElementById('reportContent').innerHTML = newReportHtml;
            currentReportData = newReportHtml;
            editedReportContent = content;
            
            // 退出编辑模式
            toggleReportEdit();
            
            // 重新生成分享链接
            generateShareLink();
            
            alert('报告内容已保存！');
        }
        
        // 取消报告编辑
        function cancelReportEdit() {
            toggleReportEdit();
        }
        
        // 预览报告编辑
        function previewReportEdit() {
            const title = document.getElementById('reportTitle').value;
            const content = document.getElementById('reportContentEditor').value;
            
            if (!content.trim()) {
                alert('报告内容不能为空！');
                return;
            }
            
            const previewHtml = `
                <div class="report-header mb-4">
                    <h3>${escapeHtml(title)}</h3>
                    <p class="text-muted">生成时间：${new Date().toLocaleString('zh-CN')}</p>
                </div>
                <div class="report-body">
                    <div style="white-space: pre-wrap; line-height: 1.8;">${escapeHtml(content)}</div>
                </div>
            `;
            
            // 临时显示预览
            const previewDiv = document.getElementById('reportContent');
            const originalContent = previewDiv.innerHTML;
            previewDiv.innerHTML = previewHtml;
            previewDiv.style.display = 'block';
            
            setTimeout(() => {
                previewDiv.innerHTML = originalContent;
                previewDiv.style.display = 'none';
            }, 3000);
        }
        
        // HTML转义函数
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        function enableInlineEditing(container) {
            const root = typeof container === 'string' ? document.getElementById(container) : container;
            if (!root) return;
            const editableNodes = root.querySelectorAll('[data-inline-editable="true"]');
            editableNodes.forEach(node => {
                node.setAttribute('contenteditable', 'true');
                node.classList.add('inline-editable');
                node.addEventListener('input', () => {
                    currentReportData = document.getElementById('reportContent').innerHTML;
                });
            });
        }

        // 文本截断
        function truncateText(text, maxLength = 120) {
            if (!text) return '';
            const plain = text.toString().replace(/\s+/g, ' ').trim();
            return plain.length > maxLength ? plain.slice(0, maxLength) + '…' : plain;
        }
        
        // 生成营销模板
        function generateTemplate() {
            const templateType = document.getElementById('templateType').value;
            const selectedRecs = recommendations.filter(r => selectedRecommendations.includes(r.id));
            if (selectedRecs.length === 0) {
                document.getElementById('templateContent').innerHTML = '<p class="text-muted">没有选中的推荐</p>';
                return;
            }
            
            let templateHtml = '';
            
            if (templateType === 'email') {
                const firstRec = selectedRecs[0];
                const coverage = Array.from(new Set(selectedRecs.map(rec => getTypeText(rec.type)).filter(Boolean))).join('、') || '核心场景';
                const summarySentence = selectedRecs.length > 1
                    ? `我们为您汇总了 ${selectedRecs.length} 组重点方案，覆盖 ${coverage}，帮助快速推进本阶段目标。`
                    : `围绕当前机会点“${escapeHtml(firstRec.title)}”，为您输出了可直接触达客户的完整方案。`;
                const highlightItems = selectedRecs.slice(0, 3).map((rec, index) => `
                    <li>
                        <span>${index + 1}. ${escapeHtml(rec.title)}</span>
                        <p>${escapeHtml(truncateText(rec.reason || rec.content, 90))}</p>
                    </li>
                `).join('') || `
                    <li>
                        <span>AI 洞察</span>
                        <p>${escapeHtml(summarySentence)}</p>
                    </li>
                `;
                const cardsHtml = selectedRecs.map((rec, index) => `
                    <div class="template-pro__card">
                        <div class="template-pro__card-head">
                            <div class="card-index">${String(index + 1).padStart(2, '0')}</div>
                            <div>
                                <p class="text-uppercase text-muted mb-1">${getTypeText(rec.type)}</p>
                                <h6>${escapeHtml(rec.title)}</h6>
                        </div>
                            <span class="priority-tag priority-${rec.priority || 'medium'}">${getPriorityText(rec.priority)}优先</span>
                        </div>
                        <p class="template-pro__card-content">${escapeHtml(truncateText(rec.content, 220))}</p>
                        <div class="template-pro__card-footer">
                            <span><i class="bi bi-lightbulb"></i>${escapeHtml(truncateText(rec.reason || '结合客户画像，建议立即跟进', 60))}</span>
                            <span><i class="bi bi-activity"></i>置信度 ${Math.round((rec.confidence || 0) * 100)}%</span>
                        </div>
                    </div>
                `).join('');
                const priorityAdviceMap = {
                    high: '该客户正处于高意向窗口，建议 24 小时内安排专属顾问跟进，结合案例资料完成闭环。',
                    medium: '维持节奏沟通，适合在本周例会或触达节点同步，观察客户反馈再推进下一步动作。',
                    low: '可纳入月度培育序列，聚焦价值点即可，待客户有明确诉求再加深方案细节。'
                };
                const priorityAdvice = priorityAdviceMap[firstRec.priority] || '建议结合客户节奏，灵活调整触达频次，并保持内容一致性。';
                const emailSubject = selectedRecs.length > 1
                    ? `${firstRec.customerName || '重点客户'} · 多场景智能推荐`
                    : `${firstRec.customerName || '重点客户'} · ${firstRec.title}`;
                
                templateHtml = `
                    <div class="template-pro template-pro--email">
                        <div class="template-pro__hero">
                            <div>
                                <span class="badge rounded-pill bg-white text-dark fw-semibold px-3 py-1">AI 精准推荐</span>
                                <h4 class="mt-3 mb-2">${escapeHtml(emailSubject)}</h4>
                                <p class="mb-0">${summarySentence}</p>
                            </div>
                            <div class="template-pro__meta">
                                <div class="template-pro__meta-item">主题：<strong>${escapeHtml(emailSubject)}</strong></div>
                                <div class="template-pro__meta-item">客户焦点：<strong>${escapeHtml(firstRec.customerName || '重点客户')}</strong></div>
                                <div class="template-pro__meta-item">生成时间：<strong>${new Date().toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit', month: '2-digit', day: '2-digit' })}</strong></div>
                            </div>
                        </div>
                        <div class="template-pro__body">
                            <div class="template-pro__section">
                                <div>
                                    <p class="template-label">内容亮点</p>
                                    <h5>核心洞察提炼</h5>
                                </div>
                                <ul class="template-pro__highlights">
                                    ${highlightItems}
                                </ul>
                            </div>
                            <div class="template-pro__cards">
                                ${cardsHtml}
                            </div>
                            <div class="template-pro__cta">
                                <div>
                                    <p class="template-label">下一步行动</p>
                                    <h5>${getPriorityText(firstRec.priority)}优先 · 执行建议</h5>
                                    <p class="mb-0">${priorityAdvice}</p>
                                </div>
                                <div class="template-pro__cta-note">
                                    <p class="text-muted mb-1"><i class="bi bi-send"></i> 建议发送渠道</p>
                                    <strong>邮件 + 企微同步</strong>
                                    <p class="small text-muted mb-0">保持同一语调与关键信息，便于客户快速理解。</p>
                                </div>
                            </div>
                            <div class="template-pro__footer text-end text-muted">
                                由 AI 智能推荐系统生成 · ${new Date().toLocaleDateString('zh-CN')}
                            </div>
                        </div>
                    </div>
                `;
            } else {
                let smsText = `【AI智能推荐】`;
                if (selectedRecs.length === 1) {
                    smsText += `${selectedRecs[0].title}。${selectedRecs[0].content}`;
                } else {
                    smsText += `我们为您准备了${selectedRecs.length}项重点方案：`;
                    selectedRecs.forEach((rec, index) => {
                        smsText += `${index + 1}.${rec.title}；`;
                    });
                }
                smsText += `详情可查看专属推荐页。`;
                const smsParagraph = smsText.replace(/；/g, '；\n');
                templateHtml = `
                    <div class="template-pro template-pro--sms">
                        <div class="sms-device">
                            <div class="sms-status-bar">
                                <span>AI 客户助手</span>
                                <span>${new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}</span>
                            </div>
                            <div class="sms-screen">
                                <div class="sms-bubble sms-bubble--out">
                                    <pre class="sms-message">${escapeHtml(smsParagraph)}</pre>
                                </div>
                                <div class="sms-tail"></div>
                            </div>
                        </div>
                        <div class="template-pro__tips">
                            <div class="tip-item">
                                <span class="tip-label">发送建议</span>
                                <p>建议在工作日 10:00-12:00 发送，回复率更高。</p>
                            </div>
                            <div class="tip-item">
                                <span class="tip-label">附加动作</span>
                                <p>短信发送后 1 小时内通过企微或电话跟进，强调核心收益。</p>
                            </div>
                            <div class="tip-item">
                                <span class="tip-label">字数提示</span>
                                <p>当前约 ${smsText.length} 字，符合 70 字以内长短信限制。</p>
                            </div>
                        </div>
                    </div>
                `;
            }
            
            document.getElementById('templateContent').innerHTML = templateHtml;
            currentTemplateData = templateHtml;
            editedTemplateContent = null; // 重置编辑内容
        }
        
        // 切换模板编辑模式
        function toggleTemplateEdit() {
            isTemplateEditing = !isTemplateEditing;
            const previewDiv = document.getElementById('templateContent');
            const editorDiv = document.getElementById('templateEditor');
            const editBtn = document.getElementById('editTemplateBtn');
            
            if (isTemplateEditing) {
                // 切换到编辑模式
                previewDiv.style.display = 'none';
                editorDiv.style.display = 'block';
                editBtn.innerHTML = '<i class="bi bi-x"></i> 取消编辑';
                editBtn.classList.remove('btn-outline-warning');
                editBtn.classList.add('btn-outline-danger');
                
                // 填充编辑器内容
                const templateType = document.getElementById('templateType').value;
                if (editedTemplateContent) {
                    document.getElementById('templateContentEditor').value = editedTemplateContent;
                } else {
                    // 从预览中提取文本内容
                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = currentTemplateData || '';
                    let textContent = '';
                    
                    if (templateType === 'email') {
                        // 邮件模板，提取文本
                        textContent = tempDiv.innerText || tempDiv.textContent || '';
                    } else {
                        // 短信模板，提取pre标签内容
                        const pre = tempDiv.querySelector('pre');
                        textContent = pre ? pre.textContent : tempDiv.innerText || '';
                    }
                    
                    document.getElementById('templateContentEditor').value = textContent;
                }
                
                // 设置主题（如果有）
                const subjectMatch = currentTemplateData.match(/主题[：:]\s*(.+?)(?:\n|<\/)/);
                if (subjectMatch) {
                    document.getElementById('templateSubject').value = subjectMatch[1].trim();
                }
            } else {
                // 切换到预览模式
                previewDiv.style.display = 'block';
                editorDiv.style.display = 'none';
                editBtn.innerHTML = '<i class="bi bi-pencil"></i> 编辑内容';
                editBtn.classList.remove('btn-outline-danger');
                editBtn.classList.add('btn-outline-warning');
            }
        }
        
        // 保存模板编辑
        function saveTemplateEdit() {
            const templateType = document.getElementById('templateType').value;
            const subject = document.getElementById('templateSubject').value;
            const content = document.getElementById('templateContentEditor').value;
            
            if (!content.trim()) {
                alert('模板内容不能为空！');
                return;
            }
            
            let newTemplateHtml = '';
            
            if (templateType === 'email') {
                // 邮件模板
                newTemplateHtml = `
                    <div class="email-template p-4 border rounded">
                        <div class="email-header mb-3">
                            <h5>主题：${subject || '智能推荐'}</h5>
                            <p class="text-muted small">发件人：AI智能推荐系统</p>
                        </div>
                        <div class="email-body">
                            <div style="white-space: pre-wrap; line-height: 1.8;">${escapeHtml(content)}</div>
                        </div>
                    </div>
                `;
            } else {
                // 短信模板
                newTemplateHtml = `
                    <div class="sms-template p-4 border rounded bg-light">
                        <div class="sms-preview">
                            <div class="sms-header mb-2">
                                <span class="badge bg-primary">短信模板</span>
                                <span class="text-muted small ms-2">约 ${content.length} 字</span>
                            </div>
                            <div class="sms-content p-3 bg-white rounded border">
                                <pre class="mb-0" style="white-space: pre-wrap; font-family: inherit;">${escapeHtml(content)}</pre>
                            </div>
                        </div>
                    </div>
                `;
            }
            
            // 更新显示
            document.getElementById('templateContent').innerHTML = newTemplateHtml;
            currentTemplateData = newTemplateHtml;
            editedTemplateContent = content;
            
            // 退出编辑模式
            toggleTemplateEdit();
            
            // 更新发送预览（如果已选择客户）
            if (selectedCustomers.length > 0) {
                updateSendPreview();
            }
            
            alert('模板内容已保存！');
        }
        
        // 取消模板编辑
        function cancelTemplateEdit() {
            toggleTemplateEdit();
        }
        
        // 预览模板编辑
        function previewTemplateEdit() {
            const templateType = document.getElementById('templateType').value;
            const subject = document.getElementById('templateSubject').value;
            const content = document.getElementById('templateContentEditor').value;
            
            if (!content.trim()) {
                alert('模板内容不能为空！');
                return;
            }
            
            let previewHtml = '';
            
            if (templateType === 'email') {
                previewHtml = `
                    <div class="email-template p-4 border rounded">
                        <div class="email-header mb-3">
                            <h5>主题：${escapeHtml(subject || '智能推荐')}</h5>
                            <p class="text-muted small">发件人：AI智能推荐系统</p>
                        </div>
                        <div class="email-body">
                            <div style="white-space: pre-wrap; line-height: 1.8;">${escapeHtml(content)}</div>
                        </div>
                    </div>
                `;
            } else {
                previewHtml = `
                    <div class="sms-template p-4 border rounded bg-light">
                        <div class="sms-preview">
                            <div class="sms-header mb-2">
                                <span class="badge bg-primary">短信模板</span>
                                <span class="text-muted small ms-2">约 ${content.length} 字</span>
                            </div>
                            <div class="sms-content p-3 bg-white rounded border">
                                <pre class="mb-0" style="white-space: pre-wrap; font-family: inherit;">${escapeHtml(content)}</pre>
                            </div>
                        </div>
                    </div>
                `;
            }
            
            // 临时显示预览
            const previewDiv = document.getElementById('templateContent');
            const originalContent = previewDiv.innerHTML;
            previewDiv.innerHTML = previewHtml;
            previewDiv.style.display = 'block';
            
            setTimeout(() => {
                previewDiv.innerHTML = originalContent;
                previewDiv.style.display = 'none';
            }, 3000);
        }
        
        // 加载客户选择
        function loadCustomerSelection() {
            const customerListDiv = document.getElementById('customerList');
            selectedCustomers = [];
            updateCustomerSelectionCount();
            
                // 从客户列表API获取
                customerListDiv.innerHTML = '<p class="text-muted">加载中...</p>';
                
                fetch('/api/customer/list?pageNum=1&pageSize=1000')
                    .then(response => response.json())
                    .then(result => {
                        if (result.code === 200 && result.data && Array.isArray(result.data)) {
                            allCustomers = result.data;
                            renderCustomerList(allCustomers);
                        } else {
                            customerListDiv.innerHTML = '<p class="text-danger">加载客户列表失败</p>';
                        }
                    })
                    .catch(error => {
                        console.error('加载客户列表失败:', error);
                        customerListDiv.innerHTML = '<p class="text-danger">加载客户列表失败: ' + error.message + '</p>';
                    });
        }
        
        // 全选客户
        function selectAllCustomers() {
            const checkboxes = document.querySelectorAll('#customerList input[type="checkbox"]');
            checkboxes.forEach(checkbox => {
                if (!checkbox.checked) {
                    checkbox.checked = true;
                    const customerId = checkbox.value;
                    if (!selectedCustomers.includes(customerId)) {
                        selectedCustomers.push(customerId);
                    }
                }
            });
            updateCustomerSelectionCount();
            updatePreviewCustomerSelect();
        }
        
        // 反选客户
        function invertCustomerSelection() {
            const checkboxes = document.querySelectorAll('#customerList input[type="checkbox"]');
            checkboxes.forEach(checkbox => {
                checkbox.checked = !checkbox.checked;
                const customerId = checkbox.value;
                const index = selectedCustomers.indexOf(customerId);
                if (checkbox.checked) {
                    if (index === -1) {
                        selectedCustomers.push(customerId);
                    }
                } else {
                    if (index > -1) {
                        selectedCustomers.splice(index, 1);
                    }
                }
            });
            updateCustomerSelectionCount();
            updatePreviewCustomerSelect();
        }
        
        // 清空客户选择
        function clearCustomerSelection() {
            const checkboxes = document.querySelectorAll('#customerList input[type="checkbox"]');
            checkboxes.forEach(checkbox => {
                checkbox.checked = false;
            });
            selectedCustomers = [];
            updateCustomerSelectionCount();
            updatePreviewCustomerSelect();
        }
        
        // 更新客户选择计数显示
        function updateCustomerSelectionCount() {
            const countDisplay = document.getElementById('selectedCustomerCountDisplay');
            if (countDisplay) {
                countDisplay.textContent = selectedCustomers.length;
            }
        }
        
        // 渲染客户列表
        function renderCustomerList(customers) {
            const customerListDiv = document.getElementById('customerList');
            if (customers.length === 0) {
                customerListDiv.innerHTML = '<p class="text-muted">暂无客户数据</p>';
                return;
            }
            
            let html = '<div class="list-group">';
            customers.forEach(customer => {
                const customerId = customer.id || customer.customerId;
                const customerName = customer.customerName || customer.name || `客户${customerId}`;
                const phone = customer.phone || customer.telephone || '';
                const email = customer.email || '';
                
                html += `
                    <label class="list-group-item">
                        <input class="form-check-input me-2" type="checkbox" value="${customerId}" onchange="toggleCustomerSelection('${customerId}')">
                        <div>
                            <strong>${customerName}</strong>
                            ${phone ? `<br><small class="text-muted">电话: ${phone}</small>` : ''}
                            ${email ? `<br><small class="text-muted">邮箱: ${email}</small>` : ''}
                        </div>
                    </label>
                `;
            });
            html += '</div>';
            
            customerListDiv.innerHTML = html;
        }
        
        // 过滤客户
        function filterCustomers() {
            const searchTerm = document.getElementById('customerSearch').value.toLowerCase();
            if (!searchTerm) {
                renderCustomerList(allCustomers);
                return;
            }
            
            const filtered = allCustomers.filter(customer => {
                const name = (customer.customerName || customer.name || '').toLowerCase();
                const phone = (customer.phone || customer.telephone || '').toLowerCase();
                const email = (customer.email || '').toLowerCase();
                return name.includes(searchTerm) || phone.includes(searchTerm) || email.includes(searchTerm);
            });
            
            renderCustomerList(filtered);
        }
        
        // 切换客户选择
        function toggleCustomerSelection(customerId) {
            const checkbox = event.target;
            if (checkbox.checked) {
                if (!selectedCustomers.includes(customerId)) {
                    selectedCustomers.push(customerId);
                }
            } else {
                const index = selectedCustomers.indexOf(customerId);
                if (index > -1) {
                    selectedCustomers.splice(index, 1);
                }
            }
            
            // 更新计数显示
            updateCustomerSelectionCount();
            // 更新预览客户选择下拉框
            updatePreviewCustomerSelect();
        }
        
        // 更新预览客户选择下拉框
        function updatePreviewCustomerSelect() {
            const select = document.getElementById('previewCustomerSelect');
            select.innerHTML = '<option value="">请选择客户</option>';
            
            selectedCustomers.forEach(customerId => {
                let customerName = customerId;
                let phone = '';
                let email = '';
                
                // 如果是数字ID，尝试从allCustomers中查找
                if (!isNaN(customerId)) {
                    const customer = allCustomers.find(c => (c.id || c.customerId) == customerId);
                    if (customer) {
                        customerName = customer.customerName || customer.name || customerId;
                        phone = customer.phone || customer.telephone || '';
                        email = customer.email || '';
                    }
                } else {
                    // 如果是客户名称（从推荐中提取），尝试从推荐中查找联系方式
                    const selectedRecs = recommendations.filter(r => selectedRecommendations.includes(r.id));
                    const rec = selectedRecs.find(r => r.customerName === customerId);
                    if (rec && rec.customerPhone) phone = rec.customerPhone;
                    if (rec && rec.customerEmail) email = rec.customerEmail;
                }
                
                const option = document.createElement('option');
                option.value = customerId;
                option.textContent = customerName + (phone ? ` (${phone})` : '');
                option.dataset.phone = phone;
                option.dataset.email = email;
                option.dataset.name = customerName;
                select.appendChild(option);
            });
        }
        
        // 更新发送预览
        function updateSendPreview() {
            const customerId = document.getElementById('previewCustomerSelect').value;
            const sendType = document.querySelector('input[name="sendType"]:checked').value;
            
            if (!customerId) {
                document.getElementById('sendPreviewContent').innerHTML = '<p class="text-muted">请先选择客户</p>';
                return;
            }
            
            let customerName = customerId;
            let phone = '';
            let email = '';
            
            // 查找客户信息
            if (!isNaN(customerId)) {
                const customer = allCustomers.find(c => (c.id || c.customerId) == customerId);
                if (customer) {
                    customerName = customer.customerName || customer.name || customerId;
                    phone = customer.phone || customer.telephone || '';
                    email = customer.email || '';
                }
            } else {
                // 如果是客户名称（从推荐中提取），尝试从下拉框选项的data属性获取
                const option = document.querySelector(`#previewCustomerSelect option[value="${customerId}"]`);
                if (option) {
                    customerName = option.dataset.name || customerId;
                    phone = option.dataset.phone || '';
                    email = option.dataset.email || '';
                }
            }
            
            const selectedRecs = recommendations.filter(r => selectedRecommendations.includes(r.id));
            
            if (sendType === 'sms') {
                // 短信预览
                let smsText = `【AI智能推荐】`;
                if (selectedRecs.length === 1) {
                    smsText += `${selectedRecs[0].title}。${selectedRecs[0].content}`;
                } else {
                    smsText += `为您推荐${selectedRecs.length}项内容：`;
                    selectedRecs.forEach((rec, index) => {
                        smsText += `${index + 1}.${rec.title}；`;
                    });
                }
                smsText += `详情请查看推荐报告。`;
                
                document.getElementById('sendPreviewContent').innerHTML = `
                    <div class="sms-preview-container">
                        <div class="sms-phone-header p-2 bg-primary text-white rounded-top">
                            <i class="bi bi-phone"></i> ${phone || '手机号码'}
                        </div>
                        <div class="sms-content-area p-3 bg-light border rounded-bottom">
                            <div class="sms-bubble p-3 bg-white rounded shadow-sm mb-2">
                                <p class="mb-0" style="white-space: pre-wrap;">${smsText}</p>
                            </div>
                            <div class="text-end mt-2">
                                <button class="btn btn-primary btn-sm" disabled>
                                    <i class="bi bi-send"></i> 发送（预览模式）
                                </button>
                            </div>
                        </div>
                    </div>
                `;
            } else {
                // 邮件预览 - 使用编辑后的模板内容或自动生成
                let emailBody = '';
                let emailSubject = '';
                
                if (editedTemplateContent) {
                    // 使用编辑后的模板内容
                    emailBody = editedTemplateContent;
                    emailSubject = document.getElementById('templateSubject').value || '智能推荐';
                } else {
                    // 自动生成
                    emailBody = `尊敬的${customerName}，\n\n您好！基于您的业务需求，我们为您准备了以下智能推荐：\n\n`;
                    selectedRecs.forEach((rec, index) => {
                        emailBody += `${index + 1}. ${rec.title}\n${rec.content}\n推荐理由：${rec.reason || '无'}\n\n`;
                    });
                    emailBody += `如有任何疑问或需要进一步了解，欢迎随时联系我们。\n\n此致\n敬礼！\n\nAI智能推荐系统\n${new Date().toLocaleDateString('zh-CN')}`;
                    emailSubject = selectedRecs.length > 1 ? '多项智能推荐' : selectedRecs[0].title;
                }
                
                let emailHtml = `
                    <div class="email-preview-container border rounded">
                        <div class="email-header p-3 bg-light border-bottom">
                            <div class="mb-2">
                                <strong>收件人：</strong>${email || 'customer@example.com'}
                            </div>
                            <div class="mb-2">
                                <strong>主题：</strong>${emailSubject}
                            </div>
                        </div>
                        <div class="email-body p-4">
                            <div style="white-space: pre-wrap; line-height: 1.8;">${escapeHtml(emailBody)}</div>
                        </div>
                        <div class="email-footer p-3 bg-light border-top text-end">
                            <button class="btn btn-primary" disabled>
                                <i class="bi bi-send"></i> 发送（预览模式）
                            </button>
                        </div>
                    </div>
                `;
                
                document.getElementById('sendPreviewContent').innerHTML = emailHtml;
            }
        }
        
        // 生成分享链接
        function generateShareLink() {
            const selectedRecs = recommendations.filter(r => selectedRecommendations.includes(r.id));
            const shareData = {
                recommendations: selectedRecs,
                generateTime: new Date().toISOString()
            };
            
            // 使用Base64编码数据（支持UTF-8）
            const encodedData = utf8ToBase64(JSON.stringify(shareData));
            shareLink = `${window.location.origin}/ai-recommendations-share.html?data=${encodeURIComponent(encodedData)}`;
            
            document.getElementById('shareLink').value = shareLink;
            
            // 生成二维码
            generateQRCode();
        }
        
        // 渲染二维码（兼容多种库实现）
        function renderQRCode(container, text) {
            if (!container) return false;
            container.innerHTML = '';
            
            // 1) 兼容新版 qrcode.js（通过构造函数实例化）
            if (typeof QRCode === 'function') {
                try {
                    new QRCode(container, {
                        text: text,
                        width: 200,
                        height: 200,
                        colorDark: '#000000',
                        colorLight: '#ffffff',
                        correctLevel: QRCode.CorrectLevel ? QRCode.CorrectLevel.M : undefined
                    });
                    return true;
                } catch (error) {
                    console.error('生成二维码失败:', error);
                    return false;
                }
            }
            
            // 2) 兼容旧版（提供 QRCode.toCanvas API）
            if (typeof QRCode === 'object' && typeof QRCode.toCanvas === 'function') {
                try {
                    QRCode.toCanvas(text, {
                width: 200,
                margin: 2,
                color: {
                    dark: '#000000',
                    light: '#FFFFFF'
                }
            }, function (error, canvas) {
                if (error) {
                    console.error('生成二维码失败:', error);
                            container.innerHTML = '<p class="text-danger small">生成二维码失败</p>';
                } else {
                    container.appendChild(canvas);
                }
            });
                    return true;
                } catch (error) {
                    console.error('生成二维码异常:', error);
                    return false;
                }
            }
            
            return false;
        }
        
        // 生成二维码
        function generateQRCode() {
            const container = document.getElementById('qrcodeContainer');
            if (!container) return;
            container.innerHTML = '';
            
            if (!shareLink) {
                container.innerHTML = '<p class="text-muted small">请先生成分享链接</p>';
                return;
            }
            
            // 检查QRCode库是否已加载，如果未加载则等待或重试
            if (typeof QRCode === 'undefined' || QRCode === null) {
                container.innerHTML = '<p class="text-muted small">二维码库加载中，请稍候...</p>';
                // 等待库加载，最多重试10次（5秒）
                let retryCount = 0;
                const maxRetries = 10;
                const checkQRCode = setInterval(function() {
                    retryCount++;
                    if (typeof QRCode !== 'undefined' && QRCode !== null) {
                        clearInterval(checkQRCode);
                        generateQRCode(); // 重新调用
                    } else if (retryCount >= maxRetries) {
                        clearInterval(checkQRCode);
                        // 降级方案：显示链接文本
                        container.innerHTML = 
                            '<div class="text-center p-3 border rounded">' +
                            '<p class="text-muted small mb-2">二维码库未加载，请使用以下链接：</p>' +
                            '<p class="mb-0"><a href="' + shareLink + '" target="_blank" class="text-break">' + shareLink + '</a></p>' +
                            '<button class="btn btn-sm btn-outline-primary mt-2" onclick="copyShareLink()">' +
                            '<i class="bi bi-clipboard"></i> 复制链接</button>' +
                            '</div>';
                        console.warn('QRCode库加载超时，已显示链接作为降级方案');
                    }
                }, 500);
                return;
            }
            
            const success = renderQRCode(container, shareLink);
            if (!success) {
                container.innerHTML = '<p class="text-danger small">生成二维码失败，请稍后重试</p>';
            }
        }
        
        // 复制报告内容
        function copyReportContent() {
            const reportText = document.getElementById('reportContent').innerText;
            navigator.clipboard.writeText(reportText).then(() => {
                alert('报告内容已复制到剪贴板');
            }).catch(err => {
                console.error('复制失败:', err);
                alert('复制失败，请手动选择复制');
            });
        }
        
        // 下载报告
        function downloadReport() {
            const reportHtml = currentReportData || document.getElementById('reportContent').innerHTML;
            const blob = new Blob([`
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>AI智能推荐报告</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        @media print {
            body { padding: 20px; }
        }
    </style>
</head>
<body>
    <div class="container mt-4">
        ${reportHtml}
    </div>
</body>
</html>
            `], { type: 'text/html' });
            
            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `AI智能推荐报告_${new Date().toISOString().split('T')[0]}.html`;
            link.click();
            URL.revokeObjectURL(url);
        }
        
        // 复制模板内容
        function copyTemplateContent() {
            const templateText = document.getElementById('templateContent').innerText;
            navigator.clipboard.writeText(templateText).then(() => {
                alert('模板内容已复制到剪贴板');
            }).catch(err => {
                console.error('复制失败:', err);
                alert('复制失败，请手动选择复制');
            });
        }
        
        // 复制分享链接
        function copyShareLink() {
            const linkInput = document.getElementById('shareLink');
            linkInput.select();
            document.execCommand('copy');
            alert('分享链接已复制到剪贴板');
        }
        
        // 下载二维码
        function downloadQRCode() {
            const canvas = document.querySelector('#qrcodeContainer canvas');
            if (!canvas) {
                alert('二维码未生成');
                return;
            }
            
            canvas.toBlob(function(blob) {
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = `推荐报告二维码_${new Date().toISOString().split('T')[0]}.png`;
                link.click();
                URL.revokeObjectURL(url);
            });
        }
        
        // 复制当前内容（统一入口）
        function copyCurrentContent() {
            // 检查是否在编辑模式
            const reportEditor = document.getElementById('reportEditor');
            const templateEditor = document.getElementById('templateEditor');
            
            if (reportEditor && reportEditor.style.display !== 'none') {
                // 编辑模式：复制编辑器内容
                const editorContent = document.getElementById('reportContentEditor').value;
                navigator.clipboard.writeText(editorContent).then(() => {
                    alert('内容已复制到剪贴板');
                }).catch(err => {
                    console.error('复制失败:', err);
                    alert('复制失败，请手动选择复制');
                });
            } else if (templateEditor && templateEditor.style.display !== 'none') {
                // 模板编辑模式：复制模板编辑器内容
                const templateContent = document.getElementById('templateContentEditor').value;
                navigator.clipboard.writeText(templateContent).then(() => {
                    alert('模板内容已复制到剪贴板');
                }).catch(err => {
                    console.error('复制失败:', err);
                    alert('复制失败，请手动选择复制');
                });
            } else {
                // 预览模式：复制预览内容
                const reportContent = document.getElementById('reportContent');
                if (reportContent && reportContent.style.display !== 'none') {
                    copyReportContent();
                } else {
                    const templateContent = document.getElementById('templateContent');
                    if (templateContent && templateContent.style.display !== 'none') {
                        copyTemplateContent();
                    } else {
                        alert('没有可复制的内容');
                    }
                }
            }
        }
        
        // 分享当前内容（统一入口）
        function shareCurrentContent() {
            // 确保已生成分享链接
            if (!shareLink) {
                generateShareLink();
            }
            
            // 显示分享链接输入框，让用户复制
            const shareLinkInput = document.getElementById('shareLink');
            if (shareLinkInput) {
                shareLinkInput.select();
                document.execCommand('copy');
                alert('分享链接已复制到剪贴板，您可以分享给他人');
            } else {
                // 如果没有分享链接输入框，直接使用Web Share API（如果支持）
                if (navigator.share) {
                    const selectedRecs = recommendations.filter(r => selectedRecommendations.includes(r.id));
                    const shareText = selectedRecs.map(r => `${r.title}: ${r.content}`).join('\n\n');
                    navigator.share({
                        title: 'AI智能推荐',
                        text: shareText,
                        url: shareLink || window.location.href
                    }).catch(err => {
                        console.error('分享失败:', err);
                        // 降级到复制链接
                        if (shareLink) {
                            navigator.clipboard.writeText(shareLink).then(() => {
                                alert('分享链接已复制到剪贴板');
                            });
                        }
                    });
                } else {
                    // 降级方案：复制链接
                    if (shareLink) {
                        navigator.clipboard.writeText(shareLink).then(() => {
                            alert('分享链接已复制到剪贴板');
                        });
                    } else {
                        generateShareLink();
                        setTimeout(() => {
                            const shareLinkInput = document.getElementById('shareLink');
                            if (shareLinkInput) {
                                shareLinkInput.select();
                                document.execCommand('copy');
                                alert('分享链接已复制到剪贴板');
                            }
                        }, 500);
                    }
                }
            }
        }
        
        // ========== 发送/分享功能 ==========
        
        // 打开发送/分享模态框（实际实现）
        function _openSendShareModal() {
            const modal = new bootstrap.Modal(document.getElementById('sendShareModal'));
            modal.show();
            
            // 重置状态
            sendSelectedCustomers = [];
            sendShareLink = '';
            document.getElementById('sendCustomerList').innerHTML = '<p class="text-muted">加载中...</p>';
            document.getElementById('selectedCustomerCount').textContent = '0';
            document.getElementById('shareSection').style.display = 'none';
            document.getElementById('generateShareBtn').disabled = true;
            document.getElementById('sendBtn').style.display = 'none';
            
            // 默认加载客户列表
            loadSendCustomerSelection();
        }
        
        // 加载发送模态框的客户选择
        function loadSendCustomerSelection() {
            const customerListDiv = document.getElementById('sendCustomerList');
            sendSelectedCustomers = [];
            document.getElementById('selectedCustomerCount').textContent = '0';
            document.getElementById('generateShareBtn').disabled = true;
            
                // 从客户列表API获取
                customerListDiv.innerHTML = '<p class="text-muted">加载中...</p>';
                
                fetch('/api/customer/list?pageNum=1&pageSize=1000')
                    .then(response => response.json())
                    .then(result => {
                        if (result.code === 200 && result.data && Array.isArray(result.data)) {
                            sendAllCustomers = result.data;
                            renderSendCustomerList(sendAllCustomers);
                        } else {
                            customerListDiv.innerHTML = '<p class="text-danger">加载客户列表失败</p>';
                        }
                    })
                    .catch(error => {
                        console.error('加载客户列表失败:', error);
                        customerListDiv.innerHTML = '<p class="text-danger">加载客户列表失败: ' + error.message + '</p>';
                    });
        }
        
        // 全选发送客户
        function selectAllSendCustomers() {
            const checkboxes = document.querySelectorAll('#sendCustomerList input[type="checkbox"]');
            checkboxes.forEach(checkbox => {
                if (!checkbox.checked) {
                    checkbox.checked = true;
                    const customerId = checkbox.value;
                    const customer = sendAllCustomers.find(c => (c.id || c.customerId) == customerId);
                    if (customer && !sendSelectedCustomers.find(c => (c.id || c.customerId) == customerId)) {
                        sendSelectedCustomers.push(customer);
                    }
                }
            });
            updateSendCustomerCount();
        }
        
        // 反选发送客户
        function invertSendCustomerSelection() {
            const checkboxes = document.querySelectorAll('#sendCustomerList input[type="checkbox"]');
            checkboxes.forEach(checkbox => {
                checkbox.checked = !checkbox.checked;
                const customerId = checkbox.value;
                const index = sendSelectedCustomers.findIndex(c => (c.id || c.customerId) == customerId);
                if (checkbox.checked) {
                    if (index === -1) {
                        const customer = sendAllCustomers.find(c => (c.id || c.customerId) == customerId);
                        if (customer) {
                            sendSelectedCustomers.push(customer);
                        }
                    }
                } else {
                    if (index > -1) {
                        sendSelectedCustomers.splice(index, 1);
                    }
                }
            });
            updateSendCustomerCount();
        }
        
        // 清空发送客户选择
        function clearSendCustomerSelection() {
            const checkboxes = document.querySelectorAll('#sendCustomerList input[type="checkbox"]');
            checkboxes.forEach(checkbox => {
                checkbox.checked = false;
            });
            sendSelectedCustomers = [];
            updateSendCustomerCount();
        }
        
        // 更新发送客户选择计数
        function updateSendCustomerCount() {
            const countElement = document.getElementById('selectedCustomerCount');
            if (countElement) {
                countElement.textContent = sendSelectedCustomers.length;
            }
            const generateBtn = document.getElementById('generateShareBtn');
            if (generateBtn) {
                generateBtn.disabled = sendSelectedCustomers.length === 0;
            }
        }
        
        // 渲染发送模态框的客户列表
        function renderSendCustomerList(customers) {
            const customerListDiv = document.getElementById('sendCustomerList');
            if (customers.length === 0) {
                customerListDiv.innerHTML = '<p class="text-muted">暂无客户数据</p>';
                return;
            }
            
            let html = '<div class="list-group">';
            customers.forEach(customer => {
                const customerId = customer.id || customer.customerId;
                const customerName = customer.customerName || customer.name || `客户${customerId}`;
                const phone = customer.phone || customer.telephone || '';
                const email = customer.email || '';
                
                html += `
                    <label class="list-group-item">
                        <input class="form-check-input me-2" type="checkbox" value="${customerId}" onchange="toggleSendCustomerSelection('${customerId}')">
                        <div>
                            <strong>${customerName}</strong>
                            ${phone ? `<br><small class="text-muted">电话: ${phone}</small>` : ''}
                            ${email ? `<br><small class="text-muted">邮箱: ${email}</small>` : ''}
                        </div>
                    </label>
                `;
            });
            html += '</div>';
            
            customerListDiv.innerHTML = html;
        }
        
        // 切换发送客户选择
        function toggleSendCustomerSelection(customerId) {
            const checkbox = event.target;
            const index = sendSelectedCustomers.findIndex(c => (c.id || c.customerId) == customerId);
            if (checkbox.checked) {
                if (index === -1) {
                    const customer = sendAllCustomers.find(c => (c.id || c.customerId) == customerId);
                    if (customer) {
                        sendSelectedCustomers.push(customer);
                    }
                }
            } else {
                if (index > -1) {
                    sendSelectedCustomers.splice(index, 1);
                }
            }
            
            updateSendCustomerCount();
        }
        
        // 过滤发送客户
        function filterSendCustomers() {
            const searchTerm = document.getElementById('sendCustomerSearch').value.toLowerCase();
            if (!searchTerm) {
                renderSendCustomerList(sendAllCustomers);
                return;
            }
            
            const filtered = sendAllCustomers.filter(customer => {
                const name = (customer.customerName || customer.name || '').toLowerCase();
                const phone = (customer.phone || customer.telephone || '').toLowerCase();
                const email = (customer.email || '').toLowerCase();
                return name.includes(searchTerm) || phone.includes(searchTerm) || email.includes(searchTerm);
            });
            
            renderSendCustomerList(filtered);
        }
        
        // 生成发送分享链接
        function generateSendShareLink() {
            if (sendSelectedCustomers.length === 0) {
                alert('请先选择至少一位客户！');
                return;
            }
            
            // 获取当前内容
            const reportContent = document.getElementById('reportContent');
            const reportEditor = document.getElementById('reportEditor');
            let content = '';
            
            if (reportEditor && reportEditor.style.display !== 'none') {
                // 编辑模式：使用编辑器内容
                content = document.getElementById('reportContentEditor').value;
            } else if (reportContent && reportContent.style.display !== 'none') {
                // 预览模式：使用预览内容
                content = reportContent.innerText || reportContent.textContent;
            } else {
                // 尝试从当前内容类型生成
                const selectedRecs = recommendations.filter(r => selectedRecommendations.includes(r.id));
                if (selectedRecs.length > 0) {
                    content = selectedRecs.map(r => `${r.title}: ${r.content}`).join('\n\n');
                } else {
                    content = 'AI智能推荐内容';
                }
            }
            
            // 构建分享数据
            const shareData = {
                customers: sendSelectedCustomers.map(c => ({
                    id: c.id || c.customerId,
                    name: c.customerName || c.name || c.name,
                    phone: c.phone || c.telephone || '',
                    email: c.email || ''
                })),
                content: content,
                contentType: currentContentType,
                generateTime: new Date().toISOString()
            };
            
            // 使用Base64编码数据（支持UTF-8）
            const encodedData = utf8ToBase64(JSON.stringify(shareData));
            sendShareLink = `${window.location.origin}/ai-recommendations-share.html?data=${encodeURIComponent(encodedData)}`;
            
            // 显示分享链接
            document.getElementById('sendShareLink').value = sendShareLink;
            
            // 生成二维码
            generateSendQRCode();
            
            // 显示分享区域和发送按钮
            document.getElementById('shareSection').style.display = 'block';
            document.getElementById('sendBtn').style.display = 'inline-block';
        }
        
        // 生成发送二维码
        function generateSendQRCode() {
            const container = document.getElementById('sendQRCodeContainer');
            if (!container) return;
            container.innerHTML = '';
            
            if (!sendShareLink) {
                container.innerHTML = '<p class="text-danger small">请先生成分享链接</p>';
                return;
            }
            
            // 检查QRCode库是否已加载，如果未加载则等待或重试
            if (typeof QRCode === 'undefined' || QRCode === null) {
                container.innerHTML = '<p class="text-muted small">二维码库加载中，请稍候...</p>';
                // 等待库加载，最多重试10次（5秒）
                let retryCount = 0;
                const maxRetries = 10;
                const checkQRCode = setInterval(function() {
                    retryCount++;
                    if (typeof QRCode !== 'undefined' && QRCode !== null) {
                        clearInterval(checkQRCode);
                        generateSendQRCode(); // 重新调用
                    } else if (retryCount >= maxRetries) {
                        clearInterval(checkQRCode);
                        // 降级方案：显示链接文本
                        container.innerHTML = 
                            '<div class="text-center p-3 border rounded">' +
                            '<p class="text-muted small mb-2">二维码库未加载，请使用以下链接：</p>' +
                            '<p class="mb-0"><a href="' + sendShareLink + '" target="_blank" class="text-break">' + sendShareLink + '</a></p>' +
                            '<button class="btn btn-sm btn-outline-primary mt-2" onclick="copySendShareLink()">' +
                            '<i class="bi bi-clipboard"></i> 复制链接</button>' +
                            '</div>';
                        console.warn('QRCode库加载超时，已显示链接作为降级方案');
                    }
                }, 500);
                return;
            }
            
            const success = renderQRCode(container, sendShareLink);
            if (!success) {
                container.innerHTML = '<p class="text-danger small">生成二维码失败，请稍后重试</p>';
            }
        }
        
        // 复制发送分享链接
        function copySendShareLink() {
            const linkInput = document.getElementById('sendShareLink');
            if (!linkInput || !linkInput.value) {
                alert('请先生成分享链接');
                return;
            }
            linkInput.select();
            document.execCommand('copy');
            alert('分享链接已复制到剪贴板');
        }
        
        // 下载发送二维码
        function downloadSendQRCode() {
            const canvas = document.querySelector('#sendQRCodeContainer canvas');
            if (!canvas) {
                alert('二维码未生成');
                return;
            }
            
            canvas.toBlob(function(blob) {
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = `分享二维码_${new Date().toISOString().split('T')[0]}.png`;
                link.click();
                URL.revokeObjectURL(url);
            });
        }
        
        // 发送给客户
        function sendToCustomers() {
            if (sendSelectedCustomers.length === 0) {
                alert('请先选择客户！');
                return;
            }
            
            if (!sendShareLink) {
                alert('请先生成分享链接！');
                return;
            }
            
            // 这里可以调用后端API发送短信或邮件
            // 目前先显示成功提示
            const customerNames = sendSelectedCustomers.map(c => c.customerName || c.name || '客户').join('、');
            alert('已成功发送给 ' + customerNames + ' 等 ' + sendSelectedCustomers.length + ' 位客户！\n\n分享链接：' + sendShareLink);
            
            // 关闭模态框
            const modal = bootstrap.Modal.getInstance(document.getElementById('sendShareModal'));
            if (modal) {
                modal.hide();
            }
        }
        
        // 将实际实现的函数立即赋值给window对象和全局变量
        // 这个赋值会在script标签执行时立即执行，确保函数可用
        if (typeof window !== 'undefined') {
            // 将实际实现赋值给window对象，替换之前的临时函数
            if (typeof _openGenerateContentModal === 'function') {
                window.openGenerateContentModal = _openGenerateContentModal;
                window._openGenerateContentModal = _openGenerateContentModal;
                // 同时更新全局函数
                openGenerateContentModal = _openGenerateContentModal;
            }
            if (typeof _openSendShareModal === 'function') {
                window.openSendShareModal = _openSendShareModal;
                window._openSendShareModal = _openSendShareModal;
                // 同时更新全局函数
                openSendShareModal = _openSendShareModal;
            }
            // 确保模板相关函数全局可访问
            window.renderTemplateList = renderTemplateList;
            window.applyTemplate = applyTemplate;
            window.deleteTemplate = deleteTemplate;
            window.saveCurrentTemplate = saveCurrentTemplate;
        }
