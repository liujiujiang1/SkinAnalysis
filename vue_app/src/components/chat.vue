<template>
    <div class="chat-container">
        <div class="chat-messages" ref="chatMessages">      
            <div v-if="safetyWarning" class="safety-warning">
                检测到可能的高危症状描述。如存在快速增大、出血、破溃、明显疼痛或颜色异常，请尽快线下就医。
            </div>
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
        
        <div class="question-chips" v-if="questionChips.length">
            <button v-for="question in questionChips" :key="question" @click="sendSuggestion(question)">
                {{ question }}
            </button>
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
import { diseaseName } from '../data/diseaseKnowledge'
import { createDiseaseMap, fetchDiseaseKnowledge } from '../utils/diseaseKnowledgeService'
import { hasUrgentSymptoms, recommendedQuestions } from '../utils/risk'

export default {
    components: {
        ChatDotRound,
        Promotion
    },
    data() {
        return {
            newMessage: '',
            messages: [{content: '您好，很高兴为您服务！请问您有什么需要帮助的吗？', sender: 'bot'}],
            loading: false,
            latestDiagnosis: null,
            knowledgeMap: {},
            safetyWarning: false
        };
    },
    computed: {
        questionChips() {
            return recommendedQuestions(this.latestDiagnosis)
        }
    },
    methods: {
        async sendMessage() {
            if (!this.newMessage.trim()) return;
            const content = this.newMessage.trim()
            this.safetyWarning = this.safetyWarning || hasUrgentSymptoms(content)
            this.messages.push({ content, sender: 'user' });
            this.saveMessage(content, 'user', this.safetyWarning ? 'urgent_symptom' : '')
            this.newMessage = '';

            await this.$nextTick();
            this.scrollToBottom();

            this.loading = true;
  
            try {
                const reply = this.normalizeBotReply(await this.getBotReply(this.messages[this.messages.length - 1].content));
                this.loading = false;
                this.messages.push({ content: '', sender: 'bot' });
                this.saveMessage(reply, 'bot', hasUrgentSymptoms(reply) ? 'urgent_symptom' : '')

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
                const fallback = '出现问题，请检查智普AI API-Key是否正确！'
                this.messages.push({ content: fallback, sender: 'bot' });
                this.saveMessage(fallback, 'bot', '')
                this.$nextTick();
                this.scrollToBottom();
            }
        },
        sendSuggestion(question) {
            if (this.loading) return
            this.newMessage = question
            this.sendMessage()
        },
        normalizeBotReply(reply) {
            let content = reply || ''
            const unsafePatterns = ['确诊为', '一定是', '无需就医', '不用就医', '肯定不是恶性']
            const hasUnsafeTone = unsafePatterns.some((pattern) => content.includes(pattern))
            if (hasUnsafeTone) {
                content += '\n\n安全提示：线上问答不能给出最终诊断。请结合医生面诊、皮肤镜或病理检查判断，若症状持续或加重请及时就医。'
            } else if (!content.includes('不能替代医生') && !content.includes('不能替代面诊')) {
                content += '\n\n提示：以上内容仅供健康管理参考，不能替代医生面诊、皮肤镜或病理检查。'
            }
            return content
        },
        async getBotReply(message) {
            try {
                const context = this.buildDiagnosisContext()
                const response = await axios.post(
                    'https://open.bigmodel.cn/api/paas/v4/chat/completions',
                    {
                        model: 'glm-4-flash',
                        messages: [
                            {role: 'system', content: '你是皮肤健康问答助手。请用专业、友好的口吻解释皮肤健康知识，但不能给出最终诊断、不能替代医生面诊、皮肤镜或病理检查。遇到快速增大、出血、破溃、明显疼痛、颜色明显变化、疑似黑色素瘤或基底细胞癌等情况，要明确建议尽快线下就医。'},
                            {role: 'system', content: context},
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
        },
        async loadLatestDiagnosis() {
            const username = sessionStorage.getItem('user_name')
            if (!username) return
            const response = await this.axios.get(`/spring_api/record/user/${username}`)
            this.latestDiagnosis = (response.data || [])[0] || null
        },
        async loadChatHistory() {
            const username = sessionStorage.getItem('user_name')
            if (!username) return
            const response = await this.axios.get(`/spring_api/chat-message/user/${username}`)
            const savedMessages = response.data || []
            if (savedMessages.length) {
                this.messages = savedMessages.map((item) => ({
                    content: item.content,
                    sender: item.sender
                }))
                this.safetyWarning = savedMessages.some((item) => (item.safetyTags || '').includes('urgent_symptom'))
            }
        },
        async saveMessage(content, sender, safetyTags) {
            const username = sessionStorage.getItem('user_name')
            if (!username || !content) return
            await this.axios.post('/spring_api/chat-message', {
                username,
                sender,
                content,
                safetyTags
            })
        },
        async loadDiseaseKnowledge() {
            this.knowledgeMap = createDiseaseMap(await fetchDiseaseKnowledge())
        },
        buildDiagnosisContext() {
            if (!this.latestDiagnosis) {
                return '当前用户暂无最近诊断记录。'
            }
            const code = this.latestDiagnosis.disease
            const knowledge = this.knowledgeMap[code]
            const probability = Number(this.latestDiagnosis.probability || 0).toFixed(2)
            const advice = knowledge ? knowledge.advice.join('；') : ''
            return `用户最近一次诊断结果：${diseaseName(code)}（${code}），概率 ${probability}%。疾病背景：${knowledge?.intro || ''} 建议重点：${advice}`
        }
    },
    mounted() {
        this.loadDiseaseKnowledge();
        this.loadLatestDiagnosis();
        this.loadChatHistory();
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

    .safety-warning {
        padding: 12px 14px;
        border-radius: 10px;
        background: #FEF2F2;
        border: 1px solid #FCA5A5;
        color: #991B1B;
        line-height: 1.6;
        font-size: 13px;
    }

    .question-chips {
        display: flex;
        gap: 8px;
        padding: 10px 16px 0;
        overflow-x: auto;
        background: white;
        border-top: 1px solid #E2E8F0;
    }

    .question-chips button {
        flex: 0 0 auto;
        border: 1px solid #99F6E4;
        background: #F0FDFA;
        color: #0F766E;
        border-radius: 999px;
        padding: 7px 12px;
        cursor: pointer;
        font-size: 13px;
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

        .question-chips {
            padding: 8px 12px 0;
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
