// 客户数据（从API获取）
let customers = [];
let selectedCustomers = [];

// 客户类型映射
const customerTypeMap = {
    '1': '个人',
    '2': '企业',
    '3': '科研院所'
};

// 反向映射：显示文本到数字
const customerTypeReverseMap = {
    '个人': 1,
    '企业': 2,
    '科研院所': 3
};

// 客户等级映射
const customerLevelMap = {
    '1': '普通',
    '2': 'VIP',
    '3': '钻石'
};

// 反向映射：显示文本到数字
const customerLevelReverseMap = {
    '普通': 1,
    'VIP': 2,
    '钻石': 3
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    loadCustomers();
    updateTotalCount();
    
    // 初始化文件上传功能
    initFileUpload();
    
    // 自动启动语音监听
    setTimeout(() => {
        autoStartVoiceListening();
    }, 2000); // 延迟2秒启动，确保页面完全加载
});

// 加载客户列表
function loadCustomers() {
    const tbody = document.getElementById('customerTableBody');
        tbody.innerHTML = '<tr><td colspan="11" class="text-center">加载中...</td></tr>';

    // 构建查询参数
    const params = new URLSearchParams();
    params.append('pageNum', '1');
    params.append('pageSize', '1000');
    
    const customerName = document.getElementById('customerName')?.value;
    const customerType = document.getElementById('customerType')?.value;
        const customerLevel = document.getElementById('customerLevel')?.value;
    const region = document.getElementById('region')?.value;
    
    if (customerName) {
        params.append('customerName', customerName);
    }
    if (customerType && customerTypeReverseMap[customerType]) {
        params.append('customerType', customerTypeReverseMap[customerType]);
    }
        if (customerLevel && customerLevelReverseMap[customerLevel]) {
            params.append('customerLevel', customerLevelReverseMap[customerLevel]);
    }
    
    fetch('/api/customer/page?' + params.toString())
        .then(response => {
            if (!response.ok) {
                // 如果HTTP状态码不是200，尝试解析错误信息
                return response.json().then(err => {
                    throw new Error(err.message || '服务器错误: ' + response.status);
                });
            }
            return response.json();
        })
        .then(result => {
            console.log('API响应:', result);
            if (result && result.code === 200 && result.data) {
                customers = result.data.list || result.data || [];
                renderCustomerTable(customers);
                updateTotalCount();
            } else {
                const errorMsg = (result && result.message) || (result && result.error) || '未知错误';
                tbody.innerHTML = '<tr><td colspan="11" class="text-center text-danger">加载失败: ' + errorMsg + '</td></tr>';
            }
        })
        .catch(error => {
            console.error('加载客户列表失败:', error);
            const errorMsg = error.message || '网络错误或服务器未响应';
            tbody.innerHTML = '<tr><td colspan="11" class="text-center text-danger">加载失败: ' + errorMsg + '<br><small>请检查数据库连接或稍后重试</small></td></tr>';
        });
}

// 渲染客户表格
function renderCustomerTable(customerList) {
    const tbody = document.getElementById('customerTableBody');
    tbody.innerHTML = '';
    
    if (customerList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="11" class="text-center">暂无数据</td></tr>';
        return;
    }

    customerList.forEach(customer => {
        const row = document.createElement('tr');
        const sensitiveStatus = customer.isSensitive ? '是' : '否';
        const customerTypeText = customerTypeMap[customer.customerType] || customer.customerType || '未知';
        const customerLevel = customer.customerLevel || 1;
        const customerLevelText = customerLevelMap[customerLevel] || '普通';
        let levelBadgeClass = 'bg-secondary';
        if (customerLevel === 2) {
            levelBadgeClass = 'bg-warning';
        } else if (customerLevel === 3) {
            levelBadgeClass = 'bg-primary';
        }
        const createTime = customer.createTime ? new Date(customer.createTime).toLocaleString('zh-CN') : '';
        
        row.innerHTML = 
            '<td>' +
                '<input type="checkbox" class="customer-checkbox" value="' + customer.id + '" onchange="toggleCustomerSelection(' + customer.id + ')">' +
            '</td>' +
            '<td class="table-cell-truncate" title="' + (customer.customerName || '') + '">' + (customer.customerName || '') + '</td>' +
            '<td class="table-cell-truncate" title="' + (customer.contactPerson || '') + '">' + (customer.contactPerson || '') + '</td>' +
            '<td class="table-cell-truncate" title="' + (customer.phone || '') + '">' + (customer.phone || '') + '</td>' +
            '<td class="table-cell-truncate" title="' + customerTypeText + '">' + customerTypeText + '</td>' +
            '<td class="table-cell-truncate">' +
                '<span class="badge ' + levelBadgeClass + '">' + customerLevelText + '</span>' +
            '</td>' +
            '<td class="table-cell-truncate" title="' + (customer.position || '') + '">' + (customer.position || '') + '</td>' +
            '<td class="table-cell-truncate" title="' + (customer.qqWeixin || '') + '">' + (customer.qqWeixin || '') + '</td>' +
            '<td class="table-cell-truncate" title="' + (customer.region || '') + '">' + (customer.region || '') + '</td>' +
            '<td class="table-cell-truncate">' + sensitiveStatus + '</td>' +
            '<td class="table-cell-truncate" title="' + createTime + '">' + createTime + '</td>' +
            '<td>' +
                '<button class="btn btn-sm btn-outline-primary me-1" onclick="viewCustomer(' + customer.id + ')">' +
                    '<i class="bi bi-eye"></i> 详情' +
                '</button>' +
                '<button class="btn btn-sm btn-outline-warning me-1" onclick="editCustomer(' + customer.id + ')">' +
                    '<i class="bi bi-pencil"></i> 编辑' +
                '</button>' +
                '<button class="btn btn-sm btn-outline-danger" onclick="deleteCustomer(' + customer.id + ')">' +
                    '<i class="bi bi-trash"></i> 删除' +
                '</button>' +
            '</td>';
        tbody.appendChild(row);
    });
}

