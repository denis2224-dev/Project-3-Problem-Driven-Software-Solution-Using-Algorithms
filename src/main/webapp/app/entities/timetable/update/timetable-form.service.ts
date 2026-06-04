import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITimetable, NewTimetable } from '../timetable.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITimetable for edit and NewTimetableFormGroupInput for create.
 */
type TimetableFormGroupInput = ITimetable | PartialWithRequiredKeyOf<NewTimetable>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITimetable | NewTimetable> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type TimetableFormRawValue = FormValueOf<ITimetable>;

type NewTimetableFormRawValue = FormValueOf<NewTimetable>;

type TimetableFormDefaults = Pick<NewTimetable, 'id' | 'createdAt'>;

type TimetableFormGroupContent = {
  id: FormControl<TimetableFormRawValue['id'] | NewTimetable['id']>;
  name: FormControl<TimetableFormRawValue['name']>;
  semester: FormControl<TimetableFormRawValue['semester']>;
  academicYear: FormControl<TimetableFormRawValue['academicYear']>;
  status: FormControl<TimetableFormRawValue['status']>;
  createdAt: FormControl<TimetableFormRawValue['createdAt']>;
};

export type TimetableFormGroup = FormGroup<TimetableFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TimetableFormService {
  createTimetableFormGroup(timetable?: TimetableFormGroupInput): TimetableFormGroup {
    const timetableRawValue = this.convertTimetableToTimetableRawValue({
      ...this.getFormDefaults(),
      ...(timetable ?? { id: null }),
    });
    return new FormGroup<TimetableFormGroupContent>({
      id: new FormControl(
        { value: timetableRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(timetableRawValue.name, {
        validators: [Validators.required, Validators.maxLength(160)],
      }),
      semester: new FormControl(timetableRawValue.semester, {
        validators: [Validators.required, Validators.maxLength(40)],
      }),
      academicYear: new FormControl(timetableRawValue.academicYear, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      status: new FormControl(timetableRawValue.status, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(timetableRawValue.createdAt, {
        validators: [Validators.required],
      }),
    });
  }

  getTimetable(form: TimetableFormGroup): ITimetable | NewTimetable {
    return this.convertTimetableRawValueToTimetable(form.getRawValue());
  }

  resetForm(form: TimetableFormGroup, timetable: TimetableFormGroupInput): void {
    const timetableRawValue = this.convertTimetableToTimetableRawValue({ ...this.getFormDefaults(), ...timetable });
    form.reset({
      ...timetableRawValue,
      id: { value: timetableRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TimetableFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertTimetableRawValueToTimetable(rawTimetable: TimetableFormRawValue | NewTimetableFormRawValue): ITimetable | NewTimetable {
    return {
      ...rawTimetable,
      createdAt: dayjs(rawTimetable.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertTimetableToTimetableRawValue(
    timetable: ITimetable | (Partial<NewTimetable> & TimetableFormDefaults),
  ): TimetableFormRawValue | PartialWithRequiredKeyOf<NewTimetableFormRawValue> {
    return {
      ...timetable,
      createdAt: timetable.createdAt ? timetable.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
