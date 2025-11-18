// 系统配置管理 JavaScript

let allConfigs = []; // 存储所有配置数据
let currentGroup = '全部'; // 当前选中的分组

// 加载配置列表
function loadConfigs() {
    const configList = document.getElementById('configList');
    configList.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary" role="status"></div><p class="mt-3 text-muted">正在加载配置数据...</p></div>';

    const url = '/api/system-config/list';
    console.log('🔍 加载配置列表，URL:', url);

    fetch(url)
        .then(response => {
            console.log('🔍 API响应状态:', response.status, response.statusText);
            if (!response.ok) {
                if (response.status === 404) {
                    console.error('❌ API不存在（404），请确认应用已重启且Controller已注册');
                    configList.innerHTML = '<div class="alert alert-danger">系统配置API不存在（404），请确认应用已重启</div>';
                    return null;
                }
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(result => {
            if (!result) return; // 404情况已处理
            
            console.log('🔍 API响应数据:', result);
            if (result.code === 200 && result.data) {
                allConfigs = result.data;
                loadGroupList(); // 加载分组列表
                renderConfigList(allConfigs); // 渲染配置列表
            } else {
                configList.innerHTML = '<div class="alert alert-warning">加载配置失败: ' + (result.message || '未知错误') + '</div>';
            }
        })
        .catch(error => {
            console.error('❌ 加载配置失败:', error);
            configList.innerHTML = '<div class="alert alert-danger">加载配置失败: ' + error.message + '<br><small>请检查网络连接或确认应用已重启</small></div>';
        });
}

// 加载分组列表到左侧目录
function loadGroupList() {
    const groupList = document.getElementById('groupList');
    
    // 获取所有分组
    const groups = new Set();
    allConfigs.forEach(config => {
        const group = config.configGroup || '其他';
        if (group && group.trim()) {
            groups.add(group);
        }
    });
    
    // 按字母顺序排序
    const sortedGroups = Array.from(groups).sort();
    
    let html = '';
    sortedGroups.forEach(group => {
        const count = allConfigs.filter(c => (c.configGroup || '其他') === group).length;
        html += `
            <div class="config-group-item" onclick="filterByGroup('${group}')" data-group="${group}">
                <i class="bi bi-folder"></i> ${escapeHtml(group)} <span class="badge bg-secondary ms-2">${count}</span>
            </div>
        `;
    });
    
    groupList.innerHTML = html;
    
    // 更新当前选中状态
    updateActiveGroup();
}

// 更新当前选中的分组样式
function updateActiveGroup() {
    document.querySelectorAll('.config-group-item').forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('data-group') === currentGroup) {
            item.classList.add('active');
        }
    });
}

// 按分组过滤配置
function filterByGroup(group) {
    currentGroup = group;
    updateActiveGroup();
    
    let filteredConfigs = allConfigs;
    if (group !== '全部') {
        filteredConfigs = allConfigs.filter(config => {
            const configGroup = config.configGroup || '其他';
            return configGroup === group;
        });
    }
    
    renderConfigList(filteredConfigs);
}