// 更新总记录数
function updateTotalCount() {
    const totalCountElement = document.getElementById('totalCustomers');
    if (totalCountElement) {
        totalCountElement.textContent = customers.length;
    }
}

// 搜索客户
function searchCustomers() {
    loadCustomers(); // 直接调用loadCustomers，它已经包含了搜索参数
}

// 重置筛选条件
function resetFilters() {
    document.getElementById('customerName').value = '';
    document.getElementById('customerType').value = '';
    document.getElementById('customerLevel').value = '';
    document.getElementById('region').value = '';
    loadCustomers();
}

// 显示新增客户模态框
function showAddCustomerModal() {
    document.getElementById('customerModalTitle').textContent = '新增客户';
    document.getElementById('customerForm').reset();
    document.getElementById('customerId').value = '';
    new bootstrap.Modal(document.getElementById('customerModal')).show();
}

// 显示AI识别模态框
function showAIRecognition() {
    new bootstrap.Modal(document.getElementById('aiRecognitionModal')).show();
}

// 显示文件上传模态框
function showFileUpload() {
    new bootstrap.Modal(document.getElementById('fileUploadModal')).show();
}

// 解析导入数据
function parseImportData() {
    const data = document.getElementById('batchImportData').value.trim();
    if (!data) {
        alert('请先粘贴要导入的数据！');
        return;
    }

    const lines = data.split('\n').filter(line => line.trim());
    const parsedData = [];
    let hasError = false;

    lines.forEach((line, index) => {
        const fields = line.split(/[\t,|]/).map(field => field.trim());
        
        if (fields.length < 5) {
            hasError = true;
            parsedData.push({
                customerName: fields[0] || '',
                contactPerson: fields[1] || '',
                phone: fields[2] || '',
                customerType: fields[3] || '',
                position: fields[4] || '',
                qqWeixin: fields[5] || '',
                cooperationContent: fields[6] || '',
                region: fields[7] || '',
                status: 'error',
                error: '字段不足，至少需要5个字段'
            });
            return;
        }

        // 验证必填字段
        const requiredFields = [fields[0], fields[1], fields[2], fields[3], fields[7]];
        const missingFields = requiredFields.some(field => !field);
        
        if (missingFields) {
            hasError = true;
            parsedData.push({
                customerName: fields[0] || '',
                contactPerson: fields[1] || '',
                phone: fields[2] || '',
                customerType: fields[3] || '',
                position: fields[4] || '',
                qqWeixin: fields[5] || '',
                cooperationContent: fields[6] || '',
                region: fields[7] || '',
                status: 'error',
                error: '必填字段不能为空'
            });
        } else {
            parsedData.push({
                customerName: fields[0],
                contactPerson: fields[1],
                phone: fields[2],
                customerType: fields[3],
                position: fields[4] || '',
                qqWeixin: fields[5] || '',
                cooperationContent: fields[6] || '',
                region: fields[7],
                address: fields[8] || '',
                remark: fields[9] || '',
                status: 'valid'
            });
        }
    });

    // 显示预览
    displayImportPreview(parsedData);
    
    if (!hasError) {
        document.getElementById('saveImportBtn').disabled = false;
    }
}

// 显示导入预览
function displayImportPreview(data) {
    const previewBody = document.getElementById('importPreviewBody');
    previewBody.innerHTML = '';

    data.forEach((item, index) => {
        const row = document.createElement('tr');
        row.className = item.status === 'error' ? 'table-danger' : 'table-success';
        
        row.innerHTML = `
            <td>${item.customerName}</td>
            <td>${item.contactPerson}</td>
            <td>${item.phone}</td>
            <td>${item.customerType}</td>
            <td>${item.position || ''}</td>
            <td>${item.qqWeixin || ''}</td>
            <td>${item.cooperationContent || ''}</td>
            <td>${item.region}</td>
            <td>
                ${item.status === 'error' ? 
                    `<span class="text-danger"><i class="bi bi-exclamation-triangle"></i> ${item.error}</span>` : 
                    `<span class="text-success"><i class="bi bi-check-circle"></i> 有效</span>`
                }
            </td>
        `;
        previewBody.appendChild(row);
    });

    document.getElementById('importPreview').style.display = 'block';
}

// 清空导入数据
function clearImportData() {
    document.getElementById('batchImportData').value = '';
    document.getElementById('importPreview').style.display = 'none';
    document.getElementById('saveImportBtn').disabled = true;
}

// 语音录入相关变量
let recognition = null;
let isRecording = false;
let isListeningForKeyword = true; // 默认始终监听关键词
let silenceTimer = null;
let lastSpeechTime = 0;
const SILENCE_TIMEOUT = 5000; // 5秒无语音自动结束
const KEYWORD = '木木';

