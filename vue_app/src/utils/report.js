import { diseaseName as fallbackDiseaseName } from '../data/diseaseKnowledge'

const escapeHtml = (value) => String(value ?? '')
  .replace(/&/g, '&amp;')
  .replace(/</g, '&lt;')
  .replace(/>/g, '&gt;')
  .replace(/"/g, '&quot;')

export function parseTopResults(value) {
  if (!value) return []
  try {
    return JSON.parse(value)
  } catch (error) {
    return []
  }
}

export function formatProbability(value) {
  return Number(value || 0).toFixed(2)
}

export function formatTime(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

export function buildDiagnosisReportHtml(record, options = {}) {
  const diseaseName = options.diseaseName || fallbackDiseaseName
  const topResults = parseTopResults(record.topResults)
  const userName = record.user?.username || record.username || sessionStorage.getItem('user_name') || '-'
  const title = `${diseaseName(record.disease)}诊断报告`

  return `<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <title>${escapeHtml(title)}</title>
  <style>
    body { margin: 0; padding: 32px; font-family: Arial, "Microsoft YaHei", sans-serif; color: #0f172a; background: #f8fafc; }
    .report { max-width: 920px; margin: 0 auto; background: #fff; border: 1px solid #e2e8f0; border-radius: 8px; padding: 28px; }
    h1 { margin: 0 0 8px; color: #134e4a; }
    .meta { color: #64748b; margin-bottom: 22px; }
    .hero { display: grid; grid-template-columns: 280px 1fr; gap: 24px; align-items: start; }
    img { width: 100%; max-height: 360px; object-fit: contain; border: 1px solid #e2e8f0; border-radius: 8px; background: #f8fafc; }
    .result { padding: 16px; border-radius: 8px; background: #f0fdfa; border: 1px solid #99f6e4; }
    .result strong { display: block; font-size: 28px; color: #0f766e; margin: 6px 0; }
    .risk { margin-top: 12px; padding: 12px; border-radius: 8px; background: #fffbeb; color: #92400e; line-height: 1.7; }
    .risk b { display: block; color: #78350f; margin-bottom: 4px; }
    .bar { height: 9px; background: #e2e8f0; border-radius: 999px; overflow: hidden; margin: 6px 0 12px; }
    .bar span { display: block; height: 100%; background: #0891b2; }
    section { margin-top: 24px; }
    h2 { font-size: 18px; margin: 0 0 10px; color: #0f172a; }
    p { white-space: pre-line; line-height: 1.7; color: #334155; }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #e2e8f0; text-align: left; }
    th { background: #f8fafc; color: #475569; }
    .notice { margin-top: 24px; padding: 12px 14px; border-radius: 8px; background: #fffbeb; color: #92400e; line-height: 1.7; }
    @media print { body { background: #fff; padding: 0; } .report { border: none; } }
    @media (max-width: 720px) { body { padding: 12px; } .hero { grid-template-columns: 1fr; } }
  </style>
</head>
<body>
  <main class="report">
    <h1>${escapeHtml(title)}</h1>
    <div class="meta">用户：${escapeHtml(userName)} · 生成时间：${escapeHtml(formatTime(new Date()))} · 诊断时间：${escapeHtml(formatTime(record.time))}</div>
    <div class="hero">
      ${record.imageData ? `<img src="${record.imageData}" alt="诊断图片" />` : '<div>暂无图片</div>'}
      <div class="result">
        <span>模型首要判断</span>
        <strong>${escapeHtml(diseaseName(record.disease))}</strong>
        <div>置信度 ${escapeHtml(formatProbability(record.probability))}%</div>
        <div class="bar"><span style="width:${Math.min(100, Number(record.probability || 0))}%"></span></div>
        <small>本结果由皮肤图像识别模型生成，仅作为健康管理参考。</small>
        <div class="risk"><b>风险分级：${escapeHtml(record.riskLevel || '未分级')}</b>${escapeHtml(record.riskAdvice || '暂无就医提醒')}</div>
      </div>
    </div>
    <section>
      <h2>Top 3 结果</h2>
      <table>
        <thead><tr><th>疾病</th><th>概率</th></tr></thead>
        <tbody>
          ${topResults.map((item) => `<tr><td>${escapeHtml(item.name || diseaseName(item.code))}</td><td>${escapeHtml(item.probability)}%</td></tr>`).join('')}
        </tbody>
      </table>
    </section>
    <section>
      <h2>疾病简介</h2>
      <p>${escapeHtml(record.adviceBrief || '暂无简介')}</p>
    </section>
    <section>
      <h2>建议</h2>
      <p>${escapeHtml(record.adviceTreatment || '暂无建议')}</p>
    </section>
    <div class="notice">免责声明：本报告不能替代医生面诊、皮肤镜检查或病理诊断。如皮损快速增大、出血、疼痛、破溃或颜色明显变化，请及时就医。</div>
  </main>
</body>
</html>`
}

export function downloadDiagnosisReport(record, options = {}) {
  const diseaseName = options.diseaseName || fallbackDiseaseName
  const html = buildDiagnosisReportHtml(record, options)
  const blob = new Blob([html], { type: 'text/html;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  const time = new Date(record.time || Date.now()).toISOString().slice(0, 10)
  link.href = url
  link.download = `${diseaseName(record.disease)}-诊断报告-${time}.html`
  link.click()
  URL.revokeObjectURL(url)
}
