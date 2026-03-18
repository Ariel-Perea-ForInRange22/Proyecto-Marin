const API_URL = 'http://127.0.0.1:8080';

// Función para obtener el token guardado
function getToken() {
    return localStorage.getItem('devcore_token');
}

// Función para verificar si estamos logueados
function isAuthenticated() {
    return !!getToken();
}

// Función para cerrar sesión
function logout() {
    localStorage.removeItem('devcore_token');
    localStorage.removeItem('devcore_user');
    window.location.href = 'login.html';
}

// Wrapper para hacer peticiones autenticadas
async function fetchAuth(endpoint, options = {}) {
    const token = getToken();
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_URL}${endpoint}`, config);
        if (response.status === 401) {
            // Token expirado o inválido
            logout();
            return null;
        }
        return response;
    } catch (error) {
        console.error('Error de red:', error);
        throw error;
    }
}
