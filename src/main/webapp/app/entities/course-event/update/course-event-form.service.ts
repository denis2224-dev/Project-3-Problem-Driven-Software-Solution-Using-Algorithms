import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICourseEvent, NewCourseEvent } from '../course-event.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICourseEvent for edit and NewCourseEventFormGroupInput for create.
 */
type CourseEventFormGroupInput = ICourseEvent | PartialWithRequiredKeyOf<NewCourseEvent>;

type CourseEventFormDefaults = Pick<NewCourseEvent, 'id'>;

type CourseEventFormGroupContent = {
  id: FormControl<ICourseEvent['id'] | NewCourseEvent['id']>;
  eventType: FormControl<ICourseEvent['eventType']>;
  durationMinutes: FormControl<ICourseEvent['durationMinutes']>;
  expectedStudents: FormControl<ICourseEvent['expectedStudents']>;
  requiredEquipment: FormControl<ICourseEvent['requiredEquipment']>;
  course: FormControl<ICourseEvent['course']>;
  professor: FormControl<ICourseEvent['professor']>;
  studentGroup: FormControl<ICourseEvent['studentGroup']>;
};

export type CourseEventFormGroup = FormGroup<CourseEventFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CourseEventFormService {
  createCourseEventFormGroup(courseEvent?: CourseEventFormGroupInput): CourseEventFormGroup {
    const courseEventRawValue = {
      ...this.getFormDefaults(),
      ...(courseEvent ?? { id: null }),
    };
    return new FormGroup<CourseEventFormGroupContent>({
      id: new FormControl(
        { value: courseEventRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      eventType: new FormControl(courseEventRawValue.eventType, {
        validators: [Validators.required],
      }),
      durationMinutes: new FormControl(courseEventRawValue.durationMinutes, {
        validators: [Validators.required, Validators.min(30), Validators.max(240)],
      }),
      expectedStudents: new FormControl(courseEventRawValue.expectedStudents, {
        validators: [Validators.required, Validators.min(1)],
      }),
      requiredEquipment: new FormControl(courseEventRawValue.requiredEquipment, {
        validators: [Validators.maxLength(1000)],
      }),
      course: new FormControl(courseEventRawValue.course, {
        validators: [Validators.required],
      }),
      professor: new FormControl(courseEventRawValue.professor, {
        validators: [Validators.required],
      }),
      studentGroup: new FormControl(courseEventRawValue.studentGroup, {
        validators: [Validators.required],
      }),
    });
  }

  getCourseEvent(form: CourseEventFormGroup): ICourseEvent | NewCourseEvent {
    return form.getRawValue();
  }

  resetForm(form: CourseEventFormGroup, courseEvent: CourseEventFormGroupInput): void {
    const courseEventRawValue = { ...this.getFormDefaults(), ...courseEvent };
    form.reset({
      ...courseEventRawValue,
      id: { value: courseEventRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CourseEventFormDefaults {
    return {
      id: null,
    };
  }
}
