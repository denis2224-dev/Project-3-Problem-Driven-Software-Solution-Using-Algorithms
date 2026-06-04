import { ICourse } from 'app/entities/course/course.model';
import { IStudentGroup } from 'app/entities/student-group/student-group.model';

export interface IExam {
  id: number;
  name?: string | null;
  durationMinutes?: number | null;
  expectedStudents?: number | null;
  course?: Pick<ICourse, 'id' | 'code'> | null;
  studentGroup?: Pick<IStudentGroup, 'id' | 'name'> | null;
}

export type NewExam = Omit<IExam, 'id'> & { id: null };
