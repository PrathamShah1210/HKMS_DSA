import { useState } from 'react';
import { Layers, Network, Award, Cpu, BookOpen, LayoutDashboard } from 'lucide-react';
import { DashboardPage } from './pages/DashboardPage';
import { HierarchyPage } from './pages/HierarchyPage';
import { PapersPage } from './pages/PapersPage';
import { RankingPage } from './pages/RankingPage';
import { VisualizerLabPage } from './pages/VisualizerLabPage';

export default function App() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'hierarchy' | 'papers' | 'ranking' | 'visualizer'>('dashboard');

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-800">
      {/* Top Navigation Bar */}
      <header className="sticky top-0 z-30 bg-white border-b border-slate-200 px-6 py-3 flex items-center justify-between shadow-sm">
        <div className="flex items-center gap-3 cursor-pointer" onClick={() => setActiveTab('dashboard')}>
          <div className="bg-brand-600 text-white p-2 rounded-xl shadow-sm">
            <Layers className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-base font-bold text-slate-900 tracking-tight">HKMS</h1>
              <span className="text-[10px] font-mono font-semibold px-2 py-0.5 rounded-full bg-brand-50 text-brand-700 border border-brand-200">
                v1.0 • Academic DSA
              </span>
            </div>
            <p className="text-xs text-slate-500 font-medium hidden sm:block">
              Hierarchical Knowledge Management System
            </p>
          </div>
        </div>

        {/* Navigation Tabs */}
        <nav className="flex items-center space-x-1 bg-slate-100 p-1 rounded-xl border border-slate-200 text-xs font-semibold">
          <button
            onClick={() => setActiveTab('dashboard')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all ${
              activeTab === 'dashboard'
                ? 'bg-white text-brand-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <LayoutDashboard className="w-3.5 h-3.5" />
            Dashboard
          </button>
          <button
            onClick={() => setActiveTab('hierarchy')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all ${
              activeTab === 'hierarchy'
                ? 'bg-white text-brand-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Network className="w-3.5 h-3.5" />
            Hierarchy
          </button>
          <button
            onClick={() => setActiveTab('papers')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all ${
              activeTab === 'papers'
                ? 'bg-white text-brand-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <BookOpen className="w-3.5 h-3.5" />
            Papers
          </button>
          <button
            onClick={() => setActiveTab('ranking')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all ${
              activeTab === 'ranking'
                ? 'bg-white text-brand-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Award className="w-3.5 h-3.5" />
            Rankings
          </button>
          <button
            onClick={() => setActiveTab('visualizer')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all ${
              activeTab === 'visualizer'
                ? 'bg-white text-brand-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Cpu className="w-3.5 h-3.5" />
            DSA Lab
          </button>
        </nav>

        {/* Environment Status Badge */}
        <div className="hidden lg:flex items-center gap-2">
          <span className="flex items-center gap-1 px-2.5 py-1 text-xs font-semibold rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            Java 21 + SQLite Ready
          </span>
        </div>
      </header>

      {/* Main Page Container */}
      <main className="flex-1 p-6 max-w-7xl mx-auto w-full">
        {activeTab === 'dashboard' && <DashboardPage onNavigateTab={setActiveTab} />}
        {activeTab === 'hierarchy' && <HierarchyPage />}
        {activeTab === 'papers' && <PapersPage />}
        {activeTab === 'ranking' && <RankingPage />}
        {activeTab === 'visualizer' && <VisualizerLabPage />}
      </main>

      {/* Academic Footer */}
      <footer className="border-t border-slate-200 bg-white py-4 px-6 text-center text-xs text-slate-500">
        <p>
          HKMS Academic Project • Pure Custom Algorithms in Java 21: N-Ary Tree, AVL BST, Trie, Max Heap •
          SQLite Persistence
        </p>
      </footer>
    </div>
  );
}
