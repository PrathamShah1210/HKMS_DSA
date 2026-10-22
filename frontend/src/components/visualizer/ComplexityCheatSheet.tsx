import React, { useState, useEffect } from 'react';
import { visualizerApi } from '../../api/visualizerApi';
import { ComplexityEntry } from '../../types/dsa';
import { Cpu } from 'lucide-react';

export const ComplexityCheatSheet: React.FC = () => {
  const [entries, setEntries] = useState<ComplexityEntry[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchComplexities = async () => {
      try {
        const data = await visualizerApi.getComplexities();
        setEntries(data || []);
      } catch (err) {
        console.error('Failed to load algorithm complexities:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchComplexities();
  }, []);

  if (loading) {
    return <div className="p-8 text-center text-xs text-slate-400">Loading complexity matrix...</div>;
  }

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      <div className="p-5 border-b border-slate-200 bg-slate-50 flex items-center justify-between">
        <div>
          <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
            <Cpu className="w-4 h-4 text-brand-600" />
            Core Algorithm Time & Space Complexity Matrix
          </h3>
          <p className="text-xs text-slate-500 mt-0.5">
            Formal theoretical analysis of all four custom data structures powering the HKMS platform.
          </p>
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50/50 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider">
            <tr>
              <th className="px-5 py-3">Data Structure</th>
              <th className="px-5 py-3">Application Feature</th>
              <th className="px-5 py-3">Search</th>
              <th className="px-5 py-3">Insert</th>
              <th className="px-5 py-3">Delete</th>
              <th className="px-5 py-3">Space</th>
              <th className="px-5 py-3">Key Algorithmic Invariant</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-slate-700">
            {entries.map((item, index) => (
              <tr key={index} className="hover:bg-slate-50/70 transition-colors">
                <td className="px-5 py-3.5 font-bold text-slate-900">{item.dataStructure}</td>
                <td className="px-5 py-3.5 text-slate-600 max-w-xs">{item.purpose}</td>
                <td className="px-5 py-3.5 font-mono font-semibold text-brand-700">{item.searchTime}</td>
                <td className="px-5 py-3.5 font-mono font-semibold text-emerald-700">{item.insertTime}</td>
                <td className="px-5 py-3.5 font-mono font-semibold text-red-700">{item.deleteTime}</td>
                <td className="px-5 py-3.5 font-mono font-semibold text-purple-700">{item.spaceComplexity}</td>
                <td className="px-5 py-3.5 text-slate-500 text-[11px] leading-relaxed max-w-xs">
                  {item.keyFeature}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
