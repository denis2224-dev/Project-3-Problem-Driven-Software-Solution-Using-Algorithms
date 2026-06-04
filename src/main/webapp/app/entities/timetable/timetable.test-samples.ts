import dayjs from 'dayjs/esm';

import { ITimetable, NewTimetable } from './timetable.model';

export const sampleWithRequiredData: ITimetable = {
  id: 1071,
  name: 'cruel knife',
  semester: 'dishonor',
  academicYear: 'ruin whine',
  status: 'DRAFT',
  createdAt: dayjs('2026-06-03T21:48'),
};

export const sampleWithPartialData: ITimetable = {
  id: 9380,
  name: 'behind',
  semester: 'furthermore',
  academicYear: 'till aha',
  status: 'APPROVED',
  createdAt: dayjs('2026-06-04T04:10'),
};

export const sampleWithFullData: ITimetable = {
  id: 1658,
  name: 'yieldingly however',
  semester: 'wound caring',
  academicYear: 'briefly polarisation',
  status: 'PUBLISHED',
  createdAt: dayjs('2026-06-04T12:31'),
};

export const sampleWithNewData: NewTimetable = {
  name: 'above controvert',
  semester: 'mmm indeed',
  academicYear: 'bleak till after',
  status: 'APPROVED',
  createdAt: dayjs('2026-06-04T16:49'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
