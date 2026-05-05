<template>
  <div class="notice-page">
    <div class="page-container">
      <div class="page-header">
        <h2 class="page-title">{{ $t('notice.platformNotice') }}</h2>
        <p class="page-desc">{{ locale === 'ko' ? '플랫폼 소식과 중요 안내를 확인하세요' : '及时了解平台动态和重要通知' }}</p>
      </div>

      <div class="notice-list" v-loading="loading">
        <div 
          v-for="(notice, index) in noticeList" 
          :key="notice.id"
          class="notice-item"
          @click="toggleNotice(notice)"
        >
          <div class="notice-header">
            <div class="notice-left">
              <div class="notice-icon" :style="{ background: getTypeGradient(notice.type) }">
                <el-icon :size="24">
                  <component :is="getTypeIcon(notice.type)" />
                </el-icon>
              </div>
              <div class="notice-info">
                <div class="notice-title-row">
                  <h3 class="notice-title">{{ locale === 'ko' && notice.titleKo ? notice.titleKo : notice.title }}</h3>
                  <el-tag v-if="index < 3" type="danger" size="small" class="new-tag">{{ locale === 'ko' ? '최신' : '最新' }}</el-tag>
                </div>
                <div class="notice-meta">
                  <el-tag :type="getTypeTagType(notice.type)" size="small">
                    {{ getTypeText(notice.type) }}
                  </el-tag>
                  <span class="notice-time">{{ formatDate(notice.publishTime) }}</span>
                </div>
              </div>
            </div>
            <div class="notice-action">
              <el-icon :size="20" class="expand-icon" :class="{ expanded: notice.expanded }">
                <ArrowDown />
              </el-icon>
            </div>
          </div>

          <transition name="expand">
            <div v-if="notice.expanded" class="notice-content">
              <div class="content-divider"></div>
              <div class="content-body" v-html="locale === 'ko' && notice.contentKo ? notice.contentKo : notice.content"></div>
            </div>
          </transition>
        </div>

        <el-empty v-if="!loading && noticeList.length === 0" :description="$t('common.noData')" />
      </div>

      <div class="pagination" v-if="noticeList.length > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[5, 10, 20]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import {
  ArrowDown,
  Bell,
  Flag,
  Tools
} from '@element-plus/icons-vue'

const { t, locale } = useI18n()
const loading = ref(false)
const noticeList = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const getTypeIcon = (type) => {
  const iconMap = {
    system: 'Bell',
    activity: 'Flag',
    maintenance: 'Tools'
  }
  return iconMap[type] || 'Bell'
}

const getTypeGradient = (type) => {
  const gradientMap = {
    system: 'linear-gradient(135deg, #409eff 0%, #66b1ff 100%)',
    activity: 'linear-gradient(135deg, #67c23a 0%, #85ce61 100%)',
    maintenance: 'linear-gradient(135deg, #ff9800 0%, #ffc107 100%)'
  }
  return gradientMap[type] || gradientMap.system
}

const getTypeTagType = (type) => {
  const typeMap = {
    system: 'primary',
    activity: 'success',
    maintenance: 'warning'
  }
  return typeMap[type] || 'primary'
}

