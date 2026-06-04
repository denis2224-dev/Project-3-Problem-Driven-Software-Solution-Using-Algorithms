import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../schedule-conflict.test-samples';

import { ScheduleConflictFormService } from './schedule-conflict-form.service';

describe('ScheduleConflict Form Service', () => {
  let service: ScheduleConflictFormService;

  beforeEach(() => {
    service = TestBed.inject(ScheduleConflictFormService);
  });

  describe('Service methods', () => {
    describe('createScheduleConflictFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createScheduleConflictFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            conflictType: expect.any(Object),
            description: expect.any(Object),
            severity: expect.any(Object),
            timetableVersion: expect.any(Object),
          }),
        );
      });

      it('passing IScheduleConflict should create a new form with FormGroup', () => {
        const formGroup = service.createScheduleConflictFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            conflictType: expect.any(Object),
            description: expect.any(Object),
            severity: expect.any(Object),
            timetableVersion: expect.any(Object),
          }),
        );
      });
    });

    describe('getScheduleConflict', () => {
      it('should return NewScheduleConflict for default ScheduleConflict initial value', () => {
        const formGroup = service.createScheduleConflictFormGroup(sampleWithNewData);

        const scheduleConflict = service.getScheduleConflict(formGroup);

        expect(scheduleConflict).toMatchObject(sampleWithNewData);
      });

      it('should return NewScheduleConflict for empty ScheduleConflict initial value', () => {
        const formGroup = service.createScheduleConflictFormGroup();

        const scheduleConflict = service.getScheduleConflict(formGroup);

        expect(scheduleConflict).toMatchObject({});
      });

      it('should return IScheduleConflict', () => {
        const formGroup = service.createScheduleConflictFormGroup(sampleWithRequiredData);

        const scheduleConflict = service.getScheduleConflict(formGroup);

        expect(scheduleConflict).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IScheduleConflict should not enable id FormControl', () => {
        const formGroup = service.createScheduleConflictFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewScheduleConflict should disable id FormControl', () => {
        const formGroup = service.createScheduleConflictFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
