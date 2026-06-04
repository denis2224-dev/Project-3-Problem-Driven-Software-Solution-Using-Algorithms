import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IStudentGroup, NewStudentGroup } from '../student-group.model';

export type PartialUpdateStudentGroup = Partial<IStudentGroup> & Pick<IStudentGroup, 'id'>;

@Injectable()
export class StudentGroupsService {
  readonly studentGroupsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly studentGroupsResource = httpResource<IStudentGroup[]>(() => {
    const params = this.studentGroupsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of studentGroup that have been fetched. It is updated when the studentGroupsResource emits a new value.
   * In case of error while fetching the studentGroups, the signal is set to an empty array.
   */
  readonly studentGroups = computed(() => (this.studentGroupsResource.hasValue() ? this.studentGroupsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/student-groups');
}

@Injectable({ providedIn: 'root' })
export class StudentGroupService extends StudentGroupsService {
  protected readonly http = inject(HttpClient);

  create(studentGroup: NewStudentGroup): Observable<IStudentGroup> {
    return this.http.post<IStudentGroup>(this.resourceUrl, studentGroup);
  }

  update(studentGroup: IStudentGroup): Observable<IStudentGroup> {
    return this.http.put<IStudentGroup>(
      `${this.resourceUrl}/${encodeURIComponent(this.getStudentGroupIdentifier(studentGroup))}`,
      studentGroup,
    );
  }

  partialUpdate(studentGroup: PartialUpdateStudentGroup): Observable<IStudentGroup> {
    return this.http.patch<IStudentGroup>(
      `${this.resourceUrl}/${encodeURIComponent(this.getStudentGroupIdentifier(studentGroup))}`,
      studentGroup,
    );
  }

  find(id: number): Observable<IStudentGroup> {
    return this.http.get<IStudentGroup>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IStudentGroup[]>> {
    const options = createRequestOption(req);
    return this.http.get<IStudentGroup[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getStudentGroupIdentifier(studentGroup: Pick<IStudentGroup, 'id'>): number {
    return studentGroup.id;
  }

  compareStudentGroup(o1: Pick<IStudentGroup, 'id'> | null, o2: Pick<IStudentGroup, 'id'> | null): boolean {
    return o1 && o2 ? this.getStudentGroupIdentifier(o1) === this.getStudentGroupIdentifier(o2) : o1 === o2;
  }

  addStudentGroupToCollectionIfMissing<Type extends Pick<IStudentGroup, 'id'>>(
    studentGroupCollection: Type[],
    ...studentGroupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const studentGroups: Type[] = studentGroupsToCheck.filter(isPresent);
    if (studentGroups.length > 0) {
      const studentGroupCollectionIdentifiers = studentGroupCollection.map(studentGroupItem =>
        this.getStudentGroupIdentifier(studentGroupItem),
      );
      const studentGroupsToAdd = studentGroups.filter(studentGroupItem => {
        const studentGroupIdentifier = this.getStudentGroupIdentifier(studentGroupItem);
        if (studentGroupCollectionIdentifiers.includes(studentGroupIdentifier)) {
          return false;
        }
        studentGroupCollectionIdentifiers.push(studentGroupIdentifier);
        return true;
      });
      return [...studentGroupsToAdd, ...studentGroupCollection];
    }
    return studentGroupCollection;
  }
}
