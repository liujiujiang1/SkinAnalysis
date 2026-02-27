<template>
    <div class="manage-container">
        <div class="page-header">
            <div class="header-info">
                <el-icon class="header-icon"><User /></el-icon>
                <div>
                    <h1 class="page-title">用户管理</h1>
                    <p class="page-subtitle">管理系统中的所有用户账号</p>
                </div>
            </div>
            <div class="header-actions">
                <el-input 
                    class="search-input" 
                    v-model="search" 
                    placeholder="搜索用户名..." 
                    clearable
                >
                    <template #prefix>
                        <el-icon><Search /></el-icon>
                    </template>
                </el-input>
            </div>
        </div>

        <div class="table-wrapper">
            <el-table 
                :data="filterTableData" 
                class="user-table"
                stripe
                :header-cell-style="{ background: '#f8fafc', color: '#475569' }"
            >
                <el-table-column label="账号" prop="username" min-width="150">
                    <template #default="{ row }">
                        <div class="user-cell">
                            <el-icon class="user-icon"><User /></el-icon>
                            <span class="username-text">{{ row.username }}</span>
                        </div>
                    </template>
                </el-table-column>
                
                <!-- <el-table-column label="密码" prop="password" min-width="200">
                    <template #default="{ row }">
                        <span class="password-text">{{ row.password.substring(0, 16) }}...</span>
                    </template>
                </el-table-column>
                 -->
                <el-table-column label="状态" prop="state" min-width="120">
                    <template #default="{ row }">
                        <el-tag 
                            :type="getStateType(row.state)"
                            effect="plain"
                            size="default"
                        >
                            {{ row.state }}
                        </el-tag>
                    </template>
                </el-table-column>
                
                <el-table-column label="性别" prop="gender" min-width="100">
                    <template #default="{ row }">
                        <div class="gender-cell">
                            <el-icon v-if="row.gender === '男'" class="gender-icon male"><Male /></el-icon>
                            <el-icon v-else class="gender-icon female"><Female /></el-icon>
                            <span>{{ row.gender }}</span>
                        </div>
                    </template>
                </el-table-column>
                
                <el-table-column label="出生日期" prop="birthday" min-width="120" />
                <el-table-column label="居住地" prop="district" min-width="150" />

                <el-table-column label="操作" width="180" align="center" fixed="right">
                    <template #default="scope">
                        <div class="action-buttons">
                            <el-button 
                                @click="editUser(scope.row)"
                                type="primary"
                                size="small"
                                :icon="Edit"
                            >
                                编辑
                            </el-button>
                            <el-button
                                type="danger"
                                size="small"
                                :icon="Delete"
                                @click="deleteUser(scope.$index, scope.row)"
                            >
                                删除
                            </el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <el-dialog 
            class="edit-dialog" 
            title="编辑用户信息" 
            v-model="showDialog"
            width="500px"
            :close-on-click-modal="false"
        >
            <el-form class="form-box" :model="newForm" :rules="rules" label-position="top">
                <el-form-item label="用户名" prop="username">
                    <el-input 
                        class="dialog_input" 
                        v-model="newForm.username"
                        disabled
                        placeholder="用户名不可修改"
                    >
                        <template #prefix>
                            <el-icon><User /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                
                <el-form-item label="密码" prop="password">
                    <el-input 
                        class="dialog_input" 
                        v-model="newForm.password"
                        type="password"
                        placeholder="请输入新密码"
                        show-password
                    >
                        <template #prefix>
                            <el-icon><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item label="性别" prop="gender">
                    <el-select 
                        v-model="newForm.gender"
                        class="dialog_select"
                        disabled
                        placeholder="性别不可修改"
                    >
                        <el-option
                            v-for="item in sexOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="状态" prop="state">
                    <el-select 
                        v-model="newForm.state"
                        class="dialog_select"
                        placeholder="选择用户状态"
                    >
                        <el-option
                            v-for="item in stateOptions"
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
                        placeholder="出生日期"
                        disabled
                        format="YYYY/MM/DD"
                        value-format="YYYY-MM-DD"
                        class="dialog_datepicker"
                    />
                </el-form-item>
            
                <el-form-item label="居住地" prop="district">
                    <el-input 
                        class="dialog_input" 
                        v-model="newForm.district" 
                        disabled
                        placeholder="居住地不可修改"
                    >
                        <template #prefix>
                            <el-icon><Location /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="cancelHandle" size="large">取 消</el-button>
                    <el-button type="primary" @click="submit" size="large">确 定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { ElMessageBox } from "element-plus";
import { 
    User, 
    Search, 
    Edit, 
    Delete, 
    Lock, 
    Location, 
    Male, 
    Female 
} from '@element-plus/icons-vue';

