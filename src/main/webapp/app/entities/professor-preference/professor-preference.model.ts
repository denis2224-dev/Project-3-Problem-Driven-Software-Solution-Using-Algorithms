import { ProfessorPreferenceType } from 'app/entities/enumerations/professor-preference-type.model';
import { IProfessor } from 'app/entities/professor/professor.model';
import { ITimeslot } from 'app/entities/timeslot/timeslot.model';

export interface IProfessorPreference {
  id: number;
  preferenceType?: keyof typeof ProfessorPreferenceType | null;
  professor?: Pick<IProfessor, 'id' | 'lastName'> | null;
  timeslot?: Pick<ITimeslot, 'id' | 'startTime'> | null;
}

export type NewProfessorPreference = Omit<IProfessorPreference, 'id'> & { id: null };
