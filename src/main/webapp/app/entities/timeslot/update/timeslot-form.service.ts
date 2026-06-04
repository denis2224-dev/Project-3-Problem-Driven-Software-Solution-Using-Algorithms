import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITimeslot, NewTimeslot } from '../timeslot.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITimeslot for edit and NewTimeslotFormGroupInput for create.
 */
type TimeslotFormGroupInput = ITimeslot | PartialWithRequiredKeyOf<NewTimeslot>;

type TimeslotFormDefaults = Pick<NewTimeslot, 'id'>;

type TimeslotFormGroupContent = {
  id: FormControl<ITimeslot['id'] | NewTimeslot['id']>;
  dayOfWeek: FormControl<ITimeslot['dayOfWeek']>;
  startTime: FormControl<ITimeslot['startTime']>;
  endTime: FormControl<ITimeslot['endTime']>;
};

export type TimeslotFormGroup = FormGroup<TimeslotFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TimeslotFormService {
  createTimeslotFormGroup(timeslot?: TimeslotFormGroupInput): TimeslotFormGroup {
    const timeslotRawValue = {
      ...this.getFormDefaults(),
      ...(timeslot ?? { id: null }),
    };
    return new FormGroup<TimeslotFormGroupContent>({
      id: new FormControl(
        { value: timeslotRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dayOfWeek: new FormControl(timeslotRawValue.dayOfWeek, {
        validators: [Validators.required],
      }),
      startTime: new FormControl(timeslotRawValue.startTime, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(5)],
      }),
      endTime: new FormControl(timeslotRawValue.endTime, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(5)],
      }),
    });
  }

  getTimeslot(form: TimeslotFormGroup): ITimeslot | NewTimeslot {
    return form.getRawValue();
  }

  resetForm(form: TimeslotFormGroup, timeslot: TimeslotFormGroupInput): void {
    const timeslotRawValue = { ...this.getFormDefaults(), ...timeslot };
    form.reset({
      ...timeslotRawValue,
      id: { value: timeslotRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TimeslotFormDefaults {
    return {
      id: null,
    };
  }
}
