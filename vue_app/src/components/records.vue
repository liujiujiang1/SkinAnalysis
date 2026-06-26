<template>
    <div class="records-page">
        <div class="page-header">
            <h1>诊断记录</h1>
            <p>按用户查看上传图片、Top 3 结果、概率和建议</p>
        </div>

        <div class="records-panel">
            <div class="records-header">
                <div>
                    <h2>全部记录</h2>
                    <p>共 {{ filteredRecords.length }} 条</p>
                </div>
                <el-input v-model="recordSearch" class="record-search" placeholder="按用户名搜索" clearable />
            </div>

            <el-table :data="pagedRecords" stripe class="records-table desktop-table">
                <el-table-column label="序号" width="80" align="center">
                    <template #default="{ $index }">
                        {{ getRowIndex($index) }}
                    </template>
                </el-table-column>
                <el-table-column label="图片" width="90">
                    <template #default="{ row }">
                        <el-image v-if="row.imageData" class="thumb" :src="row.imageData" fit="cover" />
                        <span v-else class="empty-text">无</span>
                    </template>
                </el-table-column>
                <el-table-column label="用户" min-width="120">
                    <template #default="{ row }">{{ row.user?.username || '-' }}</template>
                </el-table-column>
                <el-table-column label="疾病" min-width="120">
                    <template #default="{ row }">{{ diseaseName(row.disease) }}</template>
                </el-table-column>
                <el-table-column label="概率" min-width="100">
                    <template #default="{ row }">{{ formatProbability(row.probability) }}%</template>
                </el-table-column>
                <el-table-column label="时间" min-width="180">
                    <template #default="{ row }">{{ formatTime(row.time) }}</template>
                </el-table-column>
                <el-table-column label="地区" min-width="120">
                    <template #default="{ row }">{{ row.user?.district || '-' }}</template>
                </el-table-column>
                <el-table-column label="性别" min-width="80">
                    <template #default="{ row }">{{ row.user?.gender || '-' }}</template>
                </el-table-column>
                <el-table-column label="操作" width="110" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" plain size="small" @click="openRecord(row)">详情</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="mobile-record-list">
                <div class="mobile-record-card" v-for="(row, index) in pagedRecords" :key="row.id || getRowIndex(index)">
                    <div class="mobile-card-top">
                        <span class="mobile-index">#{{ getRowIndex(index) }}</span>
                        <el-button type="primary" plain size="small" @click="openRecord(row)">详情</el-button>
                    </div>
                    <div class="mobile-record-main">
                        <el-image v-if="row.imageData" class="mobile-thumb" :src="row.imageData" fit="cover" />
                        <div v-else class="mobile-thumb empty-thumb">无图</div>
                        <div class="mobile-record-title">
                            <strong>{{ diseaseName(row.disease) }}</strong>
                            <span>{{ formatProbability(row.probability) }}%</span>
                        </div>
                    </div>
                    <div class="mobile-fields">
                        <div><span>用户</span><strong>{{ row.user?.username || '-' }}</strong></div>
                        <div><span>时间</span><strong>{{ formatTime(row.time) }}</strong></div>
                        <div><span>地区</span><strong>{{ row.user?.district || '-' }}</strong></div>
                        <div><span>性别</span><strong>{{ row.user?.gender || '-' }}</strong></div>
                    </div>
                </div>
            </div>

            <div class="pagination-wrap">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="filteredRecords.length"
                    :layout="paginationLayout"
                    background
                />
            </div>
        </div>

        <el-dialog v-model="detailVisible" title="诊断记录详情" width="760px" class="record-dialog">
            <div v-if="selectedRecord" class="detail-layout">
                <el-image v-if="selectedRecord.imageData" class="detail-image" :src="selectedRecord.imageData" fit="contain" />
                <div v-else class="detail-image empty-image">暂无图片</div>
                <div class="detail-content">
                    <h3>{{ diseaseName(selectedRecord.disease) }}</h3>
                    <p>{{ selectedRecord.user?.username || '-' }} · {{ formatTime(selectedRecord.time) }}</p>
                    <div v-for="item in parseTopResults(selectedRecord.topResults)" :key="item.code" class="top-result">
                        <div>{{ item.name }} <span>{{ item.probability }}%</span></div>
                        <el-progress :percentage="Number(item.probability)" :stroke-width="8" />
                    </div>
                    <h4>疾病简介</h4>
                    <p class="text-block">{{ selectedRecord.adviceBrief || '暂无简介' }}</p>
                    <h4>治疗建议</h4>
                    <p class="text-block">{{ selectedRecord.adviceTreatment || '暂无建议' }}</p>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "records",
    data() {
        return {
            records: [],
            recordSearch: '',
            currentPage: 1,
            pageSize: 10,
            isMobile: false,
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
        filteredRecords() {
            const keyword = this.recordSearch.trim()
            if (!keyword) return this.records
            return this.records.filter((record) => {
                return (record.user?.username || '').includes(keyword)
            })
        },
        pagedRecords() {
            const start = (this.currentPage - 1) * this.pageSize
            return this.filteredRecords.slice(start, start + this.pageSize)
        },
        paginationLayout() {
            return this.isMobile ? 'prev, pager, next' : 'total, sizes, prev, pager, next, jumper'
        }
    },
    watch: {
        recordSearch() {
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
        this.updateMobileState()
        window.addEventListener('resize', this.updateMobileState)
        this.loadRecords()
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.updateMobileState)
    },
    methods: {
        updateMobileState() {
            this.isMobile = window.innerWidth <= 720
        },
        async loadRecords() {
            const res = await this.axios.get('/spring_api/record/query')
            this.records = res.data || []
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
        openRecord(record) {
            this.selectedRecord = record
            this.detailVisible = true
        }
    }
}
</script>

<style lang="less" scoped>
.records-page {
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

.records-panel {
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 16px;
}

.records-header {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
    margin-bottom: 12px;

    h2 {
        margin: 0 0 4px;
        color: #0F172A;
    }

    p {
        margin: 0;
        color: #64748B;
    }
}

.record-search {
    max-width: 280px;
}

.thumb {
    width: 54px;
    height: 54px;
    border-radius: 6px;
    background: #F8FAFC;
}

.empty-text {
    color: #94A3B8;
}

.pagination-wrap {
    display: flex;
    justify-content: flex-end;
    padding-top: 10px;
}

.mobile-record-list {
    display: none;
}

.mobile-record-card {
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 10px;
    background: #FFFFFF;
}

.mobile-card-top,
.mobile-record-main {
    display: flex;
    align-items: center;
}

.mobile-card-top {
    justify-content: space-between;
    margin-bottom: 8px;
}

.mobile-index {
    color: #64748B;
    font-size: 13px;
    font-weight: 700;
}

.mobile-record-main {
    gap: 10px;
    margin-bottom: 8px;
}

.mobile-thumb {
    width: 68px;
    height: 68px;
    border-radius: 6px;
    background: #F8FAFC;
    flex-shrink: 0;
    border: 1px solid #EDF2F7;
}

.empty-thumb {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94A3B8;
    font-size: 12px;
}

.mobile-record-title {
    min-width: 0;
    flex: 1;

    strong,
    span {
        display: block;
    }

    strong {
        color: #0F172A;
        font-size: 15px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    span {
        margin-top: 3px;
        color: #0891B2;
        font-weight: 700;
        font-size: 14px;
    }
}

.mobile-fields {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 6px 10px;

    div {
        min-width: 0;
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 0;
        background: transparent;
    }

    span {
        flex: 0 0 auto;
        color: #64748B;
        font-size: 12px;
    }

    strong {
        min-width: 0;
        color: #334155;
        font-size: 13px;
        font-weight: 600;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}

.detail-layout {
    display: grid;
    grid-template-columns: 300px 1fr;
    gap: 20px;
}

.detail-image {
    width: 100%;
    height: 380px;
    background: #F8FAFC;
}

.empty-image {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94A3B8;
}

.detail-content h3 {
    margin: 0 0 6px;
}

.top-result {
    margin: 10px 0;

    div {
        display: flex;
        justify-content: space-between;
        margin-bottom: 4px;
        color: #334155;
    }
}

.text-block {
    white-space: pre-line;
    color: #475569;
    line-height: 1.6;
}

@media (max-width: 720px) {
    .records-page {
        min-height: auto;
    }

    .page-header {
        margin-bottom: 12px;

        h1 {
            font-size: 20px;
            margin-bottom: 4px;
        }

        p {
            font-size: 13px;
            line-height: 1.5;
        }
    }

    .records-panel {
        padding: 12px;
        border-radius: 8px;
        overflow: hidden;
    }

    .desktop-table {
        display: none;
    }

    .mobile-record-list {
        display: grid;
        gap: 8px;
    }

    .records-header,
    .detail-layout {
        display: block;
    }

    .records-header {
        margin-bottom: 8px;

        h2 {
            font-size: 18px;
        }

        p {
            font-size: 13px;
        }
    }

    .record-search {
        max-width: none;
        margin-top: 8px;
    }

    :deep(.records-table) {
        .el-table__cell {
            padding: 9px 6px;
            font-size: 13px;
        }

        .el-table__fixed-right {
            box-shadow: -4px 0 10px rgba(15, 23, 42, 0.06);
        }
    }

    .thumb {
        width: 44px;
        height: 44px;
    }

    .detail-image {
        height: 260px;
        margin-bottom: 16px;
    }

    .pagination-wrap {
        justify-content: center;
        overflow-x: visible;
        padding-bottom: 2px;
    }

    :deep(.record-dialog) {
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
    .page-header p {
        display: none;
    }

    .records-panel {
        padding: 8px;
    }

    .mobile-fields {
        grid-template-columns: 1fr 1fr;
        gap: 5px 8px;
    }

    .mobile-record-card {
        padding: 9px;
    }

    .mobile-thumb {
        width: 62px;
        height: 62px;
    }

    .mobile-record-title strong {
        font-size: 14px;
    }

    :deep(.records-table) {
        .el-table__cell {
            padding: 8px 4px;
            font-size: 12px;
        }
    }

    .detail-image {
        height: 220px;
    }
}
</style>
