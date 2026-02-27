<template>
    <div class="container">
        <el-alert title="用户不存在" type="warning" effect="dark" center show-icon v-show="existCheck"/>
        <el-alert title="用户名或密码错误" type="error" effect="dark" center show-icon v-show="errorCheck"/>
        <el-alert title="登录成功" type="success" effect="dark" center show-icon v-show="passCheck"/>
        <div class="content">
            <div class="left-box">
                <div class="left-content">
                    <img src="/src/assets/medicine.jpg" alt="皮肤智诊 - 皮肤健康诊断"/>
                </div>
            </div>
            <div class="right-box">
                <div class="right-content">
                    <div class="logo-section">
                        <div class="logo-icon">
                            <el-icon><Monitor /></el-icon>
                        </div>
                    </div>
                    <el-form :rules="rules" :model="form" ref="ruleFormRef" v-loading="loading" @submit.prevent>
                        <h1 class="login-title">欢迎使用</h1>
                        <p class="login-subtitle">皮肤健康智能诊断平台</p>
                        <el-form-item prop="username">
                            <el-input 
                                class="username"
                                v-model="form.username"
                                placeholder="请输入用户名"
                                prefix-icon="User"
                                size="large"
                                clearable
                                aria-label="用户名输入框"
                                tabindex="1"
                            >
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="password">
                            <el-input 
                                class="password"
                                type="password"
                                v-model="form.password"
                                placeholder="请输入密码"
                                prefix-icon="Lock"
                                size="large"
                                show-password
                                aria-label="密码输入框"
                                tabindex="2"
                                @keyup.enter="login"
                            >
                            </el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button 
                                class="login-btn" 
                                @click="login" 
                                type="primary" 
                                size="large" 
                                aria-label="登录按钮"
                                tabindex="3"
                            >
                                登 录
                            </el-button>
                        </el-form-item>
                        <el-form-item>
                            <el-button 
                                class="register-btn" 
                                @click="showSignUpDialog = true" 
                                type="success" 
                                size="large" 
                                aria-label="注册按钮"
                                tabindex="4"
                            >
                                注 册
                            </el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </div>

        <el-dialog 
            title="用户注册" 
            v-model="showSignUpDialog" 
            width="90%" 
            :align-center="true" 
            :close-on-click-modal="false"
            @close="cancelHandle"
            aria-label="用户注册对话框"
        >
            <el-form class="form-box" :model="newForm" :rules="newRules" ref="newFormRef" label-position="top">
                <el-form-item label="用户名" prop="username">
                    <el-input 
                        class="dialog_input" 
                        v-model="newForm.username" 
                        placeholder="请输入用户名" 
                        aria-label="注册用户名"
                        clearable
                    >
                    </el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input 
                        class="dialog_input" 
                        v-model="newForm.password" 
                        type="password" 
                        placeholder="请输入密码" 
                        show-password 
                        aria-label="注册密码"
                    >
                    </el-input>
                </el-form-item>

                <el-form-item label="性别" prop="gender">
                    <el-select 
                        v-model="newForm.gender" 
                        placeholder="请选择性别" 
                        style="width: 100%" 
                        aria-label="选择性别"
                        clearable
                    >
                        <el-option
                            v-for="item in genderOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="出生日期" prop="birthday">
                    <el-date-picker
                        v-model="newForm.birthday"
                        type="date"
                        placeholder="选择出生日期"
                        format="YYYY/MM/DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                        aria-label="选择出生日期"
                    />
                </el-form-item>

                <el-form-item label="居住地" prop="district">
                    <el-input 
                        class="dialog_input" 
                        v-model="newForm.district" 
                        placeholder="请输入居住地" 
                        aria-label="居住地"
                        clearable
                    >
                    </el-input>
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button 
                        @click="cancelHandle" 
                        size="large"
                        aria-label="取消注册"
                    >
                        取 消
                    </el-button>
                    <el-button 
                        type="primary" 
                        @click="submit" 
                        size="large"
                        aria-label="确认注册"
                        native-type="submit"
                    >
                        确 定
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import {ElMessageBox} from "element-plus"
import { Monitor, User, Lock } from '@element-plus/icons-vue';
export default {
    components: {
        Monitor,
        User,
        Lock
    },
    name: 'user',
    data() {
        return {
            timer: null,
            loading: false,
            existCheck: false,
            errorCheck: false,
            passCheck: false,
            showSignUpDialog: false,
            form: {
                username: '',
                password: '',
            },
            newForm: {
                username: '',
                password: '',
                gender: '',
                birthday: '2000-01-01',
                district: '',
            },
            genderOptions: [
                {
                    label: "女",
                    value: "女"
                },
                {
                    label: "男",
                    value: "男"
                }
            ],
            rules: {
                username: [
                    { required: true, message: '账号不能为空', trigger: 'blur' },
                ],
                password: [
                    { required: true, message: '密码不能为空', trigger: 'blur' },
                ]
            },
            newRules: {
                username: [
                    { required: true, message: '账号不能为空', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '密码不能为空', trigger: 'blur' }
                ],
                gender: [
                    { required: true, message: '性别不能为空', trigger: 'blur' }
                ]
            }
        }
    },
    beforeUnmount() {
        clearTimeout(this.timer)
    },
    methods: {
        login() {
            if(this.form.username.length === 0 || this.form.password.length === 0){
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
                this.axios.post('/spring_api/user/login', data)
                    .then((response) => {
                        if (response.data === "Success") {
                            sessionStorage.setItem('user_name', this.form.username)
                            sessionStorage.setItem('user_authenticated', 'true')

                            this.loading = true
                            this.timer = setTimeout(() => {
                                this.loading = false;
                            }, 1000)
                            this.errorCheck = false;
                            this.passCheck = true;
                            this.existCheck = false;
                            this.timer = setTimeout(() => {
                                this.$router.push('/user/analyse')
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
                        ElMessageBox.alert("系统异常登录失败，请稍后重试或联系管理员！",{
                            title:"警告",
                            confirmButtonText:"返回",
                            type:"warning"
                        })
                    })
            }
        },
        submit() {
            let newUser = {
                'username': this.newForm.username,
                'password': this.newForm.password,
                'gender': this.newForm.gender,
                'birthday': this.newForm.birthday,
                'district': this.newForm.district
            }

            this.axios.post('/spring_api/user', newUser)
                .then((response) => {
                    let res = response.data
                    if (res === "Success") {
                        ElMessageBox.alert("注册成功！", {
                            title: "提示",
                            confirmButtonText: "确定",
                            type: "success"
                        })
                        this.showSignUpDialog = false;
                        this.newForm.username = ''
                        this.newForm.password = ''
                        this.newForm.gender = ''
                        this.newForm.birthday = '2000-01-01'
                        this.newForm.district = ''
                    } else if (res === "Exist") {
                        ElMessageBox.alert("已存在相同账号，请换个账号名重试！", {
                            title: "提示",
                            confirmButtonText: "返回",
                            type: "warning"
                        })
                    } else {
                        ElMessageBox.alert("添加失败，稍后重试！或检查系统！", {
                            title: "提示",
                            confirmButtonText: "返回",
                            type: "error"
                        })
                    }
                })
                .catch((error) => {
                    console.log(error)
                    ElMessageBox.alert("添加失败，稍后重试！或检查系统！", {
                            title: "提示",
                            confirmButtonText: "返回",
                            type: "error"
                        })
                })
        },
        cancelHandle() {
            this.showSignUpDialog = false
            this.newForm.username = ''
            this.newForm.password = ''
            this.newForm.gender = ''
            this.newForm.birthday = '2000-01-01'
            this.newForm.district = ''
        }
    }
}
</script>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;500;600;700&display=swap');
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
        background: linear-gradient(135deg, #F0FDFA 0%, #CCFBF1 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 16px;
        font-family: 'Noto Sans', sans-serif;
    }

    .content {
        width: 100%;
        max-width: 480px;
        display: flex;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 4px 20px rgba(8, 145, 178, 0.12);
        background: white;
    }

    .left-box {
        display: none;
        flex: 1;
        position: relative;
        overflow: hidden;
    }

    .left-content {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .right-box {
        flex: 1;
        background: rgba(255, 255, 255, 0.98);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 32px 24px;
    }

    .right-content {
        width: 100%;
        max-width: 100%;
    }

    .logo-section {
        display: flex;
        justify-content: center;
        margin-bottom: 24px;
    }

    .logo-icon {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        background: linear-gradient(135deg, #0891B2 0%, #22D3EE 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(8, 145, 178, 0.3);
    }

    .logo-icon .el-icon {
        font-size: 32px;
        color: white;
    }

    .login-title {
        font-size: 28px;
        font-weight: 700;
        color: #134E4A;
        text-align: center;
        margin: 0 0 8px 0;
        letter-spacing: -0.5px;
    }

    .login-subtitle {
        font-size: 14px;
        font-weight: 400;
        color: #64748B;
        text-align: center;
        margin: 0 0 32px 0;
    }

    .el-form {
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
        box-shadow: 0 0 0 1.5px #E2E8F0 inset;
        transition: all 0.2s ease;
        padding: 12px 16px;
        min-height: 48px;
        background: #F8FAFC;
    }

    .el-input__wrapper:hover {
        box-shadow: 0 0 0 1.5px #22D3EE inset;
        background: #F0FDFA;
    }

    .el-input__wrapper.is-focus {
        box-shadow: 0 0 0 3px #0891B2 inset, 0 0 0 6px rgba(8, 145, 178, 0.2);
        background: white;
    }

    .login-btn {
        width: 100%;
        height: 52px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 12px;
        background: linear-gradient(135deg, #0891B2 0%, #06b6d4 100%);
        border: none;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-top: 12px;
        min-height: 52px;
        min-width: 52px;
        color: white;
    }

    .login-btn:hover {
        background: linear-gradient(135deg, #0e7490 0%, #0891b2 100%);
        box-shadow: 0 4px 12px rgba(8, 145, 178, 0.3);
    }

    .login-btn:active {
        transform: scale(0.98);
    }

    .login-btn:focus-visible {
        outline: 3px solid #22D3EE;
        outline-offset: 2px;
    }

    .register-btn {
        width: 100%;
        height: 52px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 12px;
        background: linear-gradient(135deg, #22C55E 0%, #34d399 100%);
        border: none;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-top: 12px;
        min-height: 52px;
        min-width: 52px;
        color: white;
    }

    .register-btn:hover {
        background: linear-gradient(135deg, #16a34a 0%, #22c55e 100%);
        box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
    }

    .register-btn:active {
        transform: scale(0.98);
    }

    .register-btn:focus-visible {
        outline: 3px solid #34d399;
        outline-offset: 2px;
    }

    .form-box {
        width: 100%;
        margin: 0 auto;
    }

    .dialog_input {
        width: 100%;
    }

    .dialog-footer {
        display: flex;
        gap: 12px;
        justify-content: center;
        padding: 16px 0 8px 0;
    }

    .dialog-footer .el-button {
        min-width: 100px;
        min-height: 48px;
        border-radius: 12px;
    }

    @media (min-width: 768px) {
        .content {
            max-width: 900px;
            max-height: 600px;
        }

        .left-box {
            display: flex;
        }

        .right-box {
            padding: 48px 40px;
        }

        .right-content {
            max-width: 400px;
        }

        .logo-icon {
            width: 72px;
            height: 72px;
        }

        .logo-icon .el-icon {
            font-size: 36px;
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
    }

    @media (min-width: 1024px) {
        .content {
            max-width: 1000px;
        }

        .right-box {
            padding: 56px 48px;
        }
    }

    @media (max-width: 480px) {
        .container {
            padding: 12px;
            align-items: flex-start;
            padding-top: 24px;
        }

        .content {
            border-radius: 12px;
        }

        .right-box {
            padding: 28px 20px;
        }

        .logo-icon {
            width: 56px;
            height: 56px;
        }

        .logo-icon .el-icon {
            font-size: 28px;
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

        .login-btn,
        .register-btn {
            height: 50px;
            min-height: 50px;
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

    :deep(.el-dialog) {
        border-radius: 16px;
    }

    :deep(.el-dialog__header) {
        padding: 20px 24px;
        border-bottom: 1px solid #E2E8F0;
    }

    :deep(.el-dialog__body) {
        padding: 24px;
        max-height: 70vh;
        overflow-y: auto;
    }

    :deep(.el-dialog__footer) {
        padding: 16px 24px;
        border-top: 1px solid #E2E8F0;
    }

    :deep(.el-form-item__label) {
        font-weight: 500;
        color: #134E4A;
        font-size: 14px;
    }
</style>
