import React, { useState, useEffect } from 'react';
import { Paper, PaperCreateRequest } from '../../types/paper';
import { Modal } from '../common/Modal';

interface PaperFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: PaperCreateRequest) => Promise<void>;
  initialPaper?: Paper | null;
  categories: { id: string; name: string }[];
}

export const PaperFormModal: React.FC<PaperFormModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialPaper,
  categories,
}) => {
  const [title, setTitle] = useState('');
  const [authors, setAuthors] = useState('');
  const [paperAbstract, setPaperAbstract] = useState('');
  const [publicationYear, setPublicationYear] = useState<number>(2024);
  const [journalOrConference, setJournalOrConference] = useState('');
  const [doi, setDoi] = useState('');
  const [priorityScore, setPriorityScore] = useState<number>(75.0);
  const [categoryId, setCategoryId] = useState('');
  const [keywords, setKeywords] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (initialPaper) {
      setTitle(initialPaper.title);
      setAuthors(initialPaper.authors);
      setPaperAbstract(initialPaper.paperAbstract || '');
      setPublicationYear(initialPaper.publicationYear || 2024);
      setJournalOrConference(initialPaper.journalOrConference || '');
      setDoi(initialPaper.doi || '');
      setPriorityScore(initialPaper.priorityScore);
      setCategoryId(initialPaper.categoryId);
      setKeywords(initialPaper.keywords || '');
    } else {
      setTitle('');
      setAuthors('');
      setPaperAbstract('');
      setPublicationYear(2024);
      setJournalOrConference('');
      setDoi('');
      setPriorityScore(75.0);
      setCategoryId(categories.length > 0 ? categories[0].id : '');
      setKeywords('');
    }
  }, [initialPaper, isOpen, categories]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !authors.trim() || !categoryId) return;

    setLoading(true);
    try {
      await onSubmit({
        title: title.trim(),
        authors: authors.trim(),
        paperAbstract: paperAbstract.trim(),
        publicationYear,
        journalOrConference: journalOrConference.trim(),
        doi: doi.trim(),
        priorityScore,
        categoryId,
        keywords: keywords.trim(),
      });
      onClose();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to save paper');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={initialPaper ? 'Edit Research Paper' : 'Add New Research Paper'}
      subtitle="Synchronizes atomically across SQLite, AVL Tree, Trie, and Max Heap"
      maxWidth="max-w-2xl"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-xs font-semibold text-slate-700 mb-1">
            Paper Title <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="e.g. Attention Is All You Need"
            className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
            required
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 mb-1">
            Authors <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={authors}
            onChange={(e) => setAuthors(e.target.value)}
            placeholder="e.g. Ashish Vaswani, Noam Shazeer, et al."
            className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
            required
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Category (N-Ary Node) <span className="text-red-500">*</span>
            </label>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
              required
            >
              <option value="">Select category...</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Priority Score (Max Heap): <span className="font-mono text-brand-600 font-bold">{priorityScore}</span>
            </label>
            <input
              type="range"
              min="0"
              max="100"
              step="0.5"
              value={priorityScore}
              onChange={(e) => setPriorityScore(parseFloat(e.target.value))}
              className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-brand-600 mt-2"
            />
          </div>
        </div>

        <div className="grid grid-cols-3 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Publication Year</label>
            <input
              type="number"
              value={publicationYear}
              onChange={(e) => setPublicationYear(parseInt(e.target.value) || 2024)}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
            />
          </div>
          <div className="col-span-2">
            <label className="block text-xs font-semibold text-slate-700 mb-1">Journal / Conference</label>
            <input
              type="text"
              value={journalOrConference}
              onChange={(e) => setJournalOrConference(e.target.value)}
              placeholder="e.g. NeurIPS / IEEE S&P / ACM SIGMOD"
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 mb-1">
            Keywords & Tags (Trie Indexing)
          </label>
          <input
            type="text"
            value={keywords}
            onChange={(e) => setKeywords(e.target.value)}
            placeholder="Comma-separated: transformer, attention, deep learning"
            className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
          />
          <p className="text-[11px] text-slate-400 mt-1">Each keyword is indexed into the Prefix Trie for instant autocomplete.</p>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 mb-1">Abstract</label>
          <textarea
            rows={3}
            value={paperAbstract}
            onChange={(e) => setPaperAbstract(e.target.value)}
            placeholder="Brief research summary or methodology overview..."
            className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none resize-none"
          />
        </div>

        <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-xs font-medium text-slate-600 hover:bg-slate-100 rounded-lg"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 text-xs font-semibold text-white bg-brand-600 hover:bg-brand-700 rounded-lg shadow-sm transition-colors"
          >
            {loading ? 'Saving...' : initialPaper ? 'Update Paper' : 'Index Paper'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
