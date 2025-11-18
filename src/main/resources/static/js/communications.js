// 沟通记录数据
let customerCommunications = [];
let selectedCommunications = [];

// 当前关联的客户ID（用于在客户详情中显示）
let currentCustomerIdForCommunication = null;

// 语音输入相关变量
let isRecordingForContent = false;
let recognitionForContent = null;
let mediaRecorderForContent = null;
let audioChunksForContent = [];

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

// 语音输入功能 - 用于沟通内容
function startVoiceInputForContent() {
    console.log('【语音输入】点击录音按钮，当前状态:', isRecordingForContent);
    
    if (!isRecordingForContent) {
        console.log('【语音输入】开始录音...');
        startRecordingForContent();
    } else {
        console.log('【语音输入】停止录音...');
        stopRecordingForContent();
    }
}

// 开始录音
async function startRecordingForContent() {
    console.log('【语音输入】startRecordingForContent 函数被调用');
    
    try {
        // 检查浏览器支持
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            console.error('【语音输入】浏览器不支持getUserMedia');
            alert('您的浏览器不支持语音录制功能，请使用Chrome、Firefox或Safari浏览器');
            return;
        }
        
        console.log('【语音输入】浏览器支持getUserMedia');
        
        // 检查HTTPS环境
        if (location.protocol !== 'https:' && location.hostname !== 'localhost' && location.hostname !== '127.0.0.1') {
            console.warn('【语音输入】非HTTPS环境:', location.protocol, location.hostname);
            alert('语音功能需要在HTTPS环境下使用，请使用HTTPS访问或本地环境');
            return;
        }
        
        console.log('【语音输入】环境检查通过');
        
        // 显示录音状态
        const statusDiv = document.getElementById('voiceRecognitionStatus');
        const statusText = document.getElementById('voiceStatusText');
        const voiceBtn = document.getElementById('voiceInputBtn');
        const voiceIcon = document.getElementById('voiceInputIcon');
        const voiceText = document.getElementById('voiceInputText');
        
        if (statusDiv && statusText) {
            statusDiv.style.display = 'block';
            statusText.textContent = '正在请求麦克风权限...';
            statusDiv.className = 'mt-2';
            statusDiv.innerHTML = '<div class="alert alert-info mb-0"><i class="bi bi-mic-fill"></i> <span id="voiceStatusText">正在请求麦克风权限...</span></div>';
        }
        
        // 请求麦克风权限
        const stream = await navigator.mediaDevices.getUserMedia({ 
            audio: {
                echoCancellation: true,
                noiseSuppression: true,
                autoGainControl: true
            } 
        });
        
        // 更新状态
        if (statusDiv) {
            statusDiv.className = 'mt-2';
            statusDiv.innerHTML = '<div class="alert alert-danger mb-0"><i class="bi bi-mic-fill"></i> <span id="voiceStatusText">正在录音中... 点击录音按钮停止</span></div>';
        }
        
        // 更新按钮状态
        if (voiceBtn) {
            voiceBtn.classList.remove('btn-outline-primary');
            voiceBtn.classList.add('btn-danger');
            if (voiceIcon) voiceIcon.className = 'bi bi-stop-circle';
            if (voiceText) voiceText.textContent = '停止';
        }
        
        // 初始化语音识别（使用Web Speech API）
        if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            recognitionForContent = new SpeechRecognition();
            recognitionForContent.lang = 'zh-CN';
            recognitionForContent.continuous = true;
            recognitionForContent.interimResults = true;
            
            let finalTranscript = '';
            const contentTextarea = document.getElementById('content');
            
            recognitionForContent.onresult = function(event) {
                console.log('【语音输入】收到识别结果，结果数量:', event.results.length);
                let interimTranscript = '';
                let newFinalTranscript = '';
                
                for (let i = event.resultIndex; i < event.results.length; i++) {
                    const transcript = event.results[i][0].transcript;
                    const isFinal = event.results[i].isFinal;
                    console.log('【语音输入】识别结果', i, ':', transcript, 'isFinal:', isFinal);
                    
                    if (isFinal) {
                        newFinalTranscript += transcript;
                    } else {
                        interimTranscript += transcript;
                    }
                }
                
                // 只添加新的最终结果，避免重复
                if (newFinalTranscript) {
                    finalTranscript += newFinalTranscript;
                    console.log('【语音输入】最终文本:', finalTranscript);
                    console.log('【语音输入】新识别文本:', newFinalTranscript);
                    
                    // 更新文本区域
                    if (contentTextarea) {
                        const currentText = contentTextarea.value || '';
                        contentTextarea.value = currentText + newFinalTranscript;
                        console.log('【语音输入】已更新文本区域，当前内容长度:', contentTextarea.value.length);
                        
                        // 触发input事件以更新字符计数等
                        contentTextarea.dispatchEvent(new Event('input'));
                        // 自动滚动到底部
                        contentTextarea.scrollTop = contentTextarea.scrollHeight;
                    } else {
                        console.error('【语音输入】找不到content文本区域');
                    }
                }
                
                // 更新状态显示（显示临时结果）
                const statusTextEl = document.getElementById('voiceStatusText');
                if (statusTextEl) {
                    const displayText = (finalTranscript + interimTranscript).trim();
                    if (displayText) {
                        statusTextEl.textContent = '正在识别: ' + displayText.substring(0, 50) + (displayText.length > 50 ? '...' : '');
                    } else {
                        statusTextEl.textContent = '正在录音中... 请说话';
                    }
                }
            };
            
            recognitionForContent.onerror = function(event) {
                console.error('【语音输入】语音识别错误:', event.error);
                let errorMsg = '语音识别错误: ';
                if (event.error === 'no-speech') {
                    errorMsg = '未检测到语音，请重试';
                } else if (event.error === 'audio-capture') {
                    errorMsg = '无法访问麦克风';
                } else if (event.error === 'not-allowed') {
                    errorMsg = '麦克风权限被拒绝，请在浏览器设置中允许访问麦克风';
                } else if (event.error === 'aborted') {
                    errorMsg = '语音识别被中止';
                } else if (event.error === 'network') {
                    errorMsg = '网络错误，请检查网络连接';
                } else {
                    errorMsg += event.error;
                }
                
                console.error('【语音输入】错误详情:', errorMsg);
                
                const statusTextEl = document.getElementById('voiceStatusText');
                if (statusTextEl) {
                    statusTextEl.textContent = errorMsg;
                }
                
                // 停止录音
                stopRecordingForContent();
            };
            
            recognitionForContent.onstart = function() {
                console.log('【语音输入】✅ 语音识别已成功启动');
            };
            
            recognitionForContent.onend = function() {
                console.log('【语音输入】语音识别已结束，当前录音状态:', isRecordingForContent);
                if (isRecordingForContent) {
                    // 如果还在录音状态，重新启动识别
                    try {
                        console.log('【语音输入】重新启动语音识别...');
                        recognitionForContent.start();
                    } catch (e) {
                        console.error('【语音输入】重新启动语音识别失败:', e);
                        stopRecordingForContent();
                    }
                }
            };
            
            try {
                console.log('【语音输入】准备启动语音识别...');
                recognitionForContent.start();
                isRecordingForContent = true;
                console.log('【语音输入】语音识别启动命令已执行，isRecordingForContent:', isRecordingForContent);
                
                // 自动停止录音（60秒）
                setTimeout(() => {
                    if (isRecordingForContent) {
                        console.log('【语音输入】60秒自动停止');
                        stopRecordingForContent();
                        alert('录音时间已到60秒，已自动停止。');
                    }
                }, 60000);
            } catch (startError) {
                console.error('【语音输入】启动语音识别失败:', startError);
                alert('启动语音识别失败: ' + startError.message + '\n\n请确保使用Chrome浏览器，并允许麦克风权限。');
                stopRecordingForContent();
            }
            
        } else {
            // 如果不支持Web Speech API，提示用户
            alert('您的浏览器不支持语音识别功能，请使用Chrome浏览器');
            stopRecordingForContent();
        }
        
    } catch (error) {
        console.error('无法访问麦克风:', error);
        
        let errorMessage = '无法访问麦克风';
        if (error.name === 'NotAllowedError') {
            errorMessage = '麦克风权限被拒绝，请在浏览器设置中允许访问麦克风';
        } else if (error.name === 'NotFoundError') {
            errorMessage = '未找到麦克风设备，请检查设备连接';
        } else if (error.name === 'NotSupportedError') {
            errorMessage = '您的浏览器不支持语音录制功能';
        } else if (error.name === 'NotReadableError') {
            errorMessage = '麦克风被其他应用占用，请关闭其他应用后重试';
        }
        
        const statusDiv = document.getElementById('voiceRecognitionStatus');
        if (statusDiv) {
            statusDiv.style.display = 'block';
            statusDiv.className = 'mt-2';
            statusDiv.innerHTML = '<div class="alert alert-danger mb-0"><i class="bi bi-exclamation-triangle"></i> ' + errorMessage + '</div>';
        }
        
        alert(errorMessage);
        stopRecordingForContent();
    }
}

