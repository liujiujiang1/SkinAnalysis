<template>
    <div class="wiki-page">
        <div class="page-header">
            <div>
                <h1>疾病百科</h1>
                <p>覆盖模型识别的 7 类皮肤疾病，便于结合诊断结果查看风险和注意事项。</p>
            </div>
            <el-button v-if="isAdmin" type="primary" @click="loadDiseases">刷新知识库</el-button>
        </div>

        <div class="wiki-grid">
            <section v-for="item in diseases" :key="item.code" class="wiki-card">
                <div class="card-top">
                    <div>
                        <span class="code">{{ item.code }}</span>
                        <h2>{{ item.name }}</h2>
                    </div>
                    <el-tag :type="riskTagType(item.risk)" effect="dark">风险 {{ item.risk }}</el-tag>
                </div>
                <p class="intro">{{ item.intro }}</p>
                <div class="info-block">
                    <h3>常见症状</h3>
                    <ul>
                        <li v-for="text in item.symptoms" :key="text">{{ text }}</li>
                    </ul>
                </div>
                <div class="info-block">
                    <h3>建议</h3>
                    <ul>
                        <li v-for="text in item.advice" :key="text">{{ text }}</li>
                    </ul>
                </div>
                <div class="notice">
                    <strong>注意事项</strong>
                    <span>{{ item.cautions.join('；') }}</span>
                </div>
                <el-button v-if="isAdmin" class="edit-btn" type="primary" plain @click="openEditor(item)">编辑知识</el-button>
            </section>
        </div>

        <el-dialog v-model="editorVisible" title="编辑疾病知识" width="680px" class="wiki-editor">
            <el-form v-if="editForm" label-position="top" class="editor-form">
                <el-form-item label="疾病编码">
                    <el-input v-model="editForm.code" disabled />
                </el-form-item>
                <el-form-item label="疾病名称">
                    <el-input v-model="editForm.name" />
                </el-form-item>
                <el-form-item label="风险等级">
                    <el-select v-model="editForm.risk" class="full-input">
                        <el-option label="低" value="低" />
                        <el-option label="低到中" value="低到中" />
                        <el-option label="中" value="中" />
                        <el-option label="中高" value="中高" />
                        <el-option label="高" value="高" />
                    </el-select>
                </el-form-item>
                <el-form-item label="疾病简介">
                    <el-input v-model="editForm.intro" type="textarea" :rows="3" />
                </el-form-item>
                <el-form-item label="常见症状（一行一条）">
                    <el-input v-model="editForm.symptomsText" type="textarea" :rows="4" />
                </el-form-item>
                <el-form-item label="建议（一行一条）">
                    <el-input v-model="editForm.adviceText" type="textarea" :rows="4" />
                </el-form-item>
                <el-form-item label="注意事项（一行一条）">
                    <el-input v-model="editForm.cautionsText" type="textarea" :rows="3" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editorVisible = false">取消</el-button>
                <el-button type="primary" @click="saveEditor">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { ElMessageBox } from 'element-plus'
import { fetchDiseaseKnowledge, saveDiseaseKnowledge } from '../utils/diseaseKnowledgeService'

export default {
    name: 'diseaseWiki',
    data() {
        return {
            diseases: [],
            editorVisible: false,
            editForm: null
        }
    },
    computed: {
        isAdmin() {
            return this.$route.path.startsWith('/admin')
        }
    },
    mounted() {
        this.loadDiseases()
    },
    methods: {
        async loadDiseases() {
            this.diseases = await fetchDiseaseKnowledge()
        },
        riskTagType(risk) {
            if (!risk) return 'info'
            if (risk.includes('高')) return 'danger'
            if (risk.includes('中')) return 'warning'
            return 'success'
        },
        openEditor(item) {
            this.editForm = {
                ...item,
                symptomsText: item.symptoms.join('\n'),
                adviceText: item.advice.join('\n'),
                cautionsText: item.cautions.join('\n')
            }
            this.editorVisible = true
        },
        async saveEditor() {
            const saved = await saveDiseaseKnowledge({
                code: this.editForm.code,
                name: this.editForm.name,
                risk: this.editForm.risk,
                intro: this.editForm.intro,
                symptoms: this.editForm.symptomsText,
                advice: this.editForm.adviceText,
                cautions: this.editForm.cautionsText
            })
            const index = this.diseases.findIndex((item) => item.code === saved.code)
            if (index >= 0) {
                this.diseases.splice(index, 1, saved)
            }
            this.editorVisible = false
            ElMessageBox.alert('知识库已更新。', {
                title: '提示',
                confirmButtonText: '确定',
                type: 'success'
            })
        }
    }
}
</script>

<style lang="less" scoped>
.wiki-page {
    min-height: 100%;
}

.page-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
    margin-bottom: 20px;

    h1 {
        margin: 0 0 6px;
        color: #0F172A;
        font-size: 28px;
    }

    p {
        margin: 0;
        color: #64748B;
        line-height: 1.6;
    }
}

.wiki-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 16px;
}

.wiki-card {
    background: #FFFFFF;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 18px;
}

.card-top {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    align-items: flex-start;
    margin-bottom: 10px;

    h2 {
        margin: 4px 0 0;
        color: #134E4A;
        font-size: 20px;
    }
}

.code {
    color: #0891B2;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.04em;
}

.intro {
    color: #475569;
    line-height: 1.7;
    margin: 0 0 14px;
}

.info-block {
    margin-top: 12px;

    h3 {
        margin: 0 0 8px;
        font-size: 15px;
        color: #0F172A;
    }

    ul {
        margin: 0;
        padding-left: 18px;
        color: #475569;
        line-height: 1.7;
    }
}

.notice {
    margin-top: 14px;
    padding: 10px 12px;
    border-radius: 8px;
    background: #F8FAFC;
    border: 1px solid #E2E8F0;
    color: #475569;
    line-height: 1.6;

    strong {
        display: block;
        color: #B45309;
        margin-bottom: 4px;
    }
}

.edit-btn {
    width: 100%;
    margin-top: 14px;
}

.full-input {
    width: 100%;
}

@media (max-width: 720px) {
    .page-header {
        display: block;

        h1 {
            font-size: 22px;
        }

        p {
            font-size: 13px;
        }

        .el-button {
            width: 100%;
            margin-top: 10px;
        }
    }

    .wiki-grid {
        grid-template-columns: 1fr;
        gap: 12px;
    }

    .wiki-card {
        padding: 14px;
    }

    :deep(.wiki-editor) {
        width: calc(100vw - 24px) !important;
        margin-top: 5vh !important;

        .el-dialog__body {
            max-height: 72vh;
            overflow-y: auto;
            padding: 16px;
        }
    }
}
</style>
