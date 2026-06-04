import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBuilding, NewBuilding } from '../building.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBuilding for edit and NewBuildingFormGroupInput for create.
 */
type BuildingFormGroupInput = IBuilding | PartialWithRequiredKeyOf<NewBuilding>;

type BuildingFormDefaults = Pick<NewBuilding, 'id'>;

type BuildingFormGroupContent = {
  id: FormControl<IBuilding['id'] | NewBuilding['id']>;
  name: FormControl<IBuilding['name']>;
  code: FormControl<IBuilding['code']>;
  address: FormControl<IBuilding['address']>;
};

export type BuildingFormGroup = FormGroup<BuildingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BuildingFormService {
  createBuildingFormGroup(building?: BuildingFormGroupInput): BuildingFormGroup {
    const buildingRawValue = {
      ...this.getFormDefaults(),
      ...(building ?? { id: null }),
    };
    return new FormGroup<BuildingFormGroupContent>({
      id: new FormControl(
        { value: buildingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(buildingRawValue.name, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      code: new FormControl(buildingRawValue.code, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      address: new FormControl(buildingRawValue.address, {
        validators: [Validators.maxLength(255)],
      }),
    });
  }

  getBuilding(form: BuildingFormGroup): IBuilding | NewBuilding {
    return form.getRawValue();
  }

  resetForm(form: BuildingFormGroup, building: BuildingFormGroupInput): void {
    const buildingRawValue = { ...this.getFormDefaults(), ...building };
    form.reset({
      ...buildingRawValue,
      id: { value: buildingRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BuildingFormDefaults {
    return {
      id: null,
    };
  }
}
