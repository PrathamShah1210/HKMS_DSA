import React, { useState, useEffect } from 'react';
import { visualizerApi } from '../api/visualizerApi';
import { papersApi } from '../api/papersApi';
import { DsaSummary } from '../types/dsa';
import { Paper } from '../types/paper';
import { StatsOverview } from '../components/dashboard/StatsOverview';
import { AutocompleteSearchBar } from '../components/search/AutocompleteSearchBar';
import { PaperDetailModal } from '../components/papers/PaperDetailModal';
import { Network, GitBranch, Search, Award, ArrowRight, BookOpen } from 'lucide-react';

interface DashboardPageProps {
  onNavigateTab: (tab: 'dashboard' | 'hierarchy' | 'papers' | 'ranking' | 'visualizer') => void;
}

export const DashboardPage: React.FC<DashboardPageProps> = ({ onNavigateTab }) => {
  const [summary, setSummary] = useState<DsaSummary | null>(null);
  const [recentPapers, setRecentPapers] = useState<Paper[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedPaper, setSelectedPaper] = useState<Paper | null>(null);

  useEffect(() => {
    const loadDashboardData = async () => {
      setLoading(true);
      try {
        const [sumRes, papersRes] = await Promise.all([
          visualizerApi.getSummary(),
          papersApi.getAll(),
        ]);
        setSummary(sumRes);
        setRecentPapers(papersRes.slice(0, 5));
      } catch (err) {
        console.error('Failed to load dashboard data:', err);
      } finally {
        setLoading(false);
      }
    };
    loadDashboardData();
  }, []);

  return (
    <div className="space-y-8 animate-in fade-in duration-200">
      {/* Hero & Real-time Trie Search Bar */}
      <div className="bg-gradient-to-r from-brand-500/10 via-brand-50 to-indigo-50/50 p-8 rounded-2xl border border-brand-100 flex flex-col items-center text-center">
        <span className="px-3 py-1 text-xs font-semibold rounded-full bg-brand-100 text-brand-800 border border-brand-200 mb-3">
          Academic Research Paper Knowledge Management System
        </span>
        <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight max-w-2xl">
          Search, Organize & Prioritize Research Papers with Core DSA
        </h2>
        <p className="text-xs sm:text-sm text-slate-600 max-w-xl mt-2 mb-6">
          Every query and navigation operation is powered by manually implemented <strong>N-Ary Trees</strong>,
          <strong> AVL Trees</strong>, <strong>Tries</strong>, and <strong>Max Heaps</strong> in Java 21.
        </p>

        {/* Real-time Trie Autocomplete Search Bar */}
        <AutocompleteSearchBar onSelectPaper={(p) => setSelectedPaper(p)} />
      </div>

      {/* Live DSA Status Overview */}
      <StatsOverview summary={summary} loading={loading} />

      {/* Core DSA Navigation Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* N-Ary Tree Card */}
        <div
          onClick={() => onNavigateTab('hierarchy')}
          className="group bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:border-blue-400 hover:shadow-md cursor-pointer transition-all flex flex-col justify-between"
        >
          <div>
            <div className="w-10 h-10 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center mb-3">
              <Network className="w-5 h-5" />
            </div>
            <h3 className="text-sm font-bold text-slate-900 group-hover:text-blue-600 transition-colors">
              N-Ary Tree Hierarchy
            </h3>
            <p className="text-xs text-slate-500 mt-1">
              Interactive subject-topic-subtopic taxonomy with DFS/BFS traversals and cycle-safe reparenting.
            </p>
          </div>
          <div className="flex items-center gap-1 text-xs font-semibold text-blue-600 mt-4 pt-3 border-t border-slate-100">
            <span>Explore React Flow</span>
            <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
          </div>
        </div>

        {/* AVL Tree Card */}
        <div
          onClick={() => onNavigateTab('papers')}
          className="group bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:border-emerald-400 hover:shadow-md cursor-pointer transition-all flex flex-col justify-between"
        >
          <div>
            <div className="w-10 h-10 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center mb-3">
              <GitBranch className="w-5 h-5" />
            </div>
            <h3 className="text-sm font-bold text-slate-900 group-hover:text-emerald-600 transition-colors">
              AVL Balanced Index
            </h3>
            <p className="text-xs text-slate-500 mt-1">
              Indexed papers by ID guaranteeing logarithmic worst-case search and self-balancing rotations.
            </p>
          </div>
          <div className="flex items-center gap-1 text-xs font-semibold text-emerald-600 mt-4 pt-3 border-t border-slate-100">
            <span>Manage Papers</span>
            <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
          </div>
        </div>

        {/* Max Heap Card */}
        <div
          onClick={() => onNavigateTab('ranking')}
          className="group bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:border-amber-400 hover:shadow-md cursor-pointer transition-all flex flex-col justify-between"
        >
          <div>
            <div className="w-10 h-10 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center mb-3">
              <Award className="w-5 h-5" />
            </div>
            <h3 className="text-sm font-bold text-slate-900 group-hover:text-amber-600 transition-colors">
              Max Heap Scheduler
            </h3>
            <p className="text-xs text-slate-500 mt-1">
              Dynamic priority rankings with O(1) peekMax, O(log N) extractMax, and tracked priority updates.
            </p>
          </div>
          <div className="flex items-center gap-1 text-xs font-semibold text-amber-600 mt-4 pt-3 border-t border-slate-100">
            <span>View Rankings</span>
            <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
          </div>
        </div>

        {/* DSA Lab Card */}
        <div
          onClick={() => onNavigateTab('visualizer')}
          className="group bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:border-purple-400 hover:shadow-md cursor-pointer transition-all flex flex-col justify-between"
        >
          <div>
            <div className="w-10 h-10 rounded-lg bg-purple-50 text-purple-600 flex items-center justify-center mb-3">
              <Search className="w-5 h-5" />
            </div>
            <h3 className="text-sm font-bold text-slate-900 group-hover:text-purple-600 transition-colors">
              DSA Visualizer Lab
            </h3>
            <p className="text-xs text-slate-500 mt-1">
              Rotation step playback, balance factor inspections, heap memory array views, and complexity audits.
            </p>
          </div>
          <div className="flex items-center gap-1 text-xs font-semibold text-purple-600 mt-4 pt-3 border-t border-slate-100">
            <span>Open DSA Lab</span>
            <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
          </div>
        </div>
      </div>

      {/* Recent Landmark Papers Spotlight */}
      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
              <BookOpen className="w-4 h-4 text-brand-600" />
              Indexed Landmark Research Papers
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">
              Retrieved directly from the custom in-memory AVL Tree index.
            </p>
          </div>
          <button
            onClick={() => onNavigateTab('papers')}
            className="text-xs font-semibold text-brand-600 hover:text-brand-800"
          >
            View all ({summary?.avlPaperCount || 0}) →
          </button>
        </div>

        <div className="divide-y divide-slate-100">
          {recentPapers.map((paper) => (
            <div
              key={paper.id}
              onClick={() => setSelectedPaper(paper)}
              className="py-3 flex items-center justify-between gap-4 hover:bg-slate-50 px-2 rounded-lg cursor-pointer transition-colors"
            >
              <div className="min-w-0">
                <h4 className="text-xs font-semibold text-slate-900 hover:text-brand-600 transition-colors truncate">
                  {paper.title}
                </h4>
                <p className="text-[11px] text-slate-500 truncate mt-0.5">{paper.authors}</p>
              </div>
              <div className="flex items-center gap-2 shrink-0">
                <span className="px-2 py-0.5 rounded text-[10px] font-medium bg-slate-100 text-slate-600">
                  {paper.categoryName || 'Paper'}
                </span>
                <span className="px-2 py-0.5 rounded text-xs font-mono font-semibold bg-amber-50 text-amber-700 border border-amber-200">
                  P: {paper.priorityScore.toFixed(1)}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Paper Detail Modal */}
      <PaperDetailModal
        paper={selectedPaper}
        isOpen={selectedPaper !== null}
        onClose={() => setSelectedPaper(null)}
      />
    </div>
  );
};
