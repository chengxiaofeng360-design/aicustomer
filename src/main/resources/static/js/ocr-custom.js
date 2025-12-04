
// 处理名片上传
function handleBusinessCardUpload(event) {
    const file = event.target.files[0];
    if (!file) return;
    processBusinessCardFile(file);
}

// 处理名片拖拽
function handleBusinessCardDrop(event) {
    event.preventDefault();
    event.stopPropagation();
    document.getElementById('businessCardUploadZone').classList.remove('drag-over');

    const file = event.dataTransfer.files[0];
    if (!file) return;
    processBusinessCardFile(file);
}

function handleBusinessCardDragOver(event) {
    event.preventDefault();
    event.stopPropagation();
    document.getElementById('businessCardUploadZone').classList.add('drag-over');
}

function handleBusinessCardDragLeave(event) {
    event.preventDefault();
    event.stopPropagation();
    document.getElementById('businessCardUploadZone').classList.remove('drag-over');
}

// 处理名片文件
function processBusinessCardFile(file) {
    // 验证文件类型和大小
    if (!file.type.startsWith('image/')) {
        alert('请上传图片文件');
        return;
    }
    if (file.size > 5 * 1024 * 1024) {
        alert('文件大小不能超过5MB');
        return;
    }

    // 显示预览
    const reader = new FileReader();
    reader.onload = function (e) {
        document.getElementById('businessCardPreviewImg').src = e.target.result;
        document.getElementById('businessCardFileInfo').textContent = `${file.name} (${formatFileSize(file.size)})`;
        document.getElementById('businessCardUploadArea').style.display = 'none';
        document.getElementById('businessCardPreview').style.display = 'block';

        // 开始识别
        recognizeBusinessCard(file);
    };
    reader.readAsDataURL(file);
}

// 清除名片预览
function clearBusinessCardPreview() {
    document.getElementById('businessCardImage').value = '';
    document.getElementById('businessCardPreviewImg').src = '';
    document.getElementById('businessCardUploadArea').style.display = 'block';
    document.getElementById('businessCardPreview').style.display = 'none';
    document.getElementById('businessCardRecognizing').style.display = 'none';
}

// 调用后端API识别名片
function recognizeBusinessCard(file) {
    const recognizingDiv = document.getElementById('businessCardRecognizing');
    recognizingDiv.style.display = 'block';

    const formData = new FormData();
    formData.append('file', file);

    fetch('/api/ocr/business-card', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(result => {
            recognizingDiv.style.display = 'none';

            if (result.code === 200 && result.data) {
                const customer = result.data;
                console.log('名片识别结果:', customer);

                // 自动填充到文本录入框
                // 构造一个格式化的文本
                let text = '';
                if (customer.name) text += `姓名：${customer.name}\n`;
                if (customer.company) text += `公司：${customer.company}\n`;
                if (customer.position) text += `职位：${customer.position}\n`;
                if (customer.phone) text += `电话：${customer.phone}\n`;
                if (customer.email) text += `邮箱：${customer.email}\n`;
                if (customer.address) text += `地址：${customer.address}\n`;
                if (customer.remark) text += `备注：${customer.remark}\n`;

                // 切换到文本录入标签页
                const textTab = new bootstrap.Tab(document.getElementById('text-tab'));
                textTab.show();

                // 填充数据
                const textarea = document.getElementById('batchImportData');
                textarea.value = text;

                // 自动触发解析
                setTimeout(() => {
                    // 构造一个符合parseImportData预期的数据结构
                    // 这里我们直接调用parseImportData可能不合适，因为它期望特定格式的文本
                    // 或者我们可以直接填充processedData并显示预览

                    // 更好的方式是：将识别结果转换为标准格式文本，然后放入textarea
                    // 标准格式：客户名称 | 联系人 | 电话 | 客户类型 | 地区 | ...
                    // 但OCR结果可能不完整，所以还是让用户确认比较好

                    // 尝试构建一行标准格式数据
                    const line = [
                        customer.company || customer.name || '', // 客户名称
                        customer.name || '', // 联系人
                        customer.phone || '', // 电话
                        '企业', // 默认类型
                        '', // 地区
                        customer.position || '', // 职务
                        '', // QQ/微信
                        '', // 合作内容
                        customer.email || '', // 邮箱
                        customer.address || '', // 地址
                        customer.remark || '' // 备注
                    ].join(' | ');

                    textarea.value = line;

                    alert('识别成功！已自动填入数据录入框，请检查并完善信息后点击"解析数据"');
                }, 500);

            } else {
                alert('识别失败: ' + (result.message || '未知错误'));
            }
        })
        .catch(error => {
            recognizingDiv.style.display = 'none';
            console.error('OCR error:', error);
            alert('识别请求失败，请重试');
        });
}

// 格式化文件大小
function formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}
