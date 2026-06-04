import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IExam, NewExam } from '../exam.model';

export type PartialUpdateExam = Partial<IExam> & Pick<IExam, 'id'>;

@Injectable()
export class ExamsService {
  readonly examsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly examsResource = httpResource<IExam[]>(() => {
    const params = this.examsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of exam that have been fetched. It is updated when the examsResource emits a new value.
   * In case of error while fetching the exams, the signal is set to an empty array.
   */
  readonly exams = computed(() => (this.examsResource.hasValue() ? this.examsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/exams');
}

@Injectable({ providedIn: 'root' })
export class ExamService extends ExamsService {
  protected readonly http = inject(HttpClient);

  create(exam: NewExam): Observable<IExam> {
    return this.http.post<IExam>(this.resourceUrl, exam);
  }

  update(exam: IExam): Observable<IExam> {
    return this.http.put<IExam>(`${this.resourceUrl}/${encodeURIComponent(this.getExamIdentifier(exam))}`, exam);
  }

  partialUpdate(exam: PartialUpdateExam): Observable<IExam> {
    return this.http.patch<IExam>(`${this.resourceUrl}/${encodeURIComponent(this.getExamIdentifier(exam))}`, exam);
  }

  find(id: number): Observable<IExam> {
    return this.http.get<IExam>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IExam[]>> {
    const options = createRequestOption(req);
    return this.http.get<IExam[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getExamIdentifier(exam: Pick<IExam, 'id'>): number {
    return exam.id;
  }

  compareExam(o1: Pick<IExam, 'id'> | null, o2: Pick<IExam, 'id'> | null): boolean {
    return o1 && o2 ? this.getExamIdentifier(o1) === this.getExamIdentifier(o2) : o1 === o2;
  }

  addExamToCollectionIfMissing<Type extends Pick<IExam, 'id'>>(
    examCollection: Type[],
    ...examsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const exams: Type[] = examsToCheck.filter(isPresent);
    if (exams.length > 0) {
      const examCollectionIdentifiers = examCollection.map(examItem => this.getExamIdentifier(examItem));
      const examsToAdd = exams.filter(examItem => {
        const examIdentifier = this.getExamIdentifier(examItem);
        if (examCollectionIdentifiers.includes(examIdentifier)) {
          return false;
        }
        examCollectionIdentifiers.push(examIdentifier);
        return true;
      });
      return [...examsToAdd, ...examCollection];
    }
    return examCollection;
  }
}
