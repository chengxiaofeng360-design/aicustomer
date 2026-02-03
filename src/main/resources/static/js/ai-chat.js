let chatHistory = [];
let isTyping = false;
let currentChatId = null;
let conversationHistory = []; // 用于保存对话历史，传递给DeepSeek
let sessionList = [];
let activeSessionId = null;
let currentUser = null;
let isLoadingSessions = false;
let isLoadingMessages = false;

// 侧边栏折叠/展开功能
function toggleSidebar() {
    const sidebar = document.getElementById('chatSidebar');
    const toggleIcon = document.getElementById('sidebarToggleIcon');

    if (sidebar.classList.contains('collapsed')) {
        sidebar.classList.remove('collapsed');
        toggleIcon.className = 'bi bi-chevron-left';
    } else {
        sidebar.classList.add('collapsed');
        toggleIcon.className = 'bi bi-chevron-right';
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function () {
    // 初始化 Markdown 配置 (解决流式输出格式跳变问题)
    if (typeof marked !== 'undefined') {
        marked.use({
            breaks: true,
            gfm: true
        });
    }

    const input = document.getElementById('messageInput');
    if (input) {
        input.focus();
    }

    initCurrentUser();
    initializeChat();
    initKeyboardShortcuts();
    handlePresetMessage();
});

// 初始化键盘快捷键
function initKeyboardShortcuts() {
    document.addEventListener('keydown', function (event) {
        // Ctrl/Cmd + K: 聚焦到输入框
        if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
            event.preventDefault();
            const input = document.getElementById('messageInput');
            if (input) {
                input.focus();
            }
        }

        // Esc: 关闭弹窗和模态框
        if (event.key === 'Escape') {
            const imageModal = document.querySelector('.image-modal');
            if (imageModal) {
                imageModal.remove();
            }

        }
    });
}

function initCurrentUser() {
    try {
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
            currentUser = JSON.parse(storedUser);
        }
    } catch (error) {
        console.warn('解析当前用户信息失败:', error);
        currentUser = null;
    }
}

function renderWelcomeMessage() {
    const container = document.getElementById('chatMessages');
    if (!container) return;
    container.innerHTML = `
        <div class="message ai-message">
            <div class="message-avatar">
                <i class="bi bi-robot"></i>
            </div>
            <div class="message-content">
                <div class="message-bubble">
                    <div class="welcome-content">
                        <h6 class="welcome-title">
                            <i class="bi bi-sparkles me-2"></i>欢迎使用AI智能聊天
                        </h6>
                        <p>您好！我是专门为AI客户管理系统设计的AI智能助手。</p>
                        <div class="capabilities">
                            <h6>我可以帮助您：</h6>
                            <div class="capability-grid">
                                <div class="capability-item">
                                    <i class="bi bi-graph-up"></i>
                                    <span>数据分析</span>
                                </div>
                                <div class="capability-item">
                                    <i class="bi bi-bullseye"></i>
                                    <span>营销策略</span>
                                </div>
                                <div class="capability-item">
                                    <i class="bi bi-box-seam"></i>
                                    <span>产品推荐</span>
                                </div>
                                <div class="capability-item">
                                    <i class="bi bi-headset"></i>
                                    <span>客户服务</span>
                                </div>
                            </div>
                        </div>
                        <p class="welcome-footer">请随时向我提问，我会为您提供专业的建议和帮助！</p>
                    </div>
                </div>
                <div class="message-time">刚刚</div>
            </div>
        </div>
    `;
    setCurrentSessionSummary('与AI助手的对话', '准备开始新的对话');
}

async function loadSessions(autoSelect = false) {
    if (isLoadingSessions) return;
    isLoadingSessions = true;
    renderHistoryState('正在加载会话...', false);
    try {
        const response = await fetch('/api/ai-chat/sessions');
        const result = await response.json();
        if (response.ok && result.code === 200) {
            sessionList = Array.isArray(result.data) ? result.data : [];
            renderSessionList();
            if (autoSelect && sessionList.length > 0) {
                selectSession(sessionList[0].sessionId);
            } else if (!sessionList.length) {
                setCurrentSessionSummary('新建对话', '还没有历史记录');
            }
        } else {
            renderHistoryState(result.message || '加载会话失败', true);
        }
    } catch (error) {
        renderHistoryState(error.message || '加载会话失败', true);
    } finally {
        isLoadingSessions = false;
    }
}

function renderHistoryState(message, isError = false) {
    const historyList = document.getElementById('historyList');
    if (!historyList) return;
    const safeMessage = escapeHtml(message || '');
    historyList.innerHTML = `<div class="history-empty ${isError ? 'text-danger' : 'text-white-50'}">${safeMessage}</div>`;
}

