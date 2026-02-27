<template>
    <div class="layout-box">
      <el-container>
        <el-header class="header">
            <div class="header-content">
                <div class="logo-section">
                    <img id="logo" src="../assets/logo.png" alt="Logo">
                    <div class="brand-text">
                        <h1 class="brand-title">Intelligent Medicine</h1>
                        <p class="brand-subtitle">Health is better than wealth</p>
                    </div>
                </div>
                <div class="auth-buttons">
                    <el-button class="login-btn" @click="goLogin">登录</el-button>
                    <el-button class="sign-btn" type="primary" @click="goSignUp">注册</el-button>
                </div>
            </div>
        </el-header>
        <el-main class="main-content">
            <div class="carousel-wrapper">
                <el-carousel height="60vh" :interval="5000" arrow="hover">
                    <el-carousel-item key=1>
                        <div class="chart-container">
                            <div id="trend" class="chart"></div>
                        </div>
                    </el-carousel-item>
                    <el-carousel-item key=2>
                        <div class="chart-container">
                            <div id="death" class="chart"></div>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </el-main>
      </el-container>

      <div class="dialog-box">
        <el-dialog  
            title="用户注册" 
            v-model="showSignUpDialog" 
            width="500px" 
            :align-center="true"
            class="register-dialog"
        >
            <el-form class="form-box" :model="newForm" :rules="rules" label-position="top">
                <el-form-item label="用户名" prop="username">
                    <el-input class="dialog_input" v-model="newForm.username" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input 
                        class="dialog_input" 
                        v-model="newForm.password" 
                        type="password" 
                        placeholder="请输入密码"
                        show-password
                    ></el-input>
                </el-form-item>

                <el-form-item label="性别" prop="gender">
                    <el-select v-model="newForm.gender" class="dialog_select" placeholder="请选择性别">
                        <el-option
                            v-for="item in genderOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="出生日期" prop="birthday">
                    <el-date-picker
                        v-model="newForm.birthday"
                        type="date"
                        placeholder="选择出生日期"
                        format="YYYY/MM/DD"
                        value-format="YYYY-MM-DD"
                        class="dialog_datepicker"
                    />
                </el-form-item>

                <el-form-item label="居住地" prop="district">
                    <el-input class="dialog_input" v-model="newForm.district" placeholder="请输入居住地"></el-input>
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="cancelHandle">取 消</el-button>
                    <el-button type="primary" @click="submit">确 定</el-button>
                </div>
            </template>
        </el-dialog>
      </div>
    </div>
  </template>
  

<script lang="ts">
import * as echarts from 'echarts/core';
import {ElMessageBox} from "element-plus";
import {
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  DatasetComponent
} from 'echarts/components';
import { BarChart } from 'echarts/charts';
import { PieChart } from 'echarts/charts';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';

