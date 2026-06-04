import { IDepartment } from 'app/entities/department/department.model';

export interface IStudentGroup {
  id: number;
  name?: string | null;
  year?: number | null;
  groupSize?: number | null;
  department?: Pick<IDepartment, 'id' | 'name'> | null;
}

export type NewStudentGroup = Omit<IStudentGroup, 'id'> & { id: null };
