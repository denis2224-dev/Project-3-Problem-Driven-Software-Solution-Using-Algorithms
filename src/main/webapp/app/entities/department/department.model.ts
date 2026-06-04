import { IFaculty } from 'app/entities/faculty/faculty.model';

export interface IDepartment {
  id: number;
  name?: string | null;
  code?: string | null;
  faculty?: Pick<IFaculty, 'id' | 'name'> | null;
}

export type NewDepartment = Omit<IDepartment, 'id'> & { id: null };