export default {
    name: "home",
    data(){
        return {
            alignCenter: true,
            showSignUpDialog: false,
            newForm: {
                username: '',
                password: '',
                name: '',
                gender: '',
                birthday: '2000-01-01',
                district: "",
            },
            rules: {
                username: [
                    {required: true, message: '账号不能为空', trigger: 'blur'}
                ],
                password: [
                    {required: true, message: '密码不能为空', trigger: 'blur'}
                ],
                name: [
                    {required: true, message: '姓名不能为空', trigger: 'blur'}
                ],
                gender: [
                    {required: true, message: '性别不能为空', trigger: 'blur'}
                ],
                age: [
                    {required: true, message: '年龄不能为空', trigger: 'blur'}
                ]
            },
            genderOptions: [
                {
                    label: "女",
                    value: "女"
                },
                {
                    label: "男",
                    value: "男"
                }
            ],
        }
    },
    methods:{
        goSignUp(){
            this.showSignUpDialog = true
        },        
        submit() {
            let newUser = {
                'username': this.newForm.username,
                'password': this.newForm.password,
                'gender': this.newForm.gender,
                'birthday': this.newForm.birthday,
                'district': this.newForm.district
            }
        
                this.axios.post('/spring_api/user', newUser)
                    .then((response) => {
                        let res = response.data
                        if (res === "Success") {
                            ElMessageBox.alert("注册成功！", {
                                title: "提示",
                                confirmButtonText: "确定",
                                type: "success"
                            })
                            this.showSignUpDialog = false;
                        } else if (res === "Exist") {
                            ElMessageBox.alert("已存在相同账号，请换个账号名重试！", {
                                title: "提示",
                                confirmButtonText: "返回",
                                type: "warning"
                            })
                        } else {
                            ElMessageBox.alert("添加失败，稍后重试！或检查系统！", {
                                title: "提示",
                                confirmButtonText: "返回",
                                type: "error"
                            })
                        }
                    })
                    .catch((error) => {
                        console.log(error)
                        ElMessageBox.alert("添加失败，稍后重试！或检查系统！", {
                                title: "提示",
                                confirmButtonText: "返回",
                                type: "error"
                            })
                    })            

        },
        cancelHandle() {
            this.showSignUpDialog = false
        },
        goLogin(){
            this.$router.push('/')
        },
        drawTrend(){
            this.trendLine = echarts.init(document.getElementById('trend'));
            let option = {
                title: [
                    {
                        subtext: '1990-2019年中国皮肤疾病流行病学情况调查：发病率高达53%以上，流行率高达25%以上',
                        left: '50%',
                        bottom: '8%',
                        textAlign: 'center',
                        subtextStyle: {
                            fontSize: 16,
                            color: '#64748b',
                            fontWeight: 'normal'
                        }
                    },
                ],
                legend: {
                    textStyle:{
                        fontSize: 14,
                        color: '#64748b'
                    },
                    top: "5%",
                    itemWidth: 12,
                    itemHeight: 12
                },
                tooltip: {
                    backgroundColor: 'rgba(255, 255, 255, 0.98)',
                    borderColor: '#e2e8f0',
                    borderWidth: 1,
                    padding: [12, 16],
                    textStyle: {
                        color: '#1e293b',
                        fontSize: 14
                    }
                },
                grid: {
                    left: '10%',
                    right: '10%',
                    bottom: '20%',
                    top: '15%',
                    containLabel: true
                },
                dataset: {
                    source: [
                    ['product', '1990', '2019'],
                    ['发病人数', 606509566, 784395281],
                    ['患病人数', 290124075, 369127390],
                    ]
                },
                xAxis: {
                    data: ['发病人数', '患病人数'],
                    type: 'category', 
                    axisLabel:{
                        fontSize: 14,
                        color: '#64748b'
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#e2e8f0'
                        }
                    }
                },
                yAxis: {
                    axisLabel:{
                        fontSize: 14,
                        color: '#64748b'
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#e2e8f0'
                        }
                    },
                    splitLine:{
                        show: true,
                        lineStyle: {
                            color: '#f1f5f9',
                            width: 1,
                            type: 'dashed'
                        }
                    },
                },
                color: ['#0891B2', '#22C55E'],
                series: [
                    {
                        type: 'bar',
                        barGap: '20%',
                        barCategoryGap: '40%',
                        itemStyle: {
                            borderRadius: [8, 8, 0, 0]
                        },
                        label: {
                            show: true,
                            position: 'top',
                            textStyle: {
                                fontSize: 14,
                                color: '#1e293b',
                                fontWeight: '500'
                            },
                            formatter: function(params) {
                                return (params.value / 100000000).toFixed(2) + '亿';
                            }
                        },
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.3)'
                            }
                        }
                    },
                    {
                        type: 'bar',
                        itemStyle: {
                            borderRadius: [8, 8, 0, 0]
                        },
                        label: {
                            show: true,
                            position: 'top',
                            textStyle: {
                                fontSize: 14,
                                color: '#1e293b',
                                fontWeight: '500'
                            },
                            formatter: function(params) {
                                return (params.value / 100000000).toFixed(2) + '亿';
                            }
                        },
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.3)'
                            }
                        }
                    }
                ]
            };
            this.trendLine.setOption(option);
        },
        drawDeath(){
            this.deathPie = echarts.init(document.getElementById('death'));
            let option = {
                title: [
                    {
                        text: '黑色素瘤不同阶段的5年存活率',
                        left: '50%',
                        top: '3%',
                        textAlign: 'center',
                        textStyle: {
                            fontSize: 20,
                            color: '#1e293b',
                            fontWeight: '600'
                        }
                    },
                    {
                        subtext: 'I期\n中位生存期：5年',
                        left: '25%',
                        top: '45%',
                        textAlign: 'center',
                        subtextStyle: {
                            fontSize: 14,
                            color: '#22C55E',
                            fontWeight: '500',
                            lineHeight: 20
                        }
                    },
                    {
                        subtext: 'II期\n中位生存期：4.25年',
                        left: '75%',
                        top: '45%',
                        textAlign: 'center',
                        subtextStyle: {
                            fontSize: 14,
                            color: '#F59E0B',
                            fontWeight: '500',
                            lineHeight: 20
                        }
                    },
                    {
                        subtext: 'III期\n中位生存期：2.83年',
                        left: '25%',
                        top: '90%',
                        textAlign: 'center',
                        subtextStyle: {
                            fontSize: 14,
                            color: '#EF4444',
                            fontWeight: '500',
                            lineHeight: 20
                        }
                    },
                    {
                        subtext: 'IV期\n中位生存期：1.42年',
                        left: '75%',
                        top: '90%',
                        textAlign: 'center',
                        subtextStyle: {
                            fontSize: 14,
                            color: '#7C3AED',
                            fontWeight: '500',
                            lineHeight: 20
                        }
                    },
                ],
                legend: {
                    textStyle:{
                        fontSize: 14,
                        color: '#64748b'
                    },
                    top: "10%",
                    itemWidth: 12,
                    itemHeight: 12
                },
                tooltip: {
                    backgroundColor: 'rgba(255, 255, 255, 0.98)',
                    borderColor: '#e2e8f0',
                    borderWidth: 1,
                    padding: [12, 16],
                    textStyle: {
                        color: '#1e293b',
                        fontSize: 14
                    },
                    formatter: '{b}: {c}%'
                },
                color: ['#22C55E', '#EF4444'],
                series: [
                    {
                        type: 'pie',
                        radius: ['50%', '70%'],
                        center: ['25%', '30%'],
                        encode: {
                            itemName: 'stage',
                            value: 'I期'
                        },
                        label: {
                            show: true,
                            formatter: '{b}: {d}%',
                            fontSize: 13,
                            color: '#1e293b'
                        },
                        itemStyle: {
                            borderRadius: 5,
                            borderColor: '#fff',
                            borderWidth: 2
                        }
                    },
                    {
                        type: 'pie',
                        radius: ['50%', '70%'],
                        center: ['75%', '30%'],
                        encode: {
                            itemName: 'stage',
                            value: 'II期'
                        },
                        label: {
                            show: true,
                            formatter: '{b}: {d}%',
                            fontSize: 13,
                            color: '#1e293b'
                        },
                        itemStyle: {
                            borderRadius: 5,
                            borderColor: '#fff',
                            borderWidth: 2
                        }
                    },
                    {
                        type: 'pie',
                        radius: ['50%', '70%'],
                        center: ['25%', '75%'],
                        encode: {
                            itemName: 'stage',
                            value: 'III期'
                        },
                        label: {
                            show: true,
                            formatter: '{b}: {d}%',
                            fontSize: 13,
                            color: '#1e293b'
                        },
                        itemStyle: {
                            borderRadius: 5,
                            borderColor: '#fff',
                            borderWidth: 2
                        }
                    },
                    {
                        type: 'pie',
                        radius: ['50%', '70%'],
                        center: ['75%', '75%'],
                        encode: {
                            itemName: 'stage',
                            value: 'IV期'
                        },
                        label: {
                            show: true,
                            formatter: '{b}: {d}%',
                            fontSize: 13,
                            color: '#1e293b'
                        },
                        itemStyle: {
                            borderRadius: 5,
                            borderColor: '#fff',
                            borderWidth: 2
                        }
                    }
                ]
            };
            this.deathPie.setOption(option);
        }
    },
    mounted() {
        this.drawTrend()
        this.drawDeath()
    },
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;500;600;700&display=swap');
</style>