// 初始化语音识别
function initVoiceRecognition() {
    // 检查网络状态
    if (navigator.onLine === false) {
        console.warn('网络不可用，语音识别功能已禁用');
        return;
    }
    
    if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        recognition = new SpeechRecognition();
        recognition.continuous = true;
        recognition.interimResults = true;
        recognition.lang = 'zh-CN';

        recognition.onstart = function() {
            isRecording = true;
            updateVoiceButton(true);
            if (isListeningForKeyword) {
                showVoiceStatus('正在等待关键词"木木"触发录音...');
            } else {
                showVoiceStatus('正在听取语音，请开始说话...');
            }
        };

        recognition.onresult = function(event) {
            let finalTranscript = '';
            let interimTranscript = '';

            for (let i = event.resultIndex; i < event.results.length; i++) {
                const transcript = event.results[i][0].transcript;
                if (event.results[i].isFinal) {
                    finalTranscript += transcript;
                } else {
                    interimTranscript += transcript;
                }
            }

            // 检查是否包含关键词
            if (isListeningForKeyword && finalTranscript.toLowerCase().includes(KEYWORD)) {
                isListeningForKeyword = false;
                showVoiceStatus('关键词检测成功！开始录音，请说出客户信息...');
                startSilenceTimer();
                return;
            }

            // 如果正在录音，处理语音内容
            if (!isListeningForKeyword && finalTranscript) {
                lastSpeechTime = Date.now();
                const processedText = processVoiceInput(finalTranscript);
                const textarea = document.getElementById('batchImportData');
                const currentText = textarea.value;
                textarea.value = currentText + processedText;
                showVoiceStatus('已识别并处理：' + finalTranscript);
                startSilenceTimer(); // 重置静音计时器
            }

            // 显示临时识别结果
            if (interimTranscript) {
                if (isListeningForKeyword) {
                    showVoiceStatus('正在等待关键词"木木"...');
                } else {
                    showVoiceStatus('正在听取：' + interimTranscript);
                }
            }
        };

        recognition.onerror = function(event) {
            console.error('语音识别错误:', event.error);
            
            // 对network错误进行特殊处理（网络问题不影响主要功能）
            if (event.error === 'network') {
                // network错误通常是网络连接问题，不影响其他功能，静默处理
                console.warn('语音识别网络连接失败，功能已禁用');
                hideVoiceStatus();
                stopVoiceInput();
                // 可以选择性地显示一个非错误性的提示
                // showVoiceStatus('语音识别需要网络连接，当前网络不可用');
                return;
            }
            
            // 其他错误才显示错误提示
            if (event.error === 'not-allowed') {
                showVoiceStatus('语音识别权限被拒绝，请在浏览器设置中允许麦克风权限');
            } else if (event.error === 'no-speech') {
                // 无语音输入是正常情况，不显示错误
                hideVoiceStatus();
            } else {
            showVoiceStatus('语音识别出错：' + event.error);
            }
            stopVoiceInput();
        };

        recognition.onend = function() {
            isRecording = false;
            isListeningForKeyword = true; // 重新开始监听关键词
            updateVoiceButton(false);
            hideVoiceStatus();
            clearSilenceTimer();
            
            // 自动重新开始监听（延迟1秒避免频繁重启）
            setTimeout(() => {
                if (!isRecording) {
                    autoStartVoiceListening();
                }
            }, 1000);
        };
    } else {
        alert('您的浏览器不支持语音识别功能，请使用Chrome或Edge浏览器。');
    }
}

// 开始语音录入（现在用于手动控制）
function startVoiceInput() {
    if (!recognition) {
        initVoiceRecognition();
    }

    if (isRecording) {
        stopVoiceInput();
    } else {
        // 启动关键词检测模式
        isListeningForKeyword = true;
        try {
            recognition.start();
        } catch (error) {
            console.error('启动语音识别失败:', error);
            alert('启动语音识别失败，请检查麦克风权限。');
        }
    }
}

// 自动启动语音监听
function autoStartVoiceListening() {
    // 检查网络状态，无网络时不启动语音识别
    if (navigator.onLine === false) {
        console.warn('网络不可用，跳过语音识别自动启动');
        return;
    }
    
    if (!recognition) {
        initVoiceRecognition();
    }
    
    if (!isRecording) {
        try {
            recognition.start();
        } catch (error) {
            console.error('自动启动语音识别失败:', error);
            // 如果是network错误，静默处理
            if (error.message && error.message.includes('network')) {
                console.warn('语音识别网络连接失败，已禁用自动启动');
            }
            // 静默失败，不显示错误提示
        }
    }
}

// 停止语音录入
function stopVoiceInput() {
    if (recognition && isRecording) {
        recognition.stop();
    }
    clearSilenceTimer();
}

// 开始静音计时器
function startSilenceTimer() {
    clearSilenceTimer();
    silenceTimer = setTimeout(() => {
        if (isRecording && !isListeningForKeyword) {
            showVoiceStatus('检测到5秒无语音输入，自动结束录音...');
            setTimeout(() => {
                stopVoiceInput();
            }, 1000);
        }
    }, SILENCE_TIMEOUT);
}

// 清除静音计时器
function clearSilenceTimer() {
    if (silenceTimer) {
        clearTimeout(silenceTimer);
        silenceTimer = null;
    }
}

// 更新语音按钮状态
function updateVoiceButton(recording) {
    const btn = document.getElementById('voiceInputBtn');
    const icon = btn.querySelector('i');
    
    if (recording) {
        btn.className = 'btn btn-danger';
        icon.className = 'bi bi-mic-mute';
        btn.title = '停止语音录入';
    } else {
        btn.className = 'btn btn-outline-success';
        icon.className = 'bi bi-mic';
        btn.title = '开始语音录入';
    }
}

// 显示语音状态
function showVoiceStatus(message) {
    const statusDiv = document.getElementById('voiceStatus');
    const statusText = document.getElementById('voiceStatusText');
    statusText.textContent = message;
    statusDiv.style.display = 'block';
}