const getTypeText = (type) => {
  const textMap = {
    system: locale.value === 'ko' ? '시스템' : '系统公告',
    activity: locale.value === 'ko' ? '이벤트' : '活动公告',
    maintenance: locale.value === 'ko' ? '점검' : '维护通知'
  }
  return textMap[type] || type
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const now = new Date()
  const diff = now - d
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return locale.value === 'ko' ? '방금 전' : '刚刚'
  if (minutes < 60) return locale.value === 'ko' ? `${minutes}분 전` : `${minutes}分钟前`
  if (hours < 24) return locale.value === 'ko' ? `${hours}시간 전` : `${hours}小时前`
  if (days < 7) return locale.value === 'ko' ? `${days}일 전` : `${days}天前`

  return d.toLocaleDateString(locale.value === 'ko' ? 'ko-KR' : 'zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const getNoticeList = async () => {
  try {
    loading.value = true
    
    const mockNotices = [
      {
        id: 1,
        title: '平台用户协议更新通知',
        titleKo: '플랫폼 이용약관 업데이트 안내',
        type: 'system',
        content: `
          <h3>关于用户协议更新的重要通知</h3>
          <p>尊敬的用户：</p>
          <p>为了更好地保护您的权益，我们对《用户服务协议》进行了更新。主要更新内容包括：</p>
          <ol>
            <li>优化了用户隐私保护条款</li>
            <li>明确了交易纠纷处理流程</li>
            <li>新增了平台责任免责声明</li>
            <li>更新了违规行为处理规则</li>
          </ol>
          <p>更新后的协议将于<strong>2024年1月15日</strong>正式生效。如您继续使用本平台服务，即视为同意新版协议内容。</p>
          <p>如有疑问，请联系客服：service@example.com</p>
          <p style="text-align: right;">校园二手交易平台运营团队</p>
        `,
        contentKo: `
          <h3>이용약관 업데이트에 관한 중요 안내</h3>
          <p>안녕하세요, 사용자 여러분.</p>
          <p>사용자 여러분의 권익을 보다 효과적으로 보호하기 위해 이용약관을 업데이트하였습니다. 주요 내용은 다음과 같습니다:</p>
          <ol>
            <li>개인정보 보호 조항 최적화</li>
            <li>거래 분쟁 처리 프로세스 명확화</li>
            <li>플랫폼 책임 면책 조항 추가</li>
            <li>규정 위반 행위 처리 규칙 업데이트</li>
          </ol>
          <p>업데이트된 약관은 <strong>2024년 1월 15일</strong>부터 시행됩니다. 서비스를 계속 이용하실 경우 새로운 약관에 동의한 것으로 간주됩니다.</p>
          <p>문의사항은 고객센터(service@example.com)로 연락주시기 바랍니다.</p>
          <p style="text-align: right;">캠퍼스 중고거래 플랫폼 운영팀</p>
        `,
        publishTime: new Date(Date.now() - 2 * 60 * 60 * 1000),
        expanded: false
      },
      {
        id: 2,
        title: '新春促销活动火热进行中',
        titleKo: '새해맞이 프로모션 진행 중',
        type: 'activity',
        content: `
          <h3>新春特惠，限时抢购！</h3>
          <p>亲爱的用户，新春佳节来临之际，平台推出多款优惠商品，欢迎选购！</p>
          <h4>活动时间</h4>
          <p>2024年1月20日 - 2024年2月10日</p>
          <h4>活动内容</h4>
          <ul>
            <li>全场商品9折起</li>
            <li>满100减20，满200减50</li>
            <li>每日限时秒杀，低至5折</li>
            <li>新用户首单立减30元</li>
          </ul>
          <p>数量有限，先到先得，快来抢购吧！</p>
        `,
        contentKo: `
          <h3>새해 특가, 한정 판매!</h3>
          <p>안녕하세요! 새해를 맞아 다양한 할인 혜택을 준비했습니다.</p>
          <h4>이벤트 기간</h4>
          <p>2024년 1월 20일 - 2024년 2월 10일</p>
          <h4>이벤트 내용</h4>
          <ul>
            <li>전 상품 최대 10% 할인</li>
            <li>100위안 이상 구매 시 20위안, 200위안 이상 시 50위안 할인</li>
            <li>매일 한정 수량 타임세일 (최대 50% 할인)</li>
            <li>신규 사용자 첫 구매 시 30위안 즉시 할인</li>
          </ul>
          <p>한정 수량으로 진행되니 서둘러 확인해보세요!</p>
        `,
        publishTime: new Date(Date.now() - 5 * 60 * 60 * 1000),
        expanded: false
      },
      {
        id: 3,
        title: '系统维护升级通知',
        titleKo: '시스템 점검 및 업데이트 안내',
        type: 'maintenance',
        content: `
          <h3>系统维护升级公告</h3>
          <p>尊敬的用户：</p>
          <p>为了提升系统性能和用户体验，我们将对平台进行维护升级。</p>
          <h4>维护时间</h4>
          <p><strong>2024年1月13日 凌晨2:00 - 6:00</strong></p>
          <h4>维护内容</h4>
          <ul>
            <li>优化搜索功能，提升检索速度</li>
            <li>修复已知bug</li>
            <li>升级安全防护系统</li>
            <li>优化页面加载速度</li>
          </ul>
          <p>维护期间平台将暂停服务，给您带来不便，敬请谅解。</p>
          <p>感谢您的支持与理解！</p>
        `,
        contentKo: `
          <h3>시스템 점검 공지</h3>
          <p>안녕하세요. 서비스 품질 향상을 위해 시스템 점검을 실시합니다.</p>
          <h4>점검 시간</h4>
          <p><strong>2024년 1월 13일 새벽 2:00 - 6:00</strong></p>
          <h4>점검 내용</h4>
          <ul>
            <li>검색 기능 최적화 및 속도 향상</li>
            <li>알려진 버그 수정</li>
            <li>보안 시스템 강화</li>
            <li>페이지 로딩 속도 개선</li>
          </ul>
          <p>점검 시간 동안 서비스 이용이 일시 중단되오니 양해 부탁드립니다.</p>
          <p>항상 저희 서비스를 이용해 주셔서 감사합니다.</p>
        `,
        publishTime: new Date(Date.now() - 10 * 60 * 60 * 1000),
        expanded: false
      },
      {
        id: 4,
        title: '平台功能升级完成',
        titleKo: '플랫폼 기능 업데이트 완료',
        type: 'system',
        content: `
          <h3>功能升级完成通知</h3>
          <p>尊敬的用户，平台功能升级已顺利完成！</p>
          <h4>新增功能</h4>
          <ul>
            <li>AI智能推荐：根据您的浏览历史，为您推荐感兴趣的商品</li>
            <li>商品评价系统：购买后可对商品和卖家进行评价</li>
            <li>消息通知中心：实时接收订单、评价等重要消息</li>
            <li>收藏夹功能：喜欢的商品可以收藏，随时查看</li>
          </ul>
          <p>期待您体验全新的平台功能！</p>
        `,
        contentKo: `
          <h3>업데이트 완료 안내</h3>
          <p>안녕하세요. 새로운 기능들이 추가되었습니다!</p>
          <h4>새로운 기능</h4>
          <ul>
            <li>AI 스마트 추천: 사용자의 취향에 맞는 상품을 추천해 드립니다.</li>
            <li>상품 리뷰 시스템: 구매 후 상품과 판매자에 대한 리뷰를 남길 수 있습니다.</li>
            <li>알림 센터: 주문, 리뷰 등 중요 소식을 실시간으로 확인하세요.</li>
            <li>관심 목록: 마음에 드는 상품을 저장하고 언제든 확인하세요.</li>
          </ul>
          <p>더욱 편리해진 기능을 지금 바로 체험해보세요!</p>
        `,
        publishTime: new Date(Date.now() - 24 * 60 * 60 * 1000),
        expanded: false
      },
      {
        id: 5,
        title: '诚信交易倡议书',
        titleKo: '신뢰 거래 캠페인',
        type: 'system',
        content: `
          <h3>关于倡导诚信交易的倡议</h3>
          <p>亲爱的用户朋友们：</p>
          <p>为营造诚信、和谐的交易环境，我们向全体用户发出以下倡议：</p>
          <ol>
            <li>如实描述商品信息，不夸大、不隐瞒</li>
            <li>按时发货，保证商品质量</li>
            <li>文明交流，礼貌待人</li>
            <li>遵守平台规则，维护交易秩序</li>
            <li>发现问题及时沟通，理性解决纠纷</li>
          </ol>
          <p>让我们共同努力，打造一个值得信赖的校园二手交易平台！</p>
        `,
        contentKo: `
          <h3>신뢰할 수 있는 거래 환경을 위한 제안</h3>
          <p>안녕하세요, 사용자 여러분.</p>
          <p>건전하고 조화로운 거래 환경을 만들기 위해 다음과 같은 수칙을 제안합니다:</p>
          <ol>
            <li>상품 정보는 과장 없이 사실대로 작성해주세요.</li>
            <li>배송 시간을 준수하고 상품 품질을 보장해주세요.</li>
            <li>서로 예의를 지키며 소통해주세요.</li>
            <li>플랫폼 운영 규칙을 준수해주세요.</li>
            <li>문제 발생 시 이성적으로 소통하여 해결해주세요.</li>
          </ol>
          <p>함께 신뢰할 수 있는 캠퍼스 중고거래 플랫폼을 만들어가요!</p>
        `,
        publishTime: new Date(Date.now() - 48 * 60 * 60 * 1000),
        expanded: false
      }
    ]

    noticeList.value = mockNotices
    pagination.total = mockNotices.length
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error(t('common.noData'))
  } finally {
    loading.value = false
  }
}

const toggleNotice = (notice) => {
  notice.expanded = !notice.expanded
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

onMounted(() => {
  getNoticeList()
})
</script>

<style scoped>
.notice-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #fafbfc 100%);
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.page-desc {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.notice-list {
  margin-bottom: 30px;
}

.notice-item {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.notice-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.notice-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  min-width: 0;
}

.notice-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.notice-info {
  flex: 1;
  min-width: 0;
}

.notice-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.notice-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.new-tag {
  flex-shrink: 0;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice-time {
  font-size: 14px;
  color: #909399;
}

.notice-action {
  flex-shrink: 0;
  margin-left: 16px;
}

.expand-icon {
  color: #909399;
  transition: transform 0.3s;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

.notice-content {
  padding: 0 20px 20px 20px;
}

.content-divider {
  height: 1px;
  background: #f0f0f0;
  margin-bottom: 20px;
}

.content-body {
  line-height: 1.8;
  color: #2c3e50;
  font-size: 15px;
}

.content-body :deep(h3) {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 20px 0 12px 0;
}

.content-body :deep(h4) {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 16px 0 8px 0;
}

.content-body :deep(p) {
  margin: 12px 0;
}

.content-body :deep(ul),
.content-body :deep(ol) {
  padding-left: 24px;
  margin: 12px 0;
}

.content-body :deep(li) {
  margin: 8px 0;
}

.content-body :deep(strong) {
  color: #409eff;
  font-weight: 600;
}

.content-body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 12px 0;
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  max-height: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
  max-height: 2000px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .page-container {
    padding: 0 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .page-desc {
    font-size: 14px;
  }

  .notice-header {
    padding: 16px;
  }

  .notice-left {
    gap: 12px;
  }

  .notice-icon {
    width: 48px;
    height: 48px;
  }

  .notice-title {
    font-size: 15px;
  }

  .notice-content {
    padding: 0 16px 16px 16px;
  }
}
</style>

