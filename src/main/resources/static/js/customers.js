// 客户数据（从API获取）
let customers = [];
// 已删除批量删除功能，不再需要selectedCustomers

// 分页相关变量
let currentPage = 1;
let pageSize = 10; // 每页显示10条记录
let totalRecords = 0; // 总记录数

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

// 进度映射
const progressMap = {
    '0': '未开始',
    '1': '进行中',
    '2': '暂停中',
    '3': '已成功',
    '4': '放弃'
};

// 进度样式映射
const progressClassMap = {
    '0': 'bg-secondary',
    '1': 'bg-primary',
    '2': 'bg-warning',
    '3': 'bg-success',
    '4': 'bg-danger'
};

// 业务类型映射（具体业务类型1-6，从系统配置动态加载，这里是默认值）
let businessTypeMap = {
    '1': '品种权申请客户',
    '2': '品种权转化推广客户',
    '3': '知识产权互补协作客户',
    '4': '科普教育合作客户',
    '5': '景观设计服务客户',
    '6': '图书出版客户'
};

// 业务类型大类到具体类型的映射（用于分类页面）
const businessCategoryToTypes = {
    '品种权业务': [1, 2],  // 品种权业务：品种权申请客户、品种权转化推广客户
    '知识产权业务': [3],     // 知识产权业务：知识产权互补协作客户
    '服务业务': [4, 5, 6]   // 服务业务：科普教育合作客户、景观设计服务客户、图书出版客户
};

// 当前选中的业务类型列表（支持多个）
let currentBusinessTypeList = null;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 从URL参数获取业务类型列表（支持多个businessType参数）
    const urlParams = new URLSearchParams(window.location.search);
    const businessTypes = urlParams.getAll('businessType'); // 获取所有businessType参数
    
    if (businessTypes && businessTypes.length > 0) {
        currentBusinessTypeList = businessTypes.map(t => parseInt(t)).filter(t => !isNaN(t));
        // 更新页面标题
        const pageTitle = document.querySelector('.page-title');
        if (pageTitle && currentBusinessTypeList.length > 0) {
            let titleText = '';
            if (currentBusinessTypeList.length === 1) {
                titleText = '客户管理 - ' + (businessTypeMap[currentBusinessTypeList[0].toString()] || '客户管理');
            } else {
                titleText = '客户管理 - 业务类型客户';
            }
            pageTitle.innerHTML = '<i class="bi bi-people-fill"></i> ' + titleText;
        }
    }
    
    // 从系统配置加载业务类型
    loadBusinessTypesFromConfig();
    
    loadStatistics();
    loadCustomers();
    updateTotalCount();
    
    // 初始化文件上传功能
    initFileUpload();
    
    // 不再自动启动语音识别，只在用户点击打开模态框时启动
    // 移除自动启动，避免权限错误和用户体验问题
});

// 从系统配置加载业务类型
function loadBusinessTypesFromConfig() {
    const url = '/api/system-config/group/' + encodeURIComponent('业务类型');
    console.log('🔍 加载业务类型配置，URL:', url);
    
    fetch(url)
        .then(response => {
            console.log('🔍 API响应状态:', response.status, response.statusText);
            if (!response.ok) {
                if (response.status === 404) {
                    console.warn('⚠️ 系统配置API不存在（404），使用默认业务类型');
                    updateBusinessTypeSelectOptions();
                    return null;
                }
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(result => {
            if (!result) return; // 404情况已处理
            
            console.log('🔍 API响应数据:', result);
            if (result.code === 200 && result.data && result.data.length > 0) {
                // 清空现有映射
                businessTypeMap = {};
                
                // 按配置键排序并构建映射
                result.data
                    .filter(config => config.configKey && config.configKey.startsWith('business.type.'))
                    .sort((a, b) => {
                        const numA = parseInt(a.configKey.replace('business.type.', ''));
                        const numB = parseInt(b.configKey.replace('business.type.', ''));
                        return numA - numB;
                    })
                    .forEach((config) => {
                        const key = config.configKey.replace('business.type.', '');
                        businessTypeMap[key] = config.configValue;
                    });
                
                console.log('✅ 已从系统配置加载业务类型:', businessTypeMap);
            } else {
                console.log('⚠️ 系统配置中没有业务类型数据，使用默认值');
            }
            
            // 无论是否从配置加载成功，都要更新下拉框选项
            updateBusinessTypeSelectOptions();
        })
        .catch(error => {
            console.warn('⚠️ 加载业务类型配置失败，使用默认值:', error);
            // 即使加载失败，也要更新下拉框（使用默认值）
            updateBusinessTypeSelectOptions();
        });
}

// 更新业务类型下拉框选项
function updateBusinessTypeSelectOptions() {
    const select = document.getElementById('businessTypeSelect');
    if (!select) return;
    
    // 保存当前选中的值
    const currentValue = select.value;
    
    // 清空现有选项（保留第一个"请选择"选项）
    const firstOption = select.options[0];
    select.innerHTML = '';
    if (firstOption && firstOption.value === '') {
        select.appendChild(firstOption);
    } else {
        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = '请选择业务类型';
        select.appendChild(defaultOption);
    }
    
    // 添加所有业务类型选项（1-6）
    Object.keys(businessTypeMap).sort((a, b) => parseInt(a) - parseInt(b)).forEach(key => {
        const option = document.createElement('option');
        option.value = key;
        option.textContent = businessTypeMap[key];
        select.appendChild(option);
    });
    
    // 恢复之前选中的值
    if (currentValue) {
        select.value = currentValue;
    }
}

// 加载统计数据
function loadStatistics() {
    // 构建查询参数，包含当前业务类型列表
    const params = new URLSearchParams();
    if (currentBusinessTypeList && currentBusinessTypeList.length > 0) {
        currentBusinessTypeList.forEach(type => {
            params.append('businessType', type.toString());
        });
    }
    
    const url = '/api/customer/statistics' + (params.toString() ? '?' + params.toString() : '');
    
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('获取统计数据失败');
            }
            return response.json();
        })
        .then(result => {
            if (result && result.code === 200 && result.data) {
                const stats = result.data;
                
                // 更新客户总数
                const totalElement = document.getElementById('statsTotalCustomers');
                if (totalElement) {
                    totalElement.textContent = formatNumber(stats.totalCustomers || 0);
                }
                
                // 更新重要用户（VIP和钻石客户数量，除去普通用户）
                const satisfactionElement = document.getElementById('statsSatisfaction');
                if (satisfactionElement) {
                    const vipDiamondCount = (stats.vipCount || 0) + (stats.diamondCount || 0);
                    satisfactionElement.textContent = formatNumber(vipDiamondCount);
                }
                
                // 更新本月新增
                const newThisMonthElement = document.getElementById('statsNewThisMonth');
                if (newThisMonthElement) {
                    newThisMonthElement.textContent = formatNumber(stats.newThisMonth || 0);
                }
                
                // 更新潜在客户
                const potentialElement = document.getElementById('statsPotentialCustomers');
                if (potentialElement) {
                    potentialElement.textContent = formatNumber(stats.potentialCount || 0);
                }
            }
        })
        .catch(error => {
            console.error('加载统计数据失败:', error);
            // 失败时显示默认值
            const totalElement = document.getElementById('statsTotalCustomers');
            if (totalElement) totalElement.textContent = '0';
            const newThisMonthElement = document.getElementById('statsNewThisMonth');
            if (newThisMonthElement) newThisMonthElement.textContent = '0';
            const potentialElement = document.getElementById('statsPotentialCustomers');
            if (potentialElement) potentialElement.textContent = '0';
        });
}

