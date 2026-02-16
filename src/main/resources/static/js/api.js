
const API = (() => {
    const BASE_URL = '/api';

    async function request(url, options = {}) {
        try {
            const response = await fetch(url, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
                throw new Error('Требуется аутентификация');
            }

            if (response.status === 400) {
                const errorData = await response.json();
                return {
                    success: false,
                    fieldErrors: errorData.fieldErrors || {},
                    message: errorData.message
                };
            }

            if (response.status === 500) {
                const errorData = await response.json();
                return {
                    success: false,
                    message: errorData.message || 'Внутренняя ошибка сервера'
                };
            }

            if (response.status === 404) {
                return {
                    success: false,
                    message: 'Ресурс не найден'
                };
            }

            if (response.status === 204) {
                return { success: true };
            }

            const data = await response.json();
            return { success: true, data };

        } catch (error) {
            console.error('API request error:', error);
            return {
                success: false,
                message: error.message || 'Ошибка сети'
            };
        }
    }

    const users = {
        async getAll() {
            return request(`${BASE_URL}/admin/users`);
        },

        async getById(id) {
            return request(`${BASE_URL}/admin/users/${id}`);
        },

        async create(userData) {
            return request(`${BASE_URL}/admin/users`, {
                method: 'POST',
                body: JSON.stringify(userData)
            });
        },

        async update(id, userData) {
            return request(`${BASE_URL}/admin/users/${id}`, {
                method: 'PUT',
                body: JSON.stringify(userData)
            });
        },

        async delete(id) {
            return request(`${BASE_URL}/admin/users/${id}`, {
                method: 'DELETE'
            });
        }
    };

    const roles = {
        async getAll() {
            return request(`${BASE_URL}/admin/roles`);
        }
    };

    const profile = {
        async get() {
            return request(`${BASE_URL}/user/profile`);
        }
    };

    return {
        users,
        roles,
        profile
    };
})();