// 隐藏语音状态
function hideVoiceStatus() {
    const statusDiv = document.getElementById('voiceStatus');
    statusDiv.style.display = 'none';
}

// 处理语音输入
function processVoiceInput(text) {
    // 替换常见的语音识别错误
    let processedText = text
        .replace(/，/g, ' | ')  // 将中文逗号替换为分隔符
        .replace(/,/g, ' | ')   // 将英文逗号替换为分隔符
        .replace(/下一个客户/g, '\n')  // 将"下一个客户"替换为换行
        .replace(/下一位客户/g, '\n')  // 将"下一位客户"替换为换行
        .replace(/下一个/g, '\n')      // 将"下一个"替换为换行
        .replace(/客户类型企业客户/g, '企业客户')  // 修复客户类型识别
        .replace(/客户类型个人客户/g, '个人客户')  // 修复客户类型识别
        .replace(/年收入/g, '')  // 移除"年收入"文字，只保留数字
        .replace(/万/g, '万')    // 确保"万"字正确
        .replace(/\s+/g, ' ')   // 合并多个空格
        .trim();

    // 如果文本以分隔符结尾，添加换行
    if (processedText.endsWith(' | ')) {
        processedText = processedText.slice(0, -3) + '\n';
    }

    return processedText;
}

// 显示语音帮助
function showVoiceHelp() {
    const helpText = `
语音录入使用说明：

🎤 始终监听模式：
1. 页面加载后自动开始语音监听
2. 直接说出关键词"木木"开始录音
3. 系统检测到关键词后自动开始录音
4. 说出客户信息，系统自动识别和格式化
5. 停顿5秒以上或无语音输入自动结束录音
6. 录音结束后自动重新开始监听

📝 录音格式示例：
"张三公司，联系人李四，电话13800138000，企业客户，新品种申请，北京，农业，年收入1000万，下一个客户，王五农场，联系人赵六，电话13900139000，个人客户，品种权申请，上海，种植业，年收入500万"

⚙️ 操作流程：
1. 页面加载 → 自动开始监听
2. 说出"木木" → 开始录音
3. 说出客户信息 → 自动识别和格式化
4. 停顿5秒 → 自动结束录音
5. 自动重新监听 → 等待下次"木木"
6. 点击"解析数据" → 处理录入内容

💡 语音识别技巧：
- 关键词：清晰说出"木木"触发录音
- 数字：13800138000 说成 "一三八零零一三八零零零"
- 客户类型：说"企业客户"或"个人客户"
- 职务：联系人职务
- QQ/微信：QQ号或微信号
- 合作内容：合作内容描述
- 地区：说具体的城市名称
- 客户分隔：说"下一个客户"来分隔

🔧 自动功能：
- 始终监听：页面加载后自动开始监听
- 关键词检测：自动识别"木木"关键词
- 静音检测：5秒无语音自动结束
- 自动重启：录音结束后自动重新监听
- 数据格式化：自动处理语音识别结果
- 错误修复：自动修复常见识别错误

注意事项：
- 请确保在安静的环境中录入
- 说话要清晰，语速适中
- 必填字段：客户名称、联系人、电话、客户类型、地区
- 可选字段：品种名称等
- 麦克风按钮可用于手动控制监听状态
    `;
    
    alert(helpText);
}

// 文件上传相关变量
let uploadedFiles = [];
let processedData = [];

// 初始化文件上传
function initFileUpload() {
    const fileInput = document.getElementById('fileInput');
    const fileUploadArea = document.getElementById('fileUploadArea');

    // 文件选择事件
    fileInput.addEventListener('change', handleFileSelect);

    // 拖拽事件
    fileUploadArea.addEventListener('dragover', handleDragOver);
    fileUploadArea.addEventListener('dragleave', handleDragLeave);
    fileUploadArea.addEventListener('drop', handleFileDrop);
}

// 处理文件选择
function handleFileSelect(event) {
    const files = Array.from(event.target.files);
    addFiles(files);
}

// 处理拖拽悬停
function handleDragOver(event) {
    event.preventDefault();
    event.currentTarget.classList.add('drag-over');
}

// 处理拖拽离开
function handleDragLeave(event) {
    event.currentTarget.classList.remove('drag-over');
}

// 处理文件拖拽
function handleFileDrop(event) {
    event.preventDefault();
    event.currentTarget.classList.remove('drag-over');
    const files = Array.from(event.dataTransfer.files);
    addFiles(files);
}

// 添加文件
function addFiles(files) {
    files.forEach(file => {
        if (isValidFileType(file)) {
            uploadedFiles.push({
                file: file,
                name: file.name,
                type: getFileType(file.name),
                size: formatFileSize(file.size),
                status: '待处理'
            });
        } else {
            alert(`不支持的文件格式：${file.name}`);
        }
    });
    updateFilePreview();
    document.getElementById('processFilesBtn').disabled = false;
}

// 验证文件类型
function isValidFileType(file) {
    const validTypes = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
        'application/vnd.ms-excel', // .xls
        'application/pdf', // .pdf
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // .docx
        'application/msword', // .doc
        'text/csv', // .csv
        'text/plain' // .txt
    ];
    return validTypes.includes(file.type) || 
           file.name.endsWith('.xlsx') || 
           file.name.endsWith('.xls') || 
           file.name.endsWith('.pdf') || 
           file.name.endsWith('.docx') || 
           file.name.endsWith('.doc') || 
           file.name.endsWith('.csv') || 
           file.name.endsWith('.txt');
}

