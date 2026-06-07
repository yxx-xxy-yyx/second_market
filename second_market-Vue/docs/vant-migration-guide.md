# Vant 组件库迁移方案

## 当前使用情况

### 已导入的 Vant 组件 (main.js)
- Button, Field, Cell, CellGroup
- Dialog, Toast
- Skeleton, List, PullRefresh
- Empty, Loading

### 使用 Vant 的页面
1. Profile.vue - showToast
2. SystemMsgList.vue - showToast, showConfirmDialog
3. SystemMsgDetail.vue - showToast, showConfirmDialog
4. Cart.vue - 组件使用
5. ForumView.vue - 组件使用
6. AiChatView.vue - showToast, showConfirmDialog
7. MessageView.vue - showToast
8. ChatView.vue - showToast, showImagePreview
9. AddressView.vue - showToast, showSuccessToast
10. Profile.vue (mobile) - showToast
11. MyProducts.vue - 组件使用
12. Favorites.vue - 组件使用
13. MyOrders.vue - 组件使用
14. AiChatView.vue (mobile) - showToast, showConfirmDialog

## 迁移策略

### 第一阶段：替换 Toast/Dialog
使用 Element Plus 的 ElMessage 和 ElMessageBox 替换 Vant 的 Toast 和 Dialog

### 第二阶段：替换基础组件
使用 Element Plus 组件替换 Vant 的基础组件（Button, Field, Cell 等）

### 第三阶段：替换列表相关组件
使用 Element Plus 替换 Skeleton, List, PullRefresh 等

## Element Plus 替换对照

| Vant | Element Plus |
|------|-------------|
| Toast | ElMessage |
| Dialog | ElMessageBox |
| Button | ElButton |
| Field | ElInput |
| Cell | 可以用卡片或自定义组件 |
| Skeleton | 已有自定义 Skeleton 组件 |
| Empty | ElEmpty |
| Loading | ElLoading |

## 备注
由于这是课程作业，且 Vant 主要用于移动端视图，建议保持现状或逐步替换，避免影响现有功能。
