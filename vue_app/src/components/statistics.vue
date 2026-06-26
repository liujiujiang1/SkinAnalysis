<template>
    <div class="dashboard-page">
        <div class="page-header">
            <h1>数据统计</h1>
            <p>疾病分布、诊断趋势和用户人群概览</p>
        </div>

        <div class="summary-grid">
            <div class="summary-card">
                <span>用户总数</span>
                <strong>{{ summary.userTotal || 0 }}</strong>
            </div>
            <div class="summary-card">
                <span>诊断总数</span>
                <strong>{{ summary.recordTotal || 0 }}</strong>
            </div>
            <div class="summary-card">
                <span>今日诊断</span>
                <strong>{{ summary.todayRecords || 0 }}</strong>
            </div>
            <div class="summary-card">
                <span>近 7 日诊断</span>
                <strong>{{ summary.last7DaysRecords || 0 }}</strong>
            </div>
        </div>

        <div class="charts-grid">
            <div class="panel">
                <div class="panel-title">疾病趋势</div>
                <div id="trend" class="chart-container"></div>
            </div>
            <div class="panel">
                <div class="panel-title">疾病占比</div>
                <div id="diseasePie" class="chart-container"></div>
            </div>
            <div class="panel">
                <div class="panel-title">性别维度</div>
                <div id="genderBar" class="chart-container small"></div>
            </div>
            <div class="panel">
                <div class="panel-title">地区维度</div>
                <div id="districtBar" class="chart-container small"></div>
            </div>
        </div>
    </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
    name: "statistics",
    data() {
        this.trendLine = null
        this.diseasePie = null
        this.genderBar = null
        this.districtBar = null
        return {
            summary: {},
            trend: {},
            total: [],
            dimensions: { gender: {}, district: {} },
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
    mounted() {
        this.loadDashboard()
        window.addEventListener('resize', this.handleResize)
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.handleResize)
        ;[this.trendLine, this.diseasePie, this.genderBar, this.districtBar].forEach((chart) => {
            if (chart) chart.dispose()
        })
    },
    methods: {
        async loadDashboard() {
            const [summary, total, trend, dimensions] = await Promise.all([
                this.axios.get('/spring_api/record/summary'),
                this.axios.get('/spring_api/record/queryCount'),
                this.axios.get('/spring_api/record/queryByMonth'),
                this.axios.get('/spring_api/record/dimensions')
            ])
            this.summary = summary.data
            this.total = total.data
            this.trend = trend.data
            this.dimensions = dimensions.data
            this.$nextTick(() => {
                this.drawTrend()
                this.drawDiseasePie()
                this.drawDimensionBar('genderBar', 'gender')
                this.drawDimensionBar('districtBar', 'district')
            })
        },
        handleResize() {
            ;[this.trendLine, this.diseasePie, this.genderBar, this.districtBar].forEach((chart) => {
                if (chart) chart.resize()
            })
        },
        diseaseName(code) {
            return this.diseaseMap[code] || code || '未知'
        },
        drawDiseasePie() {
            if (this.diseasePie) this.diseasePie.dispose()
            this.diseasePie = echarts.init(document.getElementById('diseasePie'))
            this.diseasePie.setOption({
                tooltip: { trigger: 'item' },
                color: ['#0891B2', '#22C55E', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899', '#06B6D4'],
                series: [{
                    type: 'pie',
                    radius: ['42%', '70%'],
                    data: this.total.map((item) => ({
                        name: this.diseaseName(item.disease),
                        value: item.count
                    }))
                }]
            })
        },
        drawTrend() {
            if (this.trendLine) this.trendLine.dispose()
            this.trendLine = echarts.init(document.getElementById('trend'))
            const months = Object.keys(this.trend).sort()
            const diseases = ['NV', 'MEL', 'BCC', 'AKIEC', 'BKL', 'DF', 'VASC']
            this.trendLine.setOption({
                tooltip: { trigger: 'axis' },
                legend: { data: diseases },
                grid: { left: 40, right: 20, bottom: 50, top: 45 },
                xAxis: { type: 'category', data: months, axisLabel: { rotate: 35 } },
                yAxis: { type: 'value' },
                series: diseases.map((disease) => ({
                    name: disease,
                    type: 'line',
                    smooth: true,
                    data: months.map((month) => this.trend[month]?.[disease] || 0)
                }))
            })
        },
        drawDimensionBar(elementId, key) {
            const chartName = elementId === 'genderBar' ? 'genderBar' : 'districtBar'
            if (this[chartName]) this[chartName].dispose()
            this[chartName] = echarts.init(document.getElementById(elementId))
            const data = this.dimensions[key] || {}
            const names = Object.keys(data)
            this[chartName].setOption({
                tooltip: { trigger: 'axis' },
                grid: { left: 40, right: 20, bottom: 50, top: 20 },
                xAxis: { type: 'category', data: names, axisLabel: { rotate: key === 'district' ? 35 : 0 } },
                yAxis: { type: 'value' },
                series: [{
                    type: 'bar',
                    data: names.map((name) => data[name]),
                    itemStyle: { color: '#0891B2', borderRadius: [6, 6, 0, 0] }
                }]
            })
        }
    }
}
</script>

<style lang="less" scoped>
.dashboard-page {
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

.summary-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 18px;
}

.summary-card,
.panel {
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 8px;
}

.summary-card {
    padding: 18px;

    span {
        display: block;
        color: #64748B;
        font-size: 13px;
        margin-bottom: 8px;
    }

    strong {
        font-size: 28px;
        color: #0F172A;
    }
}

.charts-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
}

.panel {
    padding: 16px;
}

.panel-title {
    font-size: 16px;
    font-weight: 700;
    color: #0F172A;
    margin-bottom: 10px;
}

.chart-container {
    width: 100%;
    height: 360px;
}

.chart-container.small {
    height: 280px;
}

@media (max-width: 1024px) {
    .summary-grid,
    .charts-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}

@media (max-width: 720px) {
    .summary-grid,
    .charts-grid {
        grid-template-columns: 1fr;
    }
}
</style>
