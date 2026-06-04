import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IScheduleConflict, NewScheduleConflict } from '../schedule-conflict.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IScheduleConflict for edit and NewScheduleConflictFormGroupInput for create.
 */
type ScheduleConflictFormGroupInput = IScheduleConflict | PartialWithRequiredKeyOf<NewScheduleConflict>;

type ScheduleConflictFormDefaults = Pick<NewScheduleConflict, 'id'>;

type ScheduleConflictFormGroupContent = {
  id: FormControl<IScheduleConflict['id'] | NewScheduleConflict['id']>;
  conflictType: FormControl<IScheduleConflict['conflictType']>;
  description: FormControl<IScheduleConflict['description']>;
  severity: FormControl<IScheduleConflict['severity']>;
  timetableVersion: FormControl<IScheduleConflict['timetableVersion']>;
};

export type ScheduleConflictFormGroup = FormGroup<ScheduleConflictFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ScheduleConflictFormService {
  createScheduleConflictFormGroup(scheduleConflict?: ScheduleConflictFormGroupInput): ScheduleConflictFormGroup {
    const scheduleConflictRawValue = {
      ...this.getFormDefaults(),
      ...(scheduleConflict ?? { id: null }),
    };
    return new FormGroup<ScheduleConflictFormGroupContent>({
      id: new FormControl(
        { value: scheduleConflictRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      conflictType: new FormControl(scheduleConflictRawValue.conflictType, {
        validators: [Validators.required],
      }),
      description: new FormControl(scheduleConflictRawValue.description, {
        validators: [Validators.required, Validators.maxLength(2000)],
      }),
      severity: new FormControl(scheduleConflictRawValue.severity, {
        validators: [Validators.required],
      }),
      timetableVersion: new FormControl(scheduleConflictRawValue.timetableVersion, {
        validators: [Validators.required],
      }),
    });
  }

  getScheduleConflict(form: ScheduleConflictFormGroup): IScheduleConflict | NewScheduleConflict {
    return form.getRawValue();
  }

  resetForm(form: ScheduleConflictFormGroup, scheduleConflict: ScheduleConflictFormGroupInput): void {
    const scheduleConflictRawValue = { ...this.getFormDefaults(), ...scheduleConflict };
    form.reset({
      ...scheduleConflictRawValue,
      id: { value: scheduleConflictRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ScheduleConflictFormDefaults {
    return {
      id: null,
    };
  }
}
