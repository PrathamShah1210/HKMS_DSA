export type NodeType = 'ROOT' | 'SUBJECT' | 'TOPIC' | 'SUBTOPIC' | 'PAPER';

export interface CategoryNode {
  id: string;
  name: string;
  type: NodeType;
  parentId?: string;
  sortOrder?: number;
  paperCount: number;
  children: CategoryNode[];
}

export interface TraversalResult {
  traversalType: string;
  nodes: {
    id: string;
    name: string;
    type: NodeType;
    parentId?: string;
    paperId?: number;
  }[];
  visitedOrder: string[];
}
