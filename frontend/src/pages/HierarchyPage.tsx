import React from 'react';
import { HierarchyFlow } from '../components/hierarchy/HierarchyFlow';
import { Network, Info } from 'lucide-react';

export const HierarchyPage: React.FC = () => {
  return (
    <div className="space-y-6 animate-in fade-in duration-200">
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-start justify-between gap-4">
        <div>
          <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
            <Network className="w-5 h-5 text-brand-600" />
            N-Ary Research Hierarchy Explorer
          </h2>
          <p className="text-xs text-slate-600 mt-1 max-w-2xl leading-relaxed">
            Organizes subjects, topics, subtopics, and research papers into an arbitrary-branching hierarchical tree.
            Supports dynamic reparenting with cycle validation, cascading deletions, and step-by-step Pre-order/Post-order DFS and Level-order BFS.
          </p>
        </div>

        <div className="hidden sm:flex items-center gap-2 p-3 bg-blue-50/60 rounded-xl border border-blue-100 text-xs text-blue-900">
          <Info className="w-4 h-4 text-blue-600 shrink-0" />
          <span>Interactive React Flow Canvas: Pan, zoom, and test algorithms via top toolbar.</span>
        </div>
      </div>

      <HierarchyFlow />
    </div>
  );
};
