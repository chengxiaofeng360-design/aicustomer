// 模拟团队成员数据
let teamMembers = [
    {
        id: 1,
        name: '张三',
        position: '销售经理',
        email: 'zhangsan@company.com',
        phone: '13800138001',
        department: '销售部',
        role: '管理员',
        avatar: 'https://via.placeholder.com/40',
        workMode: 1, // 1:办公室, 2:远程, 3:混合
        onlineStatus: 1, // 1:在线, 2:忙碌, 3:离开, 4:离线
        timezone: 'Asia/Shanghai',
        tools: ['zoom', 'teams'],
        lastOnlineTime: new Date()
    },
    {
        id: 2,
        name: '李四',
        position: '技术总监',
        email: 'lisi@company.com',
        phone: '13800138002',
        department: '技术部',
        role: '管理员',
        avatar: 'https://via.placeholder.com/40',
        workMode: 2, // 远程办公
        onlineStatus: 1,
        timezone: 'Asia/Shanghai',
        tools: ['zoom', 'slack'],
        lastOnlineTime: new Date()
    },
    {
        id: 3,
        name: '王五',
        position: '客服专员',
        email: 'wangwu@company.com',
        phone: '13800138003',
        department: '客服部',
        role: '普通用户',
        avatar: 'https://via.placeholder.com/40',
        workMode: 3, // 混合办公
        onlineStatus: 2, // 忙碌
        timezone: 'Asia/Shanghai',
        tools: ['teams'],
        lastOnlineTime: new Date()
    },
    {
        id: 4,
        name: '赵六',
        position: '数据库工程师',
        email: 'zhaoliu@company.com',
        phone: '13800138004',
        department: '技术部',
        role: '普通用户',
        avatar: 'https://via.placeholder.com/40',
        workMode: 2, // 远程办公
        onlineStatus: 1,
        timezone: 'Asia/Shanghai',
        tools: ['zoom', 'slack'],
        lastOnlineTime: new Date()
    },
    {
        id: 5,
        name: '孙七',
        position: '测试工程师',
        email: 'sunqi@company.com',
        phone: '13800138005',
        department: '技术部',
        role: '普通用户',
        avatar: 'https://via.placeholder.com/40',
        workMode: 1, // 办公室
        onlineStatus: 3, // 离开
        timezone: 'Asia/Shanghai',
        tools: ['teams'],
        lastOnlineTime: new Date()
    },
    {
        id: 6,
        name: '周八',
        position: '产品经理',
        email: 'zhouba@company.com',
        phone: '13800138006',
        department: '产品部',
        role: '管理员',
        avatar: 'https://via.placeholder.com/40',
        workMode: 3, // 混合办公
        onlineStatus: 1,
        timezone: 'Asia/Shanghai',
        tools: ['zoom', 'teams', 'slack'],
        lastOnlineTime: new Date()
    },
    {
        id: 7,
        name: '吴九',
        position: '前端工程师',
        email: 'wujiu@company.com',
        phone: '13800138007',
        department: '技术部',
        role: '普通用户',
        avatar: 'https://via.placeholder.com/40',
        workMode: 2, // 远程办公
        onlineStatus: 2, // 忙碌
        timezone: 'Asia/Shanghai',
        tools: ['zoom', 'slack'],
        lastOnlineTime: new Date()
    },
    {
        id: 8,
        name: '郑十',
        position: 'UI设计师',
        email: 'zhengshi@company.com',
        phone: '13800138008',
        department: '设计部',
        role: '普通用户',
        avatar: 'https://via.placeholder.com/40',
        workMode: 1, // 办公室
        onlineStatus: 4, // 离线
        timezone: 'Asia/Shanghai',
        tools: ['teams'],
        lastOnlineTime: new Date()
    }
];

// 任务数据（从API加载）
let tasks = [];
let currentTaskFilter = 'all';
let currentTaskPage = 1;
const taskPageSize = 10;

// ========== 团队协作功能 ==========

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    loadTasks();
    loadReportData();
    // 延迟更新统计数据，确保任务数据已加载
    setTimeout(() => {
        updateStats();
    }, 500);
});

