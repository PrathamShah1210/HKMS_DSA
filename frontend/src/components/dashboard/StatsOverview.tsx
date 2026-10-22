import React from 'react';
import { Network, GitBranch, Search, Award, CheckCircle2, AlertCircle } from 'lucide-react';
import { DsaSummary } from '../../types/dsa';

interface StatsOverviewProps {
  summary: DsaSummary | null;
  loading: boolean;
}

export const StatsOverview: React.FC<StatsOverviewProps> = ({ summary, loading }) => {
  if (loading || !summary) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 animate-pulse">
        {[1, 2, 3, 4].map((i) => (
          <div key={i} className="h-28 bg-slate-100 rounded-xl border border-slate-200"></div>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {/* 1. N-Ary Tree Stats */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:border-slate-300 transition-all">
        <div className="flex items-center justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Hierarchy</span>
          <div className="p-2 rounded-lg bg-blue-50 text-blue-600">
            <Network className="w-4 h-4" />
          </div>
        </div>
        <div className="mt-3">
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">{summary.naryTotalNodes}</span>
            <span className="text-xs text-slate-500">nodes</span>
          </div>
          <div className="flex items-center justify-between mt-2 pt-2 border-t border-slate-100 text-xs text-slate-600">
            <span>Tree Depth: <strong className="text-slate-800">{summary.naryTreeDepth}</strong></span>
            <span className="font-mono text-slate-400">N-Ary Tree</span>
          </div>
        </div>
      </div>

      {/* 2. AVL Tree Stats */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:border-slate-300 transition-all">
        <div className="flex items-center justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Balanced Index</span>
          <div className="p-2 rounded-lg bg-emerald-50 text-emerald-600">
            <GitBranch className="w-4 h-4" />
          </div>
        </div>
        <div className="mt-3">
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">{summary.avlPaperCount}</span>
            <span className="text-xs text-slate-500">papers</span>
          </div>
          <div className="flex items-center justify-between mt-2 pt-2 border-t border-slate-100 text-xs text-slate-600">
            <span className="flex items-center gap-1">
              Height: <strong className="text-slate-800">{summary.avlTreeHeight}</strong>
              {summary.avlIsValid ? (
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500" />
              ) : (
                <AlertCircle className="w-3.5 h-3.5 text-amber-500" />
              )}
            </span>
            <span className="font-mono text-slate-400">AVL Tree</span>
          </div>
        </div>
      </div>

      {/* 3. Trie Stats */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:border-slate-300 transition-all">
        <div className="flex items-center justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Prefix Index</span>
          <div className="p-2 rounded-lg bg-indigo-50 text-indigo-600">
            <Search className="w-4 h-4" />
          </div>
        </div>
        <div className="mt-3">
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">{summary.trieTotalWords}</span>
            <span className="text-xs text-slate-500">terms</span>
          </div>
          <div className="flex items-center justify-between mt-2 pt-2 border-t border-slate-100 text-xs text-slate-600">
            <span>Lookup: <strong className="text-slate-800">O(L)</strong></span>
            <span className="font-mono text-slate-400">Trie</span>
          </div>
        </div>
      </div>

      {/* 4. Max Heap Stats */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:border-slate-300 transition-all">
        <div className="flex items-center justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Priority Heap</span>
          <div className="p-2 rounded-lg bg-amber-50 text-amber-600">
            <Award className="w-4 h-4" />
          </div>
        </div>
        <div className="mt-3">
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">{summary.heapTotalItems}</span>
            <span className="text-xs text-slate-500">ranked</span>
          </div>
          <div className="flex items-center justify-between mt-2 pt-2 border-t border-slate-100 text-xs text-slate-600">
            <span>Top Score: <strong className="text-amber-600">{summary.heapTopPaper ? summary.heapTopPaper.priorityScore.toFixed(1) : 'N/A'}</strong></span>
            <span className="font-mono text-slate-400">Max Heap</span>
          </div>
        </div>
      </div>
    </div>
  );
};
