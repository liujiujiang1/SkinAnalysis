<template>
    <div class="wiki-page">
        <div class="page-header">
            <h1>疾病百科</h1>
            <p>覆盖模型识别的 7 类皮肤疾病，便于结合诊断结果查看风险和注意事项。</p>
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
            </section>
        </div>
    </div>
</template>

<script>
import { diseaseKnowledge } from '../data/diseaseKnowledge'

export default {
    name: 'diseaseWiki',
    data() {
        return {
            diseases: diseaseKnowledge
        }
    },
    methods: {
        riskTagType(risk) {
            if (risk.includes('高')) return 'danger'
            if (risk.includes('中')) return 'warning'
            return 'success'
        }
    }
}
</script>

<style lang="less" scoped>
.wiki-page {
    min-height: 100%;
}

.page-header {
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

@media (max-width: 720px) {
    .page-header {
        h1 {
            font-size: 22px;
        }

        p {
            font-size: 13px;
        }
    }

    .wiki-grid {
        grid-template-columns: 1fr;
        gap: 12px;
    }

    .wiki-card {
        padding: 14px;
    }
}
</style>
