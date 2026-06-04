import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IFaculty, NewFaculty } from '../faculty.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFaculty for edit and NewFacultyFormGroupInput for create.
 */
type FacultyFormGroupInput = IFaculty | PartialWithRequiredKeyOf<NewFaculty>;

type FacultyFormDefaults = Pick<NewFaculty, 'id'>;

type FacultyFormGroupContent = {
  id: FormControl<IFaculty['id'] | NewFaculty['id']>;
  name: FormControl<IFaculty['name']>;
  code: FormControl<IFaculty['code']>;
};

export type FacultyFormGroup = FormGroup<FacultyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FacultyFormService {
  createFacultyFormGroup(faculty?: FacultyFormGroupInput): FacultyFormGroup {
    const facultyRawValue = {
      ...this.getFormDefaults(),
      ...(faculty ?? { id: null }),
    };
    return new FormGroup<FacultyFormGroupContent>({
      id: new FormControl(
        { value: facultyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(facultyRawValue.name, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      code: new FormControl(facultyRawValue.code, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
    });
  }

  getFaculty(form: FacultyFormGroup): IFaculty | NewFaculty {
    return form.getRawValue();
  }

  resetForm(form: FacultyFormGroup, faculty: FacultyFormGroupInput): void {
    const facultyRawValue = { ...this.getFormDefaults(), ...faculty };
    form.reset({
      ...facultyRawValue,
      id: { value: facultyRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): FacultyFormDefaults {
    return {
      id: null,
    };
  }
}
