<template>
    <div class="layout" v-loading="loading" element-loading-text="分析中..." element-loading-background="rgba(255, 255, 255, 0.9)">
        <div class="main-container">
            <div class="results-section">
                <div class="section-header">
                    <h3>诊断结果</h3>
                </div>
                <div class="results-list">
                    <div 
                        v-for="(result, index) in results" 
                        :key="index" 
                        class="result-item"
                        :class="{ 'active': selectedIndex === index }"
                        @click="selectResult(index)"
                    >
                        <div class="result-rank">Top{{ index + 1 }}</div>
                        <div class="result-info">
                            <div class="disease-name">{{ result.name }}</div>
                            <div class="probability">{{ result.probability }}%</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="image-section">
                <div class="section-header">
                    <h3>诊断图片</h3>
                </div>
                <div class="image-container">
                    <el-image 
                        :src="fileList.url" 
                        fit="contain"
                        class="diagnosis-image"
                    >
                        <template #error>
                            <div class="image-placeholder">
                                <el-icon size="64"><Picture /></el-icon>
                                <p>请上传图片</p>
                            </div>
                        </template>
                    </el-image>
                </div>
                <div class="upload-section">
                    <el-upload
                        ref="upload"
                        class="upload-demo"
                        action="/flask_api/detect"
                        :limit="1"
                        :on-exceed="handleExceed"
                        :auto-upload="false"
                        :on-change="handleChange"
                        :on-success="handleSuccess"
                        :on-error="handleError"
                    >
                        <template #trigger>
                            <el-button type="primary" size="large" class="select-btn">
                                <el-icon class="btn-icon"><Upload /></el-icon>
                                选择图片
                            </el-button>
                        </template>
                        <el-button type="success" size="large" @click="submitUpload" class="upload-btn">
                            <el-icon class="btn-icon"><UploadFilled /></el-icon>
                            开始分析
                        </el-button>
                    </el-upload>
                    <p class="upload-tip">仅支持单个文件，新上传的文件将覆盖之前的内容</p>
                </div>
            </div>

            <div class="advice-section">
                <div class="section-header">
                    <h3>诊疗建议</h3>
                </div>
                <div class="advice-content" v-if="adviceDisplay">
                    <div class="advice-card">
                        <div class="card-header">
                            <el-icon class="header-icon"><Document /></el-icon>
                            疾病简介
                        </div>
                        <div class="card-body brief-content">{{ adviceDisplay.brief }}</div>
                    </div>
                    <div class="advice-card">
                        <div class="card-header">
                            <el-icon class="header-icon"><FirstAidKit /></el-icon>
                            治疗方法
                        </div>
                        <div class="card-body treatment-content">{{ adviceDisplay.treatment }}</div>
                    </div>
                </div>
                <div class="advice-placeholder" v-else>
                    <el-icon size="48"><Document /></el-icon>
                    <p>上传图片后将显示诊疗建议</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {ElMessageBox} from "element-plus";
