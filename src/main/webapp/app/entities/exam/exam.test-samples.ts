import { IExam, NewExam } from './exam.model';

export const sampleWithRequiredData: IExam = {
  id: 19529,
  name: 'quizzically viciously unlike',
  durationMinutes: 189,
  expectedStudents: 25880,
};

export const sampleWithPartialData: IExam = {
  id: 8408,
  name: 'downchange',
  durationMinutes: 41,
  expectedStudents: 2609,
};

export const sampleWithFullData: IExam = {
  id: 21245,
  name: 'critical upright',
  durationMinutes: 95,
  expectedStudents: 18684,
};

export const sampleWithNewData: NewExam = {
  name: 'culture ack',
  durationMinutes: 230,
  expectedStudents: 7775,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
