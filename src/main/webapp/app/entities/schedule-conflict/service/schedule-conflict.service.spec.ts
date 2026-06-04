import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IScheduleConflict } from '../schedule-conflict.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../schedule-conflict.test-samples';

import { ScheduleConflictService } from './schedule-conflict.service';

const requireRestSample: IScheduleConflict = {
  ...sampleWithRequiredData,
};

describe('ScheduleConflict Service', () => {
  let service: ScheduleConflictService;
  let httpMock: HttpTestingController;
  let expectedResult: IScheduleConflict | IScheduleConflict[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ScheduleConflictService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ScheduleConflict', () => {
      const scheduleConflict = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(scheduleConflict).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ScheduleConflict', () => {
      const scheduleConflict = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(scheduleConflict).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ScheduleConflict', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ScheduleConflict', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ScheduleConflict', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addScheduleConflictToCollectionIfMissing', () => {
      it('should add a ScheduleConflict to an empty array', () => {
        const scheduleConflict: IScheduleConflict = sampleWithRequiredData;
        expectedResult = service.addScheduleConflictToCollectionIfMissing([], scheduleConflict);
        expect(expectedResult).toEqual([scheduleConflict]);
      });

      it('should not add a ScheduleConflict to an array that contains it', () => {
        const scheduleConflict: IScheduleConflict = sampleWithRequiredData;
        const scheduleConflictCollection: IScheduleConflict[] = [
          {
            ...scheduleConflict,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScheduleConflictToCollectionIfMissing(scheduleConflictCollection, scheduleConflict);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ScheduleConflict to an array that doesn't contain it", () => {
        const scheduleConflict: IScheduleConflict = sampleWithRequiredData;
        const scheduleConflictCollection: IScheduleConflict[] = [sampleWithPartialData];
        expectedResult = service.addScheduleConflictToCollectionIfMissing(scheduleConflictCollection, scheduleConflict);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scheduleConflict);
      });

      it('should add only unique ScheduleConflict to an array', () => {
        const scheduleConflictArray: IScheduleConflict[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scheduleConflictCollection: IScheduleConflict[] = [sampleWithRequiredData];
        expectedResult = service.addScheduleConflictToCollectionIfMissing(scheduleConflictCollection, ...scheduleConflictArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scheduleConflict: IScheduleConflict = sampleWithRequiredData;
        const scheduleConflict2: IScheduleConflict = sampleWithPartialData;
        expectedResult = service.addScheduleConflictToCollectionIfMissing([], scheduleConflict, scheduleConflict2);
        expect(expectedResult).toEqual([scheduleConflict, scheduleConflict2]);
      });

      it('should accept null and undefined values', () => {
        const scheduleConflict: IScheduleConflict = sampleWithRequiredData;
        expectedResult = service.addScheduleConflictToCollectionIfMissing([], null, scheduleConflict, undefined);
        expect(expectedResult).toEqual([scheduleConflict]);
      });

      it('should return initial array if no ScheduleConflict is added', () => {
        const scheduleConflictCollection: IScheduleConflict[] = [sampleWithRequiredData];
        expectedResult = service.addScheduleConflictToCollectionIfMissing(scheduleConflictCollection, undefined, null);
        expect(expectedResult).toEqual(scheduleConflictCollection);
      });
    });

    describe('compareScheduleConflict', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScheduleConflict(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 30168 };
        const entity2 = null;

        const compareResult1 = service.compareScheduleConflict(entity1, entity2);
        const compareResult2 = service.compareScheduleConflict(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 30168 };
        const entity2 = { id: 4995 };

        const compareResult1 = service.compareScheduleConflict(entity1, entity2);
        const compareResult2 = service.compareScheduleConflict(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 30168 };
        const entity2 = { id: 30168 };

        const compareResult1 = service.compareScheduleConflict(entity1, entity2);
        const compareResult2 = service.compareScheduleConflict(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
