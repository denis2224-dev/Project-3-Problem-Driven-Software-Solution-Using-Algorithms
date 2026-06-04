import dayjs from 'dayjs/esm';

import { ISolverJob } from 'app/entities/solver-job/solver-job.model';
import { ITimetable } from 'app/entities/timetable/timetable.model';

export interface ITimetableVersion {
  id: number;
  versionNumber?: number | null;
  createdAt?: dayjs.Dayjs | null;
  totalHardConflicts?: number | null;
  totalSoftPenalty?: number | null;
  averageStudentGap?: number | null;
  roomUtilization?: number | null;
  timetable?: Pick<ITimetable, 'id' | 'name'> | null;
  solverJob?: Pick<ISolverJob, 'id'> | null;
}

export type NewTimetableVersion = Omit<ITimetableVersion, 'id'> & { id: null };