function renderSessionList(activeId = activeSessionId) {
    const historyList = document.getElementById('historyList');
    if (!historyList) return;

    if (!sessionList || sessionList.length === 0) {
        renderHistoryState('暂无会话记录', false);
        return;
    }

    const sortedSessions = [...sessionList].sort((a, b) => {
        const timeA = a.lastMessageTime ? new Date(a.lastMessageTime).getTime() : 0;
        const timeB = b.lastMessageTime ? new Date(b.lastMessageTime).getTime() : 0;
        return timeB - timeA;
    });

    historyList.innerHTML = '';
    sortedSessions.forEach(session => {
        const item = document.createElement('div');
        item.className = 'history-item';
        if (session.sessionId === activeId) {
            item.classList.add('active');
        }
        item.dataset.sessionId = session.sessionId;

        const title = session.firstUserMessage || '与AI助手的对话';
        const meta = formatSessionMeta(session);

        item.innerHTML = `
            <i class="bi bi-chat-dots"></i>
            <div class="history-info">
                <span class="history-title">${escapeHtml(title)}</span>
                <span class="history-meta">${escapeHtml(meta)}</span>
            </div>
            <div class="delete-session-btn" title="删除会话" onclick="event.stopPropagation(); deleteSession('${session.sessionId}')">
                <i class="bi bi-trash"></i>
            </div>
        `;

        item.addEventListener('click', () => selectSession(session.sessionId));
        historyList.appendChild(item);
    });
}

/**
 * 删除会话
 */
