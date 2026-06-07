import { defineStore } from 'pinia'
import { schoolApi } from '@/api/school'
import i18n from '@/i18n'

export const useSchoolStore = defineStore('school', {
    state: () => ({
        schoolList: [],
        selectedSchool: localStorage.getItem('schoolId') || '',
        selectedLanguage: localStorage.getItem('language') || 'zh',
        loadFailedOnce: false
    }),

    getters: {
        currentSchoolName(state) {
            if (!state.selectedSchool) return i18n.global.t('nav.selectSchool')

            const school = state.schoolList.find(
                s => s.value === state.selectedSchool
            )

            return school ? school.label : i18n.global.t('nav.selectSchool')
        },
        getSchoolName: (state) => (id) => {
            const school = state.schoolList.find(item => item.value === id)
            return school ? school.label : id
        }
    },

    actions: {
        async setLanguage(language) {
            this.selectedLanguage = language || 'zh'
            localStorage.setItem('language', this.selectedLanguage)
            await this.getSchoolList()
        },
        async getSchoolList() {
            try {
                this.selectedLanguage = localStorage.getItem('language') || this.selectedLanguage || 'zh'
                const res = await schoolApi.getSchoolList({
                    language: this.selectedLanguage
                })

                if (res.code === 200 || res.code === '200') {
                    this.schoolList = (res.data || []).map(item => ({
                        label: item.name || item.nameZh || 'Unknown School',
                        value: String(item.id)
                    }))
                    localStorage.setItem(`schoolListCache:${this.selectedLanguage}`, JSON.stringify(this.schoolList))
                }
            } catch (error) {
                const cached = localStorage.getItem(`schoolListCache:${this.selectedLanguage}`)
                if (cached) {
                    try {
                        this.schoolList = JSON.parse(cached) || []
                        return
                    } catch (e) { }
                }

                if (!this.loadFailedOnce) {
                    this.loadFailedOnce = true
                    console.warn('获取学校列表失败:', error?.message || error)
                }
            }
        },

        setSchool(value) {
            this.selectedSchool = value
            localStorage.setItem('schoolId', value || '')
        },
        setSelectedSchool(value) {
            this.setSchool(value)
        }
    }
})
