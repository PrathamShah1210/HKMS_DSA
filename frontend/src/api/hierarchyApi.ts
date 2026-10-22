import api from './apiClient';
import { CategoryNode, TraversalResult, NodeType } from '../types/hierarchy';

export const hierarchyApi = {
  getTree: async (): Promise<CategoryNode> => {
    const res = await api.get<CategoryNode>('/hierarchy');
    return res.data;
  },

  getReactFlowGraph: async (): Promise<{ nodes: any[]; edges: any[] }> => {
    const res = await api.get<{ nodes: any[]; edges: any[] }>('/hierarchy/react-flow');
    return res.data;
  },

  addNode: async (data: { name: string; type: NodeType; parentId: string }): Promise<CategoryNode> => {
    const res = await api.post<CategoryNode>('/hierarchy/nodes', data);
    return res.data;
  },

  deleteNode: async (id: string): Promise<void> => {
    await api.delete(`/hierarchy/nodes/${id}`);
  },

  moveNode: async (nodeId: string, newParentId: string): Promise<any> => {
    const res = await api.post('/hierarchy/move', { nodeId, newParentId });
    return res.data;
  },

  getDfsPreOrder: async (): Promise<TraversalResult> => {
    const res = await api.get<TraversalResult>('/hierarchy/traversals/dfs-pre');
    return res.data;
  },

  getDfsPostOrder: async (): Promise<TraversalResult> => {
    const res = await api.get<TraversalResult>('/hierarchy/traversals/dfs-post');
    return res.data;
  },

  getBfs: async (): Promise<TraversalResult> => {
    const res = await api.get<TraversalResult>('/hierarchy/traversals/bfs');
    return res.data;
  },
};