// 格式化数字（添加千位分隔符）
function formatNumber(num) {
    if (num === null || num === undefined) return '0';
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// 加载客户列表
function loadCustomers(page = currentPage) {
    currentPage = page;
    const tbody = document.getElementById('customerTableBody');
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">加载中...</td></tr>';

    // 构建查询参数
    const params = new URLSearchParams();
    params.append('pageNum', currentPage.toString());
    params.append('pageSize', pageSize.toString());
    
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
    if (region) {
        params.append('region', region);
    }
    
    // 如果有业务类型列表参数，添加到查询条件（支持多个businessType）
    if (currentBusinessTypeList && currentBusinessTypeList.length > 0) {
        currentBusinessTypeList.forEach(type => {
            params.append('businessType', type.toString());
        });
    }
    
    const apiUrl = '/api/customer/page?' + params.toString();
    console.log('加载客户列表，URL:', apiUrl);
    console.log('当前业务类型列表:', currentBusinessTypeList);
    
    fetch(apiUrl)
        .then(response => {
            console.log('API响应状态:', response.status, response.statusText);
            if (!response.ok) {
                // 如果HTTP状态码不是200，尝试解析错误信息
                return response.json().then(err => {
                    throw new Error(err.message || '服务器错误: ' + response.status);
                });
            }
            return response.json();
        })
        .then(result => {
            console.log('API响应数据:', result);
            if (result && result.code === 200 && result.data) {
                const pageResult = result.data;
                customers = pageResult.list || [];
                totalRecords = pageResult.total || 0;
                console.log('加载到客户数量:', customers.length, '总数:', totalRecords);
                renderCustomerTable(customers);
                updateTotalCount();
                renderPagination();
            } else {
                const errorMsg = (result && result.message) || (result && result.error) || '未知错误';
                console.error('API返回错误:', errorMsg, result);
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-danger">加载失败: ' + errorMsg + '</td></tr>';
            }
        })
        .catch(error => {
            console.error('加载客户列表失败:', error);
            const errorMsg = error.message || '网络错误或服务器未响应';
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-danger">加载失败: ' + errorMsg + '<br><small>请检查数据库连接或稍后重试</small></td></tr>';
        });
}

// 渲染客户表格
function renderCustomerTable(customerList) {
    const tbody = document.getElementById('customerTableBody');
    tbody.innerHTML = '';
    
    if (customerList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">暂无数据</td></tr>';
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
        
        // 处理进度字段
        const progress = customer.progress !== null && customer.progress !== undefined ? customer.progress : 0;
        const progressText = progressMap[progress] || progressMap['0'];
        const progressBadgeClass = progressClassMap[progress] || progressClassMap['0'];
        
        // 处理业务类型字段
        const businessType = customer.businessType !== null && customer.businessType !== undefined ? customer.businessType : 1;
        const businessTypeText = businessType ? (businessTypeMap[businessType.toString()] || '') : '-';
        
        const createTime = customer.createTime ? new Date(customer.createTime).toLocaleString('zh-CN') : '';
        
        row.innerHTML = 
            '<td class="table-cell-truncate" title="' + (customer.customerName || '') + '">' + (customer.customerName || '') + '</td>' +
            '<td class="table-cell-truncate" title="' + (customer.contactPerson || '') + '">' + (customer.contactPerson || '') + '</td>' +
            '<td class="table-cell-truncate" title="' + customerTypeText + '">' + customerTypeText + '</td>' +
            '<td class="table-cell-truncate">' +
                '<span class="badge ' + levelBadgeClass + '">' + customerLevelText + '</span>' +
            '</td>' +
            '<td class="table-cell-truncate">' +
                '<span class="badge ' + progressBadgeClass + '">' + progressText + '</span>' +
            '</td>' +
            '<td class="table-cell-truncate" title="' + businessTypeText + '">' + businessTypeText + '</td>' +
            '<td class="table-cell-truncate">' + sensitiveStatus + '</td>' +
            '<td>' +
                '<div class="action-buttons">' +
                    '<button class="btn btn-sm btn-outline-info" onclick="goToCommunications(' + customer.id + ')" title="沟通管理">' +
                    '<i class="bi bi-chat-dots"></i> 沟通管理' +
                '</button>' +
                    '<button class="btn btn-sm btn-outline-primary" onclick="viewCustomer(' + customer.id + ')" title="查看详情">' +
                    '<i class="bi bi-eye"></i> 详情' +
                '</button>' +
                    '<button class="btn btn-sm btn-outline-warning" onclick="editCustomer(' + customer.id + ')" title="编辑客户">' +
                    '<i class="bi bi-pencil"></i> 编辑' +
                '</button>' +
                    '<button class="btn btn-sm btn-outline-danger" onclick="deleteCustomer(' + customer.id + ')" title="删除客户">' +
                    '<i class="bi bi-trash"></i> 删除' +
                '</button>' +
                '</div>' +
            '</td>';
        tbody.appendChild(row);
    });
}

