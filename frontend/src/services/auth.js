import axios from 'axios';

const authAPI = axios.create({
  baseURL: '/personal-finance-tracker/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

authAPI.interceptors.request.use(
  (config) => {
    console.log('Interceptor REQUEST - URL:', config.url);
    console.log('Interceptor REQUEST - baseURL:', config.baseURL);
    console.log('Interceptor REQUEST - full URL:', config.baseURL + config.url);
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('Interceptor REQUEST ERROR:', error);
    return Promise.reject(error);
  }
);

authAPI.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (refreshToken) {
          const response = await authAPI.post('/v1/auth/refresh-token', {
            refreshToken: refreshToken
          });

          const { accessToken, refreshToken: newRefreshToken } = response.data;

          localStorage.setItem('accessToken', accessToken);
          localStorage.setItem('refreshToken', newRefreshToken);

          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return authAPI(originalRequest);
        }
      } catch (refreshError) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export const authService = {
  async login(email, password) {
    try {
      console.log('Tentando login com:', { email, password });
      const response = await authAPI.post('/v1/auth/token', {
        email,
        password,
      });

      console.log('Response do login:', response.data);
      const { accessToken, refreshToken, userEmail, userRole } = response.data;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('userEmail', userEmail);
      localStorage.setItem('userRole', userRole);

      return response.data;
    } catch (error) {
      console.error('ERRO NO LOGIN:', error);
      console.error('Error response:', error.response);
      console.error('Error message:', error.message);

      const errorData = error.response?.data;
      if (errorData) {
        let message = errorData.detail || errorData.message || 'Erro no login';

        if (message === 'Invalid email or password') {
          message = 'Email ou senha inválidos';
        } else if (message.includes('User already exists with email')) {
          message = 'Este email já está em uso';
        }

        throw new Error(message);
      }
      throw new Error('Erro no login');
    }
  },

  async register(email, password, role = 'USER') {
    try {
      const response = await authAPI.post('/v1/users', {
        email,
        password,
        role,
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      if (errorData) {
        let message = errorData.detail || errorData.message || 'Erro no registro';

        // Se tem fieldErrors, usar eles em vez da mensagem genérica
        if (errorData.fieldErrors && errorData.fieldErrors.length > 0) {
          message = errorData.fieldErrors;
        } else if (message.includes('User already exists with email')) {
          message = 'Este email já está em uso';
        }

        throw new Error(message);
      }
      throw new Error('Erro no registro');
    }
  },

  async registerAdmin(email, password) {
    if (!this.isAuthenticated()) {
      throw new Error('Você precisa estar logado para criar usuários admin');
    }

    const userInfo = this.getUserInfo();
    if (userInfo.role !== 'ADMIN') {
      throw new Error('Apenas administradores podem criar outros administradores');
    }

    try {
      const response = await authAPI.post('/v1/users', {
        email,
        password,
        role: 'ADMIN',
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      if (errorData) {
        let message = errorData.detail || errorData.message || 'Erro no registro de admin';

        if (message.includes('User already exists with email')) {
          message = 'Este email já está em uso';
        } else if (message.includes('sem autenticação de admin válida')) {
          message = 'Token de admin inválido ou expirado';
        }

        throw new Error(message);
      }
      throw new Error('Erro no registro de admin');
    }
  },

  logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userRole');
  },

  isAuthenticated() {
    return !!localStorage.getItem('accessToken');
  },

  getUserInfo() {
    return {
      email: localStorage.getItem('userEmail'),
      role: localStorage.getItem('userRole'),
    };
  },
};

export default authAPI;