import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfessor, NewProfessor } from '../professor.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfessor for edit and NewProfessorFormGroupInput for create.
 */
type ProfessorFormGroupInput = IProfessor | PartialWithRequiredKeyOf<NewProfessor>;

type ProfessorFormDefaults = Pick<NewProfessor, 'id'>;

type ProfessorFormGroupContent = {
  id: FormControl<IProfessor['id'] | NewProfessor['id']>;
  firstName: FormControl<IProfessor['firstName']>;
  lastName: FormControl<IProfessor['lastName']>;
  email: FormControl<IProfessor['email']>;
  title: FormControl<IProfessor['title']>;
  department: FormControl<IProfessor['department']>;
};

export type ProfessorFormGroup = FormGroup<ProfessorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfessorFormService {
  createProfessorFormGroup(professor?: ProfessorFormGroupInput): ProfessorFormGroup {
    const professorRawValue = {
      ...this.getFormDefaults(),
      ...(professor ?? { id: null }),
    };
    return new FormGroup<ProfessorFormGroupContent>({
      id: new FormControl(
        { value: professorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      firstName: new FormControl(professorRawValue.firstName, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      lastName: new FormControl(professorRawValue.lastName, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      email: new FormControl(professorRawValue.email, {
        validators: [Validators.required, Validators.maxLength(160)],
      }),
      title: new FormControl(professorRawValue.title, {
        validators: [Validators.maxLength(80)],
      }),
      department: new FormControl(professorRawValue.department, {
        validators: [Validators.required],
      }),
    });
  }

  getProfessor(form: ProfessorFormGroup): IProfessor | NewProfessor {
    return form.getRawValue();
  }

  resetForm(form: ProfessorFormGroup, professor: ProfessorFormGroupInput): void {
    const professorRawValue = { ...this.getFormDefaults(), ...professor };
    form.reset({
      ...professorRawValue,
      id: { value: professorRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProfessorFormDefaults {
    return {
      id: null,
    };
  }
}
