import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITimetableVersion, NewTimetableVersion } from '../timetable-version.model';

export type PartialUpdateTimetableVersion = Partial<ITimetableVersion> & Pick<ITimetableVersion, 'id'>;

type RestOf<T extends ITimetableVersion | NewTimetableVersion> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestTimetableVersion = RestOf<ITimetableVersion>;

export type NewRestTimetableVersion = RestOf<NewTimetableVersion>;

export type PartialUpdateRestTimetableVersion = RestOf<PartialUpdateTimetableVersion>;

@Injectable()
export class TimetableVersionsService {
  readonly timetableVersionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly timetableVersionsResource = httpResource<RestTimetableVersion[]>(() => {
    const params = this.timetableVersionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of timetableVersion that have been fetched. It is updated when the timetableVersionsResource emits a new value.
   * In case of error while fetching the timetableVersions, the signal is set to an empty array.
   */
  readonly timetableVersions = computed(() =>
    (this.timetableVersionsResource.hasValue() ? this.timetableVersionsResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/timetable-versions');

  protected convertValueFromServer(restTimetableVersion: RestTimetableVersion): ITimetableVersion {
    return {
      ...restTimetableVersion,
      createdAt: restTimetableVersion.createdAt ? dayjs(restTimetableVersion.createdAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class TimetableVersionService extends TimetableVersionsService {
  protected readonly http = inject(HttpClient);

  create(timetableVersion: NewTimetableVersion): Observable<ITimetableVersion> {
    const copy = this.convertValueFromClient(timetableVersion);
    return this.http.post<RestTimetableVersion>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(timetableVersion: ITimetableVersion): Observable<ITimetableVersion> {
    const copy = this.convertValueFromClient(timetableVersion);
    return this.http
      .put<RestTimetableVersion>(`${this.resourceUrl}/${encodeURIComponent(this.getTimetableVersionIdentifier(timetableVersion))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(timetableVersion: PartialUpdateTimetableVersion): Observable<ITimetableVersion> {
    const copy = this.convertValueFromClient(timetableVersion);
    return this.http
      .patch<RestTimetableVersion>(`${this.resourceUrl}/${encodeURIComponent(this.getTimetableVersionIdentifier(timetableVersion))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITimetableVersion> {
    return this.http
      .get<RestTimetableVersion>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITimetableVersion[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTimetableVersion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTimetableVersionIdentifier(timetableVersion: Pick<ITimetableVersion, 'id'>): number {
    return timetableVersion.id;
  }

  compareTimetableVersion(o1: Pick<ITimetableVersion, 'id'> | null, o2: Pick<ITimetableVersion, 'id'> | null): boolean {
    return o1 && o2 ? this.getTimetableVersionIdentifier(o1) === this.getTimetableVersionIdentifier(o2) : o1 === o2;
  }

  addTimetableVersionToCollectionIfMissing<Type extends Pick<ITimetableVersion, 'id'>>(
    timetableVersionCollection: Type[],
    ...timetableVersionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const timetableVersions: Type[] = timetableVersionsToCheck.filter(isPresent);
    if (timetableVersions.length > 0) {
      const timetableVersionCollectionIdentifiers = timetableVersionCollection.map(timetableVersionItem =>
        this.getTimetableVersionIdentifier(timetableVersionItem),
      );
      const timetableVersionsToAdd = timetableVersions.filter(timetableVersionItem => {
        const timetableVersionIdentifier = this.getTimetableVersionIdentifier(timetableVersionItem);
        if (timetableVersionCollectionIdentifiers.includes(timetableVersionIdentifier)) {
          return false;
        }
        timetableVersionCollectionIdentifiers.push(timetableVersionIdentifier);
        return true;
      });
      return [...timetableVersionsToAdd, ...timetableVersionCollection];
    }
    return timetableVersionCollection;
  }

  protected convertValueFromClient<T extends ITimetableVersion | NewTimetableVersion | PartialUpdateTimetableVersion>(
    timetableVersion: T,
  ): RestOf<T> {
    return {
      ...timetableVersion,
      createdAt: timetableVersion.createdAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTimetableVersion): ITimetableVersion {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTimetableVersion[]): ITimetableVersion[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