async function deleteSession(sessionId) {
    if (!sessionId) return;

    if (!confirm('确定要删除这段对话历史吗？该操作不可撤销。')) {
        return;
    }

    try {
        const response = await fetch(`/api/ai-chat/sessions/${encodeURIComponent(sessionId)}`, {
            method: 'DELETE'
        });
        const result = await response.json();

        if (response.ok && result.code === 200) {
            // 从内存列表中移除
            sessionList = sessionList.filter(s => s.sessionId !== sessionId);

            // 如果删除的是当前会话，清除界面
            if (activeSessionId === sessionId) {
                activeSessionId = null;
                currentChatId = null;
                chatHistory = [];
                conversationHistory = [];
                renderWelcomeMessage();
                setCurrentSessionSummary('请选择或新建会话', '等待开始新的对话');
            }

            // 重新渲染列表
            renderSessionList(activeSessionId);

            // 显示成功提示
            showDeleteSuccess();
        } else {
            alert('删除失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('删除会话异常:', error);
        alert('删除失败，请稍后重试');
    }
}

/**
 * 显示删除成功提示
 */
function showDeleteSuccess() {
    const toast = document.createElement('div');
    toast.className = 'toast show position-fixed';
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999;';
    toast.innerHTML = `
        <div class="toast-header">
            <i class="bi bi-check-circle text-success me-2"></i>
            <strong class="me-auto">删除成功</strong>
            <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
        </div>
        <div class="toast-body">
            对话历史已成功删除
        </div>
    `;

    document.body.appendChild(toast);
    setTimeout(() => { if (toast.parentNode) toast.remove(); }, 3000);
}

async function selectSession(sessionId) {
    if (!sessionId || isLoadingMessages) return;
    activeSessionId = sessionId;
    currentChatId = sessionId;
    renderSessionList(sessionId);
    await loadMessagesBySessionId(sessionId);
}

async function loadMessagesBySessionId(sessionId) {
    isLoadingMessages = true;
    const container = document.getElementById('chatMessages');
    if (container) {
        container.innerHTML = `<div class="history-empty text-muted">正在加载消息...</div>`;
    }
    chatHistory = [];
    conversationHistory = [];
    try {
        const response = await fetch(`/api/ai-chat/messages?sessionId=${encodeURIComponent(sessionId)}`);
        const result = await response.json();
        if (response.ok && result.code === 200) {
            const messages = Array.isArray(result.data) ? result.data : [];
            if (!messages.length) {
                renderWelcomeMessage();
                setCurrentSessionSummary('新建对话', '暂无消息');
                return;
            }
            if (container) container.innerHTML = '';
            messages.forEach((msg, index) => {
                const sender = msg.messageType === 1 ? 'user' : 'ai';
                const text = resolveMessageContent(msg);
                if (!text) return;
                addMessage(text, sender === 'system' ? 'ai' : sender, {
                    timestamp: msg.createTime || msg.replyTime,
                    recordHistory: true,
                    skipScroll: index !== messages.length - 1
                });
                if (sender === 'user') {
                    conversationHistory.push({ role: 'user', content: text });
                } else if (sender === 'ai') {
                    conversationHistory.push({ role: 'assistant', content: text });
                }
            });
            trimConversationHistory();
            const session = sessionList.find(item => item.sessionId === sessionId);
            setCurrentSessionSummary(
                session ? (session.firstUserMessage || '与AI助手的对话') : '与AI助手的对话',
                formatSessionMeta(session || { messageCount: messages.length, lastMessageTime: messages[messages.length - 1]?.createTime })
            );
        } else {
            renderMessagesError(result.message || '加载消息失败');
        }
    } catch (error) {
        renderMessagesError(error.message || '加载消息失败');
    } finally {
        isLoadingMessages = false;
    }
}

function renderMessagesError(message) {
    const container = document.getElementById('chatMessages');
    if (container) {
        container.innerHTML = `<div class="history-empty text-danger">${escapeHtml(message || '加载失败')}</div>`;
    }
}

function formatSessionMeta(session) {
    if (!session) return '暂无消息';
    const countText = session.messageCount ? `消息 ${session.messageCount}` : '暂无消息';
    const timeText = formatDisplayTime(session.lastMessageTime);
    return `${countText} · ${timeText}`;
}

function formatDisplayTime(timestamp) {
    if (!timestamp) return '刚刚';
    const date = typeof timestamp === 'string' ? new Date(timestamp) : timestamp;
    if (Number.isNaN(date.getTime())) return '刚刚';
    const today = new Date();
    const sameDay = date.toDateString() === today.toDateString();
    return sameDay
        ? date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
        : date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
}

function setCurrentSessionSummary(title, meta) {
    const titleEl = document.getElementById('currentChatTitle');
    const metaEl = document.getElementById('currentChatMeta');
    if (titleEl) {
        titleEl.textContent = title || '与AI助手的对话';
    }
    if (metaEl) {
        metaEl.textContent = meta || '等待新的对话';
    }
}

function resolveMessageContent(msg) {
    if (!msg) return '';
    if (msg.messageType === 1) {
        return msg.content || msg.userMessage || '';
    }
    return msg.replyContent || msg.content || '';
}

function ensureSessionInList(session) {
    if (!session || !session.sessionId) return;
    const exists = sessionList.find(item => item.sessionId === session.sessionId);
    if (!exists) {
        sessionList.unshift(session);
    }
}

function updateSessionAfterMessage(userMessage, aiMessage) {
    if (!currentChatId) return;
    let session = sessionList.find(item => item.sessionId === currentChatId);
    const isoNow = new Date().toISOString();
    if (!session) {
        session = {
            sessionId: currentChatId,
            firstUserMessage: userMessage,
            lastAiReply: aiMessage,
            messageCount: 0,
            lastMessageTime: isoNow
        };
        sessionList.unshift(session);
    }
    session.messageCount = (session.messageCount || 0) + 2;
    session.lastMessageTime = isoNow;
    session.firstUserMessage = session.firstUserMessage || userMessage;
    session.lastAiReply = aiMessage;
    renderSessionList(currentChatId);
    setCurrentSessionSummary(session.firstUserMessage || '与AI助手的对话', formatSessionMeta(session));
}

async function requestNewSession() {
    const payload = {
        userId: currentUser?.id || null,
        customerId: null
    };
    const response = await fetch('/api/ai-chat/sessions/new', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    });
    const result = await response.json();
    if (response.ok && result.code === 200 && result.data) {
        return result.data.sessionId;
    }
    throw new Error(result.message || '创建新会话失败');
}

async function createNewChatSession() {
    try {
        const sessionId = await requestNewSession();
        currentChatId = sessionId;
        activeSessionId = sessionId;
        conversationHistory = [];
        chatHistory = [];
        renderWelcomeMessage();
        setCurrentSessionSummary('新建对话', '等待您的第一条消息');
        ensureSessionInList({
            sessionId,
            firstUserMessage: null,
            lastAiReply: null,
            messageCount: 0,
            lastMessageTime: new Date().toISOString()
        });
        renderSessionList(sessionId);
    } catch (error) {
        alert('创建新会话失败：' + (error.message || '未知错误'));
        throw error;
    }
}

async function ensureActiveSession() {
    if (currentChatId) {
        return currentChatId;
    }
    await createNewChatSession();
    return currentChatId;
}

function trimConversationHistory(limit = 20) {
    if (conversationHistory.length > limit) {
        conversationHistory = conversationHistory.slice(conversationHistory.length - limit);
    }
}

// 初始化聊天界面
function initializeChat() {
    renderWelcomeMessage();
    loadSessions(true);
}

// 处理从首页传入的快捷指令
function handlePresetMessage() {
    const preset = localStorage.getItem('aiChatPreset');
    if (!preset) return;

    const input = document.getElementById('messageInput');
    if (!input) {
        setTimeout(handlePresetMessage, 200);
        return;
    }

    input.value = preset;
    autoResize(input);
    localStorage.removeItem('aiChatPreset');

    // 轻微延迟后自动发送，确保界面就绪
    setTimeout(() => {
        sendMessage();
    }, 200);
}

// 发送消息
async function sendMessage() {
    const input = document.getElementById('messageInput');
    const message = input.value.trim();

    if (!message) return;

    try {
        await ensureActiveSession();
    } catch (error) {
        console.error('创建会话失败:', error);
        return;
    }

    // 禁用发送按钮和输入框
    const sendBtn = document.getElementById('sendBtn');
    if (sendBtn) {
        sendBtn.disabled = true;
        sendBtn.innerHTML = '<i class="bi bi-hourglass-split"></i><span class="send-label">发送中...</span>';
    }
    input.disabled = true;

    // 添加用户消息
    addMessage(message, 'user');

    // 添加到对话历史
    conversationHistory.push({
        role: 'user',
        content: message
    });
    trimConversationHistory();

    // 清空输入框并重置高度
    input.value = '';
    autoResize(input);

    // 准备AI回复容器
    const aiMessageId = 'msg_ai_' + Date.now();
    addMessage('', 'ai', {
        id: aiMessageId,
        recordHistory: false // 暂时不记录，等生成完再记录
    });
    const aiMessageBubble = document.querySelector(`#${aiMessageId} .message-text`);
    let fullAiResponse = '';

    try {
        // 调用后端流式API
        console.log('【前端】开始调用后端流式API');
        const response = await fetch('/api/ai-chat/send/stream', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                sessionId: currentChatId,
                message: message,
                customerId: null,
                history: conversationHistory
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split(/\r?\n/);
            buffer = lines.pop(); // 保留最后一个可能不完整的片段

            for (const line of lines) {
                if (line.trim() === '') continue;
                if (line.startsWith('data:')) {
                    const content = line.slice(5); // 去掉 'data:' 前缀
                    fullAiResponse += content;

                    // 实时更新UI
                    updateMessageContent(aiMessageId, fullAiResponse);
                }
            }

            // 保持滚动到底部
            requestAnimationFrame(() => {
                scrollToBottom();
            });
        }

        // 流结束
        console.log('【前端】流式响应结束');

        // 最终更新一次（处理Markdown闭合等）
        updateMessageContent(aiMessageId, fullAiResponse);

        // 添加到对话历史
        conversationHistory.push({
            role: 'assistant',
            content: fullAiResponse
        });
        trimConversationHistory();
        updateSessionAfterMessage(message, fullAiResponse);

        // 记录到本地历史（为了页面刷新后能看到）
        addToHistory(fullAiResponse, 'ai', new Date().toISOString());

    } catch (error) {
        console.error('发送消息失败:', error);
        const errorMessage = '\n\n[系统错误: ' + (error.message || '网络连接中断') + ']';
        fullAiResponse += errorMessage;
        updateMessageContent(aiMessageId, fullAiResponse);

        conversationHistory.push({
            role: 'assistant',
            content: fullAiResponse
        });
        trimConversationHistory();
        addToHistory(fullAiResponse, 'ai', new Date().toISOString());
    } finally {
        // 恢复发送按钮和输入框
        if (sendBtn) {
            sendBtn.disabled = false;
            sendBtn.innerHTML = '<i class="bi bi-send"></i><span class="send-label">发送</span>';
        }
        if (input) {
            input.disabled = false;
            input.focus();
        }
    }
}

// 更新消息内容（支持Markdown）
function updateMessageContent(messageId, content) {
    const messageDiv = document.getElementById(messageId);
    if (!messageDiv) return;

    const textContainer = messageDiv.querySelector('.message-text');
    if (!textContainer) return;

    let htmlContent = content;
    if (typeof marked !== 'undefined') {
        try {
            // 强制开启换行支持 (breaks: true)
            htmlContent = marked.parse(content, {
                breaks: true,
                gfm: true
            });
        } catch (e) {
            console.error('Markdown解析失败:', e);
            htmlContent = escapeHtml(content).replace(/\n/g, '<br>');
        }
    } else {
        htmlContent = escapeHtml(content).replace(/\n/g, '<br>');
    }

    textContainer.innerHTML = htmlContent;

    // 代码高亮
    if (typeof hljs !== 'undefined') {
        textContainer.querySelectorAll('pre code').forEach((block) => {
            hljs.highlightElement(block);
        });
    }
}

// 发送快速消息
function sendQuickMessage(message) {
    // 设置输入框内容
    document.getElementById('messageInput').value = message;

    // 滚动到聊天区域
    scrollToChatArea();

    // 发送消息
    sendMessage();
}

// 滚动到聊天区域
function scrollToChatArea() {
    const chatArea = document.querySelector('.chat-main-area');
    if (chatArea) {
        chatArea.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });

        // 聚焦到输入框
        setTimeout(() => {
            document.getElementById('messageInput').focus();
        }, 500);
    }
}

