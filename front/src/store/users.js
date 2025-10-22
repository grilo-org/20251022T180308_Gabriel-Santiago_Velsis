import { defineStore } from 'pinia'
import api from '../services/api'


export const useUserStore = defineStore('users', {
    state: () => ({
        users: [],
        loading: false
    }),
    actions: {
        async fetchUsers() {
            this.loading = true
            try {
                const res = await api.get('/usuarios')
                this.users = res.data
            } finally {
                this.loading = false
            }
        },
        async createUser(user) {
            await api.post('/usuarios', user)
            await this.fetchUsers()
        },
        async updateUser(user) {
            await api.put('/usuarios', user)
            await this.fetchUsers()
        },
        async updateUserName(userData) {
            await api.patch('/usuarios/name', userData)
            await this.fetchUsers()
        },

        async updateUserDocument(userData) {
            await api.patch('/usuarios/document', userData)
            await this.fetchUsers()
        },

        async updateUserAddress(userData) {
            await api.patch('/usuarios/address', userData)
            await this.fetchUsers()
        },

        async updateUserBirthDate(userData) {
            await api.patch('/usuarios/birthDate', userData)
            await this.fetchUsers()
        },
        async deleteUser(id) {
            await api.delete(`/usuarios/${id}`)
            await this.fetchUsers()
        },
        getUser(id) {
            return this.users.find(u => u.id === id)
        },
        async returnUserForUpdate(id) {
            this.loading = true
            try {
                const res = await api.get(`/usuarios/${id}`)
                this.userForUpdate = res.data
                return res.data
            } catch (error) {
                console.error('Erro ao buscar usuários para atualização:', error)
                throw error
            } finally {
                this.loading = false
            }
        }
    }
})