import { Picture, Upload, UploadFilled, Document, FirstAidKit } from '@element-plus/icons-vue';
export default {
    components: {
        Picture,
        Upload,
        UploadFilled,
        Document,
        FirstAidKit
    },
    name: "analyse",
    data() {
        return {
            loading: false,
            fileList: {
                name: '',
                url: ''
            },
            selectedIndex: 0,
            results: [
                { name: '尚无结果', probability: 0 },
                { name: '尚无结果', probability: 0 },
                { name: '尚无结果', probability: 0 }
            ],
            id_to_class : {0: 'MEL', 1: 'NV', 2: 'BCC', 3: 'AKIEC', 4: 'BKL', 5: 'DF', 6: 'VASC'},
            disease: {
                'MEL': '黑色素瘤',
                'NV': '黑素细胞痣',
                'BCC': '基底细胞癌',
                'AKIEC': '光化性角化病',
                'BKL': '良性角化病',
                'DF': '皮肤纤维瘤',
                'VASC': '血管病变',
            },
            adviceDisplay: null,
            adviceList: [
                {
                    brief: "黑色素瘤，通常是指恶性黑色素瘤，是黑色素细胞来源的一种高度恶性的肿瘤，多发生于皮肤，也可见于黏膜和内脏。" +
                        "好发于成人，极少见于儿童。 恶性黑色素瘤可由先天性或获得性良性黑素细胞痣演变而成，或由发育不良性痣恶变而来，也可以是新发生的。",
                    treatment: "手术切除\n干扰素；\n化疗放射治疗"
                },
                {
                    brief: "黑色素痣是由一群良性的黑色素细胞，聚集在表皮与真皮的交界产生的。" +
                        "黑色素细胞可能会分布在网状真皮下部，结缔组织束之间，围绕皮肤的其它附属器官如汗腺、毛囊、血管、神经等等，偶尔还会延伸到皮下脂肪。",
                    treatment: "在医学上黑色素痣属于良性的肿瘤，一般而言并不需要治疗。（想要去除黑色素痣，常用的方式有，手术切除、雷射治疗、电波刀切除等。）"
                },
                {
                    brief:"基底细胞癌多见于老年人，好发于头、面、颈及手背等处。最初是皮肤色到暗褐色浸润的小结节，较典型者为蜡样、半透明状结节，有高起卷曲的边缘。" +
                        "中央开始破溃，结黑色坏死性痂，中心坏死向深部组织扩展蔓延，呈大片状侵袭性坏死，可以深达软组织和骨组织。",
                    treatment: "手术切除；\nX线照射；\n激光治疗；"
                },
                {
                    brief: "光化性角化病是一种职业病，主要受日光、紫外线、放射性热能以及沥青或煤及其提炼而物诱发本病。" +
                        "病损多见于中年以上男性日光暴露部位，如面部、耳廓、手背等。主要表现为表面粗糙，可见角化性鳞屑。揭去鳞屑，可见下方的基面红润，凹凸不平，呈乳头状。",
                    treatment: "口服：B-顺维甲酸；芳香维甲酸依曲替酯\n"+"外涂：氨苯甲酸制剂；维甲酸软膏；氟尿嘧啶软膏"
                },
                {
                    brief: "角化病的病因是角化毛孔被角栓闭塞，呈毛孔性角化小丘疹。" +
                        "部分病人有甲状腺机能低下，或有库辛氏症候群。也有一部分的病人是使用皮质类固醇以后，才发生此种皮肤病。常见于异位性倾向的病人，或遗传性，多发于同一家族。",
                    treatment: "口服：青霉胺\n" +
                        "外涂：二巯基丙醇软膏；皮质类固醇霜\n" +
                        "注射：硫代硫酸钠（静注）；二巯基丙磺酸钠（肌注）"
                },
                {
                    brief:"皮肤纤维瘤是成纤维细胞或组织细胞灶性增生引致的一种真皮内的良性肿瘤。" +
                        "本病可发生于任何年龄，中青年多见，女性多于男性。可自然发生或外伤后引起。黄褐色或淡红色的皮内丘疹或结节是本病的临床特征。病损生长缓慢，长期存在，极少自行消退。",
                    treatment:"一般不需治疗，少数损害数年内可消退。（若单个损害有疼痛引起病人痛苦时可行手术切除。皮质类固醇激素皮内注射有一定的疗效）"
                },
                {
                    brief: "血管病变是皮肤和下层组织相对常见的异常，影响多达50%的18岁及以上的女性。" +
                        "血管病变包括一系列病症，无论是腿部静脉，面部静脉，血管瘤或静脉湖。蜘蛛静脉，毛细血管扩张和弥漫性发红通常是衰老，日晒或怀孕的结果，有时也是遗传造成的。",
                    treatment: "激光和基于光的设备是安全有效地消除和减少不必要的血管问题的可行治疗选择。"
                }
            ],
        };
    },
    methods: {
        handleChange(file){
            let url = null
            url = URL.createObjectURL(file.raw)
            this.fileList.url = URL.createObjectURL(file.raw)
            this.fileList.name = file.raw.name
        },
        handleExceed(files){
            this.$refs['upload'].clearFiles()
            this.$refs['upload'].handleStart(files[0])
        },
        handleSuccess(response){
            const descend = (a, b)=>{
                return b['probability'] - a['probability'];
            }
            console.log(response)
            let res = response.sort(descend)

            this.loading = true
            setTimeout(()=>{
                this.loading = false;
                this.results[0] = { 
                    name: this.disease[this.id_to_class[res[0].id]], 
                    probability: (res[0].probability * 100).toFixed(2) 
                };
                this.results[1] = { 
                    name: this.disease[this.id_to_class[res[1].id]], 
                    probability: (res[1].probability * 100).toFixed(2) 
                };
                this.results[2] = { 
                    name: this.disease[this.id_to_class[res[2].id]], 
                    probability: (res[2].probability * 100).toFixed(2) 
                };
                
                this.adviceList[parseInt(res[0].id)] = this.adviceList[parseInt(res[0].id)] || this.adviceList[0];
                this.adviceList[parseInt(res[1].id)] = this.adviceList[parseInt(res[1].id)] || this.adviceList[1];
                this.adviceList[parseInt(res[2].id)] = this.adviceList[parseInt(res[2].id)] || this.adviceList[2];
                
                this.selectedIndex = 0;
                this.selectResult(0);
                
                this.sendRecord(this.id_to_class[res[0].id])
            }, 500)
        },
        handleError(){
            ElMessageBox.alert("上传失败，请稍后重试或联系管理员！",{
                title:"提示",
                confirmButtonText:"确定",
                type:"error"
            })
        },
        submitUpload(){
            this.$refs['upload'].submit();
        },
        selectResult(index){
            this.selectedIndex = index;
            const adviceIndex = this.results[index].name === '尚无结果' ? 0 : 
                Object.keys(this.disease).find(key => this.disease[key] === this.results[index].name);
            this.adviceDisplay = this.adviceList[parseInt(adviceIndex)] || this.adviceList[0];
        },
        async sendRecord(disease){
            this.axios.post(
                '/spring_api/record',
            {
                'username': sessionStorage.getItem('user_name'),
                'disease': disease
            },
            {
              headers: {
                'Content-Type': 'application/json',
              }
            }
          );
        }
    }
};
</script>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Noto+Sans:wght@400;500;600;700&display=swap');
</style>