// 更新总记录数
function updateTotalCount() {
    const totalCountElement = document.getElementById('totalCustomers');
    const paginationInfoElement = document.getElementById('paginationInfo');
    
    const start = totalRecords > 0 ? (currentPage - 1) * pageSize + 1 : 0;
    const end = Math.min(currentPage * pageSize, totalRecords);
    
    if (totalCountElement) {
        totalCountElement.textContent = formatNumber(totalRecords);
    }
    
    if (paginationInfoElement) {
        if (totalRecords > 0) {
            paginationInfoElement.textContent = `共 ${formatNumber(totalRecords)} 条记录，当前显示第 ${start}-${end} 条`;
        } else {
            paginationInfoElement.textContent = '共 0 条记录，当前显示第 0-0 条';
        }
    }
}

// 渲染分页控件
function renderPagination() {
    const paginationNav = document.getElementById('paginationNav');
    if (!paginationNav) return;
    
    const totalPages = Math.ceil(totalRecords / pageSize);
    paginationNav.innerHTML = '';
    
    if (totalPages <= 1) {
        return; // 只有一页或没有数据时，不显示分页控件
    }
    
    // 上一页按钮
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = `<a class="page-link" href="javascript:void(0)" onclick="previousPage()">上一页</a>`;
    paginationNav.appendChild(prevLi);
    
    // 页码按钮（最多显示7个页码）
    const maxVisiblePages = 7;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
    
    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }
    
    // 第一页
    if (startPage > 1) {
        const firstLi = document.createElement('li');
        firstLi.className = 'page-item';
        firstLi.innerHTML = `<a class="page-link" href="javascript:void(0)" onclick="goToPage(1)">1</a>`;
        paginationNav.appendChild(firstLi);
        
        if (startPage > 2) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = `<span class="page-link">...</span>`;
            paginationNav.appendChild(ellipsisLi);
        }
    }
    
    // 页码按钮
    for (let i = startPage; i <= endPage; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageLi.innerHTML = `<a class="page-link" href="javascript:void(0)" onclick="goToPage(${i})">${i}</a>`;
        paginationNav.appendChild(pageLi);
    }
    
    // 最后一页
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = `<span class="page-link">...</span>`;
            paginationNav.appendChild(ellipsisLi);
        }
        
        const lastLi = document.createElement('li');
        lastLi.className = 'page-item';
        lastLi.innerHTML = `<a class="page-link" href="javascript:void(0)" onclick="goToPage(${totalPages})">${totalPages}</a>`;
        paginationNav.appendChild(lastLi);
    }
    
    // 下一页按钮
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link" href="javascript:void(0)" onclick="nextPage()">下一页</a>`;
    paginationNav.appendChild(nextLi);
}

// 跳转到指定页码
function goToPage(page) {
    const totalPages = Math.ceil(totalRecords / pageSize);
    if (page < 1 || page > totalPages || page === currentPage) {
        return;
    }
    loadCustomers(page);
}

// 上一页
function previousPage() {
    if (currentPage > 1) {
        goToPage(currentPage - 1);
    }
}

// 下一页
function nextPage() {
    const totalPages = Math.ceil(totalRecords / pageSize);
    if (currentPage < totalPages) {
        goToPage(currentPage + 1);
    }
}

