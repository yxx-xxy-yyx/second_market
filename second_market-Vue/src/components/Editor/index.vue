<template>
  <div style="border: 1px solid #ccc; border-radius: 4px;">
    <Toolbar 
      style="border-bottom: 1px solid #ccc" 
      :editor="editorRef" 
      :defaultConfig="toolbarConfig" 
      mode="default" 
    />
    <Editor 
      style="height: 500px; overflow-y: hidden" 
      v-model="valueHtml" 
      :defaultConfig="editorConfig" 
      mode="default" 
      @onCreated="handleCreated"
      @onChange="handleChange" 
    />
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { ref, shallowRef, onBeforeUnmount, watch } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const emit = defineEmits(['update:editorValue'])
const props = defineProps({
  editorValue: {
    type: String,
    default: ''
  }
})

const valueHtml = ref(props.editorValue)

watch(() => props.editorValue, (newVal) => {
  if (newVal !== valueHtml.value) {
    valueHtml.value = newVal
  }
})

const editorRef = shallowRef()

const toolbarConfig = {
  excludeKeys: [
    'headerSelect',
    'italic',
    'group-more-style',
    'group-video'
  ]
}

const editorConfig = { 
  placeholder: '请输入内容...',
  readOnly: false,
  MENU_CONF: {}
}

editorConfig.MENU_CONF['uploadImage'] = {
  base64LimitSize: 1024 * 1024 * 10
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor
}

const handleChange = (editor) => {
  emit('update:editorValue', editor.getHtml())
}
</script>

<style scoped>
</style>
