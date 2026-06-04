import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITimetable, NewTimetable } from '../timetable.model';

export type PartialUpdateTimetable = Partial<ITimetable> & Pick<ITimetable, 'id'>;

type RestOf<T extends ITimetable | NewTimetable> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestTimetable = RestOf<ITimetable>;

export type NewRestTimetable = RestOf<NewTimetable>;

export type PartialUpdateRestTimetable = RestOf<PartialUpdateTimetable>;

@Injectable()
export class TimetablesService {
  readonly timetablesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly timetablesResource = httpResource<RestTimetable[]>(() => {
    const params = this.timetablesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of timetable that have been fetched. It is updated when the timetablesResource emits a new value.
   * In case of error while fetching the timetables, the signal is set to an empty array.
   */
  readonly timetables = computed(() =>
    (this.timetablesResource.hasValue() ? this.timetablesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/timetables');

  protected convertValueFromServer(restTimetable: RestTimetable): ITimetable {
    return {
      ...restTimetable,
      createdAt: restTimetable.createdAt ? dayjs(restTimetable.createdAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class TimetableService extends TimetablesService {
  protected readonly http = inject(HttpClient);

  create(timetable: NewTimetable): Observable<ITimetable> {
    const copy = this.convertValueFromClient(timetable);
    return this.http.post<RestTimetable>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(timetable: ITimetable): Observable<ITimetable> {
    const copy = this.convertValueFromClient(timetable);
    return this.http
      .put<RestTimetable>(`${this.resourceUrl}/${encodeURIComponent(this.getTimetableIdentifier(timetable))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(timetable: PartialUpdateTimetable): Observable<ITimetable> {
    const copy = this.convertValueFromClient(timetable);
    return this.http
      .patch<RestTimetable>(`${this.resourceUrl}/${encodeURIComponent(this.getTimetableIdentifier(timetable))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITimetable> {
    return this.http
      .get<RestTimetable>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITimetable[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTimetable[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTimetableIdentifier(timetable: Pick<ITimetable, 'id'>): number {
    return timetable.id;
  }

  compareTimetable(o1: Pick<ITimetable, 'id'> | null, o2: Pick<ITimetable, 'id'> | null): boolean {
    return o1 && o2 ? this.getTimetableIdentifier(o1) === this.getTimetableIdentifier(o2) : o1 === o2;
  }

  addTimetableToCollectionIfMissing<Type extends Pick<ITimetable, 'id'>>(
    timetableCollection: Type[],
    ...timetablesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const timetables: Type[] = timetablesToCheck.filter(isPresent);
    if (timetables.length > 0) {
      const timetableCollectionIdentifiers = timetableCollection.map(timetableItem => this.getTimetableIdentifier(timetableItem));
      const timetablesToAdd = timetables.filter(timetableItem => {
        const timetableIdentifier = this.getTimetableIdentifier(timetableItem);
        if (timetableCollectionIdentifiers.includes(timetableIdentifier)) {
          return false;
        }
        timetableCollectionIdentifiers.push(timetableIdentifier);
        return true;
      });
      return [...timetablesToAdd, ...timetableCollection];
    }
    return timetableCollection;
  }

  protected convertValueFromClient<T extends ITimetable | NewTimetable | PartialUpdateTimetable>(timetable: T): RestOf<T> {
    return {
      ...timetable,
      createdAt: timetable.createdAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTimetable): ITimetable {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTimetable[]): ITimetable[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
