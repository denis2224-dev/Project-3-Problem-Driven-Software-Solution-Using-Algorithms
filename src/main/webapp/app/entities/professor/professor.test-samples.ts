import { IProfessor, NewProfessor } from './professor.model';

export const sampleWithRequiredData: IProfessor = {
  id: 2741,
  firstName: 'Jeremiah',
  lastName: 'Goyette',
  email: 'Margarita65@gmail.com',
};

export const sampleWithPartialData: IProfessor = {
  id: 1632,
  firstName: 'Jedidiah',
  lastName: 'Hickle',
  email: 'Marta.Ernser5@hotmail.com',
};

export const sampleWithFullData: IProfessor = {
  id: 313,
  firstName: 'Kacie',
  lastName: 'Schmeler',
  email: 'Vickie_Rempel29@hotmail.com',
  title: 'adventurously',
};

export const sampleWithNewData: NewProfessor = {
  firstName: 'April',
  lastName: 'Haag',
  email: 'Theron_Pollich67@hotmail.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