// 获取文件类型
function getFileType(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    const typeMap = {
        'xlsx': 'Excel文件',
        'xls': 'Excel文件',
        'pdf': 'PDF文件',
        'docx': 'Word文件',
        'doc': 'Word文件',
        'csv': 'CSV文件',
        'txt': '文本文件'
    };
    return typeMap[extension] || '未知类型';
}

// 格式化文件大小
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 更新文件预览
function updateFilePreview() {
    const tbody = document.getElementById('filePreviewBody');
    tbody.innerHTML = '';

    uploadedFiles.forEach((fileData, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${fileData.name}</td>
            <td>${fileData.type}</td>
            <td>${fileData.size}</td>
            <td>
                <span class="badge bg-secondary">${fileData.status}</span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-danger" onclick="removeFile(${index})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });

    document.getElementById('filePreview').style.display = 'block';
}

// 移除文件
function removeFile(index) {
    uploadedFiles.splice(index, 1);
    updateFilePreview();
    if (uploadedFiles.length === 0) {
        document.getElementById('filePreview').style.display = 'none';
        document.getElementById('processFilesBtn').disabled = true;
    }
}

// 处理上传的文件
function processUploadedFiles() {
    if (uploadedFiles.length === 0) {
        alert('请先选择文件！');
        return;
    }

    processedData = [];
    let processedCount = 0;

    uploadedFiles.forEach((fileData, index) => {
        const file = fileData.file;
        const reader = new FileReader();

        reader.onload = function(e) {
            try {
                const data = parseFileContent(e.target.result, file.name);
                processedData = processedData.concat(data);
                fileData.status = '已处理';
                processedCount++;

                if (processedCount === uploadedFiles.length) {
                    updateFilePreview();
                    displayDataPreview();
                    document.getElementById('saveUploadedBtn').disabled = false;
                }
            } catch (error) {
                console.error('文件处理错误:', error);
                fileData.status = '处理失败';
                updateFilePreview();
            }
        };

        reader.readAsText(file);
    });
}

// 解析文件内容
function parseFileContent(content, fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    
    switch (extension) {
        case 'csv':
            return parseCSV(content);
        case 'txt':
            return parseTXT(content);
        default:
            // 对于Excel、PDF、Word等复杂格式，这里模拟解析
            return parseComplexFile(content, fileName);
    }
}

