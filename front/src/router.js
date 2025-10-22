import { createRouter, createWebHistory } from 'vue-router'
import UserList from './pages/UserList.vue'
import UserCreate from './pages/UserCreate.vue'
import UserEdit from './pages/UserEdit.vue'


const routes = [
{ path: '/', redirect: '/users' },
{ path: '/users', component: UserList },
{ path: '/users/create', component: UserCreate },
{ path: '/users/:id/edit', component: UserEdit, props: true }
]


export default createRouter({
history: createWebHistory(),
routes
})