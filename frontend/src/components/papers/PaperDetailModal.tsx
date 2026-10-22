import React from 'react';
import { Paper } from '../../types/paper';
import { Modal } from '../common/Modal';
import { Award, ExternalLink, Calendar, GitBranch, Layers } from 'lucide-react';

interface PaperDetailModalProps {
  paper: Paper | null;
  isOpen: boolean;
  onClose: () => void;
}

export const PaperDetailModal: React.FC<PaperDetailModalProps> = ({ paper, isOpen, onClose }) => {
  if (!paper) return null;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={paper.title}
      subtitle={`Indexed under AVL Node Key #${paper.id}`}
      maxWidth="max-w-2xl"
    >
      <div className="space-y-4">
        {/* DSA In-Memory Indicators */}
        <div className="grid grid-cols-3 gap-3 p-3 bg-slate-50 rounded-xl border border-slate-200 text-xs">
          <div>
            <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">
              AVL BST Key
            </span>
            <span className="font-mono font-semibold text-slate-800 flex items-center gap-1 mt-0.5">
              <GitBranch className="w-3.5 h-3.5 text-emerald-600" />
              ID #{paper.id}
            </span>
          </div>

          <div>
            <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">
              Heap Priority Score
            </span>
            <span className="font-mono font-semibold text-amber-700 flex items-center gap-1 mt-0.5">
              <Award className="w-3.5 h-3.5 text-amber-500" />
              {paper.priorityScore.toFixed(1)} / 100
            </span>
          </div>

          <div>
            <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">
              N-Ary Category
            </span>
            <span className="font-medium text-slate-800 flex items-center gap-1 mt-0.5 truncate">
              <Layers className="w-3.5 h-3.5 text-blue-600 shrink-0" />
              {paper.categoryName || paper.categoryId}
            </span>
          </div>
        </div>

        {/* Authors */}
        <div>
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-1">Authors</h4>
          <p className="text-sm font-medium text-slate-800">{paper.authors}</p>
        </div>

        {/* Abstract */}
        {paper.paperAbstract && (
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-1">Abstract</h4>
            <p className="text-xs leading-relaxed text-slate-600 bg-white p-3 rounded-lg border border-slate-200">
              {paper.paperAbstract}
            </p>
          </div>
        )}

        {/* Publication Metadata */}
        <div className="grid grid-cols-2 gap-4 text-xs text-slate-600">
          <div>
            <span className="text-slate-400 block">Venue / Journal:</span>
            <span className="font-semibold text-slate-800">{paper.journalOrConference || 'Unspecified'}</span>
          </div>
          <div>
            <span className="text-slate-400 block">Year:</span>
            <span className="font-semibold text-slate-800 flex items-center gap-1">
              <Calendar className="w-3.5 h-3.5 text-slate-400" />
              {paper.publicationYear || 'N/A'}
            </span>
          </div>
        </div>

        {/* Keywords */}
        {paper.keywords && (
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-1">
              Indexed Trie Keywords
            </h4>
            <div className="flex flex-wrap gap-1.5">
              {paper.keywords.split(',').map((kw, i) => (
                <span
                  key={i}
                  className="px-2 py-0.5 rounded text-xs bg-slate-100 text-slate-700 border border-slate-200 font-mono"
                >
                  {kw.trim()}
                </span>
              ))}
            </div>
          </div>
        )}

        {/* DOI */}
        {paper.doi && (
          <div className="pt-2 border-t border-slate-100 flex items-center justify-between text-xs">
            <span className="text-slate-500 font-mono">DOI: {paper.doi}</span>
            <a
              href={`https://doi.org/${paper.doi}`}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1 text-brand-600 hover:text-brand-800 font-medium"
            >
              Open External DOI
              <ExternalLink className="w-3 h-3" />
            </a>
          </div>
        )}
      </div>
    </Modal>
  );
};
