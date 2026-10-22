import React, { useState, useEffect, useCallback } from 'react';
import { rankingApi, HeapItemResponse } from '../../api/rankingApi';
import { Award, Zap, ArrowUp, RefreshCw } from 'lucide-react';

export const PriorityLeaderboard: React.FC = () => {
  const [items, setItems] = useState<HeapItemResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [extracting, setExtracting] = useState(false);
  const [extractedItem, setExtractedItem] = useState<HeapItemResponse | null>(null);

  const loadLeaderboard = useCallback(async () => {
    setLoading(true);
    try {
      const data = await rankingApi.getTopRanked(15);
      setItems(data || []);
    } catch (err) {
      console.error('Failed to load priority leaderboard:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadLeaderboard();
  }, [loadLeaderboard]);

  const handleExtractMax = async () => {
    setExtracting(true);
    try {
      const max = await rankingApi.extractMax();
      setExtractedItem(max);
      loadLeaderboard();
    } catch (err) {
      alert('Heap is empty or extraction failed');
    } finally {
      setExtracting(false);
    }
  };

  const handleReinsert = async (id: number, priority: number) => {
    try {
      await rankingApi.reinsert(id, priority);
      setExtractedItem(null);
      loadLeaderboard();
    } catch (err) {
      alert('Failed to re-insert paper into heap');
    }
  };

  const handleQuickPriorityUpdate = async (id: number, currentScore: number, delta: number) => {
    const newScore = Math.max(0, Math.min(100, Math.round((currentScore + delta) * 10) / 10));
    try {
      await rankingApi.updatePriority(id, newScore);
      loadLeaderboard();
    } catch (err) {
      alert('Failed to update priority score');
    }
  };

  return (
    <div className="space-y-6">
      {/* Controls & Extract-Max Bar */}
      <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h3 className="text-base font-bold text-slate-900">Priority Paper Ranking</h3>
            <span className="px-2 py-0.5 rounded text-[11px] font-mono font-medium bg-amber-50 text-amber-700 border border-amber-200">
              Binary Max Heap: O(log N)
            </span>
          </div>
          <p className="text-xs text-slate-500 mt-0.5">
            Papers ordered dynamically by numerical priority score. Extract root in O(log N) or adjust score in O(log N).
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={handleExtractMax}
            disabled={extracting || items.length === 0}
            className="flex items-center gap-1.5 px-4 py-2 rounded-lg text-xs font-semibold bg-amber-500 hover:bg-amber-600 text-white shadow-sm transition-all disabled:opacity-50"
          >
            <Zap className="w-4 h-4 fill-white" />
            {extracting ? 'Sifting Down...' : 'Extract Max (Pop Root)'}
          </button>
          <button
            onClick={loadLeaderboard}
            className="p-2 rounded-lg text-slate-500 hover:bg-slate-100 transition-colors"
            title="Refresh Heap"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Extracted Notification Banner */}
      {extractedItem && (
        <div className="p-4 bg-amber-50 border border-amber-200 rounded-xl text-xs text-amber-900 flex flex-wrap items-center justify-between gap-3 animate-in fade-in">
          <div className="flex items-center gap-2">
            <Award className="w-5 h-5 text-amber-600 shrink-0" />
            <div>
              <span className="font-bold">Popped Root Paper:</span> #{extractedItem.id} - "{extractedItem.payload?.title}"
              <span className="ml-2 font-mono font-semibold bg-white px-2 py-0.5 rounded border border-amber-300">
                Score: {extractedItem.priority.toFixed(1)}
              </span>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => handleReinsert(extractedItem.id, extractedItem.priority)}
              className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-amber-600 hover:bg-amber-700 text-white shadow-sm transition-all"
            >
              Re-Insert Back Into Heap
            </button>
            <button
              onClick={() => setExtractedItem(null)}
              className="p-1 rounded text-amber-700 hover:bg-amber-100"
              title="Dismiss"
            >
              ✕
            </button>
          </div>
        </div>
      )}

      {/* Leaderboard Table / Cards */}
      {loading ? (
        <div className="p-8 text-center text-sm text-slate-500 bg-white rounded-xl border border-slate-200">
          Rebuilding top-K from Max Heap...
        </div>
      ) : items.length === 0 ? (
        <div className="p-12 text-center bg-white rounded-xl border border-slate-200">
          <Award className="w-8 h-8 text-slate-300 mx-auto mb-2" />
          <h4 className="text-sm font-semibold text-slate-700">Heap is empty</h4>
          <p className="text-xs text-slate-500 mt-1">Add papers to populate the priority scheduler.</p>
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 bg-slate-50 border-b border-slate-200 flex items-center justify-between text-xs text-slate-500">
            <span className="font-semibold text-slate-700">Ranked Papers ({items.length})</span>
            <span className="font-mono text-[11px]">Root Peek: O(1) • Update Priority: O(log N)</span>
          </div>
          <div className="divide-y divide-slate-100">
            {items.map((item, index) => {
              const isRoot = index === 0;
              return (
                <div
                  key={item.id}
                  className={`p-4 flex flex-wrap items-center justify-between gap-4 transition-colors ${
                    isRoot ? 'bg-amber-50/30' : 'hover:bg-slate-50/70'
                  }`}
                >
                  <div className="flex items-center gap-3 min-w-[280px] flex-1">
                    <div
                      className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0 ${
                        isRoot
                          ? 'bg-amber-500 text-white shadow-sm'
                          : 'bg-slate-100 text-slate-600'
                      }`}
                    >
                      {index + 1}
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <h4 className="text-sm font-semibold text-slate-900 line-clamp-1">
                          {item.payload?.title || `Paper #${item.id}`}
                        </h4>
                        {isRoot && (
                          <span className="px-2 py-0.5 text-[10px] font-bold uppercase rounded bg-amber-100 text-amber-800 border border-amber-300 shrink-0">
                            Root Max
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-slate-500 mt-0.5 line-clamp-1">
                        {item.payload?.authors || 'Academic Research'}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-4">
                    <div className="text-right">
                      <div className="text-xs font-bold font-mono text-slate-900">
                        {item.priority.toFixed(1)} / 100
                      </div>
                      <div className="text-[10px] text-slate-400 font-medium">Priority Score</div>
                    </div>

                    {/* Quick increment/decrement buttons for testing O(log N) heap sift */}
                    <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-lg border border-slate-200">
                      <button
                        onClick={() => handleQuickPriorityUpdate(item.id, item.priority, -2.5)}
                        className="px-2 py-1 text-xs font-bold text-slate-600 hover:bg-white rounded transition-colors"
                        title="Decrease Priority (Sift Down)"
                      >
                        -2.5
                      </button>
                      <button
                        onClick={() => handleQuickPriorityUpdate(item.id, item.priority, +2.5)}
                        className="px-2 py-1 text-xs font-bold text-brand-600 hover:bg-white rounded transition-colors flex items-center gap-0.5"
                        title="Increase Priority (Sift Up)"
                      >
                        <ArrowUp className="w-3 h-3" />
                        +2.5
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};
