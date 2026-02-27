<template>
    <div class="container">
        <el-alert title="用户不存在" type="warning" effect="dark" center show-icon v-show="existCheck"/>
        <el-alert title="用户名或密码错误" type="error" effect="dark" center show-icon v-show="errorCheck"/>
        <el-alert title="登录成功" type="success" effect="dark" center show-icon v-show="passCheck"/>
        <div class="content">
            <div class="login-card">
                <div class="card-header">
                    <div class="logo-icon">
                        <el-icon><setting /></el-icon>
                    </div>
                    <h1 class="login-title">管理后台</h1>
                    <p class="login-subtitle">管理员登录</p>
                </div>
                <el-form :rules="rules" :model="form" ref="ruleFormRef" v-loading="loading" class="login-form" @submit.prevent>
                    <el-form-item prop="username">
                        <el-input 
                            class="username"
                            v-model="form.username"
                            placeholder="请输入管理员用户名"
                            prefix-icon="User"
                            size="large"
                            clearable
                            aria-label="用户名输入框"
                        >
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input 
                            class="password"
                            type="password"
                            v-model="form.password"
                            placeholder="请输入管理员密码"
                            prefix-icon="Lock"
                            size="large"
                            show-password
                            aria-label="密码输入框"
                        >
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button class="login-btn" @click="login" type="primary" size="large" aria-label="登录按钮">
                            登 录
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script>
import {ElMessageBox} from "element-plus"
export default {
    name: 'admin',
    data() {
        return {
            timer: null,
            loading: false,
            existCheck: false,
            errorCheck: false,
            passCheck: false,
            form: {
                username: '',
                password: '',
            },
            rules: {
                username: [
                    { required: true, message: '账号不能为空', trigger: 'blur' },
                ],
                password: [
                    { required: true, message: '密码不能为空', trigger: 'blur' },
                ],
            }
        }
    },
    beforeUnmount() {
        clearTimeout(this.timer)
    },
    methods: {
        login() {
            if(this.form.password.length === 0 || this.form.password.length === 0){
                ElMessageBox.alert("账号和密码不能为空！",{
                    title:"警告",
                    confirmButtonText:"返回",
                    type:"warning"
                })
            }else{
                let data = {
                    "username": this.form.username,
                    "password": this.form.password
                }
                this.axios({
                    method: 'POST',
                    url: "/spring_api/admin/login",
                    data: data,
                    headers:{
                        'Content-Type':'application/json'
                    }
                })
                    .then((response) => {
                        if (response.data === "Success") {
                            sessionStorage.setItem('admin_name', this.form.username)
                            sessionStorage.setItem('admin_authenticated', 'true')

                            this.loading = true
                            this.timer = setTimeout(() => {
                                this.loading = false;
                            }, 1000)
                            this.errorCheck = false;
                            this.passCheck = true;
                            this.existCheck = false;
                            this.timer = setTimeout(() => {
                                this.$router.push('/admin/background')
                            }, 1000)
                        } else if (response.data === "InfoError") {
                            this.loading = true
                            this.timer = setTimeout(() => {
                                this.loading = false
                                this.errorCheck = true;
                                this.passCheck = false;
                                this.existCheck = false;
                            }, 1000)
                        } else if (response.data === "NotExist") {
                            this.loading = true
                            this.timer = setTimeout(() => {
                                this.loading = false
                                this.errorCheck = false;
                                this.passCheck = false;
                                this.existCheck = true;
                            }, 1000)
                        }
                    })
                    .catch((error) => {
                        console.log(error)
                        ElMessageBox.alert("系统异常，请检查并修复！",{
                            title:"警告",
                            confirmButtonText:"返回",
                            type:"warning"
                        })
                    })
            }
        }
    }
}
</script>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Fira+Sans:wght@400;500;600;700&display=swap');
</style>

