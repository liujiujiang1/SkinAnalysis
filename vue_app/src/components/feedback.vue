<template>
    <div class="feedback-page">
        <div class="page-header">
            <h1>反馈标注</h1>
            <p>查看用户对诊断结果的准确性反馈和真实诊断标注。</p>
        </div>

        <div class="summary-row">
            <div class="summary-item">
                <span>总反馈</span>
                <strong>{{ feedbackList.length }}</strong>
            </div>
            <div class="summary-item">
                <span>认为准确</span>
                <strong>{{ accurateCount }}</strong>
            </div>
            <div class="summary-item">
                <span>待关注</span>
                <strong>{{ inaccurateCount }}</strong>
            </div>
        </div>

        <div class="table-panel">
            <el-table :data="feedbackList" stripe class="feedback-table">
                <el-table-column label="用户" min-width="120" prop="username" />
                <el-table-column label="预测疾病" min-width="120">
                    <template #default="{ row }">{{ diseaseName(row.predictedDisease) }}</template>
                </el-table-column>
                <el-table-column label="反馈" min-width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.accurate ? 'success' : 'danger'">
                            {{ row.accurate ? '准确' : '不准确' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="真实诊断" min-width="140">
                    <template #default="{ row }">{{ diseaseName(row.realDisease) }}</template>
                </el-table-column>
                <el-table-column label="备注" min-width="180">
                    <template #default="{ row }">{{ row.comment || '-' }}</template>
                </el-table-column>
                <el-table-column label="时间" min-width="180">
                    <template #default="{ row }">{{ formatTime(row.feedbackTime) }}</template>
                </el-table-column>
                <el-table-column label="记录ID" min-width="90">
                    <template #default="{ row }">{{ row.recordId || '-' }}</template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script>
import { diseaseName } from '../data/diseaseKnowledge'

export default {
    name: 'feedback',
    data() {
        return {
            feedbackList: []
        }
    },
    computed: {
        accurateCount() {
            return this.feedbackList.filter(item => item.accurate).length
        },
        inaccurateCount() {
            return this.feedbackList.filter(item => item.accurate === false).length
        }
    },
    mounted() {
        this.loadFeedback()
    },
    methods: {
        async loadFeedback() {
            const response = await this.axios.get('/spring_api/feedback')
            this.feedbackList = response.data || []
        },
        diseaseName,
        formatTime(value) {
            return value ? new Date(value).toLocaleString() : '-'
        }
    }
}
</script>

<style lang="less" scoped>
.feedback-page {
    min-height: 100%;
}

.page-header {
    margin-bottom: 20px;

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
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
    margin-bottom: 16px;
}

.summary-item {
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
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
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 16px;
}

@media (max-width: 720px) {
    .summary-row {
        grid-template-columns: 1fr;
    }

    .table-panel {
        padding: 8px;
        overflow-x: auto;
    }
}
</style>
