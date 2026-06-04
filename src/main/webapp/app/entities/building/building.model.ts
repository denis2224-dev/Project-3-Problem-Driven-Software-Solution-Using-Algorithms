export interface IBuilding {
  id: number;
  name?: string | null;
  code?: string | null;
  address?: string | null;
}

export type NewBuilding = Omit<IBuilding, 'id'> & { id: null };
