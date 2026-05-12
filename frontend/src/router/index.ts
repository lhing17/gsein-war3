import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import BlpGenerator from '@/views/BlpGenerator.vue'
import AvailableIds from '@/views/AvailableIds.vue'
import Templates from '@/views/Templates.vue'
import GeneralSkill from '@/views/GeneralSkill.vue'
import UnitGenerator from '@/views/UnitGenerator.vue'
import ItemGenerator from '@/views/ItemGenerator.vue'
import TowerGenerator from '@/views/TowerGenerator.vue'
import TaskGenerator from '@/views/TaskGenerator.vue'
import Tools from '@/views/Tools.vue'
import MdxReplaceBlp from '@/views/MdxReplaceBlp.vue'
import MdxClassify from '@/views/MdxClassify.vue'
import ImageSplit from '@/views/ImageSplit.vue'
import TitleGenerator from '@/views/TitleGenerator.vue'
import XlsToLni from '@/views/XlsToLni.vue'
import ConstantReplace from '@/views/ConstantReplace.vue'
import UnitPlace from '@/views/UnitPlace.vue'
import TextSearch from '@/views/TextSearch.vue'
import FourccConvert from '@/views/FourccConvert.vue'
import ColorText from '@/views/ColorText.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/blp', component: BlpGenerator },
  { path: '/ids', component: AvailableIds },
  {
    path: '/templates',
    component: Templates,
    redirect: '/templates/general-skill',
    children: [
      { path: 'general-skill', component: GeneralSkill },
      { path: 'units', component: UnitGenerator },
      { path: 'items', component: ItemGenerator },
      { path: 'towers', component: TowerGenerator },
      { path: 'tasks', component: TaskGenerator },
    ],
  },
  {
    path: '/tools',
    component: Tools,
    redirect: '/tools/mdx-replace',
    children: [
      { path: 'mdx-replace', component: MdxReplaceBlp },
      { path: 'mdx-classify', component: MdxClassify },
      { path: 'image-split', component: ImageSplit },
      { path: 'title', component: TitleGenerator },
      { path: 'xls-to-lni', component: XlsToLni },
      { path: 'constant-replace', component: ConstantReplace },
      { path: 'unit-place', component: UnitPlace },
      { path: 'text-search', component: TextSearch },
      { path: 'fourcc', component: FourccConvert },
      { path: 'color-text', component: ColorText },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
