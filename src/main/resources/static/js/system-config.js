// 系统配置管理 JavaScript

// 术语映射表 (去技术化)
const typeNameMap = {
    'STRING': '普通文本',
    'NUMBER': '数值/数字',
    'BOOLEAN': '开关/状态',
    'JSON': '分类型业务配置'
};

let allConfigs = []; // 存储所有配置数据
let currentGroup = '全部'; // 当前选中的分组
let searchTimer = null; // 搜索防抖定时器

// 加载配置列表
function loadConfigs() {
    const configList = document.getElementById('configList');
    configList.innerHTML = `
        <div class="text-center py-5">
            <div class="spinner-grow text-primary" role="status"></div>
            <p class="mt-3 text-muted">正在同步中心化配置...</p>
        </div>
    `;

    fetch('/api/system-config/list')
        .then(response => {
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return response.json();
        })
        .then(result => {
            if (result.code === 200 && result.data) {
                allConfigs = result.data;
                loadGroupList();
                renderConfigList(allConfigs);
            } else {
                configList.innerHTML = `<div class="alert alert-warning">数据同步失败: ${result.message}</div>`;
            }
        })
        .catch(error => {
            console.error('❌ 加载配置失败:', error);
            configList.innerHTML = `<div class="alert alert-danger">连接配置服务异常，请重试</div>`;
        });
}

// 加载分组列表
function loadGroupList() {
    const groupList = document.getElementById('groupList');
    const groups = new Set();
    allConfigs.forEach(config => groups.add(config.configGroup || '其他'));

    const sortedGroups = Array.from(groups).sort();

    let html = '';
    sortedGroups.forEach(group => {
        html += `
            <div class="config-group-item ${currentGroup === group ? 'active' : ''}" 
                 onclick="filterByGroup('${group}')" data-group="${group}">
                <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-folder2-open"></i>
                    <span>${escapeHtml(group)}</span>
                </div>
            </div>
        `;
    });
    groupList.innerHTML = html;
}

// 按分组过滤
function filterByGroup(group) {
    currentGroup = group;
    document.querySelectorAll('.config-group-item').forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('data-group') === group) item.classList.add('active');
    });

    const filtered = group === '全部' ? allConfigs : allConfigs.filter(c => (c.configGroup || '其他') === group);
    renderConfigList(filtered);
}

// 实时搜索处理（防抖）
function handleRealtimeSearch(keyword) {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(() => {
        const kw = keyword.toLowerCase().trim();
        if (!kw) {
            filterByGroup(currentGroup);
            return;
        }

        const filtered = allConfigs.filter(c =>
            c.configKey.toLowerCase().includes(kw) ||
            c.configValue.toLowerCase().includes(kw) ||
            (c.description && c.description.toLowerCase().includes(kw))
        );
        renderConfigList(filtered);
    }, 300);
}

