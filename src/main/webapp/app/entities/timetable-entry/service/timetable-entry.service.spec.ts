import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITimetableEntry } from '../timetable-entry.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../timetable-entry.test-samples';

import { TimetableEntryService } from './timetable-entry.service';

const requireRestSample: ITimetableEntry = {
  ...sampleWithRequiredData,
};

describe('TimetableEntry Service', () => {
  let service: TimetableEntryService;
  let httpMock: HttpTestingController;
  let expectedResult: ITimetableEntry | ITimetableEntry[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TimetableEntryService);
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

    it('should create a TimetableEntry', () => {
      const timetableEntry = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(timetableEntry).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TimetableEntry', () => {
      const timetableEntry = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(timetableEntry).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TimetableEntry', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TimetableEntry', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TimetableEntry', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addTimetableEntryToCollectionIfMissing', () => {
      it('should add a TimetableEntry to an empty array', () => {
        const timetableEntry: ITimetableEntry = sampleWithRequiredData;
        expectedResult = service.addTimetableEntryToCollectionIfMissing([], timetableEntry);
        expect(expectedResult).toEqual([timetableEntry]);
      });

      it('should not add a TimetableEntry to an array that contains it', () => {
        const timetableEntry: ITimetableEntry = sampleWithRequiredData;
        const timetableEntryCollection: ITimetableEntry[] = [
          {
            ...timetableEntry,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTimetableEntryToCollectionIfMissing(timetableEntryCollection, timetableEntry);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TimetableEntry to an array that doesn't contain it", () => {
        const timetableEntry: ITimetableEntry = sampleWithRequiredData;
        const timetableEntryCollection: ITimetableEntry[] = [sampleWithPartialData];
        expectedResult = service.addTimetableEntryToCollectionIfMissing(timetableEntryCollection, timetableEntry);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(timetableEntry);
      });

      it('should add only unique TimetableEntry to an array', () => {
        const timetableEntryArray: ITimetableEntry[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const timetableEntryCollection: ITimetableEntry[] = [sampleWithRequiredData];
        expectedResult = service.addTimetableEntryToCollectionIfMissing(timetableEntryCollection, ...timetableEntryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const timetableEntry: ITimetableEntry = sampleWithRequiredData;
        const timetableEntry2: ITimetableEntry = sampleWithPartialData;
        expectedResult = service.addTimetableEntryToCollectionIfMissing([], timetableEntry, timetableEntry2);
        expect(expectedResult).toEqual([timetableEntry, timetableEntry2]);
      });

      it('should accept null and undefined values', () => {
        const timetableEntry: ITimetableEntry = sampleWithRequiredData;
        expectedResult = service.addTimetableEntryToCollectionIfMissing([], null, timetableEntry, undefined);
        expect(expectedResult).toEqual([timetableEntry]);
      });

      it('should return initial array if no TimetableEntry is added', () => {
        const timetableEntryCollection: ITimetableEntry[] = [sampleWithRequiredData];
        expectedResult = service.addTimetableEntryToCollectionIfMissing(timetableEntryCollection, undefined, null);
        expect(expectedResult).toEqual(timetableEntryCollection);
      });
    });

    describe('compareTimetableEntry', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTimetableEntry(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15795 };
        const entity2 = null;

        const compareResult1 = service.compareTimetableEntry(entity1, entity2);
        const compareResult2 = service.compareTimetableEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15795 };
        const entity2 = { id: 12100 };

        const compareResult1 = service.compareTimetableEntry(entity1, entity2);
        const compareResult2 = service.compareTimetableEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15795 };
        const entity2 = { id: 15795 };

        const compareResult1 = service.compareTimetableEntry(entity1, entity2);
        const compareResult2 = service.compareTimetableEntry(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
