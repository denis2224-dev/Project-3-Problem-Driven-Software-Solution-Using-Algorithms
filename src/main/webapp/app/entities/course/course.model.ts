import { IDepartment } from 'app/entities/department/department.model';

export interface ICourse {
  id: number;
  code?: string | null;
  name?: string | null;
  credits?: number | null;
  department?: Pick<IDepartment, 'id' | 'name'> | null;
}

export type NewCourse = Omit<ICourse, 'id'> & { id: null };
