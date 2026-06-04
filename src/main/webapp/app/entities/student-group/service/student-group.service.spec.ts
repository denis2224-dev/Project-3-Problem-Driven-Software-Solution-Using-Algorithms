import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IStudentGroup } from '../student-group.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../student-group.test-samples';

import { StudentGroupService } from './student-group.service';

const requireRestSample: IStudentGroup = {
  ...sampleWithRequiredData,
};

describe('StudentGroup Service', () => {
  let service: StudentGroupService;
  let httpMock: HttpTestingController;
  let expectedResult: IStudentGroup | IStudentGroup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StudentGroupService);
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

    it('should create a StudentGroup', () => {
      const studentGroup = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(studentGroup).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StudentGroup', () => {
      const studentGroup = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(studentGroup).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StudentGroup', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StudentGroup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StudentGroup', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addStudentGroupToCollectionIfMissing', () => {
      it('should add a StudentGroup to an empty array', () => {
        const studentGroup: IStudentGroup = sampleWithRequiredData;
        expectedResult = service.addStudentGroupToCollectionIfMissing([], studentGroup);
        expect(expectedResult).toEqual([studentGroup]);
      });

      it('should not add a StudentGroup to an array that contains it', () => {
        const studentGroup: IStudentGroup = sampleWithRequiredData;
        const studentGroupCollection: IStudentGroup[] = [
          {
            ...studentGroup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStudentGroupToCollectionIfMissing(studentGroupCollection, studentGroup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StudentGroup to an array that doesn't contain it", () => {
        const studentGroup: IStudentGroup = sampleWithRequiredData;
        const studentGroupCollection: IStudentGroup[] = [sampleWithPartialData];
        expectedResult = service.addStudentGroupToCollectionIfMissing(studentGroupCollection, studentGroup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(studentGroup);
      });

      it('should add only unique StudentGroup to an array', () => {
        const studentGroupArray: IStudentGroup[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const studentGroupCollection: IStudentGroup[] = [sampleWithRequiredData];
        expectedResult = service.addStudentGroupToCollectionIfMissing(studentGroupCollection, ...studentGroupArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const studentGroup: IStudentGroup = sampleWithRequiredData;
        const studentGroup2: IStudentGroup = sampleWithPartialData;
        expectedResult = service.addStudentGroupToCollectionIfMissing([], studentGroup, studentGroup2);
        expect(expectedResult).toEqual([studentGroup, studentGroup2]);
      });

      it('should accept null and undefined values', () => {
        const studentGroup: IStudentGroup = sampleWithRequiredData;
        expectedResult = service.addStudentGroupToCollectionIfMissing([], null, studentGroup, undefined);
        expect(expectedResult).toEqual([studentGroup]);
      });

      it('should return initial array if no StudentGroup is added', () => {
        const studentGroupCollection: IStudentGroup[] = [sampleWithRequiredData];
        expectedResult = service.addStudentGroupToCollectionIfMissing(studentGroupCollection, undefined, null);
        expect(expectedResult).toEqual(studentGroupCollection);
      });
    });

    describe('compareStudentGroup', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStudentGroup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20706 };
        const entity2 = null;

        const compareResult1 = service.compareStudentGroup(entity1, entity2);
        const compareResult2 = service.compareStudentGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20706 };
        const entity2 = { id: 6236 };

        const compareResult1 = service.compareStudentGroup(entity1, entity2);
        const compareResult2 = service.compareStudentGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20706 };
        const entity2 = { id: 20706 };

        const compareResult1 = service.compareStudentGroup(entity1, entity2);
        const compareResult2 = service.compareStudentGroup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
