import dayjs from 'dayjs/esm';

import { ISolverJob, NewSolverJob } from './solver-job.model';

export const sampleWithRequiredData: ISolverJob = {
  id: 4905,
  status: 'BUILDING_CONFLICT_GRAPH',
};

export const sampleWithPartialData: ISolverJob = {
  id: 29109,
  status: 'CREATED',
  finishedAt: dayjs('2026-06-04T05:04'),
  progressPercent: 1,
  message: 'reassuringly',
  backtrackCount: 24710,
};

export const sampleWithFullData: ISolverJob = {
  id: 3610,
  status: 'RUNNING_AC3',
  startedAt: dayjs('2026-06-04T09:37'),
  finishedAt: dayjs('2026-06-03T18:30'),
  progressPercent: 51,
  message: 'faithfully boohoo unethically',
  hardConflictCount: 21355,
  softPenaltyScore: 6936,
  backtrackCount: 29106,
  domainReductionCount: 4710,
  runtimeMs: 20917,
};

export const sampleWithNewData: NewSolverJob = {
  status: 'FAILED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
