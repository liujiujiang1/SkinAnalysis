<template>
    <div class="layout">
        <el-container class="layout-container">
            <el-aside class="aside-container" :class="{ 'is-collapsed': isCollapsed, 'is-mobile': isMobile, 'show-mobile-menu': showMobileMenu }">
                <div class="aside-header">
                    <div class="logo-wrapper">
                        <img class="logo" src="../assets/logo.png" alt="Logo"/>
                    </div>
                    <p class="aside-title" v-show="!isCollapsed">管理后台</p>
                </div>
                
                <el-menu 
                    default-active="0" 
                    @select="handleSelect"
                    class="side-menu"
                >
                    <el-menu-item index="0">
                        <template #icon>
                            <el-icon><User /></el-icon>
                        </template>
                        <span class="menu-text" v-show="!isCollapsed">用户管理</span>
                    </el-menu-item>
                    <el-menu-item index="1">
                        <template #icon>
                            <el-icon><DataAnalysis /></el-icon>
                        </template>
                        <span class="menu-text" v-show="!isCollapsed">数据统计</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            
            <div class="overlay" :class="{ 'show-overlay': isMobile && showMobileMenu }" @click="closeMobileMenu"></div>
            
            <el-container class="main-container">
                <el-header class="main-header">
                    <div class="header-left">
                        <el-button 
                            class="menu-toggle" 
                            @click="toggleMenu"
                            circle
                            v-show="isMobile"
                        >
                            <el-icon><Fold /></el-icon>
                        </el-button>
                        <div class="brand-info">
                            <h1 class="brand-name">Intelligent Medicine</h1>
                            <p class="brand-tagline">智能医疗管理系统</p>
                        </div>
                    </div>
                    
                    <div class="header-right">
                        <div class="admin-info">
                            <el-icon class="admin-icon"><User /></el-icon>
                            <span class="admin-name">{{ adminname }}</span>
                        </div>
                        <el-tooltip effect="dark" content="退出登录" placement="bottom">
                            <el-button 
                                class="logout-btn" 
                                @click="exitLogin"
                                circle
                            >
                                <el-icon><SwitchButton /></el-icon>
                            </el-button>
                        </el-tooltip>
                    </div>
                </el-header>
                
                <el-main class="main-content">
                    <router-view v-slot="{Component}">
                        <transition name="fade" mode="out-in" appear>
                            <component :is="Component" />
                        </transition>
                    </router-view>
                </el-main>
            </el-container>
        </el-container>

        <!-- 退出登录确认弹窗 -->
        <div v-if="showLogoutModal" class="logout-modal-overlay" @click="closeLogoutModal">
            <div class="logout-modal" @click.stop>
                <div class="logout-modal-header">
                    <h3 class="logout-modal-title">确认退出</h3>
                    <button class="logout-modal-close" @click="closeLogoutModal" aria-label="关闭">
                        <el-icon><Close /></el-icon>
                    </button>
                </div>
                <div class="logout-modal-body">
                    <div class="logout-modal-icon">
                        <el-icon><WarningFilled /></el-icon>
                    </div>
                    <p class="logout-modal-message">确定要退出登录吗？</p>
                </div>
                <div class="logout-modal-footer">
                    <button class="logout-modal-btn cancel-btn" @click="closeLogoutModal">取消</button>
                    <button class="logout-modal-btn confirm-btn" @click="confirmLogout">确定</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { User, DataAnalysis, SwitchButton, Fold, Close, WarningFilled } from '@element-plus/icons-vue';

export default {
    name: "backend",
    components: {
        User,
        DataAnalysis,
        SwitchButton,
        Fold,
        Close,
        WarningFilled
    },
    data() {
        return {
            choose: 0,
            adminname: sessionStorage.getItem('admin_name') || '管理员',
            isMobile: false,
            isCollapsed: false,
            showMobileMenu: false,
            showLogoutModal: false
        };
    },
    mounted() {
        this.checkMobile();
        window.addEventListener('resize', this.checkMobile);
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.checkMobile);
    },
    methods: {
        checkMobile() {
            this.isMobile = window.innerWidth <= 768;
            this.isCollapsed = window.innerWidth <= 768;
        },
        toggleMenu() {
            this.showMobileMenu = !this.showMobileMenu;
        },
        closeMobileMenu() {
            this.showMobileMenu = false;
        },
        handleSelect(index){
            this.closeMobileMenu();
            switch (index) {
                case "0":   this.$router.push("/admin/background/manage");
                break;
                case "1":   this.$router.push("/admin/background/statistics");
                break;
            }
        },
        exitLogin(){
            this.showLogoutModal = true;
        },
        closeLogoutModal(){
            this.showLogoutModal = false;
        },
        confirmLogout(){
            sessionStorage.setItem('admin_name', '');
            sessionStorage.setItem('admin_authenticated', 'false');
            this.$router.push("/admin/login");
        }
    }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;500;600;700&display=swap');
