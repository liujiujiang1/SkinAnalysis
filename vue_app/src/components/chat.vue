<template>
    <div class="chat-container">
        <div class="chat-messages" ref="chatMessages">      
            <div 
                v-for="(message, index) in messages" 
                :key="index" 
                class="message-container" 
                :class="{ 'user-message-container': message.sender === 'user', 'bot-message-container': message.sender === 'bot' }"
            >
                <div class="message-avatar" v-if="message.sender === 'bot'">
                    <el-icon><ChatDotRound /></el-icon>
                </div>
                <div class="message" :class="{ 'user-message': message.sender === 'user', 'bot-message': message.sender === 'bot' }">
                    {{ message.content }}
                </div>
            </div>
            <div v-if="loading" class="loading-indicator">
                <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            </div>
        </div>
        
        <div class="input-container">
            <el-input 
                class="chat-input" 
                v-model="newMessage" 
                @keyup.enter="sendMessage" 
                :rows="1" 
                type="textarea"
                :autosize="{ minRows: 1, maxRows: 4 }"
                placeholder="请输入您的问题..."
                resize="none"
                aria-label="输入消息"
            />
            <el-button 
                    type="primary" 
                    @click="sendMessage" 
                    class="send-btn"
                    :disabled="!newMessage.trim() || loading"
                    aria-label="发送消息"
                >
                    <el-icon class="send-icon"><Promotion /></el-icon>
                </el-button>
        </div>
    </div>
</template>
  
<script>
import axios from 'axios';
import { ChatDotRound, Promotion } from '@element-plus/icons-vue';

import {api_key} from '../../zhipu_api-key.js'

