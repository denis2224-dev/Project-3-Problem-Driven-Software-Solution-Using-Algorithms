import { IBuilding, NewBuilding } from './building.model';

export const sampleWithRequiredData: IBuilding = {
  id: 100,
  name: 'how clamour negative',
  code: 'split deny',
};

export const sampleWithPartialData: IBuilding = {
  id: 7256,
  name: 'oof configuration',
  code: 'why above',
};

export const sampleWithFullData: IBuilding = {
  id: 10451,
  name: 'down despite',
  code: 'artistic sharply',
  address: 'excepting thrifty',
};

export const sampleWithNewData: NewBuilding = {
  name: 'venture accept',
  code: 'vainly busily',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
