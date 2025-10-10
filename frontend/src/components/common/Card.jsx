import React from 'react';
import { colors } from '../../theme/colors';

const Card = ({ children, padding = '2rem', style = {} }) => {
  const cardStyle = {
    backgroundColor: colors.cardBg,
    borderRadius: '8px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
    padding,
    ...style,
  };

  return <div style={cardStyle}>{children}</div>;
};

export default Card;
