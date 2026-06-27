<template>
    <div class="feedback-page">
        <div class="page-header">
            <h1>反馈标注</h1>
            <p>查看用户对诊断结果的准确性反馈和真实诊断标注。</p>
        </div>

        <div class="summary-row">
            <div class="summary-item">
                <span>总反馈</span>
                <strong>{{ performance.total || 0 }}</strong>
            </div>
            <div class="summary-item">
                <span>反馈准确率</span>
                <strong>{{ performance.accuracyRate || 0 }}%</strong>
            </div>
            <div class="summary-item">
                <span>待复核</span>
                <strong>{{ pendingReviewCount }}</strong>
            </div>
        </div>

        <div class="performance-grid">
            <div class="panel">
                <div class="panel-title">各疾病反馈准确率</div>
                <div id="feedbackAccuracyChart" class="chart-container"></div>
            </div>
            <div class="panel">
                <div class="panel-title">误判关注</div>
                <div class="misjudge-list">
                    <div v-for="item in performanceRows" :key="item.disease" class="misjudge-item">
                        <div>
                            <strong>{{ diseaseName(item.disease) }}</strong>
                            <span>{{ item.inaccurate }} 条不准确反馈</span>
                        </div>
                        <el-tag :type="Number(item.accuracyRate) >= 80 ? 'success' : 'warning'">{{ item.accuracyRate }}%</el-tag>
                    </div>
                </div>
            </div>
        </div>

        <div class="table-panel">
            <div class="filter-bar">
                <el-input v-model="filters.username" placeholder="搜索用户" clearable class="filter-input" />
                <el-select v-model="filters.accurate" placeholder="反馈结果" clearable class="filter-input">
                    <el-option label="准确" :value="true" />
                    <el-option label="不准确" :value="false" />
                </el-select>
                <el-select v-model="filters.reviewStatus" placeholder="审核状态" clearable class="filter-input">
                    <el-option label="待审核" value="待审核" />
                    <el-option label="待复核" value="待复核" />
                    <el-option label="已审核" value="已审核" />
                    <el-option label="需跟进" value="需跟进" />
                </el-select>
                <el-select v-model="filters.disease" placeholder="预测疾病" clearable class="filter-input">
                    <el-option v-for="item in diseaseOptions" :key="item.code" :label="item.name" :value="item.code" />
                </el-select>
                <el-button @click="resetFilters">重置</el-button>
            </div>

            <el-table :data="filteredFeedbackList" stripe class="feedback-table">
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
                <el-table-column label="审核状态" min-width="120">
                    <template #default="{ row }">
                        <el-tag :type="reviewTagType(row.reviewStatus)">
                            {{ row.reviewStatus || '待审核' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="时间" min-width="180">
                    <template #default="{ row }">{{ formatTime(row.feedbackTime) }}</template>
                </el-table-column>
                <el-table-column label="记录ID" min-width="90">
                    <template #default="{ row }">{{ row.recordId || '-' }}</template>
                </el-table-column>
                <el-table-column label="操作" min-width="180" fixed="right">
                    <template #default="{ row }">
                        <div class="action-buttons">
                            <el-button type="primary" plain size="small" @click="reviewFeedback(row, '已审核')">已审核</el-button>
                            <el-button type="warning" plain size="small" @click="reviewFeedback(row, '需跟进')">需跟进</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script>
import * as echarts from 'echarts'
import { diseaseKnowledge } from '../data/diseaseKnowledge'
import { createDiseaseMap, fetchDiseaseKnowledge } from '../utils/diseaseKnowledgeService'

export default {
    name: 'feedback',
    data() {
        this.accuracyChart = null
        return {
            feedbackList: [],
            performance: {},
            diseaseOptions: diseaseKnowledge,
            diseaseMap: createDiseaseMap(diseaseKnowledge),
            filters: {
                username: '',
                accurate: '',
                reviewStatus: '',
                disease: ''
            }
        }
    },
    computed: {
        filteredFeedbackList() {
            return this.feedbackList.filter((item) => {
                return (!this.filters.username || (item.username || '').includes(this.filters.username))
                    && (this.filters.accurate === '' || this.filters.accurate === null || typeof this.filters.accurate === 'undefined' || item.accurate === this.filters.accurate)
                    && (!this.filters.reviewStatus || (item.reviewStatus || '待审核') === this.filters.reviewStatus)
                    && (!this.filters.disease || item.predictedDisease === this.filters.disease)
            })
        },
        pendingReviewCount() {
            return this.feedbackList.filter(item => ['待审核', '待复核', undefined, null, ''].includes(item.reviewStatus)).length
        },
        performanceRows() {
            return [...(this.performance.byDisease || [])].sort((a, b) => {
                if (Number(b.inaccurate || 0) !== Number(a.inaccurate || 0)) {
                    return Number(b.inaccurate || 0) - Number(a.inaccurate || 0)
                }
                return Number(a.accuracyRate || 0) - Number(b.accuracyRate || 0)
            })
        }
    },
    mounted() {
        this.loadKnowledge()
        this.loadFeedback()
        window.addEventListener('resize', this.handleResize)
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.handleResize)
        if (this.accuracyChart) this.accuracyChart.dispose()
    },
    methods: {
        async loadKnowledge() {
            this.diseaseOptions = await fetchDiseaseKnowledge()
            this.diseaseMap = createDiseaseMap(this.diseaseOptions)
        },
        async loadFeedback() {
            const [feedback, performance] = await Promise.all([
                this.axios.get('/spring_api/feedback'),
                this.axios.get('/spring_api/feedback/performance')
            ])
            this.feedbackList = feedback.data || []
            this.performance = performance.data || {}
            this.$nextTick(this.drawAccuracyChart)
        },
        diseaseName(code) {
            return this.diseaseMap[code]?.name || code || '未知'
        },
        formatTime(value) {
            return value ? new Date(value).toLocaleString() : '-'
        },
        resetFilters() {
            this.filters = {
                username: '',
                accurate: '',
                reviewStatus: '',
                disease: ''
            }
        },
        reviewTagType(status) {
            if (status === '已审核') return 'success'
            if (status === '需跟进' || status === '待复核') return 'warning'
            return 'info'
        },
        async reviewFeedback(row, status) {
            const response = await this.axios.put(`/spring_api/feedback/${row.id}/review`, {
                reviewStatus: status,
                reviewNote: status === '需跟进' ? '管理员标记需进一步复核' : '管理员已审核'
            })
            const index = this.feedbackList.findIndex((item) => item.id === row.id)
            if (index >= 0) {
                this.feedbackList.splice(index, 1, response.data)
            }
            await this.loadFeedback()
        },
        drawAccuracyChart() {
            const element = document.getElementById('feedbackAccuracyChart')
            if (!element) return
            if (this.accuracyChart) this.accuracyChart.dispose()
            this.accuracyChart = echarts.init(element)
            const rows = this.performanceRows
            this.accuracyChart.setOption({
                tooltip: { trigger: 'axis' },
                grid: { left: 45, right: 20, top: 30, bottom: 60 },
                xAxis: {
                    type: 'category',
                    data: rows.map((item) => this.diseaseName(item.disease)),
                    axisLabel: { interval: 0, rotate: 30 }
                },
                yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
                series: [{
                    type: 'bar',
                    data: rows.map((item) => Number(item.accuracyRate || 0)),
                    itemStyle: { color: '#0891B2', borderRadius: [6, 6, 0, 0] }
                }]
            })
        },
        handleResize() {
            if (this.accuracyChart) this.accuracyChart.resize()
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

.performance-grid {
    display: grid;
    grid-template-columns: minmax(0, 1.4fr) minmax(280px, 0.6fr);
    gap: 16px;
    margin-bottom: 16px;
}

.panel {
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 16px;
}

.panel-title {
    font-size: 16px;
    font-weight: 700;
    color: #0F172A;
    margin-bottom: 10px;
}

.chart-container {
    height: 320px;
}

.misjudge-list {
    display: grid;
    gap: 10px;
}

.misjudge-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding-bottom: 10px;
    border-bottom: 1px solid #E2E8F0;

    strong,
    span {
        display: block;
    }

    strong {
        color: #0F172A;
    }

    span {
        margin-top: 3px;
        color: #64748B;
        font-size: 13px;
    }
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

.filter-bar {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 12px;
}

.filter-input {
    width: 180px;
}

.action-buttons {
    display: flex;
    gap: 6px;

    .el-button {
        margin-left: 0;
    }
}

@media (max-width: 720px) {
    .summary-row,
    .performance-grid {
        grid-template-columns: 1fr;
    }

    .table-panel {
        padding: 8px;
        overflow-x: auto;
    }

    .filter-bar {
        display: grid;
        grid-template-columns: 1fr;
    }

    .filter-input {
        width: 100%;
    }
}
</style>
