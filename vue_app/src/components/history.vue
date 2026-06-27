<template>
    <div class="history-page">
        <div class="page-header">
            <h2>我的诊断历史</h2>
            <el-button type="primary" @click="loadRecords">刷新</el-button>
        </div>

        <div class="filter-bar">
            <el-input v-model="keyword" placeholder="搜索疾病或备注" clearable class="keyword-input" />
            <el-select v-model="diseaseFilter" placeholder="疾病类型" clearable class="filter-input">
                <el-option v-for="item in diseaseOptions" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
            <el-date-picker
                v-model="dateRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                class="date-filter"
            />
            <el-button @click="resetFilters">重置</el-button>
        </div>

        <el-empty v-if="filteredRecords.length === 0" description="暂无诊断记录" />

        <div v-else class="history-grid">
            <div class="history-card" v-for="(record, index) in pagedRecords" :key="record.id">
                <div class="card-index">{{ getRowIndex(index) }}</div>
                <el-image class="record-image" :src="record.imageData" fit="cover">
                    <template #error>
                        <div class="image-empty">无图片</div>
                    </template>
                </el-image>
                <div class="record-body">
                    <div class="record-title">
                        <span>{{ diseaseName(record.disease) }}</span>
                        <el-tag type="success">{{ formatProbability(record.probability) }}%</el-tag>
                    </div>
                    <div class="risk-line">
                        <el-tag :type="riskTagType(record.riskLevel)" size="small">{{ record.riskLevel || '未分级' }}</el-tag>
                        <span>{{ record.riskAdvice || '暂无就医提醒' }}</span>
                    </div>
                    <div class="record-meta">{{ formatTime(record.time) }}</div>
                    <div class="top-list">
                        <div v-for="item in parseTopResults(record.topResults)" :key="item.code" class="top-item">
                            <span>{{ item.name }}</span>
                            <el-progress :percentage="Number(item.probability)" :stroke-width="8" />
                        </div>
                    </div>
                    <div class="card-actions">
                        <el-button type="primary" plain @click="openDetail(record)">查看详情</el-button>
                        <el-button type="success" plain @click="downloadReport(record)">下载报告</el-button>
                    </div>
                </div>
            </div>
        </div>

        <div v-if="filteredRecords.length > 0" class="pagination-wrap">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[6, 12, 24, 48]"
                :total="filteredRecords.length"
                layout="total, sizes, prev, pager, next, jumper"
                background
            />
        </div>

        <el-dialog v-model="detailVisible" title="诊断详情" width="720px" class="history-dialog">
            <div v-if="selectedRecord" class="detail-layout">
                <el-image class="detail-image" :src="selectedRecord.imageData" fit="contain" />
                <div class="detail-content">
                    <h3>{{ diseaseName(selectedRecord.disease) }}</h3>
                    <p class="detail-time">{{ formatTime(selectedRecord.time) }}</p>
                    <div class="detail-risk">
                        <el-tag :type="riskTagType(selectedRecord.riskLevel)">{{ selectedRecord.riskLevel || '未分级' }}</el-tag>
                        <span>{{ selectedRecord.riskAdvice || '暂无就医提醒' }}</span>
                    </div>
                    <div class="detail-block">
                        <h4>疾病简介</h4>
                        <p>{{ selectedRecord.adviceBrief || '暂无简介' }}</p>
                    </div>
                    <div class="detail-block">
                        <h4>Top 3 结果</h4>
                        <div v-for="item in parseTopResults(selectedRecord.topResults)" :key="item.code" class="detail-top-item">
                            <span>{{ item.name }}</span>
                            <el-progress :percentage="Number(item.probability)" :stroke-width="8" />
                        </div>
                    </div>
                    <div class="detail-block">
                        <h4>治疗建议</h4>
                        <p>{{ selectedRecord.adviceTreatment || '暂无建议' }}</p>
                    </div>
                    <div class="disclaimer">诊断结果仅供参考，不能替代医生面诊。</div>
                    <el-button class="detail-download" type="success" @click="downloadReport(selectedRecord)">下载报告</el-button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { fetchDiseaseKnowledge, createDiseaseMap } from '../utils/diseaseKnowledgeService'
import { downloadDiagnosisReport, formatProbability, formatTime, parseTopResults } from '../utils/report'
import { riskTagType } from '../utils/risk'

