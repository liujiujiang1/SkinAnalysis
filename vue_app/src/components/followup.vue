<template>
    <div class="followup-page">
        <div class="page-header">
            <h1>病灶随访</h1>
            <el-button type="primary" @click="openCreate">新建档案</el-button>
        </div>

        <div class="followup-layout">
            <section class="profile-panel">
                <el-empty v-if="profiles.length === 0" description="暂无随访档案" />
                <div
                    v-for="item in profiles"
                    :key="item.id"
                    class="profile-item"
                    :class="{ active: selectedProfile?.id === item.id }"
                    @click="selectProfile(item)"
                >
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.bodySite || '未标注部位' }}</span>
                    <el-tag size="small" type="success">{{ item.status || '随访中' }}</el-tag>
                </div>
            </section>

            <section class="timeline-panel">
                <el-empty v-if="!selectedProfile" description="请选择一个随访档案" />
                <template v-else>
                    <div class="profile-summary">
                        <div>
                            <h2>{{ selectedProfile.title }}</h2>
                            <p>{{ selectedProfile.bodySite || '未标注部位' }} · {{ selectedProfile.note || '暂无备注' }}</p>
                        </div>
                        <el-button plain @click="openEdit">编辑档案</el-button>
                    </div>

                    <div v-if="records.length >= 2" class="compare-grid">
                        <div class="compare-card">
                            <span>首次记录</span>
                            <el-image :src="oldestRecord.imageData" fit="cover" />
                            <strong>{{ diseaseName(oldestRecord.disease) }} · {{ formatProbability(oldestRecord.probability) }}%</strong>
                        </div>
                        <div class="compare-card">
                            <span>最近记录</span>
                            <el-image :src="latestRecord.imageData" fit="cover" />
                            <strong>{{ diseaseName(latestRecord.disease) }} · {{ formatProbability(latestRecord.probability) }}%</strong>
                        </div>
                        <div class="change-card">
                            <span>置信度变化</span>
                            <strong :class="{ up: probabilityDelta > 0, down: probabilityDelta < 0 }">{{ probabilityDelta > 0 ? '+' : '' }}{{ probabilityDelta.toFixed(2) }}%</strong>
                            <p>请结合图片变化、症状和医生意见判断，模型概率不能单独作为病情变化结论。</p>
                        </div>
                    </div>

                    <el-empty v-if="records.length === 0" description="该档案暂无诊断记录，请在图像诊断页上传时关联此档案" />
                    <div v-else class="timeline">
                        <div v-for="record in records" :key="record.id" class="timeline-item">
                            <el-image class="timeline-image" :src="record.imageData" fit="cover" />
                            <div class="timeline-body">
                                <div class="timeline-title">
                                    <strong>{{ diseaseName(record.disease) }}</strong>
                                    <el-tag :type="riskTagType(record.riskLevel)">{{ record.riskLevel || '未分级' }}</el-tag>
                                </div>
                                <p>{{ formatTime(record.time) }}</p>
                                <el-progress :percentage="Number(record.probability || 0)" :stroke-width="8" />
                                <div class="risk-advice">{{ record.riskAdvice || '暂无就医提醒' }}</div>
                            </div>
                        </div>
                    </div>
                </template>
            </section>
        </div>

        <el-dialog v-model="dialogVisible" :title="editingProfile ? '编辑随访档案' : '新建随访档案'" width="420px">
            <el-form label-position="top" :model="form">
                <el-form-item label="档案名称">
                    <el-input v-model="form.title" />
                </el-form-item>
                <el-form-item label="皮损部位">
                    <el-input v-model="form.bodySite" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status" class="full-input">
                        <el-option label="随访中" value="随访中" />
                        <el-option label="已就医" value="已就医" />
                        <el-option label="已结束" value="已结束" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="form.note" type="textarea" :rows="3" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveProfile">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { fetchDiseaseKnowledge, createDiseaseMap } from '../utils/diseaseKnowledgeService'
import { riskTagType } from '../utils/risk'

