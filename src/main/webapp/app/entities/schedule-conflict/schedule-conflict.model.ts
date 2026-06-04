import { ConflictSeverity } from 'app/entities/enumerations/conflict-severity.model';
import { ConflictType } from 'app/entities/enumerations/conflict-type.model';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';

export interface IScheduleConflict {
  id: number;
  conflictType?: keyof typeof ConflictType | null;
  description?: string | null;
  severity?: keyof typeof ConflictSeverity | null;
  timetableVersion?: Pick<ITimetableVersion, 'id' | 'versionNumber'> | null;
}

export type NewScheduleConflict = Omit<IScheduleConflict, 'id'> & { id: null };