<style lang="less" scoped>
.layout-box {
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
    width: 100vw;
    min-height: 100vh;
    overflow: hidden;
    font-family: 'Noto Sans', sans-serif;
}

.header {
    height: 80px;
    padding: 0 32px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
}

.header-content {
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1400px;
    margin: 0 auto;
}

.logo-section {
    display: flex;
    align-items: center;
    gap: 16px;
}

#logo {
    height: 48px;
    width: auto;
}

.brand-text {
    display: flex;
    flex-direction: column;
}

.brand-title {
    font-size: 24px;
    font-weight: 700;
    color: #0891B2;
    margin: 0;
    line-height: 1.2;
}

.brand-subtitle {
    font-size: 13px;
    color: #64748b;
    margin: 2px 0 0 0;
    font-weight: 500;
    letter-spacing: 0.5px;
}

.auth-buttons {
    display: flex;
    gap: 12px;
}

.login-btn {
    padding: 10px 28px;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 600;
    border: 2px solid #0891B2;
    color: #0891B2;
    background: white;
    transition: all 0.3s ease;
    
    &:hover {
        background: #0891B2;
        color: white;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(8, 145, 178, 0.3);
    }
}

.sign-btn {
    padding: 10px 28px;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 600;
    background: linear-gradient(135deg, #0891B2 0%, #22C55E 100%);
    border: none;
    transition: all 0.3s ease;
    
    &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(8, 145, 178, 0.4);
    }
}

