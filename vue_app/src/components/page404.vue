<template>
    <div class="error-page">
        <div class="error-content">
            <div class="error-illustration">
                <el-icon class="error-icon"><WarningFilled /></el-icon>
            </div>
            
            <h1 class="error-code">404</h1>
            <h2 class="error-title">页面未找到</h2>
            <p class="error-message">
                抱歉，您访问的页面不存在或已被移除。<br>
                请检查网址是否输入正确，或返回首页继续使用。
            </p>
            
            <div class="error-actions">
                <el-button 
                    type="primary" 
                    size="large" 
                    @click="goHome"
                    class="action-btn primary-btn"
                >
                    <el-icon class="btn-icon"><HomeFilled /></el-icon>
                    返回首页
                </el-button>
                <el-button 
                    size="large" 
                    @click="goBack"
                    class="action-btn secondary-btn"
                >
                    <el-icon class="btn-icon"><Back /></el-icon>
                    返回上一页
                </el-button>
            </div>
        </div>
        
        <div class="error-decoration">
            <div class="circle circle-1"></div>
            <div class="circle circle-2"></div>
            <div class="circle circle-3"></div>
        </div>
    </div>
</template>

<script>
import { WarningFilled, HomeFilled, Back } from '@element-plus/icons-vue';

export default {
    name: "Page404",
    components: {
        WarningFilled,
        HomeFilled,
        Back
    },
    methods: {
        goHome() {
            this.$router.push('/');
        },
        goBack() {
            this.$router.go(-1);
        }
    }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;500;600;700&display=swap');
</style>

<style lang="less" scoped>
.error-page {
    min-height: 100vh;
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px;
    font-family: 'Noto Sans', sans-serif;
    position: relative;
    overflow: hidden;
}

.error-content {
    text-align: center;
    z-index: 1;
    max-width: 600px;
}

.error-illustration {
    margin-bottom: 32px;
    display: flex;
    justify-content: center;
}

.error-icon {
    font-size: 120px;
    color: #f59e0b;
    animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
    0%, 100% {
        transform: scale(1);
        opacity: 1;
    }
    50% {
        transform: scale(1.05);
        opacity: 0.9;
    }
}

.error-code {
    font-size: 120px;
    font-weight: 800;
    color: #f59e0b;
    margin: 0 0 16px 0;
    line-height: 1;
    text-shadow: 0 4px 20px rgba(245, 158, 11, 0.3);
}

.error-title {
    font-size: 32px;
    font-weight: 700;
    color: #f8fafc;
    margin: 0 0 16px 0;
}

.error-message {
    font-size: 16px;
    color: #94a3b8;
    line-height: 1.8;
    margin: 0 0 40px 0;
}

.error-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
    flex-wrap: wrap;
}

.action-btn {
    padding: 12px 32px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 10px;
    height: 48px;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.3s ease;
    
    .btn-icon {
        font-size: 18px;
    }
}

.primary-btn {
    background: linear-gradient(135deg, #0891B2 0%, #22C55E 100%);
    border: none;
    color: white;
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(8, 145, 178, 0.4);
    }
}

.secondary-btn {
    background: rgba(255, 255, 255, 0.1);
    border: 2px solid rgba(255, 255, 255, 0.2);
    color: #f8fafc;
    
    &:hover {
        background: rgba(255, 255, 255, 0.15);
        border-color: #0891B2;
        transform: translateY(-2px);
    }
}

.error-decoration {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    overflow: hidden;
}

.circle {
    position: absolute;
    border-radius: 50%;
    opacity: 0.1;
    animation: float 6s ease-in-out infinite;
}

.circle-1 {
    width: 400px;
    height: 400px;
    background: #0891B2;
    top: -200px;
    left: -200px;
    animation-delay: 0s;
}

.circle-2 {
    width: 300px;
    height: 300px;
    background: #22C55E;
    bottom: -150px;
    right: -150px;
    animation-delay: 2s;
}

.circle-3 {
    width: 200px;
    height: 200px;
    background: #f59e0b;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    animation-delay: 4s;
}

@keyframes float {
    0%, 100% {
        transform: translate(0, 0) rotate(0deg);
    }
    33% {
        transform: translate(20px, -20px) rotate(5deg);
    }
    66% {
        transform: translate(-20px, 20px) rotate(-5deg);
    }
}

@media (max-width: 768px) {
    .error-page {
        padding: 16px;
    }
    
    .error-icon {
        font-size: 100px;
    }
    
    .error-code {
        font-size: 80px;
    }
    
    .error-title {
        font-size: 24px;
    }
    
    .error-message {
        font-size: 14px;
        margin-bottom: 32px;
    }
    
    .error-actions {
        flex-direction: column;
        align-items: center;
    }
    
    .action-btn {
        width: 100%;
        max-width: 280px;
        justify-content: center;
    }
}

@media (max-width: 480px) {
    .error-icon {
        font-size: 80px;
    }
    
    .error-code {
        font-size: 64px;
    }
    
    .error-title {
        font-size: 20px;
    }
    
    .error-message {
        font-size: 13px;
    }
    
    .action-btn {
        padding: 10px 24px;
        font-size: 15px;
        height: 44px;
    }
}
</style>