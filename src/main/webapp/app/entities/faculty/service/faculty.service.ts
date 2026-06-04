import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IFaculty, NewFaculty } from '../faculty.model';

export type PartialUpdateFaculty = Partial<IFaculty> & Pick<IFaculty, 'id'>;

@Injectable()
export class FacultiesService {
  readonly facultiesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly facultiesResource = httpResource<IFaculty[]>(() => {
    const params = this.facultiesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of faculty that have been fetched. It is updated when the facultiesResource emits a new value.
   * In case of error while fetching the faculties, the signal is set to an empty array.
   */
  readonly faculties = computed(() => (this.facultiesResource.hasValue() ? this.facultiesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/faculties');
}

@Injectable({ providedIn: 'root' })
export class FacultyService extends FacultiesService {
  protected readonly http = inject(HttpClient);

  create(faculty: NewFaculty): Observable<IFaculty> {
    return this.http.post<IFaculty>(this.resourceUrl, faculty);
  }

  update(faculty: IFaculty): Observable<IFaculty> {
    return this.http.put<IFaculty>(`${this.resourceUrl}/${encodeURIComponent(this.getFacultyIdentifier(faculty))}`, faculty);
  }

  partialUpdate(faculty: PartialUpdateFaculty): Observable<IFaculty> {
    return this.http.patch<IFaculty>(`${this.resourceUrl}/${encodeURIComponent(this.getFacultyIdentifier(faculty))}`, faculty);
  }

  find(id: number): Observable<IFaculty> {
    return this.http.get<IFaculty>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IFaculty[]>> {
    const options = createRequestOption(req);
    return this.http.get<IFaculty[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getFacultyIdentifier(faculty: Pick<IFaculty, 'id'>): number {
    return faculty.id;
  }

  compareFaculty(o1: Pick<IFaculty, 'id'> | null, o2: Pick<IFaculty, 'id'> | null): boolean {
    return o1 && o2 ? this.getFacultyIdentifier(o1) === this.getFacultyIdentifier(o2) : o1 === o2;
  }

  addFacultyToCollectionIfMissing<Type extends Pick<IFaculty, 'id'>>(
    facultyCollection: Type[],
    ...facultiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const faculties: Type[] = facultiesToCheck.filter(isPresent);
    if (faculties.length > 0) {
      const facultyCollectionIdentifiers = facultyCollection.map(facultyItem => this.getFacultyIdentifier(facultyItem));
      const facultiesToAdd = faculties.filter(facultyItem => {
        const facultyIdentifier = this.getFacultyIdentifier(facultyItem);
        if (facultyCollectionIdentifiers.includes(facultyIdentifier)) {
          return false;
        }
        facultyCollectionIdentifiers.push(facultyIdentifier);
        return true;
      });
      return [...facultiesToAdd, ...facultyCollection];
    }
    return facultyCollection;
  }
}
