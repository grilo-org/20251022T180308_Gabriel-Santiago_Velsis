<template>
    <div class="container">
        <div class="header">
            <h1>Criar Novo Usuário</h1>
            <router-link to="/users" class="btn-back">
                ← Voltar para Lista
            </router-link>
        </div>

        <div class="form-container">
            <form @submit.prevent="save" class="user-form">
                <div class="form-group" :class="{ 'error': fieldErrors.name }">
                    <label for="name">Nome</label>
                    <input 
                        id="name"
                        v-model="form.name" 
                        placeholder="Digite o nome completo" 
                        required 
                        class="form-input"
                        @input="validateForm"
                    />
                    <span class="error-message" v-if="fieldErrors.name">{{ fieldErrors.name }}</span>
                </div>

                <div class="form-group" :class="{ 'error': fieldErrors.birth_date }">
                    <label for="birth_date">Data de Nascimento</label>
                    <input 
                        id="birth_date"
                        v-model="form.birth_date" 
                        type="date" 
                        required 
                        class="form-input"
                        @input="validateForm"
                    />
                    <span class="error-message" v-if="fieldErrors.birth_date">{{ fieldErrors.birth_date }}</span>
                </div>

                <div class="form-group" :class="{ 'error': fieldErrors.document }">
                    <label for="document">CPF</label>
                    <input 
                        id="document"
                        v-model="form.document" 
                        placeholder="Somente números (11 dígitos)"
                        @input="formatDocument"
                        required
                        maxlength="14"
                        class="form-input"
                    />
                    <span class="error-message" v-if="fieldErrors.document">{{ fieldErrors.document }}</span>
                </div>

                <div class="form-row">
                    <div class="form-group" :class="{ 'error': fieldErrors.zip }">
                        <label for="zip">CEP</label>
                        <input 
                            id="zip"
                            v-model="form.zip" 
                            placeholder="Somente números (8 dígitos)"
                            @input="formatZip"
                            required
                            maxlength="9"
                            class="form-input"
                        />
                        <span class="error-message" v-if="fieldErrors.zip">{{ fieldErrors.zip }}</span>
                    </div>
                    
                    <div class="form-group" :class="{ 'error': fieldErrors.address_number }">
                        <label for="address_number">Número da Residência</label>
                        <input 
                            id="address_number"
                            v-model="form.address_number" 
                            type="text" 
                            placeholder="Nº" 
                            class="form-input"
                        />
                        <span class="error-message" v-if="fieldErrors.address_number">{{ fieldErrors.address_number }}</span>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="button" @click="router.push('/users')" class="btn-cancel">
                        Cancelar
                    </button>
                    <button type="submit" class="btn-submit" :disabled="loading || !isFormValid">
                        {{ loading ? 'Salvando...' : 'Salvar Usuário' }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/users'
import { notifySuccess, notifyError } from '../utils/notify'

const router = useRouter()
const store = useUserStore()
const loading = ref(false)

const form = reactive({
    name: '',
    birth_date: '',
    address_number: '',
    document: '',
    zip: ''
})

const fieldErrors = reactive({
    name: '',
    birth_date: '',
    document: '',
    zip: '',
    address_number: ''
})

const isFormValid = computed(() => {
    return form.name.trim() !== '' && form.birth_date !== '' && form.document !== '' && form.zip !== ''
})

function validateForm() {
    if (form.name.trim() !== '' && fieldErrors.name) {
        fieldErrors.name = ''
    }
    if (form.birth_date !== '' && fieldErrors.birth_date) {
        fieldErrors.birth_date = ''
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

function clearErrors() {
    Object.keys(fieldErrors).forEach(key => {
        fieldErrors[key] = ''
    })
}

function formatDataForBackend() {
    const data = { ...form }
    
    if (data.document) {
        data.document = data.document.replace(/\D/g, '')
    }
    
    if (data.zip) {
        data.zip = data.zip.replace(/\D/g, '')
    }
    
    if (data.birth_date) {
        data.birth_date = data.birth_date
    }
    
    return data
}

function handleBackendErrors(error) {
    clearErrors()
    
    if (error.response?.data) {
        const errorData = error.response.data
        
        if (errorData.errors) {
            errorData.errors.forEach(err => {
                const fieldName = err.fieldName || err.field
                if (fieldName && fieldErrors.hasOwnProperty(fieldName)) {
                    fieldErrors[fieldName] = err.message || err.defaultMessage
                }
            })
        } 
        else if (errorData.message) {
            const message = errorData.message.toLowerCase()
            
            if (message.includes('nome') || message.includes('name')) {
                fieldErrors.name = errorData.message
            } else if (message.includes('data') || message.includes('birth') || message.includes('nascimento')) {
                fieldErrors.birth_date = errorData.message
            } else if (message.includes('cpf') || message.includes('document')) {
                fieldErrors.document = errorData.message
            } else if (message.includes('cep') || message.includes('zip')) {
                fieldErrors.zip = errorData.message
            } else if (message.includes('número') || message.includes('number')) {
                fieldErrors.address_number = errorData.message
            } else {
                notifyError(errorData.message)
            }
        } else {
            notifyError("Erro ao criar usuário")
        }
    } else {
        notifyError("Erro de conexão com o servidor")
    }
}

async function save() {
    if (loading.value || !isFormValid.value) return
    
    clearErrors()
    loading.value = true

    try {
        const formattedData = formatDataForBackend()
        await store.createUser(formattedData)
        notifySuccess("Usuário criado com sucesso!")
        router.push('/users')
    } catch (err) {
        handleBackendErrors(err)
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.container {
    max-width: 600px;
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
    position: relative;
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

.form-input::placeholder {
    color: #adb5bd;
}

.form-input:required {
    border-left: 3px solid #e74c3c;
}

.form-input:required:valid {
    border-left: 3px solid #27ae60;
}

.form-group.error .form-input {
    border-color: #e74c3c;
    background-color: #fdf2f2;
}

.error-message {
    color: #e74c3c;
    font-size: 0.8rem;
    margin-top: 0.25rem;
    font-weight: 500;
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

.btn-submit:hover:not(:disabled) {
    background-color: #219a52;
}

.btn-submit:disabled {
    background-color: #bdc3c7;
    cursor: not-allowed;
    opacity: 0.7;
}

.btn-submit:active:not(:disabled) {
    transform: translateY(1px);
}

.form-group label::after {
    content: ' *';
    color: #e74c3c;
}

.form-group:has(input:not([required])) label::after {
    content: '';
}

.form-actions:has(.btn-submit:not(:disabled)) {
    border-top-color: #27ae60;
}

@media (max-width: 768px) {
    .container {
        padding: 1rem;
    }
    
    .form-container {
        padding: 1.5rem;
    }
    
    .form-row {
        grid-template-columns: 1fr;
        gap: 1.5rem;
    }
    
    .form-actions {
        flex-direction: column;
        align-items: center;
    }
    
    .btn-cancel,
    .btn-submit {
        width: 100%;
        max-width: 200px;
        text-align: center;
    }
}

@media (max-width: 480px) {
    .header h1 {
        font-size: 1.5rem;
    }
    
    .form-input {
        padding: 0.6rem 0.8rem;
    }
    
    .form-actions {
        gap: 0.75rem;
    }
    
    .btn-cancel,
    .btn-submit {
        padding: 0.6rem 1.5rem;
    }
}
</style>