<template>
  <div class="notice-manage">
    <div class="page-header">
      <h2 class="page-title">{{ $t("NoticeManage.manage") }}</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">
        {{ $t("NoticeManage.publish") }}
      </el-button>
    </div>

    <el-card class="table-card">
      <el-table v-loading="loading" :data="noticeList" stripe style="width: 100%">
        <el-table-column prop="id" :label="$t('NoticeManage.id')" width="80" />

        <el-table-column :label="$t('NoticeManage.title')" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.title }}
          </template>
        </el-table-column>

        <el-table-column :label="$t('NoticeManage.12')" width="120">
          <template #default="{ row }">
            <div class="type-cell">
              <el-icon :size="18" :color="getTypeColor(row.type)">
                <component :is="getTypeIcon(row.type)" />
              </el-icon>
              <span>{{ $t(getTypeTextKey(row.type)) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column :label="$t('NoticeManage.11')" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ $t(getStatusTextKey(row.status)) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column :label="$t('NoticeManage.publishTime')" width="160">
          <template #default="{ row }">
            {{ formatDate(row.publishTime) }}
          </template>
        </el-table-column>

        <el-table-column :label="$t('NoticeManage.actions')" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)" :icon="View">
              {{ $t("NoticeManage.view") }}
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)" :icon="Edit">
              {{ $t("NoticeManage.edit") }}
            </el-button>
            <el-button v-if="row.status === 'draft' || row.status === 'offline'" type="success" size="small"
              @click="handlePublish(row)" :icon="Upload">
              {{ $t("NoticeManage.publishBtn") }}
            </el-button>
            <el-button v-if="row.status === 'published'" type="info" size="small" @click="handleOffline(row)"
              :icon="Download">
              {{ $t("NoticeManage.offline") }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :icon="Delete">
              {{ $t("NoticeManage.delete") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <el-dialog v-model="viewVisible" :title="$t('NoticeManage.viewNotice')" width="800px">
      <div class="notice-view" v-if="currentNotice">
        <h3 class="notice-title">{{ currentNotice.title }}</h3>
        <div class="notice-meta">
          <el-tag :type="getTypeColor(currentNotice.type)" size="small">
            {{ $t(getTypeTextKey(currentNotice.type)) }}
          </el-tag>
          <span class="notice-time">{{ $t("NoticeManage.publishTimeLabel") }}：{{
            formatDate(currentNotice.publishTime)
          }}</span>
        </div>
        <div class="notice-content" v-html="currentNotice.content"></div>
      </div>
    </el-dialog>

    <el-dialog v-model="formVisible" :title="formTitle" width="900px" @closed="handleDialogClosed">
      <el-form :model="noticeForm" :rules="noticeRules" ref="noticeFormRef" label-width="100px">
        <el-form-item :label="$t('NoticeManage.form.title')" prop="title">
          <el-input v-model="noticeForm.title" :placeholder="$t('NoticeManage.form.titlePlaceholder')" maxlength="100"
            show-word-limit />
        </el-form-item>

        <el-form-item :label="$t('NoticeManage.form.type')" prop="type">
          <el-select v-model="noticeForm.type" :placeholder="$t('NoticeManage.form.typePlaceholder')"
            style="width: 200px">
            <el-option :label="$t('NoticeManage.type.system')" value="system">
              <div class="type-option">
                <el-icon color="#409eff">
                  <Bell />
                </el-icon>
                <span>{{ $t("NoticeManage.type.system") }}</span>
              </div>
            </el-option>
            <el-option :label="$t('NoticeManage.type.activity')" value="activity">
              <div class="type-option">
                <el-icon color="#67c23a">
                  <Flag />
                </el-icon>
                <span>{{ $t("NoticeManage.type.activity") }}</span>
              </div>
            </el-option>
            <el-option :label="$t('NoticeManage.type.maintenance')" value="maintenance">
              <div class="type-option">
                <el-icon color="#e6a23c">
                  <Tools />
                </el-icon>
                <span>{{ $t("NoticeManage.type.maintenance") }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('NoticeManage.form.content')" prop="content">
          <Editor v-model:editorValue="noticeForm.content" :editorValue="noticeForm.content" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="formVisible = false">{{
          $t("NoticeManage.cancel")
        }}</el-button>
        <el-button @click="handleSaveDraft" :loading="saving">
          {{ $t("NoticeManage.saveDraft") }}
        </el-button>
        <el-button type="primary" @click="handleSaveAndPublish" :loading="saving">
          {{ $t("NoticeManage.publishNow") }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Plus,
  View,
  Edit,
  Delete,
  Upload,
  Download,
  Bell,
  Flag,
  Tools,
} from "@element-plus/icons-vue";
import Editor from "@/components/Editor/index.vue";

const { t } = useI18n();

const loading = ref(false);
const saving = ref(false);
const viewVisible = ref(false);
const formVisible = ref(false);
const isAdd = ref(false);
const currentNotice = ref(null);
const noticeList = ref([]);
const noticeFormRef = ref();

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

const noticeForm = reactive({
  id: "",
  title: "",
  type: "system",
  content: "",
});

// 表单校验规则
const noticeRules = computed(() => ({
  title: [
    {
      required: true,
      message: t("NoticeManage.rules.titleRequired"),
      trigger: "blur",
    },
    { min: 5, message: t("NoticeManage.rules.titleLength"), trigger: "blur" },
  ],
  type: [
    {
      required: true,
      message: t("NoticeManage.rules.typeRequired"),
      trigger: "change",
    },
  ],
  content: [
    {
      required: true,
      message: t("NoticeManage.rules.contentRequired"),
      trigger: "blur",
    },
  ],
}));

const formTitle = computed(() =>
  isAdd.value ? t("NoticeManage.publishNotice") : t("NoticeManage.editNotice"),
);

const getTypeIcon = (type) => {
  const iconMap = {
    system: "Bell",
    activity: "Flag",
    maintenance: "Tools",
  };
  return iconMap[type] || "Bell";
};

const getTypeColor = (type) => {
  const colorMap = {
    system: "#409eff",
    activity: "#67c23a",
    maintenance: "#e6a23c",
  };
  return colorMap[type] || "#409eff";
};

// 类型翻译键映射（前缀改为 NoticeManage）
const getTypeTextKey = (type) => {
  const textMap = {
    system: "NoticeManage.type.system",
    activity: "NoticeManage.type.activity",
    maintenance: "NoticeManage.type.maintenance",
  };
  return textMap[type] || type;
};

const getStatusType = (status) => {
  const typeMap = {
    draft: "info",
    published: "success",
    offline: "info",
  };
  return typeMap[status] || "info";
};

// 状态翻译键映射（前缀改为 NoticeManage）
const getStatusTextKey = (status) => {
  const textMap = {
    draft: "NoticeManage.status.draft",
    published: "NoticeManage.status.published",
    offline: "NoticeManage.status.offline",
  };
  return textMap[status] || status;
};

const formatDate = (date) => {
  if (!date) return "-";
  return new Date(date).toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const getNoticeList = async () => {
  try {
    loading.value = true;

    noticeList.value = [
      {
        id: 1,
        title: "平台用户协议更新通知",
        type: "system",
        content:
          "<p>尊敬的用户，我们对平台用户协议进行了更新，请仔细阅读...</p>",
        status: "published",
        publishTime: new Date(Date.now() - 24 * 60 * 60 * 1000),
      },
      {
        id: 2,
        title: "新春促销活动开始啦",
        type: "activity",
        content: "<p>新春佳节，平台推出多款优惠商品，欢迎选购...</p>",
        status: "published",
        publishTime: new Date(Date.now() - 48 * 60 * 60 * 1000),
      },
      {
        id: 3,
        title: "系统维护通知",
        type: "maintenance",
        content: "<p>系统将于本周六凌晨2:00-6:00进行维护升级...</p>",
        status: "draft",
        publishTime: null,
      },
      {
        id: 4,
        title: "功能升级公告",
        type: "system",
        content: "<p>平台即将上线新功能，敬请期待...</p>",
        status: "offline",
        publishTime: new Date(Date.now() - 72 * 60 * 60 * 1000),
      },
    ]

    pagination.total = noticeList.value.length
  } catch (error) {
    console.error("获取公告列表失败:", error)
    ElMessage.error(t("NoticeManage.fetchFail"))
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  getNoticeList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  getNoticeList()
}

const handleAdd = () => {
  isAdd.value = true
  formVisible.value = true
  resetForm()
}

const handleView = (row) => {
  currentNotice.value = { ...row }
  viewVisible.value = true
}

const handleEdit = (row) => {
  isAdd.value = false
  formVisible.value = true
  Object.assign(noticeForm, {
    id: row.id,
    title: row.title,
    type: row.type,
    content: row.content
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      t("NoticeManage.confirmDelete", { title: row.title }),
      t("NoticeManage.confirmDeleteTitle"),
      {
        confirmButtonText: t("NoticeManage.confirm"),
        cancelButtonText: t("NoticeManage.cancel"),
        type: "warning"
      }
    )

    ElMessage.success(t("NoticeManage.deleteSuccess"));
    const index = noticeList.value.findIndex((item) => item.id === row.id);
    if (index !== -1) {
      noticeList.value.splice(index, 1);
      pagination.total--;
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除公告失败:", error);
      ElMessage.error(t("NoticeManage.deleteFail"));
    }
  }
}

const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm(
      t("NoticeManage.confirmPublish", { title: row.title }),
      t("NoticeManage.confirmPublishTitle"),
      {
        confirmButtonText: t("NoticeManage.confirm"),
        cancelButtonText: t("NoticeManage.cancel"),
        type: "success"
      }
    )

    ElMessage.success('发布成功')
    row.status = 'published'
    row.publishTime = new Date()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布公告失败:', error)
      ElMessage.error(t("NoticeManage.publishFail"))
    }
  }
};

const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm(
      t("NoticeManage.confirmOffline", { title: row.title }),
      t("NoticeManage.confirmOfflineTitle"),
      {
        confirmButtonText: t("NoticeManage.confirm"),
        cancelButtonText: t("NoticeManage.cancel"),
        type: 'warning'
      }
    )

    ElMessage.success(t("NoticeManage.offlineSuccess"))
    row.status = 'offline'
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架公告失败:', error)
      ElMessage.error(t("NoticeManage.offlineFail"))
    }
  }
}

