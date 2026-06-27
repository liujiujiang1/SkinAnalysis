<template>
    <div class="profile-page">
        <div class="profile-panel">
            <h2>个人资料</h2>
            <el-form label-position="top" :model="form">
                <el-form-item label="用户名">
                    <el-input v-model="form.username" disabled />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="form.password" type="password" show-password />
                </el-form-item>
                <el-form-item label="性别">
                    <el-select v-model="form.gender" placeholder="请选择性别">
                        <el-option label="男" value="男" />
                        <el-option label="女" value="女" />
                        <el-option label="男" value="鐢?" />
                        <el-option label="女" value="濂?" />
                    </el-select>
                </el-form-item>
                <el-form-item label="出生日期">
                    <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="居住地">
                    <el-input v-model="form.district" />
                </el-form-item>
                <el-form-item label="密保问题" required>
                    <el-input v-model="form.securityQuestion" placeholder="例如：我的出生城市是？" />
                </el-form-item>
                <el-form-item label="密保答案" required>
                    <el-input v-model="form.securityAnswer" placeholder="忘记密码时用于验证" />
                </el-form-item>
                <el-button type="primary" @click="saveProfile">保存资料</el-button>
            </el-form>
        </div>
    </div>
</template>

<script>
import { ElMessage } from 'element-plus'

export default {
    name: "profile",
    data() {
        return {
            form: {
                username: '',
                password: '',
                gender: '女',
                birthday: '',
                district: '',
                securityQuestion: '',
                securityAnswer: '',
                state: '正常'
            }
        }
    },
    mounted() {
        this.loadProfile()
    },
    methods: {
        loadProfile() {
            const username = sessionStorage.getItem('user_name')
            this.axios.get(`/spring_api/user/${username}`)
                .then((response) => {
                    this.form = {
                        ...response.data,
                        gender: response.data.gender || '女',
                        state: response.data.state || '正常'
                    }
                })
        },
        saveProfile() {
            if (!this.form.securityQuestion?.trim() || !this.form.securityAnswer?.trim()) {
                ElMessage.warning('密保问题和密保答案为必填项')
                return
            }
            this.axios.put('/spring_api/user', this.form)
                .then((response) => {
                    if (response.data === 'Success') {
                        ElMessage.success('资料已保存')
                        this.loadProfile()
                    } else if (response.data === 'SecurityEmpty') {
                        ElMessage.warning('密保问题和密保答案为必填项')
                    } else {
                        ElMessage.error('保存失败')
                    }
                })
        }
    }
}
</script>

<style lang="less" scoped>
.profile-page {
    min-height: 100%;
    display: flex;
    justify-content: center;
}

.profile-panel {
    width: 100%;
    max-width: 520px;
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 10px;
    padding: 24px;

    h2 {
        margin: 0 0 20px;
        color: #134E4A;
    }

    .el-button {
        width: 100%;
        height: 44px;
    }
}
</style>
