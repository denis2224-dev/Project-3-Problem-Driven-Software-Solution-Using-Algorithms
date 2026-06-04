import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IProfessorPreference } from '../professor-preference.model';
import { ProfessorPreferenceService } from '../service/professor-preference.service';

const professorPreferenceResolve = (route: ActivatedRouteSnapshot): Observable<null | IProfessorPreference> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(ProfessorPreferenceService);
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

export default professorPreferenceResolve;
