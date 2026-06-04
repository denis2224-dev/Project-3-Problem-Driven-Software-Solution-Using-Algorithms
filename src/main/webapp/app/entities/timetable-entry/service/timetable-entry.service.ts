import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITimetableEntry, NewTimetableEntry } from '../timetable-entry.model';

export type PartialUpdateTimetableEntry = Partial<ITimetableEntry> & Pick<ITimetableEntry, 'id'>;

@Injectable()
export class TimetableEntriesService {
  readonly timetableEntriesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly timetableEntriesResource = httpResource<ITimetableEntry[]>(() => {
    const params = this.timetableEntriesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of timetableEntry that have been fetched. It is updated when the timetableEntriesResource emits a new value.
   * In case of error while fetching the timetableEntries, the signal is set to an empty array.
   */
  readonly timetableEntries = computed(() => (this.timetableEntriesResource.hasValue() ? this.timetableEntriesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/timetable-entries');
}

@Injectable({ providedIn: 'root' })
export class TimetableEntryService extends TimetableEntriesService {
  protected readonly http = inject(HttpClient);

  create(timetableEntry: NewTimetableEntry): Observable<ITimetableEntry> {
    return this.http.post<ITimetableEntry>(this.resourceUrl, timetableEntry);
  }

  update(timetableEntry: ITimetableEntry): Observable<ITimetableEntry> {
    return this.http.put<ITimetableEntry>(
      `${this.resourceUrl}/${encodeURIComponent(this.getTimetableEntryIdentifier(timetableEntry))}`,
      timetableEntry,
    );
  }

  partialUpdate(timetableEntry: PartialUpdateTimetableEntry): Observable<ITimetableEntry> {
    return this.http.patch<ITimetableEntry>(
      `${this.resourceUrl}/${encodeURIComponent(this.getTimetableEntryIdentifier(timetableEntry))}`,
      timetableEntry,
    );
  }

  find(id: number): Observable<ITimetableEntry> {
    return this.http.get<ITimetableEntry>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ITimetableEntry[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITimetableEntry[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTimetableEntryIdentifier(timetableEntry: Pick<ITimetableEntry, 'id'>): number {
    return timetableEntry.id;
  }

  compareTimetableEntry(o1: Pick<ITimetableEntry, 'id'> | null, o2: Pick<ITimetableEntry, 'id'> | null): boolean {
    return o1 && o2 ? this.getTimetableEntryIdentifier(o1) === this.getTimetableEntryIdentifier(o2) : o1 === o2;
  }

  addTimetableEntryToCollectionIfMissing<Type extends Pick<ITimetableEntry, 'id'>>(
    timetableEntryCollection: Type[],
    ...timetableEntriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const timetableEntries: Type[] = timetableEntriesToCheck.filter(isPresent);
    if (timetableEntries.length > 0) {
      const timetableEntryCollectionIdentifiers = timetableEntryCollection.map(timetableEntryItem =>
        this.getTimetableEntryIdentifier(timetableEntryItem),
      );
      const timetableEntriesToAdd = timetableEntries.filter(timetableEntryItem => {
        const timetableEntryIdentifier = this.getTimetableEntryIdentifier(timetableEntryItem);
        if (timetableEntryCollectionIdentifiers.includes(timetableEntryIdentifier)) {
          return false;
        }
        timetableEntryCollectionIdentifiers.push(timetableEntryIdentifier);
        return true;
      });
      return [...timetableEntriesToAdd, ...timetableEntryCollection];
    }
    return timetableEntryCollection;
  }
}
