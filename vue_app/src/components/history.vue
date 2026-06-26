<template>
    <div class="history-page">
        <div class="page-header">
            <h2>我的诊断历史</h2>
            <el-button type="primary" @click="loadRecords">刷新</el-button>
        </div>

        <el-empty v-if="records.length === 0" description="暂无诊断记录" />

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
                    <div class="record-meta">{{ formatTime(record.time) }}</div>
                    <div class="top-list">
                        <div v-for="item in parseTopResults(record.topResults)" :key="item.code" class="top-item">
                            <span>{{ item.name }}</span>
                            <el-progress :percentage="Number(item.probability)" :stroke-width="8" />
                        </div>
                    </div>
                    <el-button type="primary" plain @click="openDetail(record)">查看详情</el-button>
                </div>
            </div>
        </div>

        <div v-if="records.length > 0" class="pagination-wrap">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[6, 12, 24, 48]"
                :total="records.length"
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
                    <div class="detail-block">
                        <h4>疾病简介</h4>
                        <p>{{ selectedRecord.adviceBrief || '暂无简介' }}</p>
                    </div>
                    <div class="detail-block">
                        <h4>治疗建议</h4>
                        <p>{{ selectedRecord.adviceTreatment || '暂无建议' }}</p>
                    </div>
                    <div class="disclaimer">诊断结果仅供参考，不能替代医生面诊。</div>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "history",
    data() {
        return {
            records: [],
            currentPage: 1,
            pageSize: 6,
            selectedRecord: null,
            detailVisible: false,
            diseaseMap: {
                MEL: '黑色素瘤',
                NV: '黑素细胞痣',
                BCC: '基底细胞癌',
                AKIEC: '光化性角化病',
                BKL: '良性角化病',
                DF: '皮肤纤维瘤',
                VASC: '血管病变'
            }
        }
    },
    computed: {
        pagedRecords() {
            const start = (this.currentPage - 1) * this.pageSize
            return this.records.slice(start, start + this.pageSize)
        }
    },
    watch: {
        pageSize() {
            this.currentPage = 1
        },
        records() {
            const maxPage = Math.max(1, Math.ceil(this.records.length / this.pageSize))
            if (this.currentPage > maxPage) {
                this.currentPage = maxPage
            }
        }
    },
    mounted() {
        this.loadRecords()
    },
    methods: {
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
            return this.diseaseMap[code] || code || '未知'
        },
        formatProbability(value) {
            return Number(value || 0).toFixed(2)
        },
        formatTime(value) {
            return value ? new Date(value).toLocaleString() : '-'
        },
        parseTopResults(value) {
            if (!value) return []
            try {
                return JSON.parse(value)
            } catch (e) {
                return []
            }
        },
        openDetail(record) {
            this.selectedRecord = record
            this.detailVisible = true
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

.top-item {
    margin-bottom: 10px;
    font-size: 13px;
    color: #334155;
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

.detail-block h4 {
    margin: 14px 0 6px;
}

.detail-block p {
    white-space: pre-line;
    line-height: 1.6;
    color: #475569;
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
