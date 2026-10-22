import React from 'react';
import { PriorityLeaderboard } from '../components/ranking/PriorityLeaderboard';

export const RankingPage: React.FC = () => {
  return (
    <div className="space-y-6 animate-in fade-in duration-200">
      <PriorityLeaderboard />
    </div>
  );
};
