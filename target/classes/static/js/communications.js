// 沟通记录数据
let customerCommunications = [];
let selectedCommunications = [];

// 当前关联的客户ID（用于在客户详情中显示）
let currentCustomerIdForCommunication = null;

// 沟通类型映射
const communicationTypeMap = {
    '1': '电话',
    '2': '邮件',
    '3': '微信',
    '4': '面谈',
    '5': '其他'
};

// 重要程度映射
const importanceMap = {
    '1': '高',
    '2': '中',
    '3': '低'
};

// 获取沟通类型文本
function getCommunicationTypeText(type) {
    if (typeof type === 'string') {
        return communicationTypeMap[type] || '未知';
    }
    return communicationTypeMap[String(type)] || '未知';
}

// 获取重要程度文本
function getImportanceText(importance) {
    if (typeof importance === 'string') {
        return importanceMap[importance] || '未知';
    }
    return importanceMap[String(importance)] || '未知';
}

// 获取重要程度样式类
function getImportanceClass(importance) {
    const imp = String(importance);
    if (imp === '1') return 'bg-danger';
    if (imp === '2') return 'bg-warning';
    if (imp === '3') return 'bg-success';
    return 'bg-secondary';
}

// 格式化日期时间
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return date.toLocaleString('zh-CN');
}

// 加载客户沟通记录列表（从API）
function loadCustomerCommunications(customerId, tbodyId = 'customerCommunicationTableBody') {
    if (!customerId) {
        console.error('客户ID不能为空');
        return;
    }
    
    currentCustomerIdForCommunication = customerId;
    const tbody = document.getElementById(tbodyId);
    if (!tbody) {
        console.error('找不到表格tbody元素:', tbodyId);
        return;
    }
    
    tbody.innerHTML = '<tr><td colspan="7" class="text-center">加载中...</td></tr>';
    
    fetch(`/api/communication/customer/${customerId}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200 && result.data) {
                customerCommunications = result.data || [];
                renderCustomerCommunications(customerCommunications, tbodyId);
            } else {
                customerCommunications = [];
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">暂无沟通记录</td></tr>';
            }
        })
        .catch(error => {
            console.error('加载沟通记录失败:', error);
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-danger">加载失败，请刷新重试</td></tr>';
        });
}

// 渲染客户沟通记录表格
function renderCustomerCommunications(communications, tbodyId = 'customerCommunicationTableBody') {
    const tbody = document.getElementById(tbodyId);
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (!communications || communications.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">暂无沟通记录</td></tr>';
        return;
    }
    
    // 按时间倒序排列
    const sortedCommunications = [...communications].sort((a, b) => {
        const timeA = new Date(a.communicationTime || a.createTime || 0).getTime();
        const timeB = new Date(b.communicationTime || b.createTime || 0).getTime();
        return timeB - timeA;
    });
    
    sortedCommunications.forEach(communication => {
        const row = document.createElement('tr');
        const typeText = getCommunicationTypeText(communication.communicationType);
        const importanceText = getImportanceText(communication.importance);
        const importanceClass = getImportanceClass(communication.importance);
        const commTime = formatDateTime(communication.communicationTime);
        const subject = communication.subject || communication.content || '无主题';
        
        row.innerHTML = 
            '<td class="table-cell-truncate" title="' + typeText + '">' + typeText + '</td>' +
            '<td class="table-cell-truncate" title="' + (subject.length > 30 ? subject : subject) + '">' + 
                (subject.length > 30 ? subject.substring(0, 30) + '...' : subject) + 
            '</td>' +
            '<td><span class="badge ' + importanceClass + '">' + importanceText + '</span></td>' +
            '<td class="table-cell-truncate" title="' + commTime + '">' + commTime + '</td>' +
            '<td class="table-cell-truncate" title="' + (communication.communicatorName || communication.operator || '') + '">' + 
                (communication.communicatorName || communication.operator || '') + 
            '</td>' +
            '<td>' +
                '<button class="btn btn-sm btn-outline-primary me-1" onclick="viewCommunicationDetail(' + communication.id + ')" title="查看详情">' +
                    '<i class="bi bi-eye"></i>' +
                '</button>' +
                '<button class="btn btn-sm btn-outline-warning me-1" onclick="editCommunicationFromCustomer(' + communication.id + ')" title="编辑">' +
                    '<i class="bi bi-pencil"></i>' +
                '</button>' +
                '<button class="btn btn-sm btn-outline-danger" onclick="deleteCommunicationFromCustomer(' + communication.id + ')" title="删除">' +
                    '<i class="bi bi-trash"></i>' +
                '</button>' +
            '</td>';
        tbody.appendChild(row);
    });
}

// 显示新增沟通记录模态框（在客户详情中）
function showAddCommunicationModalForCustomer(customerId, customerName) {
    currentCustomerIdForCommunication = customerId;
    document.getElementById('communicationModalTitle').textContent = '新增沟通记录';
    document.getElementById('communicationForm').reset();
    document.getElementById('communicationId').value = '';
    
    // 自动填充客户信息
    const customerSelect = document.getElementById('customerSelect');
    const customerIdHidden = document.getElementById('customerIdHidden');
    if (customerSelect) {
        customerSelect.value = customerName || '';
        customerSelect.readOnly = true;
    }
    if (customerIdHidden) {
        customerIdHidden.value = customerId;
    }
    
    // 设置当前时间为默认沟通时间
    const now = new Date();
    const localDateTime = now.toISOString().slice(0, 16);
    const timeInput = document.getElementById('communicationTime');
    if (timeInput) {
        timeInput.value = localDateTime;
    }
    
    new bootstrap.Modal(document.getElementById('communicationModal')).show();
}

// 查看沟通记录详情
function viewCommunicationDetail(id) {
    fetch(`/api/communication/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200 && result.data) {
                const communication = result.data;
                showCommunicationDetail(communication);
            } else {
                alert('获取沟通记录详情失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('获取沟通记录详情失败:', error);
            alert('获取沟通记录详情失败，请重试');
        });
}