export default {
    name: "history",
    data() {
        return {
            records: [],
            keyword: '',
            diseaseFilter: '',
            dateRange: [],
            currentPage: 1,
            pageSize: 6,
            selectedRecord: null,
            detailVisible: false,
            diseaseOptions: [],
            diseaseMap: {}
        }
    },
    computed: {
        filteredRecords() {
            const keyword = this.keyword.trim()
            const start = this.dateRange?.[0] ? new Date(`${this.dateRange[0]} 00:00:00`).getTime() : null
            const end = this.dateRange?.[1] ? new Date(`${this.dateRange[1]} 23:59:59`).getTime() : null
            return this.records.filter((record) => {
                const recordTime = record.time ? new Date(record.time).getTime() : 0
                const text = `${this.diseaseName(record.disease)} ${record.adviceBrief || ''} ${record.adviceTreatment || ''}`
                return (!keyword || text.includes(keyword))
                    && (!this.diseaseFilter || record.disease === this.diseaseFilter)
                    && (!start || recordTime >= start)
                    && (!end || recordTime <= end)
            })
        },
        pagedRecords() {
            const start = (this.currentPage - 1) * this.pageSize
            return this.filteredRecords.slice(start, start + this.pageSize)
        }
    },
    watch: {
        keyword() {
            this.currentPage = 1
        },
        diseaseFilter() {
            this.currentPage = 1
        },
        dateRange() {
            this.currentPage = 1
        },
        pageSize() {
            this.currentPage = 1
        },
        filteredRecords() {
            const maxPage = Math.max(1, Math.ceil(this.filteredRecords.length / this.pageSize))
            if (this.currentPage > maxPage) {
                this.currentPage = maxPage
            }
        }
    },
    mounted() {
        this.loadKnowledge()
        this.loadRecords()
    },
    methods: {
        async loadKnowledge() {
            this.diseaseOptions = await fetchDiseaseKnowledge()
            this.diseaseMap = createDiseaseMap(this.diseaseOptions)
        },
        loadRecords() {
            const username = sessionStorage.getItem('user_name')
            this.axios.get(`/spring_api/record/user/${username}`)
                .then((response) => {
                    this.records = response.data
                    this.currentPage = 1
                })
        },
        getRowIndex(index) {
            return (this.currentPage - 1) * this.pageSize + index + 1
        },
        diseaseName(code) {
            return this.diseaseMap[code]?.name || code || '未知'
        },
        formatProbability,
        formatTime,
        parseTopResults,
        riskTagType,
        openDetail(record) {
            this.selectedRecord = record
            this.detailVisible = true
        },
        resetFilters() {
            this.keyword = ''
            this.diseaseFilter = ''
            this.dateRange = []
        },
        downloadReport(record) {
            downloadDiagnosisReport(record, { diseaseName: this.diseaseName })
        }
    }
}
</script>

<style lang="less" scoped>
.history-page {
    min-height: 100%;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
        margin: 0;
        color: #134E4A;
    }
}

.history-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 18px;
}

.filter-bar {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    align-items: center;
    margin-bottom: 16px;
    padding: 14px;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    background: #FFFFFF;
}

.keyword-input,
.filter-input {
    width: 220px;
}

.date-filter {
    width: 280px;
}

.history-card {
    position: relative;
    background: white;
    border-radius: 10px;
    border: 1px solid #E2E8F0;
    overflow: hidden;
}

.card-index {
    position: absolute;
    top: 10px;
    left: 10px;
    z-index: 1;
    min-width: 32px;
    height: 28px;
    padding: 0 8px;
    border-radius: 14px;
    background: rgba(15, 23, 42, 0.78);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 700;
}

.record-image {
    width: 100%;
    height: 180px;
    background: #F8FAFC;
}

.image-empty {
    height: 180px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94A3B8;
}

.record-body {
    padding: 16px;
}

.record-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 18px;
    font-weight: 700;
    color: #134E4A;
}

.record-meta {
    margin: 8px 0 14px;
    color: #64748B;
    font-size: 13px;
}

.risk-line {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    margin-top: 8px;
    color: #64748B;
    font-size: 12px;
    line-height: 1.5;

    span:last-child {
        flex: 1;
    }
}

.top-item {
    margin-bottom: 10px;
    font-size: 13px;
    color: #334155;
}

.card-actions {
    display: flex;
    gap: 8px;

    .el-button {
        flex: 1;
        margin-left: 0;
    }
}

.detail-layout {
    display: grid;
    grid-template-columns: 280px 1fr;
    gap: 20px;
}

.detail-image {
    width: 100%;
    height: 360px;
    background: #F8FAFC;
}

.detail-content h3 {
    margin: 0;
    color: #134E4A;
}

.detail-time {
    color: #64748B;
}

.detail-risk {
    display: flex;
    gap: 10px;
    align-items: flex-start;
    padding: 10px;
    border-radius: 8px;
    background: #F8FAFC;
    color: #475569;
    line-height: 1.6;
}

.detail-block h4 {
    margin: 14px 0 6px;
}

.detail-block p {
    white-space: pre-line;
    line-height: 1.6;
    color: #475569;
}

.detail-top-item {
    margin-bottom: 10px;
    color: #334155;
    font-size: 13px;
}

.detail-download {
    width: 100%;
    margin-top: 12px;
}

.disclaimer {
    margin-top: 14px;
    padding: 10px;
    border-radius: 8px;
    background: #FFFBEB;
    color: #B45309;
}

.pagination-wrap {
    display: flex;
    justify-content: flex-end;
    padding-top: 18px;
}

@media (max-width: 720px) {
    .page-header {
        align-items: flex-start;
        gap: 10px;

        h2 {
            font-size: 20px;
        }
    }

    .history-grid {
        grid-template-columns: 1fr;
        gap: 12px;
    }

    .filter-bar {
        display: grid;
        grid-template-columns: 1fr;
    }

    .keyword-input,
    .filter-input,
    .date-filter {
        width: 100%;
    }

    .record-image {
        height: 160px;
    }

    .record-body {
        padding: 12px;
    }

    .record-title {
        font-size: 16px;
        gap: 10px;
    }

    .detail-layout {
        grid-template-columns: 1fr;
    }

    .detail-image {
        height: 260px;
    }

    .pagination-wrap {
        justify-content: flex-start;
        overflow-x: auto;

        :deep(.el-pagination) {
            min-width: max-content;
        }
    }

    :deep(.history-dialog) {
        width: calc(100vw - 24px) !important;
        margin-top: 5vh !important;

        .el-dialog__body {
            max-height: 74vh;
            overflow-y: auto;
            padding: 16px;
        }
    }
}

@media (max-width: 480px) {
    .page-header {
        display: block;

        .el-button {
            width: 100%;
            margin-top: 10px;
        }
    }

    .record-image {
        height: 140px;
    }

    .detail-image {
        height: 220px;
    }
}
</style>
