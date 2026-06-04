import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfessorPreference, NewProfessorPreference } from '../professor-preference.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfessorPreference for edit and NewProfessorPreferenceFormGroupInput for create.
 */
type ProfessorPreferenceFormGroupInput = IProfessorPreference | PartialWithRequiredKeyOf<NewProfessorPreference>;

type ProfessorPreferenceFormDefaults = Pick<NewProfessorPreference, 'id'>;

type ProfessorPreferenceFormGroupContent = {
  id: FormControl<IProfessorPreference['id'] | NewProfessorPreference['id']>;
  preferenceType: FormControl<IProfessorPreference['preferenceType']>;
  professor: FormControl<IProfessorPreference['professor']>;
  timeslot: FormControl<IProfessorPreference['timeslot']>;
};

export type ProfessorPreferenceFormGroup = FormGroup<ProfessorPreferenceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfessorPreferenceFormService {
  createProfessorPreferenceFormGroup(professorPreference?: ProfessorPreferenceFormGroupInput): ProfessorPreferenceFormGroup {
    const professorPreferenceRawValue = {
      ...this.getFormDefaults(),
      ...(professorPreference ?? { id: null }),
    };
    return new FormGroup<ProfessorPreferenceFormGroupContent>({
      id: new FormControl(
        { value: professorPreferenceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      preferenceType: new FormControl(professorPreferenceRawValue.preferenceType, {
        validators: [Validators.required],
      }),
      professor: new FormControl(professorPreferenceRawValue.professor, {
        validators: [Validators.required],
      }),
      timeslot: new FormControl(professorPreferenceRawValue.timeslot, {
        validators: [Validators.required],
      }),
    });
  }

  getProfessorPreference(form: ProfessorPreferenceFormGroup): IProfessorPreference | NewProfessorPreference {
    return form.getRawValue();
  }

  resetForm(form: ProfessorPreferenceFormGroup, professorPreference: ProfessorPreferenceFormGroupInput): void {
    const professorPreferenceRawValue = { ...this.getFormDefaults(), ...professorPreference };
    form.reset({
      ...professorPreferenceRawValue,
      id: { value: professorPreferenceRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProfessorPreferenceFormDefaults {
    return {
      id: null,
    };
  }
}
