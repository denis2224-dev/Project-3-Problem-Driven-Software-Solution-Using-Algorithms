import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IExam } from '../exam.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../exam.test-samples';

import { ExamService } from './exam.service';

const requireRestSample: IExam = {
  ...sampleWithRequiredData,
};

describe('Exam Service', () => {
  let service: ExamService;
  let httpMock: HttpTestingController;
  let expectedResult: IExam | IExam[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ExamService);
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

    it('should create a Exam', () => {
      const exam = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(exam).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Exam', () => {
      const exam = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(exam).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Exam', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Exam', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Exam', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addExamToCollectionIfMissing', () => {
      it('should add a Exam to an empty array', () => {
        const exam: IExam = sampleWithRequiredData;
        expectedResult = service.addExamToCollectionIfMissing([], exam);
        expect(expectedResult).toEqual([exam]);
      });

      it('should not add a Exam to an array that contains it', () => {
        const exam: IExam = sampleWithRequiredData;
        const examCollection: IExam[] = [
          {
            ...exam,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addExamToCollectionIfMissing(examCollection, exam);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Exam to an array that doesn't contain it", () => {
        const exam: IExam = sampleWithRequiredData;
        const examCollection: IExam[] = [sampleWithPartialData];
        expectedResult = service.addExamToCollectionIfMissing(examCollection, exam);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(exam);
      });

      it('should add only unique Exam to an array', () => {
        const examArray: IExam[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const examCollection: IExam[] = [sampleWithRequiredData];
        expectedResult = service.addExamToCollectionIfMissing(examCollection, ...examArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const exam: IExam = sampleWithRequiredData;
        const exam2: IExam = sampleWithPartialData;
        expectedResult = service.addExamToCollectionIfMissing([], exam, exam2);
        expect(expectedResult).toEqual([exam, exam2]);
      });

      it('should accept null and undefined values', () => {
        const exam: IExam = sampleWithRequiredData;
        expectedResult = service.addExamToCollectionIfMissing([], null, exam, undefined);
        expect(expectedResult).toEqual([exam]);
      });

      it('should return initial array if no Exam is added', () => {
        const examCollection: IExam[] = [sampleWithRequiredData];
        expectedResult = service.addExamToCollectionIfMissing(examCollection, undefined, null);
        expect(expectedResult).toEqual(examCollection);
      });
    });

    describe('compareExam', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareExam(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15727 };
        const entity2 = null;

        const compareResult1 = service.compareExam(entity1, entity2);
        const compareResult2 = service.compareExam(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15727 };
        const entity2 = { id: 13366 };

        const compareResult1 = service.compareExam(entity1, entity2);
        const compareResult2 = service.compareExam(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15727 };
        const entity2 = { id: 15727 };

        const compareResult1 = service.compareExam(entity1, entity2);
        const compareResult2 = service.compareExam(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