export default {
    components: {
        ChatDotRound,
        Promotion
    },
    data() {
        return {
            newMessage: '',
            messages: [{content: '您好，很高兴为您服务！请问您有什么需要帮助的吗？', sender: 'bot'}],
            loading: false
        };
    },
    methods: {
        async sendMessage() {
            if (!this.newMessage.trim()) return;
            
            this.messages.push({ content: this.newMessage, sender: 'user' });
            this.newMessage = '';

            await this.$nextTick();
            this.scrollToBottom();

            this.loading = true;
  
            try {
                const reply = await this.getBotReply(this.messages[this.messages.length - 1].content);
                this.loading = false;
                this.messages.push({ content: '', sender: 'bot' });

                let index = 0;
                const timer = setInterval(() => {
                    if (index < reply.length) {
                        let lastPos = this.messages.length - 1;
                        this.messages[lastPos].content += reply[index];
                        index++;
                    } else {
                        clearInterval(timer);
                    }
                    this.$nextTick();
                    this.scrollToBottom();
                }, 30);
            } catch (error) {
                this.loading = false;
                this.messages.push({ content: '出现问题，请检查智普AI API-Key是否正确！', sender: 'bot' });
                this.$nextTick();
                this.scrollToBottom();
            }
        },
        async getBotReply(message) {
            try {
                const response = await axios.post(
                    'https://open.bigmodel.cn/api/paas/v4/chat/completions',
                    {
                        model: 'glm-4-flash',
                        messages: [
                            {role: 'system', content: '你是一位专业的皮肤科医生，请用专业、友好的口吻回答用户关于皮肤健康的问题。'},
                            {role: 'user', content: message}
                        ]
                    },
                    {
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${api_key}`
                        }
                    }
                );
                return response.data.choices[0].message.content;
            } catch (error) {
                throw error;
            }
        },
        scrollToBottom() {
            if (this.$refs.chatMessages) {
                this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight;
            }
        }
    },
    mounted() {
        this.scrollToBottom();
    }
};
</script>
  
<style>
    @import url('https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;500;600;700&display=swap');
</style>

<style lang="less" scoped>
    .chat-container {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        background: #F8FAFC;
        border-radius: 12px;
        overflow: hidden;
        font-family: 'Noto Sans', sans-serif;
    }

    .chat-messages {
        flex: 1;
        overflow-y: auto;
        padding: 16px;
        display: flex;
        flex-direction: column;
        gap: 12px;
        scroll-behavior: smooth;
    }

    .message-container {
        display: flex;
        gap: 8px;
        align-items: flex-start;
    }

    .user-message-container {
        justify-content: flex-end;
    }

    .bot-message-container {
        justify-content: flex-start;
    }

    .message-avatar {
        width: 36px;
        height: 36px;
        min-width: 36px;
        min-height: 36px;
        border-radius: 50%;
        background: linear-gradient(135deg, #0891B2 0%, #22D3EE 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 18px;
        flex-shrink: 0;
    }

    .message {
        padding: 12px 16px;
        border-radius: 12px;
        display: inline-block;
        max-width: 75%;
        word-wrap: break-word;
        white-space: pre-wrap;
        line-height: 1.5;
        font-size: 14px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    }

    .user-message {
        background: linear-gradient(135deg, #0891B2 0%, #22D3EE 100%);
        color: white;
        border-bottom-right-radius: 4px;
    }

    .bot-message {
        background: white;
        color: #334155;
        border: 1px solid #E2E8F0;
        border-bottom-left-radius: 4px;
    }

    .loading-indicator {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 16px;
        background: white;
        border-radius: 12px;
        border: 1px solid #E2E8F0;
        max-width: 100px;
    }

    .typing-dots {
        display: flex;
        gap: 4px;
    }

    .typing-dots span {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: #0891B2;
        animation: typing-bounce 1.4s infinite ease-in-out;
    }

    .typing-dots span:nth-child(1) {
        animation-delay: 0s;
    }

    .typing-dots span:nth-child(2) {
        animation-delay: 0.2s;
    }

    .typing-dots span:nth-child(3) {
        animation-delay: 0.4s;
    }

    @keyframes typing-bounce {
        0%, 60%, 100% {
            transform: translateY(0);
        }
        30% {
            transform: translateY(-8px);
        }
    }

    .input-container {
        display: flex;
        align-items: flex-end;
        gap: 12px;
        padding: 16px;
        background: white;
        border-top: 1px solid #E2E8F0;
    }

    .chat-input {
        flex: 1;
        font-size: 14px;
        line-height: 1.5;
    }

    .chat-input :deep(.el-textarea__inner) {
        border-radius: 24px;
        padding: 12px 16px;
        border: 2px solid #E2E8F0;
        resize: none;
        transition: all 0.2s ease;
    }

    .chat-input :deep(.el-textarea__inner):focus {
        border-color: #0891B2;
        box-shadow: 0 0 0 3px rgba(8, 145, 178, 0.1);
    }

    .send-btn {
        width: 48px;
        height: 48px;
        min-width: 48px;
        min-height: 48px;
        border-radius: 50%;
        padding: 0;
        background: linear-gradient(135deg, #0891B2 0%, #22D3EE 100%);
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.2s ease;
        flex-shrink: 0;
    }

    .send-btn:hover:not(:disabled) {
        transform: scale(1.05);
        box-shadow: 0 4px 12px rgba(8, 145, 178, 0.3);
    }

    .send-btn:active:not(:disabled) {
        transform: scale(0.95);
    }

    .send-btn:disabled {
        background: #E2E8F0;
        cursor: not-allowed;
    }

    .send-icon {
        font-size: 20px;
        color: white;
    }

    .send-btn:disabled .send-icon {
        color: #94A3B8;
    }

    @media (min-width: 768px) {
        .chat-messages {
            padding: 24px;
            gap: 16px;
        }

        .message-avatar {
            width: 40px;
            height: 40px;
            min-width: 40px;
            min-height: 40px;
            font-size: 20px;
        }

        .message {
            padding: 14px 18px;
            font-size: 15px;
            max-width: 70%;
        }

        .input-container {
            padding: 20px;
            gap: 16px;
        }

        .chat-input :deep(.el-textarea__inner) {
            font-size: 15px;
            padding: 14px 18px;
        }

        .send-btn {
            width: 52px;
            height: 52px;
            min-width: 52px;
            min-height: 52px;
        }

        .send-icon {
            font-size: 22px;
        }
    }

    @media (max-width: 480px) {
        .chat-messages {
            padding: 12px;
            gap: 10px;
        }

        .message-avatar {
            width: 32px;
            height: 32px;
            min-width: 32px;
            min-height: 32px;
            font-size: 16px;
        }

        .message {
            padding: 10px 14px;
            font-size: 13px;
            max-width: 80%;
        }

        .input-container {
            padding: 12px;
            gap: 10px;
        }

        .chat-input :deep(.el-textarea__inner) {
            font-size: 13px;
            padding: 10px 14px;
        }

        .send-btn {
            width: 44px;
            height: 44px;
            min-width: 44px;
            min-height: 44px;
        }

        .send-icon {
            font-size: 18px;
        }

        .typing-dots span {
            width: 6px;
            height: 6px;
        }
    }

    @media (prefers-reduced-motion: reduce) {
        *,
        *::before,
        *::after {
            animation-duration: 0.01ms !important;
            animation-iteration-count: 1 !important;
            transition-duration: 0.01ms !important;
        }
    }

    ::-webkit-scrollbar {
        width: 6px;
    }

    ::-webkit-scrollbar-track {
        background: #F1F5F9;
    }

    ::-webkit-scrollbar-thumb {
        background: #CBD5E1;
        border-radius: 3px;
    }

    ::-webkit-scrollbar-thumb:hover {
        background: #94A3B8;
    }
</style>