// 添加消息到聊天界面
function addMessage(content, sender, options = {}) {
    const { timestamp = new Date(), recordHistory = true, skipScroll = false, id = null } = options;
    const messagesContainer = document.getElementById('chatMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${sender}-message`;

    const time = formatDisplayTime(timestamp);

    // 转义HTML防止XSS攻击
    const escapedContent = escapeHtml(content);

    if (sender === 'user') {
        messageDiv.innerHTML = `
            <div class="message-content">
                <div class="message-bubble">
                    <p>${escapedContent}</p>
                </div>
                <div class="message-time">${time}</div>
            </div>
            <div class="message-avatar">
                <i class="bi bi-person-fill"></i>
            </div>
        `;
    } else {
        // AI消息添加语音播放按钮和来源显示
        const messageId = id || ('msg_' + Date.now());
        const escapedContentForAttr = content.replace(/'/g, "&#39;").replace(/"/g, "&quot;").replace(/\n/g, '\\n');

        // 解析来源信息
        let sourceHtml = '';
        let displayContent = content; // AI消息不转义，交给marked处理

        // 检查是否有来源标记
        if (displayContent.includes('(来源:')) {
            const parts = displayContent.split('\n\n(来源:');
            if (parts.length > 1) {
                displayContent = parts[0];
                const sourceText = parts[1].replace(')', '').trim();
                let sourceIcon = 'bi-stars';
                let sourceClass = 'bg-primary';

                if (sourceText.includes('常见问题')) {
                    sourceIcon = 'bi-question-circle';
                    sourceClass = 'bg-success';
                } else if (sourceText.includes('知识库')) {
                    sourceIcon = 'bi-book';
                    sourceClass = 'bg-info';
                }

                sourceHtml = `
                    <div class="message-source mt-2">
                        <span class="badge ${sourceClass} bg-opacity-10 text-dark border border-${sourceClass.replace('bg-', '')} rounded-pill px-2 py-1" style="font-size: 0.75rem;">
                            <i class="bi ${sourceIcon} me-1"></i> ${sourceText}
                        </span>
                    </div>
                `;
            }
        }

        // 使用marked解析Markdown
        let htmlContent = displayContent;
        if (typeof marked !== 'undefined') {
            try {
                htmlContent = marked.parse(displayContent);
            } catch (e) {
                console.error('Markdown解析失败:', e);
                htmlContent = escapeHtml(displayContent).replace(/\n/g, '<br>');
            }
        } else {
            htmlContent = escapeHtml(displayContent).replace(/\n/g, '<br>');
        }

        messageDiv.innerHTML = `
            <div class="message-avatar">
                <i class="bi bi-robot"></i>
            </div>
            <div class="message-content">
                <div class="message-bubble">
                    <div class="message-text">${htmlContent}</div>
                    ${sourceHtml}
                    <div class="message-actions">
                        <button class="btn btn-outline-secondary btn-sm" onclick="copyMessage('${messageId}')" title="复制内容">
                            <i class="bi bi-clipboard"></i>
                        </button>
                    </div>
                </div>
                <div class="message-time">${time}</div>
            </div>
        `;
        messageDiv.id = messageId;

        // 代码高亮
        if (typeof hljs !== 'undefined') {
            setTimeout(() => {
                messageDiv.querySelectorAll('pre code').forEach((block) => {
                    hljs.highlightElement(block);
                });
            }, 0);
        }
    }

    messagesContainer.appendChild(messageDiv);

    if (!skipScroll) {
        // 使用requestAnimationFrame优化滚动性能
        requestAnimationFrame(() => {
            scrollToBottom();
            setTimeout(() => {
                forceScrollToBottom();
            }, 50);
        });
    }

    if (recordHistory) {
        addToHistory(content, sender, time);
    }
}

// HTML转义函数（防止XSS攻击）
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 复制消息内容
function copyMessage(messageId) {
    const messageDiv = document.getElementById(messageId);
    const messageText = messageDiv.querySelector('.message-text').textContent;

    navigator.clipboard.writeText(messageText).then(() => {
        // 显示复制成功提示
        showCopySuccess();
    }).catch(err => {
        console.error('复制失败:', err);
        alert('复制失败，请手动选择文本复制');
    });
}

// 显示复制成功提示
function showCopySuccess() {
    const toast = document.createElement('div');
    toast.className = 'toast show position-fixed';
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999;';
    toast.innerHTML = `
        <div class="toast-header">
            <i class="bi bi-check-circle text-success me-2"></i>
            <strong class="me-auto">复制成功</strong>
            <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
        </div>
        <div class="toast-body">
            消息内容已复制到剪贴板
        </div>
    `;

    document.body.appendChild(toast);

    // 3秒后自动移除
    setTimeout(() => {
        if (toast.parentNode) {
            toast.remove();
        }
    }, 3000);
}

// 添加到历史记录
function addToHistory(content, sender, time) {
    chatHistory.push({
        content: content,
        sender: sender,
        time: time,
        chatId: currentChatId
    });
}

// 显示正在输入状态
function showTypingIndicator() {
    const messagesContainer = document.getElementById('chatMessages');
    const typingDiv = document.createElement('div');
    typingDiv.className = 'message ai-message typing-indicator';
    typingDiv.id = 'typingIndicator';
    typingDiv.innerHTML = `
        <div class="message-avatar">
            <i class="bi bi-robot"></i>
        </div>
        <div class="message-content">
            <div class="message-bubble">
                <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            </div>
        </div>
    `;
    messagesContainer.appendChild(typingDiv);

    // 立即滚动
    scrollToBottom();

    // 延迟滚动，确保DOM更新完成
    setTimeout(() => {
        forceScrollToBottom();
    }, 100);
}

// 隐藏正在输入状态
function hideTypingIndicator() {
    const typingIndicator = document.getElementById('typingIndicator');
    if (typingIndicator) {
        typingIndicator.remove();
    }
}

// 滚动到底部
function scrollToBottom() {
    // 获取正确的滚动容器
    const scrollContainer = document.querySelector('.chat-messages-container');
    const messagesContainer = document.getElementById('chatMessages');

    if (scrollContainer && messagesContainer) {
        // 使用平滑滚动
        scrollContainer.scrollTo({
            top: scrollContainer.scrollHeight,
            behavior: 'smooth'
        });

        // 备用方案：直接设置scrollTop
        setTimeout(() => {
            scrollContainer.scrollTop = scrollContainer.scrollHeight;
        }, 100);

        // 调试信息
        console.log('滚动容器高度:', scrollContainer.scrollHeight);
        console.log('当前滚动位置:', scrollContainer.scrollTop);
    }
}

// 强制滚动到底部（用于确保滚动生效，优化版）
function forceScrollToBottom() {
    const scrollContainer = document.querySelector('.chat-messages-container');
    if (!scrollContainer) return;

    // 使用requestAnimationFrame优化性能
    const scroll = () => {
        scrollContainer.scrollTop = scrollContainer.scrollHeight;
    };

    requestAnimationFrame(scroll);
    setTimeout(scroll, 50);
    setTimeout(scroll, 150);
}

// 自动调整输入框高度（添加防抖优化）
let resizeTimeout = null;
function autoResize(textarea) {
    if (!textarea) return;

    // 清除之前的定时器
    if (resizeTimeout) {
        clearTimeout(resizeTimeout);
    }

    // 使用防抖优化性能
    resizeTimeout = setTimeout(() => {
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';

        // 更新字符计数
        updateCharCount();
    }, 10);
}

// 更新字符计数（添加节流优化）
let charCountTimeout = null;
function updateCharCount() {
    // 清除之前的定时器
    if (charCountTimeout) {
        clearTimeout(charCountTimeout);
    }

    // 使用节流优化性能
    charCountTimeout = setTimeout(() => {
        const input = document.getElementById('messageInput');
        const charCount = document.getElementById('charCount');
        if (!input || !charCount) return;

        const length = input.value.length;
        const maxLength = 1000;

        charCount.textContent = `${length}/${maxLength}`;

        if (length > maxLength * 0.9) {
            charCount.style.color = '#dc3545';
        } else if (length > maxLength * 0.7) {
            charCount.style.color = '#ffc107';
        } else {
            charCount.style.color = '#6c757d';
        }
    }, 100);
}

// 清空输入框
function clearInput() {
    const input = document.getElementById('messageInput');
    input.value = '';
    autoResize(input);
    input.focus();
}

// 生成AI回复
function generateAIResponse(userMessage) {
    const responses = {
        '客户满意度分析': `📊 **客户满意度深度分析报告**

根据最新的数据分析，我为您提供以下洞察：

**整体满意度指标：**
• 综合满意度：89.2% ⬆️ (+2.3%)
• 产品满意度：92.1% ⬆️ (+1.8%)
• 服务满意度：85.7% ⬆️ (+3.2%)
• 响应速度：88.9% ⬆️ (+4.1%)

**关键发现：**
1. **服务环节**是主要改进点，建议加强客服培训
2. **响应速度**显著提升，客户反馈积极
3. **VIP客户**满意度达到95.2%，表现优异
4. **新客户**满意度相对较低，需要重点关注

**改进建议：**
• 建立客户满意度实时监控系统
• 定期进行客户回访和调研
• 优化服务流程，提升响应效率
• 加强新客户关怀和引导

需要我为您生成详细的改进方案吗？`,

        '营销策略建议': `🎯 **智能营销策略建议**

基于当前市场环境和客户数据，我为您制定以下营销策略：

**1. 数字化营销升级**
• 建立全渠道营销体系
• 利用AI进行精准客户画像
• 实施个性化营销推送
• 优化线上销售转化率

**2. 客户细分策略**
• 高价值客户：VIP专属服务
• 成长型客户：培育和引导
• 潜在客户：精准触达
• 流失客户：挽回计划

**3. 产品差异化定位**
• 突出技术优势和品质保证
• 强调服务特色和客户价值
• 建立品牌差异化优势
• 制定竞争策略

**4. 客户关系维护**
• 建立客户生命周期管理
• 实施客户成功计划
• 定期客户满意度调研
• 建立客户反馈机制

**预期效果：**
• 客户转化率提升30%
• 客户留存率提升25%
• 营销成本降低20%
• 客户满意度提升15%

您希望我详细分析哪个策略模块？`,

        '产品推荐': `🛍️ **个性化产品推荐方案**

基于您的客户画像和市场分析，我为您推荐以下产品组合：

**🌾 核心产品推荐**
1. **抗病性水稻业务**
   - 适合病虫害多发地区
   - 产量提升15-20%
   - 客户满意度95%+
   - 投资回报率：1:3.2

2. **高效肥料产品**
   - 环保型有机肥料
   - 使用效果提升25%
   - 符合绿色农业趋势
   - 客户复购率85%

3. **智能种植设备**
   - 自动化灌溉系统
   - 精准施肥设备
   - 降低人工成本40%
   - 提升种植效率30%

**📈 市场表现数据**
• 产品A：市场占有率23%，增长率+15%
• 产品B：客户满意度92%，复购率78%
• 产品C：技术领先优势，利润率高

**💡 推荐理由**
• 符合客户种植环境需求
• 具有明显的技术优势
• 市场反馈积极
• 投资回报率高

**🎯 销售建议**
• 优先推荐给大型种植户
• 提供试用和演示服务
• 制定分期付款方案
• 建立技术支持体系

需要我为您制定具体的销售策略吗？`,

        '市场趋势分析': `📈 **业务市场趋势深度分析**

基于大数据分析和行业洞察，当前业务市场呈现以下重要趋势：

**🌱 技术发展趋势**
1. **智能化种植**
   - 物联网技术广泛应用
   - AI辅助决策系统普及
   - 自动化设备需求激增
   - 预计年增长率35%

2. **生物技术突破**
   - 基因编辑技术成熟
   - 抗逆性业务增多
   - 产量和质量双提升
   - 研发投入持续增加

**🌍 市场环境变化**
1. **政策支持力度加大**
   - 农业现代化政策
   - 业务振兴计划
   - 绿色农业发展
   - 补贴政策优化

2. **消费需求升级**
   - 品质要求提高
   - 环保意识增强
   - 个性化需求增长
   - 服务要求提升

**📊 竞争格局分析**
• 头部企业集中度提升
• 技术创新成为核心竞争力
• 服务差异化日益重要
• 品牌影响力显著增强

**🚀 发展机遇**
• 数字化转型窗口期
• 新兴市场拓展机会
• 产业链整合机遇
• 国际合作空间扩大

**⚠️ 挑战与风险**
• 技术更新换代快
• 市场竞争加剧
• 成本压力增大
• 监管要求提高

**💼 战略建议**
1. 加大研发投入，提升技术优势
2. 完善服务体系，增强客户粘性
3. 拓展新兴市场，扩大业务规模
4. 加强品牌建设，提升市场影响力

您希望我深入分析哪个具体领域？`,

        '客户服务优化': `🎧 **客户服务优化升级方案**

基于客户反馈和服务数据分析，我为您制定以下优化方案：

**📞 服务渠道优化**
1. **24小时智能客服**
   - AI机器人自动应答
   - 常见问题即时解决
   - 复杂问题转人工
   - 服务效率提升80%

2. **多渠道服务整合**
   - 电话、微信、邮件统一
   - 在线客服实时响应
   - 移动端服务优化
   - 服务一致性保证

**👥 服务团队建设**
1. **专业培训体系**
   - 产品知识培训
   - 服务技能提升
   - 客户沟通技巧
   - 应急处理能力

2. **绩效考核优化**
   - 客户满意度指标
   - 响应时间要求
   - 问题解决率
   - 客户留存率

**🔄 服务流程优化**
1. **标准化服务流程**
   - 问题分类处理
   - 响应时间标准
   - 升级处理机制
   - 质量监控体系

2. **个性化服务方案**
   - VIP客户专属服务
   - 定制化解决方案
   - 定期回访机制
   - 主动关怀服务

**📊 服务质量监控**
• 实时满意度监控
• 服务数据分析
• 客户反馈收集
• 持续改进机制

**🎯 预期效果**
• 客户满意度提升至95%+
• 服务响应时间缩短50%
• 问题解决率提升至98%
• 客户投诉率降低60%

**💰 投资回报**
• 客户留存率提升25%
• 客户推荐率增加40%
• 服务成本降低30%
• 品牌价值提升20%

需要我为您制定详细的实施计划吗？`,

        '数据分析报告': `📊 **综合数据分析报告**

基于系统数据分析，我为您生成以下综合报告：

**👥 客户数据分析**
• 总客户数：1,247家
• 新增客户：156家（本月）
• 活跃客户：892家（71.5%）
• VIP客户：89家（7.1%）
• 客户增长率：+12.3%

**💰 业务数据分析**
• 总销售额：¥2,847万
• 同比增长：+18.7%
• 平均客单价：¥2.28万
• 客户复购率：76.3%
• 利润率：23.8%

**📈 趋势分析**
1. **客户增长趋势**
   - 月度增长率稳定在10%+
   - 新客户质量持续提升
   - 客户生命周期延长
   - 流失率控制在5%以下

2. **产品销售趋势**
   - 核心产品销量增长25%
   - 新产品市场接受度高
   - 季节性波动明显
   - 区域差异显著

3. **服务效果分析**
   - 客户满意度89.2%
   - 服务响应时间2.3分钟
   - 问题解决率96.8%
   - 客户推荐率78.5%

**🎯 关键洞察**
• 华东地区客户价值最高
• 企业客户贡献70%收入
• 线上渠道增长迅速
• 服务满意度影响复购率

**⚠️ 风险预警**
• 部分产品库存不足
• 竞争对手价格压力
• 客户流失风险增加
• 成本上升压力

**💡 改进建议**
1. 加强高价值客户维护
2. 优化产品库存管理
3. 提升服务响应速度
4. 拓展新兴市场渠道

需要我详细分析某个具体指标吗？`
    };

    // 查找匹配的回复
    for (let key in responses) {
        if (userMessage.includes(key)) {
            return responses[key];
        }
    }

    // 默认回复
    return `🤖 **AI智能助手**

感谢您的问题！作为专业的AI智能助手，我可以为您提供以下服务：

**📊 数据分析服务**
• 客户满意度分析
• 销售业绩分析
• 市场趋势分析
• 业务指标监控

**💡 智能建议服务**
• 营销策略建议
• 产品推荐方案
• 客户服务优化
• 业务流程改进

**🎯 专业咨询服务**
• 业务市场分析
• 技术发展趋势
• 竞争环境分析
• 投资决策支持

**📈 报告生成服务**
• 数据分析报告
• 市场调研报告
• 客户分析报告
• 业务发展报告

请告诉我您具体需要什么帮助，我会为您提供专业、详细的建议和解决方案！`;
}

// 处理键盘事件
function handleKeyPress(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendMessage();
    }
}

// 开始新对话
async function startNewChat() {
    if (!confirm('确定要开始新对话吗？当前对话记录将被清空。')) {
        return;
    }
    chatHistory = [];
    conversationHistory = [];
    await createNewChatSession();
}

// 清空对话
function clearChat() {
    if (confirm('确定要清空当前对话吗？')) {
        document.getElementById('chatMessages').innerHTML = '';
        chatHistory = [];
        conversationHistory = []; // 清空对话历史
        // 重新显示欢迎消息
        initializeChat();
    }
}

// 附件功能
let fileInput = null;

function attachFile() {
    // 创建隐藏的文件输入元素
    if (!fileInput) {
        fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = 'image/*,.pdf,.doc,.docx,.txt,.xlsx,.xls';
        fileInput.style.display = 'none';
        fileInput.onchange = handleFileSelect;
        document.body.appendChild(fileInput);
    }
    fileInput.click();
}

// 处理文件选择
function handleFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;

    // 检查文件大小（限制10MB）
    if (file.size > 10 * 1024 * 1024) {
        alert('文件大小不能超过10MB');
        return;
    }

    // 显示上传中状态
    const loadingId = 'upload_' + Date.now();
    addMessage(`<div id="${loadingId}"><i class="bi bi-cloud-upload me-2"></i>正在上传文件: ${file.name}...</div>`, 'user');

    // 构建FormData
    const formData = new FormData();
    formData.append('file', file);
    // 默认分类为"其他"，后续可以优化为让用户选择
    formData.append('category', 'other');
    formData.append('documentType', 'other');
    formData.append('title', file.name);

    // 上传文件
    fetch('/api/knowledge-doc/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(result => {
            // 移除上传中消息
            const loadingMsg = document.getElementById(loadingId);
            if (loadingMsg && loadingMsg.closest('.message')) {
                loadingMsg.closest('.message').remove();
            }

            if (result.code === 200) {
                const doc = result.data;
                const fileInfo = `[文件] ${doc.title} (${formatFileSize(doc.fileSize)})`;

                // 如果是图片，显示预览
                if (file.type.startsWith('image/')) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        const imageUrl = e.target.result;
                        addFileMessage(fileInfo, imageUrl, doc.title);
                    };
                    reader.readAsDataURL(file);
                } else {
                    addFileMessage(fileInfo, null, doc.title);
                }

                // 自动发送文件信息给AI，触发知识库索引
                setTimeout(() => {
                    sendMessageWithText(`我上传了一个文件：${doc.title}，请根据这个文件回答我的问题。`);
                }, 500);
            } else {
                addMessage(`❌ 文件上传失败: ${result.message}`, 'ai');
            }
        })
        .catch(error => {
            console.error('上传失败:', error);
            // 移除上传中消息
            const loadingMsg = document.getElementById(loadingId);
            if (loadingMsg && loadingMsg.closest('.message')) {
                loadingMsg.closest('.message').remove();
            }
            addMessage(`❌ 文件上传发生错误: ${error.message}`, 'ai');
        });
}