<style lang="less" scoped>
    .el-alert {
        position: fixed;
        top: 16px;
        left: 50%;
        transform: translateX(-50%);
        z-index: 2000;
        min-width: 280px;
        max-width: 90vw;
    }

    .container {
        width: 100vw;
        min-height: 100vh;
        background: linear-gradient(135deg, #020617 0%, #0F172A 50%, #1E293B 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 16px;
        font-family: 'Fira Sans', sans-serif;
    }

    .content {
        width: 100%;
        max-width: 480px;
    }

    .login-card {
        background: rgba(15, 23, 42, 0.8);
        backdrop-filter: blur(20px);
        border-radius: 20px;
        padding: 40px 32px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
        border: 1px solid rgba(255, 255, 255, 0.1);
    }

    .card-header {
        text-align: center;
        margin-bottom: 32px;
    }

    .logo-icon {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        background: #22C55E;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 20px;
        box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
    }

    .logo-icon .el-icon {
        font-size: 32px;
        color: white;
    }

    .login-title {
        font-size: 28px;
        font-weight: 700;
        color: #F8FAFC;
        margin: 0 0 8px 0;
        letter-spacing: -0.5px;
    }

    .login-subtitle {
        font-size: 14px;
        font-weight: 400;
        color: #94A3B8;
        margin: 0;
    }

    .login-form {
        width: 100%;
        padding: 0;
    }

    .el-form-item {
        margin-bottom: 20px;
    }

    .el-input {
        width: 100%;
        font-size: 16px;
    }

    .el-input__wrapper {
        border-radius: 12px;
        box-shadow: none;
        transition: all 0.2s ease;
        padding: 12px 16px;
        min-height: 48px;
        background: white;
        border: 1px solid rgba(255, 255, 255, 0.2);
    }

    .el-input__wrapper:hover {
        box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.3);
        border-color: #22C55E;
    }

    .el-input__wrapper.is-focus {
        box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.5);
        border-color: #22C55E;
    }

    .el-input__inner {
        color: #0F172A;
    }

    .el-input__inner::placeholder {
        color: #94A3B8;
    }

    .el-input__prefix-inner {
        color: #64748B;
    }

    .login-btn {
        width: 100%;
        height: 52px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 12px;
        background: #22C55E;
        border: none;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-top: 12px;
        min-height: 52px;
        min-width: 52px;
        color: white;
        box-shadow: 0 2px 8px rgba(34, 197, 94, 0.3);
    }

    .login-btn:hover {
        background: #16a34a;
        box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
    }

    .login-btn:active {
        transform: scale(0.98);
    }

    @media (min-width: 768px) {
        .login-card {
            padding: 48px 40px;
        }

        .login-title {
            font-size: 32px;
        }

        .login-subtitle {
            font-size: 16px;
            margin-bottom: 40px;
        }

        .el-form-item {
            margin-bottom: 24px;
        }

        .logo-icon {
            width: 72px;
            height: 72px;
        }

        .logo-icon .el-icon {
            font-size: 36px;
        }
    }

    @media (min-width: 1024px) {
        .login-card {
            padding: 56px 48px;
        }

        .content {
            max-width: 520px;
        }
    }

    @media (max-width: 480px) {
        .container {
            padding: 16px;
            align-items: center;
            padding-top: 40px;
        }

        .content {
            max-width: 100%;
        }

        .login-card {
            padding: 28px 20px;
            border-radius: 16px;
            width: 100%;
            box-sizing: border-box;
        }

        .login-title {
            font-size: 26px;
            margin-bottom: 6px;
        }

        .login-subtitle {
            font-size: 13px;
            margin-bottom: 28px;
        }

        .el-form-item {
            margin-bottom: 18px;
        }

        .el-input__wrapper {
            min-height: 48px;
            padding: 10px 14px;
        }

        .login-btn {
            height: 50px;
            min-height: 50px;
        }

        .logo-icon {
            width: 56px;
            height: 56px;
        }

        .logo-icon .el-icon {
            font-size: 28px;
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
