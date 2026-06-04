import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IScheduleConflict, NewScheduleConflict } from '../schedule-conflict.model';

export type PartialUpdateScheduleConflict = Partial<IScheduleConflict> & Pick<IScheduleConflict, 'id'>;

@Injectable()
export class ScheduleConflictsService {
  readonly scheduleConflictsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly scheduleConflictsResource = httpResource<IScheduleConflict[]>(() => {
    const params = this.scheduleConflictsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of scheduleConflict that have been fetched. It is updated when the scheduleConflictsResource emits a new value.
   * In case of error while fetching the scheduleConflicts, the signal is set to an empty array.
   */
  readonly scheduleConflicts = computed(() => (this.scheduleConflictsResource.hasValue() ? this.scheduleConflictsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/schedule-conflicts');
}

@Injectable({ providedIn: 'root' })
export class ScheduleConflictService extends ScheduleConflictsService {
  protected readonly http = inject(HttpClient);

  create(scheduleConflict: NewScheduleConflict): Observable<IScheduleConflict> {
    return this.http.post<IScheduleConflict>(this.resourceUrl, scheduleConflict);
  }

  update(scheduleConflict: IScheduleConflict): Observable<IScheduleConflict> {
    return this.http.put<IScheduleConflict>(
      `${this.resourceUrl}/${encodeURIComponent(this.getScheduleConflictIdentifier(scheduleConflict))}`,
      scheduleConflict,
    );
  }

  partialUpdate(scheduleConflict: PartialUpdateScheduleConflict): Observable<IScheduleConflict> {
    return this.http.patch<IScheduleConflict>(
      `${this.resourceUrl}/${encodeURIComponent(this.getScheduleConflictIdentifier(scheduleConflict))}`,
      scheduleConflict,
    );
  }

  find(id: number): Observable<IScheduleConflict> {
    return this.http.get<IScheduleConflict>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IScheduleConflict[]>> {
    const options = createRequestOption(req);
    return this.http.get<IScheduleConflict[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getScheduleConflictIdentifier(scheduleConflict: Pick<IScheduleConflict, 'id'>): number {
    return scheduleConflict.id;
  }

  compareScheduleConflict(o1: Pick<IScheduleConflict, 'id'> | null, o2: Pick<IScheduleConflict, 'id'> | null): boolean {
    return o1 && o2 ? this.getScheduleConflictIdentifier(o1) === this.getScheduleConflictIdentifier(o2) : o1 === o2;
  }

  addScheduleConflictToCollectionIfMissing<Type extends Pick<IScheduleConflict, 'id'>>(
    scheduleConflictCollection: Type[],
    ...scheduleConflictsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scheduleConflicts: Type[] = scheduleConflictsToCheck.filter(isPresent);
    if (scheduleConflicts.length > 0) {
      const scheduleConflictCollectionIdentifiers = scheduleConflictCollection.map(scheduleConflictItem =>
        this.getScheduleConflictIdentifier(scheduleConflictItem),
      );
      const scheduleConflictsToAdd = scheduleConflicts.filter(scheduleConflictItem => {
        const scheduleConflictIdentifier = this.getScheduleConflictIdentifier(scheduleConflictItem);
        if (scheduleConflictCollectionIdentifiers.includes(scheduleConflictIdentifier)) {
          return false;
        }
        scheduleConflictCollectionIdentifiers.push(scheduleConflictIdentifier);
        return true;
      });
      return [...scheduleConflictsToAdd, ...scheduleConflictCollection];
    }
    return scheduleConflictCollection;
  }
}
