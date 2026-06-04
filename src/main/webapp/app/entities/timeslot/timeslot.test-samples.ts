import { ITimeslot, NewTimeslot } from './timeslot.model';

export const sampleWithRequiredData: ITimeslot = {
  id: 22033,
  dayOfWeek: 'SATURDAY',
  startTime: 'along',
  endTime: 'soap ',
};

export const sampleWithPartialData: ITimeslot = {
  id: 28993,
  dayOfWeek: 'TUESDAY',
  startTime: 'oof u',
  endTime: 'and c',
};

export const sampleWithFullData: ITimeslot = {
  id: 30259,
  dayOfWeek: 'WEDNESDAY',
  startTime: 'sneak',
  endTime: 'erXXX',
};

export const sampleWithNewData: NewTimeslot = {
  dayOfWeek: 'MONDAY',
  startTime: 'er un',
  endTime: 'sleep',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
