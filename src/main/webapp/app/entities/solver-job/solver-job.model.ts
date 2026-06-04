import dayjs from 'dayjs/esm';

import { SolverJobStatus } from 'app/entities/enumerations/solver-job-status.model';

export interface ISolverJob {
  id: number;
  status?: keyof typeof SolverJobStatus | null;
  startedAt?: dayjs.Dayjs | null;
  finishedAt?: dayjs.Dayjs | null;
  progressPercent?: number | null;
  message?: string | null;
  hardConflictCount?: number | null;
  softPenaltyScore?: number | null;
  backtrackCount?: number | null;
  domainReductionCount?: number | null;
  runtimeMs?: number | null;
}

export type NewSolverJob = Omit<ISolverJob, 'id'> & { id: null };
