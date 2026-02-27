<template>
    <div class="layout">
        <el-container class="layout-container">
            <el-aside class="aside-container" :class="{ 'mobile-hidden': isMobile && !showMobileMenu }">
                <el-row class="tac">
                    <el-col>
                        <el-menu :default-active="activeIndex" @select="handleSelect" class="side-menu">
                            <div class="aside-top">
                                <p class="menu-title">菜单</p>
                            </div>
                            <el-menu-item index="1" class="menu-item">
                                <el-icon class="menu-icon"><Search /></el-icon>
                                <span class="menu-text">图像诊断</span>
                            </el-menu-item>
                            <el-menu-item index="0" class="menu-item">
                                <el-icon class="menu-icon"><ChatDotRound /></el-icon>
                                <span class="menu-text">在线问答</span>
                            </el-menu-item>
                        </el-menu>
                    </el-col>
                </el-row>
            </el-aside>

            <el-container class="main-container">
                <el-header class="main-header">
                    <div class="header-left">
                        <el-button 
                            class="menu-toggle" 
                            @click="toggleMobileMenu" 
                            v-show="isMobile"
                            circle
                            size="large"
                            aria-label="切换菜单"
                        >
                            <el-icon><Menu /></el-icon>
                        </el-button>
                        <div class="logo-box">
                            <img class="logo" src="../assets/logo.png" alt="Logo"/>
                        </div>
                        <div class="slogan-box">
                            Intelligent Medicine
                        </div>
                    </div>
                    <div class="header-right">
                        <div class="close-box" @click="exitLogin">
                            <span class="username">用户：{{ username }}</span>
                            <el-tooltip effect="dark" content="退出登录">
                                <el-button 
                                    class="logout-btn" 
                                    circle 
                                    size="large"
                                    aria-label="退出登录"
                                >
                                    <el-icon><SwitchButton /></el-icon>
                                </el-button>
                            </el-tooltip>
                        </div>
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

        <div class="mobile-overlay" :class="{ 'show': isMobile && showMobileMenu }" @click="toggleMobileMenu"></div>

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
import { Menu, Search, ChatDotRound, SwitchButton, Close, WarningFilled } from '@element-plus/icons-vue';

