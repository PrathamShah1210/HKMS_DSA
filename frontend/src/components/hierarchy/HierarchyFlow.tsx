import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
  ReactFlow,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
  Node,
  Edge,
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { hierarchyApi } from '../../api/hierarchyApi';
import { CustomTreeNode } from './CustomTreeNode';
import { TraversalResult, NodeType } from '../../types/hierarchy';
import { Plus, ArrowRightLeft, Trash2, Play, RefreshCw } from 'lucide-react';
import { Modal } from '../common/Modal';

export const HierarchyFlow: React.FC = () => {
  const [nodes, setNodes, onNodesChange] = useNodesState<Node>([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState<Edge>([]);
  const [loading, setLoading] = useState(true);

  // Modals state
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isMoveOpen, setIsMoveOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);

  // Form states
  const [newNodeName, setNewNodeName] = useState('');
  const [newNodeType, setNewNodeType] = useState<NodeType>('TOPIC');
  const [newNodeParentId, setNewNodeParentId] = useState('root');

  const [moveNodeId, setMoveNodeId] = useState('');
  const [newParentId, setNewParentId] = useState('');
  const [deleteNodeId, setDeleteNodeId] = useState('');

  // Traversal state
  const [traversal, setTraversal] = useState<TraversalResult | null>(null);

  const nodeTypes = useMemo(() => ({ customTree: CustomTreeNode }), []);

  const loadGraph = useCallback(async () => {
    setLoading(true);
    try {
      const data = await hierarchyApi.getReactFlowGraph();
      setNodes(data.nodes || []);
      setEdges(data.edges || []);
    } catch (err) {
      console.error('Failed to load hierarchy graph:', err);
    } finally {
      setLoading(false);
    }
  }, [setNodes, setEdges]);

  useEffect(() => {
    loadGraph();
  }, [loadGraph]);

  const handleAddNode = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newNodeName.trim()) return;
    try {
      await hierarchyApi.addNode({
        name: newNodeName.trim(),
        type: newNodeType,
        parentId: newNodeParentId,
      });
      setIsAddOpen(false);
      setNewNodeName('');
      loadGraph();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Error adding node');
    }
  };

  const handleMoveNode = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!moveNodeId || !newParentId) return;
    try {
      await hierarchyApi.moveNode(moveNodeId, newParentId);
      setIsMoveOpen(false);
      loadGraph();
    } catch (err: any) {
      alert(err.response?.data?.error || 'Acyclic violation or error moving node');
    }
  };

  const handleDeleteNode = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!deleteNodeId) return;
    if (!confirm('Are you sure? This will delete the node and its entire subtree!')) return;
    try {
      await hierarchyApi.deleteNode(deleteNodeId);
      setIsDeleteOpen(false);
      loadGraph();
    } catch (err: any) {
      alert(err.response?.data?.error || 'Error deleting node');
    }
  };

  const runTraversal = async (type: 'dfs-pre' | 'dfs-post' | 'bfs') => {
    try {
      let res: TraversalResult;
      if (type === 'dfs-pre') res = await hierarchyApi.getDfsPreOrder();
      else if (type === 'dfs-post') res = await hierarchyApi.getDfsPostOrder();
      else res = await hierarchyApi.getBfs();

      setTraversal(res);
    } catch (err) {
      console.error('Traversal failed:', err);
    }
  };

  return (
    <div className="flex flex-col h-[750px] bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      {/* Action Toolbar */}
      <div className="p-4 border-b border-slate-200 bg-slate-50 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <button
            onClick={() => setIsAddOpen(true)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-brand-600 hover:bg-brand-700 text-white shadow-sm transition-colors"
          >
            <Plus className="w-4 h-4" />
            Add Category
          </button>
          <button
            onClick={() => setIsMoveOpen(true)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-white hover:bg-slate-50 text-slate-700 border border-slate-200 transition-colors shadow-sm"
          >
            <ArrowRightLeft className="w-4 h-4 text-slate-500" />
            Reparent Branch
          </button>
          <button
            onClick={() => setIsDeleteOpen(true)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-white hover:bg-red-50 text-red-600 border border-slate-200 transition-colors shadow-sm"
          >
            <Trash2 className="w-4 h-4 text-red-500" />
            Delete Subtree
          </button>
          <button
            onClick={loadGraph}
            className="p-1.5 rounded-lg text-slate-500 hover:text-slate-800 hover:bg-slate-200 transition-colors"
            title="Refresh graph"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>

        {/* Traversal triggers */}
        <div className="flex items-center gap-2">
          <span className="text-xs font-medium text-slate-500 mr-1 flex items-center gap-1">
            <Play className="w-3.5 h-3.5 text-brand-600" />
            Traversals:
          </span>
          <button
            onClick={() => runTraversal('dfs-pre')}
            className="px-2.5 py-1 text-xs font-medium bg-white hover:bg-indigo-50 text-indigo-700 border border-slate-200 rounded-md transition-colors shadow-sm"
          >
            DFS Pre-Order
          </button>
          <button
            onClick={() => runTraversal('dfs-post')}
            className="px-2.5 py-1 text-xs font-medium bg-white hover:bg-indigo-50 text-indigo-700 border border-slate-200 rounded-md transition-colors shadow-sm"
          >
            DFS Post-Order
          </button>
          <button
            onClick={() => runTraversal('bfs')}
            className="px-2.5 py-1 text-xs font-medium bg-white hover:bg-blue-50 text-blue-700 border border-slate-200 rounded-md transition-colors shadow-sm"
          >
            BFS Level-Order
          </button>
        </div>
      </div>

      {/* Traversal Results Panel */}
      {traversal && (
        <div className="p-3 bg-indigo-50/70 border-b border-indigo-100 flex items-center justify-between text-xs animate-in fade-in">
          <div className="flex items-center gap-2 overflow-x-auto">
            <span className="font-semibold text-indigo-900 shrink-0">
              [{traversal.traversalType}] Visited Order ({traversal.nodes.length} nodes):
            </span>
            <div className="flex items-center gap-1">
              {traversal.visitedOrder.map((step, idx) => (
                <span
                  key={idx}
                  className="px-2 py-0.5 rounded bg-white text-indigo-800 border border-indigo-200 font-mono text-[11px] shrink-0"
                >
                  {idx + 1}. {step}
                </span>
              ))}
            </div>
          </div>
          <button
            onClick={() => setTraversal(null)}
            className="text-indigo-400 hover:text-indigo-700 ml-2 font-bold"
          >
            ×
          </button>
        </div>
      )}

      {/* React Flow Container */}
      <div className="flex-1 w-full h-full relative">
        {loading ? (
          <div className="absolute inset-0 flex items-center justify-center bg-white/70 z-10">
            <span className="text-sm font-medium text-slate-500">Constructing hierarchical view...</span>
          </div>
        ) : null}

        <ReactFlow
          nodes={nodes}
          edges={edges}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          nodeTypes={nodeTypes}
          fitView
          fitViewOptions={{ padding: 0.2 }}
        >
          <Background color="#cbd5e1" gap={16} size={1} />
          <Controls />
        </ReactFlow>
      </div>

      {/* Add Node Modal */}
      <Modal isOpen={isAddOpen} onClose={() => setIsAddOpen(false)} title="Add Category Node">
        <form onSubmit={handleAddNode} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Category Name</label>
            <input
              type="text"
              value={newNodeName}
              onChange={(e) => setNewNodeName(e.target.value)}
              placeholder="e.g. Distributed Consensus"
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Node Type</label>
              <select
                value={newNodeType}
                onChange={(e) => setNewNodeType(e.target.value as NodeType)}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
              >
                <option value="SUBJECT">SUBJECT</option>
                <option value="TOPIC">TOPIC</option>
                <option value="SUBTOPIC">SUBTOPIC</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Parent Node</label>
              <select
                value={newNodeParentId}
                onChange={(e) => setNewNodeParentId(e.target.value)}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
              >
                {nodes
                  .filter((n) => n.data.type !== 'PAPER')
                  .map((n) => (
                    <option key={n.id} value={n.id}>
                      {String(n.data.label)} ({String(n.data.type)})
                    </option>
                  ))}
              </select>
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={() => setIsAddOpen(false)}
              className="px-4 py-2 text-xs font-medium text-slate-600 hover:bg-slate-100 rounded-lg"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-xs font-semibold text-white bg-brand-600 hover:bg-brand-700 rounded-lg shadow-sm"
            >
              Add Node
            </button>
          </div>
        </form>
      </Modal>

      {/* Move Node Modal */}
      <Modal
        isOpen={isMoveOpen}
        onClose={() => setIsMoveOpen(false)}
        title="Reparent Branch (N-Ary Move Algorithm)"
        subtitle="Enforces the Acyclic Invariant: target cannot be the node itself or any of its descendants."
      >
        <form onSubmit={handleMoveNode} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Node to Move</label>
            <select
              value={moveNodeId}
              onChange={(e) => setMoveNodeId(e.target.value)}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
              required
            >
              <option value="">Select node to move...</option>
              {nodes
                .filter((n) => n.id !== 'root' && n.data.type !== 'PAPER')
                .map((n) => (
                  <option key={n.id} value={n.id}>
                    {String(n.data.label)} ({String(n.data.type)})
                  </option>
                ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">New Parent</label>
            <select
              value={newParentId}
              onChange={(e) => setNewParentId(e.target.value)}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 outline-none"
              required
            >
              <option value="">Select target parent...</option>
              {nodes
                .filter((n) => n.data.type !== 'PAPER')
                .map((n) => (
                  <option key={n.id} value={n.id}>
                    {String(n.data.label)} ({String(n.data.type)})
                  </option>
                ))}
            </select>
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={() => setIsMoveOpen(false)}
              className="px-4 py-2 text-xs font-medium text-slate-600 hover:bg-slate-100 rounded-lg"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-xs font-semibold text-white bg-brand-600 hover:bg-brand-700 rounded-lg shadow-sm"
            >
              Move Branch
            </button>
          </div>
        </form>
      </Modal>

      {/* Delete Subtree Modal */}
      <Modal isOpen={isDeleteOpen} onClose={() => setIsDeleteOpen(false)} title="Delete Subtree">
        <form onSubmit={handleDeleteNode} className="space-y-4">
          <p className="text-xs text-red-600 bg-red-50 p-3 rounded-lg border border-red-200">
            Warning: Deleting a category will cascade to delete all child subtopics and associated paper references!
          </p>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Select Subtree to Delete</label>
            <select
              value={deleteNodeId}
              onChange={(e) => setDeleteNodeId(e.target.value)}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-red-500/20 focus:border-red-500 outline-none"
              required
            >
              <option value="">Select node to delete...</option>
              {nodes
                .filter((n) => n.id !== 'root')
                .map((n) => (
                  <option key={n.id} value={n.id}>
                    {String(n.data.label)} ({String(n.data.type)})
                  </option>
                ))}
            </select>
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={() => setIsDeleteOpen(false)}
              className="px-4 py-2 text-xs font-medium text-slate-600 hover:bg-slate-100 rounded-lg"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-xs font-semibold text-white bg-red-600 hover:bg-red-700 rounded-lg shadow-sm"
            >
              Delete Subtree
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
