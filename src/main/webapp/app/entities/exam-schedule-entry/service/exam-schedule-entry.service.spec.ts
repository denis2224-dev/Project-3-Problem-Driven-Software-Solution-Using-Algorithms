import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IExamScheduleEntry } from '../exam-schedule-entry.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../exam-schedule-entry.test-samples';

import { ExamScheduleEntryService } from './exam-schedule-entry.service';

const requireRestSample: IExamScheduleEntry = {
  ...sampleWithRequiredData,
};

describe('ExamScheduleEntry Service', () => {
  let service: ExamScheduleEntryService;
  let httpMock: HttpTestingController;
  let expectedResult: IExamScheduleEntry | IExamScheduleEntry[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ExamScheduleEntryService);
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

    it('should create a ExamScheduleEntry', () => {
      const examScheduleEntry = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(examScheduleEntry).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ExamScheduleEntry', () => {
      const examScheduleEntry = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(examScheduleEntry).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ExamScheduleEntry', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ExamScheduleEntry', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ExamScheduleEntry', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addExamScheduleEntryToCollectionIfMissing', () => {
      it('should add a ExamScheduleEntry to an empty array', () => {
        const examScheduleEntry: IExamScheduleEntry = sampleWithRequiredData;
        expectedResult = service.addExamScheduleEntryToCollectionIfMissing([], examScheduleEntry);
        expect(expectedResult).toEqual([examScheduleEntry]);
      });

      it('should not add a ExamScheduleEntry to an array that contains it', () => {
        const examScheduleEntry: IExamScheduleEntry = sampleWithRequiredData;
        const examScheduleEntryCollection: IExamScheduleEntry[] = [
          {
            ...examScheduleEntry,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addExamScheduleEntryToCollectionIfMissing(examScheduleEntryCollection, examScheduleEntry);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ExamScheduleEntry to an array that doesn't contain it", () => {
        const examScheduleEntry: IExamScheduleEntry = sampleWithRequiredData;
        const examScheduleEntryCollection: IExamScheduleEntry[] = [sampleWithPartialData];
        expectedResult = service.addExamScheduleEntryToCollectionIfMissing(examScheduleEntryCollection, examScheduleEntry);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(examScheduleEntry);
      });

      it('should add only unique ExamScheduleEntry to an array', () => {
        const examScheduleEntryArray: IExamScheduleEntry[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const examScheduleEntryCollection: IExamScheduleEntry[] = [sampleWithRequiredData];
        expectedResult = service.addExamScheduleEntryToCollectionIfMissing(examScheduleEntryCollection, ...examScheduleEntryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const examScheduleEntry: IExamScheduleEntry = sampleWithRequiredData;
        const examScheduleEntry2: IExamScheduleEntry = sampleWithPartialData;
        expectedResult = service.addExamScheduleEntryToCollectionIfMissing([], examScheduleEntry, examScheduleEntry2);
        expect(expectedResult).toEqual([examScheduleEntry, examScheduleEntry2]);
      });

      it('should accept null and undefined values', () => {
        const examScheduleEntry: IExamScheduleEntry = sampleWithRequiredData;
        expectedResult = service.addExamScheduleEntryToCollectionIfMissing([], null, examScheduleEntry, undefined);
        expect(expectedResult).toEqual([examScheduleEntry]);
      });

      it('should return initial array if no ExamScheduleEntry is added', () => {
        const examScheduleEntryCollection: IExamScheduleEntry[] = [sampleWithRequiredData];
        expectedResult = service.addExamScheduleEntryToCollectionIfMissing(examScheduleEntryCollection, undefined, null);
        expect(expectedResult).toEqual(examScheduleEntryCollection);
      });
    });

    describe('compareExamScheduleEntry', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareExamScheduleEntry(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 619 };
        const entity2 = null;

        const compareResult1 = service.compareExamScheduleEntry(entity1, entity2);
        const compareResult2 = service.compareExamScheduleEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 619 };
        const entity2 = { id: 25016 };

        const compareResult1 = service.compareExamScheduleEntry(entity1, entity2);
        const compareResult2 = service.compareExamScheduleEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 619 };
        const entity2 = { id: 619 };

        const compareResult1 = service.compareExamScheduleEntry(entity1, entity2);
        const compareResult2 = service.compareExamScheduleEntry(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
