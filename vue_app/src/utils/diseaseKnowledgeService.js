import axios from 'axios'
import { diseaseKnowledge as fallbackKnowledge } from '../data/diseaseKnowledge'

const splitLines = (value) => {
  if (Array.isArray(value)) return value
  if (!value) return []
  return String(value)
    .split(/\r?\n|；|;/)
    .map((item) => item.trim())
    .filter(Boolean)
}

const joinLines = (value) => {
  if (Array.isArray(value)) return value.filter(Boolean).join('\n')
  return value || ''
}

export function normalizeKnowledge(item) {
  return {
    code: item.code,
    name: item.name,
    risk: item.risk || '低',
    intro: item.intro || '',
    symptoms: splitLines(item.symptoms),
    advice: splitLines(item.advice),
    cautions: splitLines(item.cautions),
    source: item.source || 'ISIC2019 数据集与皮肤科通用健康管理建议',
    version: item.version || 'v1.0',
    editor: item.editor || 'admin',
    reviewStatus: item.reviewStatus || '待审核',
    updatedTime: item.updatedTime || ''
  }
}

export function serializeKnowledge(item) {
  return {
    code: item.code,
    name: item.name,
    risk: item.risk,
    intro: item.intro,
    symptoms: joinLines(item.symptoms),
    advice: joinLines(item.advice),
    cautions: joinLines(item.cautions),
    source: item.source,
    version: item.version,
    editor: item.editor,
    reviewStatus: item.reviewStatus,
    updatedTime: item.updatedTime
  }
}

export async function fetchDiseaseKnowledge() {
  try {
    const response = await axios.get('/spring_api/disease-knowledge')
    const list = (response.data || []).map(normalizeKnowledge)
    return list.length ? list : fallbackKnowledge
  } catch (error) {
    return fallbackKnowledge
  }
}

export async function saveDiseaseKnowledge(item) {
  const response = await axios.put(`/spring_api/disease-knowledge/${item.code}`, serializeKnowledge(item))
  return normalizeKnowledge(response.data)
}

export function createDiseaseMap(list = fallbackKnowledge) {
  return list.reduce((map, item) => {
    map[item.code] = normalizeKnowledge(item)
    return map
  }, {})
}

export function findDisease(list, code) {
  return createDiseaseMap(list)[code] || null
}