// 显示沟通记录详情内容
function showCommunicationDetail(communication) {
    const typeText = getCommunicationTypeText(communication.communicationType);
    const importanceText = getImportanceText(communication.importance);
    const commTime = formatDateTime(communication.communicationTime);
    const createTime = formatDateTime(communication.createTime);
    
    const content = 
        '<div class="row">' +
            '<div class="col-md-6">' +
                '<h6>基本信息</h6>' +
                '<p><strong>客户名称：</strong>' + (communication.customerName || '未填写') + '</p>' +
                '<p><strong>沟通类型：</strong>' + typeText + '</p>' +
                '<p><strong>重要程度：</strong>' + importanceText + '</p>' +
                '<p><strong>沟通时间：</strong>' + commTime + '</p>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<h6>详细信息</h6>' +
                '<p><strong>沟通主题：</strong>' + (communication.subject || communication.summary || '无主题') + '</p>' +
                '<p><strong>操作人：</strong>' + (communication.communicatorName || communication.operator || '未填写') + '</p>' +
                '<p><strong>创建时间：</strong>' + createTime + '</p>' +
            '</div>' +
        '</div>' +
        '<div class="row mt-3">' +
            '<div class="col-12">' +
                '<h6>沟通内容</h6>' +
                '<p>' + (communication.content || communication.summary || '无') + '</p>' +
            '</div>' +
        '</div>' +
        '<div class="row mt-3">' +
            '<div class="col-12">' +
                '<h6>后续跟进</h6>' +
                '<p>' + (communication.followUpTask || '无') + '</p>' +
            '</div>' +
        '</div>';
    
    const detailContent = document.getElementById('communicationDetailContent');
    if (detailContent) {
        detailContent.innerHTML = content;
        new bootstrap.Modal(document.getElementById('communicationDetailModal')).show();
    }
}

