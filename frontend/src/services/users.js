import authAPI from './auth';

export const usersService = {
  async listUsers(page = 0, size = 10, sort = 'email', order = 'asc') {
    try {
      const response = await authAPI.get('/v1/users', {
        headers: {
          page,
          size,
          sort,
          order,
        },
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao listar usuários');
    }
  },

  async getUserById(id) {
    try {
      const response = await authAPI.get(`/v1/users/${id}`);
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      throw new Error(errorData?.detail || errorData?.message || 'Erro ao buscar usuário');
    }
  },

  async createUser(name, email, password, role = 'USER') {
    try {
      const response = await authAPI.post('/v1/users', {
        name,
        email,
        password,
        role,
      });
      return response.data;
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao criar usuário';

      if (errorData?.fieldErrors && errorData.fieldErrors.length > 0) {
        message = errorData.fieldErrors;
      } else if (message.includes('User already exists')) {
        message = 'Este email já está em uso';
      }

      const err = new Error(Array.isArray(message) ? message.join('\n') : message);
      err.fieldErrors = Array.isArray(message) ? message : null;
      throw err;
    }
  },

  async updateUser(id, name, email, password, role) {
    try {
      const payload = {};
      if (name) payload.name = name;
      if (email) payload.email = email;
      if (password) payload.password = password;
      if (role) payload.role = role;

      await authAPI.put(`/v1/users/${id}`, payload);
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao atualizar usuário';

      if (errorData?.fieldErrors && errorData.fieldErrors.length > 0) {
        message = errorData.fieldErrors;
      } else if (message.includes('Email já existe')) {
        message = 'Este email já está em uso';
      }

      const err = new Error(Array.isArray(message) ? message.join('\n') : message);
      err.fieldErrors = Array.isArray(message) ? message : null;
      throw err;
    }
  },

  async deleteUser(id) {
    try {
      await authAPI.delete(`/v1/users/${id}`);
    } catch (error) {
      const errorData = error.response?.data;
      let message = errorData?.detail || errorData?.message || 'Erro ao deletar usuário';

      if (message.includes('administrador padrão do sistema')) {
        message = 'O administrador padrão do sistema não pode ser deletado';
      } else if (message.includes('Usuários só podem deletar')) {
        message = 'Você não tem permissão para deletar este usuário';
      }

      throw new Error(message);
    }
  },
};