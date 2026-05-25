export const CATEGORIES = [
  { 
    id: 'electronics', 
    dbValue: '电子产品',
    children: [
      { id: 'phone', name: '手机' },
      { id: 'laptop', name: '电脑' },
      { id: 'tablet', name: '平板' },
      { id: 'accessories', name: '数码配件' }
    ]
  },
  { 
    id: 'appliances', 
    dbValue: '家用电器',
    children: [
      { id: 'fan', name: '电风扇' },
      { id: 'kettle', name: '热水壶' },
      { id: 'lamp', name: '台灯' },
      { id: 'fridge', name: '小冰箱' }
    ]
  },
  { 
    id: 'clothing', 
    dbValue: '服装配饰',
    children: [
      { id: 'tops', name: '上衣' },
      { id: 'pants', name: '裤装' },
      { id: 'shoes', name: '鞋靴' },
      { id: 'bags', name: '包袋' }
    ]
  },
  { 
    id: 'books', 
    dbValue: '图书音像',
    children: [
      { id: 'textbook', name: '课本教材' },
      { id: 'novel', name: '文学小说' },
      { id: 'exam', name: '考试考证' },
      { id: 'other_books', name: '其他图书' }
    ]
  },
  { 
    id: 'sports', 
    dbValue: '运动户外',
    children: [
      { id: 'ball', name: '球类' },
      { id: 'fitness', name: '健身器材' },
      { id: 'outdoor', name: '户外装备' },
      { id: 'cycling', name: '骑行相关' }
    ]
  },
  { 
    id: 'beauty', 
    dbValue: '美妆护肤',
    children: [
      { id: 'skincare', name: '护肤' },
      { id: 'makeup', name: '彩妆' },
      { id: 'perfume', name: '香氛' },
      { id: 'tools', name: '美妆工具' }
    ]
  },
  { 
    id: 'toys', 
    dbValue: '母婴玩具',
    children: [
      { id: 'lego', name: '乐高/拼插' },
      { id: 'plush', name: '毛绒玩具' },
      { id: 'game', name: '桌面游戏' },
      { id: 'others_toys', name: '其他玩具' }
    ]
  },
  { 
    id: 'others', 
    dbValue: '其他',
    children: [
      { id: 'daily', name: '日用品' },
      { id: 'stationery', name: '文具' },
      { id: 'instrument', name: '乐器' },
      { id: 'collection', name: '收藏' }
    ]
  }
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
