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

// 模拟任务数据
let tasks = [
    {
        id: 1,
        name: '客户需求分析',
        assignee: '张三',
        priority: 'high',
        status: 'pending',
        dueDate: '2024-01-30',
        progress: 60,
        description: '分析客户需求，制定解决方案'
    },
    {
        id: 2,
        name: '系统优化',
        assignee: '李四',
        priority: 'medium',
        status: 'completed',
        dueDate: '2024-01-25',
        progress: 100,
        description: '优化系统性能，提升用户体验'
    },
    {
        id: 3,
        name: '客户培训',
        assignee: '王五',
        priority: 'low',
        status: 'pending',
        dueDate: '2024-02-05',
        progress: 30,
        description: '为客户提供产品使用培训'
    },
    {
        id: 4,
        name: '数据库优化',
        assignee: '赵六',
        priority: 'high',
        status: 'pending',
        dueDate: '2024-01-28',
        progress: 90,
        description: '优化数据库查询性能，提升系统响应速度'
    },
    {
        id: 5,
        name: '测试用例编写',
        assignee: '孙七',
        priority: 'medium',
        status: 'pending',
        dueDate: '2024-02-01',
        progress: 50,
        description: '编写用户管理模块的测试用例'
    },
    {
        id: 6,
        name: '代码审查',
        assignee: '吴九',
        priority: 'medium',
        status: 'pending',
        dueDate: '2024-01-29',
        progress: 75,
        description: '审查前端和后端代码，确保代码质量'
    },
    {
        id: 7,
        name: '项目管理会议',
        assignee: '王五',
        priority: 'high',
        status: 'completed',
        dueDate: '2024-01-20',
        progress: 100,
        description: '组织项目进度评审会议，协调团队工作'
    },
    {
        id: 8,
        name: 'UI设计优化',
        assignee: '李四',
        priority: 'medium',
        status: 'pending',
        dueDate: '2024-02-03',
        progress: 70,
        description: '优化用户界面设计，提升用户体验'
    }
];

let currentTaskFilter = 'all';

// ========== 团队协作功能 ==========

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    updateStats();
    renderTaskTable();
    loadReportData();
});

// 更新统计数据
function updateStats() {
    const totalMembers = teamMembers.length;
    const onlineMembers = teamMembers.filter(m => m.onlineStatus === 1).length;
    const activeTasks = tasks.filter(t => t.status === 'pending').length;
    const completedTasks = tasks.filter(t => t.status === 'completed').length;
    const taskCompletionRate = tasks.length > 0 ? Math.round((completedTasks / tasks.length) * 100) : 0;
    
    document.getElementById('totalMembers').textContent = totalMembers;
    document.getElementById('onlineMembers').textContent = onlineMembers;
    document.getElementById('activeTasks').textContent = activeTasks;
    document.getElementById('completedTasks').textContent = completedTasks;
    document.getElementById('taskCompletionRate').textContent = taskCompletionRate + '%';
}

