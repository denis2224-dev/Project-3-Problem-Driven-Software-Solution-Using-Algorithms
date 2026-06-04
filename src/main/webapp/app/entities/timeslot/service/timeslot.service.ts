import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITimeslot, NewTimeslot } from '../timeslot.model';

export type PartialUpdateTimeslot = Partial<ITimeslot> & Pick<ITimeslot, 'id'>;

@Injectable()
export class TimeslotsService {
  readonly timeslotsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly timeslotsResource = httpResource<ITimeslot[]>(() => {
    const params = this.timeslotsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of timeslot that have been fetched. It is updated when the timeslotsResource emits a new value.
   * In case of error while fetching the timeslots, the signal is set to an empty array.
   */
  readonly timeslots = computed(() => (this.timeslotsResource.hasValue() ? this.timeslotsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/timeslots');
}

@Injectable({ providedIn: 'root' })
export class TimeslotService extends TimeslotsService {
  protected readonly http = inject(HttpClient);

  create(timeslot: NewTimeslot): Observable<ITimeslot> {
    return this.http.post<ITimeslot>(this.resourceUrl, timeslot);
  }

  update(timeslot: ITimeslot): Observable<ITimeslot> {
    return this.http.put<ITimeslot>(`${this.resourceUrl}/${encodeURIComponent(this.getTimeslotIdentifier(timeslot))}`, timeslot);
  }

  partialUpdate(timeslot: PartialUpdateTimeslot): Observable<ITimeslot> {
    return this.http.patch<ITimeslot>(`${this.resourceUrl}/${encodeURIComponent(this.getTimeslotIdentifier(timeslot))}`, timeslot);
  }

  find(id: number): Observable<ITimeslot> {
    return this.http.get<ITimeslot>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ITimeslot[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITimeslot[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTimeslotIdentifier(timeslot: Pick<ITimeslot, 'id'>): number {
    return timeslot.id;
  }

  compareTimeslot(o1: Pick<ITimeslot, 'id'> | null, o2: Pick<ITimeslot, 'id'> | null): boolean {
    return o1 && o2 ? this.getTimeslotIdentifier(o1) === this.getTimeslotIdentifier(o2) : o1 === o2;
  }

  addTimeslotToCollectionIfMissing<Type extends Pick<ITimeslot, 'id'>>(
    timeslotCollection: Type[],
    ...timeslotsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const timeslots: Type[] = timeslotsToCheck.filter(isPresent);
    if (timeslots.length > 0) {
      const timeslotCollectionIdentifiers = timeslotCollection.map(timeslotItem => this.getTimeslotIdentifier(timeslotItem));
      const timeslotsToAdd = timeslots.filter(timeslotItem => {
        const timeslotIdentifier = this.getTimeslotIdentifier(timeslotItem);
        if (timeslotCollectionIdentifiers.includes(timeslotIdentifier)) {
          return false;
        }
        timeslotCollectionIdentifiers.push(timeslotIdentifier);
        return true;
      });
      return [...timeslotsToAdd, ...timeslotCollection];
    }
    return timeslotCollection;
  }
}
