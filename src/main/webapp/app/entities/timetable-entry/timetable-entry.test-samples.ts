import { ITimetableEntry, NewTimetableEntry } from './timetable-entry.model';

export const sampleWithRequiredData: ITimetableEntry = {
  id: 4001,
};

export const sampleWithPartialData: ITimetableEntry = {
  id: 31097,
};

export const sampleWithFullData: ITimetableEntry = {
  id: 20219,
};

export const sampleWithNewData: NewTimetableEntry = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
