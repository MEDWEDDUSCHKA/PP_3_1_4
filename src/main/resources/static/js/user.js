
class UserPage {
    constructor() {
        this.profile = null;
    }

    async init() {
        await this.loadProfile();
    }

    async loadProfile() {
        this.showLoading();

        const result = await API.profile.get();

        if (result.success) {
            this.profile = result.data;
            this.renderProfile();
        } else {
            this.showError(result.message || 'Ошибка загрузки профиля');
        }

        this.hideLoading();
    }

    renderProfile() {
        if (!this.profile) return;

        const usernameEl = document.getElementById('profileUsername');
        if (usernameEl) {
            usernameEl.textContent = this.profile.username;
        }

        const fullNameEl = document.getElementById('profileFullName');
        if (fullNameEl) {
            fullNameEl.textContent = `${this.profile.firstName} ${this.profile.lastName}`;
        }

        const emailEl = document.getElementById('profileEmail');
        if (emailEl) {
            emailEl.textContent = this.profile.email;
        }

        const rolesEl = document.getElementById('profileRoles');
        if (rolesEl) {
            const roleNames = this.profile.roles.map(role => role.name).join(', ');
            rolesEl.textContent = roleNames;
        }
    }

    showLoading() {
        const loading = document.getElementById('loading');
        if (loading) {
            loading.style.display = 'block';
        }

        const content = document.getElementById('profileContent');
        if (content) {
            content.style.display = 'none';
        }
    }

    hideLoading() {
        const loading = document.getElementById('loading');
        if (loading) {
            loading.style.display = 'none';
        }

        const content = document.getElementById('profileContent');
        if (content) {
            content.style.display = 'block';
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
}

document.addEventListener('DOMContentLoaded', () => {
    const userPage = new UserPage();
    userPage.init();
});
