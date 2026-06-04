import { IScheduleConflict, NewScheduleConflict } from './schedule-conflict.model';

export const sampleWithRequiredData: IScheduleConflict = {
  id: 10609,
  conflictType: 'GROUP_CLASH',
  description: 'intend',
  severity: 'HARD',
};

export const sampleWithPartialData: IScheduleConflict = {
  id: 5653,
  conflictType: 'ROOM_CLASH',
  description: 'quizzically quaintly unfortunately',
  severity: 'SOFT',
};

export const sampleWithFullData: IScheduleConflict = {
  id: 10146,
  conflictType: 'EQUIPMENT_MISMATCH',
  description: 'especially',
  severity: 'SOFT',
};

export const sampleWithNewData: NewScheduleConflict = {
  conflictType: 'GROUP_CLASH',
  description: 'correctly',
  severity: 'SOFT',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
