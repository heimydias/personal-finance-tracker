import { Navigate } from 'react-router-dom';
import { authService } from '../services/auth';

const ProtectedRoute = ({ children, requireAdmin = false }) => {
  if (!authService.isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  if (requireAdmin) {
    const userInfo = authService.getUserInfo();
    if (userInfo.role !== 'ADMIN') {
      return <Navigate to="/" replace />;
    }
  }

  return children;
};

export default ProtectedRoute;