<style lang="less" scoped>
    .layout {
        width: 100%;
        height: 100%;
        font-family: 'Noto Sans', sans-serif;
        display: flex;
        flex-direction: column;
    }

    .main-container {
        display: flex;
        flex-direction: column;
        gap: 20px;
        height: 100%;
        padding: 16px;
        overflow-y: auto;
    }

    .results-section {
        order: 2;
    }

    .image-section {
        order: 1;
    }

    .advice-section {
        order: 3;
    }

    .section-header {
        padding: 12px 0;
        border-bottom: 2px solid #0891B2;
        margin-bottom: 16px;
    }

    .section-header h3 {
        font-size: 18px;
        font-weight: 600;
        color: #134E4A;
        margin: 0;
    }

    .results-section {
        background: white;
        border-radius: 12px;
        padding: 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }

    .results-list {
        display: flex;
        flex-direction: column;
        gap: 12px;
    }

    .result-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px;
        border-radius: 10px;
        background: #F8FAFC;
        border: 2px solid transparent;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .result-item:hover {
        background: #F0FDFA;
        border-color: #22D3EE;
    }

    .result-item.active {
        background: linear-gradient(135deg, #0891B2 0%, #22D3EE 100%);
        border-color: #0891B2;
    }

    .result-item.active .disease-name,
    .result-item.active .probability {
        color: white;
    }

    .result-rank {
        background: #0891B2;
        color: white;
        padding: 4px 10px;
        border-radius: 6px;
        font-weight: 600;
        font-size: 12px;
        min-width: 48px;
        text-align: center;
    }

    .result-item.active .result-rank {
        background: rgba(255, 255, 255, 0.2);
    }

    .result-info {
        flex: 1;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .disease-name {
        font-weight: 500;
        color: #134E4A;
        font-size: 14px;
    }

    .probability {
        font-weight: 700;
        color: #0891B2;
        font-size: 14px;
    }

    .image-section {
        background: white;
        border-radius: 12px;
        padding: 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        flex: 1;
    }

    .image-container {
        width: 100%;
        min-height: 300px;
        max-height: 400px;
        border: 2px dashed #E2E8F0;
        border-radius: 12px;
        overflow: hidden;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #F8FAFC;
        margin-bottom: 16px;
    }

    .diagnosis-image {
        width: 100%;
        height: 100%;
    }

    .image-placeholder {
        text-align: center;
        color: #94A3B8;
    }

    .image-placeholder p {
        margin-top: 12px;
        font-size: 14px;
    }

    .upload-section {
        display: flex;
        flex-direction: column;
        gap: 12px;
        align-items: center;
    }

    .upload-demo {
        width: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
    }

    .upload-demo :deep(.el-upload) {
        width: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
    }

    .select-btn,
    .upload-btn {
        width: 100%;
        max-width: 200px;
        height: 48px;
        border-radius: 10px;
        font-weight: 600;
        font-size: 15px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
    }

    .btn-icon {
        font-size: 18px;
    }

    .upload-tip {
        font-size: 12px;
        color: #64748B;
        text-align: center;
        margin: 0;
    }

    .advice-section {
        background: white;
        border-radius: 12px;
        padding: 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }

    .advice-content {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .advice-card {
        border: 1px solid #E2E8F0;
        border-radius: 10px;
        overflow: hidden;
    }

    .card-header {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 16px;
        background: #F0FDFA;
        font-weight: 600;
        color: #134E4A;
        font-size: 15px;
        border-bottom: 1px solid #E2E8F0;
    }

    .header-icon {
        font-size: 18px;
        color: #0891B2;
    }

    .card-body {
        padding: 16px;
        font-size: 14px;
        line-height: 1.6;
        color: #475569;
        white-space: pre-line;
    }

    .advice-placeholder {
        text-align: center;
        padding: 48px 24px;
        color: #94A3B8;
    }

    .advice-placeholder p {
        margin-top: 12px;
        font-size: 14px;
    }

    @media (min-width: 768px) {
        .main-container {
            flex-direction: row;
            padding: 24px;
            gap: 24px;
        }

        .results-section {
            width: 280px;
            flex-shrink: 0;
            order: 1;
        }

        .image-section {
            flex: 1;
            order: 2;
        }

        .advice-section {
            width: 320px;
            flex-shrink: 0;
            order: 3;
        }

        .section-header h3 {
            font-size: 20px;
        }

        .result-item {
            padding: 14px;
        }

        .disease-name {
            font-size: 15px;
        }

        .probability {
            font-size: 15px;
        }

        .image-container {
            min-height: 400px;
            max-height: 500px;
        }

        .card-body {
            font-size: 15px;
        }
    }

    @media (min-width: 1024px) {
        .results-section {
            width: 320px;
        }

        .advice-section {
            width: 380px;
        }

        .main-container {
            padding: 32px;
            gap: 32px;
        }
    }

    @media (max-width: 480px) {
        .main-container {
            padding: 12px;
            gap: 16px;
        }

        .image-section {
            order: -1;
        }

        .results-section,
        .image-section,
        .advice-section {
            padding: 12px;
            border-radius: 10px;
        }

        .section-header h3 {
            font-size: 16px;
        }

        .result-item {
            padding: 10px;
            gap: 8px;
        }

        .result-rank {
            padding: 3px 8px;
            font-size: 11px;
            min-width: 40px;
        }

        .disease-name {
            font-size: 13px;
        }

        .probability {
            font-size: 13px;
        }

        .image-container {
            min-height: 200px;
            max-height: 300px;
        }

        .select-btn,
        .upload-btn {
            height: 44px;
            font-size: 14px;
        }

        .card-header {
            padding: 10px 12px;
            font-size: 14px;
        }

        .card-body {
            padding: 12px;
            font-size: 13px;
        }
    }

    @media (prefers-reduced-motion: reduce) {
        *,
        *::before,
        *::after {
            animation-duration: 0.01ms !important;
            animation-iteration-count: 1 !important;
            transition-duration: 0.01ms !important;
        }
    }
</style>