</style>

<style lang="less" scoped>
.layout {
    width: 100vw;
    height: 100vh;
    overflow: hidden;
    font-family: 'Noto Sans', sans-serif;
}

.layout-container {
    width: 100%;
    height: 100%;
}

.aside-container {
    width: 240px;
    background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
    border-right: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    flex-direction: column;
}

.aside-header {
    padding: 24px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    text-align: center;
}

.logo-wrapper {
    display: flex;
    justify-content: center;
    margin-bottom: 12px;
}

.logo {
    width: 48px;
    height: 48px;
}

.aside-title {
    font-size: 16px;
    font-weight: 600;
    color: #f8fafc;
    margin: 0;
    letter-spacing: 0.5px;
}

.side-menu {
    flex: 1;
    border: none;
    background: transparent;
    padding: 16px 0;

    :deep(.el-menu-item) {
        height: 52px;
        line-height: 52px;
        margin: 0 12px;
        border-radius: 8px;
        color: #94a3b8;
        transition: all 0.3s ease;
        
        &:hover {
            background: rgba(34, 197, 94, 0.1);
            color: #22C55E;
        }
        
        &.is-active {
            background: linear-gradient(135deg, #22C55E 0%, #16a34a 100%);
            color: white;
            
            .el-icon {
                color: white;
            }
        }
        
        .el-icon {
            font-size: 20px;
            margin-right: 12px;
        }
    }
}

.menu-text {
    font-size: 15px;
    font-weight: 500;
}

.main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: #f8fafc;
    overflow: hidden;
}

.main-header {
    height: 72px;
    padding: 0 32px;
    background: white;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left {
    display: flex;
    align-items: center;
}

.brand-info {
    display: flex;
    flex-direction: column;
}

.brand-name {
    font-size: 20px;
    font-weight: 700;
    color: #0891B2;
    margin: 0;
    line-height: 1.2;
}

.brand-tagline {
    font-size: 12px;
    color: #94a3b8;
    margin: 2px 0 0 0;
    font-weight: 500;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 16px;
}

.admin-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    background: #f1f5f9;
    border-radius: 24px;
}

.admin-icon {
    font-size: 18px;
    color: #0891B2;
}

.admin-name {
    font-size: 14px;
    font-weight: 600;
    color: #475569;
}

.logout-btn {
    width: 40px;
    height: 40px;
    background: #fee2e2;
    border: none;
    color: #dc2626;
    
    &:hover {
        background: #dc2626;
        color: white;
        transform: rotate(180deg);
        transition: all 0.3s ease;
    }
}

.main-content {
    flex: 1;
    padding: 24px;
    overflow-y: auto;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
    transform: translateY(10px);
}

@media (max-width: 1024px) {
    .aside-container {
        width: 200px;
    }
    
    .main-header {
        padding: 0 24px;
        height: 64px;
    }
    
    .brand-name {
        font-size: 18px;
    }
}

@media (max-width: 768px) {
    .aside-container {
        width: 64px;
        
        .aside-header {
            padding: 16px 8px;
        }
        
        .logo {
            width: 36px;
            height: 36px;
        }
        
        .aside-title {
            display: none;
        }
        
        .menu-text {
            display: none;
        }
        
        :deep(.el-menu-item) {
            margin: 0 8px;
            padding: 0;
            justify-content: center;
            
            .el-icon {
                margin-right: 0;
            }
        }
    }
    
    .main-header {
        padding: 0 16px;
        height: 60px;
    }
    
    .brand-tagline {
        display: none;
    }
    
    .admin-info {
        padding: 6px 12px;
        
        .admin-name {
            font-size: 13px;
        }
    }
    
    .logout-btn {
        width: 36px;
        height: 36px;
    }
    
    .main-content {
        padding: 16px;
    }
}

