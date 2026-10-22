import { memo } from 'react';
import { Handle, Position } from '@xyflow/react';
import { BookOpen, Folder, FolderTree, Network, FileText } from 'lucide-react';

interface CustomTreeNodeProps {
  data: {
    id: string;
    label: string;
    type: string;
    paperCount: number;
    childCount: number;
  };
}

export const CustomTreeNode = memo(({ data }: CustomTreeNodeProps) => {
  const typeStyles: Record<string, { bg: string; border: string; text: string; icon: any }> = {
    ROOT: { bg: 'bg-purple-50', border: 'border-purple-300', text: 'text-purple-700', icon: Network },
    SUBJECT: { bg: 'bg-blue-50', border: 'border-blue-300', text: 'text-blue-700', icon: FolderTree },
    TOPIC: { bg: 'bg-indigo-50', border: 'border-indigo-300', text: 'text-indigo-700', icon: Folder },
    SUBTOPIC: { bg: 'bg-sky-50', border: 'border-sky-300', text: 'text-sky-700', icon: BookOpen },
    PAPER: { bg: 'bg-emerald-50', border: 'border-emerald-300', text: 'text-emerald-700', icon: FileText },
  };

  const style = typeStyles[data.type] || typeStyles.TOPIC;
  const Icon = style.icon;

  return (
    <div
      className={`px-4 py-3 rounded-xl bg-white border-2 ${style.border} shadow-sm hover:shadow-md transition-all min-w-[200px] max-w-[260px] text-left`}
    >
      <Handle type="target" position={Position.Top} className="!w-2 !h-2 !bg-slate-400" />

      <div className="flex items-center justify-between gap-2">
        <span
          className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-semibold tracking-wide uppercase ${style.bg} ${style.text}`}
        >
          <Icon className="w-3 h-3" />
          {data.type}
        </span>
        {data.type !== 'PAPER' && (
          <span className="text-[11px] font-medium text-slate-500 bg-slate-100 px-1.5 py-0.5 rounded">
            {data.paperCount} {data.paperCount === 1 ? 'paper' : 'papers'}
          </span>
        )}
      </div>

      <div className="mt-1.5">
        <p className="text-sm font-semibold text-slate-900 leading-snug line-clamp-2">
          {data.label}
        </p>
      </div>

      <Handle type="source" position={Position.Bottom} className="!w-2 !h-2 !bg-slate-400" />
    </div>
  );
});
