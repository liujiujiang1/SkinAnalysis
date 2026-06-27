const urgentDiseases = ['MEL', 'BCC']

export function buildRiskAssessment(result = {}) {
  const code = result.code || result.disease
  const probability = Number(result.probability || 0)

  if (code === 'MEL') {
    return {
      level: probability >= 40 ? '高风险' : '中风险',
      tagType: probability >= 40 ? 'danger' : 'warning',
      action: '建议尽快就医',
      advice: '黑色素瘤相关结果需要高度重视。建议尽快到皮肤科就诊，必要时完善皮肤镜或病理检查，不要自行切除、腐蚀或长期拖延观察。'
    }
  }

  if (code === 'BCC') {
    return {
      level: probability >= 50 ? '中高风险' : '中风险',
      tagType: 'warning',
      action: '建议皮肤科评估',
      advice: '基底细胞癌虽转移少见，但可能局部侵袭。建议尽快预约皮肤科评估，尤其是面部、反复破溃或持续增大的皮损。'
    }
  }

  if (code === 'AKIEC') {
    return {
      level: probability >= 60 ? '中风险' : '低到中风险',
      tagType: 'warning',
      action: '建议规范随访',
      advice: '光化性角化病与长期日晒相关，属于需要关注的癌前病变。建议做好防晒，结合皮肤科检查决定是否治疗。'
    }
  }

  if (probability < 60) {
    return {
      level: '低置信度',
      tagType: 'info',
      action: '建议重新拍摄',
      advice: '模型首要结果置信度偏低。建议重新上传清晰、光线均匀、主体完整的图片，或直接咨询医生。'
    }
  }

  return {
    level: '低风险',
    tagType: 'success',
    action: '建议定期观察',
    advice: '当前结果倾向低风险，但仍建议记录皮损大小、颜色、边界和症状变化；如出现快速增大、出血、疼痛或颜色异常，请及时就医。'
  }
}

export function riskTagType(level) {
  if (!level) return 'info'
  if (level.includes('高')) return 'danger'
  if (level.includes('中')) return 'warning'
  if (level.includes('低置信')) return 'info'
  return 'success'
}

export function hasUrgentSymptoms(text = '') {
  const keywords = ['出血', '破溃', '快速增大', '变大', '疼痛', '颜色变化', '不规则', '黑色素瘤', '恶性']
  return keywords.some((keyword) => text.includes(keyword))
}

export function recommendedQuestions(latestDiagnosis) {
  const base = [
    '这个结果需要多久复查一次？',
    '我应该如何拍摄更清晰的随访照片？',
    '哪些变化代表需要尽快就医？'
  ]
  const disease = latestDiagnosis?.disease
  if (urgentDiseases.includes(disease)) {
    return ['这个结果是否建议尽快到皮肤科？', '皮肤镜和病理检查有什么区别？', ...base.slice(1)]
  }
  return base
}
