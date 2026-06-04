import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICourseEvent, NewCourseEvent } from '../course-event.model';

export type PartialUpdateCourseEvent = Partial<ICourseEvent> & Pick<ICourseEvent, 'id'>;

@Injectable()
export class CourseEventsService {
  readonly courseEventsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly courseEventsResource = httpResource<ICourseEvent[]>(() => {
    const params = this.courseEventsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of courseEvent that have been fetched. It is updated when the courseEventsResource emits a new value.
   * In case of error while fetching the courseEvents, the signal is set to an empty array.
   */
  readonly courseEvents = computed(() => (this.courseEventsResource.hasValue() ? this.courseEventsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/course-events');
}

@Injectable({ providedIn: 'root' })
export class CourseEventService extends CourseEventsService {
  protected readonly http = inject(HttpClient);

  create(courseEvent: NewCourseEvent): Observable<ICourseEvent> {
    return this.http.post<ICourseEvent>(this.resourceUrl, courseEvent);
  }

  update(courseEvent: ICourseEvent): Observable<ICourseEvent> {
    return this.http.put<ICourseEvent>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCourseEventIdentifier(courseEvent))}`,
      courseEvent,
    );
  }

  partialUpdate(courseEvent: PartialUpdateCourseEvent): Observable<ICourseEvent> {
    return this.http.patch<ICourseEvent>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCourseEventIdentifier(courseEvent))}`,
      courseEvent,
    );
  }

  find(id: number): Observable<ICourseEvent> {
    return this.http.get<ICourseEvent>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICourseEvent[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICourseEvent[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCourseEventIdentifier(courseEvent: Pick<ICourseEvent, 'id'>): number {
    return courseEvent.id;
  }

  compareCourseEvent(o1: Pick<ICourseEvent, 'id'> | null, o2: Pick<ICourseEvent, 'id'> | null): boolean {
    return o1 && o2 ? this.getCourseEventIdentifier(o1) === this.getCourseEventIdentifier(o2) : o1 === o2;
  }

  addCourseEventToCollectionIfMissing<Type extends Pick<ICourseEvent, 'id'>>(
    courseEventCollection: Type[],
    ...courseEventsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const courseEvents: Type[] = courseEventsToCheck.filter(isPresent);
    if (courseEvents.length > 0) {
      const courseEventCollectionIdentifiers = courseEventCollection.map(courseEventItem => this.getCourseEventIdentifier(courseEventItem));
      const courseEventsToAdd = courseEvents.filter(courseEventItem => {
        const courseEventIdentifier = this.getCourseEventIdentifier(courseEventItem);
        if (courseEventCollectionIdentifiers.includes(courseEventIdentifier)) {
          return false;
        }
        courseEventCollectionIdentifiers.push(courseEventIdentifier);
        return true;
      });
      return [...courseEventsToAdd, ...courseEventCollection];
    }
    return courseEventCollection;
  }
}
