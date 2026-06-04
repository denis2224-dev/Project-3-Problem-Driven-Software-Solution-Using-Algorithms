import { IFaculty, NewFaculty } from './faculty.model';

export const sampleWithRequiredData: IFaculty = {
  id: 7802,
  name: 'guidance happy-go-lucky underneath',
  code: 'adventurously',
};

export const sampleWithPartialData: IFaculty = {
  id: 24848,
  name: 'pfft',
  code: 'gosh slime who',
};

export const sampleWithFullData: IFaculty = {
  id: 11770,
  name: 'gadzooks ouch kick',
  code: 'woefully',
};

export const sampleWithNewData: NewFaculty = {
  name: 'mealy although sprinkles',
  code: 'even',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
