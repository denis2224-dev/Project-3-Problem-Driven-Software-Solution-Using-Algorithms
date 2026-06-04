import { IDepartment, NewDepartment } from './department.model';

export const sampleWithRequiredData: IDepartment = {
  id: 32001,
  name: 'phew',
  code: 'heavenly fledgling b',
};

export const sampleWithPartialData: IDepartment = {
  id: 18435,
  name: 'sneak phew daintily',
  code: 'oof worth',
};

export const sampleWithFullData: IDepartment = {
  id: 13276,
  name: 'endow',
  code: 'headline save',
};

export const sampleWithNewData: NewDepartment = {
  name: 'peaceful if',
  code: 'stock peony',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
