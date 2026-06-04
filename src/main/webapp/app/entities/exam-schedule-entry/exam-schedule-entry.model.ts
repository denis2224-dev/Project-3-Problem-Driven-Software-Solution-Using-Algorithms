import { IExam } from 'app/entities/exam/exam.model';
import { IRoom } from 'app/entities/room/room.model';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';
import { ITimetableVersion } from 'app/entities/timetable-version/timetable-version.model';

export interface IExamScheduleEntry {
  id: number;
  exam?: Pick<IExam, 'id' | 'name'> | null;
  room?: Pick<IRoom, 'id' | 'code'> | null;
  timeslot?: Pick<ITimeslot, 'id' | 'startTime'> | null;
  timetableVersion?: Pick<ITimetableVersion, 'id' | 'versionNumber'> | null;
}

export type NewExamScheduleEntry = Omit<IExamScheduleEntry, 'id'> & { id: null };
