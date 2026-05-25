import { createI18n } from 'vue-i18n'

import zh from './locales/zh'
import en from './locales/en'
import ko from './locales/ko'

const i18n = createI18n({
    legacy: false,
    globalInjection: true,
    locale: localStorage.getItem('language') || 'zh',
    fallbackLocale: 'zh',
    messages: {
        zh,
        en,
        ko
    }
})

export default i18n

