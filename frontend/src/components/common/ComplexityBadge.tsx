import React from 'react';

interface ComplexityBadgeProps {
  label: string;
  complexity: string;
  color?: 'emerald' | 'blue' | 'purple' | 'amber';
}

export const ComplexityBadge: React.FC<ComplexityBadgeProps> = ({
  label,
  complexity,
  color = 'blue',
}) => {
  const colorMap = {
    emerald: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    blue: 'bg-blue-50 text-blue-700 border-blue-200',
    purple: 'bg-purple-50 text-purple-700 border-purple-200',
    amber: 'bg-amber-50 text-amber-700 border-amber-200',
  };

  return (
    <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-medium border ${colorMap[color]}`}>
      <span className="text-slate-500 font-normal">{label}:</span>
      <code className="font-mono font-semibold">{complexity}</code>
    </div>
  );
};
