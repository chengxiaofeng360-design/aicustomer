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
        const subject = communication.summary || communication.content || '无主题';
        
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

// 为指定客户显示新增沟通记录模态框
function showAddCommunicationModalForCustomer(customerId, customerName) {
    // 保存当前客户ID，用于后续操作
    currentCustomerIdForCommunication = customerId;
    
    // 重置表单
    const communicationModalTitle = document.getElementById('communicationModalTitle');
    if (communicationModalTitle) {
        communicationModalTitle.textContent = '新增沟通记录';
    }
    
    const communicationForm = document.getElementById('communicationForm');
    if (communicationForm) {
        communicationForm.reset();
        // 清空隐藏的ID字段
        const communicationIdField = communicationForm.querySelector('#communicationId');
        if (communicationIdField) {
            communicationIdField.value = '';
        }
        
        // 设置客户ID（支持多种可能的字段名）
        const possibleCustomerIdFields = ['#modalCustomerId', '#customerIdHidden'];
        for (const selector of possibleCustomerIdFields) {
            const field = communicationForm.querySelector(selector);
            if (field) {
                field.value = customerId;
            }
        }
        
        // 尝试找到customerSelect字段并设置值
        const customerSelect = communicationForm.querySelector('#customerSelect');
        if (customerSelect) {
            customerSelect.value = customerId || '';
            customerSelect.readOnly = true;
        }
        
        // 设置客户名称显示（如果有customerName字段）
        const customerNameField = communicationForm.querySelector('#customerName');
        if (customerNameField) {
            customerNameField.value = customerName;
        }
        
        // 设置当前时间为默认沟通时间
        const now = new Date();
        const localDateTime = now.toISOString().slice(0, 16);
        const timeInput = document.getElementById('communicationTime');
        if (timeInput) {
            timeInput.value = localDateTime;
        }
    }
    
    // 显示模态框
    const communicationModal = document.getElementById('communicationModal');
    if (communicationModal) {
        new bootstrap.Modal(communicationModal).show();
    }
}

