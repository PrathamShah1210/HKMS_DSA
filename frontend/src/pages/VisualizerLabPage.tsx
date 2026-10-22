import React, { useState } from 'react';
import { AvlVisualizer } from '../components/visualizer/AvlVisualizer';
import { HeapVisualizer } from '../components/visualizer/HeapVisualizer';
import { ComplexityCheatSheet } from '../components/visualizer/ComplexityCheatSheet';
import { Cpu, GitBranch, Award, FileSpreadsheet } from 'lucide-react';

export const VisualizerLabPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'avl' | 'heap' | 'complexities'>('avl');

  return (
    <div className="space-y-6 animate-in fade-in duration-200">
      {/* Header */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
            <Cpu className="w-5 h-5 text-brand-600" />
            Interactive DSA Laboratory
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Real-time inspection of internal tree structures, balance factors, rotation traces, and contiguous heap memory arrays.
          </p>
        </div>

        {/* Tab Switcher */}
        <div className="flex items-center space-x-1 bg-slate-100 p-1 rounded-lg border border-slate-200 text-xs">
          <button
            onClick={() => setActiveTab('avl')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md font-semibold transition-all ${
              activeTab === 'avl'
                ? 'bg-white text-emerald-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <GitBranch className="w-3.5 h-3.5 text-emerald-600" />
            AVL Tree Explorer
          </button>
          <button
            onClick={() => setActiveTab('heap')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md font-semibold transition-all ${
              activeTab === 'heap'
                ? 'bg-white text-amber-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Award className="w-3.5 h-3.5 text-amber-600" />
            Max Heap Explorer
          </button>
          <button
            onClick={() => setActiveTab('complexities')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md font-semibold transition-all ${
              activeTab === 'complexities'
                ? 'bg-white text-brand-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <FileSpreadsheet className="w-3.5 h-3.5 text-brand-600" />
            Complexity Matrix
          </button>
        </div>
      </div>

      {/* Tab Panels */}
      {activeTab === 'avl' && <AvlVisualizer />}
      {activeTab === 'heap' && <HeapVisualizer />}
      {activeTab === 'complexities' && <ComplexityCheatSheet />}
    </div>
  );
};
