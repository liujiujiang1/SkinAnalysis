package edu.whut.skinhealth.service;

import edu.whut.skinhealth.dao.DiseaseKnowledgeRepository;
import edu.whut.skinhealth.entity.DiseaseKnowledge;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DiseaseKnowledgeService {
    private final DiseaseKnowledgeRepository diseaseKnowledgeRepository;

    public DiseaseKnowledgeService(DiseaseKnowledgeRepository diseaseKnowledgeRepository) {
        this.diseaseKnowledgeRepository = diseaseKnowledgeRepository;
    }

    public List<DiseaseKnowledge> getAll() {
        return diseaseKnowledgeRepository.findAllByOrderByCodeAsc();
    }

    public Optional<DiseaseKnowledge> getByCode(String code) {
        return diseaseKnowledgeRepository.findById(code);
    }

    public DiseaseKnowledge save(DiseaseKnowledge diseaseKnowledge) {
        diseaseKnowledge.setCode(diseaseKnowledge.getCode().trim().toUpperCase());
        if (diseaseKnowledge.getSource() == null || diseaseKnowledge.getSource().isBlank()) {
            diseaseKnowledge.setSource("ISIC2019 数据集与皮肤科通用健康管理建议");
        }
        if (diseaseKnowledge.getVersion() == null || diseaseKnowledge.getVersion().isBlank()) {
            diseaseKnowledge.setVersion("v1.0");
        }
        if (diseaseKnowledge.getEditor() == null || diseaseKnowledge.getEditor().isBlank()) {
            diseaseKnowledge.setEditor("admin");
        }
        if (diseaseKnowledge.getReviewStatus() == null || diseaseKnowledge.getReviewStatus().isBlank()) {
            diseaseKnowledge.setReviewStatus("待审核");
        }
        diseaseKnowledge.setUpdatedTime(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
        return diseaseKnowledgeRepository.save(diseaseKnowledge);
    }

    public void seedDefaultsIfEmpty() {
        if (diseaseKnowledgeRepository.count() > 0) {
            return;
        }
        diseaseKnowledgeRepository.saveAll(List.of(
                build("MEL", "黑色素瘤", "高",
                        "黑色素瘤是来源于黑色素细胞的恶性肿瘤，进展风险较高，需要尽早由皮肤科或肿瘤专科评估。",
                        "痣或色斑快速增大\n边界不规则或颜色不均\n出血、破溃、瘙痒或疼痛\n出现新的深色皮损",
                        "尽快线下就医\n避免抓挠或自行处理\n保存皮损变化照片\n遵医嘱完善皮肤镜或病理检查",
                        "模型结果不能替代病理诊断\n高危结果不要仅靠观察等待"),
                build("NV", "黑素细胞痣", "低",
                        "黑素细胞痣多为良性黑色素细胞增生，常见于各年龄段，通常进展缓慢。",
                        "棕色或黑色斑丘疹\n形态较规则\n长期稳定存在\n一般无明显疼痛",
                        "定期观察大小、颜色和边界变化\n做好防晒\n有变化时进行皮肤镜检查",
                        "不要反复摩擦或抠抓\n突然变化、出血或疼痛需就医"),
                build("BCC", "基底细胞癌", "中高",
                        "基底细胞癌是常见皮肤恶性肿瘤，转移少见但可局部侵袭，面部和日晒部位更常见。",
                        "珍珠样小结节\n表面毛细血管扩张\n反复结痂或破溃\n边缘隆起并缓慢扩大",
                        "尽早到皮肤科评估\n按医生建议手术或局部治疗\n长期随访复查",
                        "不要长期拖延不处理\n面部病灶需重视功能和外观影响"),
                build("AKIEC", "光化性角化病", "中",
                        "光化性角化病常与长期紫外线暴露有关，属于癌前病变，需要规范随访和治疗。",
                        "日晒部位粗糙鳞屑斑\n触摸有砂纸感\n红斑或角化增厚\n可有轻微疼痛或灼热",
                        "减少日晒并使用防晒\n到皮肤科确认诊断\n按医嘱选择冷冻、外用药或光动力治疗",
                        "多发或反复病灶要定期复诊\n变厚、破溃、疼痛需排查鳞癌风险"),
                build("BKL", "良性角化病", "低",
                        "良性角化病多为良性角质增生性皮损，常表现为边界清楚的斑块或丘疹。",
                        "褐色或肤色斑块\n表面粗糙或蜡样\n边界较清楚\n一般生长缓慢",
                        "无症状可观察\n影响外观或反复刺激可咨询去除\n注意与恶性皮损鉴别",
                        "短期快速增大或出血需就医\n不要自行腐蚀或切除"),
                build("DF", "皮肤纤维瘤", "低",
                        "皮肤纤维瘤是常见良性皮肤结节，可能与轻微外伤或炎症后反应有关。",
                        "坚实小结节\n颜色可为棕色或淡红色\n按压周围可出现凹陷征\n通常长期稳定",
                        "稳定无症状时观察即可\n疼痛或影响生活可就医处理\n记录大小变化",
                        "明显增大、溃破或形态异常需复诊\n避免频繁挤压刺激"),
                build("VASC", "血管病变", "低到中",
                        "血管病变包括血管瘤、毛细血管扩张等多类改变，多数为良性，但需结合部位和变化判断。",
                        "红色、紫红色或蓝紫色皮损\n按压颜色可能变浅\n可见血管样结构\n部分会出血或疼痛",
                        "观察大小、颜色和出血情况\n必要时皮肤科或血管专科评估\n可咨询激光等治疗方式",
                        "反复出血、快速增大或儿童特殊部位病灶需及时就医\n避免抓挠")
        ));
    }

    private DiseaseKnowledge build(String code, String name, String risk, String intro, String symptoms, String advice, String cautions) {
        DiseaseKnowledge item = new DiseaseKnowledge();
        item.setCode(code);
        item.setName(name);
        item.setRisk(risk);
        item.setIntro(intro);
        item.setSymptoms(symptoms);
        item.setAdvice(advice);
        item.setCautions(cautions);
        item.setSource("ISIC2019 数据集与皮肤科通用健康管理建议");
        item.setVersion("v1.0");
        item.setEditor("system");
        item.setReviewStatus("已审核");
        item.setUpdatedTime(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
        return item;
    }
}
