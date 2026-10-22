import api from './apiClient';
import { Paper, PaperCreateRequest } from '../types/paper';

export const papersApi = {
  getAll: async (): Promise<Paper[]> => {
    const res = await api.get<Paper[]>('/papers');
    return res.data;
  },

  getById: async (id: number): Promise<Paper> => {
    const res = await api.get<Paper>(`/papers/${id}`);
    return res.data;
  },

  getByCategory: async (categoryId: string): Promise<Paper[]> => {
    const res = await api.get<Paper[]>(`/papers/category/${categoryId}`);
    return res.data;
  },

  create: async (data: PaperCreateRequest): Promise<Paper> => {
    const res = await api.post<Paper>('/papers', data);
    return res.data;
  },

  update: async (id: number, data: PaperCreateRequest): Promise<Paper> => {
    const res = await api.put<Paper>(`/papers/${id}`, data);
    return res.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/papers/${id}`);
  },
};
