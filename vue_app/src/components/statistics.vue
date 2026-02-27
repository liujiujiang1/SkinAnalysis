<template>
    <div class="statistics-container">
        <div class="page-header">
            <h1 class="page-title">数据统计</h1>
            <p class="page-subtitle">疾病诊断数据分析</p>
        </div>

        <div class="charts-grid">
            <div class="chart-card trend-card">
                <div class="card-header">
                    <div class="card-title">
                        <el-icon class="title-icon"><TrendCharts /></el-icon>
                        <span>疾病趋势</span>
                    </div>
                    <el-select v-model="timeSelect" @change="changeHandle" class="time-selector">
                        <el-option
                            v-for="item in timeOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </div>
                <div id="trend" class="chart-container"></div>
            </div>

            <div class="chart-card total-card">
                <div class="card-header">
                    <div class="card-title">
                        <el-icon class="title-icon"><DataAnalysis /></el-icon>
                        <span>疾病统计</span>
                    </div>
                </div>
                <div id="total" class="chart-container"></div>
            </div>
        </div>
    </div>
</template>

<script>
import * as echarts from 'echarts'
import { TrendCharts, DataAnalysis } from '@element-plus/icons-vue'
export default {
    name: "statistics",
    components: {
        TrendCharts,
        DataAnalysis
    },
    data() {
        this.trendLine = null
        this.totalBar = null
        return {
            timeSelect:'7days',
            timeOptions:[
                {
                    label: '近7天',
                    value: '7days'
                },
                {
                    label: '近1个月',
                    value: '30days'
                },
                {
                    label: '近半年',
                    value: '6months'
                },
                {
                    label: '近1年',
                    value: '1year'
                },
                {
                    label: '近3年',
                    value: '3years'
                },
                {
                    label: '近5年',
                    value: '5years'
                }
            ],
            trend:{},
            total:{}
        }
    },
    mounted() {
        this.getTotal()
            .then((response)=>{
                this.drawBar()
            })
        this.getTrend()
            .then((response)=>{
                this.drawLine()
            })
        window.addEventListener('resize', this.handleResize)
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.handleResize)
        if (this.trendLine) {
            this.trendLine.dispose()
        }
        if (this.totalBar) {
            this.totalBar.dispose()
        }
    },
    methods:{
        handleResize() {
            if (this.trendLine) {
                this.trendLine.resize()
            }
            if (this.totalBar) {
                this.totalBar.resize()
            }
        },
        changeHandle(){
            Promise.all([this.getTrend(), this.getTotal()])
                .then(() => {
                    this.drawLine()
                    this.drawBar()
                })
        },
        async getTotal(){
            await this.axios.get('/spring_api/record/queryCount')
                .then((response)=>{
                    this.total = response.data
                })
                .catch((error) => {
                        console.log(error)
                        ElMessageBox.alert("系统异常，请检查并修复！",{
                            title:"警告",
                            confirmButtonText:"返回",
                            type:"warning"
                        })
                    })
        },
        async drawBar(){
            if(this.totalBar != null)
                this.totalBar.dispose()
            this.totalBar = echarts.init(document.getElementById('total'))
            let barOption = {
                tooltip: {
                    trigger: 'axis',
                    backgroundColor: "rgba(255, 255, 255, 0.98)",
                    padding: [12, 16],
                    textStyle: {
                        color: "#1e293b",
                        fontWeight: "500",
                    },
                    borderColor: "#e2e8f0",
                    borderWidth: 1,
                    valueFormatter: (value) => {
                        return value;
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '10%',
                    top: '10%',
                    containLabel: true
                },
                color: ['#0891B2', '#22C55E', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899', '#06B6D4'],
                xAxis: {
                    type: 'category',
                    boundaryGap: true,
                    data: ['NV', 'MEL', 'BCC', 'AKIEC', 'BKL', 'DF',  'VASC'],
                    axisLine: {
                        lineStyle: {
                            color: '#e2e8f0'
                        }
                    },
                    axisLabel: {
                        color: '#64748b',
                        fontSize: 12
                    }
                },
                yAxis: {
                    type: 'value',
                    axisLine: {
                        lineStyle: {
                            color: '#e2e8f0'
                        }
                    },
                    axisLabel: {
                        color: '#64748b',
                        fontSize: 12
                    },
                    splitLine: {
                        lineStyle: {
                            color: '#f1f5f9',
                            type: 'dashed'
                        }
                    }
                },
                series: []
            }

            //async返回Promise 对象, 需要使用.then来提取信息
            let res=this.total;

            let se = {
                type: 'bar',
                data: [],
                itemStyle: {
                    normal: {
                        borderRadius: [8, 8, 0, 0],
                        color: function(params) {
                            var colorList = ['#0891B2', '#22C55E', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899', '#06B6D4'];
                            return colorList[params.dataIndex]
                        }
                    }
                },
                barWidth: '50%',
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
            for(var i=0; i<res.length; i++){
                se.data.push(res[i]['count'])
            }
            barOption.series.push(se)
            this.totalBar.setOption(barOption)
        },

        async getTrend(){
            await this.axios.get('/spring_api/record/queryByMonth')
                .then((response)=>{
                    this.trend = response.data
                })
                .catch((error) => {
                        console.log(error)
                        ElMessageBox.alert("系统异常，请检查并修复！",{
                            title:"警告",
                            confirmButtonText:"返回",
                            type:"warning"
                        })
                    })
        },
        
        async drawLine(){
            if(this.trendLine!=null)
                this.trendLine.dispose()
            this.trendLine = echarts.init(document.getElementById('trend'))
            let lineOption = {
                tooltip: {
                    trigger: 'axis',
                    backgroundColor: "rgba(255, 255, 255, 0.98)",
                    padding: [12, 16],
                    textStyle: {
                        color: "#1e293b",
                        fontWeight: "500",
                    },
                    borderColor: "#e2e8f0",
                    borderWidth: 1,
                    valueFormatter: (value) => {
                        return value;
                    }
                },
                legend: {
                    data: ["NV", "MEL", "BCC", "AKIEC", "BKL", "DF", "VASC"],
                    top: 0,
                    right: 0,
                    textStyle: {
                        color: '#64748b',
                        fontSize: 12
                    },
                    itemWidth: 12,
                    itemHeight: 12
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '10%',
                    top: '15%',
                    containLabel: true
                },
                color: ['#0891B2', '#22C55E', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899', '#06B6D4'],
                xAxis: {
                    type: 'category',
                    boundaryGap: true,
                    axisLabel:{
                        interval:0,
                        rotate:40,
                        color: '#64748b',
                        fontSize: 11
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#e2e8f0'
                        }
                    },
                    data: []
                },
                yAxis: {
                    type: 'value',
                    axisLine: {
                        lineStyle: {
                            color: '#e2e8f0'
                        }
                    },
                    axisLabel: {
                        color: '#64748b',
                        fontSize: 12
                    },
                    splitLine: {
                        lineStyle: {
                            color: '#f1f5f9',
                            type: 'dashed'
                        }
                    }
                },
                series: []
            }

            let res = this.trend;
            
            let timeList = new Array()

            for(let time in Object.keys(res)){
                timeList.unshift(Object.keys(res)[time])
            }
            timeList.sort(function(a, b){
                if (a < b)  return -1;
                else if (a > b)   return 1;
                else    return 0;
            })
            
            
            let nv = new Array()
            let mel = new Array()
            let bcc = new Array()
            let akiec = new Array()
            let bkl = new Array()
            let df = new Array()
            let vasc = new Array()

            for(let i=0; i<timeList.length; i++){
                let currTime = timeList[i]
                lineOption.xAxis.data.push(currTime)
                let currDiseaseData = res[currTime]
                console.log(currDiseaseData)
                if (currDiseaseData['NV'] != undefined)
                    nv.push(currDiseaseData['NV'])
                else
                    nv.push(0)
                if (currDiseaseData['MEL']!=undefined)
                    mel.push(currDiseaseData['MEL'])
                else
                    mel.push(0)
                if (currDiseaseData['BCC'] != undefined)
                    bcc.push(currDiseaseData['BCC'])
                else
                    bcc.push(0)
                if (currDiseaseData['AKIEC'] != undefined)
                    akiec.push(currDiseaseData['AKIEC'])
                else
                    akiec.push(0)
                if (currDiseaseData['BKL'] != undefined)
                    bkl.push(currDiseaseData['BKL'])
                else
                    bkl.push(0)
                if (currDiseaseData['DF'] != undefined)
                    df.push(currDiseaseData['DF'])
                else
                    df.push(0)
                if (currDiseaseData['VASC'])
                    vasc.push(currDiseaseData['VASC'])
                else
                    vasc.push(0)
            }

            let dataList = new Array()
            dataList['NV'] = nv
            dataList['MEL'] = mel
            dataList['BCC'] = bcc
            dataList['AKIEC'] = akiec
            dataList['BKL'] = bkl
            dataList['DF'] = df
            dataList['VASC'] = vasc
    
            let index = 0
            for(let key in dataList){
                let se = {
                    name: key,
                    type: 'line',
                    data: dataList[key],
                    smooth: true,
                    symbol: 'circle',
                    symbolSize: 6,
                    lineStyle: {
                        width: 2,
                        color: lineOption.color[index]
                    },
                    itemStyle: {
                        borderWidth: 2,
                        borderColor: '#fff'
                    },
                    areaStyle: {
                        opacity: 0.1
                    }
                }
                lineOption.series.push(se)
                index++
            }
            console.log(lineOption.series)
            this.trendLine.setOption(lineOption);
        },
    }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;500;600;700&display=swap');
</style>

<style lang="less" scoped>
.statistics-container {
    min-height: 100vh;
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
    padding: 24px;
    box-sizing: border-box;
    font-family: 'Noto Sans', sans-serif;
}

.page-header {
    text-align: center;
    margin-bottom: 32px;
}

.page-title {
    font-size: 28px;
    font-weight: 700;
    color: #f8fafc;
    margin: 0 0 8px 0;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-subtitle {
    font-size: 14px;
    color: #94a3b8;
    margin: 0;
}

.charts-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 24px;
    max-width: 1400px;
    margin: 0 auto;
}

.chart-card {
    background: rgba(255, 255, 255, 0.03);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.08);
    padding: 24px;
    transition: all 0.3s ease;
    
    &:hover {
        border-color: rgba(34, 197, 94, 0.3);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    }
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
    color: #f8fafc;
}

.title-icon {
    width: 24px;
    height: 24px;
    color: #22C55E;
}

.time-selector {
    width: 120px;
    
    :deep(.el-input__wrapper) {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.15);
        border-radius: 8px;
        box-shadow: none;
        padding: 4px 12px;
        min-height: 36px;
        
        &:hover {
            border-color: rgba(34, 197, 94, 0.5);
        }
        
        &.is-focus {
            border-color: #22C55E;
            background: rgba(255, 255, 255, 0.15);
        }
    }
    
    :deep(.el-input__inner) {
        color: #f8fafc;
        font-size: 14px;
    }
    
    :deep(.el-select__caret) {
        color: #94a3b8;
    }
}

.chart-container {
    width: 100%;
    height: 400px;
}

@media (min-width: 1024px) {
    .charts-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 768px) {
    .statistics-container {
        padding: 16px;
    }
    
    .page-title {
        font-size: 24px;
    }
    
    .page-subtitle {
        font-size: 13px;
    }
    
    .charts-grid {
        gap: 16px;
    }
    
    .chart-card {
        padding: 16px;
    }
    
    .card-title {
        font-size: 16px;
    }
    
    .card-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }
    
    .time-selector {
        width: 100%;
    }
    
    .chart-container {
        height: 300px;
    }
}

@media (max-width: 480px) {
    .statistics-container {
        padding: 12px;
    }
    
    .page-title {
        font-size: 20px;
    }
    
    .page-header {
        margin-bottom: 20px;
    }
    
    .chart-card {
        padding: 12px;
        border-radius: 12px;
    }
    
    .chart-container {
        height: 250px;
    }
}
</style>