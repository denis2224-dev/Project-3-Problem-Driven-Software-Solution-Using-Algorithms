import dayjs from 'dayjs/esm';

import { TimetableStatus } from 'app/entities/enumerations/timetable-status.model';

export interface ITimetable {
  id: number;
  name?: string | null;
  semester?: string | null;
  academicYear?: string | null;
  status?: keyof typeof TimetableStatus | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewTimetable = Omit<ITimetable, 'id'> & { id: null };
