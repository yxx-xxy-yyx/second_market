<template>
  <div class="role-manage-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="title">角色权限管理</h1>
      <button class="add-btn" @click="showAddModal = true">
        <PlusOutlined />
        新增角色
      </button>
    </div>

    <!-- 角色列表 -->
    <div class="role-grid">
      <div 
        class="role-card" 
        v-for="role in roleList" 
        :key="role.id"
        @click="selectRole(role)"
        :class="{ active: selectedRole?.id === role.id }"
      >
        <div class="role-header">
          <div class="role-icon" :style="{ background: role.color }">
            <component :is="getIconComponent(role.icon)" />
          </div>
          <div class="role-info">
            <h3 class="role-name">{{ role.name }}</h3>
            <span class="role-code">{{ role.code }}</span>
          </div>
          <div class="role-status" :class="{ active: role.status === 1 }">
            {{ role.status === 1 ? '启用' : '禁用' }}
          </div>
        </div>
        <p class="role-desc">{{ role.description }}</p>
        <div class="role-footer">
          <span class="user-count">
            <UserOutlined />
            {{ role.userCount }} 个用户
          </span>
          <div class="role-actions">
            <button class="action-btn edit" @click.stop="editRole(role)">
              <EditOutlined />
            </button>
            <button class="action-btn delete" @click.stop="deleteRole(role)">
              <DeleteOutlined />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 权限配置面板 -->
    <div class="permission-panel" v-if="selectedRole">
      <div class="panel-header">
        <h2>{{ selectedRole.name }} - 权限配置</h2>
        <button class="save-btn" @click="savePermissions">
          <SaveOutlined />
          保存权限
        </button>
      </div>

      <div class="permission-tree">
        <div 
          class="permission-group" 
          v-for="module in permissionTree" 
          :key="module.id"
        >
          <div class="group-header" @click="toggleModule(module.id)">
            <component :is="getIconComponent(module.icon)" />
            <span class="group-name">{{ module.name }}</span>
            <span class="group-code">{{ module.code }}</span>
            <DownOutlined :class="{ rotated: expandedModules.includes(module.id) }" />
          </div>
          <div class="group-children" v-show="expandedModules.includes(module.id)">
            <div 
              class="permission-item" 
              v-for="child in module.children" 
              :key="child.id"
            >
              <input 
                type="checkbox" 
                :id="child.id"
                :checked="selectedPermissions.includes(child.id)"
                @change="togglePermission(child.id)"
              />
              <label :for="child.id">{{ child.name }}</label>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑角色弹窗 -->
    <div class="modal-overlay" v-if="showAddModal || showEditModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑角色' : '新增角色' }}</h3>
          <button class="close-btn" @click="closeModal">
            <CloseOutlined />
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>角色名称</label>
            <input type="text" v-model="roleForm.name" placeholder="请输入角色名称" />
          </div>
          <div class="form-group">
            <label>角色编码</label>
            <input type="text" v-model="roleForm.code" placeholder="请输入角色编码" />
          </div>
          <div class="form-group">
            <label>角色描述</label>
            <textarea v-model="roleForm.description" placeholder="请输入角色描述" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>角色颜色</label>
            <div class="color-picker">
              <div 
                class="color-option" 
                v-for="color in colorOptions" 
                :key="color"
                :style="{ background: color }"
                :class="{ selected: roleForm.color === color }"
                @click="roleForm.color = color"
              ></div>
            </div>
          </div>
          <div class="form-group">
            <label>角色图标</label>
            <div class="icon-picker">
              <div 
                class="icon-option" 
                v-for="icon in iconOptions" 
                :key="icon"
                :class="{ selected: roleForm.icon === icon }"
                @click="roleForm.icon = icon"
              >
                <component :is="getIconComponent(icon)" />
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>状态</label>
            <div class="status-toggle">
              <button 
                :class="{ active: roleForm.status === 1 }"
                @click="roleForm.status = 1"
              >
                启用
              </button>
              <button 
                :class="{ active: roleForm.status === 0 }"
                @click="roleForm.status = 0"
              >
                禁用
              </button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeModal">取消</button>
          <button class="confirm-btn" @click="submitRole">
            {{ showEditModal ? '更新' : '创建' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div class="loading-overlay" v-if="loading">
      <div class="loading-spinner"></div>
      <span>数据加载中...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { 
  PlusOutlined, EditOutlined, DeleteOutlined, SaveOutlined,
  UserOutlined, CloseOutlined, DownOutlined,
  CrownOutlined, SettingOutlined, SafetyOutlined,
  FileOutlined, AppstoreOutlined, TeamOutlined
} from '@ant-design/icons-vue'
import { getRoleListApi, createRoleApi, updateRoleApi, deleteRoleApi, getRolePermissionsApi, updateRolePermissionsApi, getPermissionTreeApi } from '@/api/role'
import { message } from 'ant-design-vue'

const loading = ref(false)
const roleList = ref([])
const permissionTree = ref([])
const selectedRole = ref(null)
const selectedPermissions = ref([])
const expandedModules = ref([])

// 弹窗控制
const showAddModal = ref(false)
const showEditModal = ref(false)

// 表单数据
const roleForm = ref({
  name: '',
  code: '',
  description: '',
  color: '#4F46E5',
  icon: 'UserOutlined',
  status: 1
})

// 颜色选项
const colorOptions = [
  '#4F46E5', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
  '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1'
]

// 图标选项
const iconOptions = [
  'CrownOutlined', 'UserOutlined', 'TeamOutlined', 'SafetyOutlined',
  'SettingOutlined', 'FileOutlined', 'AppstoreOutlined'
]

// 获取图标组件
const getIconComponent = (iconName) => {
  const icons = {
    CrownOutlined: CrownOutlined,
    UserOutlined: UserOutlined,
    TeamOutlined: TeamOutlined,
    SafetyOutlined: SafetyOutlined,
    SettingOutlined: SettingOutlined,
    FileOutlined: FileOutlined,
    AppstoreOutlined: AppstoreOutlined,
    UserFilled: UserOutlined
  }
  return icons[iconName] || UserOutlined
}

// 加载角色列表
const loadRoleList = async () => {
  try {
    const res = await getRoleListApi()
    if (res.code === '200' && res.data) {
      roleList.value = res.data
    }
  } catch (e) {
    console.error('加载角色列表失败', e)
  }
}

// 加载权限树
const loadPermissionTree = async () => {
  try {
    const res = await getPermissionTreeApi()
    if (res.code === '200' && res.data) {
      permissionTree.value = res.data
      // 默认展开所有模块
      expandedModules.value = res.data.map(m => m.id)
    }
  } catch (e) {
    console.error('加载权限树失败', e)
  }
}

// 选择角色
const selectRole = async (role) => {
  selectedRole.value = role
  try {
    const res = await getRolePermissionsApi(role.id)
    if (res.code === '200' && res.data) {
      selectedPermissions.value = res.data.permissions || role.permissions || []
    }
  } catch (e) {
    selectedPermissions.value = role.permissions || []
  }
}

// 切换模块展开
const toggleModule = (moduleId) => {
  const index = expandedModules.value.indexOf(moduleId)
  if (index > -1) {
    expandedModules.value.splice(index, 1)
  } else {
    expandedModules.value.push(moduleId)
  }
}

// 切换权限
const togglePermission = (permissionId) => {
  const index = selectedPermissions.value.indexOf(permissionId)
  if (index > -1) {
    selectedPermissions.value.splice(index, 1)
  } else {
    selectedPermissions.value.push(permissionId)
  }
}

// 保存权限
const savePermissions = async () => {
  if (!selectedRole.value) return
  try {
    const res = await updateRolePermissionsApi(selectedRole.value.id, selectedPermissions.value)
    if (res.code === '200') {
      message.success('权限保存成功')
    } else {
      message.error(res.message || '保存失败')
    }
  } catch (e) {
    message.error('保存失败')
  }
}

// 编辑角色
const editRole = (role) => {
  roleForm.value = { ...role }
  showEditModal.value = true
}

// 删除角色
const deleteRole = async (role) => {
  if (!confirm(`确定要删除角色 "${role.name}" 吗？`)) return
  try {
    const res = await deleteRoleApi(role.id)
    if (res.code === '200') {
      message.success('删除成功')
      await loadRoleList()
      if (selectedRole.value?.id === role.id) {
        selectedRole.value = null
      }
    } else {
      message.error(res.message || '删除失败')
    }
  } catch (e) {
    message.error('删除失败')
  }
}

// 关闭弹窗
const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  roleForm.value = {
    name: '',
    code: '',
    description: '',
    color: '#4F46E5',
    icon: 'UserOutlined',
    status: 1
  }
}

// 提交角色
const submitRole = async () => {
  if (!roleForm.value.name || !roleForm.value.code) {
    message.warning('请填写角色名称和编码')
    return
  }
  try {
    let res
    if (showEditModal.value) {
      res = await updateRoleApi(roleForm.value.id, roleForm.value)
    } else {
      res = await createRoleApi(roleForm.value)
    }
    if (res.code === '200') {
      message.success(showEditModal.value ? '更新成功' : '创建成功')
      closeModal()
      await loadRoleList()
    } else {
      message.error(res.message || '操作失败')
    }
  } catch (e) {
    message.error('操作失败')
  }
}

// 初始化
onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([loadRoleList(), loadPermissionTree()])
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.role-manage-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: #4338ca;
  transform: translateY(-2px);
}

