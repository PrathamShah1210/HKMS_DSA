import api from './apiClient';
import { AutocompleteResponse } from '../types/dsa';

export const searchApi = {
  autocomplete: async (query: string, limit: number = 8): Promise<AutocompleteResponse> => {
    const res = await api.get<AutocompleteResponse>('/search/autocomplete', {
      params: { query, limit },
    });
    return res.data;
  },

  exact: async (query: string): Promise<any> => {
    const res = await api.get('/search/exact', { params: { query } });
    return res.data;
  },
};
