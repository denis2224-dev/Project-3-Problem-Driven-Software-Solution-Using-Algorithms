import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IExamScheduleEntry, NewExamScheduleEntry } from '../exam-schedule-entry.model';

export type PartialUpdateExamScheduleEntry = Partial<IExamScheduleEntry> & Pick<IExamScheduleEntry, 'id'>;

@Injectable()
export class ExamScheduleEntriesService {
  readonly examScheduleEntriesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly examScheduleEntriesResource = httpResource<IExamScheduleEntry[]>(() => {
    const params = this.examScheduleEntriesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of examScheduleEntry that have been fetched. It is updated when the examScheduleEntriesResource emits a new value.
   * In case of error while fetching the examScheduleEntries, the signal is set to an empty array.
   */
  readonly examScheduleEntries = computed(() =>
    this.examScheduleEntriesResource.hasValue() ? this.examScheduleEntriesResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/exam-schedule-entries');
}

@Injectable({ providedIn: 'root' })
export class ExamScheduleEntryService extends ExamScheduleEntriesService {
  protected readonly http = inject(HttpClient);

  create(examScheduleEntry: NewExamScheduleEntry): Observable<IExamScheduleEntry> {
    return this.http.post<IExamScheduleEntry>(this.resourceUrl, examScheduleEntry);
  }

  update(examScheduleEntry: IExamScheduleEntry): Observable<IExamScheduleEntry> {
    return this.http.put<IExamScheduleEntry>(
      `${this.resourceUrl}/${encodeURIComponent(this.getExamScheduleEntryIdentifier(examScheduleEntry))}`,
      examScheduleEntry,
    );
  }

  partialUpdate(examScheduleEntry: PartialUpdateExamScheduleEntry): Observable<IExamScheduleEntry> {
    return this.http.patch<IExamScheduleEntry>(
      `${this.resourceUrl}/${encodeURIComponent(this.getExamScheduleEntryIdentifier(examScheduleEntry))}`,
      examScheduleEntry,
    );
  }

  find(id: number): Observable<IExamScheduleEntry> {
    return this.http.get<IExamScheduleEntry>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IExamScheduleEntry[]>> {
    const options = createRequestOption(req);
    return this.http.get<IExamScheduleEntry[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getExamScheduleEntryIdentifier(examScheduleEntry: Pick<IExamScheduleEntry, 'id'>): number {
    return examScheduleEntry.id;
  }

  compareExamScheduleEntry(o1: Pick<IExamScheduleEntry, 'id'> | null, o2: Pick<IExamScheduleEntry, 'id'> | null): boolean {
    return o1 && o2 ? this.getExamScheduleEntryIdentifier(o1) === this.getExamScheduleEntryIdentifier(o2) : o1 === o2;
  }

  addExamScheduleEntryToCollectionIfMissing<Type extends Pick<IExamScheduleEntry, 'id'>>(
    examScheduleEntryCollection: Type[],
    ...examScheduleEntriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const examScheduleEntries: Type[] = examScheduleEntriesToCheck.filter(isPresent);
    if (examScheduleEntries.length > 0) {
      const examScheduleEntryCollectionIdentifiers = examScheduleEntryCollection.map(examScheduleEntryItem =>
        this.getExamScheduleEntryIdentifier(examScheduleEntryItem),
      );
      const examScheduleEntriesToAdd = examScheduleEntries.filter(examScheduleEntryItem => {
        const examScheduleEntryIdentifier = this.getExamScheduleEntryIdentifier(examScheduleEntryItem);
        if (examScheduleEntryCollectionIdentifiers.includes(examScheduleEntryIdentifier)) {
          return false;
        }
        examScheduleEntryCollectionIdentifiers.push(examScheduleEntryIdentifier);
        return true;
      });
      return [...examScheduleEntriesToAdd, ...examScheduleEntryCollection];
    }
    return examScheduleEntryCollection;
  }
}
