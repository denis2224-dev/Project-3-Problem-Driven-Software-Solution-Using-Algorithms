import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITimetableEntry, NewTimetableEntry } from '../timetable-entry.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITimetableEntry for edit and NewTimetableEntryFormGroupInput for create.
 */
type TimetableEntryFormGroupInput = ITimetableEntry | PartialWithRequiredKeyOf<NewTimetableEntry>;

type TimetableEntryFormDefaults = Pick<NewTimetableEntry, 'id'>;

type TimetableEntryFormGroupContent = {
  id: FormControl<ITimetableEntry['id'] | NewTimetableEntry['id']>;
  timetableVersion: FormControl<ITimetableEntry['timetableVersion']>;
  courseEvent: FormControl<ITimetableEntry['courseEvent']>;
  room: FormControl<ITimetableEntry['room']>;
  timeslot: FormControl<ITimetableEntry['timeslot']>;
};

export type TimetableEntryFormGroup = FormGroup<TimetableEntryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TimetableEntryFormService {
  createTimetableEntryFormGroup(timetableEntry?: TimetableEntryFormGroupInput): TimetableEntryFormGroup {
    const timetableEntryRawValue = {
      ...this.getFormDefaults(),
      ...(timetableEntry ?? { id: null }),
    };
    return new FormGroup<TimetableEntryFormGroupContent>({
      id: new FormControl(
        { value: timetableEntryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      timetableVersion: new FormControl(timetableEntryRawValue.timetableVersion, {
        validators: [Validators.required],
      }),
      courseEvent: new FormControl(timetableEntryRawValue.courseEvent, {
        validators: [Validators.required],
      }),
      room: new FormControl(timetableEntryRawValue.room, {
        validators: [Validators.required],
      }),
      timeslot: new FormControl(timetableEntryRawValue.timeslot, {
        validators: [Validators.required],
      }),
    });
  }

  getTimetableEntry(form: TimetableEntryFormGroup): ITimetableEntry | NewTimetableEntry {
    return form.getRawValue();
  }

  resetForm(form: TimetableEntryFormGroup, timetableEntry: TimetableEntryFormGroupInput): void {
    const timetableEntryRawValue = { ...this.getFormDefaults(), ...timetableEntry };
    form.reset({
      ...timetableEntryRawValue,
      id: { value: timetableEntryRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TimetableEntryFormDefaults {
    return {
      id: null,
    };
  }
}