// 解析CSV文件
function parseCSV(content) {
    const lines = content.split('\n').filter(line => line.trim());
    const data = [];
    
    lines.forEach((line, index) => {
        if (index === 0) return; // 跳过标题行
        
        const fields = line.split(',').map(field => field.trim().replace(/"/g, ''));
        if (fields.length >= 5) {
            data.push({
                customerName: fields[0] || '',
                contactPerson: fields[1] || '',
                phone: fields[2] || '',
                customerType: fields[3] || '',
                position: fields[4] || '',
                qqWeixin: fields[5] || '',
                cooperationContent: fields[6] || '',
                region: fields[7] || '',
                status: 'valid'
            });
        }
    });
    
    return data;
}

// 解析TXT文件
function parseTXT(content) {
    const lines = content.split('\n').filter(line => line.trim());
    const data = [];
    
    lines.forEach(line => {
        const fields = line.split(/[\t,|]/).map(field => field.trim());
        if (fields.length >= 5) {
            data.push({
                customerName: fields[0] || '',
                contactPerson: fields[1] || '',
                phone: fields[2] || '',
                customerType: fields[3] || '',
                position: fields[4] || '',
                qqWeixin: fields[5] || '',
                cooperationContent: fields[6] || '',
                region: fields[7] || '',
                status: 'valid'
            });
        }
    });
    
    return data;
}

// 解析复杂文件（Excel、PDF、Word等）
function parseComplexFile(content, fileName) {
    // 这里模拟解析复杂文件，实际项目中需要集成相应的解析库
    const mockData = [
        {
            customerName: '示例公司1',
            contactPerson: '联系人1',
            phone: '13800138001',
            customerType: '企业客户',
            position: '总经理',
            qqWeixin: 'QQ:123456789',
            cooperationContent: '新品种保护申请',
            region: '北京',
            status: 'valid'
        },
        {
            customerName: '示例公司2',
            contactPerson: '联系人2',
            phone: '13800138002',
            customerType: '个人客户',
            position: '市场总监',
            qqWeixin: '微信:test_weixin',
            cooperationContent: '品种权申请',
            region: '上海',
            status: 'valid'
        }
    ];
    
    return mockData;
}

// 显示数据预览
function displayDataPreview() {
    const tbody = document.getElementById('dataPreviewBody');
    tbody.innerHTML = '';

    processedData.forEach((item, index) => {
        const row = document.createElement('tr');
        row.className = item.status === 'valid' ? 'table-success' : 'table-danger';
        
        row.innerHTML = `
            <td>${item.customerName}</td>
            <td>${item.contactPerson}</td>
            <td>${item.phone}</td>
            <td>${item.customerType}</td>
            <td>${item.position || ''}</td>
            <td>${item.qqWeixin || ''}</td>
            <td>${item.cooperationContent || ''}</td>
            <td>${item.region}</td>
            <td>
                ${item.status === 'valid' ? 
                    `<span class="text-success"><i class="bi bi-check-circle"></i> 有效</span>` : 
                    `<span class="text-danger"><i class="bi bi-exclamation-triangle"></i> 无效</span>`
                }
            </td>
        `;
        tbody.appendChild(row);
    });

    document.getElementById('dataPreview').style.display = 'block';
}

// 保存上传的数据
function saveUploadedData() {
    if (processedData.length === 0) {
        alert('没有要保存的数据！');
        return;
    }

    const validData = processedData.filter(item => item.status === 'valid');
    if (validData.length === 0) {
        alert('没有有效的客户数据可保存！');
        return;
    }

    let successCount = 0;
    let errorCount = 0;
    let completedCount = 0;

    validData.forEach((item) => {
        const customerTypeText = item.customerType;
        const customerType = customerTypeReverseMap[customerTypeText] || customerTypeText;
        
        const customerLevelText = item.customerLevel || '普通';
        const customerLevel = customerLevelReverseMap[customerLevelText] || customerLevelText || 1;
        
        const newCustomer = {
            customerName: item.customerName,
            contactPerson: item.contactPerson,
            phone: item.phone,
            email: item.email || '',
            customerType: customerType,
            customerLevel: customerLevel,
            position: item.position || '',
            qqWeixin: item.qqWeixin || '',
            cooperationContent: item.cooperationContent || '',
            region: item.region || '',
            address: item.address || '',
            remark: item.remark || ''
        };

        fetch('/api/customer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newCustomer)
        })
        .then(response => response.json())
        .then(result => {
            completedCount++;
            if (result.code === 200) {
                successCount++;
            } else {
                errorCount++;
            }
            
            if (completedCount === validData.length) {
                alert(`文件导入完成！\n成功导入：${successCount} 条\n失败：${errorCount} 条`);
                loadCustomers();
                bootstrap.Modal.getInstance(document.getElementById('fileUploadModal')).hide();
                uploadedFiles = [];
                processedData = [];
                document.getElementById('filePreview').style.display = 'none';
                document.getElementById('dataPreview').style.display = 'none';
                document.getElementById('processFilesBtn').disabled = true;
                document.getElementById('saveUploadedBtn').disabled = true;
            }
        })
        .catch(error => {
            completedCount++;
            errorCount++;
            if (completedCount === validData.length) {
                alert(`文件导入完成！\n成功导入：${successCount} 条\n失败：${errorCount} 条`);
                loadCustomers();
                bootstrap.Modal.getInstance(document.getElementById('fileUploadModal')).hide();
                uploadedFiles = [];
                processedData = [];
                document.getElementById('filePreview').style.display = 'none';
                document.getElementById('dataPreview').style.display = 'none';
                document.getElementById('processFilesBtn').disabled = true;
                document.getElementById('saveUploadedBtn').disabled = true;
            }
        });
    });
}

// 保存批量导入数据
function saveBatchImport() {
    const data = document.getElementById('batchImportData').value.trim();
    if (!data) {
        alert('没有要保存的数据！');
        return;
    }

    const lines = data.split('\n').filter(line => line.trim());
    const customersToSave = [];

    lines.forEach((line) => {
        const fields = line.split(/[\t,|]/).map(field => field.trim());
        
        if (fields.length >= 5) {
            const requiredFields = [fields[0], fields[1], fields[2], fields[3], fields[7]];
            const missingFields = requiredFields.some(field => !field);
            
            if (!missingFields) {
                const customerTypeText = fields[3];
                const customerType = customerTypeReverseMap[customerTypeText] || customerTypeText;
                
                const newCustomer = {
                    customerName: fields[0],
                    contactPerson: fields[1],
                    phone: fields[2],
                    customerType: customerType,
                    position: fields[4] || '',
                    qqWeixin: fields[5] || '',
                    cooperationContent: fields[6] || '',
                    region: fields[7] || '',
                    email: fields[8] || '',
                    address: fields[9] || '',
                    remark: fields[10] || ''
                };
                customersToSave.push(newCustomer);
            }
        }
    });

    if (customersToSave.length === 0) {
        alert('没有有效的客户数据可保存！');
        return;
    }

    // 批量保存
    let successCount = 0;
    let errorCount = 0;
    let completedCount = 0;

    customersToSave.forEach((customer) => {
        fetch('/api/customer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(customer)
        })
        .then(response => response.json())
        .then(result => {
            completedCount++;
            if (result.code === 200) {
                successCount++;
            } else {
                errorCount++;
            }
            
            if (completedCount === customersToSave.length) {
                alert(`批量导入完成！\n成功导入：${successCount} 条\n失败：${errorCount} 条`);
                loadCustomers();
                bootstrap.Modal.getInstance(document.getElementById('aiRecognitionModal')).hide();
                clearImportData();
            }
        })
        .catch(error => {
            completedCount++;
            errorCount++;
            if (completedCount === customersToSave.length) {
                alert(`批量导入完成！\n成功导入：${successCount} 条\n失败：${errorCount} 条`);
                loadCustomers();
                bootstrap.Modal.getInstance(document.getElementById('aiRecognitionModal')).hide();
                clearImportData();
            }
        });
    });
}