.main-content {
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: calc(100vh - 80px);
}

.carousel-wrapper {
    width: 90%;
    max-width: 1200px;
    margin: 0 auto;
}

:deep(.el-carousel) {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

:deep(.el-carousel__item) {
    background: rgba(255, 255, 255, 0.98);
    padding: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.chart-container {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.chart {
    width: 100%;
    height: 100%;
}

:deep(.el-carousel__arrow) {
    background: rgba(8, 145, 178, 0.9);
    width: 44px;
    height: 44px;
    border-radius: 50%;
    
    &:hover {
        background: #0891B2;
    }
    
    .el-icon {
        font-size: 20px;
    }
}

:deep(.el-carousel__indicators) {
    .el-carousel__indicator {
        .el-carousel__button {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: rgba(8, 145, 178, 0.3);
        }
        
        &.is-active .el-carousel__button {
            background: #0891B2;
            width: 24px;
            border-radius: 4px;
        }
    }
}

:deep(.register-dialog) {
    .el-dialog__header {
        padding: 24px 24px 16px;
        border-bottom: 1px solid #e2e8f0;
        
        .el-dialog__title {
            font-size: 20px;
            font-weight: 600;
            color: #1e293b;
        }
    }
    
    .el-dialog__body {
        padding: 24px;
    }
    
    .el-dialog__footer {
        padding: 16px 24px 24px;
        border-top: 1px solid #e2e8f0;
    }
}

.form-box {
    width: 100%;
}

:deep(.el-form-item__label) {
    color: #475569;
    font-weight: 500;
    font-size: 14px;
}

.dialog_input,
.dialog_select,
.dialog_datepicker {
    width: 100%;
    
    .el-input__wrapper,
    .el-select__wrapper {
        border-radius: 8px;
        padding: 8px 12px;
        border: 1px solid #e2e8f0;
        box-shadow: none;
        transition: all 0.2s ease;
        
        &:hover {
            border-color: #0891B2;
        }
        
        &.is-focus {
            border-color: #0891B2;
            box-shadow: 0 0 0 3px rgba(8, 145, 178, 0.1);
        }
    }
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    
    .el-button {
        padding: 10px 24px;
        border-radius: 8px;
        font-weight: 500;
        font-size: 14px;
    }
}

@media (max-width: 1024px) {
    .header {
        padding: 0 24px;
        height: 70px;
    }
    
    .brand-title {
        font-size: 20px;
    }
    
    .brand-subtitle {
        font-size: 12px;
    }
    
    .carousel-wrapper {
        width: 95%;
    }
}

@media (max-width: 768px) {
    .header {
        padding: 0 16px;
        height: 60px;
    }
    
    .logo-section {
        gap: 12px;
    }
    
    #logo {
        height: 40px;
    }
    
    .brand-title {
        font-size: 18px;
    }
    
    .brand-subtitle {
        display: none;
    }
    
    .auth-buttons {
        gap: 8px;
    }
    
    .login-btn,
    .sign-btn {
        padding: 8px 16px;
        font-size: 14px;
    }
    
    .carousel-wrapper {
        width: 100%;
        margin: 0;
    }
    
    :deep(.register-dialog) {
        .el-dialog {
            width: 95% !important;
            margin: 0 auto;
        }
    }
}

@media (max-width: 480px) {
    .header {
        padding: 0 12px;
        height: 56px;
    }
    
    #logo {
        height: 36px;
    }
    
    .brand-title {
        font-size: 16px;
    }
    
    .login-btn,
    .sign-btn {
        padding: 6px 12px;
        font-size: 13px;
    }
}
</style>