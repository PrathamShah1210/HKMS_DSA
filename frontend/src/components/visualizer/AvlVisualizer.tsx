import React, { useState, useEffect } from 'react';
import { visualizerApi } from '../../api/visualizerApi';
import { AvlVisualizationResponse, AvlNodeMap, RotationEvent } from '../../types/dsa';
import { History, CheckCircle2, RotateCw } from 'lucide-react';

export const AvlVisualizer: React.FC = () => {
  const [data, setData] = useState<AvlVisualizationResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await visualizerApi.getAvl();
      setData(res);
    } catch (err) {
      console.error('Failed to load AVL visualization data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading || !data) {
    return (
      <div className="p-8 text-center text-sm text-slate-500 bg-white rounded-xl border border-slate-200">
        Rendering AVL Tree visual balance map...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Metrics Banner */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h3 className="text-base font-bold text-slate-900">AVL Self-Balancing Binary Search Tree</h3>
            <span className="flex items-center gap-1 px-2.5 py-0.5 rounded text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
              <CheckCircle2 className="w-3.5 h-3.5" />
              {data.isValid ? 'AVL Invariant Satisfied (|BF| <= 1)' : 'Imbalanced'}
            </span>
          </div>
          <p className="text-xs text-slate-500 mt-1">
            Indexes papers by ID in strictly O(log N). Auto-balances via Single (LL, RR) and Double (LR, RL) Rotations.
          </p>
        </div>

        <div className="flex items-center gap-4 text-xs">
          <div className="bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-200">
            <span className="text-slate-400">Total Nodes:</span>{' '}
            <strong className="text-slate-800 font-mono">{data.size}</strong>
          </div>
          <div className="bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-200">
            <span className="text-slate-400">Tree Height:</span>{' '}
            <strong className="text-slate-800 font-mono">{data.height}</strong>
          </div>
          <div className="bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-200">
            <span className="text-slate-400">Rotations Logged:</span>{' '}
            <strong className="text-brand-600 font-mono">{data.rotationHistory.length}</strong>
          </div>
          <button
            onClick={loadData}
            className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 transition-colors"
            title="Refresh"
          >
            <RotateCw className="w-4 h-4" />
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Visual Binary Tree Representation */}
        <div className="lg:col-span-2 bg-white p-6 rounded-xl border border-slate-200 shadow-sm overflow-x-auto min-h-[450px] flex items-center justify-center">
          {data.tree ? (
            <div className="w-full flex justify-center py-4">
              <RenderAvlNode node={data.tree} />
            </div>
          ) : (
            <div className="text-sm text-slate-400">AVL tree is empty</div>
          )}
        </div>

        {/* Rotation History & Audit Logs */}
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-col h-[450px]">
          <div className="flex items-center justify-between pb-3 border-b border-slate-200">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-700 flex items-center gap-1.5">
              <History className="w-4 h-4 text-brand-600" />
              Rotation Audit Log
            </h4>
            <span className="text-[11px] font-mono text-slate-400">{data.rotationHistory.length} events</span>
          </div>

          <div className="flex-1 overflow-y-auto divide-y divide-slate-100 mt-2 pr-1">
            {data.rotationHistory.length === 0 ? (
              <div className="p-8 text-center text-xs text-slate-400">
                No rotations yet (tree inserted in balanced order or initial state).
              </div>
            ) : (
              data.rotationHistory.map((rev: RotationEvent, idx: number) => {
                const badgeColor =
                  rev.rotationType === 'LEFT_LEFT' || rev.rotationType === 'RIGHT_RIGHT'
                    ? 'bg-blue-50 text-blue-700 border-blue-200'
                    : 'bg-purple-50 text-purple-700 border-purple-200';

                return (
                  <div key={idx} className="py-3 text-xs">
                    <div className="flex items-center justify-between">
                      <span className={`px-2 py-0.5 rounded font-mono font-bold text-[10px] border ${badgeColor}`}>
                        {rev.rotationType}
                      </span>
                      <span className="text-[10px] font-mono text-slate-400">
                        Pivot: #{rev.pivotKey} (BF: {rev.balanceFactorBefore})
                      </span>
                    </div>
                    <p className="text-slate-600 mt-1.5 text-[11px] leading-relaxed">
                      {rev.explanation}
                    </p>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

// Recursive AVL Node Component for visual tree diagram
const RenderAvlNode: React.FC<{ node: AvlNodeMap }> = ({ node }) => {
  const isBalanced = Math.abs(node.balanceFactor) <= 1;

  return (
    <div className="flex flex-col items-center">
      {/* Current Node Box */}
      <div className="relative group p-3 rounded-xl bg-white border-2 border-slate-200 hover:border-brand-500 shadow-sm transition-all text-center min-w-[130px] z-10">
        <div className="flex items-center justify-between gap-1 text-[10px] text-slate-400 font-mono mb-1">
          <span className="font-bold text-slate-700">Key #{node.key}</span>
          <span>H: {node.height}</span>
        </div>

        <p className="text-xs font-semibold text-slate-800 line-clamp-1 max-w-[140px]">
          {node.title || `Paper ${node.key}`}
        </p>

        <div className="mt-2 pt-1.5 border-t border-slate-100 flex items-center justify-between text-[10px]">
          <span className="text-slate-400">Balance:</span>
          <span
            className={`font-mono font-bold px-1.5 py-0.2 rounded ${
              isBalanced
                ? 'bg-emerald-50 text-emerald-700'
                : 'bg-red-50 text-red-700'
            }`}
          >
            {node.balanceFactor > 0 ? `+${node.balanceFactor}` : node.balanceFactor}
          </span>
        </div>
      </div>

      {/* Children branches */}
      {(node.left || node.right) && (
        <div className="flex items-start justify-center gap-6 mt-6 relative">
          {/* Left Branch */}
          <div className="flex flex-col items-center">
            {node.left ? (
              <RenderAvlNode node={node.left} />
            ) : (
              <div className="w-12 h-6 border-b border-dashed border-slate-300 text-[10px] text-slate-300 flex items-center justify-center">
                null
              </div>
            )}
          </div>

          {/* Right Branch */}
          <div className="flex flex-col items-center">
            {node.right ? (
              <RenderAvlNode node={node.right} />
            ) : (
              <div className="w-12 h-6 border-b border-dashed border-slate-300 text-[10px] text-slate-300 flex items-center justify-center">
                null
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
