import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../timeslot.test-samples';

import { TimeslotFormService } from './timeslot-form.service';

describe('Timeslot Form Service', () => {
  let service: TimeslotFormService;

  beforeEach(() => {
    service = TestBed.inject(TimeslotFormService);
  });

  describe('Service methods', () => {
    describe('createTimeslotFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTimeslotFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dayOfWeek: expect.any(Object),
            startTime: expect.any(Object),
            endTime: expect.any(Object),
          }),
        );
      });

      it('passing ITimeslot should create a new form with FormGroup', () => {
        const formGroup = service.createTimeslotFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dayOfWeek: expect.any(Object),
            startTime: expect.any(Object),
            endTime: expect.any(Object),
          }),
        );
      });
    });

    describe('getTimeslot', () => {
      it('should return NewTimeslot for default Timeslot initial value', () => {
        const formGroup = service.createTimeslotFormGroup(sampleWithNewData);

        const timeslot = service.getTimeslot(formGroup);

        expect(timeslot).toMatchObject(sampleWithNewData);
      });

      it('should return NewTimeslot for empty Timeslot initial value', () => {
        const formGroup = service.createTimeslotFormGroup();

        const timeslot = service.getTimeslot(formGroup);

        expect(timeslot).toMatchObject({});
      });

      it('should return ITimeslot', () => {
        const formGroup = service.createTimeslotFormGroup(sampleWithRequiredData);

        const timeslot = service.getTimeslot(formGroup);

        expect(timeslot).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITimeslot should not enable id FormControl', () => {
        const formGroup = service.createTimeslotFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTimeslot should disable id FormControl', () => {
        const formGroup = service.createTimeslotFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