// 搜索客户
function searchCustomers() {
    currentPage = 1; // 搜索时重置到第一页
    loadCustomers(1); // 直接调用loadCustomers，它已经包含了搜索参数
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
    
    // 设置默认值：第一种类型（品种权申请客户）
    const businessTypeSelect = document.getElementById('businessTypeSelect');
    if (businessTypeSelect) {
        if (currentBusinessTypeList && currentBusinessTypeList.length === 1) {
            businessTypeSelect.value = currentBusinessTypeList[0].toString();
        } else {
            businessTypeSelect.value = '1'; // 默认第一种类型
        }
    }
    
    new bootstrap.Modal(document.getElementById('customerModal')).show();
}

// 业务类型下拉框已通过updateBusinessTypeSelectOptions函数更新，无需动态监听

// 显示AI识别模态框
function showAIRecognition() {
    new bootstrap.Modal(document.getElementById('aiRecognitionModal')).show();
}

// 显示文件上传模态框
function showFileUpload() {
    console.log('显示文件上传模态框...');
    
    // 重置文件上传状态
    resetImportFileSelection(true);
    
    // 显示模态框
    const modalElement = document.getElementById('fileUploadModal');
    if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
        
        // 模态框显示后，重新初始化文件上传功能
        modalElement.addEventListener('shown.bs.modal', function onModalShown() {
            console.log('模态框已显示，重新初始化文件上传功能');
            // 重置初始化标志，允许重新初始化
            fileUploadInitialized = false;
            // 重新初始化文件上传功能
            initFileUpload();
        }, { once: true });
        
        // 模态框关闭时清理状态
        modalElement.addEventListener('hidden.bs.modal', function onModalHidden() {
            console.log('模态框已关闭，清理文件上传状态');
            // 重置初始化标志
            fileUploadInitialized = false;
            // 清空文件选择
            resetImportFileSelection(true);
        }, { once: false });
        
        // 如果模态框已经显示，立即初始化
        setTimeout(function() {
            if (modalElement.classList.contains('show')) {
                console.log('模态框已显示，立即初始化文件上传功能');
                fileUploadInitialized = false;
                initFileUpload();
            }
        }, 100);
    } else {
        console.error('找不到fileUploadModal元素');
        alert('无法打开文件上传对话框，请刷新页面重试');
    }
}

// 注意：文件选择现在通过HTML的label标签实现，无需JavaScript触发
// label标签的for属性关联到fileInput，点击label会自动触发文件选择对话框

// 清空批量导入选择
function resetImportFileSelection(silent = false) {
    uploadedFiles = [];
    processedData = [];
    
    const fileInput = document.getElementById('fileInput');
    if (fileInput) {
        fileInput.value = '';
    }
    
    const fileUploadArea = document.getElementById('fileUploadArea');
    if (fileUploadArea) {
        fileUploadArea.classList.remove('drag-over');
    }
    
    const filePreview = document.getElementById('filePreview');
    const dataPreview = document.getElementById('dataPreview');
    if (filePreview) {
        filePreview.style.display = 'none';
    }
    if (dataPreview) {
        dataPreview.style.display = 'none';
    }
    
    const filePreviewBody = document.getElementById('filePreviewBody');
    const dataPreviewBody = document.getElementById('dataPreviewBody');
    if (filePreviewBody) {
        filePreviewBody.innerHTML = '';
    }
    if (dataPreviewBody) {
        dataPreviewBody.innerHTML = '';
    }
    
    if (!silent) {
        console.info('已清空批量导入文件选择');
    }
}

