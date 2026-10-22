import api from './apiClient';
import { AvlVisualizationResponse, HeapVisualizationResponse, DsaSummary, ComplexityEntry } from '../types/dsa';

export const visualizerApi = {
  getSummary: async (): Promise<DsaSummary> => {
    const res = await api.get<DsaSummary>('/visualizer/summary');
    return res.data;
  },

  getAvl: async (): Promise<AvlVisualizationResponse> => {
    const res = await api.get<AvlVisualizationResponse>('/visualizer/avl');
    return res.data;
  },

  getHeap: async (): Promise<HeapVisualizationResponse> => {
    const res = await api.get<HeapVisualizationResponse>('/visualizer/heap');
    return res.data;
  },

  getComplexities: async (): Promise<ComplexityEntry[]> => {
    const res = await api.get<ComplexityEntry[]>('/visualizer/complexities');
    return res.data;
  },
};
