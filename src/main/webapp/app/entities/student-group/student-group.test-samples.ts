import { IStudentGroup, NewStudentGroup } from './student-group.model';

export const sampleWithRequiredData: IStudentGroup = {
  id: 14027,
  name: 'but when',
  year: 6,
  groupSize: 3120,
};

export const sampleWithPartialData: IStudentGroup = {
  id: 9121,
  name: 'pale ew',
  year: 4,
  groupSize: 30285,
};

export const sampleWithFullData: IStudentGroup = {
  id: 29835,
  name: 'aboard',
  year: 5,
  groupSize: 13318,
};

export const sampleWithNewData: NewStudentGroup = {
  name: 'before down',
  year: 6,
  groupSize: 5507,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
