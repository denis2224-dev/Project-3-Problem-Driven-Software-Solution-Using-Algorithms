import { IDepartment } from 'app/entities/department/department.model';

export interface IProfessor {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  title?: string | null;
  department?: Pick<IDepartment, 'id' | 'name'> | null;
}

export type NewProfessor = Omit<IProfessor, 'id'> & { id: null };
