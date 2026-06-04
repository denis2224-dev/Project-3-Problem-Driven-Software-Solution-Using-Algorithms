import { ICourseEvent } from 'app/entities/course-event/course-event.model';
import { IRoom } from 'app/entities/room/room.model';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';

export interface ITimetableEntry {
  id: number;
  timetableVersion?: Pick<ITimetableVersion, 'id' | 'versionNumber'> | null;
  courseEvent?: Pick<ICourseEvent, 'id'> | null;
  room?: Pick<IRoom, 'id' | 'code'> | null;
  timeslot?: Pick<ITimeslot, 'id' | 'startTime'> | null;
}

export type NewTimetableEntry = Omit<ITimetableEntry, 'id'> & { id: null };
