import React, { useState, useEffect, useCallback } from 'react';
import { papersApi } from '../api/papersApi';
import { hierarchyApi } from '../api/hierarchyApi';
import { Paper, PaperCreateRequest } from '../types/paper';
import { PaperTable } from '../components/papers/PaperTable';
import { PaperFormModal } from '../components/papers/PaperFormModal';
import { PaperDetailModal } from '../components/papers/PaperDetailModal';
import { BookOpen, Plus, Search, Filter } from 'lucide-react';

export const PapersPage: React.FC = () => {
  const [papers, setPapers] = useState<Paper[]>([]);
  const [categories, setCategories] = useState<{ id: string; name: string }[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchFilter, setSearchFilter] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

  // Modals
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingPaper, setEditingPaper] = useState<Paper | null>(null);
  const [viewingPaper, setViewingPaper] = useState<Paper | null>(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const [papersData, hierarchyGraph] = await Promise.all([
        papersApi.getAll(),
        hierarchyApi.getReactFlowGraph(),
      ]);

      setPapers(papersData || []);

      const catList = (hierarchyGraph.nodes || [])
        .filter((n: any) => n.data.type !== 'PAPER')
        .map((n: any) => ({
          id: n.id,
          name: `${n.data.label} (${n.data.type})`,
        }));
      setCategories(catList);
    } catch (err) {
      console.error('Failed to load papers:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleCreateOrUpdate = async (data: PaperCreateRequest) => {
    if (editingPaper) {
      await papersApi.update(editingPaper.id, data);
    } else {
      await papersApi.create(data);
    }
    loadData();
  };

  const handleDelete = async (id: number) => {
    if (!confirm(`Are you sure you want to delete paper #${id}?`)) return;
    try {
      await papersApi.delete(id);
      loadData();
    } catch (err) {
      alert('Failed to delete paper');
    }
  };

  const filteredPapers = papers.filter((p) => {
    const matchesCategory = selectedCategory === 'ALL' || p.categoryId === selectedCategory;
    const matchesSearch =
      searchFilter.trim() === '' ||
      p.title.toLowerCase().includes(searchFilter.toLowerCase()) ||
      p.authors.toLowerCase().includes(searchFilter.toLowerCase()) ||
      (p.keywords && p.keywords.toLowerCase().includes(searchFilter.toLowerCase()));
    return matchesCategory && matchesSearch;
  });

  return (
    <div className="space-y-6 animate-in fade-in duration-200">
      {/* Top Header */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-brand-600" />
            Research Paper Management & AVL Index
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Papers indexed by ID in an AVL Tree for O(log N) operations. Every edit synchronizes SQLite, Trie, and Max Heap.
          </p>
        </div>

        <button
          onClick={() => {
            setEditingPaper(null);
            setIsFormOpen(true);
          }}
          className="flex items-center gap-1.5 px-4 py-2 rounded-lg text-xs font-semibold bg-brand-600 hover:bg-brand-700 text-white shadow-sm transition-colors"
        >
          <Plus className="w-4 h-4" />
          Add Research Paper
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-3">
        <div className="relative flex-1 min-w-[240px]">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input
            type="text"
            value={searchFilter}
            onChange={(e) => setSearchFilter(e.target.value)}
            placeholder="Filter table by title, author, or keyword..."
            className="w-full pl-9 pr-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs focus:outline-none focus:bg-white focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all"
          />
        </div>

        <div className="flex items-center gap-2 text-xs">
          <Filter className="w-4 h-4 text-slate-400" />
          <span className="text-slate-500 font-medium">Category:</span>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-700 focus:outline-none focus:bg-white focus:border-brand-500"
          >
            <option value="ALL">All Categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Papers Table */}
      <PaperTable
        papers={filteredPapers}
        loading={loading}
        onView={(paper) => setViewingPaper(paper)}
        onEdit={(paper) => {
          setEditingPaper(paper);
          setIsFormOpen(true);
        }}
        onDelete={handleDelete}
      />

      {/* Create / Edit Form Modal */}
      <PaperFormModal
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={handleCreateOrUpdate}
        initialPaper={editingPaper}
        categories={categories}
      />

      {/* Paper Detail View Modal */}
      <PaperDetailModal
        paper={viewingPaper}
        isOpen={viewingPaper !== null}
        onClose={() => setViewingPaper(null)}
      />
    </div>
  );
};
