import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IStudentGroup, NewStudentGroup } from '../student-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStudentGroup for edit and NewStudentGroupFormGroupInput for create.
 */
type StudentGroupFormGroupInput = IStudentGroup | PartialWithRequiredKeyOf<NewStudentGroup>;

type StudentGroupFormDefaults = Pick<NewStudentGroup, 'id'>;

type StudentGroupFormGroupContent = {
  id: FormControl<IStudentGroup['id'] | NewStudentGroup['id']>;
  name: FormControl<IStudentGroup['name']>;
  year: FormControl<IStudentGroup['year']>;
  groupSize: FormControl<IStudentGroup['groupSize']>;
  department: FormControl<IStudentGroup['department']>;
};

export type StudentGroupFormGroup = FormGroup<StudentGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StudentGroupFormService {
  createStudentGroupFormGroup(studentGroup?: StudentGroupFormGroupInput): StudentGroupFormGroup {
    const studentGroupRawValue = {
      ...this.getFormDefaults(),
      ...(studentGroup ?? { id: null }),
    };
    return new FormGroup<StudentGroupFormGroupContent>({
      id: new FormControl(
        { value: studentGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(studentGroupRawValue.name, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      year: new FormControl(studentGroupRawValue.year, {
        validators: [Validators.required, Validators.min(1), Validators.max(6)],
      }),
      groupSize: new FormControl(studentGroupRawValue.groupSize, {
        validators: [Validators.required, Validators.min(1)],
      }),
      department: new FormControl(studentGroupRawValue.department, {
        validators: [Validators.required],
      }),
    });
  }

  getStudentGroup(form: StudentGroupFormGroup): IStudentGroup | NewStudentGroup {
    return form.getRawValue();
  }

  resetForm(form: StudentGroupFormGroup, studentGroup: StudentGroupFormGroupInput): void {
    const studentGroupRawValue = { ...this.getFormDefaults(), ...studentGroup };
    form.reset({
      ...studentGroupRawValue,
      id: { value: studentGroupRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): StudentGroupFormDefaults {
    return {
      id: null,
    };
  }
}
