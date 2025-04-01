import { createRouter, createWebHistory } from 'vue-router'
import ChatbotList from '../views/ChatbotList.vue'
import ChatView from '../views/ChatView.vue'

const routes = [
  {
    path: '/',
    redirect: '/chatbots'
  },
  {
    path: '/chatbots',
    name: 'ChatbotList',
    component: ChatbotList
  },
  {
    path: '/chat/:id',
    name: 'ChatView',
    component: ChatView,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 