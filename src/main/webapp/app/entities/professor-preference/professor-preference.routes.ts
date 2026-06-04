import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ProfessorPreferenceResolve from './route/professor-preference-routing-resolve.service';

const professorPreferenceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/professor-preference').then(m => m.ProfessorPreference),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/professor-preference-detail').then(m => m.ProfessorPreferenceDetail),
    resolve: {
      professorPreference: ProfessorPreferenceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/professor-preference-update').then(m => m.ProfessorPreferenceUpdate),
    resolve: {
      professorPreference: ProfessorPreferenceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/professor-preference-update').then(m => m.ProfessorPreferenceUpdate),
    resolve: {
      professorPreference: ProfessorPreferenceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default professorPreferenceRoute;