// 编辑沟通记录（从客户详情中）
function editCommunicationFromCustomer(id) {
    fetch(`/api/communication/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200 && result.data) {
                const communication = result.data;
                showEditCommunicationModal(communication);
            } else {
                alert('获取沟通记录失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            console.error('获取沟通记录失败:', error);
            alert('获取沟通记录失败，请重试');
        });
}

// 显示编辑沟通记录模态框
function showEditCommunicationModal(communication) {
    document.getElementById('communicationModalTitle').textContent = '编辑沟通记录';
    document.getElementById('communicationId').value = communication.id;
    
    const customerSelect = document.getElementById('customerSelect');
    const customerIdHidden = document.getElementById('customerIdHidden');
    if (customerSelect) {
        customerSelect.value = communication.customerName || '';
        customerSelect.readOnly = true;
    }
    if (customerIdHidden) {
        customerIdHidden.value = communication.customerId || '';
    }
    
    document.getElementById('communicationTypeSelect').value = communication.communicationType || '';
    document.getElementById('importanceSelect').value = communication.importance || '';
    
    // 格式化时间用于datetime-local输入
    if (communication.communicationTime) {
        const date = new Date(communication.communicationTime);
        const localDateTime = date.toISOString().slice(0, 16);
        document.getElementById('communicationTime').value = localDateTime;
    }
    
    document.getElementById('subject').value = communication.subject || communication.summary || '';
    document.getElementById('content').value = communication.content || '';
    document.getElementById('followUp').value = communication.followUpTask || '';
    
    new bootstrap.Modal(document.getElementById('communicationModal')).show();
}

// 保存沟通记录
function saveCommunication() {
    const form = document.getElementById('communicationForm');
    const formData = new FormData(form);
    
    const communicationId = document.getElementById('communicationId').value;
    const customerIdHidden = document.getElementById('customerIdHidden');
    const customerId = (customerIdHidden && customerIdHidden.value) || currentCustomerIdForCommunication;
    
    if (!customerId) {
        alert('客户ID不能为空！');
        return;
    }
    
    // 获取客户名称
    const customerSelect = document.getElementById('customerSelect');
    const customerName = (customerSelect && customerSelect.value) || '';
    
    const communicationData = {
        customerId: parseInt(customerId),
        customerName: customerName,
        communicationType: parseInt(formData.get('communicationType')),
        importance: parseInt(formData.get('importance')),
        communicationTime: formData.get('communicationTime'),
        subject: formData.get('subject'),
        content: formData.get('content'),
        followUpTask: formData.get('followUp')
    };
    
    const url = communicationId ? `/api/communication/${communicationId}` : `/api/communication`;
    const method = communicationId ? 'PUT' : 'POST';
    
    if (communicationId) {
        communicationData.id = parseInt(communicationId);
    }
    
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(communicationData)
    })
    .then(response => response.json())
    .then(result => {
        if (result.code === 200) {
            alert('保存成功！');
            bootstrap.Modal.getInstance(document.getElementById('communicationModal')).hide();
            
            // 如果是在客户详情中，重新加载沟通记录
            if (currentCustomerIdForCommunication) {
                loadCustomerCommunications(currentCustomerIdForCommunication);
            }
        } else {
            alert('保存失败: ' + (result.message || '未知错误'));
        }
    })
    .catch(error => {
        console.error('保存沟通记录失败:', error);
        alert('保存失败，请重试');
    });
}

// 删除沟通记录（从客户详情中）
function deleteCommunicationFromCustomer(id) {
    if (!confirm('确定要删除这个沟通记录吗？')) {
        return;
    }
    
    fetch(`/api/communication/${id}`, {
        method: 'DELETE'
    })
    .then(response => response.json())
    .then(result => {
        if (result.code === 200) {
            alert('删除成功！');
            
            // 如果是在客户详情中，重新加载沟通记录
            if (currentCustomerIdForCommunication) {
                loadCustomerCommunications(currentCustomerIdForCommunication);
            }
        } else {
            alert('删除失败: ' + (result.message || '未知错误'));
        }
    })
    .catch(error => {
        console.error('删除沟通记录失败:', error);
        alert('删除失败，请重试');
    });
}

