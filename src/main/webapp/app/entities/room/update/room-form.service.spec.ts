import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../room.test-samples';

import { RoomFormService } from './room-form.service';

describe('Room Form Service', () => {
  let service: RoomFormService;

  beforeEach(() => {
    service = TestBed.inject(RoomFormService);
  });

  describe('Service methods', () => {
    describe('createRoomFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRoomFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            capacity: expect.any(Object),
            roomType: expect.any(Object),
            equipment: expect.any(Object),
            building: expect.any(Object),
          }),
        );
      });

      it('passing IRoom should create a new form with FormGroup', () => {
        const formGroup = service.createRoomFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            code: expect.any(Object),
            capacity: expect.any(Object),
            roomType: expect.any(Object),
            equipment: expect.any(Object),
            building: expect.any(Object),
          }),
        );
      });
    });

    describe('getRoom', () => {
      it('should return NewRoom for default Room initial value', () => {
        const formGroup = service.createRoomFormGroup(sampleWithNewData);

        const room = service.getRoom(formGroup);

        expect(room).toMatchObject(sampleWithNewData);
      });

      it('should return NewRoom for empty Room initial value', () => {
        const formGroup = service.createRoomFormGroup();

        const room = service.getRoom(formGroup);

        expect(room).toMatchObject({});
      });

      it('should return IRoom', () => {
        const formGroup = service.createRoomFormGroup(sampleWithRequiredData);

        const room = service.getRoom(formGroup);

        expect(room).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRoom should not enable id FormControl', () => {
        const formGroup = service.createRoomFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRoom should disable id FormControl', () => {
        const formGroup = service.createRoomFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
