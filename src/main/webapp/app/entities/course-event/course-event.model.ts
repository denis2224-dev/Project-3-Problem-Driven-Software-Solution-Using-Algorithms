import { ICourse } from 'app/entities/course/course.model';
import { CourseEventType } from 'app/entities/enumerations/course-event-type.model';
import { IProfessor } from 'app/entities/professor/professor.model';
import { IStudentGroup } from 'app/entities/student-group/student-group.model';

export interface ICourseEvent {
  id: number;
  eventType?: keyof typeof CourseEventType | null;
  durationMinutes?: number | null;
  expectedStudents?: number | null;
  requiredEquipment?: string | null;
  course?: Pick<ICourse, 'id' | 'name'> | null;
  professor?: Pick<IProfessor, 'id' | 'lastName'> | null;
  studentGroup?: Pick<IStudentGroup, 'id' | 'name'> | null;
}

export type NewCourseEvent = Omit<ICourseEvent, 'id'> & { id: null };
