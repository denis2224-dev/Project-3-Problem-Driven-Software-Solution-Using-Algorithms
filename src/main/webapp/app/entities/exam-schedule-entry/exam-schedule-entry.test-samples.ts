import { IExamScheduleEntry, NewExamScheduleEntry } from './exam-schedule-entry.model';

export const sampleWithRequiredData: IExamScheduleEntry = {
  id: 12670,
};

export const sampleWithPartialData: IExamScheduleEntry = {
  id: 25940,
};

export const sampleWithFullData: IExamScheduleEntry = {
  id: 8869,
};

export const sampleWithNewData: NewExamScheduleEntry = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
