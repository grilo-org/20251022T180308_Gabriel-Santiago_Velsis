<template>
    <div class="container">
        <div class="header">
            <h1>Lista de Usuários</h1>
            <router-link to="/users/create" class="btn-primary">
                Novo Usuário
            </router-link>
        </div>

        <div class="table-container" v-if="!store.loading">
            <table class="users-table">
                <thead>
                    <tr>
                        <th>Nome</th>
                        <th>Nascimento</th>
                        <th>Estado</th>
                        <th>Cidade</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="u in paginatedUsers" :key="u.id">
                        <td>{{ u.name }}</td>
                        <td>{{ formatDate(u.birth_date) }}</td>
                        <td>{{ u.state || 'N/A' }}</td>
                        <td>{{ u.city || 'N/A' }}</td>
                        <td class="actions">
                            <router-link :to="`/users/${u.id}/edit`" class="btn-edit">
                                Editar
                            </router-link>
                            <button @click="remove(u.id)" class="btn-delete">
                                Excluir
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
            
            <div v-if="store.users.length === 0" class="empty-state">
                Nenhum usuário encontrado.
            </div>
        </div>

        <div v-else class="loading">
            <p>Carregando usuários...</p>
        </div>

        <div class="pagination" v-if="store.users.length > 0">
            <button @click="prevPage" :disabled="page === 1" class="btn-pagination">
                Anterior
            </button>
            <span class="page-info">Página {{ page }} de {{ totalPages }}</span>
            <button @click="nextPage" :disabled="page >= totalPages" class="btn-pagination">
                Próxima
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../store/users'

const store = useUserStore()
const page = ref(1)
const perPage = 20

onMounted(() => store.fetchUsers())

const paginatedUsers = computed(() => {
    const start = (page.value - 1) * perPage
    return store.users.slice(start, start + perPage)
})

const totalPages = computed(() => Math.ceil(store.users.length / perPage))

function nextPage() { 
    if (page.value < totalPages.value) page.value++ 
}

function prevPage() { 
    if (page.value > 1) page.value-- 
}

function formatDate(dateString) {
    if (!dateString) return 'N/A'
    const date = new Date(dateString)
    return date.toLocaleDateString('pt-BR', {
        timeZone: 'UTC'
    })
}

async function remove(id) {
    if (confirm('Deseja realmente excluir este usuário?')) {
        try {
            await store.deleteUser(id)
            notifySuccess("Usuário removido com sucesso!")
            if (paginatedUsers.value.length === 0 && page.value > 1) {
                page.value--
            }
        } catch (err) {
            notifyError(err.response?.data?.message || "Erro ao excluir usuário")
        }
    }
}
</script>

<style scoped>
.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.header h1 {
    color: #2c3e50;
    margin: 0;
}

.btn-primary {
    background-color: #3498db;
    color: white;
    padding: 0.75rem 1.5rem;
    text-decoration: none;
    border-radius: 6px;
    border: none;
    cursor: pointer;
    transition: background-color 0.3s;
}

.btn-primary:hover {
    background-color: #2980b9;
}

.table-container {
    background: white;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

.users-table {
    width: 100%;
    border-collapse: collapse;
}

.users-table th {
    background-color: #f8f9fa;
    padding: 1rem;
    text-align: left;
    font-weight: 600;
    color: #2c3e50;
    border-bottom: 2px solid #e9ecef;
}

.users-table td {
    padding: 1rem;
    border-bottom: 1px solid #e9ecef;
}

.users-table tr:hover {
    background-color: #f8f9fa;
}

.users-table tr:last-child td {
    border-bottom: none;
}

.actions {
    display: flex;
    gap: 0.5rem;
}

.btn-edit {
    background-color: #27ae60;
    color: white;
    padding: 0.5rem 1rem;
    text-decoration: none;
    border-radius: 4px;
    font-size: 0.875rem;
    transition: background-color 0.3s;
}

.btn-edit:hover {
    background-color: #219a52;
}

.btn-delete {
    background-color: #e74c3c;
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 4px;
    font-size: 0.875rem;
    cursor: pointer;
    transition: background-color 0.3s;
}

.btn-delete:hover {
    background-color: #c0392b;
}

.btn-delete:disabled {
    background-color: #95a5a6;
    cursor: not-allowed;
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
    margin-top: 2rem;
}

.btn-pagination {
    background-color: #34495e;
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.btn-pagination:hover:not(:disabled) {
    background-color: #2c3e50;
}

.btn-pagination:disabled {
    background-color: #bdc3c7;
    cursor: not-allowed;
}

.page-info {
    color: #7f8c8d;
    font-weight: 500;
}

.loading {
    text-align: center;
    padding: 3rem;
    color: #7f8c8d;
}

.empty-state {
    text-align: center;
    padding: 3rem;
    color: #7f8c8d;
    font-style: italic;
}

@media (max-width: 768px) {
    .container {
        padding: 1rem;
    }
    
    .header {
        flex-direction: column;
        gap: 1rem;
        text-align: center;
    }
    
    .users-table {
        font-size: 0.875rem;
    }
    
    .actions {
        flex-direction: column;
    }
}
</style>