// 查看客户详情
function viewCustomer(id) {
    fetch(`/api/customer/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200 && result.data) {
                const customer = result.data;
                // 检查是否为敏感数据
                if (customer.isSensitive) {
                    currentCustomerId = id;
                    showPasswordModal();
                    return;
                }
                showCustomerDetail(customer);
            } else {
                alert('获取客户详情失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('获取客户详情失败:', error);
            alert('获取客户详情失败，请重试');
        });
}

// 当前查看的客户信息（用于沟通记录）
let currentViewingCustomer = null;

function showCustomerDetail(customer) {
    currentViewingCustomer = customer;
    
    const customerTypeText = customerTypeMap[customer.customerType] || customer.customerType || '未知';
    const customerLevel = customer.customerLevel || 1;
    const customerLevelText = customerLevelMap[customerLevel] || '普通';
    let levelBadgeClass = 'bg-secondary';
    if (customerLevel === 2) {
        levelBadgeClass = 'bg-warning';
    } else if (customerLevel === 3) {
        levelBadgeClass = 'bg-primary';
    }
    const createTime = customer.createTime ? new Date(customer.createTime).toLocaleString('zh-CN') : '';
    const updateTime = customer.updateTime ? new Date(customer.updateTime).toLocaleString('zh-CN') : createTime;
    
    const content = 
        '<div class="row">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">客户名称</label>' +
                    '<p class="mb-0">' + (customer.customerName || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">联系人</label>' +
                    '<p class="mb-0">' + (customer.contactPerson || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">电话</label>' +
                    '<p class="mb-0">' + (customer.phone || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">邮箱</label>' +
                    '<p class="mb-0">' + (customer.email || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">客户类型</label>' +
                    '<p class="mb-0">' + customerTypeText + '</p>' +
                '</div>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">客户等级</label>' +
                    '<p class="mb-0"><span class="badge ' + levelBadgeClass + '">' + customerLevelText + '</span></p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">地区</label>' +
                    '<p class="mb-0">' + (customer.region || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">职务</label>' +
                    '<p class="mb-0">' + (customer.position || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">QQ/微信</label>' +
                    '<p class="mb-0">' + (customer.qqWeixin || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row">' +
            '<div class="col-md-12">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">合作内容</label>' +
                    '<p class="mb-0 text-break">' + (customer.cooperationContent || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row">' +
            '<div class="col-md-12">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">详细地址</label>' +
                    '<p class="mb-0 text-break">' + (customer.address || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row">' +
            '<div class="col-md-12">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">备注</label>' +
                    '<p class="mb-0 text-break">' + (customer.remark || '无') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row mt-3 pt-3 border-top">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">创建时间</label>' +
                    '<p class="mb-0 text-muted small">' + createTime + '</p>' +
                '</div>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">最后更新</label>' +
                    '<p class="mb-0 text-muted small">' + updateTime + '</p>' +
                '</div>' +
            '</div>' +
        '</div>';
    document.getElementById('customerDetailContent').innerHTML = content;
    
    // 初始化标签页事件监听（移除旧的监听器，添加新的）
    const communicationsTab = document.getElementById('communicationsTab');
    if (communicationsTab) {
        // 移除旧的监听器
        const newTab = communicationsTab.cloneNode(true);
        communicationsTab.parentNode.replaceChild(newTab, communicationsTab);
        
        // 添加新的监听器
        document.getElementById('communicationsTab').addEventListener('shown.bs.tab', function() {
            // 当切换到沟通记录标签页时，加载该客户的沟通记录
            if (customer.id) {
                loadCustomerCommunications(customer.id);
            }
        });
    }
    
    // 显示模态框
    const modal = new bootstrap.Modal(document.getElementById('customerDetailModal'));
    modal.show();
}

// 为当前客户显示新增沟通记录模态框
function showAddCommunicationForCurrentCustomer() {
    if (!currentViewingCustomer || !currentViewingCustomer.id) {
        alert('无法获取客户信息，请先查看客户详情');
        return;
    }
    showAddCommunicationModalForCustomer(currentViewingCustomer.id, currentViewingCustomer.customerName);
    
    // 设置客户名称显示
    const customerSelect = document.getElementById('customerSelect');
    const customerIdHidden = document.getElementById('customerIdHidden');
    if (customerSelect) {
        customerSelect.value = currentViewingCustomer.customerName || '';
    }
    if (customerIdHidden) {
        customerIdHidden.value = currentViewingCustomer.id;
    }
}
function editCustomer(id) {
    fetch(`/api/customer/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200 && result.data) {
                const customer = result.data;
                // 检查是否为敏感数据
                if (customer.isSensitive) {
                    currentCustomerId = id;
                    showPasswordModal();
                    return;
                }
                showEditModal(customer);
            } else {
                alert('获取客户信息失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('获取客户信息失败:', error);
            alert('获取客户信息失败，请重试');
        });
}

function showEditModal(customer) {
    document.getElementById('customerModalTitle').textContent = '编辑客户';
    document.getElementById('customerId').value = customer.id;
    document.getElementById('customerNameInput').value = customer.customerName || '';
    document.getElementById('contactPerson').value = customer.contactPerson || '';
    document.getElementById('phone').value = customer.phone || '';
    document.getElementById('email').value = customer.email || '';
    const customerTypeText = customerTypeMap[customer.customerType] || '';
    document.getElementById('customerTypeSelect').value = customerTypeText;
    const customerLevel = customer.customerLevel || 1;
    const customerLevelText = customerLevelMap[customerLevel] || '普通';
    document.getElementById('customerLevelSelect').value = customerLevelText;
    document.getElementById('regionSelect').value = customer.region || '';
    document.getElementById('position').value = customer.position || '';
    document.getElementById('qqWeixin').value = customer.qqWeixin || '';
    document.getElementById('cooperationContent').value = customer.cooperationContent || '';
    document.getElementById('address').value = customer.address || '';
    document.getElementById('remarks').value = customer.remark || '';
    new bootstrap.Modal(document.getElementById('customerModal')).show();
}

