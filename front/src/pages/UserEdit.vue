<template>
    <div class="container">
        <div class="header">
            <h1>Editar Usuário</h1>
        </div>

        <div class="form-container">
            <form @submit.prevent="updateWithPut" class="user-form">
                <div class="form-group">
                    <label for="name">Nome</label>
                    <input id="name" v-model="form.name" placeholder="Digite o seu nome" required class="form-input" />
                </div>

                <div class="form-group">
                    <label for="birth_date">Data de Nascimento</label>
                    <input id="birth_date" v-model="form.birth_date" type="date" required class="form-input" />
                </div>

                <div class="form-group">
                    <label for="document">CPF</label>
                    <input id="document" v-model="form.document" placeholder="Somente números (11 dígitos)"
                        @input="formatDocument" maxlength="14" class="form-input" />
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="zip">CEP</label>
                        <input id="zip" v-model="form.zip" placeholder="Somente números (8 dígitos)" @input="formatZip"
                            maxlength="9" class="form-input" />
                    </div>

                    <div class="form-group">
                        <label for="address_number">Número da Residência</label>
                        <input id="address_number" v-model="form.address_number" type="text" placeholder="Nº"
                            class="form-input" />
                    </div>
                </div>

                <div class="update-options">
                    <h3>Selecione o que deseja atualizar:</h3>
                    <div class="options-grid">
                        <button type="button" @click="updateWithPut" class="option-btn put-option">
                            🔄 Todos os Campos
                        </button>

                        <button type="button" @click="updateName" class="option-btn patch-option">
                            ✏️ Nome
                        </button>

                        <button type="button" @click="updateDocument" class="option-btn patch-option">
                            📄 CPF
                        </button>

                        <button type="button" @click="updateAddress" class="option-btn patch-option">
                            🏠 Endereço
                        </button>

                        <button type="button" @click="updateBirthDate" class="option-btn patch-option">
                            📅 Data de Nascimento
                        </button>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="button" @click="router.push('/users')" class="btn-cancel">
                        Cancelar
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { reactive, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store/users'
import { notifySuccess, notifyError } from '../utils/notify'

const router = useRouter()
const route = useRoute()
const store = useUserStore()
const loading = ref(false)

const id = parseInt(route.params.id)
const form = reactive({
    id,
    name: '',
    birth_date: '',
    address_number: '',
    document: '',
    zip: ''
})

onMounted(async () => {
    try {
        loading.value = true

        const userData = await store.returnUserForUpdate(id)

        if (userData && userData.length > 0) {
            populateForm(userData[0])
        } else {
            await loadUserFallback()
        }
    } catch (error) {
        if (error.response?.status === 500) {
            notifyError('Erro no servidor. Verifique o backend.')
        } else if (error.response?.status === 404) {
            notifyError('Usuário não encontrado no sistema.')
        } else {
            notifyError('Erro de conexão. Verifique sua rede.')
        }

        await loadUserFallback()
    } finally {
        loading.value = false
    }
})

function populateForm(userData) {
    if (userData.birth_date) {
        form.birth_date = userData.birth_date.includes('T')
            ? userData.birth_date.split('T')[0]
            : userData.birth_date
    }

    Object.assign(form, userData)

    if (form.document) {
        formatDocument() 
    }
    if (form.zip) {
        formatZip() 
    }
}

function formatDocument() {
    let value = form.document.replace(/\D/g, '')
    value = value.substring(0, 11)

    if (value.length > 9) {
        value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
    } else if (value.length > 6) {
        value = value.replace(/(\d{3})(\d{3})(\d{3})/, '$1.$2.$3')
    } else if (value.length > 3) {
        value = value.replace(/(\d{3})(\d{3})/, '$1.$2')
    }

    form.document = value
}

function formatZip() {
    let value = form.zip.replace(/\D/g, '')
    value = value.substring(0, 8)

    if (value.length > 5) {
        value = value.replace(/(\d{5})(\d{3})/, '$1-$2')
    }

    form.zip = value
}

function formatDataForBackend(data) {
    const formatted = { ...data }

    if (formatted.document) {
        formatted.document = formatted.document.replace(/\D/g, '')
    }

    if (formatted.zip) {
        formatted.zip = formatted.zip.replace(/\D/g, '')
    }

    if (formatted.birth_date) {
        formatted.birth_date = formatted.birth_date
    }

    return formatted
}

async function updateWithPut() {
    if (loading.value) return

    loading.value = true
    try {
        const formattedData = formatDataForBackend(form)
        await store.updateUser(formattedData)
        notifySuccess("Usuário atualizado com sucesso! (PUT)")
        router.push('/users')
    } catch (err) {
        notifyError(err.response?.data?.message || "Erro ao atualizar usuário")
    } finally {
        loading.value = false
    }
}

async function updateName() {
    if (loading.value || !form.name.trim()) {
        notifyError("Nome é obrigatório")
        return
    }

    loading.value = true
    try {
        const data = { id: form.id, name: form.name }
        await store.updateUserName(data)
        notifySuccess("Nome atualizado com sucesso! (PATCH)")
        router.push('/users')
    } catch (err) {
        notifyError(err.response?.data?.message || "Erro ao atualizar nome")
    } finally {
        loading.value = false
    }
}

async function updateDocument() {
    if (loading.value) return

    loading.value = true
    try {
        const formattedDoc = form.document.replace(/\D/g, '')
        const data = { id: form.id, document: formattedDoc }
        await store.updateUserDocument(data)
        notifySuccess("Documento atualizado com sucesso! (PATCH)")
        router.push('/users')
    } catch (err) {
        notifyError(err.response?.data?.message || "Erro ao atualizar documento")
    } finally {
        loading.value = false
    }
}

async function updateAddress() {
    if (loading.value) return

    loading.value = true
    try {
        const formattedZip = form.zip.replace(/\D/g, '')
        const data = {
            id: form.id,
            zip: formattedZip,
            address_number: form.address_number
        }
        await store.updateUserAddress(data)
        notifySuccess("Endereço atualizado com sucesso! (PATCH)")
        router.push('/users')
    } catch (err) {
        notifyError(err.response?.data?.message || "Erro ao atualizar endereço")
    } finally {
        loading.value = false
    }
}

async function updateBirthDate() {
    if (loading.value || !form.birth_date) {
        notifyError("Data de nascimento é obrigatória")
        return
    }

    loading.value = true
    try {
        const data = { id: form.id, birth_date: form.birth_date }
        await store.updateUserBirthDate(data)
        notifySuccess("Data de nascimento atualizada com sucesso! (PATCH)")
        router.push('/users')
    } catch (err) {
        notifyError(err.response?.data?.message || "Erro ao atualizar data de nascimento")
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.container {
    max-width: 800px;
    margin: 0 auto;
    padding: 2rem;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.header {
    text-align: center;
    margin-bottom: 2rem;
}

.header h1 {
    color: #2c3e50;
    margin-bottom: 1rem;
    font-size: 2rem;
}

.btn-back {
    color: #3498db;
    text-decoration: none;
    font-size: 0.9rem;
    transition: color 0.3s;
}

.btn-back:hover {
    color: #2980b9;
    text-decoration: underline;
}

.form-container {
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    padding: 2rem;
}

.user-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.form-group {
    display: flex;
    flex-direction: column;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
}

label {
    font-weight: 600;
    color: #2c3e50;
    margin-bottom: 0.5rem;
    font-size: 0.9rem;
}

.form-input {
    padding: 0.75rem 1rem;
    border: 2px solid #e9ecef;
    border-radius: 8px;
    font-size: 1rem;
    transition: all 0.3s ease;
    background-color: #f8f9fa;
}

.form-input:focus {
    outline: none;
    border-color: #3498db;
    background-color: white;
    box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.update-options {
    margin: 2rem 0;
    padding: 1.5rem;
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 4px solid #3498db;
}

.update-options h3 {
    color: #2c3e50;
    margin-bottom: 1rem;
    font-size: 1.2rem;
}

.options-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
}

.option-btn {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    padding: 1rem;
    border: 2px solid #e9ecef;
    border-radius: 8px;
    background: white;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: left;
}

.option-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.put-option:hover {
    border-color: #e74c3c;
    background: #fdf2f2;
}

.patch-option:hover {
    border-color: #3498db;
    background: #f0f8ff;
}

.method-badge {
    background: #34495e;
    color: white;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-size: 0.7rem;
    font-weight: bold;
    margin-bottom: 0.5rem;
}

.put-option .method-badge {
    background: #e74c3c;
}

.patch-option .method-badge {
    background: #3498db;
}

.form-actions {
    display: flex;
    gap: 1rem;
    justify-content: center;
    margin-top: 2rem;
    padding-top: 1.5rem;
    border-top: 1px solid #e9ecef;
}

.btn-cancel {
    background-color: #95a5a6;
    color: white;
    padding: 0.75rem 2rem;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 1rem;
    transition: background-color 0.3s;
    min-width: 120px;
}

.btn-cancel:hover {
    background-color: #7f8c8d;
}

.btn-submit {
    background-color: #27ae60;
    color: white;
    padding: 0.75rem 2rem;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 1rem;
    font-weight: 600;
    transition: background-color 0.3s;
    min-width: 120px;
}

.btn-submit:hover {
    background-color: #219a52;
}

@media (max-width: 768px) {
    .container {
        padding: 1rem;
    }

    .form-container {
        padding: 1.5rem;
    }

    .form-row,
    .options-grid {
        grid-template-columns: 1fr;
    }

    .form-actions {
        flex-direction: column;
        align-items: center;
    }

    .btn-cancel,
    .btn-submit {
        width: 100%;
        max-width: 200px;
    }
}

@media (max-width: 480px) {
    .header h1 {
        font-size: 1.5rem;
    }

    .form-input {
        padding: 0.6rem 0.8rem;
    }

    .update-options {
        padding: 1rem;
    }
}
</style>