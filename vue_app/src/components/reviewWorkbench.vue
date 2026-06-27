<template>
    <div class="review-page">
        <div class="page-header">
            <h1>审核工作台</h1>
            <p>集中处理高风险诊断、低置信度诊断和用户反馈复核。</p>
        </div>

        <div class="summary-row">
            <div class="summary-card">
                <span>全部任务</span>
                <strong>{{ tasks.length }}</strong>
            </div>
            <div class="summary-card">
                <span>待处理</span>
                <strong>{{ countByStatus('待处理') }}</strong>
            </div>
            <div class="summary-card">
                <span>需复查</span>
                <strong>{{ countByStatus('需复查') }}</strong>
            </div>
            <div class="summary-card">
                <span>训练样本</span>
                <strong>{{ tasks.filter(item => item.trainingCandidate).length }}</strong>
            </div>
        </div>

        <div class="table-panel">
            <div class="filter-bar">
                <el-input v-model="filters.keyword" placeholder="搜索用户/原因" clearable class="filter-input" />
                <el-select v-model="filters.taskType" placeholder="任务类型" clearable class="filter-input">
                    <el-option label="高风险诊断" value="高风险诊断" />
                    <el-option label="低置信度诊断" value="低置信度诊断" />
                    <el-option label="用户反馈复核" value="用户反馈复核" />
                </el-select>
                <el-select v-model="filters.status" placeholder="状态" clearable class="filter-input">
                    <el-option label="待处理" value="待处理" />
                    <el-option label="已审核" value="已审核" />
                    <el-option label="需复查" value="需复查" />
                    <el-option label="疑似误判" value="疑似误判" />
                </el-select>
                <el-button @click="loadTasks">刷新</el-button>
            </div>

            <el-table :data="filteredTasks" stripe>
                <el-table-column label="类型" min-width="130">
                    <template #default="{ row }">
                        <el-tag :type="taskTagType(row.taskType)">{{ row.taskType }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="用户" min-width="120" prop="username" />
                <el-table-column label="疾病" min-width="120">
                    <template #default="{ row }">{{ diseaseName(row.disease) }}</template>
                </el-table-column>
                <el-table-column label="风险" min-width="110">
                    <template #default="{ row }">
                        <el-tag :type="riskTagType(row.riskLevel)">{{ row.riskLevel || '未分级' }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="原因" min-width="240" prop="reason" />
                <el-table-column label="状态" min-width="110">
                    <template #default="{ row }">
                        <el-tag :type="statusTagType(row.status)">{{ row.status }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="训练样本" min-width="100">
                    <template #default="{ row }">{{ row.trainingCandidate ? '是' : '否' }}</template>
                </el-table-column>
                <el-table-column label="创建时间" min-width="170">
                    <template #default="{ row }">{{ formatTime(row.createdTime) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" plain size="small" @click="openEditor(row)">处理</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <el-dialog v-model="dialogVisible" title="处理审核任务" width="520px">
            <el-form v-if="selectedTask" label-position="top" :model="form">
                <el-form-item label="处理状态">
                    <el-select v-model="form.status" class="full-input">
                        <el-option label="已审核" value="已审核" />
                        <el-option label="需复查" value="需复查" />
                        <el-option label="疑似误判" value="疑似误判" />
                        <el-option label="待处理" value="待处理" />
                    </el-select>
                </el-form-item>
                <el-form-item label="审核备注">
                    <el-input v-model="form.reviewNote" type="textarea" :rows="4" placeholder="填写复核意见、建议或后续动作" />
                </el-form-item>
                <el-form-item>
                    <el-checkbox v-model="form.trainingCandidate">标记为可用于训练集</el-checkbox>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveTask">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { fetchDiseaseKnowledge, createDiseaseMap } from '../utils/diseaseKnowledgeService'
import { riskTagType } from '../utils/risk'

export default {
    name: 'reviewWorkbench',
    data() {
        return {
            tasks: [],
            diseaseMap: {},
            filters: {
                keyword: '',
                taskType: '',
                status: ''
            },
            dialogVisible: false,
            selectedTask: null,
            form: {
                status: '已审核',
                reviewNote: '',
                trainingCandidate: false
            }
        }
    },
    computed: {
        filteredTasks() {
            const keyword = this.filters.keyword.trim()
            return this.tasks.filter((item) => {
                const text = `${item.username || ''} ${item.reason || ''} ${item.reviewNote || ''}`
                return (!keyword || text.includes(keyword))
                    && (!this.filters.taskType || item.taskType === this.filters.taskType)
                    && (!this.filters.status || item.status === this.filters.status)
            })
        }
    },
    mounted() {
        this.loadKnowledge()
        this.loadTasks()
    },
    methods: {
        riskTagType,
        async loadKnowledge() {
            this.diseaseMap = createDiseaseMap(await fetchDiseaseKnowledge())
        },
        async loadTasks() {
            const response = await this.axios.get('/spring_api/review-task')
            this.tasks = response.data || []
        },
        countByStatus(status) {
            return this.tasks.filter(item => item.status === status).length
        },
        diseaseName(code) {
            return this.diseaseMap[code]?.name || code || '未知'
        },
        taskTagType(type) {
            if (type === '高风险诊断') return 'danger'
            if (type === '低置信度诊断') return 'warning'
            return 'info'
        },
        statusTagType(status) {
            if (status === '已审核') return 'success'
            if (status === '需复查' || status === '疑似误判') return 'warning'
            return 'info'
        },
        formatTime(value) {
            return value ? new Date(value).toLocaleString() : '-'
        },
        openEditor(task) {
            this.selectedTask = task
            this.form = {
                status: task.status || '已审核',
                reviewNote: task.reviewNote || '',
                trainingCandidate: Boolean(task.trainingCandidate)
            }
            this.dialogVisible = true
        },
        async saveTask() {
            await this.axios.put(`/spring_api/review-task/${this.selectedTask.id}`, this.form)
            this.dialogVisible = false
            await this.loadTasks()
        }
    }
}
</script>

<style lang="less" scoped>
.review-page {
    min-height: 100%;
}

.page-header {
    margin-bottom: 18px;

    h1 {
        margin: 0 0 6px;
        color: #0F172A;
    }

    p {
        margin: 0;
        color: #64748B;
    }
}

.summary-row {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 12px;
    margin-bottom: 16px;
}

.summary-card,
.table-panel {
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
}

.summary-card {
    padding: 16px;

    span,
    strong {
        display: block;
    }

    span {
        color: #64748B;
        font-size: 13px;
    }

    strong {
        margin-top: 6px;
        color: #0F172A;
        font-size: 24px;
    }
}

.table-panel {
    padding: 16px;
}

.filter-bar {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 12px;
}

.filter-input {
    width: 200px;
}

.full-input {
    width: 100%;
}

@media (max-width: 720px) {
    .summary-row {
        grid-template-columns: 1fr 1fr;
    }

    .filter-bar {
        display: grid;
        grid-template-columns: 1fr;
    }

    .filter-input {
        width: 100%;
    }

    .table-panel {
        overflow-x: auto;
        padding: 8px;
    }
}
</style>