// 保存客户
function saveCustomer() {
    const form = document.getElementById('customerForm');
    const formData = new FormData(form);
    
    // 验证必填字段
    const customerName = formData.get('customerName')?.trim();
    if (!customerName) {
        alert('客户姓名/企业名称不能为空！');
        return;
    }
    
    const customerTypeText = formData.get('customerType');
    // 确保customerType有值，默认为1（个人客户）
    let customerType = customerTypeReverseMap[customerTypeText];
    if (!customerType && customerTypeText) {
        // 如果映射失败，尝试直接使用原值（可能是数字字符串）
        customerType = parseInt(customerTypeText) || 1;
    }
    if (!customerType) {
        customerType = 1; // 默认个人客户
    }
    
    const customerLevelText = formData.get('customerLevel');
    const customerLevel = customerLevelReverseMap[customerLevelText] || parseInt(customerLevelText) || 1;
    
    const customerId = document.getElementById('customerId').value;
    
    const customerData = {
        customerName: customerName,
        contactPerson: formData.get('contactPerson') || null,
        phone: formData.get('phone') || null,
        email: formData.get('email') || null,
        customerType: customerType,
        customerLevel: customerLevel,
        region: formData.get('region') || null,
        position: formData.get('position') || null,
        qqWeixin: formData.get('qqWeixin') || null,
        cooperationContent: formData.get('cooperationContent') || null,
        address: formData.get('address') || null,
        remark: formData.get('remarks') || null,
        status: 1, // 默认状态：正常
        source: 2  // 默认来源：线下
    };
    
    // 如果是新增客户，生成客户编号；如果是编辑，保留原有ID和编号
    if (customerId) {
        customerData.id = parseInt(customerId);
    } else {
        // 新增客户时生成客户编号（格式：CUST + 时间戳 + 随机数）
        customerData.customerCode = 'CUST' + Date.now() + Math.floor(Math.random() * 1000);
    }
    
    console.log('准备保存客户数据:', customerData);
    
    const url = customerId ? `/api/customer` : `/api/customer`;
    const method = customerId ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(customerData)
    })
    .then(response => {
        // 先尝试解析JSON，如果失败则说明可能是HTML错误页面
        if (!response.ok) {
            return response.text().then(text => {
                console.error('服务器错误响应:', text);
                throw new Error(`HTTP ${response.status}: ${text.substring(0, 200)}`);
            });
        }
        return response.json();
    })
    .then(result => {
        console.log('服务器响应:', result);
        if (result.code === 200) {
            alert('保存成功！');
            bootstrap.Modal.getInstance(document.getElementById('customerModal')).hide();
            loadCustomers();
        } else {
            alert('保存失败: ' + (result.message || result.msg || '未知错误'));
        }
    })
    .catch(error => {
        console.error('保存客户失败:', error);
        alert('保存失败: ' + (error.message || '网络错误，请检查服务器连接'));
    });
}

// 删除客户
function deleteCustomer(id) {
    if (confirm('确定要删除这个客户吗？')) {
        fetch(`/api/customer/${id}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                alert('删除成功！');
                loadCustomers();
            } else {
                alert('删除失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('删除客户失败:', error);
            alert('删除失败，请重试');
        });
    }
}

// 批量删除
function batchDelete() {
    if (selectedCustomers.length === 0) {
        alert('请先选择要删除的客户！');
        return;
    }
    
    if (confirm('确定要删除选中的 ' + selectedCustomers.length + ' 个客户吗？')) {
        fetch('/api/customer/batch', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(selectedCustomers)
        })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                alert('批量删除成功！');
                selectedCustomers = [];
                loadCustomers();
            } else {
                alert('批量删除失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('批量删除失败:', error);
            alert('批量删除失败，请重试');
        });
    }
}

// 全选/取消全选
function toggleSelectAll() {
    const selectAll = document.getElementById('selectAll');
    const checkboxes = document.querySelectorAll('.customer-checkbox');
    
    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAll.checked;
        if (selectAll.checked) {
            if (!selectedCustomers.includes(parseInt(checkbox.value))) {
                selectedCustomers.push(parseInt(checkbox.value));
            }
        } else {
            selectedCustomers = selectedCustomers.filter(id => id !== parseInt(checkbox.value));
        }
    });
}

// 切换客户选择状态
function toggleCustomerSelection(id) {
    const index = selectedCustomers.indexOf(id);
    if (index > -1) {
        selectedCustomers.splice(index, 1);
    } else {
        selectedCustomers.push(id);
    }
}

// 获取敏感状态文本
function getSensitiveStatusText(isSensitive) {
    if (isSensitive) {
        return '<span class="badge bg-warning">敏感</span>';
    } else {
        return '<span class="badge bg-success">普通</span>';
    }
}

// 当前操作的客户ID
let currentCustomerId = null;

// 显示密码验证模态框
function showPasswordModal() {
    document.getElementById('protectionPassword').value = '';
    new bootstrap.Modal(document.getElementById('passwordModal')).show();
}

// 验证密码
function verifyPassword() {
    // 用户要求：不需要验证密码，点击确定就关闭
    bootstrap.Modal.getInstance(document.getElementById('passwordModal')).hide();
    getFullCustomerData();
}

// 获取完整的客户数据
function getFullCustomerData() {
    // 用户要求：不需要验证密码，直接获取数据
    fetch(`/api/customer/${currentCustomerId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(result => {
        if (result.code === 200) {
            // 显示客户详情
            showCustomerDetail(result.data);
        } else {
            alert('获取数据失败: ' + result.message);
        }
    })
    .catch(error => {
        console.error('获取数据失败:', error);
        alert('获取数据失败');
    });
}

// 刷新客户列表
function refreshCustomers() {
    loadCustomers();
    alert('刷新成功！');
}

// 导入客户
function importCustomers() {
    alert('导入功能开发中...');
}

// 导出客户
function exportCustomers() {
    const dataStr = JSON.stringify(customers, null, 2);
    const dataBlob = new Blob([dataStr], {type: 'application/json'});
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'customers_' + new Date().toISOString().split('T')[0] + '.json';
    link.click();
    URL.revokeObjectURL(url);
}
