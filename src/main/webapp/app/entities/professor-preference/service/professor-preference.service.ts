import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProfessorPreference, NewProfessorPreference } from '../professor-preference.model';

export type PartialUpdateProfessorPreference = Partial<IProfessorPreference> & Pick<IProfessorPreference, 'id'>;

@Injectable()
export class ProfessorPreferencesService {
  readonly professorPreferencesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly professorPreferencesResource = httpResource<IProfessorPreference[]>(() => {
    const params = this.professorPreferencesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of professorPreference that have been fetched. It is updated when the professorPreferencesResource emits a new value.
   * In case of error while fetching the professorPreferences, the signal is set to an empty array.
   */
  readonly professorPreferences = computed(() =>
    this.professorPreferencesResource.hasValue() ? this.professorPreferencesResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/professor-preferences');
}

@Injectable({ providedIn: 'root' })
export class ProfessorPreferenceService extends ProfessorPreferencesService {
  protected readonly http = inject(HttpClient);

  create(professorPreference: NewProfessorPreference): Observable<IProfessorPreference> {
    return this.http.post<IProfessorPreference>(this.resourceUrl, professorPreference);
  }

  update(professorPreference: IProfessorPreference): Observable<IProfessorPreference> {
    return this.http.put<IProfessorPreference>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfessorPreferenceIdentifier(professorPreference))}`,
      professorPreference,
    );
  }

  partialUpdate(professorPreference: PartialUpdateProfessorPreference): Observable<IProfessorPreference> {
    return this.http.patch<IProfessorPreference>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfessorPreferenceIdentifier(professorPreference))}`,
      professorPreference,
    );
  }

  find(id: number): Observable<IProfessorPreference> {
    return this.http.get<IProfessorPreference>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProfessorPreference[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProfessorPreference[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfessorPreferenceIdentifier(professorPreference: Pick<IProfessorPreference, 'id'>): number {
    return professorPreference.id;
  }

  compareProfessorPreference(o1: Pick<IProfessorPreference, 'id'> | null, o2: Pick<IProfessorPreference, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfessorPreferenceIdentifier(o1) === this.getProfessorPreferenceIdentifier(o2) : o1 === o2;
  }

  addProfessorPreferenceToCollectionIfMissing<Type extends Pick<IProfessorPreference, 'id'>>(
    professorPreferenceCollection: Type[],
    ...professorPreferencesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const professorPreferences: Type[] = professorPreferencesToCheck.filter(isPresent);
    if (professorPreferences.length > 0) {
      const professorPreferenceCollectionIdentifiers = professorPreferenceCollection.map(professorPreferenceItem =>
        this.getProfessorPreferenceIdentifier(professorPreferenceItem),
      );
      const professorPreferencesToAdd = professorPreferences.filter(professorPreferenceItem => {
        const professorPreferenceIdentifier = this.getProfessorPreferenceIdentifier(professorPreferenceItem);
        if (professorPreferenceCollectionIdentifiers.includes(professorPreferenceIdentifier)) {
          return false;
        }
        professorPreferenceCollectionIdentifiers.push(professorPreferenceIdentifier);
        return true;
      });
      return [...professorPreferencesToAdd, ...professorPreferenceCollection];
    }
    return professorPreferenceCollection;
  }
}