const handleSaveDraft = async () => {
  try {
    await noticeFormRef.value.validate()
    saving.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    if (isAdd.value) {
      noticeList.value.unshift({
        id: Date.now(),
        title: noticeForm.title,
        type: noticeForm.type,
        content: noticeForm.content,
        status: 'draft',
        publishTime: null
      })
      pagination.total++
    } else {
      const index = noticeList.value.findIndex(item => item.id === noticeForm.id)
      if (index !== -1) {
        noticeList.value[index] = {
          ...noticeList.value[index],
          title: noticeForm.title,
          type: noticeForm.type,
          content: noticeForm.content
        }
      }
    }

    ElMessage.success(t("NoticeManage.saveDraftSuccess"));
    formVisible.value = false
  } catch (error) {
    if (error !== false) {
      console.error('保存草稿失败:', error)
      ElMessage.error(t("NoticeManage.saveDraftFail"))
    }
  } finally {
    saving.value = false
  }
}

const handleSaveAndPublish = async () => {
  try {
    await noticeFormRef.value.validate()
    saving.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    if (isAdd.value) {
      noticeList.value.unshift({
        id: Date.now(),
        title: noticeForm.title,
        type: noticeForm.type,
        content: noticeForm.content,
        status: 'published',
        publishTime: new Date()
      })
      pagination.total++
    } else {
      const index = noticeList.value.findIndex(item => item.id === noticeForm.id)
      if (index !== -1) {
        noticeList.value[index] = {
          ...noticeList.value[index],
          title: noticeForm.title,
          type: noticeForm.type,
          content: noticeForm.content,
          status: 'published',
          publishTime: new Date()
        }
      }
    }

    ElMessage.success(t("NoticeManage.publishSuccess"))
    formVisible.value = false
  } catch (error) {
    if (error !== false) {
      console.error('发布失败:', error)
      ElMessage.error(t("NoticeManage.publishFail"))
    }
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  Object.assign(noticeForm, {
    id: '',
    title: '',
    type: 'system',
    content: ''
  })
}

const handleDialogClosed = () => {
  resetForm()
  noticeFormRef.value?.clearValidate()
}

onMounted(() => {
  getNoticeList()
})
</script>

<style scoped>
.notice-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(40px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.page-title {
  font-size: 24px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.page-header :deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.table-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(40px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
}

.type-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 20px 0;
}

.pagination :deep(.el-pagination) {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  padding: 8px 16px;
}

.notice-view {
  padding: 20px 0;
}

.notice-title {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 16px 0;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.notice-time {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.notice-content {
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.85);
}

.notice-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

.notice-content :deep(p) {
  margin: 12px 0;
}

:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255, 255, 255, 0.05);
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-table-border-color: rgba(255, 255, 255, 0.1);
  --el-table-text-color: rgba(255, 255, 255, 0.9);
  --el-table-header-text-color: rgba(255, 255, 255, 0.7);
}

:deep(.el-dialog) {
  background: rgba(26, 26, 62, 0.95);
  backdrop-filter: blur(40px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 20px 24px;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 700;
}

:deep(.el-dialog__close) {
  color: rgba(255, 255, 255, 0.7);
}

:deep(.el-dialog__body) {
  padding: 24px;
  color: rgba(255, 255, 255, 0.85);
}

:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.85);
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  box-shadow: none;
}

:deep(.el-input__inner) {
  color: white;
}

:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

:deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  box-shadow: none;
  color: white;
}

@media (max-width: 768px) {
  .notice-manage {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