// 渲染配置列表（新布局：卡片式）
function renderConfigList(configs) {
    const configList = document.getElementById('configList');
    
    if (!configs || configs.length === 0) {
        configList.innerHTML = '<div class="alert alert-info">暂无配置数据，请点击"新增配置"添加</div>';
        return;
    }

    let html = '';
    configs.forEach(config => {
        const typeClass = `type-${config.configType.toLowerCase()}`;
        html += `
            <div class="config-item-card">
                <div class="config-item-header">
                    <div class="config-item-key">
                        ${escapeHtml(config.configKey)}
                        <span class="config-type-badge ${typeClass}">${escapeHtml(config.configType)}</span>
                    </div>
                    <div>
                        <button class="btn btn-sm btn-outline-primary" onclick="editConfig(${config.id})" title="编辑">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger ms-2" onclick="deleteConfig(${config.id})" title="删除">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="config-item-value">${escapeHtml(config.configValue)}</div>
                ${config.description ? `<div class="text-muted small mt-2">${escapeHtml(config.description)}</div>` : ''}
                <div class="config-item-meta mt-2">
                    <span><i class="bi bi-folder"></i> ${escapeHtml(config.configGroup || '其他')}</span>
                    ${config.createTime ? `<span><i class="bi bi-calendar"></i> ${formatDate(config.createTime)}</span>` : ''}
                </div>
            </div>
        `;
    });

    configList.innerHTML = html;
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '';
    try {
        const date = new Date(dateString);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (e) {
        return dateString;
    }
}

// 显示新增配置模态框
function showAddConfigModal() {
    document.getElementById('configModalTitle').textContent = '新增配置';
    document.getElementById('configForm').reset();
    document.getElementById('configId').value = '';
    // 如果当前有选中的分组，自动填充
    if (currentGroup && currentGroup !== '全部') {
        document.getElementById('configGroup').value = currentGroup;
    }
    new bootstrap.Modal(document.getElementById('configModal')).show();
}

// 编辑配置
function editConfig(id) {
    const url = `/api/system-config/${id}`;
    console.log('🔍 获取配置详情，URL:', url);
    
    fetch(url)
        .then(response => {
            console.log('🔍 API响应状态:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(result => {
            if (result.code === 200 && result.data) {
                const config = result.data;
                document.getElementById('configModalTitle').textContent = '编辑配置';
                document.getElementById('configId').value = config.id;
                document.getElementById('configKey').value = config.configKey;
                document.getElementById('configValue').value = config.configValue;
                document.getElementById('configType').value = config.configType;
                document.getElementById('configDescription').value = config.description || '';
                document.getElementById('configGroup').value = config.configGroup || '';
                new bootstrap.Modal(document.getElementById('configModal')).show();
            } else {
                alert('获取配置信息失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('❌ 获取配置信息失败:', error);
            alert('获取配置信息失败: ' + error.message);
        });
}

// 保存配置
function saveConfig() {
    const form = document.getElementById('configForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const configId = document.getElementById('configId').value;
    const config = {
        configKey: document.getElementById('configKey').value.trim(),
        configValue: document.getElementById('configValue').value.trim(),
        configType: document.getElementById('configType').value,
        description: document.getElementById('configDescription').value.trim(),
        configGroup: document.getElementById('configGroup').value.trim() || '其他'
    };

    const url = configId ? `/api/system-config/${configId}` : '/api/system-config';
    const method = configId ? 'PUT' : 'POST';
    console.log('🔍 保存配置，URL:', url, 'Method:', method);

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(config)
    })
        .then(response => {
            console.log('🔍 保存配置响应状态:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(result => {
            if (result.code === 200) {
                bootstrap.Modal.getInstance(document.getElementById('configModal')).hide();
                loadConfigs(); // 重新加载所有数据
                alert('保存成功');
            } else {
                alert('保存失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('保存配置失败:', error);
            alert('保存配置失败，请重试');
        });
}

// 删除配置
function deleteConfig(id) {
    if (!confirm('确定要删除这个配置吗？')) {
        return;
    }

    const url = `/api/system-config/${id}`;
    console.log('🔍 删除配置，URL:', url);

    fetch(url, {
        method: 'DELETE'
    })
        .then(response => {
            console.log('🔍 删除配置响应状态:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(result => {
            if (result.code === 200) {
                loadConfigs(); // 重新加载所有数据
                alert('删除成功');
            } else {
                alert('删除失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('删除配置失败:', error);
            alert('删除配置失败，请重试');
        });
}

// 搜索配置
function searchConfigs() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (!keyword) {
        loadConfigs();
        return;
    }

    const configList = document.getElementById('configList');
    configList.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-primary" role="status"></div><p class="mt-3 text-muted">正在搜索...</p></div>';

    const url = `/api/system-config/search?keyword=${encodeURIComponent(keyword)}`;
    console.log('🔍 搜索配置，URL:', url);

    fetch(url)
        .then(response => {
            console.log('🔍 搜索配置响应状态:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(result => {
            if (result.code === 200 && result.data) {
                renderConfigList(result.data);
            } else {
                configList.innerHTML = '<div class="alert alert-warning">搜索失败: ' + (result.message || '未知错误') + '</div>';
            }
        })
        .catch(error => {
            console.error('搜索配置失败:', error);
            configList.innerHTML = '<div class="alert alert-danger">搜索失败，请检查网络连接</div>';
        });
}

// 处理搜索框回车事件
function handleSearchKeyPress(event) {
    if (event.key === 'Enter') {
        searchConfigs();
    }
}

// HTML转义
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    loadConfigs();
});