export default {
    components: {
        Menu,
        Search,
        ChatDotRound,
        SwitchButton,
        Close,
        WarningFilled
    },
    name: "backend",
    data() {
        return {
            username: sessionStorage.getItem('user_name'),
            isMobile: false,
            showMobileMenu: false,
            activeIndex: '1',
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
            this.isMobile = window.innerWidth < 768;
            if (!this.isMobile) {
                this.showMobileMenu = false;
            }
        },
        toggleMobileMenu() {
            this.showMobileMenu = !this.showMobileMenu;
        },
        handleSelect(index){
            this.activeIndex = index;
            this.showMobileMenu = false;
            switch (index) {
                case "0":   this.$router.push("/user/QAChat");
                break;
                case "1":   this.$router.push("/user/analyse");
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
            sessionStorage.setItem('user_name', '');
            sessionStorage.setItem('user_authenticated', 'false');
            this.$router.push("/");
        },
    }
};
</script>

<style>
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
        width: 100vw;
        height: 100vh;
        display: flex;
    }

    .aside-container {
        width: 200px;
        background: white;
        border-right: 1px solid #E2E8F0;
        transition: transform 0.3s ease;
        z-index: 1000;
    }

    .tac {
        height: 100%;
    }

    .side-menu {
        border: none;
        background: white;
    }

    .aside-top {
        padding: 24px 20px;
        border-bottom: 1px solid #E2E8F0;
    }

    .menu-title {
        font-size: 18px;
        font-weight: 700;
        color: #134E4A;
        margin: 0;
        text-align: center;
    }

    .menu-item {
        padding: 16px 20px;
        margin: 4px 12px;
        border-radius: 12px;
        transition: all 0.2s ease;
        cursor: pointer;
        color: #64748B;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 12px;
    }

    .menu-item:hover {
        background: #F0FDFA;
        color: #0891B2;
    }

    .menu-item.is-active {
        background: linear-gradient(135deg, #0891B2 0%, #22D3EE 100%);
        color: white;
    }

    .menu-icon {
        font-size: 20px;
        flex-shrink: 0;
    }

    .menu-text {
        font-size: 15px;
    }

    .main-container {
        flex: 1;
        display: flex;
        flex-direction: column;
        background: #F8FAFC;
        overflow: hidden;
    }

    .main-header {
        padding: 0 24px;
        height: 64px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        background: white;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        z-index: 100;
    }

    .header-left {
        display: flex;
        align-items: center;
        gap: 16px;
        flex: 1;
    }

    .header-right {
        display: flex;
        align-items: center;
    }

    .menu-toggle {
        background: #0891B2;
        border: 1.5px solid #0891B2;
        color: white;
        cursor: pointer;
        transition: all 0.2s ease;
        width: 44px;
        height: 44px;
        min-width: 44px;
        min-height: 44px;
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 1001;
        box-shadow: 0 2px 8px rgba(8, 145, 178, 0.3);
    }

    .menu-toggle:hover {
        background: #0e7490;
        border-color: #0e7490;
        transform: scale(1.05);
    }

    .logo-box {
        height: 40px;
        display: flex;
        align-items: center;
    }

    .logo {
        height: 40px;
        width: 40px;
        object-fit: contain;
    }

    .slogan-box {
        font-size: 18px;
        font-weight: 600;
        color: #0891B2;
        white-space: nowrap;
    }

    .close-box {
        display: flex;
        align-items: center;
        gap: 12px;
        cursor: pointer;
        transition: all 0.2s ease;
        padding: 8px 12px;
        border-radius: 12px;
    }

    .close-box:hover {
        background: #F8FAFC;
    }

    .username {
        font-size: 14px;
        font-weight: 500;
        color: #64748B;
        display: none;
    }

    .logout-btn {
        background: #FEF2F2;
        border: 1.5px solid #FECACA;
        color: #DC2626;
        cursor: pointer;
        transition: all 0.2s ease;
        width: 44px;
        height: 44px;
        min-width: 44px;
        min-height: 44px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .logout-btn:hover {
        background: #FEE2E2;
        border-color: #DC2626;
    }

    .main-content {
        flex: 1;
        overflow-y: auto;
        padding: 24px;
    }

    .mobile-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        z-index: 998;
        opacity: 0;
        visibility: hidden;
        transition: all 0.3s ease;
    }

    .mobile-overlay.show {
        opacity: 1;
        visibility: visible;
    }

    .fade-enter-active,
    .fade-leave-active {
        transition: opacity 0.3s ease;
    }

    .fade-enter-from,
    .fade-leave-to {
        opacity: 0;
    }

    @media (min-width: 768px) {
        .aside-container {
            width: 220px;
        }

        .menu-item {
            padding: 18px 24px;
        }

        .main-header {
            padding: 0 32px;
            height: 72px;
        }

        .slogan-box {
            font-size: 22px;
        }

        .username {
            display: block;
        }

        .main-content {
            padding: 32px;
        }
    }

    @media (min-width: 1024px) {
        .aside-container {
            width: 240px;
        }

        .main-header {
            padding: 0 40px;
            height: 80px;
        }

        .slogan-box {
            font-size: 24px;
        }
    }

    @media (max-width: 767px) {
        .aside-container {
            position: fixed;
            top: 64px;
            left: 0;
            bottom: 0;
            width: 280px;
            max-width: 85vw;
            transform: translateX(-100%);
            box-shadow: 4px 0 16px rgba(0, 0, 0, 0.1);
            z-index: 999;
        }

        .aside-container:not(.mobile-hidden) {
            transform: translateX(0);
        }

        .menu-item {
            padding: 16px 20px;
            margin: 4px 16px;
        }

        .main-header {
            padding: 0 16px;
            height: 64px;
        }

        .logo-box {
            height: 36px;
        }

        .logo {
            height: 36px;
            width: 36px;
        }

        .slogan-box {
            font-size: 14px;
            display: none;
        }

        .main-content {
            padding: 16px;
        }

        .close-box {
            padding: 8px;
        }
    }

    @media (max-width: 480px) {
        .main-header {
            padding: 0 12px;
            height: 60px;
        }

        .logo-box {
            height: 32px;
        }

        .logo {
            height: 32px;
            width: 32px;
        }

        .menu-toggle {
            width: 40px;
            height: 40px;
            min-width: 40px;
            min-height: 40px;
        }

        .logout-btn {
            width: 40px;
            height: 40px;
            min-width: 40px;
            min-height: 40px;
        }

        .main-content {
            padding: 12px;
        }

        .aside-container {
            top: 60px;
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
</style>
