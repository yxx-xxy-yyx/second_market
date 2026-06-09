import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'

export function useNeoUserKey() {
  const userStore = useUserStore()
  const schoolStore = useSchoolStore()

  const key = computed(() => {
    const u = userStore.user || {}
    const userId = u.id || u.userId || u.uid || u.username || 'guest'
    const schoolId = schoolStore.selectedSchool || 'all'
    return `${userId}:${schoolId}`
  })

  return { key }
}

