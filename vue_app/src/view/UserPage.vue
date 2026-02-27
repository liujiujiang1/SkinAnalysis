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
    </div>
</template>

<script lang="ts">
import {ElMessageBox} from "element-plus";
import { Menu, Search, ChatDotRound, SwitchButton } from '@element-plus/icons-vue';

export default {
    components: {
        Menu,
        Search,
        ChatDotRound,
        SwitchButton
    },
    name: "backend",
    data() {
        return {
            username: sessionStorage.getItem('user_name'),
            isMobile: false,
            showMobileMenu: false,
            activeIndex: '1'
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
            ElMessageBox.confirm("确定要退出登录吗？",{
                title: "提示",
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(key=>{
                    sessionStorage.setItem('user_name', '')
                    sessionStorage.setItem('user_authenticated', 'false')
                    this.$router.push("/")
                })
                .catch((action)=>{

                })
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
            transform: translateX(0);
            box-shadow: 4px 0 16px rgba(0, 0, 0, 0.1);
        }

        .aside-container.mobile-hidden {
            transform: translateX(-100%);
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
</style>