// 解析导入数据（使用AI智能识别）
function parseImportData() {
    const data = document.getElementById('batchImportData').value.trim();
    if (!data) {
        alert('请先粘贴要导入的数据！');
        return;
    }

    // 显示加载状态
    const parseBtn = document.querySelector('button[onclick*="parseImportData"]') || 
                     document.querySelector('button.btn-outline-primary');
    const originalText = parseBtn ? parseBtn.innerHTML : '';
    if (parseBtn) {
        parseBtn.disabled = true;
        parseBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> AI解析中...';
    }
    
    // 隐藏之前的预览
    document.getElementById('importPreview').style.display = 'none';
    document.getElementById('saveImportBtn').disabled = true;

    // 调用后端AI批量解析API
    fetch('/api/ai/customer-extract/batch-parse', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ data: data })
    })
    .then(response => response.json())
    .then(result => {
        if (parseBtn) {
            parseBtn.disabled = false;
            parseBtn.innerHTML = originalText;
        }
        
        if (result.code !== 200) {
            alert('解析失败: ' + (result.message || '未知错误'));
            return;
        }
        
        const parsedList = result.data.parsedList || [];
        if (parsedList.length === 0) {
            alert('没有解析到任何数据，请检查输入格式');
            return;
        }
        
        // 转换数据格式以适配预览显示
        const parsedData = parsedList.map(item => {
            return {
                customerName: item.customerName || '',
                contactPerson: item.contactPerson || '',
                phone: item.phone || '',
                customerType: item.customerType || '',
                position: item.position || '',
                qqWeixin: item.qqWeixin || '',
                cooperationContent: item.cooperationContent || '',
                region: item.region || '',
                address: item.address || '',
                remark: item.remark || '',
                status: item.status || 'valid',
                error: item.error || ''
            };
        });
        
        // 显示预览
        displayImportPreview(parsedData);
        
        // 检查是否有错误
        const hasError = parsedData.some(item => item.status === 'error');
        if (!hasError) {
            document.getElementById('saveImportBtn').disabled = false;
        }
        
        // 保存解析后的数据供后续使用
        processedData = parsedData;
    })
    .catch(error => {
        if (parseBtn) {
            parseBtn.disabled = false;
            parseBtn.innerHTML = originalText;
        }
        console.error('解析数据失败:', error);
        alert('解析数据失败，请检查网络连接或稍后重试');
    });
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
            
            // 对not-allowed错误进行静默处理（权限被拒绝，不影响其他功能）
            if (event.error === 'not-allowed') {
                console.warn('语音识别权限被拒绝，功能已禁用');
                hideVoiceStatus();
                stopVoiceInput();
                // 不显示错误提示，避免打扰用户
                return;
            }
            
            // 其他错误才显示错误提示
            if (event.error === 'no-speech') {
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
let fileUploadInitialized = false;

function initFileUpload() {
    console.log('初始化文件上传功能...');
    
    const fileInput = document.getElementById('fileInput');
    const fileUploadArea = document.getElementById('fileUploadArea');
    const selectFileBtn = document.getElementById('selectFileBtn');

    if (!fileInput) {
        console.error('找不到fileInput元素');
        return;
    }
    
    if (!fileUploadArea) {
        console.error('找不到fileUploadArea元素');
        return;
    }
    
    // 如果已经初始化过，先移除旧的事件监听器（通过克隆元素来移除所有监听器）
    if (fileUploadInitialized) {
        console.log('文件上传功能已初始化，跳过重复初始化');
        return;
    }
    
    console.log('找到fileInput和fileUploadArea元素');

    // 文件选择事件（使用命名函数，方便后续移除）
    const handleFileChange = function(e) {
        console.log('文件选择事件触发，文件数量:', e.target.files.length);
        handleFileSelect(e);
    };
    fileInput.addEventListener('change', handleFileChange);

    // 拖拽事件
    fileUploadArea.addEventListener('dragover', handleDragOver);
    fileUploadArea.addEventListener('dragleave', handleDragLeave);
    fileUploadArea.addEventListener('drop', handleFileDrop);
    
    // 点击整个上传区域也可以选择文件（通过label触发）
    fileUploadArea.addEventListener('click', function(e) {
        // 如果点击的不是按钮、label或其子元素，则触发文件选择
        const clickedButton = e.target.closest('button');
        const clickedLabel = e.target.closest('label');
        const clickedInput = e.target.closest('input[type="file"]');
        if (!clickedButton && !clickedLabel && !clickedInput) {
            // 点击上传区域时，通过label触发文件选择
            const fileInputLabel = document.querySelector('label[for="fileInput"]');
            if (fileInputLabel) {
                e.preventDefault();
                e.stopPropagation();
                // 直接点击label内部的文件输入
                const fileInput = document.getElementById('fileInput');
                if (fileInput) {
                    fileInput.click();
                }
            }
        }
    });
    
    // label标签已经通过for属性关联到fileInput，文件输入在label内部
    if (selectFileBtn) {
        console.log('[IMPORT] [前端] 找到selectFileBtn（label），文件输入在label内部');
        console.log('[IMPORT] [前端] fileInput位置:', fileInput.parentElement === selectFileBtn ? '在label内部' : '不在label内部');
        
        // 添加label点击事件监听（用于调试）
        selectFileBtn.addEventListener('click', function(e) {
            console.log('[IMPORT] [前端] label被点击', {
                target: e.target.tagName,
                currentTarget: e.currentTarget.tagName,
                fileInputExists: !!fileInput,
                fileInputInLabel: fileInput && fileInput.parentElement === selectFileBtn
            });
        });
    } else {
        console.warn('[IMPORT] [前端] 未找到selectFileBtn（label）');
    }
    
    fileUploadInitialized = true;
    console.log('文件上传功能初始化完成');
}

// 处理文件选择
function handleFileSelect(event) {
    const files = Array.from(event.target.files);
    console.log('[IMPORT] [前端] 文件选择事件触发', {
        timestamp: new Date().toISOString(),
        fileCount: files.length,
        fileNames: files.map(f => f.name),
        fileSizes: files.map(f => f.size)
    });
    
    if (files.length > 0) {
        console.log('[IMPORT] [前端] 文件选择成功，开始处理文件');
    addFiles(files);
    } else {
        console.log('[IMPORT] [前端] 未选择文件');
    }
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
    console.log('[IMPORT] [前端] 文件拖拽事件触发', {
        timestamp: new Date().toISOString(),
        fileCount: files.length,
        fileNames: files.map(f => f.name),
        fileSizes: files.map(f => f.size)
    });
    addFiles(files);
}

// 添加文件
function addFiles(files) {
    if (!files || files.length === 0) {
        return;
    }
    
    let addedCount = 0;
    files.forEach(file => {
        if (isValidFileType(file)) {
            // 检查文件是否已经存在（通过文件名和大小判断）
            const exists = uploadedFiles.some(f => 
                f.name === file.name && f.file.size === file.size
            );
            
            if (!exists) {
            uploadedFiles.push({
                file: file,
                name: file.name,
                type: getFileType(file.name),
                size: formatFileSize(file.size),
                status: '待处理'
            });
                addedCount++;
        } else {
                console.log('文件已存在，跳过:', file.name);
            }
        } else {
            alert(`不支持的文件格式：${file.name}\n请上传Excel（.xlsx, .xls）或CSV（.csv）格式的文件！`);
        }
    });
    
    if (addedCount > 0) {
    updateFilePreview();
        console.log('已添加文件:', uploadedFiles.map(f => f.name).join(', '));
    }
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
        const filePreview = document.getElementById('filePreview');
        if (filePreview) {
            filePreview.style.display = 'none';
        }
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

// 保存上传的数据（使用新的导入接口）
function saveUploadedData() {
    // 从uploadedFiles数组获取文件，而不是从fileInput
    if (!uploadedFiles || uploadedFiles.length === 0) {
        alert('请先选择要上传的文件！');
        return;
    }
    
    // 只支持上传一个文件
    if (uploadedFiles.length > 1) {
        alert('目前只支持一次上传一个文件，请先移除多余的文件！');
        return;
    }
    
    const fileData = uploadedFiles[0];
    const file = fileData.file;
    const fileName = file.name.toLowerCase();
    
    // 检查文件格式
    if (!fileName.endsWith('.xlsx') && !fileName.endsWith('.xls') && !fileName.endsWith('.csv')) {
        alert('请上传Excel（.xlsx, .xls）或CSV（.csv）格式的文件！');
        return;
    }
    
    // 创建FormData
    const formData = new FormData();
    formData.append('file', file);
    
    // 显示加载提示
    const saveBtn = document.getElementById('saveUploadedBtn');
    const originalText = saveBtn.innerHTML;
    saveBtn.disabled = true;
    saveBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 导入中...';
    
    // 调用导入接口
    fetch('/api/customer/import', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        // 检查响应状态
        if (!response.ok) {
            // 尝试解析错误信息
            return response.text().then(text => {
                try {
                    const error = JSON.parse(text);
                    throw new Error(error.message || error.error || '导入失败: HTTP ' + response.status);
                } catch (e) {
                    if (e instanceof Error && e.message.includes('导入失败')) {
                        throw e;
                    }
                    throw new Error('导入失败: HTTP ' + response.status + ' ' + response.statusText);
                }
            });
        }
        // 解析JSON响应
        return response.json();
    })
    .then(result => {
        saveBtn.disabled = false;
        saveBtn.innerHTML = originalText;
        
        if (result.code === 200) {
            const successCount = result.data.successCount || 0;
            const failureCount = result.data.failureCount || 0;
            const errors = result.data.errors || [];
            
            let message = `导入完成！\n成功：${successCount} 条\n失败：${failureCount} 条`;
            if (errors.length > 0) {
                message += '\n\n错误详情：\n' + errors.slice(0, 5).join('\n');
                if (errors.length > 5) {
                    message += `\n...还有 ${errors.length - 5} 条错误`;
                }
            }
            
            alert(message);
            
            // 关闭模态框并刷新列表
            const modal = bootstrap.Modal.getInstance(document.getElementById('fileUploadModal'));
            if (modal) {
                modal.hide();
            }
            loadCustomers();
            
            // 清空文件输入和上传文件列表
            const fileInput = document.getElementById('fileInput');
            if (fileInput) {
                fileInput.value = '';
            }
            uploadedFiles = [];
            processedData = [];
            document.getElementById('filePreview').style.display = 'none';
            document.getElementById('dataPreview').style.display = 'none';
        } else {
            const errorMsg = result.message || result.error || '未知错误';
            console.error('导入失败:', result);
            alert('导入失败: ' + errorMsg);
        }
    })
    .catch(error => {
        saveBtn.disabled = false;
        saveBtn.innerHTML = originalText;
        console.error('导入失败:', error);
        const errorMsg = error.message || '网络错误或服务器异常，请检查文件格式后重试';
        alert('导入失败: ' + errorMsg);
        // 不清空文件，允许用户重试
    });
}

// 旧的保存上传数据方法（保留用于兼容）
function saveUploadedDataOld() {
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
    // 优先使用AI解析后的数据
    let customersToSave = [];
    
    if (processedData && processedData.length > 0) {
        // 使用AI解析的数据，只保存状态为valid的记录
        customersToSave = processedData
            .filter(item => item.status === 'valid')
            .map(item => {
                // 处理客户类型映射
                const customerTypeText = item.customerType || '';
                const customerType = customerTypeReverseMap[customerTypeText] || customerTypeText;
                
                return {
                    customerName: item.customerName || '',
                    contactPerson: item.contactPerson || '',
                    phone: item.phone || '',
                    customerType: customerType,
                    position: item.position || '',
                    qqWeixin: item.qqWeixin || '',
                    cooperationContent: item.cooperationContent || '',
                    region: item.region || '',
                    email: '', // AI解析中没有email字段，保持为空
                    address: item.address || '',
                    remark: item.remark || ''
                };
            });
    } else {
        // 如果没有AI解析数据，回退到原来的解析方式
        const data = document.getElementById('batchImportData').value.trim();
        if (!data) {
            alert('没有要保存的数据！请先点击"解析数据"按钮');
            return;
        }

        const lines = data.split('\n').filter(line => line.trim());
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
    }

    if (customersToSave.length === 0) {
        alert('没有有效的客户数据可保存！请确保数据已正确解析');
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

// 跳转到沟通管理页面
function goToCommunications(customerId) {
    if (!customerId) {
        alert('客户ID不能为空');
        return;
    }
    // 跳转到沟通管理页面，并传递客户ID参数
    window.location.href = `/communications.html?customerId=${customerId}`;
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

// 当前查看的客户信息
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
    
    // 处理进度字段
    const progress = customer.progress !== null && customer.progress !== undefined ? customer.progress : 0;
    const progressText = progressMap[progress] || progressMap['0'];
    const progressBadgeClass = progressClassMap[progress] || progressClassMap['0'];
    
    // 处理业务类型字段（现在businessType是具体业务类型1-6）
    const businessType = customer.businessType || 1;
    const businessTypeText = businessTypeMap[businessType.toString()] || '未填写';
    
    const createTime = customer.createTime ? new Date(customer.createTime).toLocaleString('zh-CN') : '';
    const updateTime = customer.updateTime ? new Date(customer.updateTime).toLocaleString('zh-CN') : createTime;
    
    const content = 
        '<div class="row g-3">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">客户名称</label>' +
                    '<p class="mb-0">' + (customer.customerName || '未填写') + '</p>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">联系人</label>' +
                    '<p class="mb-0">' + (customer.contactPerson || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row g-3">' +
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
        '<div class="row g-3">' +
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
        '<div class="row g-3">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">业务类型</label>' +
                    '<p class="mb-0">' + businessTypeText + '</p>' +
                '</div>' +
            '</div>' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">地区</label>' +
                    '<p class="mb-0">' + (customer.region || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row g-3">' +
            '<div class="col-md-6">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">进度</label>' +
                    '<p class="mb-0"><span class="badge ' + progressBadgeClass + '">' + progressText + '</span></p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row g-3">' +
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
        '<div class="row g-3">' +
            '<div class="col-md-12">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">合作内容</label>' +
                    '<p class="mb-0 text-break">' + (customer.cooperationContent || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row g-3">' +
            '<div class="col-md-12">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">详细地址</label>' +
                    '<p class="mb-0 text-break">' + (customer.address || '未填写') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row g-3">' +
            '<div class="col-md-12">' +
                '<div class="mb-3">' +
                    '<label class="form-label text-muted">备注</label>' +
                    '<p class="mb-0 text-break">' + (customer.remark || '无') + '</p>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="row g-3 mt-3 pt-3 border-top">' +
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
    
    // 显示模态框
    const modal = new bootstrap.Modal(document.getElementById('customerDetailModal'));
    modal.show();
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
    
    // 设置客户类型（确保有值）
    const customerType = customer.customerType || 1;
    const customerTypeText = customerTypeMap[customerType.toString()] || customerTypeMap['1'];
    const customerTypeSelect = document.getElementById('customerTypeSelect');
    if (customerTypeSelect) {
        customerTypeSelect.value = customerTypeText;
        console.log('设置客户类型:', customerType, '->', customerTypeText, '实际值:', customerTypeSelect.value);
    }
    
    // 设置客户等级（确保有值）
    const customerLevel = customer.customerLevel || 1;
    const customerLevelText = customerLevelMap[customerLevel.toString()] || '普通';
    const customerLevelSelect = document.getElementById('customerLevelSelect');
    if (customerLevelSelect) {
        customerLevelSelect.value = customerLevelText;
        console.log('设置客户等级:', customerLevel, '->', customerLevelText, '实际值:', customerLevelSelect.value);
    }
    // 设置进度
    const progress = customer.progress !== null && customer.progress !== undefined ? customer.progress : 0;
    const progressSelect = document.getElementById('progressSelect');
    if (progressSelect) {
        progressSelect.value = progress.toString();
        console.log('设置进度:', progress, '->', progressSelect.value);
    }
    
    // 设置业务类型（确保有值，默认为1）
    const businessType = customer.businessType || 1;
    const businessTypeSelect = document.getElementById('businessTypeSelect');
    if (businessTypeSelect) {
        businessTypeSelect.value = businessType.toString();
        console.log('设置业务类型:', businessType, '->', businessTypeSelect.value);
    }
    
    // 设置地区（确保下拉框中有对应的选项）
    const regionSelect = document.getElementById('regionSelect');
    if (regionSelect && customer.region) {
        regionSelect.value = customer.region;
        // 如果设置失败，说明下拉框中没有该选项
        if (regionSelect.value !== customer.region) {
            console.warn('地区值不在下拉框中:', customer.region);
        }
    }
    
    document.getElementById('position').value = customer.position || '';
    document.getElementById('qqWeixin').value = customer.qqWeixin || '';
    document.getElementById('cooperationContent').value = customer.cooperationContent || '';
    document.getElementById('address').value = customer.address || '';
    document.getElementById('remarks').value = customer.remark || '';
    
    // 延迟显示模态框，确保所有值都已设置
    setTimeout(() => {
        const modal = new bootstrap.Modal(document.getElementById('customerModal'));
        modal.show();
    }, 100);
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
    
    const progress = parseInt(formData.get('progress')) || 0;
    // 默认设置为第一种类型（品种权申请客户）
    const businessType = formData.get('businessType') ? parseInt(formData.get('businessType')) : 1;
    
    const customerId = document.getElementById('customerId').value;
    
    const customerData = {
        customerName: customerName,
        contactPerson: formData.get('contactPerson') || null,
        phone: formData.get('phone') || null,
        email: formData.get('email') || null,
        customerType: customerType,
        customerLevel: customerLevel,
        progress: progress,
        businessType: businessType,
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

// 批量删除功能已删除

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

// 下载Excel模版
function downloadTemplate() {
    fetch('/api/customer/template')
        .then(response => {
            if (!response.ok) {
                // 尝试解析错误信息
                return response.text().then(text => {
                    try {
                        const error = JSON.parse(text);
                        throw new Error(error.error || error.message || '下载模版失败');
                    } catch (e) {
                        throw new Error('下载模版失败: HTTP ' + response.status);
                    }
                });
            }
            // 获取文件名
            const contentDisposition = response.headers.get('Content-Disposition');
            let fileName = '客户导入模版.xlsx';
            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
                if (fileNameMatch && fileNameMatch[1]) {
                    fileName = fileNameMatch[1].replace(/['"]/g, '');
                    // 处理UTF-8编码的文件名
                    if (fileName.startsWith('UTF-8\'\'')) {
                        fileName = decodeURIComponent(fileName.replace('UTF-8\'\'', ''));
                    }
                }
            }
            return response.blob().then(blob => ({ blob, fileName }));
        })
        .then(({ blob, fileName }) => {
            // 创建下载链接
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = fileName;
            link.style.display = 'none';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error('下载模版失败:', error);
            alert('下载模版失败: ' + (error.message || '未知错误，请重试'));
        });
}

// 导出客户
function exportCustomers() {
    try {
        // 获取当前筛选条件（添加空值检查，使用正确的元素ID）
        const params = new URLSearchParams();
        const customerNameEl = document.getElementById('customerName');
        const customerTypeEl = document.getElementById('customerType');
        const customerLevelEl = document.getElementById('customerLevel');
        const regionEl = document.getElementById('region');
        
        const customerName = customerNameEl ? customerNameEl.value.trim() : '';
        const customerType = customerTypeEl ? customerTypeEl.value.trim() : '';
        const customerLevel = customerLevelEl ? customerLevelEl.value.trim() : '';
        const region = regionEl ? regionEl.value.trim() : '';
        
        if (customerName) params.append('customerName', customerName);
        if (customerType) {
            // 将显示文本转换为数字
            const typeValue = customerTypeReverseMap[customerType] || customerType;
            params.append('customerType', typeValue);
        }
        if (customerLevel) {
            // 将显示文本转换为数字
            const levelValue = customerLevelReverseMap[customerLevel] || customerLevel;
            params.append('customerLevel', levelValue);
        }
        if (region) params.append('region', region);
        
        // 询问用户导出格式
        const format = confirm('点击"确定"导出Excel格式，点击"取消"导出CSV格式') ? 'excel' : 'csv';
        params.append('format', format);
        
        const url = '/api/customer/export?' + params.toString();
        const fileName = '客户数据_' + new Date().toISOString().split('T')[0] + '.' + (format === 'excel' ? 'xlsx' : 'csv');
        
        // 显示加载提示
        const loadingMsg = format === 'excel' ? '正在导出Excel文件，请稍候...' : '正在导出CSV文件，请稍候...';
        console.log('开始导出:', url);
        
        // 使用 fetch API 下载文件
        fetch(url, {
            method: 'GET',
            headers: {
                'Accept': format === 'excel' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'text/csv'
            }
        })
        .then(response => {
            console.log('导出响应状态:', response.status, response.statusText);
            
            if (!response.ok) {
                // 尝试解析错误信息
                return response.text().then(text => {
                    try {
                        const error = JSON.parse(text);
                        throw new Error(error.error || error.message || '导出失败: ' + response.status);
                    } catch (e) {
                        if (e instanceof Error && e.message.startsWith('导出失败')) {
                            throw e;
                        }
                        throw new Error('导出失败: ' + response.status + ' ' + response.statusText);
                    }
                });
            }
            
            // 检查响应类型
            const contentType = response.headers.get('content-type');
            console.log('响应Content-Type:', contentType);
            
            if (contentType && contentType.includes('application/json')) {
                // 如果是JSON响应，说明是错误
                return response.json().then(data => {
                    throw new Error(data.error || data.message || '导出失败');
                });
            }
            
            // 返回二进制数据
            return response.blob();
        })
        .then(blob => {
            console.log('导出成功，文件大小:', blob.size, 'bytes');
            
            // 检查blob类型，如果是JSON说明是错误响应
            if (blob.type && blob.type.includes('application/json')) {
                return blob.text().then(text => {
                    try {
                        const error = JSON.parse(text);
                        throw new Error(error.error || error.message || '导出失败');
                    } catch (e) {
                        if (e instanceof Error && e.message.startsWith('导出失败')) {
                            throw e;
                        }
                        throw new Error('导出失败: ' + text);
                    }
                });
            }
            
            if (blob.size === 0) {
                throw new Error('导出的文件为空，请检查是否有数据可导出');
            }
            
            // 创建下载链接
            const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
            link.download = fileName;
            link.style.display = 'none';
            document.body.appendChild(link);
    link.click();
            
            // 清理
            setTimeout(() => {
                document.body.removeChild(link);
                window.URL.revokeObjectURL(url);
            }, 100);
            
            console.log('文件下载完成:', fileName);
        })
        .catch(error => {
            console.error('导出失败:', error);
            const errorMsg = error.message || '未知错误，请重试';
            alert('导出失败: ' + errorMsg + '\n\n如果问题持续，请检查浏览器控制台获取详细信息。');
        });
    } catch (error) {
        console.error('导出异常:', error);
        alert('导出失败: ' + (error.message || '请重试'));
    }
}