// 格式化文件大小
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// 添加文件消息
function addFileMessage(fileInfo, imageUrl, fileName) {
    const messagesContainer = document.getElementById('chatMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message user-message file-message';

    const time = new Date().toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
    });

    let content = `
        <div class="message-content">
            <div class="message-bubble file-bubble">
                <div class="file-info">
                    <i class="bi bi-file-earmark"></i>
                    <span>${fileInfo}</span>
                </div>
    `;

    if (imageUrl) {
        content += `
                <div class="file-preview">
                    <img src="${imageUrl}" alt="${fileName}" onclick="showImageModal('${imageUrl}')">
                </div>
        `;
    }

    content += `
            </div>
            <div class="message-time">${time}</div>
        </div>
        <div class="message-avatar">
            <i class="bi bi-person-fill"></i>
        </div>
    `;

    messageDiv.innerHTML = content;
    messagesContainer.appendChild(messageDiv);

    // 滚动到底部
    scrollToBottom();

    // 添加到历史记录
    addToHistory(fileInfo, 'user', time);

    // 自动发送文件信息给AI
    setTimeout(() => {
        sendMessageWithText(`我上传了一个文件：${fileInfo}`);
    }, 500);
}

// 发送带文本的消息
function sendMessageWithText(text) {
    const input = document.getElementById('messageInput');
    input.value = text;
    sendMessage();
}

// 显示图片模态框
function showImageModal(imageUrl) {
    const modal = document.createElement('div');
    modal.className = 'image-modal';
    modal.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.9); z-index: 10000; display: flex; align-items: center; justify-content: center; cursor: pointer;';
    modal.onclick = () => modal.remove();

    const img = document.createElement('img');
    img.src = imageUrl;
    img.style.cssText = 'max-width: 90%; max-height: 90%; object-fit: contain;';
    modal.appendChild(img);

    document.body.appendChild(modal);
}



// 测试滚动功能
function testScroll() {
    console.log('开始测试滚动功能...');

    // 添加测试消息
    addMessage('这是一条测试消息，用于验证滚动功能是否正常工作。', 'user');

    setTimeout(() => {
        addMessage('这是AI的测试回复，用于验证滚动功能是否正常工作。', 'ai');
    }, 1000);

    // 强制滚动测试
    setTimeout(() => {
        console.log('执行强制滚动测试...');
        forceScrollToBottom();
    }, 2000);
}

