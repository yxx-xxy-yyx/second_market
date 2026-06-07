import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as Icons from '@element-plus/icons-vue'

export default {
  install(app) {
    app.use(ElementPlus)
    Object.entries(Icons).forEach(([name, component]) => {
      app.component(name, component)
    })
  }
}
