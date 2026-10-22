import { Paper } from './paper';

export interface RotationEvent {
  timestamp: string;
  rotationType: 'LEFT_LEFT' | 'RIGHT_RIGHT' | 'LEFT_RIGHT' | 'RIGHT_LEFT';
  pivotKey: number;
  balanceFactorBefore: number;
  explanation: string;
}

export interface AvlNodeMap {
  key: number;
  title: string;
  height: number;
  balanceFactor: number;
  priorityScore: number;
  left?: AvlNodeMap | null;
  right?: AvlNodeMap | null;
}

export interface AvlVisualizationResponse {
  tree: AvlNodeMap | null;
  height: number;
  size: number;
  isValid: boolean;
  rotationHistory: RotationEvent[];
}

export interface HeapItemNode {
  index: number;
  id: number;
  priority: number;
  title: string;
  parentIndex?: number | null;
  leftIndex?: number | null;
  rightIndex?: number | null;
}

export interface HeapStepTrace {
  operation: string;
  index1: number;
  index2: number;
  description: string;
}

export interface HeapVisualizationResponse {
  elements: HeapItemNode[];
  size: number;
  isValid: boolean;
  traceHistory: HeapStepTrace[];
}

export interface AutocompleteMatch {
  term: string;
  paperIds: number[];
  frequency: number;
}

export interface AutocompleteResponse {
  query: string;
  executionTimeMicros: number;
  suggestions: AutocompleteMatch[];
  matchedPapers: Paper[];
}

export interface DsaSummary {
  naryTotalNodes: number;
  naryTreeDepth: number;
  avlPaperCount: number;
  avlTreeHeight: number;
  avlIsValid: boolean;
  avlTotalRotations: number;
  trieTotalWords: number;
  heapTotalItems: number;
  heapIsValid: boolean;
  heapTopPaper?: Paper;
}

export interface ComplexityEntry {
  dataStructure: string;
  purpose: string;
  searchTime: string;
  insertTime: string;
  deleteTime: string;
  spaceComplexity: string;
  keyFeature: string;
}
