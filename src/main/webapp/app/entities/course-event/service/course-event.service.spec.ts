import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICourseEvent } from '../course-event.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../course-event.test-samples';

import { CourseEventService } from './course-event.service';

const requireRestSample: ICourseEvent = {
  ...sampleWithRequiredData,
};

describe('CourseEvent Service', () => {
  let service: CourseEventService;
  let httpMock: HttpTestingController;
  let expectedResult: ICourseEvent | ICourseEvent[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CourseEventService);
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

    it('should create a CourseEvent', () => {
      const courseEvent = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(courseEvent).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CourseEvent', () => {
      const courseEvent = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(courseEvent).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CourseEvent', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CourseEvent', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CourseEvent', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addCourseEventToCollectionIfMissing', () => {
      it('should add a CourseEvent to an empty array', () => {
        const courseEvent: ICourseEvent = sampleWithRequiredData;
        expectedResult = service.addCourseEventToCollectionIfMissing([], courseEvent);
        expect(expectedResult).toEqual([courseEvent]);
      });

      it('should not add a CourseEvent to an array that contains it', () => {
        const courseEvent: ICourseEvent = sampleWithRequiredData;
        const courseEventCollection: ICourseEvent[] = [
          {
            ...courseEvent,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCourseEventToCollectionIfMissing(courseEventCollection, courseEvent);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CourseEvent to an array that doesn't contain it", () => {
        const courseEvent: ICourseEvent = sampleWithRequiredData;
        const courseEventCollection: ICourseEvent[] = [sampleWithPartialData];
        expectedResult = service.addCourseEventToCollectionIfMissing(courseEventCollection, courseEvent);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(courseEvent);
      });

      it('should add only unique CourseEvent to an array', () => {
        const courseEventArray: ICourseEvent[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const courseEventCollection: ICourseEvent[] = [sampleWithRequiredData];
        expectedResult = service.addCourseEventToCollectionIfMissing(courseEventCollection, ...courseEventArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const courseEvent: ICourseEvent = sampleWithRequiredData;
        const courseEvent2: ICourseEvent = sampleWithPartialData;
        expectedResult = service.addCourseEventToCollectionIfMissing([], courseEvent, courseEvent2);
        expect(expectedResult).toEqual([courseEvent, courseEvent2]);
      });

      it('should accept null and undefined values', () => {
        const courseEvent: ICourseEvent = sampleWithRequiredData;
        expectedResult = service.addCourseEventToCollectionIfMissing([], null, courseEvent, undefined);
        expect(expectedResult).toEqual([courseEvent]);
      });

      it('should return initial array if no CourseEvent is added', () => {
        const courseEventCollection: ICourseEvent[] = [sampleWithRequiredData];
        expectedResult = service.addCourseEventToCollectionIfMissing(courseEventCollection, undefined, null);
        expect(expectedResult).toEqual(courseEventCollection);
      });
    });

    describe('compareCourseEvent', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCourseEvent(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9540 };
        const entity2 = null;

        const compareResult1 = service.compareCourseEvent(entity1, entity2);
        const compareResult2 = service.compareCourseEvent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9540 };
        const entity2 = { id: 726 };

        const compareResult1 = service.compareCourseEvent(entity1, entity2);
        const compareResult2 = service.compareCourseEvent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 9540 };
        const entity2 = { id: 9540 };

        const compareResult1 = service.compareCourseEvent(entity1, entity2);
        const compareResult2 = service.compareCourseEvent(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
