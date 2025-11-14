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

