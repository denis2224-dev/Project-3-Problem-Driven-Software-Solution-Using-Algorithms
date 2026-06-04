import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IExamScheduleEntry, NewExamScheduleEntry } from '../exam-schedule-entry.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExamScheduleEntry for edit and NewExamScheduleEntryFormGroupInput for create.
 */
type ExamScheduleEntryFormGroupInput = IExamScheduleEntry | PartialWithRequiredKeyOf<NewExamScheduleEntry>;

type ExamScheduleEntryFormDefaults = Pick<NewExamScheduleEntry, 'id'>;

type ExamScheduleEntryFormGroupContent = {
  id: FormControl<IExamScheduleEntry['id'] | NewExamScheduleEntry['id']>;
  exam: FormControl<IExamScheduleEntry['exam']>;
  room: FormControl<IExamScheduleEntry['room']>;
  timeslot: FormControl<IExamScheduleEntry['timeslot']>;
  timetableVersion: FormControl<IExamScheduleEntry['timetableVersion']>;
};

export type ExamScheduleEntryFormGroup = FormGroup<ExamScheduleEntryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExamScheduleEntryFormService {
  createExamScheduleEntryFormGroup(examScheduleEntry?: ExamScheduleEntryFormGroupInput): ExamScheduleEntryFormGroup {
    const examScheduleEntryRawValue = {
      ...this.getFormDefaults(),
      ...(examScheduleEntry ?? { id: null }),
    };
    return new FormGroup<ExamScheduleEntryFormGroupContent>({
      id: new FormControl(
        { value: examScheduleEntryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      exam: new FormControl(examScheduleEntryRawValue.exam, {
        validators: [Validators.required],
      }),
      room: new FormControl(examScheduleEntryRawValue.room, {
        validators: [Validators.required],
      }),
      timeslot: new FormControl(examScheduleEntryRawValue.timeslot, {
        validators: [Validators.required],
      }),
      timetableVersion: new FormControl(examScheduleEntryRawValue.timetableVersion, {
        validators: [Validators.required],
      }),
    });
  }

  getExamScheduleEntry(form: ExamScheduleEntryFormGroup): IExamScheduleEntry | NewExamScheduleEntry {
    return form.getRawValue();
  }

  resetForm(form: ExamScheduleEntryFormGroup, examScheduleEntry: ExamScheduleEntryFormGroupInput): void {
    const examScheduleEntryRawValue = { ...this.getFormDefaults(), ...examScheduleEntry };
    form.reset({
      ...examScheduleEntryRawValue,
      id: { value: examScheduleEntryRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ExamScheduleEntryFormDefaults {
    return {
      id: null,
    };
  }
}
