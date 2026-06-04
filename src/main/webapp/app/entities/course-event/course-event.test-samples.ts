import { ICourseEvent, NewCourseEvent } from './course-event.model';

export const sampleWithRequiredData: ICourseEvent = {
  id: 2358,
  eventType: 'LABORATORY',
  durationMinutes: 215,
  expectedStudents: 19972,
};

export const sampleWithPartialData: ICourseEvent = {
  id: 25838,
  eventType: 'SEMINAR',
  durationMinutes: 109,
  expectedStudents: 9653,
};

export const sampleWithFullData: ICourseEvent = {
  id: 29912,
  eventType: 'SEMINAR',
  durationMinutes: 174,
  expectedStudents: 30588,
  requiredEquipment: 'scared inwardly bah',
};

export const sampleWithNewData: NewCourseEvent = {
  eventType: 'SEMINAR',
  durationMinutes: 118,
  expectedStudents: 25344,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
