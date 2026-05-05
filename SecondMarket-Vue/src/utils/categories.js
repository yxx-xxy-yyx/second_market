export const CATEGORIES = [
  { id: 'electronics', dbValue: '电子产品' },
  { id: 'appliances', dbValue: '家用电器' },
  { id: 'clothing', dbValue: '服装配饰' },
  { id: 'books', dbValue: '图书音像' },
  { id: 'sports', dbValue: '运动户外' },
  { id: 'beauty', dbValue: '美妆护肤' },
  { id: 'toys', dbValue: '母婴玩具' },
  { id: 'others', dbValue: '其他' }
]

export function categoryIdToDbValue(categoryId) {
  return CATEGORIES.find((c) => c.id === categoryId)?.dbValue || ''
}

export function normalizeCategory(category) {
  if (!category) return ''
  const byId = CATEGORIES.find((c) => c.id === category)
  if (byId) return byId.id
  const byDbValue = CATEGORIES.find((c) => c.dbValue === category)
  if (byDbValue) return byDbValue.id
  return ''
}
