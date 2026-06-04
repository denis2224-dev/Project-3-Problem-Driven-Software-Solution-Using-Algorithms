import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../course-event.test-samples';

import { CourseEventFormService } from './course-event-form.service';

describe('CourseEvent Form Service', () => {
  let service: CourseEventFormService;

  beforeEach(() => {
    service = TestBed.inject(CourseEventFormService);
  });

  describe('Service methods', () => {
    describe('createCourseEventFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCourseEventFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            eventType: expect.any(Object),
            durationMinutes: expect.any(Object),
            expectedStudents: expect.any(Object),
            requiredEquipment: expect.any(Object),
            course: expect.any(Object),
            professor: expect.any(Object),
            studentGroup: expect.any(Object),
          }),
        );
      });

      it('passing ICourseEvent should create a new form with FormGroup', () => {
        const formGroup = service.createCourseEventFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            eventType: expect.any(Object),
            durationMinutes: expect.any(Object),
            expectedStudents: expect.any(Object),
            requiredEquipment: expect.any(Object),
            course: expect.any(Object),
            professor: expect.any(Object),
            studentGroup: expect.any(Object),
          }),
        );
      });
    });

    describe('getCourseEvent', () => {
      it('should return NewCourseEvent for default CourseEvent initial value', () => {
        const formGroup = service.createCourseEventFormGroup(sampleWithNewData);

        const courseEvent = service.getCourseEvent(formGroup);

        expect(courseEvent).toMatchObject(sampleWithNewData);
      });

      it('should return NewCourseEvent for empty CourseEvent initial value', () => {
        const formGroup = service.createCourseEventFormGroup();

        const courseEvent = service.getCourseEvent(formGroup);

        expect(courseEvent).toMatchObject({});
      });

      it('should return ICourseEvent', () => {
        const formGroup = service.createCourseEventFormGroup(sampleWithRequiredData);

        const courseEvent = service.getCourseEvent(formGroup);

        expect(courseEvent).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICourseEvent should not enable id FormControl', () => {
        const formGroup = service.createCourseEventFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCourseEvent should disable id FormControl', () => {
        const formGroup = service.createCourseEventFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
