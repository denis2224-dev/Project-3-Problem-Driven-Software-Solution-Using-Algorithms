import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISolverJob, NewSolverJob } from '../solver-job.model';

export type PartialUpdateSolverJob = Partial<ISolverJob> & Pick<ISolverJob, 'id'>;

type RestOf<T extends ISolverJob | NewSolverJob> = Omit<T, 'startedAt' | 'finishedAt'> & {
  startedAt?: string | null;
  finishedAt?: string | null;
};

export type RestSolverJob = RestOf<ISolverJob>;

export type NewRestSolverJob = RestOf<NewSolverJob>;

export type PartialUpdateRestSolverJob = RestOf<PartialUpdateSolverJob>;

@Injectable()
export class SolverJobsService {
  readonly solverJobsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly solverJobsResource = httpResource<RestSolverJob[]>(() => {
    const params = this.solverJobsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of solverJob that have been fetched. It is updated when the solverJobsResource emits a new value.
   * In case of error while fetching the solverJobs, the signal is set to an empty array.
   */
  readonly solverJobs = computed(() =>
    (this.solverJobsResource.hasValue() ? this.solverJobsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/solver-jobs');

  protected convertValueFromServer(restSolverJob: RestSolverJob): ISolverJob {
    return {
      ...restSolverJob,
      startedAt: restSolverJob.startedAt ? dayjs(restSolverJob.startedAt) : undefined,
      finishedAt: restSolverJob.finishedAt ? dayjs(restSolverJob.finishedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class SolverJobService extends SolverJobsService {
  protected readonly http = inject(HttpClient);

  create(solverJob: NewSolverJob): Observable<ISolverJob> {
    const copy = this.convertValueFromClient(solverJob);
    return this.http.post<RestSolverJob>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(solverJob: ISolverJob): Observable<ISolverJob> {
    const copy = this.convertValueFromClient(solverJob);
    return this.http
      .put<RestSolverJob>(`${this.resourceUrl}/${encodeURIComponent(this.getSolverJobIdentifier(solverJob))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(solverJob: PartialUpdateSolverJob): Observable<ISolverJob> {
    const copy = this.convertValueFromClient(solverJob);
    return this.http
      .patch<RestSolverJob>(`${this.resourceUrl}/${encodeURIComponent(this.getSolverJobIdentifier(solverJob))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ISolverJob> {
    return this.http
      .get<RestSolverJob>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ISolverJob[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSolverJob[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSolverJobIdentifier(solverJob: Pick<ISolverJob, 'id'>): number {
    return solverJob.id;
  }

  compareSolverJob(o1: Pick<ISolverJob, 'id'> | null, o2: Pick<ISolverJob, 'id'> | null): boolean {
    return o1 && o2 ? this.getSolverJobIdentifier(o1) === this.getSolverJobIdentifier(o2) : o1 === o2;
  }

  addSolverJobToCollectionIfMissing<Type extends Pick<ISolverJob, 'id'>>(
    solverJobCollection: Type[],
    ...solverJobsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const solverJobs: Type[] = solverJobsToCheck.filter(isPresent);
    if (solverJobs.length > 0) {
      const solverJobCollectionIdentifiers = solverJobCollection.map(solverJobItem => this.getSolverJobIdentifier(solverJobItem));
      const solverJobsToAdd = solverJobs.filter(solverJobItem => {
        const solverJobIdentifier = this.getSolverJobIdentifier(solverJobItem);
        if (solverJobCollectionIdentifiers.includes(solverJobIdentifier)) {
          return false;
        }
        solverJobCollectionIdentifiers.push(solverJobIdentifier);
        return true;
      });
      return [...solverJobsToAdd, ...solverJobCollection];
    }
    return solverJobCollection;
  }

  protected convertValueFromClient<T extends ISolverJob | NewSolverJob | PartialUpdateSolverJob>(solverJob: T): RestOf<T> {
    return {
      ...solverJob,
      startedAt: solverJob.startedAt?.toJSON() ?? null,
      finishedAt: solverJob.finishedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestSolverJob): ISolverJob {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestSolverJob[]): ISolverJob[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