// 渲染配置列表
function renderConfigList(configs) {
    const configList = document.getElementById('configList');
    if (!configs || configs.length === 0) {
        configList.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-search display-1 text-light"></i>
                <p class="mt-3 text-muted">目前还没有找到相关的配置项</p>
            </div>
        `;
        return;
    }

    let html = '';
    configs.forEach(config => {
        const typeClass = `type-${config.configType.toLowerCase()}`;
        const humanTypeName = typeNameMap[config.configType] || config.configType;

        let valueDisplay = escapeHtml(config.configValue);

        // 如果是 JSON，尝试更友好的显示
        if (config.configType === 'JSON') {
            try {
                const jsonObj = JSON.parse(config.configValue);
                if (typeof jsonObj === 'object' && jsonObj !== null) {
                    let preview = '';
                    for (let key in jsonObj) {
                        const val = jsonObj[key];
                        const valStr = Array.isArray(val) ? val.join(', ') : val;
                        preview += `<div class="mb-1"><span class="badge bg-secondary bg-opacity-10 text-secondary border me-1">${escapeHtml(key)}:</span> <span class="small text-muted">${escapeHtml(String(valStr))}</span></div>`;
                    }
                    valueDisplay = `<div class="json-preview-box">${preview || '空分类'}</div>`;
                }
            } catch (e) { }
        }

        // 优先显示描述作为标题
        const displayTitle = config.description ? escapeHtml(config.description) : escapeHtml(config.configKey);
        const subTitle = config.description ? `<code>${escapeHtml(config.configKey)}</code>` : '';

        html += `
            <div class="config-item-card">
                <div class="config-item-header">
                    <div>
                        <div class="fs-6 fw-bold text-dark mb-1">
                            ${displayTitle}
                        </div>
                        <div class="d-flex align-items-center gap-2">
                            <span class="config-type-badge ${typeClass}">${humanTypeName}</span>
                            <span class="text-muted small">${subTitle}</span>
                            ${subTitle ? `<i class="bi bi-clipboard copy-btn small" title="复制键名" onclick="copyToClipboard('${config.configKey}')"></i>` : ''}
                        </div>
                    </div>
                    <div class="d-flex gap-1">
                        <button class="btn btn-icon btn-sm" onclick="editConfig(${config.id})" title="修改">
                            <i class="bi bi-pencil-square text-primary"></i>
                        </button>
                        <button class="btn btn-icon btn-sm" onclick="deleteConfig(${config.id})" title="移除">
                            <i class="bi bi-trash3 text-danger"></i>
                        </button>
                    </div>
                </div>
                <div class="config-item-value">${valueDisplay}</div>
                <div class="mt-2 text-muted small">
                    <i class="bi bi-folder-symlink"></i> 所属分组: ${escapeHtml(config.configGroup || '未分类')}
                </div>
            </div>
        `;
    });
    configList.innerHTML = html;
}

// 可视化编辑器逻辑
let currentEditorMode = 'raw'; // 'raw' or 'visual'

function handleTypeChange(type) {
    const toggle = document.getElementById('modeToggleContainer');
    if (type === 'JSON') {
        toggle.style.display = 'flex';
    } else {
        toggle.style.display = 'none';
        switchEditorMode('raw'); // 非 JSON 强制切回源码模式
    }
}

function switchEditorMode(mode) {
    currentEditorMode = mode;
    const rawEditor = document.getElementById('configValue');
    const visualEditor = document.getElementById('visualEditor');
    const modeRawBtn = document.getElementById('modeRaw');
    const modeVisualBtn = document.getElementById('modeVisual');

    if (mode === 'visual') {
        // 源码 -> 可视化
        try {
            const jsonObj = JSON.parse(rawEditor.value || '{}');
            renderVisualItems(jsonObj);
            rawEditor.style.display = 'none';
            visualEditor.style.display = 'block';
            modeVisualBtn.classList.add('active');
            modeRawBtn.classList.remove('active');
        } catch (e) {
            alert('当前 JSON 格式有误，请先在源码模式下修正后再切换可视化。');
            switchEditorMode('raw');
        }
    } else {
        // 可视化 -> 源码
        if (visualEditor.style.display === 'block') {
            syncVisualToRaw();
        }
        rawEditor.style.display = 'block';
        visualEditor.style.display = 'none';
        modeRawBtn.classList.add('active');
        modeVisualBtn.classList.remove('active');
    }
}

function renderVisualItems(obj) {
    const container = document.getElementById('visualItems');
    container.innerHTML = '';

    for (let key in obj) {
        let val = obj[key];
        // 如果是数组，转为逗号分隔字符串
        if (Array.isArray(val)) val = val.join(', ');
        addVisualItem(key, val);
    }

    if (Object.keys(obj).length === 0) {
        addVisualItem('', '');
    }
}

function addVisualItem(key = '', value = '') {
    const container = document.getElementById('visualItems');
    const row = document.createElement('div');
    row.className = 'visual-item-row';
    row.innerHTML = `
        <div class="visual-item-key">
            <input type="text" class="form-control form-control-sm" placeholder="项名/分类" value="${escapeHtml(key)}">
        </div>
        <div class="visual-item-value">
            <input type="text" class="form-control form-control-sm" placeholder="值(多个用逗号隔开)" value="${escapeHtml(String(value))}">
        </div>
        <button type="button" class="btn btn-link text-danger p-0 ms-2" onclick="this.parentElement.remove()" title="删除项">
            <i class="bi bi-x-circle"></i>
        </button>
    `;
    container.appendChild(row);
}

function syncVisualToRaw() {
    const rows = document.querySelectorAll('.visual-item-row');
    const result = {};

    rows.forEach(row => {
        const key = row.querySelector('.visual-item-key input').value.trim();
        let val = row.querySelector('.visual-item-value input').value.trim();

        if (key) {
            // 尝试检测是否是数组或数字
            if (val.includes(',')) {
                // 如果包含逗号，转为处理过的数组
                result[key] = val.split(',').map(v => {
                    v = v.trim();
                    return isNaN(v) || v === '' ? v : Number(v);
                });
            } else {
                // 普通值，尝试转数字
                result[key] = (isNaN(val) || val === '') ? val : Number(val);
            }
        }
    });

    document.getElementById('configValue').value = JSON.stringify(result, null, 2);
}

// 复制到剪贴板 (保持原样...)
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        const alert = document.createElement('div');
        alert.className = 'position-fixed top-0 start-50 translate-middle-x mt-3 alert alert-success py-1 px-3 z-3';
        alert.style.borderRadius = '20px';
        alert.innerHTML = '<small><i class="bi bi-check-circle"></i> 已复制到剪贴板</small>';
        document.body.appendChild(alert);
        setTimeout(() => alert.remove(), 2000);
    } catch (err) { }
}

// 重写部分已有函数以适配新功能
function showAddConfigModal() {
    document.getElementById('configModalTitle').textContent = '新增系统项';
    document.getElementById('configForm').reset();
    document.getElementById('configId').value = '';
    handleTypeChange('STRING'); // 默认非 JSON
    switchEditorMode('raw');
    if (currentGroup && currentGroup !== '全部') {
        document.getElementById('configGroup').value = currentGroup;
    }
    new bootstrap.Modal(document.getElementById('configModal')).show();
}

function editConfig(id) {
    fetch(`/api/system-config/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200 && result.data) {
                const config = result.data;
                document.getElementById('configModalTitle').textContent = '编辑系统项';
                document.getElementById('configId').value = config.id;
                document.getElementById('configKey').value = config.configKey;
                document.getElementById('configValue').value = config.configValue;
                document.getElementById('configType').value = config.configType;
                document.getElementById('configDescription').value = config.description || '';
                document.getElementById('configGroup').value = config.configGroup || '';

                handleTypeChange(config.configType);
                // 默认切换到可视化模式（如果是 JSON 且解析成功）
                if (config.configType === 'JSON') {
                    switchEditorMode('visual');
                } else {
                    switchEditorMode('raw');
                }

                new bootstrap.Modal(document.getElementById('configModal')).show();
            }
        });
}

function saveConfig() {
    // 如果在可视化模式，先同步回源码
    if (currentEditorMode === 'visual') {
        syncVisualToRaw();
    }

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

    fetch(configId ? `/api/system-config/${configId}` : '/api/system-config', {
        method: configId ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(config)
    })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                bootstrap.Modal.getInstance(document.getElementById('configModal')).hide();
                loadConfigs();
            } else {
                alert('保存失败: ' + result.message);
            }
        });
}

// 删除配置
function deleteConfig(id) {
    if (!confirm('确定要移除此配置项吗？')) return;
    fetch(`/api/system-config/${id}`, { method: 'DELETE' })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) loadConfigs();
            else alert('移除失败');
        });
}

// HTML转义
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 初始化
document.addEventListener('DOMContentLoaded', loadConfigs);
