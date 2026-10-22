import React, { useState, useEffect } from 'react';
import { visualizerApi } from '../../api/visualizerApi';
import { HeapVisualizationResponse, HeapItemNode, HeapStepTrace } from '../../types/dsa';
import { CheckCircle2, RotateCw, Activity, ArrowRight } from 'lucide-react';

export const HeapVisualizer: React.FC = () => {
  const [data, setData] = useState<HeapVisualizationResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await visualizerApi.getHeap();
      setData(res);
    } catch (err) {
      console.error('Failed to load heap visualizer:', err);
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
        Loading Max Heap array structures...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Metrics Banner */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h3 className="text-base font-bold text-slate-900">Binary Max-Heap Visualizer</h3>
            <span className="flex items-center gap-1 px-2.5 py-0.5 rounded text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
              <CheckCircle2 className="w-3.5 h-3.5" />
              {data.isValid ? 'Heap Invariant Satisfied (Parent >= Children)' : 'Invalid Heap'}
            </span>
          </div>
          <p className="text-xs text-slate-500 mt-1">
            Contiguous 1D array representation with parent index <code>⌊(i-1)/2⌋</code> and children <code>2i+1, 2i+2</code>.
          </p>
        </div>

        <div className="flex items-center gap-4 text-xs">
          <div className="bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-200">
            <span className="text-slate-400">Total Elements:</span>{' '}
            <strong className="text-slate-800 font-mono">{data.size}</strong>
          </div>
          <div className="bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-200">
            <span className="text-slate-400">Recorded Operations:</span>{' '}
            <strong className="text-amber-600 font-mono">{data.traceHistory.length}</strong>
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

      {/* 1D Array Representation */}
      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-700">
            Contiguous Memory Array View (Indices 0 to {data.size - 1})
          </h4>
          <span className="text-xs text-slate-400 font-mono">
            Hover over any cell to highlight parent & children relationships
          </span>
        </div>

        <div className="flex items-center gap-3 overflow-x-auto pb-4 pt-2">
          {data.elements.map((item: HeapItemNode) => {
            const isHovered = hoveredIndex === item.index;
            const isParentOfHovered = hoveredIndex !== null && data.elements[hoveredIndex]?.parentIndex === item.index;
            const isChildOfHovered =
              hoveredIndex !== null &&
              (data.elements[hoveredIndex]?.leftIndex === item.index || data.elements[hoveredIndex]?.rightIndex === item.index);

            return (
              <div
                key={item.index}
                onMouseEnter={() => setHoveredIndex(item.index)}
                onMouseLeave={() => setHoveredIndex(null)}
                className={`flex-shrink-0 w-28 p-3 rounded-xl border-2 transition-all cursor-pointer text-center ${
                  isHovered
                    ? 'border-brand-500 bg-brand-50/50 shadow-md scale-105'
                    : isParentOfHovered
                    ? 'border-purple-400 bg-purple-50'
                    : isChildOfHovered
                    ? 'border-emerald-400 bg-emerald-50'
                    : 'border-slate-200 bg-slate-50/70 hover:border-slate-300'
                }`}
              >
                <span className="text-[10px] font-mono font-bold text-slate-400 block mb-1">
                  INDEX [{item.index}]
                </span>
                <span className="text-sm font-bold font-mono text-amber-700 block">
                  {item.priority.toFixed(1)}
                </span>
                <p className="text-[11px] font-medium text-slate-800 truncate mt-1" title={item.title}>
                  #{item.id} {item.title || 'Paper'}
                </p>
                <div className="mt-2 pt-1 border-t border-slate-200/60 flex items-center justify-around text-[9px] font-mono text-slate-400">
                  <span>P:{item.parentIndex ?? '—'}</span>
                  <span>L:{item.leftIndex ?? '—'}</span>
                  <span>R:{item.rightIndex ?? '—'}</span>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Heap Operation Traces */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
        <h4 className="text-xs font-bold uppercase tracking-wider text-slate-700 mb-3 flex items-center gap-1.5">
          <Activity className="w-4 h-4 text-amber-500" />
          Recent Heapify & Swap Trace History
        </h4>

        <div className="max-h-60 overflow-y-auto divide-y divide-slate-100">
          {data.traceHistory.length === 0 ? (
            <div className="p-6 text-center text-xs text-slate-400">
              No recent swap events recorded.
            </div>
          ) : (
            data.traceHistory.slice(-10).reverse().map((trace: HeapStepTrace, idx: number) => (
              <div key={idx} className="py-2.5 flex items-center justify-between text-xs">
                <div className="flex items-center gap-2">
                  <span className="px-2 py-0.5 rounded font-mono font-bold text-[10px] bg-amber-50 text-amber-800 border border-amber-200">
                    {trace.operation}
                  </span>
                  <span className="text-slate-600">{trace.description}</span>
                </div>
                <div className="font-mono text-[11px] text-slate-400 flex items-center gap-1">
                  <span>Idx {trace.index1}</span>
                  <ArrowRight className="w-3 h-3" />
                  <span>Idx {trace.index2}</span>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};
