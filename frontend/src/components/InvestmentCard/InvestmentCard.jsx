import React from 'react';
import './InvestmentCard.css'; 

const formatDate = (dateString) => {
  if (!dateString) return '-';
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString;
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  } catch (e) {
    console.error('Erro ao formatar data:', e);
    return dateString;
  }
};

const formatCurrency = (value, showSymbol = true) => {
  if (typeof value !== 'number' || isNaN(value)) return '-';
  return value.toLocaleString('pt-BR', {
    style: showSymbol ? 'currency' : 'decimal',
    currency: 'BRL',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

function InvestmentCard({ investment, onClick }) { 
  if (!investment) {
    return null;
  }

  const handleCardClick = () => {
    if (onClick) {
      onClick(investment); 
    }
  };

  return (
    <div 
      className={`investment-row ${investment.isHistory ? 'history-row' : ''}`}
      onClick={handleCardClick}
      style={{ cursor: onClick ? 'pointer' : 'default' }} 
    >
      <div className="investment-cell investment-name-cell">
        <span className="main-name">{investment.assetName || 'N/A'}</span>
        {investment.assetSubtitle && <span className="subtitle">{investment.assetSubtitle}</span>}
      </div>
      <div className="investment-cell investment-type-cell">
        {investment.type && <span className="investment-type-tag">{investment.type}</span>}
      </div>
      <div className="investment-cell investment-value-cell">
        {formatCurrency(investment.value)}
      </div>
      <div className="investment-cell investment-date-cell">
        {formatDate(investment.investmentDate)}
      </div>
      <div className="investment-cell investment-maturity-cell">
        {formatDate(investment.maturityDate)}
      </div>
    </div>
  );
}

export default InvestmentCard;