// 停止录音
function stopRecordingForContent() {
    console.log('【语音输入】stopRecordingForContent 函数被调用');
    isRecordingForContent = false;
    
    // 停止语音识别
    if (recognitionForContent) {
        try {
            console.log('【语音输入】停止语音识别...');
            recognitionForContent.stop();
            recognitionForContent = null;
            console.log('【语音输入】语音识别已停止');
        } catch (e) {
            console.error('【语音输入】停止语音识别失败:', e);
        }
    }
    
    // 停止MediaRecorder
    if (mediaRecorderForContent && mediaRecorderForContent.state !== 'inactive') {
        try {
            console.log('【语音输入】停止MediaRecorder，当前状态:', mediaRecorderForContent.state);
            mediaRecorderForContent.stop();
            console.log('【语音输入】MediaRecorder已停止');
        } catch (e) {
            console.error('【语音输入】停止MediaRecorder失败:', e);
        }
        mediaRecorderForContent = null;
    }
    
    // 更新UI状态
    const statusDiv = document.getElementById('voiceRecognitionStatus');
    const voiceBtn = document.getElementById('voiceInputBtn');
    const voiceIcon = document.getElementById('voiceInputIcon');
    const voiceText = document.getElementById('voiceInputText');
    
    if (statusDiv) {
        statusDiv.style.display = 'none';
        console.log('【语音输入】状态显示已隐藏');
    }
    
    if (voiceBtn) {
        voiceBtn.classList.remove('btn-danger');
        voiceBtn.classList.add('btn-outline-primary');
        if (voiceIcon) voiceIcon.className = 'bi bi-mic';
        if (voiceText) voiceText.textContent = '录音';
        console.log('【语音输入】按钮状态已恢复');
    }
}

