import React from 'react';
import { Paper } from '../../types/paper';
import { Eye, Edit3, Trash2, Award, BookOpen } from 'lucide-react';

interface PaperTableProps {
  papers: Paper[];
  loading: boolean;
  onView: (paper: Paper) => void;
  onEdit: (paper: Paper) => void;
  onDelete: (id: number) => void;
}

export const PaperTable: React.FC<PaperTableProps> = ({
  papers,
  loading,
  onView,
  onEdit,
  onDelete,
}) => {
  if (loading) {
    return (
      <div className="p-8 text-center text-sm text-slate-500 bg-white rounded-xl border border-slate-200">
        Loading indexed papers from AVL Tree...
      </div>
    );
  }

  if (papers.length === 0) {
    return (
      <div className="p-12 text-center bg-white rounded-xl border border-slate-200">
        <BookOpen className="w-8 h-8 text-slate-300 mx-auto mb-2" />
        <h4 className="text-sm font-semibold text-slate-700">No research papers found</h4>
        <p className="text-xs text-slate-500 mt-1">Add your first paper to index it in AVL, Trie, and Max Heap.</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-50/75 border-b border-slate-200 text-xs font-semibold text-slate-600 uppercase tracking-wider">
            <tr>
              <th className="px-5 py-3.5">ID (AVL Key)</th>
              <th className="px-5 py-3.5">Title & Authors</th>
              <th className="px-5 py-3.5">Category</th>
              <th className="px-5 py-3.5">Priority (Heap)</th>
              <th className="px-5 py-3.5">Year</th>
              <th className="px-5 py-3.5 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-slate-700">
            {papers.map((paper) => (
              <tr key={paper.id} className="hover:bg-slate-50/80 transition-colors group">
                <td className="px-5 py-4 font-mono font-medium text-xs text-slate-500">
                  #{paper.id}
                </td>
                <td className="px-5 py-4">
                  <div
                    onClick={() => onView(paper)}
                    className="font-semibold text-slate-900 group-hover:text-brand-600 transition-colors cursor-pointer"
                  >
                    {paper.title}
                  </div>
                  <div className="text-xs text-slate-500 mt-0.5 line-clamp-1">{paper.authors}</div>
                </td>
                <td className="px-5 py-4">
                  <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200">
                    {paper.categoryName || paper.categoryId}
                  </span>
                </td>
                <td className="px-5 py-4">
                  <div className="flex items-center gap-1.5">
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-mono font-semibold bg-amber-50 text-amber-700 border border-amber-200">
                      <Award className="w-3 h-3" />
                      {paper.priorityScore.toFixed(1)}
                    </span>
                  </div>
                </td>
                <td className="px-5 py-4 text-xs text-slate-500 font-medium">
                  {paper.publicationYear || '—'}
                </td>
                <td className="px-5 py-4 text-right">
                  <div className="flex items-center justify-end gap-1">
                    <button
                      onClick={() => onView(paper)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-brand-600 hover:bg-brand-50 transition-colors"
                      title="View Details"
                    >
                      <Eye className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => onEdit(paper)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors"
                      title="Edit Paper"
                    >
                      <Edit3 className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => onDelete(paper.id)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
                      title="Delete Paper"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
