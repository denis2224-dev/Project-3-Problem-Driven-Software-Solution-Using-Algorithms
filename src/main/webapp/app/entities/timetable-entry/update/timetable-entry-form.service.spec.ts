import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../timetable-entry.test-samples';

import { TimetableEntryFormService } from './timetable-entry-form.service';

describe('TimetableEntry Form Service', () => {
  let service: TimetableEntryFormService;

  beforeEach(() => {
    service = TestBed.inject(TimetableEntryFormService);
  });

  describe('Service methods', () => {
    describe('createTimetableEntryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTimetableEntryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            timetableVersion: expect.any(Object),
            courseEvent: expect.any(Object),
            room: expect.any(Object),
            timeslot: expect.any(Object),
          }),
        );
      });

      it('passing ITimetableEntry should create a new form with FormGroup', () => {
        const formGroup = service.createTimetableEntryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            timetableVersion: expect.any(Object),
            courseEvent: expect.any(Object),
            room: expect.any(Object),
            timeslot: expect.any(Object),
          }),
        );
      });
    });

    describe('getTimetableEntry', () => {
      it('should return NewTimetableEntry for default TimetableEntry initial value', () => {
        const formGroup = service.createTimetableEntryFormGroup(sampleWithNewData);

        const timetableEntry = service.getTimetableEntry(formGroup);

        expect(timetableEntry).toMatchObject(sampleWithNewData);
      });

      it('should return NewTimetableEntry for empty TimetableEntry initial value', () => {
        const formGroup = service.createTimetableEntryFormGroup();

        const timetableEntry = service.getTimetableEntry(formGroup);

        expect(timetableEntry).toMatchObject({});
      });

      it('should return ITimetableEntry', () => {
        const formGroup = service.createTimetableEntryFormGroup(sampleWithRequiredData);

        const timetableEntry = service.getTimetableEntry(formGroup);

        expect(timetableEntry).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITimetableEntry should not enable id FormControl', () => {
        const formGroup = service.createTimetableEntryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTimetableEntry should disable id FormControl', () => {
        const formGroup = service.createTimetableEntryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
