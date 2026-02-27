<template>
    <div class="layout">
        <el-container class="layout-container">
            <el-aside class="aside-container">
                <div class="aside-header">
                    <div class="logo-wrapper">
                        <img class="logo" src="../assets/logo.png" alt="Logo"/>
                    </div>
                    <p class="aside-title">管理后台</p>
                </div>
                
                <el-menu 
                    default-active="0" 
                    @select="handleSelect"
                    class="side-menu"
                >
                    <el-menu-item index="0">
                        <el-icon><User /></el-icon>
                        <span class="menu-text">用户管理</span>
                    </el-menu-item>
                    <el-menu-item index="1">
                        <el-icon><DataAnalysis /></el-icon>
                        <span class="menu-text">数据统计</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            
            <el-container class="main-container">
                <el-header class="main-header">
                    <div class="header-left">
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
                                :icon="SwitchButton"
                                circle
                            />
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
    </div>
</template>

<script lang="ts">
import { ElMessageBox } from "element-plus";
import { User, DataAnalysis, SwitchButton } from '@element-plus/icons-vue';

export default {
    name: "backend",
    components: {
        User,
        DataAnalysis,
        SwitchButton
    },
    data() {
        return {
            choose: 0,
            adminname: sessionStorage.getItem('admin_name') || '管理员'
        };
    },
    methods: {
        handleSelect(index){
            switch (index) {
                case "0":   this.$router.push("/admin/background/manage");
                break;
                case "1":   this.$router.push("/admin/background/statistics");
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
                .then(() => {
                    sessionStorage.setItem('admin_name', '')
                    sessionStorage.setItem('admin_authenticated', 'false')
                    this.$router.push("/admin/login")
                })
                .catch(() => {
                })
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
</style>