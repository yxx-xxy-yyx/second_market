<template>
  <div class="forum-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>论坛管理</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="帖子管理" name="posts">
          <div class="search-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索帖子标题或内容"
              clearable
              style="width: 300px; margin-right: 10px"
              @keyup.enter="loadPosts"
            />
            <el-select v-model="searchCategory" placeholder="选择分类" clearable style="width: 150px; margin-right: 10px">
              <el-option label="求助" value="help" />
              <el-option label="分享" value="share" />
              <el-option label="交流" value="discussion" />
              <el-option label="二手" value="secondhand" />
              <el-option label="其他" value="other" />
            </el-select>
            <el-button type="primary" @click="loadPosts">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>

          <el-table :data="posts" style="width: 100%; margin-top: 20px" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
            <el-table-column prop="content" label="内容预览" min-width="250" show-overflow-tooltip>
              <template #default="{ row }">
                <span>{{ row.content && row.content.length > 50 ? row.content.slice(0, 50) + '...' : row.content }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.category === 'help'" type="danger">求助</el-tag>
                <el-tag v-else-if="row.category === 'share'" type="success">分享</el-tag>
                <el-tag v-else-if="row.category === 'discussion'" type="primary">交流</el-tag>
                <el-tag v-else-if="row.category === 'secondhand'" type="warning">二手</el-tag>
                <el-tag v-else>其他</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="viewCount" label="浏览量" width="80" />
            <el-table-column prop="likeCount" label="点赞数" width="80" />
            <el-table-column prop="commentCount" label="评论数" width="80" />
            <el-table-column prop="createTime" label="发布时间" width="170">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="viewPostDetail(row)">
                  查看
                </el-button>
                <el-button type="danger" link size="small" @click="confirmDeletePost(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="postPage"
            v-model:page-size="postSize"
            :total="postTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadPosts"
            @current-change="loadPosts"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-tab-pane>

        <el-tab-pane label="评论管理" name="comments">
          <div class="search-bar">
            <el-input
              v-model="commentKeyword"
              placeholder="搜索评论内容"
              clearable
              style="width: 300px; margin-right: 10px"
              @keyup.enter="loadComments"
            />
            <el-button type="primary" @click="loadComments">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>

          <el-table :data="comments" style="width: 100%; margin-top: 20px" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="postId" label="帖子ID" width="100" />
            <el-table-column prop="content" label="评论内容" min-width="300" show-overflow-tooltip />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="createTime" label="评论时间" width="170">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="danger" link size="small" @click="confirmDeleteComment(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="commentPage"
            v-model:page-size="commentSize"
            :total="commentTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadComments"
            @current-change="loadComments"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="postDetailVisible" title="帖子详情" width="700px">
      <div v-if="currentPost" class="post-detail">
        <h3>{{ currentPost.title }}</h3>
        <div class="post-meta">
          <el-tag>{{ currentPost.category }}</el-tag>
          <span style="margin-left: 10px">浏览: {{ currentPost.viewCount }}</span>
          <span style="margin-left: 10px">点赞: {{ currentPost.likeCount }}</span>
          <span style="margin-left: 10px">评论: {{ currentPost.commentCount }}</span>
        </div>
        <div class="post-content" style="margin-top: 20px; white-space: pre-wrap">{{ currentPost.content }}</div>
        <div class="post-time" style="margin-top: 20px; color: #999">发布于: {{ formatDate(currentPost.createTime) }}</div>
      </div>
      <template #footer>
        <el-button @click="postDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { forumApi } from '@/api/forum'

const activeTab = ref('posts')
const searchKeyword = ref('')
const searchCategory = ref('')
const posts = ref([])
const postPage = ref(1)
const postSize = ref(10)
const postTotal = ref(0)

const commentKeyword = ref('')
const comments = ref([])
const commentPage = ref(1)
const commentSize = ref(10)
const commentTotal = ref(0)

const postDetailVisible = ref(false)
const currentPost = ref(null)

const loadPosts = async () => {
  try {
    const res = await forumApi.adminGetAllPosts({
      current: postPage.value,
      size: postSize.value,
      keyword: searchKeyword.value || undefined,
      category: searchCategory.value || undefined
    })
    if (res.data) {
      posts.value = res.data.records || []
      postTotal.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载帖子列表失败')
  }
}

const loadComments = async () => {
  try {
    const res = await forumApi.adminGetAllComments({
      current: commentPage.value,
      size: commentSize.value,
      keyword: commentKeyword.value || undefined
    })
    if (res.data) {
      comments.value = res.data.records || []
      commentTotal.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载评论列表失败')
  }
}

const viewPostDetail = (row) => {
  currentPost.value = row
  postDetailVisible.value = true
}

const confirmDeletePost = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除帖子《${row.title}》吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await forumApi.adminDeletePost(row.id)
    ElMessage.success('删除成功')
    loadPosts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const confirmDeleteComment = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条评论吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await forumApi.adminDeleteComment(row.id)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.forum-manage {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
}

.post-detail h3 {
  margin-bottom: 10px;
}

.post-meta {
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
</style>
