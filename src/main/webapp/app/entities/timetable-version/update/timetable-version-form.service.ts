import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITimetableVersion, NewTimetableVersion } from '../timetable-version.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITimetableVersion for edit and NewTimetableVersionFormGroupInput for create.
 */
type TimetableVersionFormGroupInput = ITimetableVersion | PartialWithRequiredKeyOf<NewTimetableVersion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITimetableVersion | NewTimetableVersion> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type TimetableVersionFormRawValue = FormValueOf<ITimetableVersion>;

type NewTimetableVersionFormRawValue = FormValueOf<NewTimetableVersion>;

type TimetableVersionFormDefaults = Pick<NewTimetableVersion, 'id' | 'createdAt'>;

type TimetableVersionFormGroupContent = {
  id: FormControl<TimetableVersionFormRawValue['id'] | NewTimetableVersion['id']>;
  versionNumber: FormControl<TimetableVersionFormRawValue['versionNumber']>;
  createdAt: FormControl<TimetableVersionFormRawValue['createdAt']>;
  totalHardConflicts: FormControl<TimetableVersionFormRawValue['totalHardConflicts']>;
  totalSoftPenalty: FormControl<TimetableVersionFormRawValue['totalSoftPenalty']>;
  averageStudentGap: FormControl<TimetableVersionFormRawValue['averageStudentGap']>;
  roomUtilization: FormControl<TimetableVersionFormRawValue['roomUtilization']>;
  timetable: FormControl<TimetableVersionFormRawValue['timetable']>;
  solverJob: FormControl<TimetableVersionFormRawValue['solverJob']>;
};

export type TimetableVersionFormGroup = FormGroup<TimetableVersionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TimetableVersionFormService {
  createTimetableVersionFormGroup(timetableVersion?: TimetableVersionFormGroupInput): TimetableVersionFormGroup {
    const timetableVersionRawValue = this.convertTimetableVersionToTimetableVersionRawValue({
      ...this.getFormDefaults(),
      ...(timetableVersion ?? { id: null }),
    });
    return new FormGroup<TimetableVersionFormGroupContent>({
      id: new FormControl(
        { value: timetableVersionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      versionNumber: new FormControl(timetableVersionRawValue.versionNumber, {
        validators: [Validators.required, Validators.min(1)],
      }),
      createdAt: new FormControl(timetableVersionRawValue.createdAt, {
        validators: [Validators.required],
      }),
      totalHardConflicts: new FormControl(timetableVersionRawValue.totalHardConflicts, {
        validators: [Validators.min(0)],
      }),
      totalSoftPenalty: new FormControl(timetableVersionRawValue.totalSoftPenalty, {
        validators: [Validators.min(0)],
      }),
      averageStudentGap: new FormControl(timetableVersionRawValue.averageStudentGap, {
        validators: [Validators.min(0)],
      }),
      roomUtilization: new FormControl(timetableVersionRawValue.roomUtilization, {
        validators: [Validators.min(0)],
      }),
      timetable: new FormControl(timetableVersionRawValue.timetable, {
        validators: [Validators.required],
      }),
      solverJob: new FormControl(timetableVersionRawValue.solverJob),
    });
  }

  getTimetableVersion(form: TimetableVersionFormGroup): ITimetableVersion | NewTimetableVersion {
    return this.convertTimetableVersionRawValueToTimetableVersion(form.getRawValue());
  }

  resetForm(form: TimetableVersionFormGroup, timetableVersion: TimetableVersionFormGroupInput): void {
    const timetableVersionRawValue = this.convertTimetableVersionToTimetableVersionRawValue({
      ...this.getFormDefaults(),
      ...timetableVersion,
    });
    form.reset({
      ...timetableVersionRawValue,
      id: { value: timetableVersionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TimetableVersionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertTimetableVersionRawValueToTimetableVersion(
    rawTimetableVersion: TimetableVersionFormRawValue | NewTimetableVersionFormRawValue,
  ): ITimetableVersion | NewTimetableVersion {
    return {
      ...rawTimetableVersion,
      createdAt: dayjs(rawTimetableVersion.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertTimetableVersionToTimetableVersionRawValue(
    timetableVersion: ITimetableVersion | (Partial<NewTimetableVersion> & TimetableVersionFormDefaults),
  ): TimetableVersionFormRawValue | PartialWithRequiredKeyOf<NewTimetableVersionFormRawValue> {
    return {
      ...timetableVersion,
      createdAt: timetableVersion.createdAt ? timetableVersion.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
