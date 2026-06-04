import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDepartment, NewDepartment } from '../department.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDepartment for edit and NewDepartmentFormGroupInput for create.
 */
type DepartmentFormGroupInput = IDepartment | PartialWithRequiredKeyOf<NewDepartment>;

type DepartmentFormDefaults = Pick<NewDepartment, 'id'>;

type DepartmentFormGroupContent = {
  id: FormControl<IDepartment['id'] | NewDepartment['id']>;
  name: FormControl<IDepartment['name']>;
  code: FormControl<IDepartment['code']>;
  faculty: FormControl<IDepartment['faculty']>;
};

export type DepartmentFormGroup = FormGroup<DepartmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DepartmentFormService {
  createDepartmentFormGroup(department?: DepartmentFormGroupInput): DepartmentFormGroup {
    const departmentRawValue = {
      ...this.getFormDefaults(),
      ...(department ?? { id: null }),
    };
    return new FormGroup<DepartmentFormGroupContent>({
      id: new FormControl(
        { value: departmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(departmentRawValue.name, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      code: new FormControl(departmentRawValue.code, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      faculty: new FormControl(departmentRawValue.faculty, {
        validators: [Validators.required],
      }),
    });
  }

  getDepartment(form: DepartmentFormGroup): IDepartment | NewDepartment {
    return form.getRawValue();
  }

  resetForm(form: DepartmentFormGroup, department: DepartmentFormGroupInput): void {
    const departmentRawValue = { ...this.getFormDefaults(), ...department };
    form.reset({
      ...departmentRawValue,
      id: { value: departmentRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DepartmentFormDefaults {
    return {
      id: null,
    };
  }
}
