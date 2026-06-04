import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProfessor, NewProfessor } from '../professor.model';

export type PartialUpdateProfessor = Partial<IProfessor> & Pick<IProfessor, 'id'>;

@Injectable()
export class ProfessorsService {
  readonly professorsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly professorsResource = httpResource<IProfessor[]>(() => {
    const params = this.professorsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of professor that have been fetched. It is updated when the professorsResource emits a new value.
   * In case of error while fetching the professors, the signal is set to an empty array.
   */
  readonly professors = computed(() => (this.professorsResource.hasValue() ? this.professorsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/professors');
}

@Injectable({ providedIn: 'root' })
export class ProfessorService extends ProfessorsService {
  protected readonly http = inject(HttpClient);

  create(professor: NewProfessor): Observable<IProfessor> {
    return this.http.post<IProfessor>(this.resourceUrl, professor);
  }

  update(professor: IProfessor): Observable<IProfessor> {
    return this.http.put<IProfessor>(`${this.resourceUrl}/${encodeURIComponent(this.getProfessorIdentifier(professor))}`, professor);
  }

  partialUpdate(professor: PartialUpdateProfessor): Observable<IProfessor> {
    return this.http.patch<IProfessor>(`${this.resourceUrl}/${encodeURIComponent(this.getProfessorIdentifier(professor))}`, professor);
  }

  find(id: number): Observable<IProfessor> {
    return this.http.get<IProfessor>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProfessor[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProfessor[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfessorIdentifier(professor: Pick<IProfessor, 'id'>): number {
    return professor.id;
  }

  compareProfessor(o1: Pick<IProfessor, 'id'> | null, o2: Pick<IProfessor, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfessorIdentifier(o1) === this.getProfessorIdentifier(o2) : o1 === o2;
  }

  addProfessorToCollectionIfMissing<Type extends Pick<IProfessor, 'id'>>(
    professorCollection: Type[],
    ...professorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const professors: Type[] = professorsToCheck.filter(isPresent);
    if (professors.length > 0) {
      const professorCollectionIdentifiers = professorCollection.map(professorItem => this.getProfessorIdentifier(professorItem));
      const professorsToAdd = professors.filter(professorItem => {
        const professorIdentifier = this.getProfessorIdentifier(professorItem);
        if (professorCollectionIdentifiers.includes(professorIdentifier)) {
          return false;
        }
        professorCollectionIdentifiers.push(professorIdentifier);
        return true;
      });
      return [...professorsToAdd, ...professorCollection];
    }
    return professorCollection;
  }
}