export default {
    name: 'followup',
    data() {
        return {
            profiles: [],
            selectedProfile: null,
            records: [],
            diseaseMap: {},
            dialogVisible: false,
            editingProfile: null,
            form: {
                title: '',
                bodySite: '',
                status: '随访中',
                note: ''
            }
        }
    },
    computed: {
        latestRecord() {
            return this.records[0] || {}
        },
        oldestRecord() {
            return this.records[this.records.length - 1] || {}
        },
        probabilityDelta() {
            return Number(this.latestRecord.probability || 0) - Number(this.oldestRecord.probability || 0)
        }
    },
    mounted() {
        this.loadKnowledge()
        this.loadProfiles()
    },
    methods: {
        riskTagType,
        async loadKnowledge() {
            this.diseaseMap = createDiseaseMap(await fetchDiseaseKnowledge())
        },
        async loadProfiles() {
            const username = sessionStorage.getItem('user_name')
            const response = await this.axios.get(`/spring_api/lesion-profile/user/${username}`)
            this.profiles = response.data || []
            if (!this.selectedProfile && this.profiles.length) {
                this.selectProfile(this.profiles[0])
            }
        },
        async selectProfile(profile) {
            this.selectedProfile = profile
            const response = await this.axios.get(`/spring_api/record/lesion/${profile.id}`, {
                params: { username: sessionStorage.getItem('user_name') }
            })
            this.records = response.data || []
        },
        openCreate() {
            this.editingProfile = null
            this.form = { title: '', bodySite: '', status: '随访中', note: '' }
            this.dialogVisible = true
        },
        openEdit() {
            this.editingProfile = this.selectedProfile
            this.form = {
                title: this.selectedProfile.title,
                bodySite: this.selectedProfile.bodySite,
                status: this.selectedProfile.status || '随访中',
                note: this.selectedProfile.note || ''
            }
            this.dialogVisible = true
        },
        async saveProfile() {
            const payload = {
                username: sessionStorage.getItem('user_name'),
                ...this.form
            }
            const response = this.editingProfile
                ? await this.axios.put(`/spring_api/lesion-profile/${this.editingProfile.id}`, payload)
                : await this.axios.post('/spring_api/lesion-profile', payload)
            this.dialogVisible = false
            await this.loadProfiles()
            this.selectProfile(response.data)
        },
        diseaseName(code) {
            return this.diseaseMap[code]?.name || code || '未知'
        },
        formatProbability(value) {
            return Number(value || 0).toFixed(2)
        },
        formatTime(value) {
            return value ? new Date(value).toLocaleString() : '-'
        }
    }
}
</script>

<style lang="less" scoped>
.followup-page {
    min-height: 100%;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h1 {
        margin: 0;
        color: #0F172A;
    }
}

.followup-layout {
    display: grid;
    grid-template-columns: 280px 1fr;
    gap: 16px;
}

.profile-panel,
.timeline-panel {
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 16px;
}

.profile-item {
    display: grid;
    gap: 6px;
    padding: 12px;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    margin-bottom: 10px;
    cursor: pointer;
    color: #475569;

    strong {
        color: #0F172A;
    }
}

.profile-item.active {
    border-color: #0891B2;
    background: #F0FDFA;
}

.profile-summary {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 16px;

    h2 {
        margin: 0 0 4px;
        color: #134E4A;
    }

    p {
        margin: 0;
        color: #64748B;
    }
}

.compare-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
    margin-bottom: 16px;
}

.compare-card,
.change-card {
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 12px;
    background: #F8FAFC;

    span,
    strong {
        display: block;
    }

    span {
        color: #64748B;
        font-size: 13px;
    }

    strong {
        margin-top: 8px;
        color: #0F172A;
    }

    .el-image {
        width: 100%;
        height: 140px;
        margin-top: 8px;
        border-radius: 6px;
        background: #fff;
    }
}

.change-card strong {
    font-size: 28px;
}

.change-card strong.up {
    color: #DC2626;
}

.change-card strong.down {
    color: #059669;
}

.change-card p {
    color: #64748B;
    line-height: 1.6;
}

.timeline {
    display: grid;
    gap: 12px;
}

.timeline-item {
    display: grid;
    grid-template-columns: 120px 1fr;
    gap: 14px;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 12px;
}

.timeline-image {
    width: 120px;
    height: 120px;
    border-radius: 6px;
    background: #F8FAFC;
}

.timeline-title {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    align-items: center;
}

.timeline-body p {
    color: #64748B;
    margin: 6px 0 10px;
}

.risk-advice {
    margin-top: 10px;
    color: #475569;
    line-height: 1.6;
    font-size: 13px;
}

.full-input {
    width: 100%;
}

@media (max-width: 860px) {
    .followup-layout,
    .compare-grid {
        grid-template-columns: 1fr;
    }

    .timeline-item {
        grid-template-columns: 84px 1fr;
    }

    .timeline-image {
        width: 84px;
        height: 84px;
    }
}
</style>