// 渲染任务表格
function renderTaskTable() {
    const tbody = document.getElementById('taskTableBody');
    tbody.innerHTML = '';

    let filteredTasks = tasks;
    if (currentTaskFilter === 'pending') {
        filteredTasks = tasks.filter(t => t.status === 'pending');
    } else if (currentTaskFilter === 'completed') {
        filteredTasks = tasks.filter(t => t.status === 'completed');
    }

    filteredTasks.forEach(task => {
        const row = document.createElement('tr');
        const priorityClass = getPriorityClass(task.priority);
        const statusClass = getStatusClass(task.status);
        
        row.innerHTML = `
            <td>${task.name}</td>
            <td>${task.assignee}</td>
            <td><span class="badge ${priorityClass}">${getPriorityText(task.priority)}</span></td>
            <td><span class="badge ${statusClass}">${getStatusText(task.status)}</span></td>
            <td>${task.dueDate}</td>
            <td>
                <div class="progress" style="height: 20px;">
                    <div class="progress-bar" role="progressbar" style="width: ${task.progress}%">${task.progress}%</div>
                </div>
            </td>
            <td>
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

// 获取优先级样式类
function getPriorityClass(priority) {
    switch (priority) {
        case 'high': return 'bg-danger';
        case 'medium': return 'bg-warning';
        case 'low': return 'bg-success';
        default: return 'bg-secondary';
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

// 获取状态样式类
function getStatusClass(status) {
    switch (status) {
        case 'pending': return 'bg-warning';
        case 'completed': return 'bg-success';
        case 'cancelled': return 'bg-danger';
        default: return 'bg-secondary';
    }
}

// 获取状态文本
function getStatusText(status) {
    switch (status) {
        case 'pending': return '进行中';
        case 'completed': return '已完成';
        case 'cancelled': return '已取消';
        default: return '未知';
    }
}

// 过滤任务
function filterTasks(filter) {
    currentTaskFilter = filter;
    renderTaskTable();
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

// 编辑任务
function editTask(taskId) {
    const task = tasks.find(t => t.id === taskId);
    if (task) {
        document.getElementById('taskModalTitle').textContent = '编辑任务';
        document.getElementById('taskId').value = task.id;
        document.getElementById('taskName').value = task.name;
        document.getElementById('taskAssignee').value = task.assignee;
        document.getElementById('taskPriority').value = task.priority;
        document.getElementById('taskDueDate').value = task.dueDate;
        document.getElementById('taskDescription').value = task.description;
        document.getElementById('taskProgress').value = task.progress;
        
        loadAssigneeOptions();
        new bootstrap.Modal(document.getElementById('taskModal')).show();
    }
}

// 保存任务
function saveTask() {
    const form = document.getElementById('taskForm');
    const formData = new FormData(form);
    
    const taskData = {
        name: formData.get('name'),
        assignee: formData.get('assignee'),
        priority: formData.get('priority'),
        dueDate: formData.get('dueDate'),
        description: formData.get('description'),
        progress: parseInt(formData.get('progress'))
    };

    const taskId = document.getElementById('taskId').value;
    
    if (taskId) {
        // 编辑模式
        const index = tasks.findIndex(t => t.id == taskId);
        if (index !== -1) {
            tasks[index] = { ...tasks[index], ...taskData };
        }
    } else {
        // 新增模式
        const newTask = {
            id: tasks.length + 1,
            status: 'pending',
            ...taskData
        };
        tasks.push(newTask);
    }

    renderTaskTable();
    updateStats();
    bootstrap.Modal.getInstance(document.getElementById('taskModal')).hide();
    alert('保存成功！');
}

// 删除任务
function deleteTask(taskId) {
    if (confirm('确定要删除这个任务吗？')) {
        tasks = tasks.filter(t => t.id !== taskId);
        renderTaskTable();
        updateStats();
        alert('删除成功！');
    }
}

// 导入任务
function importTasks() {
    alert('导入任务功能开发中...');
}

// 导出任务
function exportTasks() {
    alert('导出任务功能开发中...');
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
        
        if (result.code === 200) {
            reportData = result.data;
            renderReportTable();
            loadReportStats();
        } else {
            console.error('加载任务进度汇报数据失败:', result.message);
            // 使用假数据
            loadMockReportData();
        }
    } catch (error) {
        console.error('加载任务进度汇报数据失败:', error);
        // 使用假数据
        loadMockReportData();
    }
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
    loadReportStats();
}

// 渲染任务进度汇报表格
function renderReportTable() {
    const tbody = document.getElementById('reportTableBody');
    tbody.innerHTML = '';
    
    reportData.forEach(report => {
        const row = document.createElement('tr');
        const statusClass = getReportStatusClass(report.reportStatus);
        const reportTypeText = getReportTypeText(report.reportType);
        const workModeText = getWorkModeText(report.workMode);
        const abnormalClass = report.isAbnormal ? 'text-warning' : '';
        
        row.innerHTML = `
            <td class="${abnormalClass}">${report.taskName}</td>
            <td>${report.employeeName}</td>
            <td><span class="badge bg-info">${reportTypeText}</span></td>
            <td class="table-cell-truncate" title="${report.reportTitle}">${report.reportTitle}</td>
            <td>
                <div class="progress" style="height: 20px;">
                    <div class="progress-bar" role="progressbar" style="width: ${report.progress}%">${report.progress}%</div>
                </div>
            </td>
            <td>${report.workDuration ? Math.floor(report.workDuration / 60) + '小时' + (report.workDuration % 60) + '分钟' : '-'}</td>
            <td><span class="badge ${statusClass}">${getReportStatusText(report.reportStatus)}</span></td>
            <td>${report.overallScore ? report.overallScore.toFixed(1) : '-'}</td>
            <td>${report.createTime ? report.createTime.split(' ')[0] : '-'}</td>
            <td>
                <button class="btn btn-outline-info btn-sm" onclick="viewReportDetail(${report.id})" title="查看详情">
                    <i class="bi bi-eye"></i>
                </button>
                ${report.reportStatus === 1 ? `
                    <button class="btn btn-outline-success btn-sm" onclick="reviewReport(${report.id})" title="审核">
                        <i class="bi bi-check-circle"></i>
                    </button>
                ` : ''}
                ${report.isAbnormal ? `
                    <button class="btn btn-outline-warning btn-sm" onclick="superviseReport(${report.id})" title="监督处理">
                        <i class="bi bi-shield-check"></i>
                    </button>
                ` : ''}
                <button class="btn btn-outline-primary btn-sm" onclick="editReport(${report.id})" title="编辑">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-outline-danger btn-sm" onclick="deleteReport(${report.id})" title="删除">
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

// 加载汇报统计
function loadReportStats() {
    const totalReports = reportData.length;
    const pendingReports = reportData.filter(r => r.reportStatus === 1).length;
    const approvedReports = reportData.filter(r => r.reportStatus === 2).length;
    const abnormalReports = reportData.filter(r => r.isAbnormal).length;
    
    // 计算平均评分
    const scoredReports = reportData.filter(r => r.overallScore != null);
    const avgScore = scoredReports.length > 0 ? 
        scoredReports.reduce((sum, r) => sum + r.overallScore, 0) / scoredReports.length : 0;
    
    // 计算汇报完成率
    const reportRate = totalReports > 0 ? Math.round((approvedReports / totalReports) * 100) : 0;
    
    document.getElementById('totalReports').textContent = totalReports;
    document.getElementById('pendingReports').textContent = pendingReports;
    document.getElementById('approvedReports').textContent = approvedReports;
    document.getElementById('abnormalReports').textContent = abnormalReports;
    document.getElementById('avgScore').textContent = avgScore.toFixed(1);
    document.getElementById('reportRate').textContent = reportRate + '%';
}

// 查看汇报详情
function viewReportDetail(reportId) {
    const report = reportData.find(r => r.id === reportId);
    if (report) {
        let detailHtml = `
            <div class="report-detail">
                <h6>任务进度汇报详情</h6>
                <p><strong>任务名称:</strong> ${report.taskName}</p>
                <p><strong>员工姓名:</strong> ${report.employeeName}</p>
                <p><strong>汇报类型:</strong> ${getReportTypeText(report.reportType)}</p>
                <p><strong>汇报标题:</strong> ${report.reportTitle}</p>
                <p><strong>汇报内容:</strong> ${report.reportContent}</p>
                <p><strong>完成进度:</strong> ${report.progress}%</p>
                <p><strong>工作时长:</strong> ${report.workDuration ? Math.floor(report.workDuration / 60) + '小时' + (report.workDuration % 60) + '分钟' : '-'}</p>
                <p><strong>工作成果:</strong> ${report.workResults || '-'}</p>
                <p><strong>遇到的问题:</strong> ${report.problems || '-'}</p>
                <p><strong>解决方案:</strong> ${report.solutions || '-'}</p>
                <p><strong>下一步计划:</strong> ${report.nextPlan || '-'}</p>
                <p><strong>需要支持:</strong> ${report.supportNeeded || '-'}</p>
                <p><strong>工作地点:</strong> ${report.workLocation || '-'}</p>
                <p><strong>工作模式:</strong> ${getWorkModeText(report.workMode)}</p>
                <p><strong>汇报状态:</strong> <span class="badge ${getReportStatusClass(report.reportStatus)}">${getReportStatusText(report.reportStatus)}</span></p>
        `;
        
        if (report.overallScore) {
            detailHtml += `
                <p><strong>质量评分:</strong> ${report.qualityScore || '-'}</p>
                <p><strong>效率评分:</strong> ${report.efficiencyScore || '-'}</p>
                <p><strong>态度评分:</strong> ${report.attitudeScore || '-'}</p>
                <p><strong>综合评分:</strong> ${report.overallScore.toFixed(1)}</p>
            `;
        }
        
        if (report.isAbnormal) {
            detailHtml += `
                <p><strong>异常原因:</strong> ${report.abnormalReason || '-'}</p>
            `;
        }
        
        detailHtml += '</div>';
        
        alert(detailHtml.replace(/<[^>]*>/g, '')); // 简单显示，去掉HTML标签
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
    loadReportStats();
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
function showCreateReportModal() {
    document.getElementById('reportModalTitle').textContent = '新建任务进度汇报';
    document.getElementById('reportForm').reset();
    document.getElementById('reportId').value = '';
    loadTaskOptions();
    new bootstrap.Modal(document.getElementById('reportModal')).show();
}

// 加载任务选项
function loadTaskOptions() {
    const select = document.getElementById('reportTaskId');
    select.innerHTML = '<option value="">请选择任务</option>';
    tasks.forEach(task => {
        const option = document.createElement('option');
        option.value = task.id;
        option.textContent = task.name;
        select.appendChild(option);
    });
}

// 编辑汇报
function editReport(reportId) {
    const report = reportData.find(r => r.id === reportId);
    if (report) {
        document.getElementById('reportModalTitle').textContent = '编辑任务进度汇报';
        document.getElementById('reportId').value = report.id;
        document.getElementById('reportTaskId').value = report.taskId;
        document.getElementById('reportType').value = report.reportType;
        document.getElementById('reportTitle').value = report.reportTitle;
        document.getElementById('reportContent').value = report.reportContent;
        document.getElementById('reportProgress').value = report.progress;
        document.getElementById('reportWorkDuration').value = report.workDuration || '';
        document.getElementById('reportWorkMode').value = report.workMode || '';
        document.getElementById('reportWorkResults').value = report.workResults || '';
        document.getElementById('reportProblems').value = report.problems || '';
        document.getElementById('reportSolutions').value = report.solutions || '';
        document.getElementById('reportNextPlan').value = report.nextPlan || '';
        document.getElementById('reportSupportNeeded').value = report.supportNeeded || '';
        document.getElementById('reportWorkLocation').value = report.workLocation || '';
        document.getElementById('reportIsAbnormal').checked = report.isAbnormal || false;
        document.getElementById('reportAbnormalReason').value = report.abnormalReason || '';
        
        // 显示/隐藏异常原因输入框
        const abnormalReasonDiv = document.getElementById('abnormalReasonDiv');
        if (report.isAbnormal) {
            abnormalReasonDiv.style.display = 'block';
        } else {
            abnormalReasonDiv.style.display = 'none';
        }
        
        loadTaskOptions();
        new bootstrap.Modal(document.getElementById('reportModal')).show();
    }
}

// 保存汇报
function saveReport() {
    const form = document.getElementById('reportForm');
    const formData = new FormData(form);
    
    const reportData = {
        taskId: formData.get('taskId'),
        reportType: formData.get('reportType'),
        reportTitle: formData.get('reportTitle'),
        reportContent: formData.get('reportContent'),
        progress: parseInt(formData.get('progress')),
        workDuration: formData.get('workDuration') ? parseInt(formData.get('workDuration')) : null,
        workMode: formData.get('workMode') ? parseInt(formData.get('workMode')) : null,
        workResults: formData.get('workResults'),
        problems: formData.get('problems'),
        solutions: formData.get('solutions'),
        nextPlan: formData.get('nextPlan'),
        supportNeeded: formData.get('supportNeeded'),
        workLocation: formData.get('workLocation'),
        isAbnormal: formData.get('isAbnormal') === 'on',
        abnormalReason: formData.get('abnormalReason')
    };
    
    const reportId = document.getElementById('reportId').value;
    
    if (reportId) {
        // 编辑模式
        const index = reportData.findIndex(r => r.id == reportId);
        if (index !== -1) {
            reportData[index] = { ...reportData[index], ...reportData };
        }
    } else {
        // 新增模式
        const newReport = {
            id: reportData.length + 1,
            employeeId: 1, // 假设当前用户ID为1
            employeeName: '当前用户',
            taskName: tasks.find(t => t.id == reportData.taskId)?.name || '',
            reportStatus: 1, // 默认待审核状态
            createTime: new Date().toISOString().split('T')[0] + ' ' + new Date().toTimeString().split(' ')[0],
            ...reportData
        };
        reportData.push(newReport);
    }
    
    renderReportTable();
    loadReportStats();
    bootstrap.Modal.getInstance(document.getElementById('reportModal')).hide();
    alert('保存成功！');
}

// 删除汇报
function deleteReport(reportId) {
    if (confirm('确定要删除这个汇报吗？')) {
        reportData = reportData.filter(r => r.id !== reportId);
        renderReportTable();
        loadReportStats();
        alert('删除成功！');
    }
}

// 显示待审核汇报
function showPendingReports() {
    const pendingData = reportData.filter(r => r.reportStatus === 1);
    if (pendingData.length === 0) {
        alert('暂无待审核汇报');
        return;
    }
    
    let pendingHtml = '待审核汇报:\n\n';
    pendingData.forEach(report => {
        pendingHtml += `${report.taskName} - ${report.employeeName} - ${report.reportTitle}\n`;
    });
    
    alert(pendingHtml);
}

// 显示异常汇报
function showAbnormalReports() {
    const abnormalData = reportData.filter(r => r.isAbnormal);
    if (abnormalData.length === 0) {
        alert('暂无异常汇报');
        return;
    }
    
    let abnormalHtml = '异常汇报:\n\n';
    abnormalData.forEach(report => {
        abnormalHtml += `${report.taskName} - ${report.employeeName} - ${report.abnormalReason}\n`;
    });
    
    alert(abnormalHtml);
}

// 显示汇报统计
function showReportStats() {
    alert('汇报统计功能开发中...');
}

// 监听异常汇报复选框变化
document.addEventListener('DOMContentLoaded', function() {
    const abnormalCheckbox = document.getElementById('reportIsAbnormal');
    const abnormalReasonDiv = document.getElementById('abnormalReasonDiv');
    
    if (abnormalCheckbox && abnormalReasonDiv) {
        abnormalCheckbox.addEventListener('change', function() {
            if (this.checked) {
                abnormalReasonDiv.style.display = 'block';
            } else {
                abnormalReasonDiv.style.display = 'none';
            }
        });
    }
});