// 加载任务列表
async function loadTasks() {
    try {
        let url = `/api/team-task/tasks?page=${currentTaskPage}&size=${taskPageSize}`;
        
        // 根据筛选条件添加status参数
        if (currentTaskFilter === 'pending') {
            url += '&status=2'; // 进行中
        } else if (currentTaskFilter === 'completed') {
            url += '&status=4'; // 已完成
        }
        
        const response = await fetch(url);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            tasks = result.data.list || [];
            renderTaskTable();
            // 更新统计数据（异步）
            updateStats();
        } else {
            console.error('加载任务失败:', result.message);
            alert('加载任务失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('加载任务失败:', error);
        alert('加载任务失败，请重试');
    }
}

// 更新统计数据
async function updateStats() {
    // 团队成员数（使用真实数据）
    const totalMembers = teamMembers.length;
    
    // 从API获取真实的任务统计数据
    try {
        const response = await fetch('/api/team-task/tasks?page=1&size=1000');
        const result = await response.json();
        
        if (result.code === 200 && result.data && result.data.list) {
            const allTasks = result.data.list;
            // 进行中任务（status = 2）
            const activeTasks = allTasks.filter(t => t.status === 2).length;
            // 已完成任务（status = 4）
            const completedTasks = allTasks.filter(t => t.status === 4).length;
            // 重要任务（priority = 3 高 或 priority = 4 紧急）
            const importantTasks = allTasks.filter(t => t.priority === 3 || t.priority === 4).length;
            
            document.getElementById('activeTasks').textContent = activeTasks;
            document.getElementById('completedTasks').textContent = completedTasks;
            document.getElementById('importantTasks').textContent = importantTasks;
        } else {
            // 如果API失败，使用本地数据
            const activeTasks = tasks.filter(t => t.status === 2).length;
            const completedTasks = tasks.filter(t => t.status === 4).length;
            const importantTasks = tasks.filter(t => t.priority === 3 || t.priority === 4).length;
            document.getElementById('activeTasks').textContent = activeTasks;
            document.getElementById('completedTasks').textContent = completedTasks;
            document.getElementById('importantTasks').textContent = importantTasks;
        }
    } catch (error) {
        console.error('获取任务统计数据失败:', error);
        // 使用本地数据作为备用
        const activeTasks = tasks.filter(t => t.status === 2).length;
        const completedTasks = tasks.filter(t => t.status === 4).length;
        const importantTasks = tasks.filter(t => t.priority === 3 || t.priority === 4).length;
        document.getElementById('activeTasks').textContent = activeTasks;
        document.getElementById('completedTasks').textContent = completedTasks;
        document.getElementById('importantTasks').textContent = importantTasks;
    }
    
    document.getElementById('totalMembers').textContent = totalMembers;
}

// 渲染任务表格
function renderTaskTable() {
    const tbody = document.getElementById('taskTableBody');
    if (!tbody) {
        console.error('找不到任务表格tbody元素');
        return;
    }
    
    tbody.innerHTML = '';

    if (tasks.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = '<td colspan="7" class="text-center text-muted">暂无任务数据</td>';
        tbody.appendChild(row);
        return;
    }

    tasks.forEach(task => {
        const row = document.createElement('tr');
        const priorityClass = getPriorityClass(task.priority);
        const statusClass = getStatusClass(task.status);
        
        // 获取任务名称（title或name）
        const taskName = task.title || task.name || '未命名任务';
        // 获取负责人
        const assignee = task.assigneeName || task.assignee || '未分配';
        // 获取截止日期
        const deadline = task.deadline ? formatDate(task.deadline) : (task.endDate ? formatDate(task.endDate) : '-');
        // 获取进度
        const progress = task.progress || 0;
        
        row.innerHTML = `
            <td>${escapeHtml(taskName)}</td>
            <td>${escapeHtml(assignee)}</td>
            <td><span class="badge ${priorityClass}">${getPriorityText(task.priority)}</span></td>
            <td><span class="badge ${statusClass}">${getStatusText(task.status)}</span></td>
            <td>${deadline}</td>
            <td>
                <div class="progress" style="height: 20px;">
                    <div class="progress-bar" role="progressbar" style="width: ${progress}%">${progress}%</div>
                </div>
            </td>
            <td>
                <button class="btn btn-outline-info btn-sm" onclick="viewTaskDetail(${task.id})" title="查看详情">
                    <i class="bi bi-eye"></i>
                </button>
                <button class="btn btn-outline-primary btn-sm" onclick="editTask(${task.id})" title="编辑">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm" onclick="deleteTask(${task.id})" title="删除">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// HTML转义函数
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '-';
    try {
        const date = new Date(dateString);
        // 如果是日期时间格式，只显示日期部分
        if (dateString.includes('T') || dateString.includes(' ')) {
            return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
        }
        return date.toLocaleDateString('zh-CN');
    } catch (e) {
        return dateString;
    }
}

// 获取优先级样式类（后端使用数字：1:低, 2:中, 3:高, 4:紧急）
function getPriorityClass(priority) {
    if (typeof priority === 'string') {
        // 兼容旧的前端字符串格式
        switch (priority) {
            case 'high': return 'bg-danger';
            case 'medium': return 'bg-warning';
            case 'low': return 'bg-success';
            default: return 'bg-secondary';
        }
    }
    // 数字格式
    switch (priority) {
        case 1: return 'bg-success'; // 低
        case 2: return 'bg-info'; // 中
        case 3: return 'bg-warning'; // 高
        case 4: return 'bg-danger'; // 紧急
        default: return 'bg-secondary';
    }
}

// 获取优先级文本
function getPriorityText(priority) {
    if (typeof priority === 'string') {
        // 兼容旧的前端字符串格式
        switch (priority) {
            case 'high': return '高';
            case 'medium': return '中';
            case 'low': return '低';
            default: return '未知';
        }
    }
    // 数字格式
    switch (priority) {
        case 1: return '低';
        case 2: return '中';
        case 3: return '高';
        case 4: return '紧急';
        default: return '未知';
    }
}

// 获取状态样式类（后端使用数字：1:待分配, 2:进行中, 3:待审核, 4:已完成, 5:已取消）
function getStatusClass(status) {
    if (typeof status === 'string') {
        // 兼容旧的前端字符串格式
        switch (status) {
            case 'pending': return 'bg-warning';
            case 'completed': return 'bg-success';
            case 'cancelled': return 'bg-danger';
            default: return 'bg-secondary';
        }
    }
    // 数字格式
    switch (status) {
        case 1: return 'bg-secondary'; // 待分配
        case 2: return 'bg-warning'; // 进行中
        case 3: return 'bg-info'; // 待审核
        case 4: return 'bg-success'; // 已完成
        case 5: return 'bg-danger'; // 已取消
        default: return 'bg-secondary';
    }
}

// 获取状态文本
function getStatusText(status) {
    if (typeof status === 'string') {
        // 兼容旧的前端字符串格式
        switch (status) {
            case 'pending': return '进行中';
            case 'completed': return '已完成';
            case 'cancelled': return '已取消';
            default: return '未知';
        }
    }
    // 数字格式
    switch (status) {
        case 1: return '待分配';
        case 2: return '进行中';
        case 3: return '待审核';
        case 4: return '已完成';
        case 5: return '已取消';
        default: return '未知';
    }
}

// 过滤任务
function filterTasks(filter) {
    currentTaskFilter = filter;
    currentTaskPage = 1; // 重置到第一页
    loadTasks();
}

// 显示添加任务模态框
function showAddTaskModal() {
    document.getElementById('taskModalTitle').textContent = '新建任务';
    document.getElementById('taskForm').reset();
    document.getElementById('taskId').value = '';
    loadAssigneeOptions();
    new bootstrap.Modal(document.getElementById('taskModal')).show();
}

// 加载负责人选项
function loadAssigneeOptions() {
    const select = document.getElementById('taskAssignee');
    select.innerHTML = '<option value="">请选择负责人</option>';
    teamMembers.forEach(member => {
        const option = document.createElement('option');
        option.value = member.name;
        option.textContent = member.name;
        select.appendChild(option);
    });
}

// 查看任务详情
async function viewTaskDetail(taskId) {
    try {
        const response = await fetch(`/api/team-task/tasks/${taskId}`);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            const task = result.data;
            
            // 填充详情信息（只显示编辑表单中的字段）
            document.getElementById('detailTaskName').textContent = task.title || task.name || '未命名任务';
            document.getElementById('detailAssignee').textContent = task.assigneeName || task.assignee || '未分配';
            document.getElementById('detailPriority').innerHTML = `<span class="badge ${getPriorityClass(task.priority)}">${getPriorityText(task.priority)}</span>`;
            
            // 处理截止日期（与编辑表单一致，只显示日期）
            let deadline = '-';
            if (task.deadline) {
                const date = new Date(task.deadline);
                deadline = date.toISOString().split('T')[0];
            } else if (task.endDate) {
                deadline = task.endDate;
            }
            document.getElementById('detailDeadline').textContent = deadline;
            
            // 处理描述
            document.getElementById('detailDescription').textContent = task.description || '无描述';
            
            // 处理进度
            const progress = task.progress || 0;
            document.getElementById('detailProgressBar').style.width = progress + '%';
            document.getElementById('detailProgressText').textContent = progress + '%';
            
            // 显示模态框
            new bootstrap.Modal(document.getElementById('taskDetailModal')).show();
        } else {
            alert('获取任务详情失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('查看任务详情失败:', error);
        alert('获取任务详情失败，请重试');
    }
}

// 获取任务类型文本
function getTaskTypeText(taskType) {
    if (!taskType) return '未设置';
    switch (taskType) {
        case 1: return '客户跟进';
        case 2: return '项目推进';
        case 3: return '问题处理';
        case 4: return '会议安排';
        case 5: return '其他';
        default: return '未知';
    }
}

// 格式化日期时间
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '-';
    try {
        const date = new Date(dateTimeString);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (e) {
        return dateTimeString;
    }
}

// 编辑任务
async function editTask(taskId) {
    try {
        const response = await fetch(`/api/team-task/tasks/${taskId}`);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            const task = result.data;
            document.getElementById('taskModalTitle').textContent = '编辑任务';
            document.getElementById('taskId').value = task.id;
            document.getElementById('taskName').value = task.title || task.name || '';
            document.getElementById('taskAssignee').value = task.assigneeName || task.assignee || '';
            document.getElementById('taskPriority').value = task.priority || 2;
            
            // 处理截止日期
            let deadline = '';
            if (task.deadline) {
                const date = new Date(task.deadline);
                deadline = date.toISOString().split('T')[0];
            } else if (task.endDate) {
                deadline = task.endDate;
            }
            document.getElementById('taskDueDate').value = deadline;
            
            document.getElementById('taskDescription').value = task.description || '';
            document.getElementById('taskProgress').value = task.progress || 0;
            
            loadAssigneeOptions();
            new bootstrap.Modal(document.getElementById('taskModal')).show();
        } else {
            alert('获取任务详情失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('获取任务详情失败:', error);
        alert('获取任务详情失败，请重试');
    }
}

// 保存任务
async function saveTask() {
    const form = document.getElementById('taskForm');
    if (!form) {
        alert('表单不存在');
        return;
    }
    
    const formData = new FormData(form);
    const taskId = document.getElementById('taskId').value;
    
    // 构建任务数据
    const taskData = {
        title: formData.get('name') || '',
        assigneeName: formData.get('assignee') || '',
        priority: parseInt(formData.get('priority')) || 2,
        deadline: formData.get('dueDate') ? new Date(formData.get('dueDate')).toISOString() : null,
        description: formData.get('description') || '',
        progress: parseInt(formData.get('progress')) || 0
    };
    
    // 如果没有设置状态，使用默认值
    if (!taskId) {
        taskData.status = 2; // 默认进行中
    }
    
    try {
        let url = '/api/team-task/tasks';
        let method = 'POST';
        
        if (taskId) {
            // 编辑模式
            url = `/api/team-task/tasks/${taskId}`;
            method = 'PUT';
            taskData.id = parseInt(taskId);
        }
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(taskData)
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            bootstrap.Modal.getInstance(document.getElementById('taskModal')).hide();
            alert('保存成功！');
            loadTasks(); // 重新加载任务列表
        } else {
            alert('保存失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('保存任务失败:', error);
        alert('保存任务失败，请重试');
    }
}

// 删除任务
async function deleteTask(taskId) {
    if (!confirm('确定要删除这个任务吗？')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/team-task/tasks/${taskId}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            alert('删除成功！');
            loadTasks(); // 重新加载任务列表
        } else {
            alert('删除失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('删除任务失败:', error);
        alert('删除任务失败，请重试');
    }
}


// ========== 任务进度跟踪和汇报功能 ==========

// 任务进度汇报数据
let reportData = [];
let currentReportPage = 1;
const reportPageSize = 10;

// 加载任务进度汇报数据
async function loadReportData() {
    try {
        const response = await fetch('/api/task-progress-report/reports?page=' + currentReportPage + '&size=' + reportPageSize);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            // 兼容处理：如果data是数组，说明后端返回格式有问题；如果是对象，说明是PageResult
            let pageResult;
            if (Array.isArray(result.data)) {
                // 如果返回的是数组，手动构建PageResult
                console.warn('API返回格式异常，data是数组而不是PageResult对象');
                reportData = result.data;
                pageResult = {
                    list: result.data,
                    total: result.data.length,
                    pageNum: currentReportPage,
                    pageSize: reportPageSize,
                    pages: Math.ceil(result.data.length / reportPageSize)
                };
            } else {
                // 正常情况：返回的是PageResult对象
                pageResult = result.data;
                reportData = pageResult.list || [];
            }
            renderReportTable();
            renderReportPagination(pageResult);
        } else {
            console.error('加载任务进度汇报数据失败:', result.message);
            reportData = [];
            renderReportTable();
        }
    } catch (error) {
        console.error('加载任务进度汇报数据失败:', error);
        reportData = [];
        renderReportTable();
    }
}

// 渲染汇报分页
function renderReportPagination(pageResult) {
    const pagination = document.getElementById('reportPagination');
    if (!pagination) return;
    
    pagination.innerHTML = '';
    
    if (pageResult.pages <= 1) return;
    
    // 上一页
    const prevLi = document.createElement('li');
    prevLi.className = 'page-item' + (pageResult.pageNum <= 1 ? ' disabled' : '');
    prevLi.innerHTML = `<a class="page-link" href="#" onclick="changeReportPage(${pageResult.pageNum - 1}); return false;">上一页</a>`;
    pagination.appendChild(prevLi);
    
    // 页码
    for (let i = 1; i <= pageResult.pages; i++) {
        const li = document.createElement('li');
        li.className = 'page-item' + (i === pageResult.pageNum ? ' active' : '');
        li.innerHTML = `<a class="page-link" href="#" onclick="changeReportPage(${i}); return false;">${i}</a>`;
        pagination.appendChild(li);
    }
    
    // 下一页
    const nextLi = document.createElement('li');
    nextLi.className = 'page-item' + (pageResult.pageNum >= pageResult.pages ? ' disabled' : '');
    nextLi.innerHTML = `<a class="page-link" href="#" onclick="changeReportPage(${pageResult.pageNum + 1}); return false;">下一页</a>`;
    pagination.appendChild(nextLi);
}

// 切换汇报页码
function changeReportPage(page) {
    currentReportPage = page;
    loadReportData();
}

// 加载假任务进度汇报数据
function loadMockReportData() {
    reportData = [
        {
            id: 1,
            taskId: 1,
            taskName: '客户需求分析',
            employeeId: 1,
            employeeName: '张三',
            reportType: 1,
            reportTitle: '客户需求分析日报',
            reportContent: '今日完成了ABC科技公司的需求调研，收集了客户的主要业务需求和技术要求。',
            progress: 60,
            workDuration: 480,
            workResults: '完成需求调研报告初稿',
            problems: '客户对某些技术细节不够明确',
            solutions: '已安排技术专家进行详细沟通',
            nextPlan: '明日完成需求文档的最终版本',
            supportNeeded: '需要技术团队支持',
            workLocation: '办公室A区',
            workMode: 1,
            reportStatus: 2,
            qualityScore: 4,
            efficiencyScore: 4,
            attitudeScore: 4,
            overallScore: 4.0,
            isAbnormal: false,
            createTime: '2024-01-15 18:00:00'
        },
        {
            id: 2,
            taskId: 2,
            taskName: 'UI设计优化',
            employeeId: 2,
            employeeName: '李四',
            reportType: 1,
            reportTitle: 'UI设计优化日报',
            reportContent: '今日完成了登录页面的UI设计优化，提升了用户体验。',
            progress: 80,
            workDuration: 450,
            workResults: '完成登录页面设计',
            problems: '部分交互细节需要调整',
            solutions: '已与产品经理沟通确认',
            nextPlan: '明日完成注册页面设计',
            supportNeeded: '需要产品经理确认交互流程',
            workLocation: '办公室B区',
            workMode: 1,
            reportStatus: 2,
            qualityScore: 4,
            efficiencyScore: 4,
            attitudeScore: 4,
            overallScore: 4.0,
            isAbnormal: false,
            createTime: '2024-01-15 17:30:00'
        },
        {
            id: 3,
            taskId: 3,
            taskName: '项目管理会议',
            employeeId: 3,
            employeeName: '王五',
            reportType: 1,
            reportTitle: '项目管理会议日报',
            reportContent: '今日组织了项目进度评审会议，协调了各团队的工作安排。',
            progress: 70,
            workDuration: 360,
            workResults: '完成项目进度评审',
            problems: '部分团队成员时间冲突',
            solutions: '已重新安排会议时间',
            nextPlan: '明日进行资源分配优化',
            supportNeeded: '需要各部门配合',
            workLocation: '办公室C区',
            workMode: 1,
            reportStatus: 1,
            qualityScore: null,
            efficiencyScore: null,
            attitudeScore: null,
            overallScore: null,
            isAbnormal: false,
            createTime: '2024-01-15 16:45:00'
        },
        {
            id: 4,
            taskId: 4,
            taskName: '数据库优化',
            employeeId: 4,
            employeeName: '赵六',
            reportType: 1,
            reportTitle: '数据库优化日报',
            reportContent: '今日完成了数据库查询优化，提升了系统性能。',
            progress: 90,
            workDuration: 480,
            workResults: '完成查询优化',
            problems: '部分复杂查询仍需优化',
            solutions: '已制定进一步优化方案',
            nextPlan: '明日进行索引优化',
            supportNeeded: '需要DBA支持',
            workLocation: '家中',
            workMode: 2,
            reportStatus: 2,
            qualityScore: 5,
            efficiencyScore: 4,
            attitudeScore: 4,
            overallScore: 4.3,
            isAbnormal: false,
            createTime: '2024-01-15 18:15:00'
        },
        {
            id: 5,
            taskId: 1,
            taskName: '客户需求分析',
            employeeId: 1,
            employeeName: '张三',
            reportType: 5,
            reportTitle: '紧急汇报：客户需求变更',
            reportContent: '客户突然提出新的需求变更，需要紧急处理。',
            progress: 50,
            workDuration: 120,
            workResults: '分析新需求影响',
            problems: '需求变更影响较大',
            solutions: '已与客户沟通确认',
            nextPlan: '明日重新制定方案',
            supportNeeded: '需要技术团队支持',
            workLocation: '办公室A区',
            workMode: 1,
            reportStatus: 1,
            qualityScore: null,
            efficiencyScore: null,
            attitudeScore: null,
            overallScore: null,
            isAbnormal: true,
            abnormalReason: '客户需求变更频繁',
            createTime: '2024-01-15 19:00:00'
        }
    ];
    renderReportTable();
}

// 渲染任务进度汇报表格
function renderReportTable() {
    const tbody = document.getElementById('reportTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (reportData.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = '<td colspan="8" class="text-center">暂无数据</td>';
        tbody.appendChild(row);
        return;
    }
    
    reportData.forEach(report => {
        const row = document.createElement('tr');
        const reportTypeText = getReportTypeText(report.reportType);
        // 处理删除状态：deleted可能是0/1或false/true，也可能不存在（默认为正常）
        const isDeleted = report.deleted === 1 || report.deleted === true;
        const deletedStatus = isDeleted ? '已删除' : '正常';
        const deletedClass = isDeleted ? 'bg-danger' : 'bg-success';
        const createTime = report.createTime ? formatDate(report.createTime) : '-';
        
        row.innerHTML = `
            <td>${escapeHtml(report.taskName || (report.taskId ? '任务' + report.taskId : '-'))}</td>
            <td><span class="badge bg-info">${reportTypeText}</span></td>
            <td class="table-cell-truncate" title="${escapeHtml(report.reportTitle || '')}">${escapeHtml(report.reportTitle || '-')}</td>
            <td class="table-cell-truncate" title="${escapeHtml(report.reportContent || '')}">${escapeHtml(report.reportContent || '-')}</td>
            <td>${escapeHtml(report.employeeName || '-')}</td>
            <td>${createTime}</td>
            <td><span class="badge ${deletedClass}">${deletedStatus}</span></td>
            <td>
                <button class="btn btn-outline-info btn-sm" onclick="viewReportDetail(${report.id})" title="查看详情">
                    <i class="bi bi-eye"></i>
                </button>
                <button class="btn btn-outline-primary btn-sm" onclick="editReport(${report.id})" title="编辑" ${isDeleted ? 'disabled' : ''}>
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm" onclick="deleteReport(${report.id})" title="删除" ${isDeleted ? 'disabled' : ''}>
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// 获取汇报状态样式类
function getReportStatusClass(status) {
    switch (status) {
        case 1: return 'bg-warning'; // 待审核
        case 2: return 'bg-success'; // 已通过
        case 3: return 'bg-info';    // 需修改
        case 4: return 'bg-danger'; // 已驳回
        default: return 'bg-secondary';
    }
}

// 获取汇报状态文本
function getReportStatusText(status) {
    switch (status) {
        case 1: return '待审核';
        case 2: return '已通过';
        case 3: return '需修改';
        case 4: return '已驳回';
        default: return '未知';
    }
}

// 获取汇报类型文本
function getReportTypeText(type) {
    switch (type) {
        case 1: return '日报';
        case 2: return '周报';
        case 3: return '月报';
        case 4: return '项目汇报';
        case 5: return '紧急汇报';
        default: return '未知';
    }
}

// 获取工作模式文本
function getWorkModeText(mode) {
    switch (mode) {
        case 1: return '办公室';
        case 2: return '远程';
        case 3: return '混合';
        default: return '未知';
    }
}

// 查看汇报详情
async function viewReportDetail(reportId) {
    try {
        const response = await fetch(`/api/task-progress-report/reports/${reportId}`);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            const report = result.data;
            
            // 填充详情信息（只显示编辑表单中的字段）
            document.getElementById('reportDetailTaskName').textContent = report.taskName || '-';
            document.getElementById('reportDetailType').textContent = getReportTypeText(report.reportType);
            document.getElementById('reportDetailTitle').textContent = report.reportTitle || '-';
            document.getElementById('reportDetailContent').textContent = report.reportContent || '-';
            document.getElementById('reportDetailEmployeeName').textContent = report.employeeName || '-';
            document.getElementById('reportDetailCreateTime').textContent = report.createTime ? formatDate(report.createTime) : '-';
            
            // 显示模态框
            new bootstrap.Modal(document.getElementById('reportDetailModal')).show();
        } else {
            alert('获取汇报详情失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('查看汇报详情失败:', error);
        alert('获取汇报详情失败，请重试');
    }
}

// 审核汇报
function reviewReport(reportId) {
    const report = reportData.find(r => r.id === reportId);
    if (report) {
        document.getElementById('reviewReportId').value = reportId;
        document.getElementById('reviewStatus').value = '';
        document.getElementById('reviewComment').value = '';
        document.getElementById('reviewQualityScore').value = '';
        document.getElementById('reviewEfficiencyScore').value = '';
        document.getElementById('reviewAttitudeScore').value = '';
        new bootstrap.Modal(document.getElementById('reviewModal')).show();
    }
}

// 提交审核
function submitReview() {
    const reportId = document.getElementById('reviewReportId').value;
    const reportStatus = document.getElementById('reviewStatus').value;
    const reviewComment = document.getElementById('reviewComment').value;
    const qualityScore = document.getElementById('reviewQualityScore').value;
    const efficiencyScore = document.getElementById('reviewEfficiencyScore').value;
    const attitudeScore = document.getElementById('reviewAttitudeScore').value;
    
    if (!reportStatus) {
        alert('请选择审核结果');
        return;
    }
    
    // 这里可以调用API进行审核
    console.log('审核汇报:', reportId, reportStatus, reviewComment, qualityScore, efficiencyScore, attitudeScore);
    
    // 更新本地数据
    const report = reportData.find(r => r.id == reportId);
    if (report) {
        report.reportStatus = parseInt(reportStatus);
        report.reviewComment = reviewComment;
        report.qualityScore = qualityScore ? parseInt(qualityScore) : null;
        report.efficiencyScore = efficiencyScore ? parseInt(efficiencyScore) : null;
        report.attitudeScore = attitudeScore ? parseInt(attitudeScore) : null;
        
        if (qualityScore && efficiencyScore && attitudeScore) {
            report.overallScore = (parseInt(qualityScore) + parseInt(efficiencyScore) + parseInt(attitudeScore)) / 3.0;
        }
    }
    
    renderReportTable();
    bootstrap.Modal.getInstance(document.getElementById('reviewModal')).hide();
    alert('审核完成！');
}

// 监督汇报
function superviseReport(reportId) {
    const supervisionNote = prompt('请输入监督备注:');
    if (supervisionNote) {
        // 这里可以调用API进行监督处理
        console.log('监督汇报:', reportId, supervisionNote);
        alert('监督处理完成！');
        loadReportData(); // 重新加载数据
    }
}

// 显示创建汇报模态框
async function showCreateReportModal() {
    document.getElementById('reportModalTitle').textContent = '新建任务进度汇报';
    document.getElementById('reportForm').reset();
    document.getElementById('reportId').value = '';
    await loadTaskOptions();
    await loadEmployeeOptions();
    new bootstrap.Modal(document.getElementById('reportModal')).show();
}

// 加载任务选项
async function loadTaskOptions() {
    const select = document.getElementById('reportTaskId');
    if (!select) return;
    
    select.innerHTML = '<option value="">请选择任务</option>';
    
    try {
        const response = await fetch('/api/team-task/tasks?page=1&size=1000');
        const result = await response.json();
        
        if (result.code === 200 && result.data && result.data.list) {
            result.data.list.forEach(task => {
                const option = document.createElement('option');
                option.value = task.id;
                option.textContent = task.title || task.name || `任务${task.id}`;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('加载任务列表失败:', error);
    }
}

// 加载员工选项
async function loadEmployeeOptions() {
    const select = document.getElementById('reportEmployeeName');
    if (!select) return;
    
    select.innerHTML = '<option value="">请选择员工</option>';
    
    // 使用现有的teamMembers数组
    if (teamMembers && teamMembers.length > 0) {
        teamMembers.forEach(member => {
            const option = document.createElement('option');
            option.value = member.name;
            option.textContent = member.name;
            select.appendChild(option);
        });
    } else {
        // 如果没有teamMembers，可以从API加载
        // 这里暂时使用空数组，后续可以添加员工API
        console.warn('teamMembers数组为空，无法加载员工列表');
    }
}

// 编辑汇报
async function editReport(reportId) {
    try {
        const response = await fetch(`/api/task-progress-report/reports/${reportId}`);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            const report = result.data;
            document.getElementById('reportModalTitle').textContent = '编辑任务进度汇报';
            document.getElementById('reportId').value = report.id;
            document.getElementById('reportType').value = report.reportType || '';
            document.getElementById('reportTitle').value = report.reportTitle || '';
            document.getElementById('reportContent').value = report.reportContent || '';
            
            // 先加载选项，然后设置值
            await loadTaskOptions();
            await loadEmployeeOptions();
            
            // 延迟设置下拉框的值，确保选项已加载
            setTimeout(() => {
                document.getElementById('reportTaskId').value = report.taskId || '';
                document.getElementById('reportEmployeeName').value = report.employeeName || '';
            }, 200);
            
            new bootstrap.Modal(document.getElementById('reportModal')).show();
        } else {
            alert('获取汇报信息失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('编辑汇报失败:', error);
        alert('获取汇报信息失败: ' + error.message);
    }
}

// 保存汇报
async function saveReport() {
    const form = document.getElementById('reportForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const formData = new FormData(form);
    const reportId = document.getElementById('reportId').value;
    
    const reportData = {
        taskId: formData.get('taskId') ? parseInt(formData.get('taskId')) : null,
        reportType: formData.get('reportType') ? parseInt(formData.get('reportType')) : null,
        reportTitle: formData.get('reportTitle'),
        reportContent: formData.get('reportContent'),
        employeeName: formData.get('employeeName')
    };
    
    // 验证必填字段
    if (!reportData.taskId) {
        alert('请选择任务');
        return;
    }
    if (!reportData.reportType) {
        alert('请选择汇报类型');
        return;
    }
    if (!reportData.reportTitle || reportData.reportTitle.trim() === '') {
        alert('请输入汇报标题');
        return;
    }
    if (!reportData.reportContent || reportData.reportContent.trim() === '') {
        alert('请输入汇报内容');
        return;
    }
    if (!reportData.employeeName || reportData.employeeName.trim() === '') {
        alert('请选择员工姓名');
        return;
    }
    
    try {
        let response;
        if (reportId) {
            // 编辑模式
            response = await fetch(`/api/task-progress-report/reports/${reportId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reportData)
            });
        } else {
            // 新建模式
            response = await fetch('/api/task-progress-report/reports', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reportData)
            });
        }
        
        const result = await response.json();
        
        if (result.code === 200) {
            bootstrap.Modal.getInstance(document.getElementById('reportModal')).hide();
            alert('保存成功！');
            // 如果是新建，重置到第一页；如果是编辑，保持当前页
            if (!reportId) {
                currentReportPage = 1;
            }
            // 延迟一下再加载，确保后端数据已保存
            setTimeout(() => {
                loadReportData();
            }, 300);
        } else {
            alert('保存失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('保存汇报失败:', error);
        alert('保存失败: ' + error.message);
    }
}

// 删除汇报
async function deleteReport(reportId) {
    if (!confirm('确定要删除这个汇报吗？')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/task-progress-report/reports/${reportId}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            alert('删除成功！');
            // 删除后延迟一下再加载，确保后端数据已更新
            setTimeout(() => {
                loadReportData();
            }, 300);
        } else {
            alert('删除失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('删除汇报失败:', error);
        alert('删除失败: ' + error.message);
    }
}