@media (max-width: 480px) {
    .aside-container {
        width: 56px;
    }
    
    .main-header {
        padding: 0 12px;
        height: 56px;
    }
    
    .brand-name {
        font-size: 16px;
    }
    
    .admin-name {
        display: none;
    }
    
    .main-content {
        padding: 12px;
    }
}

.overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    z-index: 1000;
    display: none;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.overlay.show-overlay {
    display: block;
    opacity: 1;
}

@media (max-width: 768px) {
    .aside-container.is-mobile {
        position: fixed;
        left: -280px;
        top: 0;
        height: 100%;
        z-index: 1001;
        width: 280px !important;
        transition: left 0.3s ease;
        box-shadow: 4px 0 12px rgba(0, 0, 0, 0.15);
    }
    
    .aside-container.is-mobile.show-mobile-menu {
        left: 0;
    }
    
    .aside-container.is-mobile .aside-title {
        display: block;
    }
    
    .aside-container.is-mobile.show-mobile-menu .menu-text {
        display: block !important;
    }
    
    .aside-container.is-mobile :deep(.el-menu-item) {
        margin: 0 16px;
        padding: 0 12px;
        justify-content: flex-start;
        
        .el-icon {
            margin-right: 12px;
        }
    }
    
    .menu-toggle {
        width: 40px;
        height: 40px;
        background: #f1f5f9;
        border: none;
        color: #0891B2;
        margin-right: 12px;
    }

    :deep(.message-box-center) {
        top: 50% !important;
        transform: translate(-50%, -50%) !important;
        margin-top: 0 !important;
    }

    /* 退出登录确认弹窗样式 */
    .logout-modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 2000;
        backdrop-filter: blur(4px);
    }

    .logout-modal {
        background: rgba(255, 255, 255, 0.9);
        backdrop-filter: blur(20px);
        border-radius: 16px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        width: 90%;
        max-width: 400px;
        overflow: hidden;
        transition: all 0.3s ease;
    }

    .logout-modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 24px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    }

    .logout-modal-title {
        font-size: 18px;
        font-weight: 600;
        color: #1E293B;
        margin: 0;
    }

    .logout-modal-close {
        background: none;
        border: none;
        font-size: 20px;
        color: #64748B;
        cursor: pointer;
        padding: 4px;
        border-radius: 8px;
        transition: all 0.2s ease;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .logout-modal-close:hover {
        background: rgba(0, 0, 0, 0.05);
        color: #1E293B;
    }

    .logout-modal-body {
        padding: 32px 24px;
        text-align: center;
    }

    .logout-modal-icon {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        background: rgba(249, 115, 22, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 20px;
    }

    .logout-modal-icon .el-icon {
        font-size: 32px;
        color: #F97316;
    }

    .logout-modal-message {
        font-size: 16px;
        font-weight: 500;
        color: #1E293B;
        margin: 0;
        line-height: 1.5;
    }

    .logout-modal-footer {
        display: flex;
        gap: 12px;
        padding: 0 24px 24px;
    }

    .logout-modal-btn {
        flex: 1;
        height: 48px;
        border: none;
        border-radius: 12px;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
        font-family: 'Noto Sans', sans-serif;
    }

    .logout-modal-btn.cancel-btn {
        background: #F1F5F9;
        color: #475569;
        border: 1px solid #E2E8F0;
    }

    .logout-modal-btn.cancel-btn:hover {
        background: #E2E8F0;
    }

    .logout-modal-btn.confirm-btn {
        background: #2563EB;
        color: white;
    }

    .logout-modal-btn.confirm-btn:hover {
        background: #1D4ED8;
        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
    }

    @media (max-width: 480px) {
        .logout-modal {
            width: 95%;
            max-width: 320px;
        }

        .logout-modal-header {
            padding: 16px 20px;
        }

        .logout-modal-body {
            padding: 24px 20px;
        }

        .logout-modal-footer {
            padding: 0 20px 20px;
        }

        .logout-modal-btn {
            height: 44px;
            font-size: 15px;
        }

        .logout-modal-icon {
            width: 56px;
            height: 56px;
        }

        .logout-modal-icon .el-icon {
            font-size: 28px;
        }

        .logout-modal-message {
            font-size: 15px;
        }
    }
}
</style>