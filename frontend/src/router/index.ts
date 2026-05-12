import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import BlpGenerator from '@/views/BlpGenerator.vue'
import AvailableIds from '@/views/AvailableIds.vue'
import Templates from '@/views/Templates.vue'
import Tools from '@/views/Tools.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/blp', component: BlpGenerator },
  { path: '/ids', component: AvailableIds },
  { path: '/templates', component: Templates },
  { path: '/tools', component: Tools },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
