import React, { useState } from "react";
import "./DashboardPage.css";
import { FaChartBar } from "react-icons/fa";
import SummaryCard from "../../components/SummaryCard/SummaryCard";
import { useNavigate } from "react-router-dom";
import { useWalletData } from "../../hooks/useWalletData";
import { useDisplayableInvestments } from "../../hooks/useDisplayableInvestments";
import InvestmentListSection from "../../components/InvestmentListSection/InvestmentListSection";
import { useAuthGuard } from "../../hooks/useAuthGuard";

function DashboardPage() {
  useAuthGuard();
  const navigate = useNavigate();

  const {
    userActiveInvestments,
    allAssets,
    totalBalance,
    expectedReturn,
    countInvestments,
    loadingData,
    error,
    refreshWalletData
  } = useWalletData();

  const displayableActiveInvestments = useDisplayableInvestments(
    userActiveInvestments,
    allAssets,
    loadingData,
    false
  );

  const recentInvestments = displayableActiveInvestments
    .slice()
    .reverse()
    .slice(0, 2);

    const opportunities = allAssets
    .sort((a, b) => b.profitability - a.profitability)
    .slice(0, 2)
    .map((asset) => ({
      id: asset.id,
      name: asset.name,
      type: asset.assetType.replace('_', ' '),
      description: `Vencimento em ${new Date(asset.maturityDate).toLocaleDateString("pt-BR")}`,
      profitability: `${(asset.profitability * 100).toFixed(2)}%`,
    }));

  const handleInvestNowButton = () => {
    navigate("/assets");
  };

  const handleViewAssetDetails = () => {
    navigate("/assets");
  }

  return (
    <div className="new-dashboard-container">
      <div className="new-dashboard-header">
        <h1>Dashboard</h1>
      </div>

      <div className="new-dashboard-main-content">
        <div className="new-dashboard-left-column">
        <SummaryCard
          totalBalance={totalBalance}
          expectedReturn={expectedReturn}
          countInvestments={countInvestments}
          loading={loadingData}
          error={error}
        />

          <div className="new-recent-investments-card">
            <h2>Investimentos Recentes</h2>
            {recentInvestments.length === 0 ? (
              <p className="new-empty-message">
                {loadingData ? "Carregando os investimentos recentes..." : "Nenhum investimento recente para exibir."}
              </p>
            ) : (
              <InvestmentListSection
                title=""
                investmentsList={recentInvestments}
                isLoading={loadingData}
                specificError={error}
                onInvestmentClick={() => {}}
                refreshWalletData={refreshWalletData}
              />
            )}
          </div>
        </div>

        <div className="new-dashboard-right-column">
          <div className="new-welcome-card">
            <h3>Bem-vindo</h3>
            <button onClick={handleInvestNowButton} className="invest-now-button">
              <FaChartBar /> Investir Agora
            </button>
          </div>

          <div className="new-opportunities-card">
            <h3>Melhores Oportunidades</h3>
            <div className="new-opportunities-list">
              {opportunities.map((opp) => (
                <div key={opp.id} className="new-opportunity-item">
                  <div className="new-opportunity-header">
                    <b className="new-opportunity-item-title">{opp.name}</b>
                    <span className="new-opportunity-profitability">{opp.profitability}</span>
                  </div>
                  <p className="new-opportunity-type">{opp.type}</p>
                  <p className="new-opportunity-description">{opp.description}</p>
                  <div className="new-opportunity-footer">
                    <button className="view-details-button" onClick={handleViewAssetDetails}>Ver Detalhes</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;