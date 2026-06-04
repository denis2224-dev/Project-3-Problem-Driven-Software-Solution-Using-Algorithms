import { IRoom, NewRoom } from './room.model';

export const sampleWithRequiredData: IRoom = {
  id: 25051,
  name: 'wire',
  code: 'inspect how',
  capacity: 29891,
  roomType: 'LECTURE',
};

export const sampleWithPartialData: IRoom = {
  id: 3867,
  name: 'until hm',
  code: 'as',
  capacity: 15782,
  roomType: 'LABORATORY',
  equipment: 'deny phooey twine',
};

export const sampleWithFullData: IRoom = {
  id: 10102,
  name: 'roughly brr opera',
  code: 'pushy meanwhile',
  capacity: 6378,
  roomType: 'SEMINAR',
  equipment: 'quizzically brandish',
};

export const sampleWithNewData: NewRoom = {
  name: 'cellar',
  code: 'hotfoot linseed forenenst',
  capacity: 10147,
  roomType: 'SEMINAR',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
