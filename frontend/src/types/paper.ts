export interface Paper {
  id: number;
  title: string;
  authors: string;
  paperAbstract?: string;
  publicationYear?: number;
  journalOrConference?: string;
  doi?: string;
  priorityScore: number;
  categoryId: string;
  categoryName?: string;
  keywords?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface PaperCreateRequest {
  title: string;
  authors: string;
  paperAbstract?: string;
  publicationYear?: number;
  journalOrConference?: string;
  doi?: string;
  priorityScore: number;
  categoryId: string;
  keywords?: string;
}
