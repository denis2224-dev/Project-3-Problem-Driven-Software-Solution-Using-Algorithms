import { AcademicDayOfWeek } from 'app/entities/enumerations/academic-day-of-week.model';

export interface ITimeslot {
  id: number;
  dayOfWeek?: keyof typeof AcademicDayOfWeek | null;
  startTime?: string | null;
  endTime?: string | null;
}

export type NewTimeslot = Omit<ITimeslot, 'id'> & { id: null };