/* 角色卡片网格 */
.role-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.role-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.role-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.role-card.active {
  border-color: #4f46e5;
  box-shadow: 0 4px 20px rgba(79, 70, 229, 0.2);
}

.role-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.role-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.role-info {
  flex: 1;
}

.role-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.role-code {
  font-size: 12px;
  color: #6b7280;
}

.role-status {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  background: #fee2e2;
  color: #ef4444;
}

.role-status.active {
  background: #d1fae5;
  color: #10b981;
}

.role-desc {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 16px;
  line-height: 1.5;
}

.role-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-count {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
}

.role-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.action-btn.edit {
  background: #dbeafe;
  color: #3b82f6;
}

.action-btn.edit:hover {
  background: #3b82f6;
  color: white;
}

.action-btn.delete {
  background: #fee2e2;
  color: #ef4444;
}

.action-btn.delete:hover {
  background: #ef4444;
  color: white;
}

/* 权限配置面板 */
.permission-panel {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.panel-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.save-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.save-btn:hover {
  background: #059669;
}

.permission-tree {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.permission-group {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f9fafb;
  cursor: pointer;
  transition: background 0.2s;
}

.group-header:hover {
  background: #f3f4f6;
}

.group-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
}

.group-code {
  font-size: 12px;
  color: #6b7280;
  margin-left: auto;
}

.group-header .anticon {
  transition: transform 0.3s;
}

.group-header .anticon.rotated {
  transform: rotate(180deg);
}

.group-children {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.permission-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.permission-item input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #4f46e5;
}

.permission-item label {
  font-size: 13px;
  color: #374151;
  cursor: pointer;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 480px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: #f3f4f6;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: #e5e7eb;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  display: block;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #4f46e5;
}

.color-picker {
  display: flex;
  gap: 12px;
}

.color-option {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
  border: 2px solid transparent;
}

.color-option:hover {
  transform: scale(1.1);
}

.color-option.selected {
  border-color: #1a1a2e;
  transform: scale(1.1);
}

.icon-picker {
  display: flex;
  gap: 12px;
}

.icon-option {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #f3f4f6;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  border: 2px solid transparent;
}

.icon-option:hover {
  background: #e5e7eb;
}

.icon-option.selected {
  background: #4f46e5;
  color: white;
  border-color: #4f46e5;
}

.status-toggle {
  display: flex;
  gap: 12px;
}

.status-toggle button {
  padding: 10px 20px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.status-toggle button.active {
  background: #4f46e5;
  color: white;
  border-color: #4f46e5;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
}

.cancel-btn {
  padding: 10px 20px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: white;
  cursor: pointer;
}

.confirm-btn {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  background: #4f46e5;
  color: white;
  cursor: pointer;
}

.confirm-btn:hover {
  background: #4338ca;
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #4f46e5;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .role-manage-page {
    padding: 16px;
  }
  
  .role-grid {
    grid-template-columns: 1fr;
  }
  
  .permission-panel {
    margin-top: 24px;
  }
}
</style>