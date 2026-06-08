import { computed } from 'vue'
import { useSchoolStore } from '@/stores/school'

export function useNeoSchoolKey() {
  const schoolStore = useSchoolStore()
  const key = computed(() => {
    const schoolId = schoolStore.selectedSchool || 'all'
    return String(schoolId)
  })
  return { key }
}

