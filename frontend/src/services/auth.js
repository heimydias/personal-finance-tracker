import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || '/personal-finance-tracker/api/v1';

const authAPI = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

authAPI.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
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
          const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
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
      const response = await authAPI.post('/auth/login', {
        email,
        password,
      });

      const { accessToken, refreshToken, userEmail, userRole } = response.data;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('userEmail', userEmail);
      localStorage.setItem('userRole', userRole);

      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      if (errorData) {
        let message = errorData.detail || errorData.message || 'Erro no login';

        if (message === 'Invalid email or password') {
          message = 'Email ou senha inválidos';
        } else if (message.includes('User already exists with email')) {
          message = 'Este email já está em uso';
        }

        throw { message };
      }
      throw { message: 'Erro no login' };
    }
  },

  async register(email, password, role = 'USER') {
    try {
      const response = await authAPI.post('/auth/register', {
        email,
        password,
        role,
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      if (errorData) {
        let message = errorData.detail || errorData.message || 'Erro no registro';

        if (message.includes('User already exists with email')) {
          message = 'Este email já está em uso';
        }

        throw { message };
      }
      throw { message: 'Erro no registro' };
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