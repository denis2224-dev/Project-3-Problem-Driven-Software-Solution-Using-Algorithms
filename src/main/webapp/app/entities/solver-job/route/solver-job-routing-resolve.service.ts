import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { SolverJobService } from '../service/solver-job.service';
import { ISolverJob } from '../solver-job.model';

const solverJobResolve = (route: ActivatedRouteSnapshot): Observable<null | ISolverJob> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(SolverJobService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default solverJobResolve;
