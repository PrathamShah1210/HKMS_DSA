import React, { useState, useEffect, useRef } from 'react';
import { Search, Zap, BookOpen, Clock, Tag, X } from 'lucide-react';
import { searchApi } from '../../api/searchApi';
import { AutocompleteMatch } from '../../types/dsa';
import { Paper } from '../../types/paper';

interface AutocompleteSearchBarProps {
  onSelectPaper?: (paper: Paper) => void;
}

export const AutocompleteSearchBar: React.FC<AutocompleteSearchBarProps> = ({ onSelectPaper }) => {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState<AutocompleteMatch[]>([]);
  const [matchedPapers, setMatchedPapers] = useState<Paper[]>([]);
  const [executionTimeMicros, setExecutionTimeMicros] = useState<number | null>(null);
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!query.trim()) {
      setSuggestions([]);
      setMatchedPapers([]);
      setExecutionTimeMicros(null);
      setIsOpen(false);
      return;
    }

    const timer = setTimeout(async () => {
      setLoading(true);
      try {
        const data = await searchApi.autocomplete(query.trim(), 6);
        setSuggestions(data.suggestions || []);
        setMatchedPapers(data.matchedPapers || []);
        setExecutionTimeMicros(data.executionTimeMicros);
        setIsOpen(true);
      } catch (err) {
        console.error('Autocomplete error:', err);
      } finally {
        setLoading(false);
      }
    }, 150);

    return () => clearTimeout(timer);
  }, [query]);

  // Click outside listener
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div className="relative w-full max-w-2xl" ref={dropdownRef}>
      <div className="relative flex items-center">
        <Search className="absolute left-3.5 w-4 h-4 text-slate-400" />
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => {
            if (query.trim() && (suggestions.length > 0 || matchedPapers.length > 0)) {
              setIsOpen(true);
            }
          }}
          placeholder="Search research papers by title, author, or keyword (Prefix Trie)..."
          className="w-full pl-10 pr-24 py-2.5 bg-white border border-slate-200 rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 shadow-sm transition-all"
        />
        {query && (
          <button
            onClick={() => {
              setQuery('');
              setIsOpen(false);
            }}
            className="absolute right-12 p-1 text-slate-400 hover:text-slate-600 rounded-md"
          >
            <X className="w-4 h-4" />
          </button>
        )}
        <div className="absolute right-3 flex items-center gap-1 px-1.5 py-0.5 rounded bg-slate-100 text-[10px] font-mono font-medium text-slate-500 border border-slate-200">
          <Zap className="w-3 h-3 text-amber-500" />
          Trie
        </div>
      </div>

      {/* Autocomplete Dropdown */}
      {isOpen && (
        <div className="absolute z-50 left-0 right-0 mt-2 bg-white rounded-xl shadow-xl border border-slate-200 overflow-hidden animate-in fade-in slide-in-from-top-1 duration-150">
          {/* Diagnostic Latency Bar */}
          {executionTimeMicros !== null && (
            <div className="px-4 py-1.5 bg-slate-50 border-b border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <span className="flex items-center gap-1">
                <Clock className="w-3 h-3 text-slate-400" />
                Prefix lookup completed in
                <strong className="text-emerald-600 font-mono font-semibold ml-1">
                  {executionTimeMicros} µs
                </strong>
                <span className="text-[10px] text-slate-400 font-normal">
                  ({(executionTimeMicros / 1000).toFixed(2)} ms)
                </span>
              </span>
              <span className="text-[10px] font-mono text-slate-400">Trie O(L) + AVL O(log N)</span>
            </div>
          )}

          {loading ? (
            <div className="p-6 text-center text-xs text-slate-400">Searching prefix index...</div>
          ) : (
            <div className="max-h-96 overflow-y-auto divide-y divide-slate-100">
              {/* Trie Suggestions */}
              {suggestions.length > 0 && (
                <div className="p-2">
                  <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-3 py-1">
                    Matching Terms
                  </div>
                  <div className="flex flex-wrap gap-1.5 px-3 py-1">
                    {suggestions.map((s, idx) => (
                      <button
                        key={idx}
                        onClick={() => setQuery(s.term)}
                        className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs bg-slate-100 hover:bg-brand-50 text-slate-700 hover:text-brand-700 border border-slate-200 hover:border-brand-200 transition-colors"
                      >
                        <Tag className="w-3 h-3 text-slate-400" />
                        <span className="font-medium">{s.term}</span>
                        <span className="text-[10px] text-slate-400 bg-white px-1 rounded border border-slate-200">
                          {s.frequency}
                        </span>
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* Matched Research Papers */}
              {matchedPapers.length > 0 ? (
                <div className="p-2">
                  <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-3 py-1">
                    Indexed Research Papers ({matchedPapers.length})
                  </div>
                  <div className="space-y-1">
                    {matchedPapers.map((paper) => (
                      <div
                        key={paper.id}
                        onClick={() => {
                          if (onSelectPaper) onSelectPaper(paper);
                          setIsOpen(false);
                        }}
                        className="p-3 rounded-lg hover:bg-slate-50 cursor-pointer transition-colors border border-transparent hover:border-slate-200 group"
                      >
                        <div className="flex items-start justify-between gap-2">
                          <h4 className="text-sm font-semibold text-slate-900 group-hover:text-brand-600 transition-colors line-clamp-1">
                            {paper.title}
                          </h4>
                          <span className="shrink-0 px-2 py-0.5 text-xs font-mono font-medium rounded bg-amber-50 text-amber-700 border border-amber-200">
                            P: {paper.priorityScore.toFixed(1)}
                          </span>
                        </div>
                        <p className="text-xs text-slate-500 mt-0.5 line-clamp-1">{paper.authors}</p>
                        <div className="flex items-center gap-2 mt-1.5 text-[11px] text-slate-400">
                          <span className="inline-flex items-center gap-1 text-slate-600 bg-slate-100 px-1.5 py-0.5 rounded">
                            <BookOpen className="w-3 h-3" />
                            {paper.categoryName || 'Research Paper'}
                          </span>
                          {paper.publicationYear && <span>{paper.publicationYear}</span>}
                          {paper.journalOrConference && <span>• {paper.journalOrConference}</span>}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ) : (
                suggestions.length === 0 && (
                  <div className="p-6 text-center text-xs text-slate-400">
                    No matching papers or keywords found for "{query}".
                  </div>
                )
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
};
