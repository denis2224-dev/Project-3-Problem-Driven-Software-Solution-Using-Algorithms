import { ICourse, NewCourse } from './course.model';

export const sampleWithRequiredData: ICourse = {
  id: 8824,
  code: 'radiant',
  name: 'sturdy',
  credits: 2,
};

export const sampleWithPartialData: ICourse = {
  id: 1689,
  code: 'while',
  name: 'ugh',
  credits: 16,
};

export const sampleWithFullData: ICourse = {
  id: 30968,
  code: 'weakly',
  name: 'whenever pro',
  credits: 13,
};

export const sampleWithNewData: NewCourse = {
  code: 'unto',
  name: 'enchanting vivaciously',
  credits: 30,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
