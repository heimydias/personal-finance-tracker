import React from 'react';
import { colors } from '../../theme/colors';

const Button = ({
  children,
  onClick,
  variant = 'primary',
  type = 'button',
  disabled = false,
  fullWidth = false,
  size = 'medium',
  icon = null,
}) => {
  const variants = {
    primary: {
      backgroundColor: disabled ? colors.lightGray : colors.primary,
      color: colors.text.white,
      border: 'none',
    },
    secondary: {
      backgroundColor: disabled ? colors.lightGray : colors.gray,
      color: colors.text.white,
      border: 'none',
    },
    success: {
      backgroundColor: disabled ? colors.lightGray : colors.emerald,
      color: colors.text.white,
      border: 'none',
    },
    danger: {
      backgroundColor: disabled ? colors.lightGray : colors.danger,
      color: colors.text.white,
      border: 'none',
    },
    warning: {
      backgroundColor: disabled ? colors.lightGray : colors.warning,
      color: colors.text.white,
      border: 'none',
    },
    purple: {
      backgroundColor: disabled ? colors.lightGray : colors.purple,
      color: colors.text.white,
      border: 'none',
    },
    outline: {
      backgroundColor: 'transparent',
      color: disabled ? colors.lightGray : colors.primary,
      border: `1px solid ${disabled ? colors.lightGray : colors.primary}`,
    },
    ghost: {
      backgroundColor: 'rgba(255,255,255,0.2)',
      color: colors.text.white,
      border: '1px solid rgba(255,255,255,0.3)',
    },
  };

  const sizes = {
    small: {
      padding: '0.5rem 1rem',
      fontSize: '0.875rem',
    },
    medium: {
      padding: '0.75rem 1.5rem',
      fontSize: '1rem',
    },
    large: {
      padding: '1rem 2rem',
      fontSize: '1.125rem',
    },
  };

  const baseStyle = {
    ...variants[variant],
    ...sizes[size],
    borderRadius: '6px',
    cursor: disabled ? 'not-allowed' : 'pointer',
    fontWeight: 'bold',
    width: fullWidth ? '100%' : 'auto',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '0.5rem',
  };

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      style={baseStyle}
    >
      {icon && <span>{icon}</span>}
      {children}
    </button>
  );
};

export default Button;
