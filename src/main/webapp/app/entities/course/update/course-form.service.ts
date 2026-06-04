import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICourse, NewCourse } from '../course.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICourse for edit and NewCourseFormGroupInput for create.
 */
type CourseFormGroupInput = ICourse | PartialWithRequiredKeyOf<NewCourse>;

type CourseFormDefaults = Pick<NewCourse, 'id'>;

type CourseFormGroupContent = {
  id: FormControl<ICourse['id'] | NewCourse['id']>;
  code: FormControl<ICourse['code']>;
  name: FormControl<ICourse['name']>;
  credits: FormControl<ICourse['credits']>;
  department: FormControl<ICourse['department']>;
};

export type CourseFormGroup = FormGroup<CourseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CourseFormService {
  createCourseFormGroup(course?: CourseFormGroupInput): CourseFormGroup {
    const courseRawValue = {
      ...this.getFormDefaults(),
      ...(course ?? { id: null }),
    };
    return new FormGroup<CourseFormGroupContent>({
      id: new FormControl(
        { value: courseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(courseRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      name: new FormControl(courseRawValue.name, {
        validators: [Validators.required, Validators.maxLength(160)],
      }),
      credits: new FormControl(courseRawValue.credits, {
        validators: [Validators.required, Validators.min(1), Validators.max(30)],
      }),
      department: new FormControl(courseRawValue.department, {
        validators: [Validators.required],
      }),
    });
  }

  getCourse(form: CourseFormGroup): ICourse | NewCourse {
    return form.getRawValue();
  }

  resetForm(form: CourseFormGroup, course: CourseFormGroupInput): void {
    const courseRawValue = { ...this.getFormDefaults(), ...course };
    form.reset({
      ...courseRawValue,
      id: { value: courseRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CourseFormDefaults {
    return {
      id: null,
    };
  }
}
