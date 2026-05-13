<template>
  <span class="gw-icon" :class="{ 'gw-icon--glow': glow }">
    <Icon :icon="icon" :width="size" :height="size" />
  </span>
</template>

<script setup lang="ts">
import { Icon } from '@iconify/vue'

defineProps<{
  icon: string
  size?: number | string
  glow?: boolean
}>()
</script>

<style scoped>
.gw-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--gw-text-secondary);
  transition: color 0.2s ease;
}

.gw-icon--glow {
  filter: drop-shadow(0 0 4px rgba(201, 162, 39, 0.3));
}

:global(.el-menu-item.is-active) .gw-icon {
  color: var(--gw-accent-gold);
}

:global(.el-menu-item:hover) .gw-icon {
  color: var(--gw-accent-gold-light);
}
</style>
