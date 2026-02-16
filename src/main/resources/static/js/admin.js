
class AdminPage {
    constructor() {
        this.users = [];
        this.roles = [];
        this.currentUser = null;
        this.isEditMode = false;
    }

    async init() {
        this.attachEventListeners();
        await this.loadRoles();
        await this.loadUsers();
    }

    async loadRoles() {
        const result = await API.roles.getAll();
        
        if (result.success) {
            this.roles = result.data;
        } else {
            this.roles = [];
            console.error('Ошибка загрузки ролей:', result.message);
        }
    }

    async loadUsers() {
        this.showLoading();
        
        const result = await API.users.getAll();
        
        if (result.success) {
            this.users = result.data;
            this.renderUsersTable();
        } else {
            this.showError(result.message || 'Ошибка загрузки пользователей');
        }
        
        this.hideLoading();
    }



    renderUsersTable() {
        const tbody = document.querySelector('#usersTable tbody');
        if (!tbody) return;

        tbody.innerHTML = '';

        if (this.users.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">Нет пользователей</td></tr>';
            return;
        }

        this.users.forEach(user => {
            tbody.appendChild(this.createUserRow(user));
        });
    }

    createUserRow(user) {
        const tr = document.createElement('tr');
        
        const roleNames = user.roles.map(role => role.name).join(', ');
        
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.firstName} ${user.lastName}</td>
            <td>${user.email}</td>
            <td>${roleNames}</td>
            <td>
                <button class="btn btn-sm btn-primary edit-btn" data-id="${user.id}">
                    Редактировать
                </button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${user.id}">
                    Удалить
                </button>
            </td>
        `;
        
        return tr;
    }

    attachEventListeners() {
        const createBtn = document.getElementById('createUserBtn');
        if (createBtn) {
            createBtn.addEventListener('click', () => this.showUserModal());
        }

        const tbody = document.querySelector('#usersTable tbody');
        if (tbody) {
            tbody.addEventListener('click', (e) => {
                if (e.target.classList.contains('edit-btn')) {
                    const userId = parseInt(e.target.dataset.id);
                    this.showUserModal(userId);
                } else if (e.target.classList.contains('delete-btn')) {
                    const userId = parseInt(e.target.dataset.id);
                    this.deleteUser(userId);
                }
            });
        }

        const saveBtn = document.getElementById('saveUserBtn');
        if (saveBtn) {
            saveBtn.addEventListener('click', () => this.saveUser());
        }

        const modal = document.getElementById('userModal');
        if (modal) {
            modal.addEventListener('hidden.bs.modal', () => {
                this.clearErrors();
                document.getElementById('userForm').reset();
            });
        }
    }

    showLoading() {
        const loading = document.getElementById('loading');
        if (loading) {
            loading.style.display = 'block';
        }
    }

    hideLoading() {
        const loading = document.getElementById('loading');
        if (loading) {
            loading.style.display = 'none';
        }
    }

    async showUserModal(userId = null) {
        this.isEditMode = !!userId;
        this.currentUser = null;
        this.clearErrors();

        const modal = new bootstrap.Modal(document.getElementById('userModal'));
        const modalTitle = document.getElementById('userModalLabel');
        const form = document.getElementById('userForm');

        if (this.isEditMode) {
            modalTitle.textContent = 'Редактировать пользователя';

            const result = await API.users.getById(userId);
            if (result.success) {
                this.currentUser = result.data;
                this.fillUserForm(this.currentUser);
            } else {
                this.showError(result.message || 'Ошибка загрузки пользователя');
                return;
            }
        } else {
            modalTitle.textContent = 'Создать пользователя';
            form.reset();
        }

        this.renderRolesCheckboxes();
        modal.show();
    }

    renderRolesCheckboxes() {
        const container = document.getElementById('rolesContainer');
        if (!container) return;

        container.innerHTML = '';

        this.roles.forEach(role => {
            const isChecked = this.currentUser && 
                this.currentUser.roles.some(r => r.id === role.id);

            const div = document.createElement('div');
            div.className = 'form-check';
            div.innerHTML = `
                <input class="form-check-input" type="checkbox" 
                    value="${role.id}" id="role${role.id}" 
                    ${isChecked ? 'checked' : ''}>
                <label class="form-check-label" for="role${role.id}">
                    ${role.name}
                </label>
            `;
            container.appendChild(div);
        });
    }

    fillUserForm(user) {
        document.getElementById('username').value = user.username;
        document.getElementById('firstName').value = user.firstName;
        document.getElementById('lastName').value = user.lastName;
        document.getElementById('email').value = user.email;
        document.getElementById('password').value = '';

        const passwordInput = document.getElementById('password');
        if (passwordInput) {
            passwordInput.placeholder = 'Оставьте пустым, чтобы не менять';
        }
    }

    getFormData() {
        const roleCheckboxes = document.querySelectorAll('#rolesContainer input[type="checkbox"]:checked');
        const roleIds = Array.from(roleCheckboxes).map(cb => parseInt(cb.value));

        const data = {
            username: document.getElementById('username').value.trim(),
            firstName: document.getElementById('firstName').value.trim(),
            lastName: document.getElementById('lastName').value.trim(),
            email: document.getElementById('email').value.trim(),
            password: document.getElementById('password').value,
            roleIds: roleIds
        };

        return data;
    }

    async saveUser() {
        this.clearErrors();

        const formData = this.getFormData();

        if (!this.validateForm(formData)) {
            return;
        }

        let result;
        if (this.isEditMode) {
            result = await API.users.update(this.currentUser.id, formData);
        } else {
            result = await API.users.create(formData);
        }

        if (result.success) {
            this.showSuccess(this.isEditMode ? 'Пользователь обновлен' : 'Пользователь создан');

            const modal = bootstrap.Modal.getInstance(document.getElementById('userModal'));
            modal.hide();

            await this.loadUsers();
        } else {
            if (result.fieldErrors) {
                this.showFormErrors(result.fieldErrors);
            } else {
                this.showError(result.message || 'Ошибка сохранения пользователя');
            }
        }
    }

    validateForm(data) {
        let isValid = true;

        if (!data.username) {
            this.showFieldError('username', 'Имя пользователя обязательно');
            isValid = false;
        }

        if (!data.firstName) {
            this.showFieldError('firstName', 'Имя обязательно');
            isValid = false;
        }

        if (!data.lastName) {
            this.showFieldError('lastName', 'Фамилия обязательна');
            isValid = false;
        }

        if (!data.email) {
            this.showFieldError('email', 'Email обязателен');
            isValid = false;
        } else if (!this.isValidEmail(data.email)) {
            this.showFieldError('email', 'Некорректный формат email');
            isValid = false;
        }

        if (!this.isEditMode && !data.password) {
            this.showFieldError('password', 'Пароль обязателен');
            isValid = false;
        } else if (data.password && data.password.length < 4) {
            this.showFieldError('password', 'Пароль должен содержать минимум 4 символа');
            isValid = false;
        }

        if (data.roleIds.length === 0) {
            this.showError('Выберите хотя бы одну роль');
            isValid = false;
        }

        return isValid;
    }

    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    async deleteUser(userId) {
        if (!confirm('Вы уверены, что хотите удалить этого пользователя?')) {
            return;
        }

        const result = await API.users.delete(userId);

        if (result.success) {
            this.showSuccess('Пользователь удален');
            await this.loadUsers();
        } else {
            this.showError(result.message || 'Ошибка удаления пользователя');
        }
    }

    showError(message) {
        const alertDiv = document.getElementById('alertContainer');
        if (!alertDiv) return;

        alertDiv.innerHTML = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    }

    showSuccess(message) {
        const alertDiv = document.getElementById('alertContainer');
        if (!alertDiv) return;

        alertDiv.innerHTML = `
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;

        setTimeout(() => {
            const alert = alertDiv.querySelector('.alert');
            if (alert) {
                alert.remove();
            }
        }, 3000);
    }

    showFormErrors(fieldErrors) {
        for (const [field, message] of Object.entries(fieldErrors)) {
            this.showFieldError(field, message);
        }
    }

    showFieldError(fieldName, message) {
        const input = document.getElementById(fieldName);
        if (!input) return;

        input.classList.add('is-invalid');

        let feedback = input.nextElementSibling;
        if (!feedback || !feedback.classList.contains('invalid-feedback')) {
            feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';
            input.parentNode.insertBefore(feedback, input.nextSibling);
        }

        feedback.textContent = message;
    }

    clearErrors() {
        document.querySelectorAll('.is-invalid').forEach(input => {
            input.classList.remove('is-invalid');
        });

        document.querySelectorAll('.invalid-feedback').forEach(feedback => {
            feedback.remove();
        });

        const alertDiv = document.getElementById('alertContainer');
        if (alertDiv) {
            alertDiv.innerHTML = '';
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const adminPage = new AdminPage();
    adminPage.init();
});
