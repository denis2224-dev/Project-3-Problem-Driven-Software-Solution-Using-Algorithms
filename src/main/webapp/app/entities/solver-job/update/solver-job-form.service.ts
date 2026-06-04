import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISolverJob, NewSolverJob } from '../solver-job.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISolverJob for edit and NewSolverJobFormGroupInput for create.
 */
type SolverJobFormGroupInput = ISolverJob | PartialWithRequiredKeyOf<NewSolverJob>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISolverJob | NewSolverJob> = Omit<T, 'startedAt' | 'finishedAt'> & {
  startedAt?: string | null;
  finishedAt?: string | null;
};

type SolverJobFormRawValue = FormValueOf<ISolverJob>;

type NewSolverJobFormRawValue = FormValueOf<NewSolverJob>;

type SolverJobFormDefaults = Pick<NewSolverJob, 'id' | 'startedAt' | 'finishedAt'>;

type SolverJobFormGroupContent = {
  id: FormControl<SolverJobFormRawValue['id'] | NewSolverJob['id']>;
  status: FormControl<SolverJobFormRawValue['status']>;
  startedAt: FormControl<SolverJobFormRawValue['startedAt']>;
  finishedAt: FormControl<SolverJobFormRawValue['finishedAt']>;
  progressPercent: FormControl<SolverJobFormRawValue['progressPercent']>;
  message: FormControl<SolverJobFormRawValue['message']>;
  hardConflictCount: FormControl<SolverJobFormRawValue['hardConflictCount']>;
  softPenaltyScore: FormControl<SolverJobFormRawValue['softPenaltyScore']>;
  backtrackCount: FormControl<SolverJobFormRawValue['backtrackCount']>;
  domainReductionCount: FormControl<SolverJobFormRawValue['domainReductionCount']>;
  runtimeMs: FormControl<SolverJobFormRawValue['runtimeMs']>;
};

export type SolverJobFormGroup = FormGroup<SolverJobFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SolverJobFormService {
  createSolverJobFormGroup(solverJob?: SolverJobFormGroupInput): SolverJobFormGroup {
    const solverJobRawValue = this.convertSolverJobToSolverJobRawValue({
      ...this.getFormDefaults(),
      ...(solverJob ?? { id: null }),
    });
    return new FormGroup<SolverJobFormGroupContent>({
      id: new FormControl(
        { value: solverJobRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      status: new FormControl(solverJobRawValue.status, {
        validators: [Validators.required],
      }),
      startedAt: new FormControl(solverJobRawValue.startedAt),
      finishedAt: new FormControl(solverJobRawValue.finishedAt),
      progressPercent: new FormControl(solverJobRawValue.progressPercent, {
        validators: [Validators.min(0), Validators.max(100)],
      }),
      message: new FormControl(solverJobRawValue.message, {
        validators: [Validators.maxLength(2000)],
      }),
      hardConflictCount: new FormControl(solverJobRawValue.hardConflictCount, {
        validators: [Validators.min(0)],
      }),
      softPenaltyScore: new FormControl(solverJobRawValue.softPenaltyScore, {
        validators: [Validators.min(0)],
      }),
      backtrackCount: new FormControl(solverJobRawValue.backtrackCount, {
        validators: [Validators.min(0)],
      }),
      domainReductionCount: new FormControl(solverJobRawValue.domainReductionCount, {
        validators: [Validators.min(0)],
      }),
      runtimeMs: new FormControl(solverJobRawValue.runtimeMs, {
        validators: [Validators.min(0)],
      }),
    });
  }

  getSolverJob(form: SolverJobFormGroup): ISolverJob | NewSolverJob {
    return this.convertSolverJobRawValueToSolverJob(form.getRawValue());
  }

  resetForm(form: SolverJobFormGroup, solverJob: SolverJobFormGroupInput): void {
    const solverJobRawValue = this.convertSolverJobToSolverJobRawValue({ ...this.getFormDefaults(), ...solverJob });
    form.reset({
      ...solverJobRawValue,
      id: { value: solverJobRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SolverJobFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startedAt: currentTime,
      finishedAt: currentTime,
    };
  }

  private convertSolverJobRawValueToSolverJob(rawSolverJob: SolverJobFormRawValue | NewSolverJobFormRawValue): ISolverJob | NewSolverJob {
    return {
      ...rawSolverJob,
      startedAt: dayjs(rawSolverJob.startedAt, DATE_TIME_FORMAT),
      finishedAt: dayjs(rawSolverJob.finishedAt, DATE_TIME_FORMAT),
    };
  }

  private convertSolverJobToSolverJobRawValue(
    solverJob: ISolverJob | (Partial<NewSolverJob> & SolverJobFormDefaults),
  ): SolverJobFormRawValue | PartialWithRequiredKeyOf<NewSolverJobFormRawValue> {
    return {
      ...solverJob,
      startedAt: solverJob.startedAt ? solverJob.startedAt.format(DATE_TIME_FORMAT) : undefined,
      finishedAt: solverJob.finishedAt ? solverJob.finishedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