// 查看沟通记录详情
function viewCommunicationDetail(id) {
    console.log('开始查看沟通记录详情，ID:', id);
    fetch(`/api/communication/${id}`)
        .then(response => {
            console.log('API响应状态:', response.status);
            return response.json();
        })
        .then(result => {
            console.log('API响应结果:', result);
            if (result.code === 200 && result.data) {
                const communication = result.data;
                console.log('获取到沟通记录数据:', communication);
                showCommunicationDetail(communication);
            } else {
                console.error('API返回错误:', result.message || '未知错误');
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
    console.log('开始显示沟通记录详情:', communication);
    const typeText = getCommunicationTypeText(communication.communicationType);
    const importanceText = getImportanceText(communication.importance);
    const commTime = formatDateTime(communication.communicationTime);
    const createTime = formatDateTime(communication.createTime);
    
    const content = 
        '<div class="row">' +
            '<div class="col-md-6">' +
                '<h6>基本信息</h6>' +
                '<p><strong>沟通类型：</strong>' + typeText + '</p>' +
                '<p><strong>重要程度：</strong>' + importanceText + '</p>' +
                '<p><strong>沟通时间：</strong>' + commTime + '</p>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<h6>详细信息</h6>' +
                '<p><strong>沟通主题：</strong>' + (communication.summary || '无主题') + '</p>' +
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
    console.log('找到communicationDetailContent:', detailContent);
    
    if (detailContent) {
        detailContent.innerHTML = content;
        console.log('设置详情内容完成');
        
        // 首先隐藏可能存在的沟通记录列表模态框
        const listModalElement = document.getElementById('communicationListModal');
        if (listModalElement) {
            try {
                // 获取模态框实例并隐藏
                const listModal = bootstrap.Modal.getInstance(listModalElement);
                if (listModal) {
                    listModal.hide();
                }
                
                // 等待隐藏动画完成后移除模态框和遮罩层
                setTimeout(() => {
                    // 移除模态框元素
                    if (listModalElement.parentNode) {
                        listModalElement.parentNode.removeChild(listModalElement);
                    }
                    
                    // 移除可能存在的遮罩层
                    const backdrops = document.querySelectorAll('.modal-backdrop');
                    backdrops.forEach(backdrop => {
                        if (backdrop.parentNode) {
                            backdrop.parentNode.removeChild(backdrop);
                        }
                    });
                }, 150); // 等待Bootstrap的隐藏动画完成
            } catch (error) {
                console.warn('隐藏沟通记录列表模态框时发生错误:', error);
            }
        }
        
        // 等待一段时间确保列表模态框完全隐藏后再显示详情模态框
        setTimeout(() => {
            const modalElement = document.getElementById('communicationDetailModal');
            console.log('找到communicationDetailModal:', modalElement);
            
            if (modalElement) {
                try {
                    // 确保移除所有可能存在的遮罩层
                    const backdrops = document.querySelectorAll('.modal-backdrop');
                    backdrops.forEach(backdrop => {
                        if (backdrop.parentNode) {
                            backdrop.parentNode.removeChild(backdrop);
                        }
                    });
                    
                    // 显示详情模态框
                    const modal = new bootstrap.Modal(modalElement);
                    console.log('创建模态框实例:', modal);
                    modal.show();
                    console.log('调用模态框show()方法');
                } catch (error) {
                    console.error('显示模态框时发生错误:', error);
                    alert('显示模态框时发生错误: ' + error.message);
                }
            } else {
                console.error('未找到communicationDetailModal元素');
                alert('未找到详情模态框，请检查页面结构');
            }
        }, 200); // 等待足够时间确保列表模态框完全隐藏
    } else {
        console.error('未找到communicationDetailContent元素');
        alert('未找到详情内容容器，请检查页面结构');
    }
}

// 测试模态框显示的函数（用于调试）
function testCommunicationDetailModal() {
    console.log('开始测试沟通记录详情模态框');
    
    // 检查Bootstrap是否加载
    if (typeof bootstrap === 'undefined') {
        console.error('Bootstrap未加载');
        alert('Bootstrap未加载');
        return;
    }
    
    const modalElement = document.getElementById('communicationDetailModal');
    if (modalElement) {
        console.log('找到模态框元素:', modalElement);
        
        // 设置测试内容
        const detailContent = document.getElementById('communicationDetailContent');
        if (detailContent) {
            detailContent.innerHTML = '<p>这是测试内容</p>';
        }
        
        // 显示模态框
        try {
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
            console.log('模态框显示成功');
        } catch (error) {
            console.error('模态框显示失败:', error);
            alert('模态框显示失败: ' + error.message);
        }
    } else {
        console.error('未找到模态框元素');
        alert('未找到模态框元素');
    }
}

// 编辑沟通记录（从客户列表中）
function editCommunicationFromCustomer(id) {
    // 首先隐藏可能存在的沟通记录详情模态框
    const detailModalElement = document.getElementById('communicationDetailModal');
    if (detailModalElement) {
        try {
            const detailModal = bootstrap.Modal.getInstance(detailModalElement);
            if (detailModal) {
                detailModal.hide();
            }
        } catch (error) {
            console.warn('隐藏沟通记录详情模态框时发生错误:', error);
        }
    }
    
    fetch(`/api/communication/${id}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200 && result.data) {
                const communication = result.data;
                showEditCommunicationModal(communication);
            } else {
                console.error('获取沟通记录失败:', result);
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
    const communicationModalTitle = document.getElementById('communicationModalTitle');
    if (communicationModalTitle) {
        communicationModalTitle.textContent = '编辑沟通记录';
    }
    
    const communicationIdField = document.getElementById('communicationId');
    if (communicationIdField) {
        communicationIdField.value = communication.id;
    }
    
    // 设置客户信息（支持多种可能的字段名）
    const customerSelect = document.getElementById('customerSelect');
    if (customerSelect) {
        customerSelect.value = communication.customerName || communication.customerId || '';
        customerSelect.readOnly = true;
    }
    
    // 设置客户ID（支持多种可能的字段名）
    const possibleCustomerIdFields = ['#modalCustomerId', '#customerIdHidden'];
    for (const selector of possibleCustomerIdFields) {
        const field = document.querySelector(selector);
        if (field) {
            field.value = communication.customerId || '';
        }
    }
    
    // 设置沟通类型（支持两种可能的字段名）
    const communicationTypeField = document.getElementById('communicationTypeSelect') || document.getElementById('communicationType');
    if (communicationTypeField) {
        communicationTypeField.value = communication.communicationType || '';
    }
    
    const importanceSelect = document.getElementById('importanceSelect');
    if (importanceSelect) {
        importanceSelect.value = communication.importance || '';
    }
    
    // 格式化时间用于datetime-local输入
    const communicationTimeField = document.getElementById('communicationTime');
    if (communicationTimeField && communication.communicationTime) {
        const date = new Date(communication.communicationTime);
        const localDateTime = date.toISOString().slice(0, 16);
        communicationTimeField.value = localDateTime;
    }
    
    const subjectField = document.getElementById('subject');
    if (subjectField) {
        subjectField.value = communication.summary || communication.subject || '';
    }
    
    const contentField = document.getElementById('content');
    if (contentField) {
        contentField.value = communication.content || '';
    }
    
    const followUpField = document.getElementById('followUp');
    if (followUpField) {
        followUpField.value = communication.followUpTask || communication.followUp || '';
    }
    
    // 显示模态框
    const communicationModal = document.getElementById('communicationModal');
    if (communicationModal) {
        new bootstrap.Modal(communicationModal).show();
    }
}

// 保存沟通记录
function saveCommunication() {
    const form = document.getElementById('communicationForm');
    if (!form) {
        alert('表单未找到！');
        return;
    }
    
    const formData = new FormData(form);
    
    const communicationId = document.getElementById('communicationId').value;
    
    // 尝试从多个可能的地方获取客户ID
    let customerId = null;
    
    // 1. 从modalCustomerId元素获取（来自customers.html的新增/编辑表单）
    const modalCustomerIdElement = document.getElementById('modalCustomerId');
    if (modalCustomerIdElement) {
        customerId = modalCustomerIdElement.value;
    }
    
    // 2. 如果没有，从customerIdHidden元素获取（来自customers.html的模态框）
    if (!customerId) {
        const customerIdHiddenElement = document.getElementById('customerIdHidden');
        if (customerIdHiddenElement) {
            customerId = customerIdHiddenElement.value;
        }
    }
    
    // 3. 如果没有，从currentCustomerIdForCommunication变量获取
    if (!customerId) {
        customerId = currentCustomerIdForCommunication;
    }
    
    // 4. 如果没有，从currentViewingCustomer获取（客户详情页）
    if (!customerId && typeof currentViewingCustomer !== 'undefined' && currentViewingCustomer) {
        customerId = currentViewingCustomer.id;
    }
    
    if (!customerId) {
        alert('客户ID不能为空！');
        return;
    }
    
    // 获取客户名称
    const customerSelect = document.getElementById('customerSelect');
    // 优先从customerSelect获取（即使字段隐藏，值也应该被设置）
    let customerName = (customerSelect && customerSelect.value) || '';
    
    // 如果customerSelect为空，尝试从customers.js中的当前客户信息获取
    if (!customerName && typeof currentViewingCustomer !== 'undefined' && currentViewingCustomer) {
        customerName = currentViewingCustomer.customerName || '';
    }
    
    // 如果还是没有，尝试通过customerId查询客户信息
    if (!customerName && customerId) {
        console.warn('客户名称为空，尝试通过customerId查询:', customerId);
        // 如果customerName为空，尝试从currentViewingCustomer获取
        if (typeof currentViewingCustomer !== 'undefined' && currentViewingCustomer && currentViewingCustomer.id == customerId) {
            customerName = currentViewingCustomer.customerName || '';
        }
        // 如果还是没有，使用一个默认值（避免数据库NOT NULL约束错误）
        if (!customerName) {
            customerName = '客户ID:' + customerId;
            console.warn('使用默认客户名称:', customerName);
        }
    }
    
    console.log('获取到的客户信息 - ID:', customerId, '名称:', customerName);
    
    // 处理日期时间格式：将 datetime-local 格式转换为 ISO 8601 格式
    let communicationTime = formData.get('communicationTime');
    if (communicationTime) {
        // datetime-local 格式是 "YYYY-MM-DDTHH:mm"，需要转换为 "YYYY-MM-DDTHH:mm:ss"
        if (communicationTime.length === 16) {
            communicationTime = communicationTime + ':00';
        }
    }
    
    const communicationData = {
        customerId: parseInt(customerId),
        customerName: customerName || '',
        communicationType: parseInt(formData.get('communicationType')),
        importance: parseInt(formData.get('importance')) || 1,
        communicationTime: communicationTime || null,
        summary: formData.get('subject') || '', // 将subject映射到summary字段
        content: formData.get('content') || '',
        followUpTask: formData.get('followUp') || ''
    };
    
    // 验证必填字段
    if (!communicationData.customerId || isNaN(communicationData.customerId)) {
        alert('客户ID不能为空！');
        return;
    }
    if (!communicationData.customerName || communicationData.customerName.trim() === '') {
        alert('客户名称不能为空！');
        return;
    }
    if (!communicationData.communicationType || isNaN(communicationData.communicationType)) {
        alert('请选择沟通类型！');
        return;
    }
    
    console.log('准备发送的沟通记录数据:', communicationData);
    
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
            
            // 重新加载沟通记录列表
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

// 删除沟通记录（从客户列表中）
function deleteCommunicationFromCustomer(id) {
    if (!confirm('确定要删除这个沟通记录吗？')) {
        return;
    }
    
    // 首先隐藏可能存在的沟通记录详情模态框
    const detailModalElement = document.getElementById('communicationDetailModal');
    if (detailModalElement) {
        try {
            const detailModal = bootstrap.Modal.getInstance(detailModalElement);
            if (detailModal) {
                detailModal.hide();
            }
        } catch (error) {
            console.warn('隐藏沟通记录详情模态框时发生错误:', error);
        }
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

