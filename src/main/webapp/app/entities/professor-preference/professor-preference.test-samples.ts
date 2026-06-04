import { IProfessorPreference, NewProfessorPreference } from './professor-preference.model';

export const sampleWithRequiredData: IProfessorPreference = {
  id: 7352,
  preferenceType: 'AVOID',
};

export const sampleWithPartialData: IProfessorPreference = {
  id: 17164,
  preferenceType: 'AVOID',
};

export const sampleWithFullData: IProfessorPreference = {
  id: 30406,
  preferenceType: 'PREFERRED',
};

export const sampleWithNewData: NewProfessorPreference = {
  preferenceType: 'AVOID',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