export default {
    name: "manage",
    components: {
        User,
        Search,
        Edit,
        Delete,
        Lock,
        Location,
        Male,
        Female
    },
    data() {
        return {
            search: '',
            userData: [],
            showDialog: false,
            newForm: {
                username: '',
                password: '',
                name: '',
                gender: '女',
                age: '2000-01-01',
                state: "正常",
                district: "",
            },
            submitType: '',
            rules: {
                username: [
                    {required: true, message: '账号不能为空', trigger: 'blur'}
                ],
                password: [
                    {required: true, message: '密码不能为空', trigger: 'blur'}
                ],
                name: [
                    {required: true, message: '姓名不能为空', trigger: 'blur'}
                ],
                gender: [
                    {required: true, message: '性别不能为空', trigger: 'blur'}
                ],
                age: [
                    {required: true, message: '年龄不能为空', trigger: 'blur'}
                ]
            },
            stateOptions: [
                {
                    label: "正常",
                    value: "正常"
                },
                {
                    label: "冻结",
                    value: "冻结"
                },
                {
                    label: "注销",
                    value: "注销"
                }
            ],
            sexOptions: [
                {
                    label: "女",
                    value: "女"
                },
                {
                    label: "男",
                    value: "男"
                }
            ],
            sexSelected: "女",
            pickerMinDate: "",
        }
    },
    computed: {
        filterTableData() {
            return this.userData.filter((data) => {
                return this.search === "" ? data : data.username.includes(this.search)
            })
        },
    },
    mounted() {
        this.query();
    },
    methods: {
        getStateType(state) {
            const typeMap = {
                '正常': 'success',
                '冻结': 'warning',
                '注销': 'info'
            };
            return typeMap[state] || 'info';
        },
        disabledDate(time){
            let curDate = (new Date()).getTime();
            let year = 18 * 365 * 24 * 3600 * 1000;
            let maxTime = curDate - year
            return time.getTime() > maxTime;
        },
        query() {
            this.axios.get("/spring_api/user")
                .then((response) => {
                    this.userData = response.data
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        deleteUser(index, row) {
            ElMessageBox.confirm("你确定删除该项信息吗?", "提示", {
                confirmButtonText: "确认",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then((action) => {
                    if (action === 'confirm') {
                        this.axios.delete('/spring_api/user/'+row['username'])
                        .then((response) => {
                            let res = response.data
                            if (res == "Success") {
                                ElMessageBox.alert("删除成功！", {
                                    title: "提示",
                                    confirmButtonText: "确定",
                                    type: "success"
                                })
                            } else if (res == "NotExist") {
                                ElMessageBox.alert("该用户不存在！", {
                                    title: "提示",
                                    confirmButtonText: "确定",
                                    type: "warning",
                                })
                            }
                            this.query()
                        })
                        .catch((error) => {
                            console.log(error)
                            ElMessageBox.alert("删除失败，请稍后重试或检查系统！", {
                                title: "提示",
                                confirmButtonText: "确定",
                                type: "error",
                            })
                            this.query()
                        })
                    }
                })
        },
        
        editUser(row) {
            this.newForm = { ...row }
            this.showDialog = true
        },
        submit() {
            let data = {
                'username': this.newForm.username,
                'password': this.newForm.password,
                'gender': this.newForm.gender,
                'birthday': this.newForm.birthday,
                'district': this.newForm.district,
                'state': this.newForm.state
            }

            this.axios.put("/spring_api/user", data)
                .then((response) => {
                    let res = response.data
                    console.log(res)
                    if (res === "Success") {
                        ElMessageBox.alert("修改成功", {
                            title: "提示",
                            confirmButtonText: "确定",
                            type: 'success'
                        })
                    } else if (res === "NotExist"){
                        this.newForm = data
                        ElMessageBox.alert("该用户不存在！", {
                            title: "提示",
                            confirmButtonText: "返回",
                            type: 'error'
                        })
                    } else {
                        this.newForm = data
                        ElMessageBox.alert("修改失败，稍后重试！或检查系统！", {
                            title: "提示",
                            confirmButtonText: "返回",
                            type: 'error'
                        })
                    }
                    this.query()
                    this.showDialog = false
                })
                .catch((error) => {
                    console.log(error)
                    this.newForm = data
                    ElMessageBox.alert("修改失败，稍后重试！或检查系统！", {
                        title: "提示",
                        confirmButtonText: "返回",
                        type: 'error'
                    })
                })
        },
        cancelHandle() {
            this.showDialog = false
            this.query()
        },
    }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;500;600;700&display=swap');
</style>

<style lang="less" scoped>
.manage-container {
    width: 100%;
    height: 100%;
    font-family: 'Noto Sans', sans-serif;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 24px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-info {
    display: flex;
    align-items: center;
    gap: 16px;
}

.header-icon {
    font-size: 32px;
    color: #0891B2;
    width: 56px;
    height: 56px;
    background: linear-gradient(135deg, rgba(8, 145, 178, 0.1) 0%, rgba(34, 197, 94, 0.1) 100%);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.page-title {
    font-size: 24px;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 4px 0;
    line-height: 1.2;
}

.page-subtitle {
    font-size: 14px;
    color: #64748b;
    margin: 0;
}

.header-actions {
    display: flex;
    gap: 12px;
}

.search-input {
    width: 300px;
    
    :deep(.el-input__wrapper) {
        border-radius: 10px;
        padding: 10px 16px;
        border: 1px solid #e2e8f0;
        box-shadow: none;
        transition: all 0.2s ease;
        
        &:hover {
            border-color: #0891B2;
        }
        
        &.is-focus {
            border-color: #0891B2;
            box-shadow: 0 0 0 3px rgba(8, 145, 178, 0.1);
        }
    }
}

.table-wrapper {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.user-table {
    :deep(.el-table__header-wrapper) {
        th {
            background: #f8fafc;
            color: #475569;
            font-weight: 600;
            font-size: 14px;
        }
    }
    
    :deep(.el-table__body-wrapper) {
        tr {
            transition: background-color 0.2s ease;
            
            &:hover {
                background-color: #f8fafc;
            }
        }
    }
}

.user-cell {
    display: flex;
    align-items: center;
    gap: 8px;
}

.user-icon {
    font-size: 18px;
    color: #0891B2;
}

.username-text {
    font-weight: 500;
    color: #1e293b;
}

.password-text {
    font-family: 'Courier New', monospace;
    font-size: 13px;
    color: #94a3b8;
}

.gender-cell {
    display: flex;
    align-items: center;
    gap: 6px;
}

.gender-icon {
    font-size: 18px;
    
    &.male {
        color: #3b82f6;
    }
    
    &.female {
        color: #ec4899;
    }
}

.action-buttons {
    display: flex;
    gap: 8px;
    justify-content: center;
}

:deep(.edit-dialog) {
    .el-dialog__header {
        padding: 24px 24px 16px;
        border-bottom: 1px solid #e2e8f0;
        
        .el-dialog__title {
            font-size: 20px;
            font-weight: 600;
            color: #1e293b;
        }
    }
    
    .el-dialog__body {
        padding: 24px;
    }
    
    .el-dialog__footer {
        padding: 16px 24px 24px;
        border-top: 1px solid #e2e8f0;
    }
}

.form-box {
    width: 100%;
}

:deep(.el-form-item__label) {
    color: #475569;
    font-weight: 500;
    font-size: 14px;
}

.dialog_input,
.dialog_select,
.dialog_datepicker {
    width: 100%;
    
    .el-input__wrapper,
    .el-select__wrapper {
        border-radius: 8px;
        padding: 8px 12px;
        border: 1px solid #e2e8f0;
        box-shadow: none;
        transition: all 0.2s ease;
        
        &:hover {
            border-color: #0891B2;
        }
        
        &.is-focus {
            border-color: #0891B2;
            box-shadow: 0 0 0 3px rgba(8, 145, 178, 0.1);
        }
    }
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    
    .el-button {
        padding: 10px 24px;
        border-radius: 8px;
        font-weight: 500;
        font-size: 14px;
    }
}

@media (max-width: 1024px) {
    .page-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
    }
    
    .header-actions {
        width: 100%;
        
        .search-input {
            width: 100%;
        }
    }
    
    .table-wrapper {
        padding: 16px;
    }
}

@media (max-width: 768px) {
    .page-header {
        padding: 16px;
    }
    
    .header-icon {
        width: 48px;
        height: 48px;
        font-size: 24px;
    }
    
    .page-title {
        font-size: 20px;
    }
    
    .page-subtitle {
        font-size: 13px;
    }
    
    .table-wrapper {
        padding: 12px;
        overflow-x: auto;
    }
    
    :deep(.user-table) {
        .el-table__cell {
            padding: 8px;
        }
    }
    
    .action-buttons {
        flex-direction: column;
        gap: 6px;
        
        .el-button {
            width: 100%;
        }
    }
}

@media (max-width: 480px) {
    .page-header {
        padding: 12px;
    }
    
    .header-info {
        gap: 12px;
    }
    
    .page-title {
        font-size: 18px;
    }
    
    .table-wrapper {
        padding: 8px;
    }
}
</style>