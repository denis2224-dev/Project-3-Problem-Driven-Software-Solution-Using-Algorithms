import dayjs from 'dayjs/esm';

import { ITimetableVersion, NewTimetableVersion } from './timetable-version.model';

export const sampleWithRequiredData: ITimetableVersion = {
  id: 5228,
  versionNumber: 29646,
  createdAt: dayjs('2026-06-04T01:41'),
};

export const sampleWithPartialData: ITimetableVersion = {
  id: 11293,
  versionNumber: 8591,
  createdAt: dayjs('2026-06-04T09:51'),
  totalSoftPenalty: 6640,
  averageStudentGap: 20275.53,
};

export const sampleWithFullData: ITimetableVersion = {
  id: 23767,
  versionNumber: 20641,
  createdAt: dayjs('2026-06-03T20:18'),
  totalHardConflicts: 20969,
  totalSoftPenalty: 24762,
  averageStudentGap: 10496.27,
  roomUtilization: 7142.81,
};

export const sampleWithNewData: NewTimetableVersion = {
  versionNumber: 15787,
  createdAt: dayjs('2026-06-04T11:24'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
