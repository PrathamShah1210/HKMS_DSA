import api from './apiClient';
import { Paper } from '../types/paper';

export interface HeapItemResponse {
  id: number;
  priority: number;
  payload: Paper;
}

export const rankingApi = {
  getTopRanked: async (limit: number = 10): Promise<HeapItemResponse[]> => {
    const res = await api.get<HeapItemResponse[]>('/ranking/top', { params: { limit } });
    return res.data;
  },

  peekMax: async (): Promise<HeapItemResponse> => {
    const res = await api.get<HeapItemResponse>('/ranking/peek');
    return res.data;
  },

  extractMax: async (): Promise<HeapItemResponse> => {
    const res = await api.post<HeapItemResponse>('/ranking/extract-max');
    return res.data;
  },

  reinsert: async (id: number, priority?: number): Promise<any> => {
    const res = await api.post(`/ranking/${id}/reinsert`, null, { params: { priority } });
    return res.data;
  },

  updatePriority: async (id: number, priorityScore: number): Promise<any> => {
    const res = await api.patch(`/ranking/${id}/priority`, { priorityScore });
    return res.data;
  },
};
