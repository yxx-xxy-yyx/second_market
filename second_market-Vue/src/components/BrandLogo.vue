<template>
  <div class="inline-flex items-center gap-2">
    <svg width="26" height="26" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path
        d="M6.8 6.6C8.6 4.8 10.5 4 12.4 4c2.9 0 5.3 1.6 6.6 4.2.8 1.7.9 3.6.3 5.4-1.2 3.5-4.3 6-7.8 6-2 0-3.8-.7-5.4-2.3"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
      />
      <path
        d="M7 16.9c.8.8 1.9 1.3 3 1.3 1.3 0 2.4-.5 3.3-1.4l4.2-4.2"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
      <path
        d="M8.6 12.2h6.8"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        opacity="0.6"
      />
    </svg>
    <span class="brand-text">{{ title }}</span>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: 'SecondMarket'
  }
})
</script>

<style scoped>
.brand-text {
  font-weight: 800;
  letter-spacing: 0.2px;
  color: var(--text-primary);
  font-size: 16px;
  line-height: 1;
}
</style>
