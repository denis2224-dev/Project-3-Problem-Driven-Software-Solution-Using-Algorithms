import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRoom, NewRoom } from '../room.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRoom for edit and NewRoomFormGroupInput for create.
 */
type RoomFormGroupInput = IRoom | PartialWithRequiredKeyOf<NewRoom>;

type RoomFormDefaults = Pick<NewRoom, 'id'>;

type RoomFormGroupContent = {
  id: FormControl<IRoom['id'] | NewRoom['id']>;
  name: FormControl<IRoom['name']>;
  code: FormControl<IRoom['code']>;
  capacity: FormControl<IRoom['capacity']>;
  roomType: FormControl<IRoom['roomType']>;
  equipment: FormControl<IRoom['equipment']>;
  building: FormControl<IRoom['building']>;
};

export type RoomFormGroup = FormGroup<RoomFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RoomFormService {
  createRoomFormGroup(room?: RoomFormGroupInput): RoomFormGroup {
    const roomRawValue = {
      ...this.getFormDefaults(),
      ...(room ?? { id: null }),
    };
    return new FormGroup<RoomFormGroupContent>({
      id: new FormControl(
        { value: roomRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(roomRawValue.name, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      code: new FormControl(roomRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      capacity: new FormControl(roomRawValue.capacity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      roomType: new FormControl(roomRawValue.roomType, {
        validators: [Validators.required],
      }),
      equipment: new FormControl(roomRawValue.equipment, {
        validators: [Validators.maxLength(1000)],
      }),
      building: new FormControl(roomRawValue.building, {
        validators: [Validators.required],
      }),
    });
  }

  getRoom(form: RoomFormGroup): IRoom | NewRoom {
    return form.getRawValue();
  }

  resetForm(form: RoomFormGroup, room: RoomFormGroupInput): void {
    const roomRawValue = { ...this.getFormDefaults(), ...room };
    form.reset({
      ...roomRawValue,
      id: { value: roomRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RoomFormDefaults {
    return {
      id: null,
    };
  }
}
